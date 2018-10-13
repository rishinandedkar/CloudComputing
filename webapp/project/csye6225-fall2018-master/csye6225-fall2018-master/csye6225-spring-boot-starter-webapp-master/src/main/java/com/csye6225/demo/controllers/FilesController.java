package com.csye6225.demo.controllers;

import java.io.File;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.csye6225.demo.model.FileInfo;

@Profile("application")
@RestController
public class FilesController {

	private final String FILE_UPLOAD_LOCATION = "C:\\upload";
	
	@RequestMapping(value="/upload", method =RequestMethod.POST, consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<FileInfo> uploadFile(@RequestParam("file") MultipartFile file){
		
		if(!file.isEmpty()) {
			try {
				String fileName = file.getOriginalFilename();
				File f =new File(FILE_UPLOAD_LOCATION + File.separator + fileName);
				file.transferTo(f);
				
				FileInfo fileInfo = new FileInfo();
				fileInfo.setFileName(fileName);
				fileInfo.setFileSize(file.getSize());
				
				HttpHeaders headers = new HttpHeaders();
				headers.add("File Upload successfull:", fileName);
				
				return new ResponseEntity<FileInfo>(fileInfo, headers, HttpStatus.OK);
			}catch(Exception ex) {
				return new ResponseEntity<FileInfo>(HttpStatus.BAD_REQUEST); 
			}
		}else {
			return new ResponseEntity<FileInfo>(HttpStatus.BAD_REQUEST);
		}
	}
}
