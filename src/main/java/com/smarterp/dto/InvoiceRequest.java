package com.smarterp.dto;

import com.smarterp.entities.Invoice;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class InvoiceRequest {

    @NotNull(message = "Ledger ID is required")
    private Long ledgerId;

    @NotNull(message = "Invoice type is required")
    private Invoice.InvoiceType type;

    private LocalDate date;

    @NotNull(message = "Items are required")
    private List<InvoiceItemRequest> items;
}