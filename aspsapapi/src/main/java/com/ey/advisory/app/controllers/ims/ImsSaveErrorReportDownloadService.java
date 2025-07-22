package com.ey.advisory.app.controllers.ims;

import java.sql.Clob;
import java.util.List;

import com.aspose.cells.Workbook;

public interface ImsSaveErrorReportDownloadService {
	
	//public Workbook ImsSaveErrorReportDownload(List<ImsErrorReportResponseDownloadDto> imsErrorReportDownload);
	public Workbook convertPayload(Clob clobResponsePayloadResp,
			List<ImsErrorReportResponseDownloadDto> errorReports,
			ImsErrorReportResponseDownloadDto dto);

}
