package com.csye6225.demo.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.csye6225.demo.model.ReceiptUpload;
import com.csye6225.demo.model.Transaction;
import com.csye6225.demo.model.User;
import com.csye6225.demo.repository.ReceiptUploadRepository;
import com.csye6225.demo.repository.TransactionRepository;
import com.csye6225.demo.service.S3ServicesImpl;
import com.csye6225.demo.service.TransactionService;
import com.csye6225.demo.service.UserService;
import com.google.gson.JsonObject;

@Configuration
@Profile("s3developement")
@RestController
public class TransactionControllerS3 {

	
	
	@Autowired
	TransactionRepository transactionRepository;

	@Autowired
	TransactionService transactionService;

	@Autowired
	ReceiptUploadRepository receiptUploadRepository;

	@Autowired
	private AmazonS3 s3Client;
	
	private String bucketName = System.getProperty("bucket.name");

	@Autowired
	private UserService userService;
	
	@Autowired
    private S3ServicesImpl s3ServiceImpl;
	
	
	/*
	  Create Transaction 
	*/	
	
	 @RequestMapping(value="/user/transaction", method= RequestMethod.POST, produces= "application/json")
	    public String createTransaction(@Valid @RequestBody Transaction transaction, HttpServletRequest request,/* @RequestParam("files") MultipartFile[] files,*/ HttpServletResponse response) {

	    	JsonObject jo = new JsonObject();
	    	try {
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
	    	}catch(Exception ex) {
	    		ex.printStackTrace();
	    	}
	        return jo.toString();
	    }
	 
	 
	 
	 /*
		Delete Transaction by Transaction id
		 */

		    @DeleteMapping("/user/transaction/{id}")
		    public String deleteTransaction(@PathVariable(value = "id") String transactionId, HttpServletRequest request, HttpServletResponse response) throws IOException {


		        JsonObject jo = new JsonObject();
		        try {
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
		        }catch(Exception ex) {
		    		ex.printStackTrace();
		    	}
		        return jo.toString();

		    }
		    
		    /*
			Get Transactions
			 */
		    
		    @GetMapping("/user/transactions")
		    public String getTransactionByUserId(HttpServletRequest request, HttpServletResponse response) {



		    	JsonObject jo = new JsonObject();
		    	try {
			        String header = request.getHeader("Authorization");
			        if (header != null && header.contains("Basic")) {
			            String[] credentialValues= decode(header);
		
			            User userExists = userService.findByEmail(credentialValues[0]);
			            if (userExists != null) {
			                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		
			                if (encoder.matches(credentialValues[1], userExists.getPassword()) || credentialValues[1].equals(userExists.getPassword()))
			                {
			                    List<Transaction> transactions = transactionRepository.findTransactionsByUserId(userExists.getId());
			                    for(int i = 0; i < transactions.size(); i++) {	                    	
			                    	jo.addProperty("id", transactions.get(i).getTransactionId());
			                    	jo.addProperty("description", transactions.get(i).getDescription());
			                    	jo.addProperty("merchant", transactions.get(i).getMerchant());
			                    	jo.addProperty("amount", transactions.get(i).getAmount());
			                    	jo.addProperty("date", transactions.get(i).getDate());
			                    	jo.addProperty("category", transactions.get(i).getCategory());
			                    	
		                        	response.setStatus(HttpServletResponse.SC_OK);
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
		    	}catch(Exception ex) {
		    		ex.printStackTrace();
		    	}
		        return jo.toString();
		    }



		/*
		Update Transaction description
		 */

		    @PutMapping("/user/transaction/{id}")
		    public String getTransactionByUserId(@RequestBody Transaction transaction,@PathVariable(value = "id") String transactionId, HttpServletRequest request, HttpServletResponse response) {


		        JsonObject jo = new JsonObject();
		        try {
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
		        }catch(Exception ex) {
		    		ex.printStackTrace();
		    	}
		        return jo.toString();

		    }
	 
	
	 /*
    Add receipts to a transaction
     */

	 @PostMapping("/user/transaction/{id}/attachments")
	    public String attachNewReceiptToTransaction(@RequestParam(value = "receipts") MultipartFile[] receipts, @PathVariable(value = "id") String transactionId, HttpServletRequest request, HttpServletResponse response) {

	        JsonObject jo = new JsonObject();
	        try {
		        String header = request.getHeader("Authorization");
		        if (header != null && header.contains("Basic")) {
		            String[] credentialValues= decode(header);
	
		            User userExists = userService.findByEmail(credentialValues[0]);
		            if (userExists != null) {
		                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
	
		                if (encoder.matches(credentialValues[1], userExists.getPassword()) || credentialValues[1].equals(userExists.getPassword()))
		                {
		                    Transaction transaction = transactionRepository.findOne(transactionId);
	
		                    if(transaction.getUser().getId() == userExists.getId() ) {
		                        if(transaction != null) {
	
		                            if (receipts.length != 0) {
		                                try {
		                                    //String uploadsDir = "C:\\uploads";
		                                    String uploadsDir = "/uploads/";
		                                    String realPathtoUploads = request.getServletContext().getRealPath(uploadsDir);
		                                    if (!new File(realPathtoUploads).exists()) {
		                                        new File(realPathtoUploads).mkdir();
		                                    }
	
	
	
		                                    for(MultipartFile receipt : receipts) {
		                                        String orgName = receipt.getOriginalFilename();
		                                        if(orgName.endsWith(".png") || orgName.endsWith(".jpeg") || orgName.endsWith(".jpg")) {

		                                        String filePath = realPathtoUploads + orgName;
	
		                                        File dest = new File(filePath);
		                                        receipt.transferTo(dest);
		                                        String key = Instant.now().getEpochSecond() + "_" + dest.getName();
		                                        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, key);
		                                        generatePresignedUrlRequest.setMethod(HttpMethod.GET);
		                                        generatePresignedUrlRequest.setExpiration(DateTime.now().plusDays(4).toDate());
	
		                                        URL signedUrl = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
	
		                                        ReceiptUpload receiptUpload = new ReceiptUpload();
		                                        receiptUpload.setReceiptPath(signedUrl.toString());
		                                        receiptUpload.setTransaction(transaction);
		                                        receiptUploadRepository.save(receiptUpload);
		                                        s3ServiceImpl.uploadReceipt(key,dest);
	
		                                        jo.addProperty("Receipts id is:", receiptUpload.getReceiptId());
		                                        jo.addProperty("message", "Receipts added successfully");
		                                    }
	
//		                                    if (receipts.length >1) {
//		                                        jo.addProperty("message", "Receipts added successfully");
//		                                    } 
		                                    else {
		                                        jo.addProperty("message", "Wrong file uploaded");
		                                    }
	
		                                    response.setStatus(HttpServletResponse.SC_CREATED);
		                                    }
		                                } catch (Exception e) {
		                                    System.out.println(e);
		                                }
	
		                            } else System.out.println("*******Receipt empty******");
	
	
		                        } else {
		                            jo.addProperty("message", "No transaction found with the given transaction id");
		                        }
		                    } else {
		                        jo.addProperty("message","unauthorized user accessing the transaction");
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
	        }catch(Exception ex) {
	    		ex.printStackTrace();
	    	}
	        return jo.toString();
	    }
    
    
    
    @GetMapping("/user/transaction/{id}/attachments")
    public String getTransactionReceipt(@PathVariable(value = "id") String transactionId, HttpServletRequest request, HttpServletResponse response) {


        JsonObject jo = new JsonObject();
        try {
	        String header = request.getHeader("Authorization");
	        if (header != null && header.contains("Basic")) {
	            String[] credentialValues= decode(header);
	
	            User userExists = userService.findByEmail(credentialValues[0]);
	            if (userExists != null) {
	                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
	
	                if (encoder.matches(credentialValues[1], userExists.getPassword()) || credentialValues[1].equals(userExists.getPassword()))
	                {
	                    Transaction transaction = transactionRepository.findOne(transactionId);
	
	                    if(transaction.getUser().getId() == userExists.getId() ) {
	                        if(transaction != null) {
	
	                            List<ReceiptUpload> receipts = transaction.getReceipts();
	
	                            if(receipts.size() != 0) {
	
	                                int counter=1;
	                                for(ReceiptUpload receipt : receipts) {
	                                    File tempReceipt = new File(receipt.getReceiptPath());
	
	                                    jo.addProperty("Receipt "+counter, tempReceipt.getName());
	                                    counter++;
	                                }
	
	                                response.setStatus(HttpServletResponse.SC_OK);
	                            } else {
	                                jo.addProperty("message","No receipts found on this transaction");
	                            }
	
	
	                        } else {
	                            jo.addProperty("message", "No transaction found with the given transaction id");
	                        }
	                    } else {
	                        jo.addProperty("message","unauthorized user accessing the transaction");
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
        }catch(Exception ex) {
    		ex.printStackTrace();
    	}
        return jo.toString();

    }
    
    
    /*
    Delete a receipt associated to a transaction
     */

    @DeleteMapping("/user/transaction/{id}/attachments/{idAttachments}")
	public String deleteReceiptById(@PathVariable(value = "id") String transactionId, @PathVariable(value = "idAttachments") String idAttachment, HttpServletRequest request, HttpServletResponse response) throws IOException {

	    JsonObject jo = new JsonObject();
	    try {
		    String header = request.getHeader("Authorization");
		    if (header != null && header.contains("Basic")) {
		        String[] credentialValues= decode(header);
	
		        User userExists = userService.findByEmail(credentialValues[0]);
		        if (userExists != null) {
		            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
	
		            if (encoder.matches(credentialValues[1], userExists.getPassword()) || credentialValues[1].equals(userExists.getPassword()))
		            {
		                Transaction transaction = transactionRepository.findOne(transactionId);
	
		                if(transaction.getUser().getId() == userExists.getId() ) {
		                    if(transaction != null) {
	
		                        List<ReceiptUpload> receipts = transaction.getReceipts();
	
		                        ReceiptUpload deleteReceiptUpload = null;
	
		                        for(ReceiptUpload ru: receipts) {
	
		                            if(ru.getReceiptId().equals(idAttachment))
		                                deleteReceiptUpload = ru;
	
		                        }
	
		                        if(deleteReceiptUpload != null) {
	
		                            File deleteReceipt = new File(deleteReceiptUpload.getReceiptPath());
		                            deleteReceipt.delete();
	
		                            ReceiptUpload deleteReceiptUploadDb = receiptUploadRepository.findOne(deleteReceiptUpload.getReceiptId());
	
		                            receiptUploadRepository.delete(deleteReceiptUploadDb);
		                            
		                            
		                            String key = getFileKey(deleteReceiptUploadDb.getReceiptPath());
		                            
		                            s3ServiceImpl.deleteReceiptFromS3(key);
	
		                            jo.addProperty("message", "Receipt deleted successfully");
		                            //response.setStatus(HttpServletResponse.SC_NO_CONTENT);
	
	
		                        } else {
		                            jo.addProperty("message", "No receipt found with the given receipt id");
		                        }
	
		                    } else {
		                        jo.addProperty("message", "No transaction found with the given transaction id");
		                    }
		                } else {
		                    jo.addProperty("message","unauthorized user accessing the transaction");
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
	    }catch(Exception ex) {
    		ex.printStackTrace();
    	}
	    return jo.toString();

}

    
  //Update attachment method
	
  	@PutMapping("/user/transaction/{id}/attachments/{idAttachments}")
      public String UpdateReceipt(@RequestParam(value = "receipts") MultipartFile[] receipts, @PathVariable(value = "id") String transactionId, @PathVariable(value = "idAttachments") String idAttachment, HttpServletRequest request, HttpServletResponse response) throws IllegalStateException, IOException {
  	
  		JsonObject jo = new JsonObject();
  		try {
  			deleteReceiptById(transactionId, idAttachment, request, response);
  			
  			attachNewReceiptToTransaction(receipts, transactionId, request, response);
  	
  			jo.addProperty("message", "Receipt updated successfully");
  		}catch(Exception ex) {
      		ex.printStackTrace();
      	}
  		return jo.toString();
  	}


private String getFileKey(String filePath) {
	
	String[] data = filePath.split("/");
	String key = data[data.length - 1].split("\\?")[0];
	return key;
}



	

    
    
    public String[] decode(String header){
        assert header.substring(0, 6).equals("Basic");
        String basicAuthEncoded = header.substring(6);
        String basicAuthAsString = new String(Base64.getDecoder().decode(basicAuthEncoded.getBytes()));
        final String[] credentialValues = basicAuthAsString.split(":", 2);
        return  credentialValues;
    }
    
   /* public String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }*/

	
}
