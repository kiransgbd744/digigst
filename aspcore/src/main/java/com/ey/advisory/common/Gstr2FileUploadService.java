
package com.ey.advisory.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;



/**
 * 
 * @author Anand3.M
 *
 */
/*
 * This interface represent the Service for uploading the files and reading data
 * from file and pushing data to Document Service
 */
public interface Gstr2FileUploadService {
	public ResponseEntity<String> uploadDocuments(MultipartFile[] file,
			String folderName, String uniqueName,String userName) throws Exception;

}


