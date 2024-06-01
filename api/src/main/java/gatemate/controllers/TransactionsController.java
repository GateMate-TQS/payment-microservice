package gatemate.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import gatemate.entities.Transactions;
import gatemate.services.TransactionNotFoundException;
import gatemate.services.TransactionsService;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/")
public class TransactionsController {
    private final TransactionsService transactionsService;

    @GetMapping("/transactions_by_user/{userEmail}")
    public ResponseEntity<Object> getTransactionsByUser(@PathVariable String userEmail) {
        List<Transactions> transactions = transactionsService.getTransactionsByUser(userEmail);

        if (transactions.isEmpty()) {
            return new ResponseEntity<>("No transactions found for user: " + userEmail, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(transactions, HttpStatus.OK);
        }
    }

    @PostMapping("/create_transaction")
    public ResponseEntity<String> createTransaction(@RequestBody Transactions transaction) {
        if (transaction.getUserEmail() == null || transaction.getIataFlight() == null
                || transaction.getStatus() == null) {
            return new ResponseEntity<>("Invalid transaction data", HttpStatus.BAD_REQUEST);
        }
        transactionsService.createTransaction(transaction);
        return new ResponseEntity<>("Transaction created", HttpStatus.CREATED);
    }

    @PutMapping("/update_transaction/{id}")
    public ResponseEntity<String> updateTransaction(@PathVariable Long id, @RequestBody Transactions transaction) {
        if (transaction.getUserEmail() == null || transaction.getIataFlight() == null
                || transaction.getStatus() == null) {
            return new ResponseEntity<>("Invalid transaction data", HttpStatus.BAD_REQUEST);
        }
        try {
            transactionsService.updateTransaction(id);
            return new ResponseEntity<>("Transaction updated", HttpStatus.OK);
        } catch (TransactionNotFoundException e) {
            return new ResponseEntity<>("Transaction not found", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getTransaction(@PathVariable String id) {
        Long longId;
        try {
            longId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            return new ResponseEntity<>("Invalid transaction ID", HttpStatus.BAD_REQUEST);
        }

        Transactions transaction = transactionsService.getTransaction(longId);

        if (transaction == null) {
            return new ResponseEntity<>("Transaction not found", HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(transaction, HttpStatus.OK);
        }
    }
}
