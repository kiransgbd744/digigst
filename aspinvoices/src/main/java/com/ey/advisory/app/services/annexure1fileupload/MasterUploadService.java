package com.ey.advisory.app.services.annexure1fileupload;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
/**
 * 
 * @author Mahesh.Golla
 *
 */
public interface MasterUploadService {

	ResponseEntity<String> fileUploadsCustomer(MultipartFile[] files,
			                               String fileType, String foldername,
			                               String userName, String uniqueName, 
			                               String dataType, Long entityId);

}
