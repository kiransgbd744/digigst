package com.ey.advisory.app.gstr2jsonupload;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author vishal.verma
 *
 */
public interface Gstr2JsonUploadService {

	public ResponseEntity<String> gstr2JsonFileUpload(MultipartFile[] files, 
			String folderName, String gstin);
	
	//public ResponseEntity<String> readJson(String gstin);
}
