package com.csye6225.demo.service;

import com.csye6225.demo.model.Transaction;
import java.util.List;

public interface TransactionService {

	 public Transaction findTransactionByUserId(Long userId);
	 
	 public List<Transaction> findTransactionsByUserId(Long userId);
}
