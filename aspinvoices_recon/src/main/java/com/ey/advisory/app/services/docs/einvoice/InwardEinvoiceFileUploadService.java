package com.ey.advisory.app.services.docs.einvoice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface InwardEinvoiceFileUploadService {

	public ResponseEntity<String> uploadDocuments(MultipartFile[] files,
			String folderName, String uniqueName, String userName,
			String fileType, String dataType) throws Exception;

}
