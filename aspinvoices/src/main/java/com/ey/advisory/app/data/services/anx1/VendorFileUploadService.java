package com.ey.advisory.app.data.services.anx1;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface VendorFileUploadService {

	ResponseEntity<String> vendorProcessingFile(MultipartFile[] files,
			String fileType, String folderName, String userName,
			String uniqueName, String dataType, String groupCode, Long entityId)
			throws IOException;
}
