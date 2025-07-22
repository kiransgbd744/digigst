package com.ey.advisory.app.gstr2.recon.summary;

import java.util.List;

import com.aspose.cells.Workbook;

public interface Gstr2ReconSummaryDownloadService {

	public Workbook getReconSummaryDownload(Long configId,
			List<String> gstinList, String toTaxPeriod, String fromTaxPeriod,
			String reconType, Long entityId, String toTaxPeriod_A2, String fromTaxPeriod_A2, String criteria);

}
