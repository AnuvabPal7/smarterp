package com.smarterp.controllers;

import com.smarterp.dto.InvoiceRequest;
import com.smarterp.dto.InvoiceResponse;
import com.smarterp.entities.Invoice;
import com.smarterp.services.InvoiceService;
import com.smarterp.services.PdfService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final PdfService pdfService;

    @PostMapping
    public ResponseEntity<InvoiceResponse> create(@Valid @RequestBody InvoiceRequest request) {
        return ResponseEntity.ok(invoiceService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<InvoiceResponse>> getAll() {
        return ResponseEntity.ok(invoiceService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getById(id));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<InvoiceResponse>> getByType(@PathVariable Invoice.InvoiceType type) {
        return ResponseEntity.ok(invoiceService.getByType(type));
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> generatePdf(@PathVariable Long id) {
        try {
            byte[] pdf = pdfService.generateInvoicePdf(id);
            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "attachment; filename=invoice-" + id + ".pdf")
                    .body(pdf);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}