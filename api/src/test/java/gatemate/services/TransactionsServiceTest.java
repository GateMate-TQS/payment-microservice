package gatemate.services;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import gatemate.entities.TransactionStatus;
import gatemate.entities.Transactions;
import gatemate.repositories.TransactionsRepository;


@ExtendWith(MockitoExtension.class)
class TransactionsServiceTest {
 
    @Mock
    private TransactionsRepository transactionsRepository;

    @InjectMocks
    private TransactionsServiceImpl transactionsServiceImpl;

    @BeforeEach
    public void setUp() {
        Transactions transaction1 = new Transactions();
        transaction1.setId(1L);
        transaction1.setUserEmail("FirstUser");
        transaction1.setIataFlight("AA123");
        transaction1.setStatus(TransactionStatus.PAYED);
        
        Transactions transaction2 = new Transactions();
        transaction2.setId(2L);
        transaction2.setUserEmail("FirstUser");
        transaction2.setIataFlight("AA456");
        transaction2.setStatus(TransactionStatus.CHECKEDIN);

        Transactions transaction3 = new Transactions();
        transaction3.setId(3L);
        transaction3.setUserEmail("SecondUser");
        transaction3.setIataFlight("AA456");
        transaction3.setStatus(TransactionStatus.PAYED);

        lenient().when(transactionsRepository.findByUserEmail("FirstUser")).thenReturn(Arrays.asList(transaction1, transaction2));
        lenient().when(transactionsRepository.findByIataFlight("AA456")).thenReturn(Arrays.asList(transaction2, transaction3));
 
    }

    @Test
    @DisplayName("Test to find all transactions by user")
    void whenFindByUser_thenReturnTransactionList() {
        Transactions transaction1 = new Transactions();
        transaction1.setUserEmail("FirstUser");
        Transactions transaction2 = new Transactions();
        transaction2.setUserEmail("FirstUser");

        assertThat(transactionsServiceImpl.getTransactionsByUser("FirstUser"))
            .hasSize(2)
            .extracting(Transactions::getUserEmail)
            .contains(transaction1.getUserEmail(), transaction2.getUserEmail());
    }

    @Test
    @DisplayName("Test to find all transactions by user with invalid user")
    void whenFindByInvalidUser_thenReturnEmptyList() {
        Transactions transaction1 = new Transactions();
        transaction1.setUserEmail("FirstUser");
        Transactions transaction2 = new Transactions();
        transaction2.setUserEmail("FirstUser");

        assertThat(transactionsServiceImpl.getTransactionsByUser("InvalidUser")).isEmpty();
    }


    @Test
    @DisplayName("Test to find all transactions by flight")
    void whenFindByFlight_thenReturnTransactionList() {
        Transactions transaction2 = new Transactions();
        transaction2.setIataFlight("AA456");
        Transactions transaction3 = new Transactions();
        transaction3.setIataFlight("AA456");

        assertThat(transactionsServiceImpl.getTransactionsByFlight("AA456"))
            .hasSize(2)
            .extracting(Transactions::getIataFlight)
            .contains(transaction2.getIataFlight(), transaction3.getIataFlight());
    }

    @Test
    @DisplayName("Test to find all transactions by flight with invalid flight")
    void whenFindByInvalidFlight_thenReturnEmptyList() {
        Transactions transaction2 = new Transactions();
        transaction2.setIataFlight("AA456");
        Transactions transaction3 = new Transactions();
        transaction3.setIataFlight("AA456");

        assertThat(transactionsServiceImpl.getTransactionsByFlight("InvalidFlight")).isEmpty();
    }

    @Test
    @DisplayName("Test to create a transaction")
    void whenCreateTransaction_thenReturnTransaction() {
        Transactions transaction = new Transactions();
        transaction.setUserEmail("FirstUser");
        transaction.setIataFlight("AA123");
        transaction.setStatus(TransactionStatus.PAYED);

        transactionsServiceImpl.createTransaction(transaction);

        verify(transactionsRepository, times(1)).save(transaction);
    }


}
