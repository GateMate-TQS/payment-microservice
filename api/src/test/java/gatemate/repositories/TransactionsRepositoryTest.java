package gatemate.repositories;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import gatemate.entities.Transactions;

@DataJpaTest
class TransactionsRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TransactionsRepository transactionsRepository;

    @Test
    @DisplayName("Find transaction by id")
    void whenFindTransactionByExistingId_thenReturnTransaction() {
        Transactions transaction = new Transactions();
        entityManager.persistAndFlush(transaction);

        Transactions transactiondb = transactionsRepository.findById(transaction.getId()).orElse(null);
        assertThat(transactiondb).isNotNull();
        assertThat(transactiondb.getId()).isEqualTo(transaction.getId());
    }

    @Test
    @DisplayName("Find transaction by invalid id")
    void whenInvalidId_thenReturnNull() {
        Transactions transactiondb = transactionsRepository.findById(-1L).orElse(null);
        assertThat(transactiondb).isNull();
    }

    @Test
    @DisplayName("Find all transactions")
    void givenSetOfTransactions_whenFindAll_thenReturnSet() {
        Transactions transaction1 = new Transactions();
        transaction1.setUserEmail("FirstUser");
        Transactions transaction2 = new Transactions();
        transaction2.setUserEmail("SecondUser");

        entityManager.persistAndFlush(transaction1);
        entityManager.persistAndFlush(transaction2);

        assertThat(transactionsRepository.findAll())
                .hasSize(2)
                .extracting(Transactions::getUserEmail)
                .contains(transaction1.getUserEmail(), transaction2.getUserEmail());
    }

    @Test
    @DisplayName("Find transactions by user")
    void givenSetOfTransactions_whenFindByUser_thenReturnSet() {
        Transactions transaction1 = new Transactions();
        transaction1.setUserEmail("FirstUser");
        Transactions transaction2 = new Transactions();
        transaction2.setUserEmail("SecondUser");

        entityManager.persistAndFlush(transaction1);
        entityManager.persistAndFlush(transaction2);

        assertThat(transactionsRepository.findByUserEmail("FirstUser"))
                .hasSize(1)
                .extracting(Transactions::getUserEmail)
                .contains(transaction1.getUserEmail());
    }

    @Test
    @DisplayName("Find transactions by invalid user")
    void whenFindByInvalidUser_thenReturnEmptySet() {
        Transactions transaction1 = new Transactions();
        transaction1.setUserEmail("FirstUser");
        Transactions transaction2 = new Transactions();
        transaction2.setUserEmail("SecondUser");

        entityManager.persistAndFlush(transaction1);
        entityManager.persistAndFlush(transaction2);

        assertThat(transactionsRepository.findByUserEmail("InvalidUser")).isEmpty();
    }

    @Test
    @DisplayName("Find transactions by flight")
    void givenSetOfTransactions_whenFindByFlight_thenReturnSet() {
        Transactions transaction1 = new Transactions();
        transaction1.setIataFlight("AA123");
        Transactions transaction2 = new Transactions();
        transaction2.setIataFlight("AA456");

        entityManager.persistAndFlush(transaction1);
        entityManager.persistAndFlush(transaction2);

        assertThat(transactionsRepository.findByIataFlight("AA123"))
                .hasSize(1)
                .extracting(Transactions::getIataFlight)
                .contains(transaction1.getIataFlight());
    }

    @Test
    @DisplayName("Find transactions by invalid flight")
    void whenFindByInvalidFlight_thenReturnEmptySet() {
        Transactions transaction1 = new Transactions();
        transaction1.setIataFlight("AA123");
        Transactions transaction2 = new Transactions();
        transaction2.setIataFlight("AA456");

        entityManager.persistAndFlush(transaction1);
        entityManager.persistAndFlush(transaction2);

        assertThat(transactionsRepository.findByIataFlight("InvalidFlight")).isEmpty();
    }

    @Test
    @DisplayName("Save transaction")
    void whenSaveTransaction_thenTransactionIsSaved() {
        Transactions transaction = new Transactions();
        transaction.setUserEmail("FirstUser");

        transactionsRepository.save(transaction);

        assertThat(transactionsRepository.findAll())
                .hasSize(1)
                .extracting(Transactions::getUserEmail)
                .contains(transaction.getUserEmail());
    }

    @Test
    @DisplayName("Update transaction")
    void whenUpdateTransaction_thenTransactionIsUpdated() {
        Transactions transaction = new Transactions();
        transaction.setUserEmail("FirstUser");
        entityManager.persistAndFlush(transaction);

        transaction.setUserEmail("UpdatedUser");
        transactionsRepository.save(transaction);

        Transactions updatedTransaction = transactionsRepository.findById(transaction.getId()).orElse(null);
        assertThat(updatedTransaction).isNotNull();
        assertThat(updatedTransaction.getUserEmail()).isEqualTo("UpdatedUser");
    }

    @Test
    @DisplayName("Delete transaction")
    void whenDeleteTransaction_thenTransactionIsDeleted() {
        Transactions transaction = new Transactions();
        entityManager.persistAndFlush(transaction);

        transactionsRepository.delete(transaction);
        Transactions deletedTransaction = transactionsRepository.findById(transaction.getId()).orElse(null);
        assertThat(deletedTransaction).isNull();
    }
}
