package org.example.tpgraphql.controller;

import org.apache.el.parser.ParseException;
import org.example.tpgraphql.dto.TransactionRequest;
import org.example.tpgraphql.entity.Compte;
import org.example.tpgraphql.entity.Transaction;
import org.example.tpgraphql.entity.TypeCompte;
import org.example.tpgraphql.entity.TypeTransaction;
import org.example.tpgraphql.repository.CompteRepository;
import org.example.tpgraphql.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Controller
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class CompteControllerGraphQL {

    private final CompteRepository compteRepository;
    private final TransactionRepository transactionRepository;

    @QueryMapping
    public List<Compte> allComptes() {
        return compteRepository.findAll();
    }

    @QueryMapping
    public Compte compteById(@Argument Long id) {
        return compteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compte not found"));
    }
    @QueryMapping
    public List<Compte> findByType(@Argument TypeCompte type) {
        return compteRepository.findByType(type);
    }



    @MutationMapping
    public Compte saveCompte(@Argument Compte compte) {
        // Vérifiez si la date est au bon format
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            formatter.parse(compte.getDateCreation()); // Valide le format
        } catch (java.text.ParseException e) {
            throw new RuntimeException("Invalid date format. Expected 'YYYY-MM-DD'.", e);
        }

        // Sauvegarder le compte
        return compteRepository.save(compte);
    }

    @QueryMapping
    public Map<String, Object> totalSolde() {
        long count = compteRepository.count();
        double sum = compteRepository.sumSoldes();
        double average = count > 0 ? sum / count : 0;

        return Map.of(
                "count", count,
                "sum", sum,
                "average", average
        );
    }

    @MutationMapping
    public Transaction addTransaction(@Argument TransactionRequest transactionRequest){
        Compte compte = compteRepository.findById(transactionRequest.getCompteId())
                .orElseThrow(() ->new RuntimeException("Compte not found "));
        Transaction transaction = new Transaction();
        transaction.setMontant(transactionRequest.getMontant());
        transaction.setDate(transactionRequest.getDate());
        transaction.setType(transactionRequest.getType());
        transaction.setCompte(compte);
        transactionRepository.save(transaction);
        return transaction;

    }



    @MutationMapping
    public String deleteById(@Argument Long id) {
        if (compteRepository.existsById(id)) {
            compteRepository.deleteById(id);
            return "Compte supprimé avec succès.";
        } else {
            throw new RuntimeException("Compte introuvable avec l'ID : " + id);
        }
    }

    @QueryMapping
    public Map<String, Object> transactionStats() {
        long count = transactionRepository.count();
        double sumDepots = transactionRepository.sumByType(TypeTransaction.DEPOT) != null
                ? transactionRepository.sumByType(TypeTransaction.DEPOT)
                : 0.0;
        double sumRetraits = transactionRepository.sumByType(TypeTransaction.RETRAIT) != null
                ? transactionRepository.sumByType(TypeTransaction.RETRAIT)
                : 0.0;

        return Map.of(
                "count", count,
                "sumDepots", sumDepots,
                "sumRetraits", sumRetraits
        );
    }
}
