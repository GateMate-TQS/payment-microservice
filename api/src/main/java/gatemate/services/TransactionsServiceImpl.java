package gatemate.services;

import java.util.List;

import org.springframework.stereotype.Service;

import gatemate.entities.Transactions;
import gatemate.repositories.TransactionsRepository;

@Service
public class TransactionsServiceImpl implements TransactionsService{

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
    public List<Transactions> getTransactionsByFlight(String iataFlight) {
        return transactionsRepository.findByIataFlight(iataFlight);
    }
    
}
