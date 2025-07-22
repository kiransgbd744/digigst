package com.ey.advisory.app.services.docs.gstr2;

import java.util.List;

import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.docs.dto.gstr2.InwardDocSaveFinalRespDto;

/**
 * This interface represents the service for saving a batch of documents
 * obtained from various sources. The saveDocuments method with the list of
 * InwardTransDocument objects assumes that the structural validations are
 * already done on the documents (This includes the data type check and required
 * elements check).
 * 
 * @author @author Laxmi.Salukuti
 *
 */
public interface InwardDocSave240Service {

	/**
	 * The method for saving a list of structurally validated documents.
	 * Structural validations include validating the document structure,
	 * mandatory elements check and data type check.
	 * 
	 * @param docs
	 * @return
	 */
	public InwardDocSaveFinalRespDto saveDocuments(
			List<InwardTransDocument> docs, String sourceId,
			String headerPayloadId);

}
