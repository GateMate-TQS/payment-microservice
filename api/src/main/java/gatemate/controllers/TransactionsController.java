package gatemate.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import gatemate.entities.Transactions;
import gatemate.services.TransactionNotFoundException;
import gatemate.services.TransactionsService;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@AllArgsConstructor
@RequestMapping("/")
public class TransactionsController {
    private final TransactionsService transactionsService;

    @Operation(summary = "Obter transações por e-mail do usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transações encontradas", content = @Content(schema = @Schema(implementation = Transactions.class))),
            @ApiResponse(responseCode = "404", description = "Nenhuma transação encontrada para o usuário", content = @Content(schema = @Schema(implementation = String.class)))
    })
    @GetMapping("/transactions_by_user/{userEmail}")
    public ResponseEntity<Object> getTransactionsByUser(@PathVariable String userEmail) {
        List<Transactions> transactions = transactionsService.getTransactionsByUser(userEmail);

        if (transactions.isEmpty()) {
            return new ResponseEntity<>("No transactions found for user: " + userEmail, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(transactions, HttpStatus.OK);
        }
    }

    @Operation(summary = "Criar uma nova transação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Transação criada", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Dados da transação inválidos", content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/create_transaction")
    public ResponseEntity<String> createTransaction(@RequestBody Transactions transaction) {
        if (transaction.getUserEmail() == null || transaction.getIataFlight() == null
                || transaction.getStatus() == null) {
            return new ResponseEntity<>("Invalid transaction data", HttpStatus.BAD_REQUEST);
        }
        transactionsService.createTransaction(transaction);
        return new ResponseEntity<>("Transaction created", HttpStatus.CREATED);
    }

    @Operation(summary = "Atualizar uma transação existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transação atualizada", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Dados da transação inválidos", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "Transação não encontrada", content = @Content(schema = @Schema(implementation = String.class)))
    })
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

    @Operation(summary = "Obter informações de uma transação pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transação encontrada", content = @Content(schema = @Schema(implementation = Transactions.class))),
            @ApiResponse(responseCode = "400", description = "ID da transação inválido", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "Transação não encontrada", content = @Content(schema = @Schema(implementation = String.class)))
    })
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
