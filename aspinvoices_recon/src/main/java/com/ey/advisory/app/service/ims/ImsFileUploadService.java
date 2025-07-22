package com.ey.advisory.app.service.ims;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * 
 * @author Ravindra V S
 *
 */
public interface ImsFileUploadService {
	public ResponseEntity<String> uploadDocuments(MultipartFile[] file, String folderName, String uniqueName,
			String userName, String fileType, String dataType) throws Exception;

}
