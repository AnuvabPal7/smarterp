package com.smarterp.services;

import com.smarterp.dto.ReportResponse;
import com.smarterp.dto.StockItemResponse;
import com.smarterp.entities.Invoice;
import com.smarterp.repositories.InvoiceRepository;
import com.smarterp.repositories.StockItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final InvoiceRepository invoiceRepository;
    private final StockItemRepository stockItemRepository;

    @Value("${groq.api.key}")
    private String groqApiKey;

    @Transactional(readOnly = true)
    public ReportResponse generateReport() throws Exception {
        List<Invoice> allInvoices = invoiceRepository.findAll();

        double totalSales = allInvoices.stream()
                .filter(i -> i.getType() == Invoice.InvoiceType.SALES)
                .mapToDouble(Invoice::getTotalAmount)
                .sum();

        double totalPurchase = allInvoices.stream()
                .filter(i -> i.getType() == Invoice.InvoiceType.PURCHASE)
                .mapToDouble(Invoice::getTotalAmount)
                .sum();

        double totalProfit = totalSales - totalPurchase;

        int totalStockItems = (int) stockItemRepository.count();

        List<StockItemResponse> lowStockItems = stockItemRepository
                .findByQuantityLessThan(10)
                .stream()
                .map(StockItemResponse::from)
                .collect(Collectors.toList());

        int salesCount = (int) allInvoices.stream()
                .filter(i -> i.getType() == Invoice.InvoiceType.SALES)
                .count();

        int purchaseCount = (int) allInvoices.stream()
                .filter(i -> i.getType() == Invoice.InvoiceType.PURCHASE)
                .count();

        String aiSummary = generateAiSummary(totalSales, totalPurchase, totalProfit, totalStockItems, lowStockItems.size());

        return new ReportResponse(
                totalSales,
                totalPurchase,
                totalProfit,
                totalStockItems,
                lowStockItems.size(),
                allInvoices.size(),
                salesCount,
                purchaseCount,
                lowStockItems,
                aiSummary
        );
    }
    private String generateAiSummary(double totalSales, double totalPurchase,
                                      double totalProfit, int totalStockItems, int lowStockCount) throws Exception {
        String prompt = String.format(
                "You are a business analyst for a small retail business. " +
                "Analyze this business data and give a brief 3-4 sentence summary in plain English:\\n" +
                "- Total Sales: Rs. %.2f\\n" +
                "- Total Purchases: Rs. %.2f\\n" +
                "- Net Profit/Loss: Rs. %.2f\\n" +
                "- Total Stock Items: %d\\n" +
                "- Low Stock Alerts: %d items\\n" +
                "Give practical business insights and recommendations.",
                totalSales, totalPurchase, totalProfit, totalStockItems, lowStockCount
        );

        String requestBody = "{"
                + "\"model\": \"llama-3.3-70b-versatile\","
                + "\"messages\": [{\"role\": \"user\", \"content\": \"" + prompt.replace("\"", "\\\"") + "\"}],"
                + "\"max_tokens\": 200"
                + "}";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.groq.com/openai/v1/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + groqApiKey)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String responseBody = response.body();

        System.out.println("Groq response: " + responseBody);

        int start = responseBody.indexOf("\"content\":\"");
        if (start != -1) {
            start += 11;
            StringBuilder content = new StringBuilder();
            int i = start;
            while (i < responseBody.length()) {
                char c = responseBody.charAt(i);
                if (c == '\\' && i + 1 < responseBody.length()) {
                    char next = responseBody.charAt(i + 1);
                    if (next == '"') { content.append('"'); i += 2; continue; }
                    if (next == 'n') { content.append(' '); i += 2; continue; }
                    if (next == '\\') { content.append('\\'); i += 2; continue; }
                }
                if (c == '"') break;
                content.append(c);
                i++;
            }
            return content.toString().trim();
        }

        return "AI summary unavailable.";
    }
}