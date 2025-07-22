package com.ey.advisory.app.services.doc.gstr1a;

import java.util.List;
import java.util.Map;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAnx1OutWardErrHeader;
import com.ey.advisory.common.ProcessingResult;

/**
 * This interface represents the service for saving a batch of documents
 * obtained from various sources. The saveErrorDocuments method with the list of
 * Anx1OutwardErrorTransDocument objects assumes that the structural validations
 * are already done on the documents (This includes the data type check and
 * required elements check).
 * 
 * @author Shashikant.Shukla
 *
 */
public interface Gstr1ADocErrorSaveService {

	/**
	 * The method for saving a list of structurally validated documents.
	 * Structural validations include validating the document structure,
	 * mandatory elements check and data type check.
	 * 
	 * @param eror
	 *            docs
	 * @return
	 */
	public void saveErrorRecord(
			Map<String, List<ProcessingResult>> processingResults,
			List<Gstr1AAnx1OutWardErrHeader> errorDocument);

}
