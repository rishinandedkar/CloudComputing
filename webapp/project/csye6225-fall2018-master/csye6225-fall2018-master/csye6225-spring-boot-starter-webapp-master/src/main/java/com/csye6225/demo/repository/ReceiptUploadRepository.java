package com.csye6225.demo.repository;

import com.csye6225.demo.model.ReceiptUpload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface ReceiptUploadRepository extends JpaRepository<ReceiptUpload,String>{

}
