package com.smarterp.dto;

import com.smarterp.entities.StockItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockItemResponse {
    private Long id;
    private String name;
    private String sku;
    private Double purchasePrice;
    private Double sellingPrice;
    private Integer quantity;
    private String unit;

    public static StockItemResponse from(StockItem item) {
        return new StockItemResponse(
            item.getId(),
            item.getName(),
            item.getSku(),
            item.getPurchasePrice(),
            item.getSellingPrice(),
            item.getQuantity(),
            item.getUnit()
        );
    }
}