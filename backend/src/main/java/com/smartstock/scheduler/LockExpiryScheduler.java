package com.smartstock.scheduler;

import com.smartstock.model.CartLock;
import com.smartstock.model.InventoryAudit;
import com.smartstock.repository.CartLockRepository;
import com.smartstock.repository.InventoryAuditRepository;
import com.smartstock.repository.OtpCodeRepository;
import com.smartstock.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Runs every 30 seconds to:
 * 1. Find all active locks that have expired
 * 2. Return their stock back to the product
 * 3. Mark locks as 'expired'
 * 4. Clean up old OTPs
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LockExpiryScheduler {

    private final CartLockRepository cartLockRepository;
    private final ProductRepository productRepository;
    private final InventoryAuditRepository auditRepository;
    private final OtpCodeRepository otpCodeRepository;

    @Scheduled(cron = "${lock.cleanup.cron}")
    @Transactional
    public void processExpiredLocks() {
        LocalDateTime now = LocalDateTime.now();

        // Find expired locks before marking them
        List<CartLock> expiredLocks = cartLockRepository.findExpiredLocks(now);

        if (expiredLocks.isEmpty()) return;

        log.info("Processing {} expired cart locks...", expiredLocks.size());

        for (CartLock lock : expiredLocks) {
            try {
                // Return stock
                int updated = productRepository.returnStock(lock.getProductId(), lock.getQty());
                if (updated > 0) {
                    // Log stock return
                    auditRepository.save(InventoryAudit.builder()
                            .productId(lock.getProductId())
                            .qtyChange(lock.getQty())
                            .action("LOCK_EXPIRED_RETURN")
                            .notes("Lock #" + lock.getId() + " expired. Stock returned for phone: " + lock.getPhone())
                            .build());
                    log.info("Stock returned: product={} qty={} user={}", lock.getProductId(), lock.getQty(), lock.getPhone());
                }
            } catch (Exception e) {
                log.error("Failed to return stock for lock {}: {}", lock.getId(), e.getMessage());
            }
        }

        // Mark all expired locks in bulk
        int marked = cartLockRepository.markExpiredLocks(now);
        log.info("Marked {} locks as expired.", marked);

        // Cleanup old checked_out/expired locks older than 24h
        cartLockRepository.cleanupOldLocks(now.minusHours(24));
    }

    // Run OTP cleanup every 10 minutes
    @Scheduled(fixedDelay = 600_000)
    @Transactional
    public void cleanupExpiredOtps() {
        int deleted = otpCodeRepository.deleteExpiredOtps(LocalDateTime.now().minusMinutes(30));
        if (deleted > 0) {
            log.debug("Cleaned up {} expired OTPs", deleted);
        }
    }
}
