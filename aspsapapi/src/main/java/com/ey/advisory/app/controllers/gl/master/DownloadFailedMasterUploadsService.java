/**
 * @author kiran s
 
 
 */
package com.ey.advisory.app.controllers.gl.master;

import java.util.List;

import com.aspose.cells.Workbook;

public interface DownloadFailedMasterUploadsService {
	
	Workbook generateFailedMasterUploadWorkbook(
			String fileId,String FileType);

}
