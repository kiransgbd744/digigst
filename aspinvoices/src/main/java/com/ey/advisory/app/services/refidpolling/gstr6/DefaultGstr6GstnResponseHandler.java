package com.ey.advisory.app.services.refidpolling.gstr6;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.javatuples.Triplet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.Gstr6DistributionEntity;
import com.ey.advisory.app.data.entities.client.Gstr6VerticalWebError;
import com.ey.advisory.app.data.entities.client.InwardTransDocError;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.data.repositories.client.Gstr6GstnDistributionSaveRepository;
import com.ey.advisory.app.data.repositories.client.Gstr6VerticalWebErrorRepo;
import com.ey.advisory.app.data.repositories.client.gstr2.InwardTransDocErrRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.InwardTransDocRepository;
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
@Component("DefaultGstr6GstnResponseHandler")
public class DefaultGstr6GstnResponseHandler implements Gstr6GstnResponseHandler {

	@Autowired
	@Qualifier("batchSaveStatusRepository")
	private Gstr1BatchRepository batchSaveStatusRepository;

	@Autowired
	@Qualifier("InwardTransDocRepository")
	private InwardTransDocRepository inwardTransDocRepository;

	@Autowired
	@Qualifier("Gstr6GstnDistributionSaveRepository")
	private Gstr6GstnDistributionSaveRepository distributionRepository;

	@Autowired
	@Qualifier("Gstr6PEInwardResponserHandler")
	Gstr6ResponseHandler gtr6PEInwardResponserHandler;

	@Autowired
	@Qualifier("Gstr6PEDistributionResponserHandler")
	Gstr6ResponseHandler gstr6PEDistributionResponserHandler;

	@Autowired
	@Qualifier("InwardTransDocErrRepository")
	private InwardTransDocErrRepository docErrorRepository;

	@Autowired
	@Qualifier("Gstr6VerticalWebErrorRepo")
	private Gstr6VerticalWebErrorRepo isdDocErrorRepository;

	private static final List<String> GSTR6_INWARD_SECTIONS = ImmutableList.of(APIConstants.B2B, APIConstants.B2BA,
			APIConstants.CDN, APIConstants.CDNA);
	private static final List<String> GSTR6_DISTRIBUTION_SECTIONS = ImmutableList.of(APIConstants.ISD,
			APIConstants.ISDA);

	public static final String ER = "ER";
	public static final String P = "P";
	public static final String PE = "PE";

	private static final List<String> CUST_GSTIN_ERROR_CODES = ImmutableList.of("RET11410", "RET191113", "RET191119",
			"RET191125");

	// Proceed records Handler
	@Override
	public void phandleResponse(JsonElement root, Gstr1SaveBatchEntity batch) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Pooling Gstr6 ReturnStatus- P response handling" + "started for {} ", batch.getSection());
		}
		if (batch.getSection() != null && batch.getReturnType() != null) {
			LocalDateTime now = EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now());
			if (GSTR6_INWARD_SECTIONS.contains(batch.getSection().toLowerCase())) {
				inwardTransDocRepository.markDocsAsSavedForBatch(batch.getId(), now, batch.getReturnPeriod());
			} else if (GSTR6_DISTRIBUTION_SECTIONS.contains(batch.getSection().toLowerCase())) {
				distributionRepository.markDocsAsSavedForBatch(batch.getId(), now);
			} else {
				LOGGER.error("Pooling Gstr6 ReturnStatus- UnKnown section {}"
						+ " found in P case this should be handled here.", batch.getSection());
			}
			batchSaveStatusRepository.updateGstr1SaveBatchbyBatchId(batch.getId(), P, 0, now);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Pooling Gstr6 ReturnStatus- P response handling" + "completed for {} ", batch.getSection());
		}
	}

	// Processed with errors records Handler
	@Override
	public void pEhandleResponse(JsonElement root, Gstr1SaveBatchEntity batch) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Pooling Gstr6 ReturnStatus- PE response handling" + "started for {} ", batch.getSection());
		}
		LocalDateTime now = EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now());
		LocalDate utcDateTimeFromLocal = EYDateUtil.toUTCDateTimeFromLocal(LocalDate.now());
		Integer errorCount = batch.getBatchSize();
		if (batch.getSection() != null) {
			if (GSTR6_INWARD_SECTIONS.contains(batch.getSection().toLowerCase())) {

				Triplet<List<String>, Map<String, InwardTransDocError>, Integer> secResponse = null;
				secResponse = gtr6PEInwardResponserHandler.response(root, batch, errorCount);
				if (secResponse != null && secResponse.getValue1()!=null && !secResponse.getValue1().isEmpty()) {
					Map<String, InwardTransDocError> docErrorMap = secResponse.getValue1();
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"Pooling Gstr6 ReturnStatus- response has error count of {} and with  error doc_keys are {} ",
								errorCount, docErrorMap.keySet());
					}
					List<InwardTransDocument> headerDocs = new ArrayList<>();
					Map<String, InwardTransDocError> docErrorMap1 = new HashMap<>();
					for (Entry<String, InwardTransDocError> entry : docErrorMap.entrySet()) {
						InwardTransDocError value = entry.getValue();
						String errorCode = value.getErrorCode();
						String errorMsg = value.getErrorDesc();
						if (CUST_GSTIN_ERROR_CODES.contains(errorCode)) {
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug(
										"Pooling Gstr6 ReturnStatus- response is invalid gstin with error-code {} ",
										errorCode);
							}
							String docKey = entry.getKey();
							List<InwardTransDocument> errorDocs = inwardTransDocRepository.findDocsByDocKey(docKey);
							if (errorDocs != null && !errorDocs.isEmpty()) {
								InwardTransDocument errorDoc = errorDocs.get(0);
								String cgstin = errorDoc.getCgstin();
								List<String> docKeysArrForInvalidGstins = inwardTransDocRepository
										.findDocKeysByBatchIdAndCtin(batch.getId(), cgstin);

								docKeysArrForInvalidGstins.stream().forEach(arr -> {
									InwardTransDocError errObj = new InwardTransDocError();
									errObj.setErrorCode(errorCode);
									errObj.setErrorType(GSTConstants.ERR);
									errObj.setSource(GSTConstants.GSTN_VALIDATION);
									errObj.setErrorDesc(errorMsg);
									errObj.setCreatedBy(GSTConstants.SYSTEM);
									errObj.setCreatedDate(now);
									errObj.setValType(GSTConstants.BUSINESS_VALIDATIONS);
									String key = String.valueOf(arr);
									// Using the invoice key, get the error
									// object
									docErrorMap1.put(key, errObj);
								});
							}
						}
					}
					docErrorMap.putAll(docErrorMap1);
					errorCount = docErrorMap.size();
					List<String> invKeySetAsList = new ArrayList<>(docErrorMap.keySet());

					/*
					 * inwardTransDocRepository.markDocsAsErrorsForBatch(batch.
					 * getId(), invKeySetAsList, now); if
					 * (LOGGER.isDebugEnabled()) { LOGGER.debug(
					 * "Pooling Gstr6 ReturnStatus- Only the Error invoices of the batch {} is updated as GSTN_ERROR "
					 * , batch.getId()); }
					 */
					if (invKeySetAsList != null && !invKeySetAsList.isEmpty()) {
						inwardTransDocRepository.markDocsAsSavedForBatchByErroredDocKeys(batch.getId(), invKeySetAsList,
								now, batch.getReturnPeriod());

						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"Pooling Gstr6 ReturnStatus- Only the Saved invoices of the batch {} is updated as SAVED_TO_GSTN ",
									batch.getId());
						}
						headerDocs = inwardTransDocRepository.findByGstnBatchIdAndDocKeyIn(batch.getId(),
								invKeySetAsList);
					}

					List<InwardTransDocError> errorDocList = new ArrayList<>();
					List<InwardTransDocument> headerDocs1 = new ArrayList<>();
					for (InwardTransDocument doc : headerDocs) {
						String key = String.valueOf(doc.getDocKey());
						// Using the invoice key, get the error object
						InwardTransDocError error = docErrorMap.get(key);
						error.setDocHeaderId(doc.getId());
						error.setCgstin(doc.getSgstin());
						error.setTaxperiod(doc.getTaxperiod());
						error.setDerivedTaxperiod(doc.getDerivedTaxperiod());
						error.setAcceptanceId(doc.getAcceptanceId());
						error.setCreatedBy(APIConstants.SYSTEM);
						error.setCreatedDate(now);
						errorDocList.add(error);

						doc.setGstnError(true);
						doc.setSaved(false);
						doc.setSavedToGSTNDate(utcDateTimeFromLocal);
						doc.setGstnErrorCode(error.getErrorCode());
						doc.setGstnErrorDesc(error.getErrorDesc());
						doc.setUpdatedDate(now);
						doc.setGstr6SavedReturnPeriod(batch.getReturnPeriod());
						headerDocs1.add(doc);
					}
					if (headerDocs != null && !headerDocs.isEmpty()) {
						inwardTransDocRepository.saveAll(headerDocs1);
						docErrorRepository.saveAll(errorDocList);
					}
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"Pooling Gstr6 ReturnStatus- batch table is updating with batchId {}, status {}, errorcount {} ",
								batch.getId(), PE, errorCount);
					}
					batchSaveStatusRepository.updateGstr1SaveBatchbyBatchId(batch.getId(), PE, errorCount, now);
				}

			} else if (GSTR6_DISTRIBUTION_SECTIONS.contains(batch.getSection().toLowerCase())) {
				Triplet<List<String>, Map<String, Gstr6VerticalWebError>, Integer> secResponse = null;
				secResponse = gstr6PEDistributionResponserHandler.isdResponse(root, batch, errorCount);
				if (secResponse != null && secResponse.getValue1()!=null && !secResponse.getValue1().isEmpty()) {
					Map<String, Gstr6VerticalWebError> docErrorMap = secResponse.getValue1();
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"Pooling Gstr6 ReturnStatus- response has error count of {} and with  error doc_keys are {} ",
								errorCount, docErrorMap.keySet());
					}
					List<Gstr6DistributionEntity> headerDocs = new ArrayList<>();
					Map<String, Gstr6VerticalWebError> docErrorMap1 = new HashMap<>();
					for (Entry<String, Gstr6VerticalWebError> entry : docErrorMap.entrySet()) {
						Gstr6VerticalWebError value = entry.getValue();
						String errorCode = value.getErrorCode();
						String errorMsg = value.getErrorDesc();
						if (CUST_GSTIN_ERROR_CODES.contains(errorCode)) {
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("Pooling Gstr6 ReturnStatus- response is invalid gstin with error-code {} ",
										errorCode);
							}
							String docKey = entry.getKey();
							List<Gstr6DistributionEntity> errorDocs = distributionRepository.findDocsByDocKey(docKey);
							if (errorDocs != null && !errorDocs.isEmpty()) {
								Gstr6DistributionEntity errorDoc = errorDocs.get(0);
								String cgstin = errorDoc.getRecipientGSTIN();
								List<String> docKeysArrForInvalidGstins = distributionRepository
										.findDocKeysByBatchIdAndCtin(batch.getId(), cgstin);

								docKeysArrForInvalidGstins.stream().forEach(arr -> {
									Gstr6VerticalWebError errObj = new Gstr6VerticalWebError();
									errObj.setErrorCode(errorCode);
									errObj.setErrorType(GSTConstants.ERR);
									errObj.setErrorSource(GSTConstants.GSTN_VALIDATION);
									errObj.setErrorDesc(errorMsg);
									errObj.setCreatedBy(GSTConstants.SYSTEM);
									errObj.setCreatedDate(now);
									errObj.setValueType(GSTConstants.BUSINESS_VALIDATIONS);
									String key = String.valueOf(arr);
									// Using the invoice key, get the error object
									docErrorMap1.put(key, errObj);
								});
							}
						}
					}
					docErrorMap.putAll(docErrorMap1);
					errorCount = docErrorMap.size();
					List<String> invKeySetAsList = new ArrayList<>(docErrorMap.keySet());

					/*
					 * distributionRepository.markDocsAsErrorsForBatch(batch.getId()
					 * , invKeySetAsList, now); if (LOGGER.isDebugEnabled()) {
					 * LOGGER.debug(
					 * "Pooling Gstr6 ReturnStatus- Only the Error invoices of the batch {} is updated as GSTN_ERROR "
					 * , batch.getId()); }
					 */
					if(invKeySetAsList!=null && !invKeySetAsList.isEmpty()){
						distributionRepository.markDocsAsSavedForBatchByErroredDocKeys(batch.getId(), invKeySetAsList, now);

						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"Pooling Gstr6 ReturnStatus- Only the Saved invoices of the batch {} is updated as SAVED_TO_GSTN ",
									batch.getId());
						}

						headerDocs = distributionRepository.findByGstnBatchIdAndDocKeyIn(batch.getId(), invKeySetAsList);	
					}		

					List<Gstr6VerticalWebError> errorDocList = new ArrayList<>();
					List<Gstr6DistributionEntity> headerDocs1 = new ArrayList<>();
					for (Gstr6DistributionEntity doc : headerDocs) {
						String key = String.valueOf(doc.getProcessKey());
						// Using the invoice key, get the error object
						Gstr6VerticalWebError error = docErrorMap.get(key);
						error.setCommanId(doc.getId());
						error.setCgstin(doc.getRecipientGSTIN());
						error.setTaxperiod(doc.getReturnPeriod());
						error.setDerivedTaxperiod(doc.getDerivedRetPeriod());
						error.setFileId(doc.getFileId());
						error.setCreatedBy(APIConstants.SYSTEM);
						error.setCreatedDate(now);
						errorDocList.add(error);

						doc.setError(true);
						doc.setSaveToGstn(false);
						doc.setSavedToGSTNDate(utcDateTimeFromLocal);
						doc.setGstnErrorCode(error.getErrorCode());
						doc.setGstnErrorDesc(error.getErrorDesc());
						doc.setModifiedOn(now);
						headerDocs1.add(doc);
					}
					if (headerDocs != null && !headerDocs.isEmpty()) {
						distributionRepository.saveAll(headerDocs1);
						isdDocErrorRepository.saveAll(errorDocList);
					}
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"Pooling Gstr6 ReturnStatus- batch table is updating with batchId {}, status {}, errorcount {} ",
								batch.getId(), PE, errorCount);
					}
					batchSaveStatusRepository.updateGstr1SaveBatchbyBatchId(batch.getId(), PE, errorCount, now);
				}		

			} else {
				LOGGER.error("Pooling Gstr6 ReturnStatus- UnKnown section {}"
						+ " found in PE case this should be handled here.", batch.getSection());
			}

		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Pooling Gstr6 ReturnStatus- PE response handling" + " completed for {} ", batch.getSection());
		}
	}

	// Error Handler
	@Override
	public void ehandleResponse(JsonElement root, Gstr1SaveBatchEntity batch) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Pooling Gstr6 ReturnStatus- ER response" + " handling started for {} ", batch.getSection());
		}
		if (batch.getSection() != null && batch.getReturnType() != null) {
			LocalDateTime now = EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now());
			if (GSTR6_INWARD_SECTIONS.contains(batch.getSection().toLowerCase())) {
				inwardTransDocRepository.markDocsAsErrorForBatch(batch.getId(), now, batch.getReturnPeriod());
			} else if (GSTR6_DISTRIBUTION_SECTIONS.contains(batch.getSection().toLowerCase())) {
				distributionRepository.markDocsAsErrorForBatch(batch.getId(), now);
			} else {
				LOGGER.error("Pooling Gstr6 ReturnStatus- UnKnown section {}"
						+ " found in ER case this should be handled here.", batch.getSection());
			}
			Integer errorCount = batch.getBatchSize();
			batchSaveStatusRepository.updateGstr1SaveBatchbyBatchId(batch.getId(), ER, errorCount, now);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Pooling Gstr6 ReturnStatus- ER response handling" + " completed for {} ", batch.getSection());
		}
	}

}
