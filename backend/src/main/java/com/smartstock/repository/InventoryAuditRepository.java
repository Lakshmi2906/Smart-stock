package com.smartstock.repository;

import com.smartstock.model.InventoryAudit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryAuditRepository extends JpaRepository<InventoryAudit, Integer> {

    List<InventoryAudit> findByProductIdOrderByTimestampDesc(Integer productId);

    Page<InventoryAudit> findAllByOrderByTimestampDesc(Pageable pageable);

    List<InventoryAudit> findByAdminPhoneOrderByTimestampDesc(String adminPhone);
}
