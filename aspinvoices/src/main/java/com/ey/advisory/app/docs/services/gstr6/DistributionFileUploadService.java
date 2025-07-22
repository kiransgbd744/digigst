package com.ey.advisory.app.docs.services.gstr6;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * 
 * @author Balakrishna.S
 *
 */

public interface DistributionFileUploadService {

	ResponseEntity<String> fileUploadsAnnexure1(MultipartFile[] files,
            String fileType, String foldername,
            String userName, String uniqueName, 
            String dataType);

	
}
