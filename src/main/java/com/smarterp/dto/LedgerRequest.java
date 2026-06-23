package com.smarterp.dto;

import com.smarterp.entities.Ledger;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LedgerRequest {

    @NotBlank(message = "Ledger name is required")
    private String name;

    @NotNull(message = "Ledger type is required")
    private Ledger.LedgerType type;

    private Double openingBalance = 0.0;
}