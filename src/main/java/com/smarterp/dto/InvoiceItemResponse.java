package com.smarterp.dto;

import com.smarterp.entities.InvoiceItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceItemResponse {
    private Long id;
    private Long stockItemId;
    private String stockItemName;
    private Integer quantity;
    private Double rate;
    private Double amount;

    public static InvoiceItemResponse from(InvoiceItem item) {
        return new InvoiceItemResponse(
            item.getId(),
            item.getStockItem().getId(),
            item.getStockItem().getName(),
            item.getQuantity(),
            item.getRate(),
            item.getAmount()
        );
    }
}