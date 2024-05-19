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

        when(transactionsService.getTransactionsByUser("FirstUser")).thenReturn(Arrays.asList(transaction1, transaction2));

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
    void whenFindByUserWithNoTransactions_thenReturnNotFound()
    {
        when(transactionsService.getTransactionsByUser("FirstUser")).thenReturn(Arrays.asList());

        RestAssuredMockMvc.given()
            .when()
            .get("/transactions_by_user/FirstUser")
            .then()
            .statusCode(404);

        verify(transactionsService, times(1)).getTransactionsByUser("FirstUser");
    }

    @Test
    @DisplayName("Test to find all transactions by flight")
    void whenFindByFlight_thenReturnTransactionList() {
        Transactions transaction1 = new Transactions();
        transaction1.setIataFlight("AA123");
        Transactions transaction2 = new Transactions();
        transaction2.setIataFlight("AA123");
            
        when(transactionsService.getTransactionsByFlight("AA123")).thenReturn(Arrays.asList(transaction1, transaction2));

        RestAssuredMockMvc.given()
            .when()
            .get("/transactions_by_flight/AA123")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .and()
            .body("[0].iataFlight", is("AA123"))
            .body("[1].iataFlight", is("AA123"));

        verify(transactionsService, times(1)).getTransactionsByFlight("AA123");
    }

    @Test
    @DisplayName("Test to find all transactions by flight with no transactions")
    void whenFindByFlightWithNoTransactions_thenReturnNotFound()
    {
        when(transactionsService.getTransactionsByFlight("AA123")).thenReturn(Arrays.asList());

        RestAssuredMockMvc.given()
            .when()
            .get("/transactions_by_flight/AA123")
            .then()
            .statusCode(404);

        verify(transactionsService, times(1)).getTransactionsByFlight("AA123");
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
            .statusCode(200);

        // Verify that createTransaction method is called with the captured argument
        verify(transactionsService, times(1)).createTransaction(argumentCaptor.capture());

        // Assert that the captured argument is the same as the transaction object
        Transactions capturedTransaction = argumentCaptor.getValue();
        assertEquals("FirstUser", capturedTransaction.getUserEmail());
        assertEquals("AA123", capturedTransaction.getIataFlight());
        assertEquals(TransactionStatus.CANCELED, capturedTransaction.getStatus());
    }

    
}
