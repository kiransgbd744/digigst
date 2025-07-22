package com.ey.advisory.app.services.docs.gstr2;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.Anx2InwardErrorHeaderEntity;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.docs.dto.gstr2.InwardDocSaveFinalRespDto;
import com.ey.advisory.app.docs.dto.gstr2.InwardDocSaveRespDto;
import com.ey.advisory.app.docs.dto.gstr2.InwardDocSvErrSaveRespDto;
import com.ey.advisory.app.services.docs.DefaultInwardDocErrorSaveService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.ProcessingResult;

/**
 * This class is responsible for saving corrected Inward Structural Error
 * Documents.
 * 
 * @author Mohana.Dasari
 *
 */
@Component("InwardDocSvErrCorrectionSave")
public class InwardDocSvErrCorrectionSave {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(InwardDocSvErrCorrectionSave.class);

	private static final String EXCEPTION_APP = "Exception while saving the records";
	private static final String EXCEPTIONS = "Exception Occured: {}";

	@Autowired
	@Qualifier("Anx2RawFileToInwardTransDocErrorConvertion")
	private Anx2RawFileToInwardTransDocErrorConvertion inwardDocErrConversion;

	@Autowired
	@Qualifier("Anx2RawFileToInwardTransDocConvertion")
	private Anx2RawFileToInwardTransDocConvertion inwardDocConversion;

	@Autowired
	@Qualifier("DefaultInwardDocSaveService")
	private DefaultInwardDocSaveService defaultInwardDocSaveService;

	@Autowired
	@Qualifier("DefaultInwardDocErrorSaveService")
	private DefaultInwardDocErrorSaveService defInwardDocErrSaveSvc;

	/**
	 * This method is responsible for saving Structural Error Inward Documents
	 * and Structurally validated processed Inward Documents
	 * 
	 * @param processingResults
	 */
	public List<InwardDocSvErrSaveRespDto> saveInwardSvErrCorrectedDocs(
			Map<String, List<ProcessingResult>> processingResults,
			Map<String, List<Object[]>> documentMap,
			List<InwardDocSvErrSaveRespDto> resp) {
		// InwardDocSvErrCorrectionRespDto
		List<String> listKeys = new ArrayList<>();
		for (String keys : processingResults.keySet()) {
			listKeys.add(keys);
		}

		if (!processingResults.isEmpty()) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("SV processingResults is not empty");
			}
			Map<String, List<Object[]>> documentMapObj = new HashMap<>();
			Map<String, List<Object[]>> errDocMapObj = new HashMap<>();
			for (String keys : documentMap.keySet()) {
				if (!listKeys.contains(keys)) {
					List<Object[]> list = documentMap.get(keys);
					documentMapObj.put(keys, list);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("documentMapObj " + documentMapObj);
					}
				} else {
					List<Object[]> list = documentMap.get(keys);
					errDocMapObj.put(keys, list);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("errDocMapObj " + errDocMapObj);
					}
				}
			}

			// Convert Error Object to Inward Doc Error Header Entity
			if (errDocMapObj != null && !errDocMapObj.isEmpty()) {
				List<Anx2InwardErrorHeaderEntity> inwardDocSvErrList = inwardDocErrConversion
						.convertAnx2RawFileToInWardTransError(errDocMapObj,
								null, null);

				if (LOGGER.isDebugEnabled()) {
					if (inwardDocSvErrList != null
							&& !inwardDocSvErrList.isEmpty()) {
						LOGGER.debug("inwardDocSvErrList Size "
								+ inwardDocSvErrList.size());
					}
				}
			}

			// Convert Doc Object to Inward Doc Header Entity
			if (documentMap != null && !documentMap.isEmpty()) {
				List<InwardTransDocument> inwardDocList = inwardDocConversion
						.convertAnx2RawFileToInwardTransDoc(documentMap, null,
								null);
				if (LOGGER.isDebugEnabled()) {
					if (inwardDocList != null && !inwardDocList.isEmpty()) {
						LOGGER.debug(
								"inwardDocList Size " + inwardDocList.size());
					}
				}
			}
			// Save SV Error Documents and Structurally Processed Documents
			try {
				resp = saveErrDocAndDoc(documentMap, processingResults,
						documentMapObj, errDocMapObj, resp);
			} catch (Exception e) {
				LOGGER.error(EXCEPTIONS, e);
				throw new AppException(EXCEPTION_APP);
			}
		} else {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("SV processingResults is empty");
			}
			try {
				// Save Structurally Processed Documents
				resp = saveDoc(documentMap, resp);
			} catch (Exception e) {
				LOGGER.error(EXCEPTIONS, e);
				throw new AppException(EXCEPTION_APP);
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Inward SV Error Corrected Docs are saved");
			}
		}

		return resp;

	}

	@Transactional(value = "clientTransactionManager")
	private List<InwardDocSvErrSaveRespDto> saveErrDocAndDoc(
			Map<String, List<Object[]>> documentMap,
			Map<String, List<ProcessingResult>> processingResults,
			Map<String, List<Object[]>> documentMapObj,
			Map<String, List<Object[]>> errDocMapObj,
			List<InwardDocSvErrSaveRespDto> respDto) {

		try {
			if (errDocMapObj != null && !errDocMapObj.isEmpty()) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("errDocMapObj is not null");
				}
				// Convert Structural Error Object to Inward Doc Error Header
				// Entity
				List<Anx2InwardErrorHeaderEntity> inwardDocSvErrList = inwardDocErrConversion
						.convertAnx2RawFileToInWardTransError(errDocMapObj,
								null, null);

				if (LOGGER.isDebugEnabled()) {
					if (inwardDocSvErrList != null
							&& !inwardDocSvErrList.isEmpty()) {
						LOGGER.debug("inwardDocSvErrList Size "
								+ inwardDocSvErrList.size());
						LOGGER.debug("errDocMapObj before saving Error");
					}
				}

				// Save SV Errors to Inward Doc Error Header Entity
				List<InwardDocSvErrSaveRespDto> errResp = defInwardDocErrSaveSvc
						.saveErrorRecord(processingResults, inwardDocSvErrList);
				errResp.forEach(errRes -> {
					InwardDocSvErrSaveRespDto res = new InwardDocSvErrSaveRespDto();
					res.setId(errRes.getId());
					res.setDocNo(errRes.getDocNo());
					res.setDocType(errRes.getDocType());
					res.setDocDate(errRes.getDocDate());
					res.setCustGstin(errRes.getCustGstin());
					res.setSupplierGstin(errRes.getSupplierGstin());
					res.setErrors(errRes.getErrors());
					respDto.add(res);
				});
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("errDocMapObj End ");
				}
			}

			if (!documentMapObj.isEmpty()) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.info("documentMapObj is not null ");
				}
				// Convert Structurally Processed Doc to Inward Doc Header
				// Entity
				List<InwardTransDocument> inwardDocList = inwardDocConversion
						.convertAnx2RawFileToInwardTransDoc(documentMapObj,
								null, null);
				if (LOGGER.isDebugEnabled()) {
					if (inwardDocList != null && !inwardDocList.isEmpty()) {
						LOGGER.debug(
								"inwardDocList Size " + inwardDocList.size());
						LOGGER.debug("documentMapObj before saving Error");
					}
				}
				// Save SV Errors to Inward Doc Header Entity
				InwardDocSaveFinalRespDto finalResp = defaultInwardDocSaveService
						.saveDocuments(inwardDocList, null, null);
				List<InwardDocSaveRespDto> resp = finalResp.getSavedDocsResp();
				resp.forEach(response -> {
					InwardDocSvErrSaveRespDto res = new InwardDocSvErrSaveRespDto();
					res.setId(response.getId());
					res.setDocNo(response.getDocNo());
					res.setDocType(response.getDocType());
					String formattedDate = response.getDocDate()
							.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
					res.setDocDate(formattedDate);
					res.setCustGstin(response.getCustGstin());
					res.setSupplierGstin(response.getSupplierGstin());
					res.setErrors(response.getErrors());
					respDto.add(res);
				});
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("documentMapObj End");
				}
			}

		} catch (Exception e) {
			LOGGER.error(EXCEPTIONS, e);
			throw new AppException(EXCEPTION_APP);
		}
		return respDto;
	}

	private List<InwardDocSvErrSaveRespDto> saveDoc(
			Map<String, List<Object[]>> documentMap,
			List<InwardDocSvErrSaveRespDto> respDto) {

		try {
			List<InwardTransDocument> documents = inwardDocConversion
					.convertAnx2RawFileToInwardTransDoc(documentMap, null,
							null);
			InwardDocSaveFinalRespDto finalResp = defaultInwardDocSaveService
					.saveDocuments(documents, null, null);
			List<InwardDocSaveRespDto> resp = finalResp.getSavedDocsResp();
			resp.forEach(response -> {
				InwardDocSvErrSaveRespDto res = new InwardDocSvErrSaveRespDto();
				res.setId(response.getId());
				res.setDocNo(response.getDocNo());
				res.setDocType(response.getDocType());
				String formattedDate = response.getDocDate()
						.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				res.setDocDate(formattedDate);
				res.setCustGstin(response.getCustGstin());
				res.setSupplierGstin(response.getSupplierGstin());
				res.setErrors(response.getErrors());
				respDto.add(res);
			});
		} catch (Exception e) {
			LOGGER.error(EXCEPTIONS, e);
			throw new AppException(EXCEPTION_APP);
		}
		return respDto;
	}

}
