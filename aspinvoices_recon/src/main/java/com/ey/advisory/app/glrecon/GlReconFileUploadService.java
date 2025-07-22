package com.ey.advisory.app.glrecon;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * 
 * @author Sakshi.jain
 *
 */
/*
 * This interface represent the Service for uploading the files and reading data
 * from file and pushing data to Document Service
 */
public interface GlReconFileUploadService {
	public ResponseEntity<String> uploadDocuments(MultipartFile[] file,
		String folderName, String uniqueName,String userName, String ruleId) throws Exception;

}
