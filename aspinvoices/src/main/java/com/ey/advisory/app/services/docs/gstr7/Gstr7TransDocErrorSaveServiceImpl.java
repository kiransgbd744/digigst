package com.ey.advisory.app.services.docs.gstr7;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.gstr7trans.Gstr7TransDocErrHeaderEntity;
import com.ey.advisory.app.data.entities.client.gstr7trans.Gstr7TransDocErrItemEntity;
import com.ey.advisory.app.data.repositories.client.gstr7trans.Gstr7TransDocHeaderErrRepository;
import com.ey.advisory.app.services.docs.DocKeyGenerator;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.PerfamanceEventConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.TenantContext;

import lombok.extern.slf4j.Slf4j;

@Service("Gstr7TransDocErrorSaveServiceImpl")
@Slf4j
public class Gstr7TransDocErrorSaveServiceImpl
		implements Gstr7TransDocErrorSaveService {

	@Autowired
	private Gstr7TransDocHeaderErrRepository gstr7TransDocHeaderErrRepository;

	@Autowired
	private DocKeyGenerator<Gstr7TransDocErrHeaderEntity, String> docKeyGen;

	private static final String CLASS_NAME = "Gstr7TransDocErrorSaveServiceImpl";
	private static final String METHOD_NAME = "saveErrorRecord";

	@Override
	public void saveErrorRecord(
			Map<String, List<ProcessingResult>> processingResults,
			List<Gstr7TransDocErrHeaderEntity> errorDocument) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("DefaultDocErrorSaveService saveErrorRecord begining");
		}
		try {
			String groupCode = TenantContext.getTenantId();
			Map<String, Gstr7TransDocErrHeaderEntity> errDocMap = new HashMap<>();
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
			List<String> docKeys = errorDocument.stream()
					.filter(doc -> doc.getDocKey() != null).map(doc -> doc.getDocKey())
					.collect(Collectors.toList());

			// Execute the repository query to udpate the isDelete to true for
			// the above ids. Also mark the updated date to the current date.
			// call the is delete to true repository method.
			if (!docKeys.isEmpty()) {
				gstr7TransDocHeaderErrRepository.updateDocDeletionByDocKeys(docKeys,
						LocalDateTime.now());
			}

			// Save the list of documents.
			errorDocument.forEach(errDoc -> {
				if (null == errDoc.getId()) {
					errDoc.setCreatedOn(LocalDateTime.now());
				}
			});
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"STRUCTURAL_ERROR_SAVE_START", CLASS_NAME, METHOD_NAME,
					null);
			gstr7TransDocHeaderErrRepository.saveAll(errorDocument);
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"STRUCTURAL_ERROR_SAVE_END", CLASS_NAME, METHOD_NAME, null);
		} catch (Exception e) {
			LOGGER.error("ExceptionOccured:{} ", e);
			throw new AppException("Exception while saving the records in Structural Table");
		}

	}

	private void populateErrorCodesAndDescription(
			Map<String, List<ProcessingResult>> processingResults,
			String docKey, Gstr7TransDocErrHeaderEntity errDoc) {

		LOGGER.error("Processing Results {} ", processingResults);
		List<ProcessingResult> errors = processingResults.get(docKey);
		List<String> headerList = new ArrayList<>();
		Map<Integer, List<String>> aitemErrorMap = new HashMap<>();
		if (errors != null && !errors.isEmpty()) {
			for (ProcessingResult error : errors) {
				TransDocProcessingResultLoc loc = (TransDocProcessingResultLoc) error
						.getLocation();
				Integer lineNo = loc.getLineNo();
				if (lineNo == null)
					headerList.add(error.getCode());
				else
					aitemErrorMap
							.computeIfAbsent(lineNo, k -> new ArrayList<>())
							.add(error.getCode());
			}
			if (!headerList.isEmpty()) {
				errDoc.setErrorCodes(
						headerList.stream().collect(Collectors.joining(",")));
			}
			if (aitemErrorMap.size() > 0) {
				IntStream.range(0, errDoc.getLineItems().size())
						.forEach(idx -> {
							Gstr7TransDocErrItemEntity item = errDoc
									.getLineItems().get(idx);
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("map {}, index {}", aitemErrorMap,
										idx);
							}
							if (aitemErrorMap.get(idx) != null) {
								item.setErrorCodes(
										aitemErrorMap.get(idx).stream().collect(
												Collectors.joining(",")));
							}
						});
			}
		}
	}
}
