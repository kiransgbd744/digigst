package com.ey.advisory.app.services.docs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.Anx1OutWardErrHeader;
import com.ey.advisory.app.docs.dto.OutwardDocSvErrSaveRespDto;
import com.ey.advisory.app.docs.errutils.sv.SvErrDataArrayHandler;
import com.ey.advisory.app.docs.errutils.sv.SvErrDataArrayKeyBuilder;
import com.ey.advisory.app.docs.errutils.sv.impl.OutwardDocSvErrDataArrayHandler;
import com.ey.advisory.app.docs.errutils.sv.impl.OuwardDocSvErrDataArrayKeyBuilder;
import com.ey.advisory.app.services.strcutvalidation.outward.Anx1OutwardDocStructuralValidatorChain;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;

/**
 * This class is responsible for saving corrected Structural Outward Error.
 * Documents. If any Incoming Outward Document has Structural Validation errors,
 * It will be saved to Error Doc Header else it will be processed and saved to
 * Doc Header and Business Validation will be performed on it
 * 
 * @author Mohana.Dasari
 *
 */
@Service("DefaultOutwardDocSvErrCorrectionSaveService")
public class DefaultOutwardDocSvErrCorrectionSaveService
		implements OutwardDocSvErrCorrectionSaveService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DefaultOutwardDocSvErrCorrectionSaveService.class);

	@Autowired
	@Qualifier("OutwardDocSvErrListToArrConversion")
	private OutwardDocSvErrListToArrConversion outwardSvErrListToArrConversion;

	@Autowired
	@Qualifier("Anx1OutwardDocStructuralValidatorChain")
	private Anx1OutwardDocStructuralValidatorChain structValChain;
	
	@Autowired
	@Qualifier("OutwardDocSvErrCorrectionSave")
	private OutwardDocSvErrCorrectionSave outwardDocSvErrCorrectionSave;
	

	@Override
	public List<OutwardDocSvErrSaveRespDto> saveSvErrDocuments(
			List<Anx1OutWardErrHeader> correctedErrDocs) {
		List<OutwardDocSvErrSaveRespDto> resp = new ArrayList<>();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(" Entered saveSvErrDocuments ");
		}
		// Convert Corrected Outward Structural Error List to Array object
		List<Object[]> docObjList = outwardSvErrListToArrConversion
				.convertOutwardDocSvErrListToArrObj(correctedErrDocs);

		if (LOGGER.isDebugEnabled()) {
			if (docObjList != null && !docObjList.isEmpty()) {
				int size = docObjList.size();
				LOGGER.debug(" Outward SV Err Document Size " + size);
			}
		}

		// Add Handler to read object array and create Doc Key.
		SvErrDataArrayKeyBuilder<String> outwardDocSvErrDataArrayKeyBuilder = 
				new OuwardDocSvErrDataArrayKeyBuilder();
		SvErrDataArrayHandler dataArrayHandler = 
				new OutwardDocSvErrDataArrayHandler<String>(
				outwardDocSvErrDataArrayKeyBuilder);
		docObjList.forEach(docObj -> {
			dataArrayHandler.handleSvErrDataArray(docObj);
		});
		// Get Document Map with Doc Key and List of Document Objects
		Map<String, List<Object[]>> documentMap = 
				((OutwardDocSvErrDataArrayHandler<?>) dataArrayHandler)
				.getDocumentMap();
		if (!documentMap.isEmpty()) {
			documentMap.entrySet().forEach(entry -> {
				String key = entry.getKey();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("key {}", key);
					LOGGER.debug("Document Map  Length {}", documentMap.size());
					LOGGER.debug("Document Map:{}", documentMap);
				}
			});
		}

		// Validate Structural Errors against Document Map
		Map<String, List<ProcessingResult>> processingResults = structValChain
				.validation(documentMap);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Outward Structural Validations {} keys {}",
					processingResults.values(), processingResults.keySet());
		}
		// Save SV Error Corrected Documents and get save Response
		try {
			resp = outwardDocSvErrCorrectionSave.saveOutwardSvErrCorrectedDocs(
					processingResults, documentMap, resp);
		} catch (Exception e) {
			LOGGER.error(GSTConstants.EXCEPTIONS, e);
			throw new AppException(GSTConstants.EXCEPTION_APP);
		}
		return resp;
	}

}
