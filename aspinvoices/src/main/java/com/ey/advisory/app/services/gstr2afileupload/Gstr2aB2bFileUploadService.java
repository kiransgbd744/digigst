package com.ey.advisory.app.services.gstr2afileupload;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * 
 * @author Anand3.M
 *
 */
public interface Gstr2aB2bFileUploadService {
	public ResponseEntity<String> uploadDocuments(MultipartFile[] file, String folderName, String uniqueName,
			String userName, String fileType, String dataType) throws Exception;

}
