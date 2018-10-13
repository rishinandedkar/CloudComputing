package com.csye6225.demo.service;

import java.io.File;
import java.io.IOException;

import com.amazonaws.services.s3.model.DeleteObjectRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

@Service
public class S3ServicesImpl implements S3Services{

	private Logger logger = LoggerFactory.getLogger(S3ServicesImpl.class);

    @Autowired
    private AmazonS3 s3client;


    private String bucketName = "mansicsye6225";


    @Override
    public void uploadReceipt(String keyName, File receipt) {

        try {
            s3client.putObject(bucketName, keyName, receipt);
            logger.info("Receipt uploaded to S3 bucket successfully");

        } catch (AmazonServiceException ase) {
            logger.info("Error Message:    " + ase.getMessage());
        } catch (AmazonClientException ace) {
            logger.info("Error Message: " + ace.getMessage());
        }
    }

    @Override
    public void deleteReceiptFromS3(String key) {
        try{
            s3client.deleteObject(new DeleteObjectRequest("mansicsye6225", key));
        }
        catch(Exception ex){
            System.out.println("An error occurred deleting receipt from S3");
        }
    }
	
}
