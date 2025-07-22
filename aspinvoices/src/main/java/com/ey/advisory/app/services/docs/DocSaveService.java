package com.ey.advisory.app.services.docs;

import java.util.List;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.docs.dto.OutwardDocSaveRespDto;

/**
 * This interface represents the service for saving a batch of documents 
 * obtained from various sources. The saveDocuments method  with the 
 * list of OutwardTransDocument objects assumes that the structural 
 * validations are already done on the documents (This includes the data type
 * check and required elements check). 
 * 
 * @author Mohana.Dasari
 *
 */
public interface DocSaveService {
	
	/**
	 * The method for saving a list of structurally validated documents. 
	 * Structural validations include validating the document structure, 
	 * mandatory elements check and data type check. 
	 * 
	 * @param docs
	 * @return
	 */
	public OutwardDocSaveRespDto saveDocuments(
			List<OutwardTransDocument> docs);  
	
}

