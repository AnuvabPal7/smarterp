package com.smarterp.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stock_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String sku;

    @Column(nullable = false)
    private Double purchasePrice;

    @Column(nullable = false)
    private Double sellingPrice;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private String unit;
}