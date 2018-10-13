package com.csye6225.demo.controllers;


import com.csye6225.demo.model.Transaction;
import com.csye6225.demo.model.User;
import com.csye6225.demo.repository.TransactionRepository;
import com.csye6225.demo.repository.UserRepository;
import com.csye6225.demo.service.TransactionService;
import com.csye6225.demo.service.UserService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.csye6225.demo.model.ReceiptUpload;
import com.csye6225.demo.repository.ReceiptUploadRepository;
import com.csye6225.demo.service.S3ServicesImpl;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Profile("s3developement")
@RestController
public class TransactionController {

	@Autowired
	TransactionRepository transactionRepository;

	@Autowired
	TransactionService transactionService;

	@Autowired
	ReceiptUploadRepository receiptUploadRepository;

	@Autowired
	private AmazonS3 s3Client;
	
	private String bucketName = "mansicsye6225";

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
	    public String getTransactionByUserId(HttpServletRequest request, HttpServletResponse response) {



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


	    	//JsonArray obj = new JsonArray();
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
	    
	    
	    /*
	    Get receipts associated with a transaction
	     */

	    @GetMapping("/user/transaction/{id}/attachments")
	    public String getTransactionReceipt(@PathVariable(value = "id") String transactionId, HttpServletRequest request, HttpServletResponse response) {


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
	        return jo.toString();

	    }


	    /*
	    Add receipts to a transaction
	     */

	    @PostMapping("/user/transaction/{id}/attachments")
	    public String attachNewReceiptToTransaction(@RequestParam(value = "receipts") MultipartFile[] receipts, @PathVariable(value = "id") String transactionId, HttpServletRequest request, HttpServletResponse response) {

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



	                                    }

	                                    if (receipts.length >1) {
	                                        jo.addProperty("message", "Receipts added successfully");
	                                    } else {
	                                        jo.addProperty("message", "Receipt added successfully");
	                                    }

	                                    response.setStatus(HttpServletResponse.SC_CREATED);
	                                } catch (Exception e) {
	                                    System.out.println(e);
	                                }

	                            } else System.out.println("******************Receipt empty***************");


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
	        return jo.toString();





	    }


	    /*
	    Delete a receipt associated to a transaction
	     */

	@DeleteMapping("/user/transaction/{id}/attachments/{idAttachments}")
	public String deleteReceiptById(@PathVariable(value = "id") String transactionId, @PathVariable(value = "idAttachments") String idAttachment, HttpServletRequest request, HttpServletResponse response) throws IOException {




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

	                if(transaction.getUser().getId() == userExists.getId() ) {
	                    if(transaction != null) {

	                        List<ReceiptUpload> receipts = transaction.getReceipts();

	                        ReceiptUpload deleteReceiptUpload = new ReceiptUpload();

	                        for(ReceiptUpload ru: receipts) {

	                            if(ru.getReceiptId().equals(idAttachment))
	                                deleteReceiptUpload = ru;

	                        }

	                        if(deleteReceiptUpload != null) {

	                            File deleteReceipt = new File(deleteReceiptUpload.getReceiptPath());
	                            deleteReceipt.delete();

	                            ReceiptUpload deleteReceiptUploadDb = receiptUploadRepository.findOne(deleteReceiptUpload.getReceiptId());

	                            receiptUploadRepository.delete(deleteReceiptUploadDb);

	                            jo.addProperty("message", "Receipt deleted successfully");
	                            response.setStatus(HttpServletResponse.SC_NO_CONTENT);


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
	    return jo.toString();




	}   
	    
	
	
	/*
	Update Receipt attached to a transaction
	 */

	@PutMapping("/user/transaction/{id}/attachments/{idattachments}")
    public String UpdateReceipt(@RequestParam(value = "receipts") MultipartFile[] receipts, @PathVariable(value = "id") String transactionId, @PathVariable(value = "idAttachments") String idAttachment, HttpServletRequest request, HttpServletResponse response) {


		//byte[] bytes = Base64.decodeBase64(auth.split(" ")[1]);
		//String uNamePwd[] = new String(bytes).split(":");
		
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

	                if(transaction.getUser().getId() == userExists.getId() ) {
	                    if(transaction != null) {

		
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
