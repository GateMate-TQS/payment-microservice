package gatemate.repositories;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gatemate.entities.Transactions;

@Repository
public interface TransactionsRepository extends JpaRepository<Transactions, Long>{

    List<Transactions> findByUserEmail(String userEmail);

    List<Transactions> findByIataFlight(String iataFlight);

} 