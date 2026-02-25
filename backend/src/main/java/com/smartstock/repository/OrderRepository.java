package com.smartstock.repository;

import com.smartstock.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    List<Order> findByPhoneOrderByCreatedAtDesc(String phone);

    Optional<Order> findByRazorpayOrderId(String razorpayOrderId);

    Page<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);

    List<Order> findByStatus(String status);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt > :since")
    long countRecentOrders(@Param("since") LocalDateTime since);

    @Query("SELECT COALESCE(SUM(o.total), 0) FROM Order o WHERE o.status = 'paid' AND o.createdAt > :since")
    BigDecimal sumRevenueForPeriod(@Param("since") LocalDateTime since);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = 'paid'")
    long countPaidOrders();
}
