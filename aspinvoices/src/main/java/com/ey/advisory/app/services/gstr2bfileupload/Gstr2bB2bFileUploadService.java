package com.ey.advisory.app.services.gstr2bfileupload;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface Gstr2bB2bFileUploadService {

	public ResponseEntity<String> uploadDocuments(MultipartFile[] files,
			String folderName, String uniqueName, String userName,
			String fileType, String dataType) throws Exception;

}
