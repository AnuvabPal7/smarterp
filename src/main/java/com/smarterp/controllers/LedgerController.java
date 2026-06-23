package com.smarterp.controllers;

import com.smarterp.dto.LedgerRequest;
import com.smarterp.dto.LedgerResponse;
import com.smarterp.entities.Ledger;
import com.smarterp.services.LedgerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ledgers")
@RequiredArgsConstructor
public class LedgerController {

    private final LedgerService ledgerService;

    @PostMapping
    public ResponseEntity<LedgerResponse> create(@Valid @RequestBody LedgerRequest request) {
        return ResponseEntity.ok(ledgerService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<LedgerResponse>> getAll() {
        return ResponseEntity.ok(ledgerService.getAll());
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<LedgerResponse>> getByType(@PathVariable Ledger.LedgerType type) {
        return ResponseEntity.ok(ledgerService.getByType(type));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LedgerResponse> update(@PathVariable Long id,
                                                  @Valid @RequestBody LedgerRequest request) {
        return ResponseEntity.ok(ledgerService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ledgerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}