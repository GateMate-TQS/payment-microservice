package gatemate.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import gatemate.entities.Transactions;
import gatemate.entities.TransactionStatus;
import gatemate.repositories.TransactionsRepository;

@Service
public class TransactionsServiceImpl implements TransactionsService {

    private final TransactionsRepository transactionsRepository;

    public TransactionsServiceImpl(TransactionsRepository transactionsRepository) {
        this.transactionsRepository = transactionsRepository;
    }

    @Override
    public void createTransaction(Transactions transaction) {
        transactionsRepository.save(transaction);
    }

    @Override
    public List<Transactions> getTransactionsByUser(String userEmail) {
        return transactionsRepository.findByUserEmail(userEmail);
    }

    @Override
    public void updateTransaction(Long id) {
        Optional<Transactions> transactionOpt = transactionsRepository.findById(id);
        if (transactionOpt.isPresent()) {
            Transactions transaction = transactionOpt.get();
            transaction.setStatus(TransactionStatus.CHECKEDIN);
            transactionsRepository.save(transaction);
        } else {
            throw new TransactionNotFoundException("Transaction not found for id: " + id);
        }
    }

    @Override
    public Transactions getTransaction(Long id) {
        return transactionsRepository.findById(id).orElse(null);
    }
}
