package gatemate.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gatemate.entities.Transactions;
import gatemate.services.TransactionsService;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/")
public class TransactionsController {
    private final TransactionsService transactionsService;

    @GetMapping("/transactions_by_user/{userEmail}")
    public ResponseEntity<List<Transactions>> getTransactionsByUser(@PathVariable String userEmail) {
        List<Transactions> transactions = transactionsService.getTransactionsByUser(userEmail);

        if (transactions.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(transactions, HttpStatus.OK);
        }
    }

    @GetMapping("/transactions_by_flight/{iataFlight}")
    public ResponseEntity<List<Transactions>> getTransactionsByFlight(@PathVariable String iataFlight) {
        List<Transactions> transactions = transactionsService.getTransactionsByFlight(iataFlight);

        if (transactions.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(transactions, HttpStatus.OK);
        }
    }

    @PostMapping("/create_transaction")
    public ResponseEntity<String> createTransaction(@RequestBody Transactions transaction) {
        // System.out.println("Creating transaction");
        transactionsService.createTransaction(transaction);
        return new ResponseEntity<>("Transaction created", HttpStatus.OK);
    }

}
