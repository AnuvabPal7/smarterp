package com.smarterp.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "ledgers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ledger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LedgerType type;

    @Column(nullable = false)
    private Double openingBalance;

    @Column(nullable = false)
    private Double currentBalance;

    @OneToMany(mappedBy = "ledger", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Invoice> invoices;

    public enum LedgerType {
        CUSTOMER, SUPPLIER, EXPENSE, INCOME
    }
}