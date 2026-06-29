package com.smarterp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {
    private Double totalSalesAmount;
    private Double totalPurchaseAmount;
    private Double totalProfit;
    private Integer totalStockItems;
    private Integer lowStockCount;
    private Integer totalInvoices;
    private Integer salesInvoiceCount;
    private Integer purchaseInvoiceCount;
    private List<StockItemResponse> lowStockItems;
    private String aiSummary;
}