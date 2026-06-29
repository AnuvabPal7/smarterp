package com.smarterp.services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.smarterp.entities.Invoice;
import com.smarterp.entities.InvoiceItem;
import com.smarterp.repositories.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
public class PdfService {

    private final InvoiceRepository invoiceRepository;

    @Transactional(readOnly = true)
    public byte[] generateInvoicePdf(Long invoiceId) throws Exception {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, baos);
        document.open();

        // Fonts
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, BaseColor.DARK_GRAY);
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.DARK_GRAY);
        Font boldFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.DARK_GRAY);
        Font smallFont = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.GRAY);

        // Header
        Paragraph title = new Paragraph("SmartERP", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        Paragraph subtitle = new Paragraph("Business Management System", smallFont);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        document.add(subtitle);

        document.add(Chunk.NEWLINE);

        // Invoice type label
        String invoiceLabel = invoice.getType() == Invoice.InvoiceType.SALES ? "SALES INVOICE" : "PURCHASE INVOICE";
        Paragraph invoiceTitle = new Paragraph(invoiceLabel, new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.DARK_GRAY));
        invoiceTitle.setAlignment(Element.ALIGN_CENTER);
        document.add(invoiceTitle);

        document.add(Chunk.NEWLINE);

        // Invoice details table
        PdfPTable detailsTable = new PdfPTable(2);
        detailsTable.setWidthPercentage(100);

        addDetailRow(detailsTable, "Invoice Number:", invoice.getInvoiceNumber(), boldFont, normalFont);
        addDetailRow(detailsTable, "Date:", invoice.getDate().toString(), boldFont, normalFont);
        addDetailRow(detailsTable, "Party:", invoice.getLedger().getName(), boldFont, normalFont);
        addDetailRow(detailsTable, "Status:", invoice.getStatus().toString(), boldFont, normalFont);

        document.add(detailsTable);
        document.add(Chunk.NEWLINE);

        // Items table
        PdfPTable itemsTable = new PdfPTable(5);
        itemsTable.setWidthPercentage(100);
        itemsTable.setWidths(new float[]{3, 1, 2, 2, 2});

        // Table header
        String[] headers = {"Item Name", "Qty", "Unit", "Rate (Rs.)", "Amount (Rs.)"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(new BaseColor(52, 73, 94));
            cell.setPadding(8);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            itemsTable.addCell(cell);
        }

        // Table rows
        double total = 0;
        for (InvoiceItem item : invoice.getItems()) {
            addItemRow(itemsTable, item.getStockItem().getName(), normalFont);
            addItemRow(itemsTable, String.valueOf(item.getQuantity()), normalFont);
            addItemRow(itemsTable, item.getStockItem().getUnit(), normalFont);
            addItemRow(itemsTable, String.format("%.2f", item.getRate()), normalFont);
            addItemRow(itemsTable, String.format("%.2f", item.getAmount()), normalFont);
            total += item.getAmount();
        }

        document.add(itemsTable);
        document.add(Chunk.NEWLINE);

        // Total
        PdfPTable totalTable = new PdfPTable(2);
        totalTable.setWidthPercentage(40);
        totalTable.setHorizontalAlignment(Element.ALIGN_RIGHT);

        PdfPCell totalLabelCell = new PdfPCell(new Phrase("TOTAL AMOUNT:", boldFont));
        totalLabelCell.setBorder(Rectangle.NO_BORDER);
        totalLabelCell.setPadding(5);
        totalTable.addCell(totalLabelCell);

        PdfPCell totalValueCell = new PdfPCell(new Phrase(String.format("Rs. %.2f", total), boldFont));
        totalValueCell.setBorder(Rectangle.NO_BORDER);
        totalValueCell.setPadding(5);
        totalValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalTable.addCell(totalValueCell);

        document.add(totalTable);

        document.add(Chunk.NEWLINE);

        // Footer
        Paragraph footer = new Paragraph("Thank you for your business!", smallFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        Paragraph generated = new Paragraph("Generated by SmartERP", smallFont);
        generated.setAlignment(Element.ALIGN_CENTER);
        document.add(generated);

        document.close();
        return baos.toByteArray();
    }

    private void addDetailRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(4);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(4);
        table.addCell(valueCell);
    }

    private void addItemRow(PdfPTable table, String value, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(value, font));
        cell.setPadding(6);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }
}