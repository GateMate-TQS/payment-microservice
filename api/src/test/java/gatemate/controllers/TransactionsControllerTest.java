package gatemate.controllers;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import gatemate.entities.TransactionStatus;
import gatemate.entities.Transactions;
import gatemate.services.TransactionNotFoundException;
import gatemate.services.TransactionsService;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;

@WebMvcTest(TransactionsController.class)
class TransactionsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionsService transactionsService;

    @BeforeEach
    public void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Test
    @DisplayName("Test to find all transactions by user")
    void whenFindByUser_thenReturnTransactionList() {
        Transactions transaction1 = new Transactions();
        transaction1.setUserEmail("FirstUser");
        Transactions transaction2 = new Transactions();
        transaction2.setUserEmail("FirstUser");

        when(transactionsService.getTransactionsByUser("FirstUser"))
                .thenReturn(Arrays.asList(transaction1, transaction2));

        RestAssuredMockMvc.given()
                .when()
                .get("/transactions_by_user/FirstUser")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .and()
                .body("[0].userEmail", is("FirstUser"))
                .body("[1].userEmail", is("FirstUser"));

        verify(transactionsService, times(1)).getTransactionsByUser("FirstUser");
    }

    @Test
    @DisplayName("Test to find all transactions by user with no transactions")
    void whenFindByUserWithNoTransactions_thenReturnNotFound() {
        when(transactionsService.getTransactionsByUser("FirstUser")).thenReturn(Arrays.asList());

        RestAssuredMockMvc.given()
                .when()
                .get("/transactions_by_user/FirstUser")
                .then()
                .statusCode(404);

        verify(transactionsService, times(1)).getTransactionsByUser("FirstUser");
    }

    @Test
    @DisplayName("Test to create a transaction")
    void whenCreateTransaction_thenReturnTransactionCreated() {
        // Create a dictionary representing the transaction
        Map<String, Object> transactionBody = new HashMap<>();
        transactionBody.put("userEmail", "FirstUser");
        transactionBody.put("iataFlight", "AA123");
        transactionBody.put("status", "CANCELED");

        // Capture the argument passed to createTransaction
        ArgumentCaptor<Transactions> argumentCaptor = ArgumentCaptor.forClass(Transactions.class);

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(transactionBody)
                .when()
                .post("/create_transaction")
                .then()
                .statusCode(201); // Changed to 201 Created for REST best practices

        // Verify that createTransaction method is called with the captured argument
        verify(transactionsService, times(1)).createTransaction(argumentCaptor.capture());

        // Assert that the captured argument is the same as the transaction object
        Transactions capturedTransaction = argumentCaptor.getValue();
        assertEquals("FirstUser", capturedTransaction.getUserEmail());
        assertEquals("AA123", capturedTransaction.getIataFlight());
        assertEquals(TransactionStatus.CANCELED, capturedTransaction.getStatus());
    }

    @Test
    @DisplayName("Test to create a transaction with invalid data")
    void whenCreateTransactionWithInvalidData_thenReturnBadRequest() {
        // Create an incomplete dictionary representing the transaction
        Map<String, Object> transactionBody = new HashMap<>();
        transactionBody.put("userEmail", "FirstUser");
        transactionBody.put("status", "invalid_status");

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(transactionBody)
                .when()
                .post("/create_transaction")
                .then()
                .statusCode(400); // Expecting 400 Bad Request due to invalid input
    }

    @Test
    @DisplayName("Test to update a transaction")
    void whenUpdateTransaction_thenReturnTransactionUpdated() {
        Transactions transaction = new Transactions();
        transaction.setId(1L);
        transaction.setUserEmail("FirstUser");
        transaction.setIataFlight("AA123");
        transaction.setStatus(TransactionStatus.CANCELED);

        when(transactionsService.getTransaction(1L)).thenReturn(transaction);

        Map<String, Object> transactionBody = new HashMap<>();
        transactionBody.put("userEmail", "FirstUser");
        transactionBody.put("iataFlight", "AA123");
        transactionBody.put("status", "CHECKEDIN");

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(transactionBody)
                .when()
                .put("/update_transaction/1")
                .then()
                .statusCode(200)
                .body(is("Transaction updated"));

        verify(transactionsService, times(1)).updateTransaction(1L);
    }

    @Test
    @DisplayName("Test to update a transaction with invalid ID")
    void whenUpdateTransactionWithInvalidId_thenReturnNotFound() {
        doThrow(new TransactionNotFoundException("Transaction not found")).when(transactionsService)
                .updateTransaction(999L);

        Map<String, Object> transactionBody = new HashMap<>();
        transactionBody.put("userEmail", "NonExistentUser");
        transactionBody.put("iataFlight", "NonExistentFlight");
        transactionBody.put("status", "CANCELED");

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(transactionBody)
                .when()
                .put("/update_transaction/999")
                .then()
                .statusCode(404)
                .body(is("Transaction not found"));

        verify(transactionsService, times(1)).updateTransaction(999L);
    }

    @Test
    @DisplayName("Test to get a transaction by ID")
    void whenGetTransactionById_thenReturnTransaction() {
        Transactions transaction = new Transactions();
        transaction.setId(1L);
        transaction.setUserEmail("FirstUser");
        transaction.setIataFlight("AA123");
        transaction.setStatus(TransactionStatus.PAYED);

        when(transactionsService.getTransaction(1L)).thenReturn(transaction);

        RestAssuredMockMvc.given()
                .when()
                .get("/1")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .and()
                .body("id", is(1))
                .body("userEmail", is("FirstUser"))
                .body("iataFlight", is("AA123"))
                .body("status", is("PAYED"));

        verify(transactionsService, times(1)).getTransaction(1L);
    }

    @Test
    @DisplayName("Test to get a transaction by ID with invalid ID")
    void whenGetTransactionByInvalidId_thenReturnBadRequest() {
        RestAssuredMockMvc.given()
                .when()
                .get("/invalid-id")
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Test to get a transaction by ID with non-existent ID")
    void whenGetTransactionByNonExistentId_thenReturnNotFound() {
        when(transactionsService.getTransaction(1L)).thenReturn(null);

        RestAssuredMockMvc.given()
                .when()
                .get("/1")
                .then()
                .statusCode(404);

        verify(transactionsService, times(1)).getTransaction(1L);
    }
}
