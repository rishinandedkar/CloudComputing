package com.csye6225.demo.repository;

import com.csye6225.demo.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,String>{

	 Transaction findByUserId(Long userId);

     List<Transaction> findTransactionsByUserId(Long userId);
}
