package com.ey.advisory.app.services.annexure2fileupload;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface ProductWebUploadService {

	ResponseEntity<String> productProcessingFile(MultipartFile[] files,
			String folderName, String uniqueName, String fileType,
			String userName, String dataType,String groupCode,
			Long entityId)  throws IOException;
}
