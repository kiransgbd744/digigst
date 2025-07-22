/**
 * 
 */
package com.ey.advisory.app.services.search.docsearch;

import java.util.List;

import com.ey.advisory.app.docs.dto.EInvoiceDocumentDto;
import com.ey.advisory.core.dto.EInvoiceDocSearchReqDto;
import com.ey.advisory.core.search.PageRequest;

/**
 * @author Laxmi.Salukuti
 *
 */
public interface EinvoiceManagementDao {

	List<EInvoiceDocumentDto> getEInvMngmtListing(
			EInvoiceDocSearchReqDto request,PageRequest pageReq);

}
