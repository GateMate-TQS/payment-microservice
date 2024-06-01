package gatemate.controllers;

import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import gatemate.entities.TransactionStatus;
import gatemate.entities.Transactions;
import gatemate.repositories.TransactionsRepository;
import gatemate.services.TransactionsService;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;

@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@SpringBootTest
class TransactionsControllerIT {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TransactionsRepository transactionsRepository;

    @Mock
    private TransactionsService transactionsService;

    @BeforeEach
    void setUp() {
        Transactions transaction1 = new Transactions();
        transaction1.setUserEmail("FirstUser");
        transaction1.setIataFlight("FirstFlight");
        transaction1.setStatus(TransactionStatus.CANCELED);

        Transactions transaction2 = new Transactions();
        transaction2.setUserEmail("FirstUser");
        transaction2.setIataFlight("SecondFlight");
        transaction2.setStatus(TransactionStatus.CHECKEDIN);

        Transactions transaction3 = new Transactions();
        transaction3.setUserEmail("SecondUser");
        transaction3.setIataFlight("FirstFlight");
        transaction3.setStatus(TransactionStatus.PAYED);

        transactionsRepository.save(transaction1);
        transactionsRepository.save(transaction2);
        transactionsRepository.save(transaction3);
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @AfterEach
    void clearDatabase() {
        transactionsRepository.deleteAll();
    }

    @Test
    @DisplayName("Test to find all transactions by user")
    void whenFindByUser_thenReturnTransactionList() {
        RestAssuredMockMvc.given()
                .when()
                .get("/transactions_by_user/FirstUser")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .and()
                .body("$.size()", is(2))
                .body("[0].userEmail", is("FirstUser"))
                .body("[1].userEmail", is("FirstUser"));
    }

    @Test
    @DisplayName("Test to find all transactions by user with no transactions")
    void whenFindByUserWithNoTransactions_thenReturnNotFound() {
        RestAssuredMockMvc.given()
                .when()
                .get("/transactions_by_user/ThirdUser")
                .then()
                .statusCode(404)
                .body(is("No transactions found for user: ThirdUser"));
    }

    @Test
    @DisplayName("Test to create transaction")
    void whenCreateTransaction_thenReturnCreated() {
        Transactions transaction = new Transactions();
        transaction.setUserEmail("ThirdUser");
        transaction.setIataFlight("ThirdFlight");
        transaction.setStatus(TransactionStatus.PAYED);

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(transaction)
                .when()
                .post("/create_transaction")
                .then()
                .statusCode(201)
                .body(is("Transaction created"));
    }

    @Test
    @DisplayName("Test to create transaction with invalid data")
    void whenCreateTransactionWithInvalidData_thenReturnBadRequest() {
        Transactions transaction = new Transactions();
        transaction.setUserEmail("ThirdUser");

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(transaction)
                .when()
                .post("/create_transaction")
                .then()
                .statusCode(400)
                .body(is("Invalid transaction data"));
    }

    @Test
    @DisplayName("Test to update transaction")
    void whenUpdateTransaction_thenReturnUpdated() {
        Transactions transaction = new Transactions();
        transaction.setUserEmail("FirstUser");
        transaction.setIataFlight("FirstFlight");
        transaction.setStatus(TransactionStatus.CANCELED);

        Transactions savedTransaction = transactionsRepository.save(transaction);

        transaction.setStatus(TransactionStatus.CHECKEDIN);

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(transaction)
                .when()
                .put("/update_transaction/" + savedTransaction.getId())
                .then()
                .statusCode(200)
                .body(is("Transaction updated"));
    }

    @Test
    @DisplayName("Test to update transaction with non-existent ID")
    void whenUpdateTransactionWithNonExistentId_thenReturnNotFound() {
        Transactions transaction = new Transactions();
        transaction.setUserEmail("NonExistentUser");
        transaction.setIataFlight("NonExistentFlight");
        transaction.setStatus(TransactionStatus.CANCELED);

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(transaction)
                .when()
                .put("/update_transaction/999")
                .then()
                .statusCode(404)
                .body(is("Transaction not found"));
    }

    @Test
    @DisplayName("Test to update transaction with invalid data")
    void whenUpdateTransactionWithInvalidData_thenReturnBadRequest() {
        Transactions transaction = new Transactions();
        transaction.setUserEmail("FirstUser");
        // Missing required fields

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(transaction)
                .when()
                .put("/update_transaction/1")
                .then()
                .statusCode(400)
                .body(is("Invalid transaction data"));
    }

    @Test
    @DisplayName("Test to get transaction by ID")
    void whenGetTransactionById_thenReturnTransaction() {
        Transactions transaction = new Transactions();
        transaction.setUserEmail("FirstUser");
        transaction.setIataFlight("AA123");
        transaction.setStatus(TransactionStatus.PAYED);

        Transactions savedTransaction = transactionsRepository.save(transaction);

        RestAssuredMockMvc.given()
                .when()
                .get("/" + savedTransaction.getId())
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .and()
                .body("id", is(savedTransaction.getId().intValue()))
                .body("userEmail", is("FirstUser"))
                .body("iataFlight", is("AA123"))
                .body("status", is("PAYED"));
    }

    @Test
    @DisplayName("Test to get a transaction by ID with non-existent ID")
    void whenGetTransactionByNonExistentId_thenReturnNotFound() {
        RestAssuredMockMvc.given()
                .when()
                .get("/999")
                .then()
                .statusCode(404)
                .body(is("Transaction not found"));
    }

    @Test
    @DisplayName("Test to get a transaction by ID with invalid ID")
    void whenGetTransactionByInvalidId_thenReturnBadRequest() {
        RestAssuredMockMvc.given()
                .when()
                .get("/invalid-id")
                .then()
                .statusCode(400)
                .body(is("Invalid transaction ID"));
    }
}