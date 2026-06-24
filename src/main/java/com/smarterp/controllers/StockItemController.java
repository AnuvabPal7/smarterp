package com.smarterp.controllers;

import com.smarterp.dto.StockItemRequest;
import com.smarterp.dto.StockItemResponse;
import com.smarterp.services.StockItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock-items")
@RequiredArgsConstructor
public class StockItemController {

    private final StockItemService stockItemService;

    @PostMapping
    public ResponseEntity<StockItemResponse> create(@Valid @RequestBody StockItemRequest request) {
        return ResponseEntity.ok(stockItemService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<StockItemResponse>> getAll() {
        return ResponseEntity.ok(stockItemService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockItemResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(stockItemService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StockItemResponse> update(@PathVariable Long id,
                                                     @Valid @RequestBody StockItemRequest request) {
        return ResponseEntity.ok(stockItemService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        stockItemService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<StockItemResponse>> getLowStock(
            @RequestParam(defaultValue = "10") Integer threshold) {
        return ResponseEntity.ok(stockItemService.getLowStock(threshold));
    }
}