package com.ey.advisory.app.services.docs;

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

import com.ey.advisory.app.data.entities.client.Anx1OutWardErrHeader;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.docs.dto.DocSaveRespDto;
import com.ey.advisory.app.docs.dto.OutwardDocSaveRespDto;
import com.ey.advisory.app.docs.dto.OutwardDocSvErrSaveRespDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;

/**
 * This class is responsible for saving corrected Outward Structural Error
 * Documents.
 * 
 * @author Mohana.Dasari
 *
 */
@Component("OutwardDocSvErrCorrectionSave")
class OutwardDocSvErrCorrectionSave {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(OutwardDocSvErrCorrectionSave.class);
	
	@Autowired
	@Qualifier("SRFileToOutwardTransDocErrConvertion")
	private SRFileToOutwardTransDocErrConvertion outwardDocErrConversion;
	
	@Autowired
	@Qualifier("SRFileToOutwardTransDocConvertion")
	private SRFileToOutwardTransDocConvertion outwardDocConversion;
	
	@Autowired
	@Qualifier("DefaultDocSaveService")
	private DefaultDocSaveService defaultOutwardDocSaveService;
	
	@Autowired
	@Qualifier("DefaultDocErrorSaveService")
	private DefaultDocErrorSaveService defOutwardDocErSavSvc;

	
	/**
	 * This method is responsible for saving Structural Error Outward Documents 
	 * and Structurally validated processed Outward Documents
	 * @param processingResults
	 */
	public List<OutwardDocSvErrSaveRespDto>  saveOutwardSvErrCorrectedDocs(
			Map<String, List<ProcessingResult>> processingResults,
			Map<String, List<Object[]>> documentMap,
			List<OutwardDocSvErrSaveRespDto> resp) {
		List<String> listKeys = new ArrayList<>();
		for (String keys : processingResults.keySet()) {
			listKeys.add(keys);
		}
		if (!processingResults.isEmpty()) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
				        "SV processingResults are not empty");
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
			//Save SV Error Documents and Structurally Processed Documents
			try {
				resp = saveErrDocAndDoc(documentMap, processingResults,
						documentMapObj, errDocMapObj,resp);
			} catch (Exception e) {
				LOGGER.error(GSTConstants.EXCEPTIONS, e);
				throw new AppException(GSTConstants.EXCEPTION_APP);
			}
		}else{
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
				        "SV processingResults is empty");
			}
			try {
				//Save Structurally Processed Documents
				resp = saveDoc(documentMap,resp);
			} catch (Exception e) {
				LOGGER.error(GSTConstants.EXCEPTIONS, e);
				throw new AppException(GSTConstants.EXCEPTION_APP);
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Outward SV Error Corrected Docs are saved");
			}
		}
		
		return resp;
	}
	
	@Transactional(value = "clientTransactionManager")
	private List<OutwardDocSvErrSaveRespDto> saveErrDocAndDoc(
			Map<String, List<Object[]>> documentMap,
			Map<String, List<ProcessingResult>> processingResults,
			Map<String, List<Object[]>> documentMapObj,
			Map<String, List<Object[]>> errDocMapObj,
			List<OutwardDocSvErrSaveRespDto> respDto) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(" Start Outward saveErrDocAndDoc");
		}
		try {
			if (errDocMapObj != null && !errDocMapObj.isEmpty()) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("errDocMapObj is not null");
				}
				// Convert Structural Error Object to Outward Doc Error Header
				// Entity
				List<Anx1OutWardErrHeader> outwardDocSvErrList = 
						outwardDocErrConversion
						.convertSRFileToOutWardTransError(errDocMapObj, null,
								null,null);
				if (LOGGER.isDebugEnabled()) {
					if (outwardDocSvErrList != null
							&& !outwardDocSvErrList.isEmpty()) {
						LOGGER.debug("outwardDocSvErrList Size "
								+ outwardDocSvErrList.size());
						LOGGER.debug("errDocMapObj before saving Error Record");
					}
				}
				// Save SV Errors to Outward Doc Error Header Entity
				defOutwardDocErSavSvc
						.saveErrorRecord(processingResults,
								outwardDocSvErrList);
//				errResp.forEach(errRes -> {
//					OutwardDocSvErrSaveRespDto res = 
//										new OutwardDocSvErrSaveRespDto();
//					res.setId(errRes.getId());
//					res.setDocNo(errRes.getDocNo());
//					res.setDocType(errRes.getDocType());
//					res.setDocDate(errRes.getDocDate());
//					res.setSupplierGstin(errRes.getSupplierGstin());
//					res.setErrors(errRes.getErrors());
//					respDto.add(res);
//				});
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("errDocMapObj End ");
				}
						
			}
			
			if (!documentMapObj.isEmpty()) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.info("documentMapObj is not null ");
				}
				// Convert Structurally Processed Doc to Outward Doc Header
				// Entity
				List<OutwardTransDocument> outwardDocList = outwardDocConversion
						.convertSRFileToOutwardTransDoc(documentMapObj, null,
								null,null);
						
				if (LOGGER.isDebugEnabled()) {
					if (outwardDocList != null && !outwardDocList.isEmpty()) {
						LOGGER.debug(
								"outwardDocList Size " + outwardDocList.size());
						LOGGER.debug("documentMapObj before saving Error");
					}
				}
				// Save SV Errors to Outward Doc Header Entity
				OutwardDocSaveRespDto docSaveResp = defaultOutwardDocSaveService
						.saveDocuments(outwardDocList);
				List<DocSaveRespDto> resp = docSaveResp.getSavedDocsResp();
				resp.forEach(response -> {
					OutwardDocSvErrSaveRespDto res = 
							new OutwardDocSvErrSaveRespDto();
					res.setId(response.getId());
					res.setDocNo(response.getDocNo());
					res.setDocType(response.getDocType());
					String formattedDate = response.getDocDate()
							.format(DateTimeFormatter
									.ofPattern(GSTConstants.DATE_FORMAT1));
					res.setDocDate(formattedDate);
					res.setSupplierGstin(response.getSupplierGstin());
					res.setErrors(response.getErrors());
					respDto.add(res);
				});
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("documentMapObj End");
				}
			}
		} catch (Exception e) {
			LOGGER.error(GSTConstants.EXCEPTIONS, e);
			throw new AppException(GSTConstants.EXCEPTION_APP);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(" End Outward saveErrDocAndDoc");
		}
		return respDto;
	}
	
	private List<OutwardDocSvErrSaveRespDto> saveDoc(
			Map<String, List<Object[]>> documentMap,
			List<OutwardDocSvErrSaveRespDto> respDto) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(" Start Outward saveDoc");
		}

		try {
			List<OutwardTransDocument> documents = outwardDocConversion
					.convertSRFileToOutwardTransDoc(documentMap, null, null,null);
			OutwardDocSaveRespDto docSaveResp = defaultOutwardDocSaveService
					.saveDocuments(documents);
			List<DocSaveRespDto> resp = docSaveResp.getSavedDocsResp();
			resp.forEach(response -> {
				OutwardDocSvErrSaveRespDto res = 
						new OutwardDocSvErrSaveRespDto();
				res.setId(response.getId());
				res.setDocNo(response.getDocNo());
				res.setDocType(response.getDocType());
				String formattedDate = response.getDocDate().format(
						DateTimeFormatter.ofPattern(GSTConstants.DATE_FORMAT1));
				res.setDocDate(formattedDate);
				res.setSupplierGstin(response.getSupplierGstin());
				res.setErrors(response.getErrors());
				respDto.add(res);
			});
		} catch (Exception e) {
			LOGGER.error(GSTConstants.EXCEPTIONS, e);
			throw new AppException(GSTConstants.EXCEPTION_APP);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(" End Outward saveDoc");
		}
		return respDto;
	}
}

