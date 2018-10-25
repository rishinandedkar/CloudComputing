package com.csye6225.demo.service;

import java.io.File;

public interface S3Services {
	
	public void uploadReceipt(String keyName, File receipt);
    public void deleteReceiptFromS3(String key);
}
