package com.ey.advisory.admin.services.onboarding.gstinfileupload;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * 
 * @author Sasidhar Reddy
 *
 */
/*
 * This interface represent the Service for uploading the files and reading data
 * from file and pushing data to Document Service
 */
public interface GstinFileUploadService {
	public ResponseEntity<String> uploadDocuments(MultipartFile[] file, String folderName, String uniqueName)
			throws Exception;

}
