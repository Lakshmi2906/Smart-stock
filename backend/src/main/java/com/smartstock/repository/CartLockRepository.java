package com.smartstock.repository;

import com.smartstock.model.CartLock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartLockRepository extends JpaRepository<CartLock, Integer> {

    List<CartLock> findByPhoneAndStatus(String phone, String status);

    Optional<CartLock> findByIdAndPhone(Integer id, String phone);

    @Query("SELECT cl FROM CartLock cl WHERE cl.phone = :phone AND cl.status = 'active' ORDER BY cl.createdAt DESC")
    List<CartLock> findActiveByPhone(@Param("phone") String phone);

    @Query("SELECT cl FROM CartLock cl WHERE cl.status = 'active' AND cl.lockExpires < :now")
    List<CartLock> findExpiredLocks(@Param("now") LocalDateTime now);

    @Query("SELECT COUNT(cl) FROM CartLock cl WHERE cl.status = 'active'")
    long countActiveLocks();

    @Modifying
    @Query("UPDATE CartLock cl SET cl.status = 'expired' WHERE cl.status = 'active' AND cl.lockExpires < :now")
    int markExpiredLocks(@Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE CartLock cl SET cl.lockExpires = :newExpiry WHERE cl.id = :id AND cl.status = 'active'")
    int extendLock(@Param("id") Integer id, @Param("newExpiry") LocalDateTime newExpiry);

    @Modifying
    @Query("DELETE FROM CartLock cl WHERE cl.status IN ('expired', 'checked_out') AND cl.createdAt < :before")
    int cleanupOldLocks(@Param("before") LocalDateTime before);

    boolean existsByPhoneAndProductIdAndStatus(String phone, Integer productId, String status);
}
