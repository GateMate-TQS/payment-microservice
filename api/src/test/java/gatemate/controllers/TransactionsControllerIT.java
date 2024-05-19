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
            .statusCode(404);
    }

    @Test
    @DisplayName("Test to find all transactions by flight")
    void whenFindByFlight_thenReturnTransactionList() {
        RestAssuredMockMvc.given()
            .when()
            .get("/transactions_by_flight/FirstFlight")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .and()
            .body("[0].iataFlight", is("FirstFlight"))
            .body("[1].iataFlight", is("FirstFlight"));
    }

    @Test
    @DisplayName("Test to find all transactions by flight with no transactions")
    void whenFindByFlightWithNoTransactions_thenReturnNotFound() {
        RestAssuredMockMvc.given()
            .when()
            .get("/transactions_by_flight/ThirdFlight")
            .then()
            .statusCode(404);
    }
}
