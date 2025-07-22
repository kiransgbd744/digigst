package com.ey.advisory.app.services.ims;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.service.ims.ImsEntitySummaryReqDto;
import com.ey.advisory.core.search.PageRequest;

public interface ImsRecordsTableReportsService {
	
	public Workbook findImsRecordsGetcall(ImsEntitySummaryReqDto searchParams,
			PageRequest pageReq);
	
	public Workbook findImsSummaryEntityLevel(ImsEntitySummaryReqDto searchParams,
			PageRequest pageReq);

}
