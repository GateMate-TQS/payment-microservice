package gatemate.services;

import java.util.List;

import gatemate.entities.Transactions;

public interface TransactionsService {
    public void createTransaction(Transactions transaction);

    public List<Transactions> getTransactionsByUser(String userEmail);    

    public List<Transactions> getTransactionsByFlight(String iataFlight);
}
