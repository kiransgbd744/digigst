/**
 * 
 */
package com.ey.advisory.app.services.itc04;

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

import com.ey.advisory.app.data.entities.client.Itc04HeaderEntity;
import com.ey.advisory.app.data.entities.client.Itc04ItemEntity;
import com.ey.advisory.app.docs.dto.Itc04DocDetailsSaveRespDto;
import com.ey.advisory.app.docs.dto.Itc04DocErrorDetailsDto;
import com.ey.advisory.app.docs.dto.Itc04DocSaveRespDto;
import com.ey.advisory.app.services.docs.DocKeyGenerator;
import com.ey.advisory.app.services.validation.DocRulesValidationResult;
import com.ey.advisory.app.services.validation.itc04.Itc04DocRulesValidatorService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.ErrorMasterUtil;
import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.PerfamanceEventConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;
import com.ey.advisory.common.TransDocProcessingResultLoc;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Laxmi.Salukuti
 *
 */
@Service("Itc04DefaultDocSaveService")
@Slf4j
public class Itc04DefaultDocSaveService implements Itc04DocSaveService {

	
	@Autowired
	@Qualifier("Itc04DocRulesValidatorService")
	private Itc04DocRulesValidatorService itc04DocRulesValidatorService;

	@Autowired
	private DocKeyGenerator<Itc04HeaderEntity, String> docKeyGen;

	@Autowired
	@Qualifier("Itc04DocSave")
	private Itc04DocSave itc04DocSave;

	@Autowired
	@Qualifier("Itc04CanDocCheckServiceImpl")
	private Itc04OriginalDocCheckService itc04OriginalDocCheckService;

	@Autowired
	@Qualifier("Itc04DocSaveResp")
	private Itc04DocSaveResp itc04DocSaveResp;

	private static final String CLASS_NAME = "Itc04DefaultDocSaveService";
	private static final String METHOD_NAME = "saveItc04Documents";

	@Override
	public Itc04DocSaveRespDto saveItc04Documents(
			List<Itc04HeaderEntity> documents) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Itc04DefaultDocSaveService saveDocuments Begining");
		}
		Itc04DocSaveRespDto finalRespDto = new Itc04DocSaveRespDto();
		try {
			// Take the groupCode from a thread local variable.
			ProcessingContext context = new ProcessingContext();

			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"CONVERT_CALC_SETVALUES_START", CLASS_NAME, METHOD_NAME,
					null);
			itc04DocSave.convertCalcAndSetValues(documents);

			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"CONVERT_CALC_SETVALUES_END", CLASS_NAME, METHOD_NAME,
					null);

			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"BUSINESS_ERROR_START", CLASS_NAME, METHOD_NAME, null);
			DocRulesValidationResult<String> valResult = itc04DocRulesValidatorService
					.validate(documents, context);

			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"BUSINESS_ERROR_END", CLASS_NAME, METHOD_NAME, null);
			Map<String, List<ProcessingResult>> processingResults = valResult
					.getProcessingResults();

			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"ORIGINAL_DOC_SEARCH_START", CLASS_NAME, METHOD_NAME, null);
			Map<String, List<ProcessingResult>> canChkprocessingResults = itc04OriginalDocCheckService
					.checkForItc04OrgInvoices(documents);

			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"ORIGINAL_DOC_SEARCH_END", CLASS_NAME, METHOD_NAME, null);
			canChkprocessingResults
					.forEach((key, value) -> processingResults.merge(key, value,
							(v1, v2) -> Stream.of(v1, v2)
									.flatMap(x -> x.stream()).collect(
											Collectors.toList())));

			// This is for locating the error

			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"IS_PROCESSED_IS_ERROR_CODE_SETTING_START", CLASS_NAME,
					METHOD_NAME, null);

			Map<String, Itc04HeaderEntity> errDocMap = new HashMap<>();
			// For each document with errors, set the isError to true.
			if (!processingResults.isEmpty()) {
				for (Itc04HeaderEntity doc : documents) {
					String docKey = docKeyGen.generateKey(doc);
					// if (valResult.hasErrors(docKey)) {
					if (isError(processingResults, docKey)) {
						doc.setProcessed(false);
						doc.setError(true);
						errDocMap.put(docKey, doc);
					} else {
						// If there are no errors, then set the isProcessed to
						// true
						// and isError to false
						doc.setProcessed(true);
						doc.setError(false);
					}
					// if (valResult.hasInfo(docKeyGen.generateKey(doc))) {
					if (isInfo(processingResults, docKey)) {
						doc.setInfo(true);
						errDocMap.put(docKey, doc);
					}

					if (isError(processingResults, docKey)
							|| isInfo(processingResults, docKey)) {
						populateErrorCodeAndErrorDescription(processingResults,
								docKey, doc);
					}
				}
			} else {
				for (Itc04HeaderEntity doc : documents) {
					doc.setProcessed(true);
					doc.setError(false);

				}
			}
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"IS_PROCESSED_IS_ERROR_CODE_SETTING_END", CLASS_NAME,
					METHOD_NAME, null);

			// Keep the list of errors ready.

			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"SAVE_DOC_START", CLASS_NAME, METHOD_NAME, null);

			List<Itc04HeaderEntity> savedDocs = itc04DocSave.saveDocs(documents,
					docKeyGen);

			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"SAVE_DOC_END", CLASS_NAME, METHOD_NAME, null);

			List<Itc04DocErrorDetailsDto> outError = new ArrayList<>();
			if (savedDocs.get(0).getDataOriginTypeCode()
					.equalsIgnoreCase("A")) {
				Map<String, List<Itc04DocErrorDetailsDto>> errorMap = convertErrors(
						processingResults, errDocMap);

				// Get the list of document ids for hte existing documetns and
				// keep
				// it aside, so that we can use this to populate the 'oldDocId'
				// value
				// while creating the response.
				Stream<Long> docIdStream = documents.stream()
						.map(doc -> doc.getId());
				List<Long> oldDocIds = docIdStream.filter(x -> x != null)
						.collect(Collectors.toList());
				errorMap.entrySet().forEach(e -> {
					List<Itc04DocErrorDetailsDto> errorList = e.getValue();
					errorList.forEach(error -> {
						// if (error.getDocHeaderId() != null) {
						outError.add(error);
						// }
					});
				});

				PerfUtil.logEventToFile(
						PerfamanceEventConstants.FILE_PROCESSING,
						"CREATEITC04_SAVEDOC_API_RESP_START", CLASS_NAME,
						METHOD_NAME, null);

				List<Itc04DocDetailsSaveRespDto> docSaveRespDtos = itc04DocSaveResp
						.createItc04SaveAPIResponse(oldDocIds, savedDocs,
								errorMap);

				finalRespDto.setSavedDocsResp(docSaveRespDtos);

				PerfUtil.logEventToFile(
						PerfamanceEventConstants.FILE_PROCESSING,
						"CREATEITC04_SAVEDOC_API_RESP_END", CLASS_NAME,
						METHOD_NAME, null);
			}
			// finalRespDto.setProcessingResults(processingResults);
		} catch (Exception e) {
			LOGGER.error("Save Docs : Exception Occured:{} ", e);
			throw new AppException("Exception while saving the documents ",
					e.getMessage());
		}
		return finalRespDto;
	}

	private void populateErrorCodeAndErrorDescription(
			Map<String, List<ProcessingResult>> processingResults,
			String docKey, Itc04HeaderEntity doc) {
		List<String> HeaderErrorList = new ArrayList<>();
		List<String> HeaderInfoList = new ArrayList<>();
		List<ProcessingResult> errors = processingResults.get(docKey);
		Map<Integer, List<String>> aitemErrorMap = new HashMap<>();
		Map<Integer, List<String>> aiteInfoMap = new HashMap<>();
		for (ProcessingResult error : errors) {

			TransDocProcessingResultLoc loc = (TransDocProcessingResultLoc) error
					.getLocation();
			if (loc == null) {
				if (ProcessingResultType.INFO.equals(error.getType())) {
					HeaderInfoList.add(error.getCode());
				} else {
					HeaderErrorList.add(error.getCode());
				}
			}

			else {
				Integer lineNo = loc.getLineNo();
				if (lineNo == null) {
					if (ProcessingResultType.INFO.equals(error.getType())) {
						HeaderInfoList.add(error.getCode());
					} else {
						HeaderErrorList.add(error.getCode());
					}
				} else {
					if (ProcessingResultType.INFO.equals(error.getType())) {
						aiteInfoMap
								.computeIfAbsent(lineNo, k -> new ArrayList<>())
								.add(error.getCode());
					} else {
						aitemErrorMap
								.computeIfAbsent(lineNo, k -> new ArrayList<>())
								.add(error.getCode());
					}
				}
			}
		}
		if (HeaderErrorList != null && HeaderErrorList.size() > 0) {
			doc.setErrCodes(
					HeaderErrorList.stream().collect(Collectors.joining(",")));
		}
		if (HeaderInfoList != null && HeaderInfoList.size() > 0) {
			doc.setInfoCodes(
					HeaderInfoList.stream().collect(Collectors.joining(",")));
		}
		if (aitemErrorMap != null && aitemErrorMap.size() > 0) {
			IntStream.range(0, doc.getLineItems().size()).forEach(idx -> {
				Itc04ItemEntity item = doc.getLineItems().get(idx);
				if (aitemErrorMap.get(idx) != null) {
					item.setErrCodes(aitemErrorMap.get(idx).stream()
							.collect(Collectors.joining(",")));
				}
			});
		}

		if (aiteInfoMap != null && aiteInfoMap.size() > 0) {
			IntStream.range(0, doc.getLineItems().size()).forEach(idx -> {
				Itc04ItemEntity item = doc.getLineItems().get(idx);
				if (aiteInfoMap.get(idx) != null) {
					item.setInfoCodes(aiteInfoMap.get(idx).stream()
							.collect(Collectors.joining(",")));
				}
			});
		}
	}

	private boolean isError(
			Map<String, List<ProcessingResult>> processingResults,
			String docKey) {

		return processingResults.get(docKey).stream()
				.anyMatch(r -> r.getType() == ProcessingResultType.ERROR);
	}

	private boolean isInfo(
			Map<String, List<ProcessingResult>> processingResults,
			String docKey) {

		return processingResults.get(docKey).stream()
				.anyMatch(r -> r.getType() == ProcessingResultType.INFO);
	}

	private Map<String, List<Itc04DocErrorDetailsDto>> convertErrors(
			Map<String, List<ProcessingResult>> results,
			Map<String, Itc04HeaderEntity> errDocMap) {
		Map<String, List<Itc04DocErrorDetailsDto>> map = new HashMap<>();
		results.keySet().stream().forEach(key -> {
			List<ProcessingResult> pResults = results.get(key);
			Itc04HeaderEntity itc04Document = errDocMap.get(key);

			List<Itc04DocErrorDetailsDto> errors = new ArrayList<>();
			pResults.forEach(pr -> {
				// Instantiate the ent
				Itc04DocErrorDetailsDto error = new Itc04DocErrorDetailsDto();
				TransDocProcessingResultLoc loc = (TransDocProcessingResultLoc) pr
						.getLocation();
				if (null != loc) { // In case of bifurcation failure, loc is
									// null
					Object[] arr = loc.getFieldIdentifiers();
					if (arr != null) {
						String[] fields = Arrays.copyOf(arr, arr.length,
								String[].class);
						String errField = StringUtils.join(fields, ',');
						error.setErrorFields(errField);
					}
				}
				String errorCode = pr.getCode();
				error.setErrorCode(errorCode);
				String errDesc = ErrorMasterUtil.findErrDescByErrCode(errorCode,
						"ITC04");
				error.setErrorDesc(errDesc);
				error.setErrorType(ProcessingResultType.ERROR == pr.getType()
						? "ERR" : "INFO");
				errors.add(error);
			});
			map.put(key, errors);
		});
		return map;
	}

}