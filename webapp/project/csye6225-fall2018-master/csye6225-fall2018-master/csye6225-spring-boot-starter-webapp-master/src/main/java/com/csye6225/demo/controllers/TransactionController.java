package com.csye6225.demo.controllers;


import com.csye6225.demo.model.Transaction;
import com.csye6225.demo.model.User;
import com.csye6225.demo.repository.TransactionRepository;
import com.csye6225.demo.service.TransactionService;
import com.csye6225.demo.service.UserService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

@RestController
public class TransactionController {

	@Autowired
	TransactionRepository transactionRepository;

	@Autowired
	TransactionService transactionService;



	    @Autowired
	    private UserService userService;

	    /*
	    Create Transaction 
	     */

	    @RequestMapping(value="/user/transaction", method= RequestMethod.POST, produces= "application/json")
	    public String createTransaction(@Valid @RequestBody Transaction transaction, HttpServletRequest request,/* @RequestParam("files") MultipartFile[] files,*/ HttpServletResponse response) {

	    	JsonObject jo = new JsonObject();
	        String header = request.getHeader("Authorization");
	        if (header != null && header.contains("Basic")) {
	            String[] credentialValues= decode(header);

	            User userExists = userService.findByEmail(credentialValues[0]);
	            if (userExists != null) {
	                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	                if (encoder.matches(credentialValues[1], userExists.getPassword()) || credentialValues[1].equals(userExists.getPassword()))
	                 {
	                	if (transaction.getDescription().length() > 30) {
	                            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	                            jo.addProperty("message", "description max length exceeded");
	                        } 
	                	else {
	                			transaction.setUser(userExists);
	                        	Transaction newTransaction = transactionRepository.save(transaction);
	                            userExists.getTransactions().add(newTransaction);
	                            

	                            if (newTransaction != null) {
	                            	jo.addProperty("message", "Transaction created sucessfully with transaction id:" + transaction.getTransactionId());
	                                
	                            	jo.addProperty("id",newTransaction.getTransactionId());
	                            	jo.addProperty("description", newTransaction.getDescription());
	                            	jo.addProperty("merchant", newTransaction.getMerchant());
	                            	jo.addProperty("amount", newTransaction.getAmount());
	                            	jo.addProperty("date", newTransaction.getDate());
	                            	jo.addProperty("category", newTransaction.getCategory());
	                            	
	                            	response.setStatus(HttpServletResponse.SC_CREATED);
	                            } else
	                            	jo.addProperty("404", "Not found");
	                        }

	                }
	                else
	                	jo.addProperty("message", "Incorrect credentials");
	            } else {
	            	jo.addProperty("message", "Incorrect credentials");
	            }
	        } else {
	        	jo.addProperty("message", "You are not logged in !!");
	        }
	        return jo.toString();


	    }




	/*
	Delete Transaction by Transaction id

	 */

	    @DeleteMapping("/user/transaction/{id}")
	    public String deleteTransaction(@PathVariable(value = "id") String transactionId, HttpServletRequest request, HttpServletResponse response) throws IOException {


	        JsonObject jo = new JsonObject();
	        String header = request.getHeader("Authorization");
	        if (header != null && header.contains("Basic")) {
	            String[] credentialValues= decode(header);

	            User userExists = userService.findByEmail(credentialValues[0]);
	            if (userExists != null) {
	                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	                if (encoder.matches(credentialValues[1], userExists.getPassword()) || credentialValues[1].equals(userExists.getPassword()))
	                {
	                	Transaction transaction = transactionRepository.findOne(transactionId);

	                    if(transaction.getUser().getId() == userExists.getId()) {
	                        if(transaction != null) {
	                        	
	                        	jo.addProperty("message", "Transaction deleted successfully");
	                            transactionRepository.delete(transaction);
	                            jo.addProperty("message", "Transaction deleted successfully");
	                            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
	                            jo.addProperty("message", "Transaction deleted successfully");


	                        } else {
	                        	jo.addProperty("message", "No Transaction found with the given id");
	                        }
	                    } else {
	                    	jo.addProperty("message","unauthorized user accessing the Transaction");
	                    }



	                }
	                else
	                	jo.addProperty("message", "Incorrect credentials");
	            } else {
	            	jo.addProperty("message", "Incorrect credentials");
	            }
	        } else {
	        	jo.addProperty("message", "You are not logged in !!");
	        }
	        return jo.toString();




	    }
	    
	    /*
		Get Transactions
		 */
	    
	    @GetMapping("/user/transactions")
	    public String getTaskByUserId(HttpServletRequest request, HttpServletResponse response) {



	        //JsonObject jsonObject = new JsonObject();
	    	JsonArray obj = new JsonArray();
	    	//JsonObject objects[] = new JsonObject[10];
	    	JsonObject jo = new JsonObject();
	        String header = request.getHeader("Authorization");
	        if (header != null && header.contains("Basic")) {
	            String[] credentialValues= decode(header);

	            User userExists = userService.findByEmail(credentialValues[0]);
	            if (userExists != null) {
	                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	                if (encoder.matches(credentialValues[1], userExists.getPassword()) || credentialValues[1].equals(userExists.getPassword()))
	                {
	                    List<Transaction> transactions = transactionRepository.findTransactionsByUserId(userExists.getId());
	                    //List<Transaction> transactionList = new ArrayList<>();
	                    for(int i = 0; i < transactions.size(); i++) {	                    	
	                    	jo.addProperty("id", transactions.get(i).getTransactionId());
	                    	jo.addProperty("description", transactions.get(i).getDescription());
	                    	jo.addProperty("merchant", transactions.get(i).getMerchant());
	                    	jo.addProperty("amount", transactions.get(i).getAmount());
	                    	jo.addProperty("date", transactions.get(i).getDate());
	                    	jo.addProperty("category", transactions.get(i).getCategory());
	                    	//obj.add(jsonObject);
	                    	//transactionList.add(jsonObject);
                        	
	                        	
	                        	response.setStatus(HttpServletResponse.SC_OK);
	                    	
	                    	//System.out.println(transactions.get(i).getDescription());
	                    }
	                    
	                    

	                }
	                else
	                	jo.addProperty("message", "Incorrect credentials");
	            } else {
	            	jo.addProperty("message", "Incorrect credentials");
	            }
	        } else {
	        	jo.addProperty("message", "You are not logged in !!");
	        }
	        return jo.toString();




	    }



	/*
	Update Transaction description
	 */

	    @PutMapping("/user/transaction/{id}")
	    public String getTransactionByUserId(@RequestBody Transaction transaction,@PathVariable(value = "id") String transactionId, HttpServletRequest request, HttpServletResponse response) {


	    	JsonArray obj = new JsonArray();
	        JsonObject jo = new JsonObject();
	        String header = request.getHeader("Authorization");
	        if (header != null && header.contains("Basic")) {
	            String[] credentialValues= decode(header);

	            User userExists = userService.findByEmail(credentialValues[0]);
	            if (userExists != null) {
	                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	                if (encoder.matches(credentialValues[1], userExists.getPassword()) || credentialValues[1].equals(userExists.getPassword()))
	                {
	                	Transaction resTransaction = transactionRepository.findOne(transactionId);

	                    if(resTransaction.getUser().getId() == userExists.getId()) {
	                        if(resTransaction != null) {
	                            

	                            resTransaction.setDescription(transaction.getDescription());
	                            resTransaction.setMerchant(transaction.getMerchant());
	                            resTransaction.setAmount(transaction.getAmount());
	                            resTransaction.setDate(transaction.getDate());
	                            resTransaction.setCategory(transaction.getCategory());
	                            transactionRepository.save(resTransaction);
	                            
	                            
	                            //List<Transaction> transactions = transactionRepository.findTransactionsByUserId(userExists.getId());
	    	                    //for(int i = 0; i < transactions.size(); i++) {	
	                            jo.addProperty("message", "Transaction updated successfully");
	                            jo.addProperty("id",resTransaction.getTransactionId());
	                            jo.addProperty("description", resTransaction.getDescription());
	                            jo.addProperty("merchant", resTransaction.getMerchant());
	                            jo.addProperty("amount", resTransaction.getAmount());
	                            	jo.addProperty("date", resTransaction.getDate());
	                            	jo.addProperty("category", resTransaction.getCategory());
	    	                    	//obj.add(jsonObject);      
	                            	
	    	                    	response.setStatus(HttpServletResponse.SC_OK);
	                            
	                        } else {
	                        	jo.addProperty("message", "No Transaction found with the given id");
	                        }
	                    } else {
	                    	jo.addProperty("message","unauthorized user accessing the Transaction");
	                    }

	                }
	                else
	                	jo.addProperty("message", "Incorrect credentials");
	            } else {
	            	jo.addProperty("message", "Incorrect credentials");
	            }
	        } else {
	        	jo.addProperty("message", "You are not logged in !!");
	        }
	        return jo.toString();

 }

	    	public String[] decode(String header){
	        assert header.substring(0, 6).equals("Basic");
	        String basicAuthEncoded = header.substring(6);
	        String basicAuthAsString = new String(Base64.getDecoder().decode(basicAuthEncoded.getBytes()));
	        final String[] credentialValues = basicAuthAsString.split(":", 2);
	        return  credentialValues;
	    }
}
