/**
 * 
 */
package com.ey.advisory.app.data.services.ewb;

import java.util.List;

import com.ey.advisory.app.docs.dto.einvoice.CEWBDownloadReportResponse;

/**
 * @author Laxmi.Salukuti
 *
 */
public interface CEWBDownloadReportService {

	public void generateCsvForProcessedCEWB(String fullPath,
			List<CEWBDownloadReportResponse> response);

	public void generateCsvForErrorCEWB(String fullPath,
			List<CEWBDownloadReportResponse> response);

}
