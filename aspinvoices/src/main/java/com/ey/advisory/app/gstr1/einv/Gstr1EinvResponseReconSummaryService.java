package com.ey.advisory.app.gstr1.einv;

import java.util.List;

import com.aspose.cells.Workbook;

public interface Gstr1EinvResponseReconSummaryService {
	
	List<ResponseSummaryDto> getResponseSummData(List<String> gstins,
			String taxPeriod);

	Workbook getResponseSummReport(List<String> gstins, String taxPeriod,
			Long entityId);
}
