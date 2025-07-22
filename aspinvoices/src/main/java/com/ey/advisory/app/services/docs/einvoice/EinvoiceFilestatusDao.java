/**
 * 
 */
package com.ey.advisory.app.services.docs.einvoice;

import java.util.List;

import com.ey.advisory.app.docs.dto.Anx1FileStatusReportsReqDto;
import com.ey.advisory.app.docs.dto.einvoice.DataStatusEinvoiceDto;

/**
 * @author Sujith.Nanga
 *
 * 
 */
public interface EinvoiceFilestatusDao {

	List<DataStatusEinvoiceDto> getProcessedReports(
			Anx1FileStatusReportsReqDto request);
}
