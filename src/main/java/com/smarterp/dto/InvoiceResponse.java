package com.smarterp.dto;

import com.smarterp.entities.Invoice;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceResponse {
    private Long id;
    private String invoiceNumber;
    private Invoice.InvoiceType type;
    private Long ledgerId;
    private String ledgerName;
    private LocalDate date;
    private Double totalAmount;
    private Invoice.InvoiceStatus status;
    private List<InvoiceItemResponse> items;

    public static InvoiceResponse from(Invoice invoice) {
        return new InvoiceResponse(
            invoice.getId(),
            invoice.getInvoiceNumber(),
            invoice.getType(),
            invoice.getLedger().getId(),
            invoice.getLedger().getName(),
            invoice.getDate(),
            invoice.getTotalAmount(),
            invoice.getStatus(),
            invoice.getItems().stream()
                .map(InvoiceItemResponse::from)
                .collect(Collectors.toList())
        );
    }
}