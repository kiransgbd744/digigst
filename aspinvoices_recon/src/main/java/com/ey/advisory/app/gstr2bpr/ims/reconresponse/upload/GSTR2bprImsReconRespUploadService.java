package com.ey.advisory.app.gstr2bpr.ims.reconresponse.upload;

/**
 * @author Jithendra.B
 *
 */
public interface GSTR2bprImsReconRespUploadService {
	
	public void validateResponse(Long fileId, String fileName,
			String folderName,Long entityId);
}
