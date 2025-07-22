package com.ey.advisory.app.services.doc.gstr1a;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.Anx1OutWardErrHeader;
import com.ey.advisory.app.data.entities.client.OutwardTransDocError;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAnx1OutWardErrHeader;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAnxOutwardTransDocLineItemError;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AAnx1OutWardErrHeaderRepository;
import com.ey.advisory.app.docs.dto.OutwardDocSvErrSaveRespDto;
import com.ey.advisory.app.services.docs.DocKeyGenerator;
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

@Service("DefaultGstr1ADocErrorSaveService")
@Slf4j
public class DefaultGstr1ADocErrorSaveService
		implements Gstr1ADocErrorSaveService {

	/*
	 * @Autowired
	 * 
	 * @Qualifier("DocErrorRepository") private DocErrorRepository
	 * docErrorRepository;
	 */

	@Autowired
	@Qualifier("Gstr1AAnx1OutWardErrHeaderRepository")
	private Gstr1AAnx1OutWardErrHeaderRepository anx1OutWardErrHeaderRepository;

	@Autowired
	@Qualifier("Gstr1ASalesDocRulesValidatorService")
	private Gstr1ASalesDocRulesValidatorService salesDocRulesValidatorService;

	@Autowired
	private DocKeyGenerator<Gstr1AAnx1OutWardErrHeader, String> docKeyGen;

	@Autowired
	@Qualifier("Gstr1AOutwardDocSvErrSaveResp")
	private Gstr1AOutwardDocSvErrSaveResp outwardDocSvErrSaveResp;

	private static final String CLASS_NAME = "DefaultDocErrorSaveService";
	private static final String METHOD_NAME = "saveErrorRecord";

	@Override
	public void saveErrorRecord(
			Map<String, List<ProcessingResult>> processingResults,
			List<Gstr1AAnx1OutWardErrHeader> errorDocument) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("DefaultDocErrorSaveService saveErrorRecord begining");
		}
		List<OutwardDocSvErrSaveRespDto> docSaveRespDto = new ArrayList<>();
		try {
			String groupCode = TenantContext.getTenantId();
			Map<String, Gstr1AAnx1OutWardErrHeader> errDocMap = new HashMap<>();
			if (!processingResults.isEmpty()) {
				errorDocument.forEach(errDoc -> {
					String docKey = docKeyGen.generateKey(errDoc);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"About to Mark the Document ERROR as it is"
										+ " Structurally Errored, DocKey is {}",
								docKey);
					}
					errDoc.setIsError("true");
					errDoc.setIsProcessed("false");
					errDocMap.put(docKey, errDoc);
					populateErrorCodesAndDescription(processingResults, docKey,
							errDoc);
				});
			}
			ProcessingContext context = new ProcessingContext();
			context.seAttribute("groupCode", groupCode);

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
				anx1OutWardErrHeaderRepository.updateDocDeletion(docIds,
						updatedDate);
			}

			// Save the list of documents.
			errorDocument.forEach(errDoc -> {
				if (null == errDoc.getId()) {
					errDoc.setCreatedDate(LocalDateTime.now());
				}
			});
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"STRUCTURAL_ERROR_SAVE_START", CLASS_NAME, METHOD_NAME,
					null);
			
			if(LOGGER.isDebugEnabled())
			{
			LOGGER.debug(" errorDocument {} ",errorDocument);
			}
			List<Gstr1AAnx1OutWardErrHeader> anx1OutWardErrHeaders = anx1OutWardErrHeaderRepository
					.saveAll(errorDocument);
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"STRUCTURAL_ERROR_SAVE_END", CLASS_NAME, METHOD_NAME, null);

		} catch (Exception e) {
			LOGGER.error("ExceptionOccured:{} ", e);
			throw new AppException("Exception while saving the records");
		}

	}

	private Map<String, List<OutwardTransDocError>> convertErrors(
			Map<String, List<ProcessingResult>> results,
			Map<String, Anx1OutWardErrHeader> errDocMap) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"SRFileToOutwardTransDocErrConvertion convertErrors Begining");
		}
		Map<String, List<OutwardTransDocError>> map = new HashMap<>();

		results.keySet().stream().forEach(key -> {
			List<ProcessingResult> pResults = results.get(key);
			List<OutwardTransDocError> errors = new ArrayList<>();
			Anx1OutWardErrHeader errDoc = errDocMap.get(key);
			pResults.forEach(pr -> {
				// Instantiate the ent
				OutwardTransDocError error = new OutwardTransDocError();
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
				error.setType(GSTConstants.STRUCTURAL_VALIDATIONS);
				error.setCreatedBy(null);
				error.setCreatedDate(LocalDateTime.now());
				errors.add(error);
			});
			map.put(key, errors);
		});
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"SRFileToOutwardTransDocErrConvertion convertErrors End");
		}
		return map;
	}

	private void populateErrorCodesAndDescription(
			Map<String, List<ProcessingResult>> processingResults,
			String docKey, Gstr1AAnx1OutWardErrHeader errDoc) {

		List<ProcessingResult> errors = processingResults.get(docKey);
		List<String> headerList = new ArrayList<>();
		Map<Integer, List<String>> aitemErrorMap = new HashMap<>();
			for (ProcessingResult error : errors) {
				TransDocProcessingResultLoc loc = (TransDocProcessingResultLoc) error
						.getLocation();
				Integer lineNo = loc.getLineNo();
				if (lineNo == null)
				{
					
					
					headerList.add(error.getCode());
				}
					else
					aitemErrorMap
							.computeIfAbsent(lineNo, k -> new ArrayList<>())
							.add(error.getCode());
			}
		if (!headerList.isEmpty()) {
			
			errDoc.setErrCodes(
					headerList.stream().collect(Collectors.joining(",")));
			
		}
		
		
		
		if (aitemErrorMap.size() > 0) {
			IntStream.range(0, errDoc.getLineItems().size()).forEach(idx -> {
				Gstr1AAnxOutwardTransDocLineItemError item = errDoc
						.getLineItems().get(idx);
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