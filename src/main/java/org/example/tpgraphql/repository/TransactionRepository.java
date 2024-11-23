package org.example.tpgraphql.repository;


import org.example.tpgraphql.entity.Compte;
import org.example.tpgraphql.entity.Transaction;
import org.example.tpgraphql.entity.TypeTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByCompte(Compte compte);

    @Query("SELECT SUM(t.montant) FROM Transaction t WHERE t.type = :type")
    Double sumByType(@Param("type") TypeTransaction type);
}
