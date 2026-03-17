package com.smartstock.repository;

import com.smartstock.model.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    List<Product> findByCategory(String category);

    List<Product> findByCategoryIgnoreCase(String category);

    List<Product> findByIsFlashSaleTrue();

    List<Product> findByStockLessThanAndStockGreaterThan(int maxStock, int minStock);

    List<Product> findByStockLessThanEqualAndStockGreaterThan(int maxStock, int minStock);

    @Query("SELECT p FROM Product p WHERE p.stock < :threshold AND p.stock > 0 ORDER BY p.stock ASC")
    List<Product> findLowStockProducts(@Param("threshold") int threshold);

    @Query("SELECT p FROM Product p WHERE p.stock = 0")
    List<Product> findOutOfStockProducts();

    /**
     * Pessimistic write lock — used during atomic stock decrement for cart locking.
     * Prevents concurrent reads of the same row during the lock acquisition.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdForUpdate(@Param("id") Integer id);

    @Modifying
    @Query("UPDATE Product p SET p.stock = p.stock + :qty WHERE p.id = :id")
    int returnStock(@Param("id") Integer id, @Param("qty") int qty);

    @Modifying
    @Query("UPDATE Product p SET p.stock = p.stock - :qty WHERE p.id = :id AND p.stock >= :qty")
    int deductStock(@Param("id") Integer id, @Param("qty") int qty);

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(p.category) LIKE LOWER(CONCAT('%', :q, '%'))")
    List<Product> search(@Param("q") String query);
}
