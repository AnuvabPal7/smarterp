package com.smarterp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InvoiceItemRequest {

    @NotNull(message = "Stock item ID is required")
    private Long stockItemId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @NotNull(message = "Rate is required")
    @Min(value = 0, message = "Rate cannot be negative")
    private Double rate;
}