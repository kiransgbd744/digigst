package com.ey.advisory.app.service.ims;

/**
 * @author vishal.verma
 *
 */
public interface ImsRespUploadService {
	
	public void validateResponse(Long fileId, String fileName,
			String folderName);
}
