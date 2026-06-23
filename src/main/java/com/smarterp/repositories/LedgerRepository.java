package com.smarterp.repositories;

import com.smarterp.entities.Ledger;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LedgerRepository extends JpaRepository<Ledger, Long> {
    List<Ledger> findByType(Ledger.LedgerType type);
    boolean existsByName(String name);
}