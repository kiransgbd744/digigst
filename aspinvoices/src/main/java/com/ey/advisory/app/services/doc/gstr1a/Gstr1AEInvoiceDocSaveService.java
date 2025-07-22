/**
 * 
 */
package com.ey.advisory.app.services.doc.gstr1a;

import java.util.List;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocument;
import com.ey.advisory.app.docs.dto.einvoice.EInvoiceOutwardDocSaveRespDto;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.einv.dto.GenerateEWBByIrnERPReqDto;
import com.ey.advisory.ewb.dto.CancelEwbReqDto;

/**
 * This interface represents the service for saving a batch of documents
 * obtained from various sources. The saveDocuments method with the list of
 * OutwardTransDocument objects assumes that the structural validations are
 * already done on the documents (This includes the data type check and required
 * elements check).
 * 
 * @author Shashikant.Shukla
 *
 */
public interface Gstr1AEInvoiceDocSaveService {

	/**
	 * The method for saving a list of structurally validated documents.
	 * Structural validations include validating the document structure,
	 * mandatory elements check and data type check.
	 * 
	 * @param docs
	 * @return
	 */
	public EInvoiceOutwardDocSaveRespDto saveDocuments(
			List<Gstr1AOutwardTransDocument> docs, String sourceId,
			String headerPayloadId, List<AsyncExecJob> asyncJobs);

	public EInvoiceOutwardDocSaveRespDto saveCanEwbDocuments(
			List<CancelEwbReqDto> documents, String sourceId,
			String headerPayloadId);

	public EInvoiceOutwardDocSaveRespDto saveGenEwbIrnDocuments(
			List<GenerateEWBByIrnERPReqDto> documents, String headerPayloadId);

}
