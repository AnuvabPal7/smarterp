package com.smarterp.services;

import com.smarterp.dto.StockItemRequest;
import com.smarterp.dto.StockItemResponse;
import com.smarterp.entities.StockItem;
import com.smarterp.repositories.StockItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockItemService {

    private final StockItemRepository stockItemRepository;

    public StockItemResponse create(StockItemRequest request) {
        if (stockItemRepository.existsBySku(request.getSku())) {
            throw new IllegalArgumentException("Stock item with this SKU already exists");
        }

        StockItem item = StockItem.builder()
                .name(request.getName())
                .sku(request.getSku())
                .purchasePrice(request.getPurchasePrice())
                .sellingPrice(request.getSellingPrice())
                .quantity(request.getQuantity())
                .unit(request.getUnit())
                .build();

        return StockItemResponse.from(stockItemRepository.save(item));
    }

    public List<StockItemResponse> getAll() {
        return stockItemRepository.findAll()
                .stream()
                .map(StockItemResponse::from)
                .collect(Collectors.toList());
    }

    public StockItemResponse getById(Long id) {
        StockItem item = stockItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Stock item not found"));
        return StockItemResponse.from(item);
    }

    public StockItemResponse update(Long id, StockItemRequest request) {
        StockItem item = stockItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Stock item not found"));

        item.setName(request.getName());
        item.setSku(request.getSku());
        item.setPurchasePrice(request.getPurchasePrice());
        item.setSellingPrice(request.getSellingPrice());
        item.setQuantity(request.getQuantity());
        item.setUnit(request.getUnit());

        return StockItemResponse.from(stockItemRepository.save(item));
    }

    public void delete(Long id) {
        if (!stockItemRepository.existsById(id)) {
            throw new IllegalArgumentException("Stock item not found");
        }
        stockItemRepository.deleteById(id);
    }

    public List<StockItemResponse> getLowStock(Integer threshold) {
        return stockItemRepository.findByQuantityLessThan(threshold)
                .stream()
                .map(StockItemResponse::from)
                .collect(Collectors.toList());
    }
}