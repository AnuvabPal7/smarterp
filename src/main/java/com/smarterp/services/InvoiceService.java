package com.smarterp.services;

import com.smarterp.dto.InvoiceItemRequest;
import com.smarterp.dto.InvoiceRequest;
import com.smarterp.dto.InvoiceResponse;
import com.smarterp.entities.*;
import com.smarterp.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository invoiceItemRepository;
    private final LedgerRepository ledgerRepository;
    private final StockItemRepository stockItemRepository;

    @Transactional
    public InvoiceResponse create(InvoiceRequest request) {
        Ledger ledger = ledgerRepository.findById(request.getLedgerId())
                .orElseThrow(() -> new IllegalArgumentException("Ledger not found"));

        if (request.getType() == Invoice.InvoiceType.SALES &&
                ledger.getType() != Ledger.LedgerType.CUSTOMER) {
            throw new IllegalArgumentException("Sales invoice requires a CUSTOMER ledger");
        }
        if (request.getType() == Invoice.InvoiceType.PURCHASE &&
                ledger.getType() != Ledger.LedgerType.SUPPLIER) {
            throw new IllegalArgumentException("Purchase invoice requires a SUPPLIER ledger");
        }

        String invoiceNumber = generateInvoiceNumber(request.getType());

        Invoice invoice = Invoice.builder()
                .invoiceNumber(invoiceNumber)
                .type(request.getType())
                .ledger(ledger)
                .date(request.getDate() != null ? request.getDate() : LocalDate.now())
                .totalAmount(0.0)
                .status(Invoice.InvoiceStatus.PENDING)
                .items(new ArrayList<>())
                .build();

        invoice = invoiceRepository.save(invoice);

        double totalAmount = 0.0;
        List<InvoiceItem> invoiceItems = new ArrayList<>();

        for (InvoiceItemRequest itemRequest : request.getItems()) {
            StockItem stockItem = stockItemRepository.findById(itemRequest.getStockItemId())
                    .orElseThrow(() -> new IllegalArgumentException("Stock item not found: " + itemRequest.getStockItemId()));

            if (request.getType() == Invoice.InvoiceType.SALES) {
                if (stockItem.getQuantity() < itemRequest.getQuantity()) {
                    throw new IllegalArgumentException(
                        "Insufficient stock for: " + stockItem.getName() +
                        ". Available: " + stockItem.getQuantity()
                    );
                }
                stockItem.setQuantity(stockItem.getQuantity() - itemRequest.getQuantity());
            }

            if (request.getType() == Invoice.InvoiceType.PURCHASE) {
                stockItem.setQuantity(stockItem.getQuantity() + itemRequest.getQuantity());
            }

            stockItemRepository.save(stockItem);

            double amount = itemRequest.getQuantity() * itemRequest.getRate();
            totalAmount += amount;

            InvoiceItem invoiceItem = InvoiceItem.builder()
                    .invoice(invoice)
                    .stockItem(stockItem)
                    .quantity(itemRequest.getQuantity())
                    .rate(itemRequest.getRate())
                    .amount(amount)
                    .build();

            invoiceItems.add(invoiceItemRepository.save(invoiceItem));
        }

        invoice.setTotalAmount(totalAmount);
        invoice.setItems(invoiceItems);

        ledger.setCurrentBalance(ledger.getCurrentBalance() + totalAmount);
        ledgerRepository.save(ledger);

        return InvoiceResponse.from(invoiceRepository.save(invoice));
    }

    public List<InvoiceResponse> getAll() {
        return invoiceRepository.findAll()
                .stream()
                .map(InvoiceResponse::from)
                .collect(Collectors.toList());
    }

    public List<InvoiceResponse> getByType(Invoice.InvoiceType type) {
        return invoiceRepository.findByType(type)
                .stream()
                .map(InvoiceResponse::from)
                .collect(Collectors.toList());
    }

    public InvoiceResponse getById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));
        return InvoiceResponse.from(invoice);
    }

    private String generateInvoiceNumber(Invoice.InvoiceType type) {
        String prefix = type == Invoice.InvoiceType.SALES ? "SALE" : "PUR";
        long count = invoiceRepository.count() + 1;
        return prefix + "-" + String.format("%04d", count);
    }
}