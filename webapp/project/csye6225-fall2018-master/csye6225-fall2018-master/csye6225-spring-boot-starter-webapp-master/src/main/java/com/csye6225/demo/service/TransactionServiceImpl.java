package com.csye6225.demo.service;

import com.csye6225.demo.model.Transaction;
import com.csye6225.demo.repository.TransactionRepository;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImpl implements TransactionService{

	@Autowired
    @Qualifier("transactionRepository")
    private TransactionRepository transactionRepository;

    @Override
    public Transaction findTransactionByUserId(Long userId) {
        return transactionRepository.findByUserId(userId);
    }
    
    public List<Transaction> findTransactionsByUserId(Long userId) {
    	return transactionRepository.findTransactionsByUserId(userId); 
    }
   
}
