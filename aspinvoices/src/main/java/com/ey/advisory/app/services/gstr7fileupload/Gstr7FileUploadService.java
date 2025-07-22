package com.ey.advisory.app.services.gstr7fileupload;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * 
 * @author Anand3.M
 *
 */
public interface Gstr7FileUploadService {
	public ResponseEntity<String> uploadDocuments(MultipartFile[] file,
			String folderName, String uniqueName, String userName,
			String fileType, String dataType, String source) throws Exception;

}
