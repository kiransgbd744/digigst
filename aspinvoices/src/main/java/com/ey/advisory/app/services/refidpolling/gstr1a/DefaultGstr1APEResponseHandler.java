package com.ey.advisory.app.services.refidpolling.gstr1a;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocument;
import com.ey.advisory.app.data.gstr1A.entities.client.OutwardTransDocErrorGstr1A;
import com.ey.advisory.app.data.gstr1A.repositories.client.DocRepositoryGstr1A;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1ADocErrorRepository;
import com.ey.advisory.app.docs.dto.gstr1.Gstr1SaveToGstnDependencyRetryReqDto;
import com.ey.advisory.app.services.common.DocKeyGenerator;
import com.ey.advisory.app.services.refidpolling.gstr1.Gstr1GstnResponseHandler;
import com.ey.advisory.app.services.refidpolling.gstr1.Gstr1ResponseHandler;
import com.ey.advisory.app.services.savetogstn.jobs.anx1.ChunkSizeFetcher;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.SaveToGstnOprtnType;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import lombok.extern.slf4j.Slf4j;

/**
 * This class is responsible for handling the PE response that we receive from
 * GSTN.
 * 
 * @author Siva Reddy
 *
 */
@Slf4j
@Component("DefaultGstr1APEResponseHandler")
public class DefaultGstr1APEResponseHandler
		implements Gstr1GstnResponseHandler {

	@Autowired
	@Qualifier("Gstr1ACdnurResponseHandler")
	private Gstr1AResponseHandler cdnurResponseHandler;

	@Autowired
	@Qualifier("Gstr1ACdnrResponseHandler")
	private Gstr1AResponseHandler cdnrResponseHandler;

	@Autowired
	@Qualifier("Gstr1AExpResponseHandler")
	private Gstr1AResponseHandler expResponseHandler;

	@Autowired
	@Qualifier("Gstr1APEB2clResponseHandler")
	private Gstr1AResponseHandler pEB2clResponseHandler;

	@Autowired
	@Qualifier("Gstr1APEB2bResponserHandler")
	private Gstr1AResponseHandler pEB2bResponserHandler;

	@Autowired
	@Qualifier("DocRepositoryGstr1A")
	private DocRepositoryGstr1A outwardTransDocumentRepository;

	@Autowired
	@Qualifier("batchSaveStatusRepository")
	private Gstr1BatchRepository batchSaveStatusRepository;

	@Autowired
	@Qualifier("Gstr1ADocErrorRepository")
	private Gstr1ADocErrorRepository docErrorRepository;

	@Autowired
	@Qualifier("DocKeyGenerator")
	private DocKeyGenerator docKeyGenerator;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("ChunkSizeFetcherImpl")
	private ChunkSizeFetcher chunkSizeFetcher;

	// @Autowired
	// private Gstr1GSTINDeleteDataRepository autoDraftRepo;
	//
	// @Autowired
	// private Gstr1vsEinvReconRespProcessedRepository deleteRespRepo;
	//
	@Autowired
	@Qualifier("HsnResponseHandler")
	private Gstr1ResponseHandler hsnResponseHandler;

	private static final List<String> SECTIONS = ImmutableList.of(
			APIConstants.EXP, APIConstants.EXPA, APIConstants.CDNUR,
			APIConstants.CDNURA, APIConstants.B2CL, APIConstants.B2CLA,
			APIConstants.CDNR, APIConstants.CDNRA, APIConstants.B2B,
			APIConstants.B2BA);

	public static final String PE = "PE";

	private static final List<String> CUST_GSTIN_ERROR_CODES = ImmutableList
			.of("RET11410", "RET191113", "RET191119", "RET191125");

	private static final List<String> ORIGINAL_INVOICE_ERROR_CODES = ImmutableList
			.of("RET191119", "RET191124");

	@Override
	public void handleResponse(JsonElement root, Gstr1SaveBatchEntity batch) {

		boolean isOriginalInvoiceNotFound = false;

		if (batch.getSection() != null) {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Pooling Gstr1 ReturnStatus- PE response handling started for {} ",
						batch.getSection());
			}
			// Assuming entire batch as error initially.
			Integer errorCount = batch.getBatchSize();
			if (!SECTIONS.contains(batch.getSection().toLowerCase())) {

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Summary section save PE case {} ",
							batch.getSection());
				}
				LocalDateTime now = EYDateUtil
						.toUTCDateTimeFromLocal(LocalDateTime.now());
				if (APIConstants.HSNSUM.equalsIgnoreCase(batch.getSection())) {
					errorCount = hsnResponseHandler.SaveResponse(root, batch,
							errorCount);
				}
				// Finally, mark the Batch itself with the status of 'PE'
				batchSaveStatusRepository.updateGstr1SaveBatchbyBatchId(
						batch.getId(), PE, errorCount, now);
			} else {

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Transaction level section save PE case {} ",
							batch.getSection());
				}

				Triplet<List<String>, Map<String, OutwardTransDocErrorGstr1A>, Integer> secResponse = null;

				if (batch.getSection().equalsIgnoreCase(APIConstants.B2B)
						|| batch.getSection()
								.equalsIgnoreCase(APIConstants.B2BA)) {
					secResponse = pEB2bResponserHandler.response(root, batch,
							errorCount);
				} else if (batch.getSection()
						.equalsIgnoreCase(APIConstants.B2CL)
						|| batch.getSection()
								.equalsIgnoreCase(APIConstants.B2CLA)) {
					secResponse = pEB2clResponseHandler.response(root, batch,
							errorCount);
				} else if (batch.getSection().equalsIgnoreCase(APIConstants.EXP)
						|| batch.getSection()
								.equalsIgnoreCase(APIConstants.EXPA)) {
					secResponse = expResponseHandler.response(root, batch,
							errorCount);
				} else if (batch.getSection()
						.equalsIgnoreCase(APIConstants.CDNR)
						|| batch.getSection()
								.equalsIgnoreCase(APIConstants.CDNRA)) {
					secResponse = cdnrResponseHandler.response(root, batch,
							errorCount);
				} else if (batch.getSection()
						.equalsIgnoreCase(APIConstants.CDNUR)
						|| batch.getSection()
								.equalsIgnoreCase(APIConstants.CDNURA)) {
					secResponse = cdnurResponseHandler.response(root, batch,
							errorCount);
				} else {
					LOGGER.error(
							"Pooling Gstr1 ReturnStatus- UnKnown section {}"
									+ " found in PE case this should be handled here.",
							batch.getSection());
				}
				Map<String, OutwardTransDocErrorGstr1A> docErrorMap = secResponse
						.getValue1();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Pooling Gstr1 ReturnStatus- response has error count of {} and with  error doc_keys are {} ",
							errorCount, docErrorMap.keySet());
				}
				// Extra logic to handle to Invalid Cust GSTIN
				LocalDateTime now = EYDateUtil
						.toUTCDateTimeFromLocal(LocalDateTime.now());

				Set<String> OriginalInvoiceErrorCodes = null;
				Pair<Integer, Set<String>> pair = handleDocRepo(docErrorMap,
						errorCount, batch, isOriginalInvoiceNotFound);

				errorCount = pair.getValue0();
				OriginalInvoiceErrorCodes = pair.getValue1();
				// Finally, mark the Batch itself with the status of 'PE'
				batchSaveStatusRepository.updateGstr1SaveBatchbyBatchId(
						batch.getId(), PE, errorCount, now);
				Long retryCount = batch.getRetryCount() != null
						? batch.getRetryCount() : 0l;
				Long userRequestId = batch.getUserRequestId();
				// Extra Logic to create new job save retry job with time delay.
				if (isOriginalInvoiceNotFound && retryCount < 2) {
					Gstr1SaveToGstnDependencyRetryReqDto dependencyRetryDto = new Gstr1SaveToGstnDependencyRetryReqDto();

					dependencyRetryDto.setBatchId(batch.getId());
					dependencyRetryDto.setErrorCodes(OriginalInvoiceErrorCodes);
					dependencyRetryDto.setSection(batch.getSection());
					dependencyRetryDto.setRetryCount(++retryCount);
					dependencyRetryDto.setUserRequestId(userRequestId);
					dependencyRetryDto
							.setOperationType(SaveToGstnOprtnType.SAVE);
				dependencyRetryDto.setReturnType(APIConstants.GSTR1A.toUpperCase());

					Gson gson = GsonUtil.newSAPGsonInstance();

					String jsonParams = gson.toJson(dependencyRetryDto);
					Long delayOf = chunkSizeFetcher.getDelayOf();
					asyncJobsService.createJob(batch.getGroupCode(),
							JobConstants.GSTR1_DEPENDENCY_RETRY_SAVETOGSTN,
							jsonParams, APIConstants.SYSTEM.toUpperCase(), 1L,
							null, delayOf);
				} else {
					LOGGER.error(
							"GSTR1_DEPENDENCY_RETRY_SAVETOGSTN retry count "
									+ "exceeded still the Orinal invoices not saved");
				}

			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Pooling Gstr1 ReturnStatus- PE response handling completed for {} ",
						batch.getSection());
			}
		}
	}

	public LocalDate deserialize(String date) throws JsonParseException {
		return LocalDate.parse(date);
	}

	// private Pair<Integer, Set<String>> handleDeleteResponseRepo(
	// Map<String, OutwardTransDocErrorGstr1A> docErrorMap, Integer errorCount,
	// Gstr1SaveBatchEntity batch, boolean isOriginalInvoiceNotFound) {
	//
	// if (LOGGER.isDebugEnabled()) {
	// LOGGER.debug(
	// "Pooling Gstr1 ReturnStatus- response has error count of {} and with
	// error doc_keys are {} ",
	// errorCount, docErrorMap.keySet());
	// }
	// // Extra logic to handle to Invalid Cust GSTIN
	// LocalDateTime now = EYDateUtil
	// .toUTCDateTimeFromLocal(LocalDateTime.now());
	// LocalDate utcDateTimeFromLocal = EYDateUtil
	// .toUTCDateTimeFromLocal(LocalDate.now());
	// // List<Object[]> docIdKeysArr = new ArrayList<>();
	// List<Gstr1vsEinvReconRespProcessedEntity> headerDocs = new ArrayList<>();
	// // Set<Entry<String, OutwardTransDocErrorGstr1A>> entrySet =
	// // docErrorMap.entrySet();
	// // Extra map to avoid java.util.ConcurrentModificationException
	// Map<String, OutwardTransDocErrorGstr1A> docErrorMap1 = new HashMap<>();
	// for (Entry<String, OutwardTransDocErrorGstr1A> entry : docErrorMap
	// .entrySet()) {
	//
	// OutwardTransDocErrorGstr1A value = entry.getValue();
	// String errorCode = value.getErrorCode();
	// String errorMsg = value.getErrorDesc();
	//
	// if (CUST_GSTIN_ERROR_CODES.contains(errorCode)) {
	//
	// if (LOGGER.isDebugEnabled()) {
	// LOGGER.debug(
	// "Pooling Gstr1 ReturnStatus- response is invalid gstin with error-code {}
	// ",
	// errorCode);
	// }
	// String docKey = entry.getKey();
	// List<Gstr1vsEinvReconRespProcessedEntity> errorDocs = deleteRespRepo
	// .findDocsByDocKey(docKey);
	//
	// if (errorDocs != null && !errorDocs.isEmpty()) {
	// // At any given moment only one record exist with
	// // active docKey
	// Gstr1vsEinvReconRespProcessedEntity errorDoc = errorDocs
	// .get(0);
	// String cgstin = errorDoc.getCgstinEinv();
	// // Get the list of document id/doc key pairs for which we
	// // need to store the errors. This is done to get all
	// // the required doc ids/keys in a single stretch.
	// List<String> docKeysArrForInvalidGstins = deleteRespRepo
	// .findDocKeysByBatchIdAndCtin(batch.getId(), cgstin);
	// docKeysArrForInvalidGstins.stream().forEach(arr -> {
	// OutwardTransDocErrorGstr1A errObj = new OutwardTransDocErrorGstr1A();
	// errObj.setErrorCode(errorCode);
	// errObj.setErrorType(GSTConstants.ERR);
	// errObj.setSource(GSTConstants.GSTN_VALIDATION);
	// errObj.setErrorDesc(errorMsg);
	// errObj.setCreatedBy(GSTConstants.SYSTEM);
	// errObj.setCreatedDate(now);
	// errObj.setType(GSTConstants.BUSINESS_VALIDATIONS);
	// String key = String.valueOf(arr);
	// // Using the invoice key, get the error object
	// docErrorMap1.put(key, errObj);
	// });
	// }
	// }
	// }
	// docErrorMap.putAll(docErrorMap1);
	// errorCount = docErrorMap.size();
	// List<String> invKeySetAsList = new ArrayList<>(docErrorMap.keySet());
	// // Mark the successful documents with 'isSavedToGstin' as
	// // true.(i.e that are not present in the invoice key list).
	// autoDraftRepo.markDocsAsSavedForBatchByErroredDocKeys(batch.getId(),
	// invKeySetAsList);
	// if (LOGGER.isDebugEnabled()) {
	// LOGGER.debug(
	// "Pooling Gstr1 ReturnStatus- Only the Saved invoices of the batch {} is
	// updated as SAVED_TO_GSTN ",
	// batch.getId());
	// }
	// headerDocs = deleteRespRepo
	// .findByGstnBatchIdAndDocKeyEinvIn(batch.getId(), invKeySetAsList);
	// Set<String> OriginalInvoiceErrorCodes = new HashSet<>();
	// // List<OutwardTransDocErrorGstr1A> errorDocList = new ArrayList<>();
	// List<Gstr1vsEinvReconRespProcessedEntity> headerDocs1 = new
	// ArrayList<>();
	// // Attach the errors with the corresponding document header ids.
	// // The docIds array will contain an object array where first
	// // element is the document header id and the second element
	// // is the doc key.
	// for (Gstr1vsEinvReconRespProcessedEntity doc : headerDocs) {
	// String key = String.valueOf(doc.getDocKeyEinv());
	// // Using the invoice key, get the error object
	// OutwardTransDocErrorGstr1A error = docErrorMap.get(key);
	// /*
	// * error.setDocHeaderId(doc.getId());
	// * error.setSgstin(doc.getSgstin());
	// * error.setTaxperiod(doc.getReturnPeriod());
	// * error.setCreatedBy(APIConstants.SYSTEM);
	// * error.setCreatedDate(now); errorDocList.add(error);
	// */
	//
	// doc.setGstnError(true);
	// doc.setSaved(false);
	// doc.setSavedToGSTNDate(utcDateTimeFromLocal);
	// doc.setGstnErrorCode(error.getErrorCode());
	// doc.setGstnErrorDesc(error.getErrorDesc());
	// headerDocs1.add(doc);
	//
	// // Extra Logic to find the Original invoice not found Error.
	// if (ORIGINAL_INVOICE_ERROR_CODES.contains(error.getErrorCode())) {
	// isOriginalInvoiceNotFound = true;
	// OriginalInvoiceErrorCodes.add(error.getErrorCode());
	// }
	// }
	//
	// if (headerDocs != null && !headerDocs.isEmpty()) {
	// deleteRespRepo.saveAll(headerDocs1);
	// // Now save the list of errors to the DB. Now, this error
	// // doc list will have the doc ids populated.
	// // docErrorRepository.saveAll(errorDocList);
	// }
	// if (LOGGER.isDebugEnabled()) {
	// LOGGER.debug(
	// "Pooling Gstr1 ReturnStatus- batch table is updating with batchId {},
	// status {}, errorcount {} ",
	// batch.getId(), PE, errorCount);
	// }
	// return new Pair<Integer, Set<String>>(errorCount,
	// OriginalInvoiceErrorCodes);
	// }

	// private Pair<Integer, Set<String>> handleAutoDraftRepo(
	// Map<String, OutwardTransDocErrorGstr1A> docErrorMap, Integer errorCount,
	// Gstr1SaveBatchEntity batch, boolean isOriginalInvoiceNotFound) {
	//
	// if (LOGGER.isDebugEnabled()) {
	// LOGGER.debug(
	// "Pooling Gstr1 ReturnStatus- response has error count of {} and with
	// error doc_keys are {} ",
	// errorCount, docErrorMap.keySet());
	// }
	// // Extra logic to handle to Invalid Cust GSTIN
	// LocalDateTime now = EYDateUtil
	// .toUTCDateTimeFromLocal(LocalDateTime.now());
	// LocalDate utcDateTimeFromLocal = EYDateUtil
	// .toUTCDateTimeFromLocal(LocalDate.now());
	//
	// // Extra map to avoid java.util.ConcurrentModificationException
	// Map<String, OutwardTransDocErrorGstr1A> docErrorMap1 = new HashMap<>();
	// for (Entry<String, OutwardTransDocErrorGstr1A> entry : docErrorMap
	// .entrySet()) {
	//
	// OutwardTransDocErrorGstr1A value = entry.getValue();
	// String errorCode = value.getErrorCode();
	// String errorMsg = value.getErrorDesc();
	//
	// if (CUST_GSTIN_ERROR_CODES.contains(errorCode)) {
	//
	// if (LOGGER.isDebugEnabled()) {
	// LOGGER.debug(
	// "Pooling Gstr1 ReturnStatus- response is invalid gstin with error-code {}
	// ",
	// errorCode);
	// }
	// String docKey = entry.getKey();
	// List<Gstr1GSTINDeleteDataEntity> errorDocs = autoDraftRepo
	// .findDocsByDocKey(docKey);
	//
	// if (errorDocs != null && !errorDocs.isEmpty()) {
	// // At any given moment only one record exist with
	// // active docKey
	// Gstr1GSTINDeleteDataEntity errorDoc = errorDocs.get(0);
	// String cgstin = errorDoc.getCgstin();
	// // Get the list of document id/doc key pairs for which we
	// // need to store the errors. This is done to get all
	// // the required doc ids/keys in a single stretch.
	// List<String> docKeysArrForInvalidGstins = autoDraftRepo
	// .findDocKeysByBatchIdAndCtin(batch.getId(), cgstin);
	// docKeysArrForInvalidGstins.stream().forEach(arr -> {
	// OutwardTransDocErrorGstr1A errObj = new OutwardTransDocErrorGstr1A();
	// errObj.setErrorCode(errorCode);
	// errObj.setErrorType(GSTConstants.ERR);
	// errObj.setSource(GSTConstants.GSTN_VALIDATION);
	// errObj.setErrorDesc(errorMsg);
	// errObj.setCreatedBy(GSTConstants.SYSTEM);
	// errObj.setCreatedDate(now);
	// errObj.setType(GSTConstants.BUSINESS_VALIDATIONS);
	// String key = String.valueOf(arr);
	// // Using the invoice key, get the error object
	// docErrorMap1.put(key, errObj);
	// });
	// }
	// }
	// }
	// docErrorMap.putAll(docErrorMap1);
	// errorCount = docErrorMap.size();
	// List<String> invKeySetAsList = new ArrayList<>(docErrorMap.keySet());
	// // Mark the successful documents with 'isSavedToGstin' as
	// // true.(i.e that are not present in the invoice key list).
	//// autoDraftRepo.markDocsAsSavedForBatchByErroredDocKeys(
	//// batch.getId(), invKeySetAsList);
	// if (LOGGER.isDebugEnabled()) {
	// LOGGER.debug(
	// "Pooling Gstr1 ReturnStatus- Only the Saved invoices of the batch {} is
	// updated as SAVED_TO_GSTN ",
	// batch.getId());
	// }
	//// headerDocs = autoDraftRepo
	//// .findByGstnBatchIdAndDocKeyIn(batch.getId(), invKeySetAsList);
	// Set<String> OriginalInvoiceErrorCodes = new HashSet<>();
	// //List<OutwardTransDocErrorGstr1A> errorDocList = new ArrayList<>();
	// List<Gstr1GSTINDeleteDataEntity> headerDocs1 = new ArrayList<>();
	// // Attach the errors with the corresponding document header ids.
	// // The docIds array will contain an object array where first
	// // element is the document header id and the second element
	// // is the doc key.
	// for (Gstr1GSTINDeleteDataEntity doc : headerDocs) {
	// String key = String.valueOf(doc.getDocKey());
	// // Using the invoice key, get the error object
	// OutwardTransDocErrorGstr1A error = docErrorMap.get(key);
	// /*error.setDocHeaderId(doc.getId());
	// error.setSgstin(doc.getSgstin());
	// error.setTaxperiod(doc.getReturnPeriod());
	// error.setCreatedBy(APIConstants.SYSTEM);
	// error.setCreatedDate(now);
	// errorDocList.add(error);*/
	//
	// doc.setGstnError(true);
	// doc.setSaved(false);
	// doc.setSavedToGSTNDate(utcDateTimeFromLocal);
	// doc.setGstnErrorCode(error.getErrorCode());
	// doc.setGstnErrorDesc(error.getErrorDesc());
	// headerDocs1.add(doc);
	//
	// // Extra Logic to find the Original invoice not found Error.
	// if (ORIGINAL_INVOICE_ERROR_CODES.contains(error.getErrorCode())) {
	// isOriginalInvoiceNotFound = true;
	// OriginalInvoiceErrorCodes.add(error.getErrorCode());
	// }
	// }
	//
	// if (headerDocs != null && !headerDocs.isEmpty()) {
	//// autoDraftRepo.saveAll(headerDocs1);
	// // Now save the list of errors to the DB. Now, this error
	// // doc list will have the doc ids populated.
	// //docErrorRepository.saveAll(errorDocList);
	// }
	// if (LOGGER.isDebugEnabled()) {
	// LOGGER.debug(
	// "Pooling Gstr1 ReturnStatus- batch table is updating with batchId {},
	// status {}, errorcount {} ",
	// batch.getId(), PE, errorCount);
	// }
	// return new Pair<Integer,
	// Set<String>>(errorCount,OriginalInvoiceErrorCodes);
	// }
	//

	private Pair<Integer, Set<String>> handleDocRepo(
			Map<String, OutwardTransDocErrorGstr1A> docErrorMap, Integer errorCount,
			Gstr1SaveBatchEntity batch, boolean isOriginalInvoiceNotFound) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Pooling Gstr1 ReturnStatus- response has error count of {} and with  error doc_keys are {} ",
					errorCount, docErrorMap.keySet());
		}
		// Extra logic to handle to Invalid Cust GSTIN
		LocalDateTime now = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());
		LocalDate utcDateTimeFromLocal = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDate.now());
		// List<Object[]> docIdKeysArr = new ArrayList<>();
		List<Gstr1AOutwardTransDocument> headerDocs = new ArrayList<>();
		// Set<Entry<String, OutwardTransDocErrorGstr1A>> entrySet =
		// docErrorMap.entrySet();
		// Extra map to avoid java.util.ConcurrentModificationException
		Map<String, OutwardTransDocErrorGstr1A> docErrorMap1 = new HashMap<>();
		for (Entry<String, OutwardTransDocErrorGstr1A> entry : docErrorMap
				.entrySet()) {

			OutwardTransDocErrorGstr1A value = entry.getValue();
			String errorCode = value.getErrorCode();
			String errorMsg = value.getErrorDesc();

			if (CUST_GSTIN_ERROR_CODES.contains(errorCode)) {

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Pooling Gstr1 ReturnStatus- response is invalid gstin with error-code {} ",
							errorCode);
				}
				String docKey = entry.getKey();
				List<Gstr1AOutwardTransDocument> errorDocs = outwardTransDocumentRepository
						.findDocsByDocKey(docKey);

				if (errorDocs != null && !errorDocs.isEmpty()) {
					// At any given moment only one record exist with
					// active docKey
					Gstr1AOutwardTransDocument errorDoc = errorDocs.get(0);
					String cgstin = errorDoc.getCgstin();
					// Get the list of document id/doc key pairs for which we
					// need to store the errors. This is done to get all
					// the required doc ids/keys in a single stretch.
					List<String> docKeysArrForInvalidGstins = outwardTransDocumentRepository
							.findDocKeysByBatchIdAndCtin(batch.getId(), cgstin);
					docKeysArrForInvalidGstins.stream().forEach(arr -> {
						OutwardTransDocErrorGstr1A errObj = new OutwardTransDocErrorGstr1A();
						errObj.setErrorCode(errorCode);
						errObj.setErrorType(GSTConstants.ERR);
						errObj.setSource(GSTConstants.GSTN_VALIDATION);
						errObj.setErrorDesc(errorMsg);
						errObj.setCreatedBy(GSTConstants.SYSTEM);
						errObj.setCreatedDate(now);
						errObj.setType(GSTConstants.BUSINESS_VALIDATIONS);
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
		// Mark the successful documents with 'isSavedToGstin' as
		// true.(i.e that are not present in the invoice key list).
		outwardTransDocumentRepository.markDocsAsSavedForBatchByErroredDocKeys(
				batch.getId(), invKeySetAsList, now);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Pooling Gstr1 ReturnStatus- Only the Saved invoices of the batch {} is updated as SAVED_TO_GSTN ",
					batch.getId());
		}
		headerDocs = outwardTransDocumentRepository
				.findByGstnBatchIdAndDocKeyIn(batch.getId(), invKeySetAsList);
		Set<String> OriginalInvoiceErrorCodes = new HashSet<>();
		List<OutwardTransDocErrorGstr1A> errorDocList = new ArrayList<>();
		List<Gstr1AOutwardTransDocument> headerDocs1 = new ArrayList<>();
		// Attach the errors with the corresponding document header ids.
		// The docIds array will contain an object array where first
		// element is the document header id and the second element
		// is the doc key.
		for (Gstr1AOutwardTransDocument doc : headerDocs) {
			String key = String.valueOf(doc.getDocKey());
			// Using the invoice key, get the error object
			OutwardTransDocErrorGstr1A error = docErrorMap.get(key);
			error.setDocHeaderId(doc.getId());
			error.setSgstin(doc.getSgstin());
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
			headerDocs1.add(doc);

			// Extra Logic to find the Original invoice not found Error.
			if (ORIGINAL_INVOICE_ERROR_CODES.contains(error.getErrorCode())) {
				isOriginalInvoiceNotFound = true;
				OriginalInvoiceErrorCodes.add(error.getErrorCode());
			}
		}
		if (headerDocs != null && !headerDocs.isEmpty()) {
			outwardTransDocumentRepository.saveAll(headerDocs1);
			// Now save the list of errors to the DB. Now, this error
			// doc list will have the doc ids populated.
			docErrorRepository.saveAll(errorDocList);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Pooling Gstr1 ReturnStatus- batch table is updating with batchId {}, status {}, errorcount {} ",
					batch.getId(), PE, errorCount);
		}
		return new Pair<Integer, Set<String>>(errorCount,
				OriginalInvoiceErrorCodes);
	}
}
