package com.ey.advisory.app.gstr1.einv;

import java.util.List;

import com.aspose.cells.Workbook;

public interface PostReconSummaryService {

	PostReconSummaryTabDto getReconSummaryDetails(List<String> recipientGstins,
			String taxPeriod);

	Workbook getPostReconReport(List<String> recipientGstins, String taxPeriod,
			String entityId);

}
