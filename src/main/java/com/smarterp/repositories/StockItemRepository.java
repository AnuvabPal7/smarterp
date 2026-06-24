package com.smarterp.repositories;

import com.smarterp.entities.StockItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface StockItemRepository extends JpaRepository<StockItem, Long> {
    boolean existsBySku(String sku);
    Optional<StockItem> findBySku(String sku);
    List<StockItem> findByQuantityLessThan(Integer quantity);
}