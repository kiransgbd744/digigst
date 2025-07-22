package com.ey.advisory.app.asprecon.gstr2.reconresponse.upload;

/**
 * @author Jithendra.B
 *
 */
public interface GSTR2AAutoReconRespUploadService {

	public void validateResponse(Long fileId, String fileName,
			String folderName);
}
