/**
 * 
 */
package com.ey.advisory.app.services.docs.gstr7;

import java.util.List;

import com.ey.advisory.app.data.entities.client.gstr7trans.Gstr7TransDocHeaderEntity;
import com.ey.advisory.app.docs.dto.einvoice.EInvoiceOutwardDocSaveRespDto;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;

/**
 * This interface represents the service for saving a batch of documents
 * obtained from various sources. The saveDocuments method with the list of
 * OutwardTransDocument objects assumes that the structural validations are
 * already done on the documents (This includes the data type check and required
 * elements check).
 * 
 * @author Siva.Reddy
 *
 */
public interface Gstr7TransDocSaveService {

	/**
	 * The method for saving a list of structurally validated documents.
	 * Structural validations include validating the document structure,
	 * mandatory elements check and data type check.
	 * 
	 * @param docs
	 * @return
	 */
	public EInvoiceOutwardDocSaveRespDto saveDocuments(
			List<Gstr7TransDocHeaderEntity> docs, String sourceId,
			String headerPayloadId, List<AsyncExecJob> asyncJobs);

}
