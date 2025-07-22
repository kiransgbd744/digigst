package com.ey.advisory.app.services.refidpolling.gstr7;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.javatuples.Triplet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.Gstr7DocErrorEntity;
import com.ey.advisory.app.data.entities.client.Gstr7ProcessedTdsEntity;
import com.ey.advisory.app.data.repositories.client.gstr7.Gstr7DocErrorRepository;
import com.ey.advisory.app.data.repositories.client.gstr7.Gstr7ProcessedRepository;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author SriBhavya
 *
 */
@Slf4j
@Component("DefaultGstr7GstnResponseHandler")
public class DefaultGstr7GstnResponseHandler implements Gstr7GstnResponseHandler{
	
	@Autowired
	@Qualifier("batchSaveStatusRepository")
	private Gstr1BatchRepository batchSaveStatusRepository;
	
	@Autowired
	@Qualifier("Gstr7ProcessedRepository")
	private Gstr7ProcessedRepository gstr7ProcessedRepository;
	
	@Autowired
	@Qualifier("Gstr7ResponserHandler")
	Gstr7ResponseHandler gstr7ResponserHandler;		
	
	@Autowired
	@Qualifier("Gstr7DocErrorRepository")
	private Gstr7DocErrorRepository gstr7DocErrorRepository;
	
	private static final List<String> GSTR7_SECTIONS = ImmutableList
			.of(APIConstants.TDS, APIConstants.TDSA);

	public static final String ER = "ER";
	public static final String P = "P";
	public static final String PE = "PE";
	
	/*private static final List<String> CUST_GSTIN_ERROR_CODES = ImmutableList
			.of("RET11410", "RET191113", "RET191119", "RET191125");*/
	
	//Processed records 
	@Override
	public void phandleResponse(JsonElement root, Gstr1SaveBatchEntity batch) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Pooling Gstr7 ReturnStatus- P response handling"
					+ "started for {} ", batch.getSection());
		}
		if (batch.getSection() != null && batch.getReturnType() != null) {
			LocalDateTime now = EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now());
			if (GSTR7_SECTIONS.contains(batch.getSection().toLowerCase())) {				
				gstr7ProcessedRepository.markDocsAsSavedForBatch(batch.getId(),now);
			} else {
				LOGGER.error(
						"Pooling Gstr7 ReturnStatus- UnKnown section {}"
								+ " found in P case this should be handled here.",
						batch.getSection());
			}
			batchSaveStatusRepository
					.updateGstr1SaveBatchbyBatchId(batch.getId(), P, 0, now);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Pooling Gstr6 ReturnStatus- P response handling"
					+ "completed for {} ", batch.getSection());
		}
		
	}
    //Records processed with errors 
	@Override
	public void pEhandleResponse(JsonElement root, Gstr1SaveBatchEntity batch) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Pooling Gstr7 ReturnStatus- PE response handling"
					+ "started for {} ", batch.getSection());
		}
		Integer errorCount = batch.getBatchSize();
		if (batch.getSection() != null) {
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			Triplet<List<String>, Map<String, Gstr7DocErrorEntity>, Integer> secResponse = null;
			secResponse = gstr7ResponserHandler.response(root, batch,errorCount);
			Map<String, Gstr7DocErrorEntity> docErrorMap = secResponse.getValue1();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Pooling Gstr7 ReturnStatus- response has error count of {} and with  error doc_keys are {} ",
						errorCount, docErrorMap.keySet());
			}
			List<Gstr7ProcessedTdsEntity> headerDocs = new ArrayList<>();
			//Map<String, Gstr7DocErrorEntity> docErrorMap1 = new HashMap<>();
			/*for (Entry<String, Gstr7DocErrorEntity> entry : docErrorMap.entrySet()) {
				Gstr7DocErrorEntity value = entry.getValue();
				String errorCode = value.getErrorCode();
				String errorMsg = value.getErrorDescription();
				if (CUST_GSTIN_ERROR_CODES.contains(errorCode)) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"Pooling Gstr7 ReturnStatus- response is invalid gstin with error-code {} ",
								errorCode);
					}
					String docKey = entry.getKey();
					List<Gstr7ProcessedTdsEntity> errorDocs = gstr7ProcessedRepository
							.findDocsByDocKey(docKey);
					if (errorDocs != null && !errorDocs.isEmpty()) {
						Gstr7ProcessedTdsEntity errorDoc = errorDocs.get(0);
						String cgstin = errorDoc.getTdsGstin();
						List<String> docKeysArrForInvalidGstins = gstr7ProcessedRepository
								.findDocKeysByBatchIdAndCtin(batch.getId(),
										cgstin);

						docKeysArrForInvalidGstins.stream().forEach(arr -> {
							Gstr7DocErrorEntity errObj = new Gstr7DocErrorEntity();
							errObj.setErrorCode(errorCode);
							errObj.setErrorType(GSTConstants.ERR);
							errObj.setErrorSource(GSTConstants.GSTN_VALIDATION);
							errObj.setErrorDescription(errorMsg);
							errObj.setCreatedBy(GSTConstants.SYSTEM);
							errObj.setCreatedOn(LocalDate.now());
							errObj.setValType(
									GSTConstants.BUSINESS_VALIDATIONS);
							String key = String.valueOf(arr);
							// Using the invoice key, get the error object
							docErrorMap1.put(key, errObj);
						});
					}
				}
				
			}*/
			//docErrorMap.putAll(docErrorMap1);
			errorCount = docErrorMap.size();
			List<String> invKeySetAsList = new ArrayList<>(
					docErrorMap.keySet());
			
			/*gstr7ProcessedRepository.markDocsAsErrorsForBatch(batch.getId(),
					invKeySetAsList, now);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Pooling Gstr6 ReturnStatus- Only the Error invoices of the batch {} is updated as GSTN_ERROR ",
						batch.getId());
			}*/
			gstr7ProcessedRepository
					.markDocsAsSavedForBatchByErroredDocKeys(batch.getId(),
							invKeySetAsList, now);
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Pooling Gstr6 ReturnStatus- Only the Saved invoices of the batch {} is updated as SAVED_TO_GSTN ",
						batch.getId());
			}
			headerDocs = gstr7ProcessedRepository
					.findByGstnBatchIdAndDocKeyIn(batch.getId(),
							invKeySetAsList);
			
			List<Gstr7DocErrorEntity> errorDocList = new ArrayList<>();
			List<Gstr7ProcessedTdsEntity> headerDocs1 = new ArrayList<>();
			for (Gstr7ProcessedTdsEntity doc : headerDocs) {
				String key = String.valueOf(doc.getGstinkey());
				// Using the invoice key, get the error object
				Gstr7DocErrorEntity error1 = docErrorMap.get(key);
				
				//Extra logic in summary PE case to create new error object 
				//for all gstin keys.
				Gstr7DocErrorEntity error = new Gstr7DocErrorEntity();
				error.setErrorCode(error1.getErrorCode());
				error.setErrorType(GSTConstants.ERR);
				error.setErrorSource(GSTConstants.GSTN_VALIDATION);
				error.setErrorDescription(error1.getErrorDescription());
				error.setCreatedBy(GSTConstants.SYSTEM);
				error.setCreatedOn(LocalDate.now());
				error.setValType(
						GSTConstants.BUSINESS_VALIDATIONS);
				
				
				error.setDocHeaderId(doc.getId());
				error.setInvKey(doc.getTdsInvKey());
				//error.setCgstin(doc.getTdsGstin());
				//error.setTaxperiod(doc.getReturnPeriod());
				//error.setDerivedTaxperiod(doc.getDerivedTaxperiod());
				error.setFileId(doc.getFileId());
				error.setCreatedBy(APIConstants.SYSTEM);
				error.setCreatedOn(LocalDate.now());
				errorDocList.add(error);
				
				doc.setGstnError(true);
				doc.setSavedGstn(false);
				doc.setSavedGstnDate(LocalDate.now());
				doc.setGstnErrorCode(error.getErrorCode());
				doc.setGstnErrorDesc(error.getErrorDescription());
				doc.setModifiedOn(now);	
				headerDocs1.add(doc);
			}
			if (headerDocs != null && !headerDocs.isEmpty()) {
				gstr7ProcessedRepository.saveAll(headerDocs1);
				gstr7DocErrorRepository.saveAll(errorDocList);
			}
			//we are getting summary level error count so changed the logic to detail level error count 
			Integer batchErrorCount = gstr7ProcessedRepository.
					countOfBatchRecords(batch.getSgstin(),batch.getReturnPeriod(),batch.getId());
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Pooling Gstr7 ReturnStatus- batch table is updating with batchId {}, "
						+ "status {}, errorcount {} ",
						batch.getId(), PE, batchErrorCount);
			}			
			batchSaveStatusRepository.updateGstr1SaveBatchbyBatchId(
					batch.getId(), PE, batchErrorCount, now);
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Pooling Gstr7 ReturnStatus- PE response handling"
						+ " completed for {} ", batch.getSection());
			}

		} else {
			LOGGER.error(
					"Pooling Gstr7 ReturnStatus- UnKnown section {}"
							+ " found in PE case this should be handled here.",
					batch.getSection());
		}
		
		
	}
    //Error Records 
	@Override
	public void ehandleResponse(JsonElement root, Gstr1SaveBatchEntity batch) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Pooling Gstr7 ReturnStatus- ER response"
					+ " handling started for {} ", batch.getSection());
		}
		if (batch.getSection() != null && batch.getReturnType() != null) {
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			if (GSTR7_SECTIONS.contains(batch.getSection().toLowerCase())) {				
				gstr7ProcessedRepository.markDocsAsErrorForBatch(batch.getId(),now);
			}  else {
				LOGGER.error(
						"Pooling Gstr7 ReturnStatus- UnKnown section {}"
								+ " found in ER case this should be handled here.",
						batch.getSection());
			}
			Integer errorCount = batch.getBatchSize();
			batchSaveStatusRepository.updateGstr1SaveBatchbyBatchId(
					batch.getId(), ER, errorCount, now);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Pooling Gstr7 ReturnStatus- ER response handling"
					+ " completed for {} ", batch.getSection());
		}
		
	}

}
