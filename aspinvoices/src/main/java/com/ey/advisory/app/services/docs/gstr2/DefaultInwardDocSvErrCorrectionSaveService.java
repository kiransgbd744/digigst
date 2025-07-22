package com.ey.advisory.app.services.docs.gstr2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.Anx2InwardErrorHeaderEntity;
import com.ey.advisory.app.docs.dto.gstr2.InwardDocSvErrSaveRespDto;
import com.ey.advisory.app.docs.errutils.sv.SvErrDataArrayHandler;
import com.ey.advisory.app.docs.errutils.sv.SvErrDataArrayKeyBuilder;
import com.ey.advisory.app.docs.errutils.sv.impl.InwardDocSvErrDataArrayHandler;
import com.ey.advisory.app.docs.errutils.sv.impl.
											InwardDocSvErrDataArrayKeyBuilder;
import com.ey.advisory.app.services.strcutvalidation.inward.
										Anx1InwardDocStructuralValidatorChain;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.ProcessingResult;

/**
 * This class is responsible for saving corrected Structural Inward Error.
 * Documents. If any Incoming Inward Document has Structural Validation errors,
 * It will be saved to Error Doc Header else it will be processed and saved to
 * Doc Header and Business Validation will be performed on it
 * 
 * @author Mohana.Dasari
 *
 */
@Service("DefaultInwardDocSvErrCorrectionSaveService")
public class DefaultInwardDocSvErrCorrectionSaveService
		implements InwardDocSvErrCorrectionSaveService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DefaultInwardDocSvErrCorrectionSaveService.class);

	private static final String EXCEPTION_APP = "Exception while saving the records";
	private static final String EXCEPTIONS = "Exception Occured: {}";
	
	@Autowired
	@Qualifier("InwardDocSvErrListToArrConversion")
	private InwardDocSvErrListToArrConversion inwardDocSvErrListToArrConversion;

	@Autowired
	@Qualifier("Anx1InwardDocStructuralValidatorChain")
	private Anx1InwardDocStructuralValidatorChain structValChain;

	@Autowired
	@Qualifier("InwardDocSvErrCorrectionSave")
	private InwardDocSvErrCorrectionSave inwardDocSvErrCorrectionSave;

	@Override
	public List<InwardDocSvErrSaveRespDto> saveSvErrDocuments(
			List<Anx2InwardErrorHeaderEntity> correctedErrDocs) {
		List<InwardDocSvErrSaveRespDto> resp = 
								new ArrayList<>();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(" Entered saveSvErrDocuments ");
		}

		// Convert Corrected Inward Structural Error List to Array object
		List<Object[]> docObjList = inwardDocSvErrListToArrConversion
				.convertInwardDocSvErrListToArrObj(correctedErrDocs);

		if (LOGGER.isDebugEnabled()) {
			if (docObjList != null && !docObjList.isEmpty()) {
				int size = docObjList.size();
				LOGGER.debug(" Inward SV Err Document Size " + size);
			}
		}

		// Add Handler to read object array and create Doc Key.
		SvErrDataArrayKeyBuilder<String> inwardDocSvErrDataArrayKeyBuilder = 
				new InwardDocSvErrDataArrayKeyBuilder();
		SvErrDataArrayHandler dataArrayHandler = 
				new InwardDocSvErrDataArrayHandler<String>(
				inwardDocSvErrDataArrayKeyBuilder);
		docObjList.forEach(docObj -> {
			dataArrayHandler.handleSvErrDataArray(docObj);
		});

		// Get Document Map with Doc Key and List of Document Objects
		Map<String, List<Object[]>> documentMap = 
				((InwardDocSvErrDataArrayHandler<?>) dataArrayHandler)
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
			LOGGER.debug("Inward Structural Validations {} keys {}",
					processingResults.values(), processingResults.keySet());
		}

		// Save SV Error Corrected Documents and get save Response
		try {
			resp = inwardDocSvErrCorrectionSave.saveInwardSvErrCorrectedDocs(
					processingResults, documentMap, resp);
		} catch (Exception e) {
			LOGGER.error(EXCEPTIONS, e);
			throw new AppException(EXCEPTION_APP);
		}
		return resp;
	}
}
