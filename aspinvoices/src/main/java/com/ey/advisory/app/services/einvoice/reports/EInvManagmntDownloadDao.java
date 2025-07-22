/**
 * 
 */
package com.ey.advisory.app.services.einvoice.reports;

import java.util.List;

import com.ey.advisory.app.docs.dto.einvoice.EinvoiceMangementResponseDto;
import com.ey.advisory.core.dto.EInvoiceDocSearchReqDto;

/**
 * @author Laxmi.Salukuti
 *
 */
public interface EInvManagmntDownloadDao {

	List<EinvoiceMangementResponseDto> getEInvMngmtScreendwnld(
			EInvoiceDocSearchReqDto request);

}
