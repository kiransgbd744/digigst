package com.ey.advisory.app.gstr2bpr.reconresponse.upload;

/**
 * @author Jithendra.B
 *
 */
public interface GSTR2bprReconRespUploadService {
	
	public void validateResponse(Long fileId, String fileName,
			String folderName,Long entityId);
}
