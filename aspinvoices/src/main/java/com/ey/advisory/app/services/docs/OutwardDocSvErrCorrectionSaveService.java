package com.ey.advisory.app.services.docs;

import java.util.List;

import com.ey.advisory.app.data.entities.client.Anx1OutWardErrHeader;
import com.ey.advisory.app.docs.dto.OutwardDocSvErrSaveRespDto;

/**
 * This interface represents the service for saving a batch of outward 
 * structural error documents obtained from excel upload.
 * saveSvErrDocuments method with the list of Anx1OutWardErrHeader objects
 * does the structural validations
 * (This includes the data type check and required elements check).
 * 
 * @author Mohana.Dasari
 *
 */
public interface OutwardDocSvErrCorrectionSaveService {

	/**
	 * The method is responsible for saving a 
	 * list of corrected structural  error documents from UI.
	 * Structural validations include validating the document structure, 
	 * mandatory elements check and data type check. 
	 * 
	 * @param docs
	 * @return
	 */
	public List<OutwardDocSvErrSaveRespDto> saveSvErrDocuments(
			List<Anx1OutWardErrHeader> correctedErrDocs);
	
}
