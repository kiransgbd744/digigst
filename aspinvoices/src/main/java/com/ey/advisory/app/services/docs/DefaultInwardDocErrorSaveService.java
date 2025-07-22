package com.ey.advisory.app.services.docs;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.Anx2InwardErrorHeaderEntity;
import com.ey.advisory.app.data.entities.client.Anx2InwardErrorItemEntity;
import com.ey.advisory.app.data.entities.client.InwardTransDocError;
import com.ey.advisory.app.data.repositories.client.Anx2InwardErrHeaderRepository;
import com.ey.advisory.app.docs.dto.gstr2.InwardDocSvErrSaveRespDto;
import com.ey.advisory.app.services.docs.gstr2.InwardDocSvErrSaveResp;
import com.ey.advisory.app.services.validation.purchase.PurchaseDocRulesValidatorService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.PerfamanceEventConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.TenantContext;

import lombok.extern.slf4j.Slf4j;

@Service("DefaultInwardDocErrorSaveService")
@Slf4j
public class DefaultInwardDocErrorSaveService
		implements InwardDocErrorSaveService {

	@Autowired
	@Qualifier("Anx2InwardErrHeaderRepository")
	private Anx2InwardErrHeaderRepository anx2InwardErrHeaderRepository;

	@Autowired
	@Qualifier("PurchaseDocRulesValidatorService")
	private PurchaseDocRulesValidatorService purchaseDocRulesValidatorService;

	@Autowired
	@Qualifier("DefaultInwardTransDocErrorKeyGenerator")
	private DocKeyGenerator<Anx2InwardErrorHeaderEntity, String> docKeyGen;

	@Autowired
	@Qualifier("InwardDocSvErrSaveResp")
	private InwardDocSvErrSaveResp inwardSvErrDocSaveResp;

	private static final String CLASS_NAME = "DefaultInwardDocErrorSaveService";
	private static final String METHOD_NAME = "saveErrorRecord";

	public List<InwardDocSvErrSaveRespDto> saveErrorRecord(
			Map<String, List<ProcessingResult>> processingResults,
			List<Anx2InwardErrorHeaderEntity> errorDocument) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info(
					"DefaultInwardDocErrorSaveService saveErrorRecord begining");
		}
		List<InwardDocSvErrSaveRespDto> docSaveRespDtos = new ArrayList<>();
		try {

			// TODO: to take the groupCode from a thread local variable.
			String groupCode = TenantContext.getTenantId();

			Map<String, Anx2InwardErrorHeaderEntity> errDocMap = new HashMap<>();
			if (!processingResults.isEmpty()) {
				errorDocument.forEach(errDoc -> {
					String docKey = docKeyGen.generateKey(errDoc);
					errDoc.setIsError("true");
					errDoc.setIsProcessed("false");
					errDocMap.put(docKey, errDoc);
					populateErrorCodesAndDescription(processingResults, docKey,
							errDoc);
				});
			}

			// TODO: to take the groupCode from a thread local variable.
			ProcessingContext context = new ProcessingContext();
			context.seAttribute("groupCode", groupCode);

			// Keep the list of errors ready.
			Map<String, List<InwardTransDocError>> errorMap = convertErrors(
					processingResults, errDocMap);

			// Get the list of document ids for the existing documents and keep
			// it aside, so that we can use this to populate the 'oldDocId'
			// value
			// while creating the response.
			Stream<Long> docIdStream = errorDocument.stream()
					.map(doc -> doc.getId());
			List<Long> oldDocIds = docIdStream.filter(x -> x != null)
					.collect(Collectors.toList());
			/**
			 * Start - Document Structural Validation Error Correction. Below
			 * code is for updating the existing document and is executed in
			 * case of document Structural Validation Error correction
			 * 
			 */
			// Get all the non-null ids from the list. This will be used to
			// mark the existing documents in the DB as deleted.
			List<Long> docIds = errorDocument.stream()
					.filter(doc -> doc.getId() != null).map(doc -> doc.getId())
					.collect(Collectors.toList());

			// Execute the repository query to udpate the isDelete to true for
			// the above ids. Also mark the updated date to the current date.
			// call the is delete to true repository method.
			if (!docIds.isEmpty()) {
				LocalDateTime updatedDate = LocalDateTime.now();
				anx2InwardErrHeaderRepository.updateDocDeletion(docIds,
						updatedDate);
			}
			/**
			 * END - Document Structural Validation Error Correction
			 */

			// explictly set the documeent ids to null in the input collection
			// to be saved.
			errorDocument.forEach(doc -> doc.setId(null));

			// Save the list of documents.
			errorDocument.forEach(errDoc -> {
				if (null == errDoc.getId()) {
					errDoc.setCreatedDate(LocalDateTime.now());
				}
			});
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"STRUCTURAL_ERROR_SAVE_START", CLASS_NAME, METHOD_NAME,
					null);
			List<Anx2InwardErrorHeaderEntity> anx2InwardErrorHeaderEntity = anx2InwardErrHeaderRepository
					.saveAll(errorDocument);
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"STRUCTURAL_ERROR_SAVE_END", CLASS_NAME, METHOD_NAME, null);
			// Map the Ids of the saved documents to the errors associated with
			// it.
			anx2InwardErrorHeaderEntity.forEach(doc -> {

				String docKey = docKeyGen.generateKey(doc);
				int maxlength = 99;
				if (docKey.length() > maxlength) {
					docKey = docKey.substring(0, maxlength);
				}

				List<InwardTransDocError> errList = errorMap.get(docKey);
				Long id = doc.getId();
				if (errList != null && !errList.isEmpty()) {
					errList.forEach(err -> {
						err.setDocHeaderId(id);
						err.setTaxperiod(doc.getTaxperiod());
						err.setDerivedTaxperiod(doc.getDerivedTaxperiod());
						err.setCgstin(doc.getCgstin());
						err.setAcceptanceId(doc.getAcceptanceId());
					});
				}
			});

			// Add all the errors into a single list to save to the DB.
			List<InwardTransDocError> inError = new ArrayList<>();
			errorMap.entrySet().forEach(e -> {
				List<InwardTransDocError> errorList = e.getValue();
				errorList.forEach(error -> {
					if (error.getDocHeaderId() != null) {
						inError.add(error);
					}
				});
			});
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"STRUCTURAL_ERROR_SAVE_END", CLASS_NAME, METHOD_NAME, null);
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"STRUCTURAL_ERROR_SAVE_END", CLASS_NAME, METHOD_NAME, null);
			docSaveRespDtos = inwardSvErrDocSaveResp
					.createInwardDocSvErrSaveResponse(oldDocIds,
							anx2InwardErrorHeaderEntity, inError,
							docSaveRespDtos);
		} catch (Exception e) {

			LOGGER.error("ExceptionOccured:{} ", e);
			throw new AppException("Exception while saving the records");
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("DefaultInwardDocErrorSaveService saveErrorRecord End");
		}

		return docSaveRespDtos;
	}

	private Map<String, List<InwardTransDocError>> convertErrors(
			Map<String, List<ProcessingResult>> results,
			Map<String, Anx2InwardErrorHeaderEntity> errDocMap) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Anx2RawFileToInwardTransDocErrConvertion convertErrors Begining");
		}
		Map<String, List<InwardTransDocError>> map = new HashMap<>();
		results.keySet().stream().forEach(key -> {
			List<ProcessingResult> pResults = results.get(key);
			List<InwardTransDocError> errors = new ArrayList<>();
			Anx2InwardErrorHeaderEntity errDoc = errDocMap.get(key);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("About to Search for dockey {} in errDocMap ",
						key);
			pResults.forEach(pr -> {
				// Instantiate the ent
				InwardTransDocError error = new InwardTransDocError();
				TransDocProcessingResultLoc loc = (TransDocProcessingResultLoc) pr
						.getLocation();
				if (null != loc) { // In case of bifurcation failure, loc is
									// null
					Object[] arr = loc.getFieldIdentifiers();
					String[] fields = Arrays.copyOf(arr, arr.length,
							String[].class);
					String errField = StringUtils.join(fields, ',');
					error.setItemIndex(loc.getLineNo());
					if (loc.getLineNo() != null) {
						Integer lineNo = loc.getLineNo();
						Integer itemNo = errDoc.getItemNoForIndex(lineNo);
						error.setItemNum(itemNo);

					}
					error.setErrorField(errField);
				}
				error.setErrorCode(pr.getCode());
				error.setErrorDesc(pr.getDescription());

				error.setErrorType(ProcessingResultType.ERROR == pr.getType()
						? "ERR" : "INFO");
				error.setSource("ASP");
				error.setValType(GSTConstants.STRUCTURAL_VALIDATIONS);
				error.setCreatedBy(null);
				error.setCreatedDate(LocalDateTime.now());
				errors.add(error);
			});
			map.put(key, errors);
		});
		if (LOGGER.isDebugEnabled()) {
			LOGGER.error(
					"Anx2RawFileToInwardTransDocErrConvertion convertErrors End");
		}
		return map;
	}

	private void populateErrorCodesAndDescription(
			Map<String, List<ProcessingResult>> processingResults,
			String docKey, Anx2InwardErrorHeaderEntity errDoc) {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("About to Search for dockey {} in Processing Results",
					docKey);
		List<ProcessingResult> errors = processingResults.get(docKey);
		List<String> headerList = new ArrayList<>();
		Map<Integer, List<String>> aitemErrorMap = new HashMap<>();

		for (ProcessingResult error : errors) {
			TransDocProcessingResultLoc loc = (TransDocProcessingResultLoc) error
					.getLocation();
			Integer lineNo = loc.getLineNo();
			if (lineNo == null)
				headerList.add(error.getCode());
			else
				aitemErrorMap.computeIfAbsent(lineNo, k -> new ArrayList<>())
						.add(error.getCode());
		}
		if (!headerList.isEmpty()) {
			errDoc.setErrCodes(
					headerList.stream().collect(Collectors.joining(",")));
		}
		if (aitemErrorMap.size() > 0) {
			IntStream.range(0, errDoc.getLineItems().size()).forEach(idx -> {
				Anx2InwardErrorItemEntity item = errDoc.getLineItems().get(idx);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("map {}, index {}", aitemErrorMap, idx);
				}
				if (aitemErrorMap.get(idx) != null) {
					item.setErrCodes(aitemErrorMap.get(idx).stream()
							.collect(Collectors.joining(",")));
				}
			});

		}

	}
}
