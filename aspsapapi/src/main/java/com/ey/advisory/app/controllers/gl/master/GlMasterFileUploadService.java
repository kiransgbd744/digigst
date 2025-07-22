package com.ey.advisory.app.controllers.gl.master;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * 
 * @author Kiran s
 *
 */
public interface GlMasterFileUploadService {
	public ResponseEntity<String> uploadDocuments(MultipartFile[] file, String folderName, String uniqueName,
			String userName, String fileType, String dataType,Long entityId) throws Exception;

}
