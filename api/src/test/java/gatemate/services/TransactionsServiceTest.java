package gatemate.services;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Optional;

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

    private Transactions transaction1;
    private Transactions transaction2;
    private Transactions transaction3;

    @BeforeEach
    public void setUp() {
        transaction1 = new Transactions();
        transaction1.setId(1L);
        transaction1.setUserEmail("FirstUser");
        transaction1.setIataFlight("AA123");
        transaction1.setStatus(TransactionStatus.PAYED);

        transaction2 = new Transactions();
        transaction2.setId(2L);
        transaction2.setUserEmail("FirstUser");
        transaction2.setIataFlight("AA456");
        transaction2.setStatus(TransactionStatus.CHECKEDIN);

        transaction3 = new Transactions();
        transaction3.setId(3L);
        transaction3.setUserEmail("SecondUser");
        transaction3.setIataFlight("AA456");
        transaction3.setStatus(TransactionStatus.PAYED);

        lenient().when(transactionsRepository.findByUserEmail("FirstUser"))
                .thenReturn(Arrays.asList(transaction1, transaction2));
        lenient().when(transactionsRepository.findByIataFlight("AA456"))
                .thenReturn(Arrays.asList(transaction2, transaction3));
        lenient().when(transactionsRepository.findById(1L)).thenReturn(Optional.of(transaction1));
        lenient().when(transactionsRepository.findById(2L)).thenReturn(Optional.of(transaction2));
        lenient().when(transactionsRepository.findById(3L)).thenReturn(Optional.of(transaction3));
    }

    @Test
    @DisplayName("Find all transactions by user")
    void whenFindByUser_thenReturnTransactionList() {
        assertThat(transactionsServiceImpl.getTransactionsByUser("FirstUser"))
                .hasSize(2)
                .extracting(Transactions::getUserEmail)
                .contains(transaction1.getUserEmail(), transaction2.getUserEmail());
    }

    @Test
    @DisplayName("Find all transactions by user with invalid user")
    void whenFindByInvalidUser_thenReturnEmptyList() {
        assertThat(transactionsServiceImpl.getTransactionsByUser("InvalidUser")).isEmpty();
    }

    @Test
    @DisplayName("Create a transaction")
    void whenCreateTransaction_thenReturnTransaction() {
        Transactions transaction = new Transactions();
        transaction.setUserEmail("FirstUser");
        transaction.setIataFlight("AA123");
        transaction.setStatus(TransactionStatus.PAYED);

        transactionsServiceImpl.createTransaction(transaction);

        verify(transactionsRepository, times(1)).save(transaction);
    }

    @Test
    @DisplayName("Update a transaction")
    void whenUpdateTransaction_thenStatusShouldBeCheckedIn() {
        transactionsServiceImpl.updateTransaction(1L);

        verify(transactionsRepository, times(1)).save(transaction1);
        assertThat(transaction1.getStatus()).isEqualTo(TransactionStatus.CHECKEDIN);
    }

    @Test
    @DisplayName("Get a transaction by ID")
    void whenGetTransactionById_thenReturnTransaction() {
        Transactions foundTransaction = transactionsServiceImpl.getTransaction(1L);

        assertThat(foundTransaction).isEqualTo(transaction1);
    }

    @Test
    @DisplayName("Get a transaction by invalid ID")
    void whenGetTransactionByInvalidId_thenReturnNull() {
        Transactions foundTransaction = transactionsServiceImpl.getTransaction(-1L);

        assertThat(foundTransaction).isNull();
    }

    @Test
    @DisplayName("Update a transaction with invalid ID")
    void whenUpdateTransactionWithInvalidId_thenThrowException() {
        doThrow(new TransactionNotFoundException("Transaction not found for id: -1")).when(transactionsRepository)
                .findById(-1L);

        Throwable thrown = catchThrowable(() -> {
            transactionsServiceImpl.updateTransaction(-1L);
        });

        assertThat(thrown).isInstanceOf(TransactionNotFoundException.class)
                .hasMessage("Transaction not found for id: -1");
    }
}
