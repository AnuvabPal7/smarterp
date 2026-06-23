package com.smarterp.dto;

import com.smarterp.entities.Ledger;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LedgerResponse {
    private Long id;
    private String name;
    private Ledger.LedgerType type;
    private Double openingBalance;
    private Double currentBalance;

    public static LedgerResponse from(Ledger ledger) {
        return new LedgerResponse(
            ledger.getId(),
            ledger.getName(),
            ledger.getType(),
            ledger.getOpeningBalance(),
            ledger.getCurrentBalance()
        );
    }
}