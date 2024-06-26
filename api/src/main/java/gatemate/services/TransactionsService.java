package gatemate.services;

import java.util.List;

import gatemate.entities.Transactions;

public interface TransactionsService {
    public void createTransaction(Transactions transaction);

    public List<Transactions> getTransactionsByUser(String userEmail);

    public void updateTransaction(Long id);

    public Transactions getTransaction(Long id);
}
