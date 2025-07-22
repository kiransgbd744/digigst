package com.ey.advisory.app.recipientmasterupload;


/**
 * @author Hema G M
 *
 */
public interface RecipientMasterUploadAsyncService {

	public void validateDataFileUpload(Long fileId, String fileName,
			String folderName);
}
