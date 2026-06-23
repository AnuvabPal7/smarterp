package com.smarterp.services;

import com.smarterp.dto.LedgerRequest;
import com.smarterp.dto.LedgerResponse;
import com.smarterp.entities.Ledger;
import com.smarterp.repositories.LedgerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LedgerService {

    private final LedgerRepository ledgerRepository;

    public LedgerResponse create(LedgerRequest request) {
        if (ledgerRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Ledger with this name already exists");
        }

        Ledger ledger = Ledger.builder()
                .name(request.getName())
                .type(request.getType())
                .openingBalance(request.getOpeningBalance())
                .currentBalance(request.getOpeningBalance())
                .build();

        return LedgerResponse.from(ledgerRepository.save(ledger));
    }

    public List<LedgerResponse> getAll() {
        return ledgerRepository.findAll()
                .stream()
                .map(LedgerResponse::from)
                .collect(Collectors.toList());
    }

    public List<LedgerResponse> getByType(Ledger.LedgerType type) {
        return ledgerRepository.findByType(type)
                .stream()
                .map(LedgerResponse::from)
                .collect(Collectors.toList());
    }

    public LedgerResponse update(Long id, LedgerRequest request) {
        Ledger ledger = ledgerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ledger not found"));

        ledger.setName(request.getName());
        ledger.setType(request.getType());
        ledger.setOpeningBalance(request.getOpeningBalance());

        return LedgerResponse.from(ledgerRepository.save(ledger));
    }

    public void delete(Long id) {
        if (!ledgerRepository.existsById(id)) {
            throw new IllegalArgumentException("Ledger not found");
        }
        ledgerRepository.deleteById(id);
    }
}