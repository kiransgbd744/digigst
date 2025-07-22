package com.ey.advisory.app.services.docs.gstr2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.InwardTransDocError;
import com.ey.advisory.app.data.entities.client.InwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.data.repositories.client.gstr2.InwardTransDocErrRepository;
import com.ey.advisory.app.services.docs.DocKeyGenerator;
import com.ey.advisory.app.services.docs.InwardCanDocCheckServiceImpl;
import com.ey.advisory.app.services.validation.DocRulesValidationResult;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.TenantContext;

@Service("InwardDocError")
public class InwardDocError {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(InwardDocError.class);

	@Autowired
	@Qualifier("InwardTransDocErrRepository")
	private InwardTransDocErrRepository docErrorRepository;

	/***
	 * 
	 * @param documents
	 * @param processingResults
	 * @param valResult
	 * @param docKeyGen
	 * @return
	 */
	public Map<String, InwardTransDocument> locateDocErrors(
			List<InwardTransDocument> documents,
			Map<String, List<ProcessingResult>> processingResults,
			DocRulesValidationResult<String> valResult,
			DocKeyGenerator<InwardTransDocument, String> docKeyGen) {
		Map<String, InwardTransDocument> errDocMap = new HashMap<>();
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug(" processingResults:{}",processingResults);
		}
		if (!processingResults.isEmpty()) {
			for (InwardTransDocument doc : documents) {
				if(!doc.isDeleted()){
				String docKey1 = docKeyGen.generateKey(doc);
				if (isError(processingResults, docKey1)) {
					doc.setIsProcessed(false);
					doc.setIsError(true);
					errDocMap.put(docKey1, doc);
				} else {
					// If there are no errors, then set the isProcessed to true
					// and isError to false
					doc.setIsProcessed(true);
					doc.setIsError(false);
				}
				if (isInfo(processingResults, docKey1)) {
					doc.setIsInfo(true);
					errDocMap.put(docKey1, doc);
				}
				if (isError(processingResults, docKey1)
						|| isInfo(processingResults, docKey1)) {
					populateErrorCodeAndErrorDescription(processingResults,
							docKey1, doc);
				}
			}
			}
			if(LOGGER.isDebugEnabled()){
				LOGGER.debug(" errDocMap1:{}",errDocMap);
			}
			/*for (InwardTransDocument doc : documents) {
				if(!doc.isDeleted()){
				String docKey2 = InwardCanDocCheckServiceImpl.reconDocKey2aprgeneration(doc);
				if (isError(processingResults, docKey2)) {
					doc.setIsProcessed(false);
					doc.setIsError(true);
					errDocMap.put(docKey2, doc);
				} else {
					// If there are no errors, then set the isProcessed to true
					// and isError to false
					doc.setIsProcessed(true);
					doc.setIsError(false);
				}
				if (isInfo(processingResults, docKey2)) {
					doc.setIsInfo(true);
					errDocMap.put(docKey2, doc);
				}
				if (isError(processingResults, docKey2)
						|| isInfo(processingResults, docKey2)) {
					populateErrorCodeAndErrorDescription(processingResults,
							docKey2, doc);
				}
				}
			}
			
			if(LOGGER.isDebugEnabled()){
				LOGGER.debug(" errDocMap2:{}",errDocMap);
			}
			for (InwardTransDocument doc : documents) {
				if(!doc.isDeleted()){
				 String docKey = InwardCanDocCheckServiceImpl.reconDocKeygeneration(doc);
				if (isError(processingResults, docKey)) {
					doc.setIsProcessed(false);
					doc.setIsError(true);
					errDocMap.put(docKey, doc);
				} else {
					// If there are no errors, then set the isProcessed to true
					// and isError to false
					doc.setIsProcessed(true);
					doc.setIsError(false);
				}
				if (isInfo(processingResults, docKey)) {
					doc.setIsInfo(true);
					errDocMap.put(docKey, doc);
				}
				if (isError(processingResults, docKey)
						|| isInfo(processingResults, docKey)) {
					populateErrorCodeAndErrorDescription(processingResults,
							docKey, doc);
				}
				}
			}
			if(LOGGER.isDebugEnabled()){
				LOGGER.debug(" errDocMap3:{}",errDocMap);
			}*/
		} else {
			for (InwardTransDocument doc : documents) {
				if(!doc.isDeleted()){
				doc.setIsProcessed(true);
				doc.setIsError(false);
				}
			}
		}
		return errDocMap;
	}

	/**
	 * 
	 * @param results
	 * @param errDocMap
	 * @return
	 */
	public Map<String, List<InwardTransDocError>> convertErrors(
			Map<String, List<ProcessingResult>> results,
			Map<String, InwardTransDocument> errDocMap) {
		Map<String, List<InwardTransDocError>> map = new HashMap<>();
		results.keySet().stream().forEach(key -> {
			List<ProcessingResult> pResults = results.get(key);
			if(LOGGER.isDebugEnabled()){
				LOGGER.debug("pResults:{} and errDocMap:{}",pResults,errDocMap);
			}
			InwardTransDocument inwardTransDocument = errDocMap.get(key);
			if(LOGGER.isDebugEnabled()){
if(inwardTransDocument==null){
	LOGGER.debug("inwardTransDocument is null for docKey{}",key);
}else{
	LOGGER.debug("inwardTransDocument is not null for docKey{}",key);
}
			}
			List<InwardTransDocError> errors = new ArrayList<>();
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
					if (loc.getLineNo() != null && inwardTransDocument!=null) {
						Integer lineNo = loc.getLineNo();
						Integer itemNo = inwardTransDocument
								.getItemNoForIndex(lineNo);
						error.setItemNum(itemNo);
					}
					error.setErrorField(errField);
				}
				error.setErrorCode(pr.getCode());
				error.setErrorDesc(pr.getDescription());

				error.setErrorType(ProcessingResultType.ERROR == pr.getType()
						? "ERR" : "INFO");
				error.setSource("ASP");
				if (ProcessingResultType.ERROR == pr.getType()) {
					error.setValType(GSTConstants.BUSINESS_VALIDATIONS);
				}
				errors.add(error);
			});
			map.put(key, errors);
		});
		return map;
	}

	/**
	 * 
	 * @param errorMap
	 * @param savedDocs
	 * @param docKeyGen
	 * @return
	 */
	public List<InwardTransDocError> saveErrors(
			Map<String, List<InwardTransDocError>> errorMap,
			List<InwardTransDocument> savedDocs,
			DocKeyGenerator<InwardTransDocument, String> docKeyGen) {
		// Map the Ids of the saved documents to the errors associated with
		// it.
		String groupCode = TenantContext.getTenantId();
		savedDocs.forEach(doc -> {
			String docKey = docKeyGen.generateKey(doc);
			List<InwardTransDocError> errList = errorMap.get(docKey);
			Long id = doc.getId();
			if (errList != null && !errList.isEmpty()) {
				errList.forEach(err -> {
					LOGGER.debug("Errors " + err + "for Document Id " + id
							+ "  for groupCode " + groupCode);
					err.setDocHeaderId(id);
					err.setTaxperiod(doc.getTaxperiod());
					err.setCgstin(doc.getCgstin());
					err.setDerivedTaxperiod(doc.getDerivedTaxperiod());
					err.setAcceptanceId(doc.getAcceptanceId());
				});
			}
		});
		/*savedDocs.forEach(doc -> {
			String docKey = InwardCanDocCheckServiceImpl.reconDocKey2aprgeneration(doc);
			List<InwardTransDocError> errList = errorMap.get(docKey);
			Long id = doc.getId();
			if (errList != null && !errList.isEmpty()) {
				errList.forEach(err -> {
					LOGGER.debug("Errors 2apr " + err + "for Document Id " + id
							+ "  for groupCode " + groupCode);
					err.setDocHeaderId(id);
					err.setTaxperiod(doc.getTaxperiod());
					err.setCgstin(doc.getCgstin());
					err.setDerivedTaxperiod(doc.getDerivedTaxperiod());
					err.setAcceptanceId(doc.getAcceptanceId());
				});
			}
		});
		savedDocs.forEach(doc -> {
			String docKey = InwardCanDocCheckServiceImpl.reconDocKeygeneration(doc);
			List<InwardTransDocError> errList = errorMap.get(docKey);
			Long id = doc.getId();
			if (errList != null && !errList.isEmpty()) {
				errList.forEach(err -> {
					LOGGER.debug("Errors 2bpr " + err + "for Document Id " + id
							+ "  for groupCode " + groupCode);
					err.setDocHeaderId(id);
					err.setTaxperiod(doc.getTaxperiod());
					err.setCgstin(doc.getCgstin());
					err.setDerivedTaxperiod(doc.getDerivedTaxperiod());
					err.setAcceptanceId(doc.getAcceptanceId());
				});
			}
		});*/
		List<InwardTransDocError> errorsToSave = new ArrayList<>();
		errorMap.entrySet().forEach(e -> errorsToSave.addAll(e.getValue()));
		errorsToSave.forEach(error -> 
	    LOGGER.debug("ErrorBefore: " + error + " and " + " for groupCode " + groupCode));
		errorsToSave.removeIf(error -> error.getDocHeaderId() == null);
		errorsToSave.forEach(error -> 
	    LOGGER.debug("ErrorAfter: " + error + " and " + " for groupCode " + groupCode));
		if (!errorsToSave.isEmpty()) {
			docErrorRepository.saveAll(errorsToSave);
		}
		return errorsToSave;
	}

	private void populateErrorCodeAndErrorDescription(
			Map<String, List<ProcessingResult>> processingResults,
			String docKey, InwardTransDocument doc) {
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
				InwardTransDocLineItem item = doc.getLineItems().get(idx);
				if (aitemErrorMap.get(idx) != null) {
					item.setErrCodes(aitemErrorMap.get(idx).stream()
							.collect(Collectors.joining(",")));
				}
			});
		}

		if (aiteInfoMap != null && aiteInfoMap.size() > 0) {
			IntStream.range(0, doc.getLineItems().size()).forEach(idx -> {
				InwardTransDocLineItem item = doc.getLineItems().get(idx);
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
		/*List<ProcessingResult> results = processingResults.get(docKey);
	    if (results == null) {
	        // Handle the null case, maybe return false or throw a specific exception
	        return false; // Assuming no error if results are null
	    }*/
		return processingResults.get(docKey).stream()
				.anyMatch(r -> r.getType() == ProcessingResultType.ERROR);
	}

	private boolean isInfo(
			Map<String, List<ProcessingResult>> processingResults,
			String docKey) {
		/*List<ProcessingResult> results = processingResults.get(docKey);
	    if (results == null) {
	        // Handle the null case, maybe return false or throw a specific exception
	        return false; // Assuming no error if results are null
	    }*/
		return processingResults.get(docKey).stream()
				.anyMatch(r -> r.getType() == ProcessingResultType.INFO);
	}
}
