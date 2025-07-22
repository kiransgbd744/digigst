package com.ey.advisory.app.services.gstr1fileupload;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * 
 * @author Mahesh.Golla
 *
 */
/*
 * This interface represent the Service for uploading the files and reading data
 * from file and pushing data to Document Service
 */
public interface SftpFileUploadService {
	public ResponseEntity<String> uploadDocuments(MultipartFile[] file,
		String folderName, String uniqueName,String userName,String fileType,
			                           String dataType) throws Exception;

}
