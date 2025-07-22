package com.ey.advisory.app.gstr1.einv;

import java.util.List;

import com.aspose.cells.Workbook;

public interface Gstr1EinvPreReconSummaryService {

	List<PreReconSummaryDto> getPreReconSummData(List<String> gstins,
			String taxPeriod);

	Workbook getPreReconSummReport(List<String> gstins, String taxPeriod,
			Long entityId);
}
