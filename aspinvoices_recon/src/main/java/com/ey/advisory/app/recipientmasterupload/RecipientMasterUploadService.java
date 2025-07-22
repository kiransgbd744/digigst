package com.ey.advisory.app.recipientmasterupload;

import com.aspose.cells.Workbook;

/**
 * @author Hema G M
 *
 */
public interface RecipientMasterUploadService {

	Workbook getRecipientMasterErrorReport(Long fileId, String typeOfFlag);
	
	
}
