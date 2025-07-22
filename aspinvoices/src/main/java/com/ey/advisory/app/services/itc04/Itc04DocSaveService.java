/**
 * 
 */
package com.ey.advisory.app.services.itc04;

import java.util.List;

import com.ey.advisory.app.data.entities.client.Itc04HeaderEntity;
import com.ey.advisory.app.docs.dto.Itc04DocSaveRespDto;

/**
 * This interface represents the service for saving a batch of documents
 * obtained from various sources. The saveDocuments method with the list of
 * Itc04HeaderEntity objects assumes that the structural validations are already
 * done on the documents (This includes the data type check and required
 * elements check).
 * 
 * @author Laxmi.Salukuti
 *
 */
public interface Itc04DocSaveService {

	/**
	 * The method for saving a list of structurally validated documents.
	 * Structural validations include validating the document structure,
	 * mandatory elements check and data type check.
	 * 
	 * @param docs
	 * @return
	 */
	public Itc04DocSaveRespDto saveItc04Documents(List<Itc04HeaderEntity> docs);

}
