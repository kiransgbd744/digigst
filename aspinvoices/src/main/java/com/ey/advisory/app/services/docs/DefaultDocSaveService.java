package com.ey.advisory.app.services.docs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.DocRateSummary;
import com.ey.advisory.app.data.entities.client.GstrReturnStatusEntity;
import com.ey.advisory.app.data.entities.client.OutwardTransDocError;
import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.DocErrorRepository;
import com.ey.advisory.app.data.repositories.client.DocRateSummaryRepository;
import com.ey.advisory.app.data.repositories.client.GstrReturnStatusRepository;
import com.ey.advisory.app.docs.dto.DocSaveRespDto;
import com.ey.advisory.app.docs.dto.OutwardDocSaveRespDto;
import com.ey.advisory.app.services.bifurcation.DocBifurcator;
import com.ey.advisory.app.services.docs.common.OrgHierarchyMasterDataUpdation;
import com.ey.advisory.app.services.docs.common.OutwardAdditionalMasterDataAddition;
import com.ey.advisory.app.services.docs.einvoice.EInvoiceDefaultDocSaveService;
import com.ey.advisory.app.services.validation.DocRulesValidationResult;
import com.ey.advisory.app.services.validation.sales.SalesDocRulesValidatorService;
import com.ey.advisory.app.util.AspDocumentConstants.FormReturnTypes;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AspInvoiceStatus;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.PerfamanceEventConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;

/**
 * 
 * @author Mohana.Dasari
 *
 */
@Service("DefaultDocSaveService")
public class DefaultDocSaveService implements DocSaveService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DefaultDocSaveService.class);
	
    public static final String CONFI_KEY = "b2cs.threshold.value";


	@Autowired
	@Qualifier("DocErrorRepository")
	private DocErrorRepository docErrorRepository;

	@Autowired
	@Qualifier("SalesDocRulesValidatorService")
	private SalesDocRulesValidatorService salesDocRulesValSvc;

	@Autowired
	@Qualifier("DocRateSummaryRepository")
	private DocRateSummaryRepository docRateSummaryRepository;

	@Autowired
	private DocKeyGenerator<OutwardTransDocument, String> docKeyGen;

	@Autowired
	@Qualifier("DefaultOutwardTransDocBifurcator")
	private DocBifurcator<OutwardTransDocument> defaultBifurcator;

	@Autowired
	@Qualifier("SimplifiedOutwardTransDocBifurcator")
	private DocBifurcator<OutwardTransDocument> simplifiedBifurcator;

	@Autowired
	@Qualifier("OutwardDocSaveResp")
	private OutwardDocSaveResp outwardDocSaveResp;

	@Autowired
	@Qualifier("CanDocCheckServiceImpl")
	private OriginalDocCheckService canDocCheckService;

	@Autowired
	@Qualifier("OutwardDocSave")
	private OutwardDocSave outwardDocSave;

	@Autowired
	@Qualifier("OrgHierarchyMasterDataUpdation")
	private OrgHierarchyMasterDataUpdation orgHierarchyMasterDataUpdation;

	@Autowired
	@Qualifier("OutwardAdditionalMasterDataAddition")
	private OutwardAdditionalMasterDataAddition additionalMasterDataAddition;

	@Autowired
	@Qualifier("DefaultOrginalDocCheckServiceImpl")
	private OriginalDocCheckService originalDocCheckService;

	@Autowired
	@Qualifier("gstrReturnStatusRepository")
	private GstrReturnStatusRepository gstrReturnStatusRepository;
	@Autowired
	@Qualifier("EInvoiceDefaultDocSaveService")
	EInvoiceDefaultDocSaveService einvDefaultDocSaveService;
	
	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	private static final String CLASS_NAME = "DefaultDocSaveService";
	private static final String PIPE = "|";

	@Transactional(value = "clientTransactionManager")
	@Override
	public OutwardDocSaveRespDto saveDocuments(
			List<OutwardTransDocument> documents) {

		OutwardDocSaveRespDto finalRespDto = new OutwardDocSaveRespDto();
		String groupCode = TenantContext.getTenantId();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Entered saveDocuments for groupCode{} ", groupCode);
		}

		try {
			
			// Take the groupCode from a thread local variable.
			ProcessingContext context = new ProcessingContext();
			context.seAttribute("groupCode", groupCode);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Validating documents for groupCode {}", groupCode);
			}

			/*
			 * Convert Calculate Configure And Set Values to both Header and
			 * Item
			 */
			markDuplicateDocuments(documents);
			settingFiledGstins(context);
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"CALC_CALC_CONFIG_START", CLASS_NAME, "saveDocuments",
					null);
			outwardDocSave.convertCalcConfigAndSetValues(documents);
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"CALC_CALC_CONFIG_END", CLASS_NAME, "saveDocuments", null);

			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"BIFURCATION_START", CLASS_NAME, "saveDocuments", null);
			// Bifurcate Outward Documents
			bifurcateOutwardDoc(documents,
					new HashMap<String, List<ProcessingResult>>(), groupCode);
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"BIFURCATION_END", CLASS_NAME, "saveDocuments", null);

			// Invoke the validation service and the processing results.
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"BUSINESS_VALIDATION_START", CLASS_NAME, "saveDocuments",
					null);
			DocRulesValidationResult<String> valResult = salesDocRulesValSvc
					.validate(documents, context);

			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"BUSINESS_VALIDATION_END", CLASS_NAME, "saveDocuments",
					null);
			Map<String, List<ProcessingResult>> ruleProcessingResults = valResult
					.getProcessingResults();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Validated documents for groupCode {}", groupCode);
			}

			Map<String, List<ProcessingResult>> OrgDocChkprocessingResults = originalDocCheckService
					.checkForCrDrOrgInvoices(documents, false, context);
			Map<String, List<ProcessingResult>> canChkprocessingResults = canDocCheckService
					.checkForCrDrOrgInvoices(documents, true, context);

			HashMap<String, List<ProcessingResult>> processingCrdrResults = new HashMap<>(
					ruleProcessingResults);

			OrgDocChkprocessingResults.forEach(
					(key, value) -> processingCrdrResults.merge(key, value,
							(v1, v2) -> Stream.of(v1, v2)
									.flatMap(x -> x.stream()).collect(
											Collectors.toList())));

			HashMap<String, List<ProcessingResult>> processingResults = new HashMap<>(
					processingCrdrResults);

			canChkprocessingResults
					.forEach((key, value) -> processingResults.merge(key, value,
							(v1, v2) -> Stream.of(v1, v2)
									.flatMap(x -> x.stream()).collect(
											Collectors.toList())));

			// This is for locating the error
			Map<String, OutwardTransDocument> errDocMap = new HashMap<>();
			// For each document with errors, set the isError to true.
			if (!processingResults.isEmpty()) {
				for (OutwardTransDocument doc : documents) {
					String docKey = docKeyGen.generateKey(doc);
					// if (valResult.hasErrors(docKey)) {
					if (isError(processingResults, docKey)) {
						doc.setIsProcessed(false);
						doc.setIsError(true);
						doc.setAspInvoiceStatus(AspInvoiceStatus.ASP_ERROR
								.getAspInvoiceStatusCode());
						errDocMap.put(docKey, doc);
					} else {
						// If there are no errors, then set the isProcessed to
						// true
						// and isError to false
						doc.setIsProcessed(true);
						doc.setIsError(false);
						doc.setAspInvoiceStatus(AspInvoiceStatus.ASP_PROCESSED
								.getAspInvoiceStatusCode());
					}
					// if (valResult.hasInfo(docKeyGen.generateKey(doc))) {
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
			} else {
				for (OutwardTransDocument doc : documents) {
					doc.setIsProcessed(true);
					doc.setIsError(false);
					doc.setAspInvoiceStatus(AspInvoiceStatus.ASP_PROCESSED
							.getAspInvoiceStatusCode());
				}
			}

			// Keep the list of errors ready.
			Map<String, List<OutwardTransDocError>> errorMap = convertErrors(
					processingResults, errDocMap);

			// Get the list of document ids for hte existing documetns and keep
			// it aside, so that we can use this to populate the 'oldDocId'
			// value
			// while creating the response.
			Stream<Long> docIdStream = documents.stream()
					.map(doc -> doc.getId());
			List<Long> oldDocIds = docIdStream.filter(x -> x != null)
					.collect(Collectors.toList());

			// Save the list of documents.
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"SAVE_DB_DOC_START", CLASS_NAME, "saveDocuments", null);
			List<OutwardTransDocument> savedDocs = outwardDocSave
					.saveDocs(documents, docKeyGen);
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"SAVE_DB_DOC_END", CLASS_NAME, "saveDocuments", null);
			// Map the Ids of the saved documents to the errors associated with
			// it.
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"MAP_DOC_TO_ERRORS_START", CLASS_NAME, "saveDocuments",
					null);
			savedDocs.forEach(doc -> {
				String docKey = docKeyGen.generateKey(doc);
				List<OutwardTransDocError> errList = errorMap.get(docKey);
				Long id = doc.getId();
				if (errList != null && !errList.isEmpty()) {
					errList.forEach(err -> {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Error " + err + "for Document " + doc
									+ "  for groupCode " + groupCode);
						}
						err.setDocHeaderId(id);
						err.setSgstin(doc.getSgstin());
						err.setTaxperiod(doc.getTaxperiod());
						err.setDerivedTaxperiod(doc.getDerivedTaxperiod());
						err.setAcceptanceId(doc.getAcceptanceId());
					});
				}
			});
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"MAP_DOC_TO_ERRORS_END", CLASS_NAME, "saveDocuments", null);
			// Add all the errors into a single list to save to the DB.
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"DOC_ERRORS_TO_LIST_START", CLASS_NAME, "saveDocuments",
					null);
			List<OutwardTransDocError> outError = new ArrayList<>();
			errorMap.entrySet().forEach(e -> {
				List<OutwardTransDocError> errorList = e.getValue();
				errorList.forEach(error -> {
					if (error.getDocHeaderId() != null) {
						outError.add(error);
					}
				});
			});
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"DOC_ERRORS_TO_LIST_END", CLASS_NAME, "saveDocuments",
					null);

			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"DOC_ERROR_SAVE_START", CLASS_NAME, "saveDocuments", null);
			if (!outError.isEmpty()) {
				docErrorRepository.saveAll(outError);
			}
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"DOC_ERROR_SAVE_END", CLASS_NAME, "saveDocuments", null);
			// docErrorRepository.saveAll(errorsToSave);

			// Create the rate level invoices.
			List<DocRateSummary> docRateSummaryList = einvDefaultDocSaveService.createRateLevelSummary(
					savedDocs);
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"DOC_RATE_START", CLASS_NAME, "saveDocuments", null);
			docRateSummaryRepository.saveAll(docRateSummaryList);
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"DOC_RATE_END", CLASS_NAME, "saveDocuments", null);
			// Onboarding Config Params

			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"CONFIGURATION_PARAMS_START", CLASS_NAME, "saveDocuments",
					null);
			configureOnboardingParams(documents);
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"CONFIGURATION_PARAMS_END", CLASS_NAME, "saveDocuments",
					null);
			// Finally create response to return.

			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"CREATE_OUTWARD_DOC_SAVE_START", CLASS_NAME,
					"saveDocuments", null);
			List<DocSaveRespDto> docSaveRespDtos = outwardDocSaveResp
					.createOutwardDocSaveAPIResponse(oldDocIds, savedDocs,
							outError);
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"CREATE_OUTWARD_DOC_SAVE_END", CLASS_NAME, "saveDocuments",
					null);
			finalRespDto.setSavedDocsResp(docSaveRespDtos);
			finalRespDto.setProcessingResults(processingResults);
		} catch (Exception e) {
			LOGGER.error("Save Docs : Exception Occured:{} ", e);
			throw new AppException("Exception while saving the documents ",
					e.getMessage());
		}
		return finalRespDto;
	}

	private void settingFiledGstins(ProcessingContext context) {
		List<GstrReturnStatusEntity> filedRecords = gstrReturnStatusRepository
				.findByReturnTypeAndStatusIgnoreCaseAndIsCounterPartyGstinFalse(
						"GSTR1", "FILED");
		Set<String> filedSet = new HashSet<>();
		for (GstrReturnStatusEntity entity : filedRecords) {

			filedSet.add(entity.getGstin() + PIPE + entity.getTaxPeriod());
		}
		context.seAttribute("filedSet", filedSet);
	}

	private Map<String, List<OutwardTransDocError>> convertErrors(
			Map<String, List<ProcessingResult>> results,
			Map<String, OutwardTransDocument> errDocMap) {
		Map<String, List<OutwardTransDocError>> map = new HashMap<>();
		results.keySet().stream().forEach(key -> {
			List<ProcessingResult> pResults = results.get(key);
			OutwardTransDocument outwardTransDocument = errDocMap.get(key);

			List<OutwardTransDocError> errors = new ArrayList<>();
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
						Integer itemNo = outwardTransDocument
								.getItemNoForIndex(lineNo);
						error.setItemNum(itemNo);
					}
					error.setErrorField(errField);
				}
				error.setErrorCode(pr.getCode());
				error.setErrorDesc(pr.getDescription());
				error.setErrorType(ProcessingResultType.ERROR == pr.getType()
						? "ERR" : "INFO");
				if (ProcessingResultType.ERROR == pr.getType()) {
					error.setType(GSTConstants.BUSINESS_VALIDATIONS);
				}
				error.setSource("ASP");
				errors.add(error);
			});
			map.put(key, errors);
		});
		return map;
	}

	private void bifurcateOutwardDoc(List<OutwardTransDocument> documents,
			Map<String, List<ProcessingResult>> processingResults,
			String groupCode) {
		// Initialize an empty processing context and pass it to the
		// bifurcator.
		for (OutwardTransDocument document : documents) {
			if (!GSTConstants.I
					.equalsIgnoreCase(document.getTransactionType())) {
				document.setCmplianceApplicable(true);
			}
		}

		ProcessingContext bifContext = new ProcessingContext();
		Map<String, Config> confiMap = configManager
				.getConfigs("OUTWARD", CONFI_KEY, "DEFAULT");
		
		String configString = confiMap.get(CONFI_KEY) == null
				? "201704-202406|250000;202407-202812|100000"
				: confiMap.get(CONFI_KEY).getValue();
		
		bifContext.seAttribute(CONFI_KEY, configString);
		documents.stream()
				.filter(doc -> !"CAN".equalsIgnoreCase(doc.getSupplyType())
						&& !doc.getIsError())
				.forEach(doc -> {
					// if document has business rule error, it will not be
					// eligible for bifurcation
					// doc.setCmplianceApplicable(true);
					String formReturnType = doc.getFormReturnType();
					doc = defaultBifurcator.bifurcate(doc, bifContext);
					if (!defaultBifurcator.isBifurcated(doc)
							&& FormReturnTypes.GSTR1.getType()
									.equals(formReturnType)) {
						String docKey = docKeyGen.generateKey(doc);
						List<ProcessingResult> prList = processingResults
								.get(docKey);
						String[] errorLocations = new String[] {
								GSTConstants.DOC_NO };
						TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
								null, errorLocations);
						// List<ProcessingResult> prList =
						// processingResults.get(docKey);
						ProcessingResult pr = new ProcessingResult("ASP",
								"ER0501",
								"Transaction cannot be mapped to any of the Tables "
										+ "of the GSTR-1 Return Form",
								location);
						doc.setCmplianceApplicable(false);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Bifurcation for Document" + doc
									+ "  for groupCode " + groupCode);
						}
						// If the document does not have any validation errors,
						// then the prList will be null. In that case we need to
						// create a new list and add the errors to the list.
						if (prList != null) {
							prList.add(pr);
						} else {
							prList = new ArrayList<>();
							prList.add(pr);
							processingResults.put(docKey, prList);
						}
					}

					doc = simplifiedBifurcator.bifurcate(doc, bifContext);
					if (!simplifiedBifurcator.isBifurcated(doc)
							&& FormReturnTypes.ANX1.getType()
									.equals(formReturnType)) {
						String docKey = docKeyGen.generateKey(doc);
						List<ProcessingResult> prList = processingResults
								.get(docKey);
						ProcessingResult pr = new ProcessingResult("ASP",
								"ER0501",
								"Transaction cannot be mapped to any of the Tables "
										+ "of the Annexure-1 Return Form");
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Bifurcation for Document" + doc
									+ "  for groupCode " + groupCode);
						}
						doc.setCmplianceApplicable(false);
						// If the document does not have any validation errors,
						// then
						// the prList will be null. In that case we need to
						// create a
						// new list and add the errors to the list.
						if (prList != null) {
							prList.add(pr);
						} else {
							prList = new ArrayList<>();
							prList.add(pr);
							processingResults.put(docKey, prList);
						}
					}
				});
	}

	private void configureOnboardingParams(
			List<OutwardTransDocument> documents) {
		// Update Org. Hierarchy Attributes
		orgHierarchyMasterDataUpdation
				.updateOrgHierarchyMasterDataValForOutwardDocs(documents);
		// Add Additional Master Data
		additionalMasterDataAddition.addAdditionalMasterData(documents);
	}

	private void populateErrorCodeAndErrorDescription(
			HashMap<String, List<ProcessingResult>> processingResults,
			String docKey, OutwardTransDocument doc) {
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
				OutwardTransDocLineItem item = doc.getLineItems().get(idx);
				if (aitemErrorMap.get(idx) != null) {
					item.setErrCodes(aitemErrorMap.get(idx).stream()
							.collect(Collectors.joining(",")));
				}
			});
		}

		if (aiteInfoMap != null && aiteInfoMap.size() > 0) {
			IntStream.range(0, doc.getLineItems().size()).forEach(idx -> {
				OutwardTransDocLineItem item = doc.getLineItems().get(idx);
				if (aiteInfoMap.get(idx) != null) {
					item.setInfoCodes(aiteInfoMap.get(idx).stream()
							.collect(Collectors.joining(",")));
				}
			});
		}

	}

	private void markDuplicateDocuments(List<OutwardTransDocument> docs) {

		// Get the map of documents will the keys and values as the list of
		// documents.
		// Map<String, List<OutwardTransDocument>> retResultMap = new
		// HashMap<>();

		Map<String, List<OutwardTransDocument>> allDocsMap = docs.stream()
				.collect(Collectors
						.groupingBy(doc -> docKeyGen.generateKey(doc)));

		// Filter out the documents that have more than one element in the value
		// list.
		Map<String, List<OutwardTransDocument>> duplicatesMap = allDocsMap
				.entrySet().stream().filter(e -> e.getValue().size() > 1)
				.collect(Collectors.toMap(Map.Entry::getKey,
						Map.Entry::getValue));

		// Iterate over all the documents in the duplicatesMap and set
		// isDuplicate = true.
		duplicatesMap.entrySet().forEach(entry -> {
			// String key = entry.getKey();
			List<OutwardTransDocument> value = entry.getValue();
			int lastIndex = value.size() - 1;
			IntStream.range(0, value.size()).forEach(idx -> {
				OutwardTransDocument item = value.get(idx);

				if (idx != lastIndex) {

					item.setIsError(true);
					item.setErrCodes(GSTConstants.ER15167);
				}
			});

		});

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
}
