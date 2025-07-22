package com.ey.advisory.app.services.docs.gstr2;

import java.util.List;

import com.ey.advisory.app.data.entities.client.Anx2InwardErrorHeaderEntity;
import com.ey.advisory.app.docs.dto.gstr2.InwardDocSvErrSaveRespDto;

/**
 * This interface represents the service for saving a batch of inward structural
 * error documents obtained from excel upload. saveSvErrDocuments method with
 * the list of Anx2InwardErrorHeaderEntity objects does the structural
 * validations (This includes the data type check and required elements check).
 * 
 * @author Mohana.Dasari
 *
 */
public interface InwardDocSvErrCorrectionSaveService {

	/**
	 * The method is responsible for saving a 
	 * list of corrected structural  error documents from UI.
	 * Structural validations include validating the document structure, 
	 * mandatory elements check and data type check. 
	 * 
	 * @param docs
	 * @return
	 */
	public List<InwardDocSvErrSaveRespDto> saveSvErrDocuments(
			List<Anx2InwardErrorHeaderEntity> correctedErrDocs); 
}
