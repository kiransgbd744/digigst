package com.ey.advisory.app.services.docs.gstr7;

import java.util.List;
import java.util.Map;

import com.ey.advisory.app.data.entities.client.gstr7trans.Gstr7TransDocErrHeaderEntity;
import com.ey.advisory.common.ProcessingResult;

/**
 * This interface represents the service for saving a batch of documents 
 * obtained from various sources. The saveErrorDocuments method  with the 
 * list of Anx1OutwardErrorTransDocument objects assumes that the structural 
 * validations are already done on the documents (This includes the data type
 * check and required elements check). 
 * 
 * @author Siva.Reddy
 *
 */
public interface Gstr7TransDocErrorSaveService {
	
	/**
	 * The method for saving a list of structurally validated documents. 
	 * Structural validations include validating the document structure, 
	 * mandatory elements check and data type check. 
	 * 
	 * @param eror docs
	 * @return
	 */
	public void saveErrorRecord(
			Map<String, List<ProcessingResult>> processingResults,
			List<Gstr7TransDocErrHeaderEntity> errorDocument);  
	
}

