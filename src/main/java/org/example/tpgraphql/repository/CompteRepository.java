package org.example.tpgraphql.repository;

import org.example.tpgraphql.entity.Compte;
import org.example.tpgraphql.entity.TypeCompte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CompteRepository extends JpaRepository<Compte, Long> {
    List<Compte> findByType(TypeCompte type);
    void deleteById(Long id);
    @Query("SELECT SUM(c.solde) FROM Compte c")
    double sumSoldes();
}

