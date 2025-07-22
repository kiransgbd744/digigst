package com.ey.advisory.app.services.docs.gstr2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.entities.client.GstrReturnStatusEntity;
import com.ey.advisory.app.data.entities.client.InwardTransDocError;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.data.repositories.client.Gstr2BReconResultRespPsdRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2ReconResultRespPsdRepository;
import com.ey.advisory.app.data.repositories.client.GstrReturnStatusRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.InwardTransDocRepository;
import com.ey.advisory.app.docs.dto.gstr2.InwardDocSaveFinalRespDto;
import com.ey.advisory.app.docs.dto.gstr2.InwardDocSaveRespDto;
import com.ey.advisory.app.services.bifurcation.DocBifurcator;
import com.ey.advisory.app.services.docs.DocKeyGenerator;
import com.ey.advisory.app.services.docs.OriginalDocCheckService;
import com.ey.advisory.app.services.docs.common.InwardAdditionalMasterDataAddition;
import com.ey.advisory.app.services.docs.common.OrgHierarchyMasterDataUpdation;
import com.ey.advisory.app.services.validation.DocRulesValidationResult;
import com.ey.advisory.app.services.validation.purchase.PurchaseDocRulesValidatorService;
import com.ey.advisory.app.util.OnboardingQuestionValidationsUtil;
import com.ey.advisory.app.util.AspDocumentConstants.FormReturnTypes;
import com.ey.advisory.app.util.OnboardingConstant.CONFIG_PARAM_INWARD_QUE_KEY_ID;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.PerfamanceEventConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;

/**
 * 
 * @author Mohana.Dasari
 *
 */

@Service("DefaultInwardDocSaveService")
public class DefaultInwardDocSaveService implements InwardDocSaveService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DefaultInwardDocSaveService.class);

	@Autowired
	@Qualifier("InwardDocSave")
	private InwardDocSave inwardDocSave;

	@Autowired
	@Qualifier("InwardDocError")
	private InwardDocError inwardDocError;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Autowired
	@Qualifier("InwardDocSaveResp")
	private InwardDocSaveResp inwardDocSaveResp;

	@Autowired
	@Qualifier("PurchaseDocRulesValidatorService")
	private PurchaseDocRulesValidatorService purchaseDocRulesValidatorService;

	@Autowired
	private DocKeyGenerator<InwardTransDocument, String> docKeyGen;

	@Autowired
	@Qualifier("InwardTransDocRepository")
	private InwardTransDocRepository docHeaderRepository;

	@Autowired
	@Qualifier("Gstr2ReconResultRespPsdRepository")
	private Gstr2ReconResultRespPsdRepository gstr2ReconResultRespPsdRepository;

	@Autowired
	@Qualifier("DefaultInwardTransDocBifurcator")
	private DocBifurcator<InwardTransDocument> defaultBifurcator;

	@Autowired
	@Qualifier("SimplifiedInwardTransDocBifurcator")
	private DocBifurcator<InwardTransDocument> simplifiedBifurcator;

	@Autowired
	@Qualifier("OrgHierarchyMasterDataUpdation")
	private OrgHierarchyMasterDataUpdation orgHierarchyMasterDataUpdation;

	@Autowired
	@Qualifier("InwardAdditionalMasterDataAddition")
	private InwardAdditionalMasterDataAddition additionalMasterDataAddition;

	@Autowired
	@Qualifier("DefaultInwardOrginalDocCheckServiceImpl")
	private OriginalDocCheckService originalDocCheckService;

	@Autowired
	@Qualifier("InwardCanDocCheckServiceImpl")
	private OriginalDocCheckService canDocCheckService;
	@Autowired
	@Qualifier("gstrReturnStatusRepository")
	private GstrReturnStatusRepository gstrReturnStatusRepository;

	@Autowired
	@Qualifier("Gstr2BReconResultRespPsdRepository")
	private Gstr2BReconResultRespPsdRepository gstr2BReconResultRespPsdRepository;

	@Autowired
	@Qualifier("OnboardingQuestionValidationsUtil")
	private OnboardingQuestionValidationsUtil util;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinInfoRepository;

	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository configPrtRepo;

	private static final List<String> LOCK = ImmutableList.of("LOCK", "LOCK2");
	private static final String CLASS_NAME = "DefaultInwardDocSaveService";
	private static final String METHOD_NAME = "saveDocuments";
	private static final String DOC_KEY_JOINER = "|";
	private static final String quote = "-";

	private static final List<String> INV_RNV = ImmutableList
			.of(GSTConstants.INV, GSTConstants.RNV);
	private static final List<String> CR_RCR = ImmutableList.of(GSTConstants.CR,
			GSTConstants.RCR);
	private static final List<String> DR_RDR = ImmutableList.of(GSTConstants.DR,
			GSTConstants.RDR);

	@Override
	public InwardDocSaveFinalRespDto saveDocuments(
			List<InwardTransDocument> documents, String sourceId,
			String headerPayloadId) {

		int totalCount = documents.size();
		InwardDocSaveFinalRespDto finalRespDto = new InwardDocSaveFinalRespDto();
		String groupCode = TenantContext.getTenantId();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Entered Inward saveDocuments for groupCode ",
					groupCode);
		}
		ProcessingContext context = new ProcessingContext();
		context.seAttribute("groupCode", groupCode);

		/*
		 * Convert Calculate Configure And Set Values to both Header and Item
		 */
		// store the time SECURITY_ATTR_CONFIG_CALC_BEGIN
		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"SECURITY_ATTR_CONFIG_CALC_START", CLASS_NAME, METHOD_NAME,
				null);
		inwardDocSave.convertCalcConfigAndSetValues(documents, sourceId,
				headerPayloadId);
		// store the time SECURITY_ATTR_CONFIG_CALC_END
		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"SECURITY_ATTR_CONFIG_CALC_END", CLASS_NAME, METHOD_NAME, null);
		gstinInfocache(context);
		settingFiledGstins(context);
		// soft delete the duplicate records
		markDuplicateDocuments(documents);
		// soft delete the Locked records
		softDeleteTheLockedRecord(documents.stream()
				.filter(doc -> !doc.isDeleted()).collect(Collectors.toList()));
		// soft delete the gstr2B Locked records
		softDeleteTheGstr2BLockedRecord(documents.stream()
				.filter(doc -> !doc.isDeleted()).collect(Collectors.toList()));
		// Invoke the validation service and the processing results.
		// store the time BUSINESS_VALIDATION_BEGIN
		duplicateCheckBasedOnOnboarding(documents.stream()
				.filter(doc -> !doc.isDeleted() && !GSTConstants.CAN
						.equalsIgnoreCase(doc.getSupplyType()))
				.collect(Collectors.toList()));

		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"BUSINESS_VALIDATION_START", CLASS_NAME, METHOD_NAME, null);
		DocRulesValidationResult<String> valResult = purchaseDocRulesValidatorService
				.validate(documents, context);
		Map<String, List<ProcessingResult>> ruleProcessingResults = valResult
				.getProcessingResults();
		// store the time BUSINESS_VALIDATION_END
		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"BUSINESS_VALIDATION_END", CLASS_NAME, METHOD_NAME, null);

		Map<String, List<ProcessingResult>> OrgDocChkprocessingResults = originalDocCheckService
				.checkForInwardCrDrOrgInvoices(documents, context);

		HashMap<String, List<ProcessingResult>> processingMergeResults = new HashMap<>(
				ruleProcessingResults);

		OrgDocChkprocessingResults.forEach(
				(key, value) -> processingMergeResults.merge(key, value,
						(v1, v2) -> Stream.of(v1, v2).flatMap(x -> x.stream())
								.collect(Collectors.toList())));

		Map<String, List<ProcessingResult>> canChkprocessingResults = canDocCheckService
				.checkForInwardCrDrOrgInvoices(documents, context);

		HashMap<String, List<ProcessingResult>> processingResults = new HashMap<>(
				processingMergeResults);

		canChkprocessingResults
				.forEach((key, value) -> processingResults.merge(key, value,
						(v1, v2) -> Stream.of(v1, v2).flatMap(x -> x.stream())
								.collect(Collectors.toList())));

		// Bifurcate Inward Documents
		// store the time BIFURCATION_BEGIN
		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"BIFURCATION_START", CLASS_NAME, METHOD_NAME, null);
		bifurcateInwardDoc(documents, processingResults, groupCode, context);
		// store the time BIFURCATION_END
		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"BIFURCATION_END", CLASS_NAME, METHOD_NAME, null);

		// This is for locating the error
		Map<String, InwardTransDocument> errDocMap = inwardDocError
				.locateDocErrors(documents, processingResults, valResult,
						docKeyGen);

		// Keep the list of errors ready.
		Map<String, List<InwardTransDocError>> errorMap = inwardDocError
				.convertErrors(processingResults, errDocMap);

		// Get the list of document ids for hte existing documetns and keep
		// it aside, so that we can use this to populate the 'oldDocId'
		// value
		// while creating the response.
		Stream<Long> docIdStream = documents.stream().map(doc -> doc.getId());
		List<Long> oldDocIds = docIdStream.filter(x -> x != null)
				.collect(Collectors.toList());

		// Save the list of documents.
		// store the time DOCS_SAVE_BEGIN
		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"DOCS_DB_SAVE_START", CLASS_NAME, METHOD_NAME, null);

		List<InwardTransDocument> savedDocs = inwardDocSave.saveDocs(documents,
				docKeyGen);
		// store the time DOCS_SAVE_END
		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"DOCS_DB_SAVE_END", CLASS_NAME, METHOD_NAME, null);

		// Async Job Invocation
		/*
		 * asyncJobsService.createJob(groupCode, JobConstants.ANX2_RECON_PR, "",
		 * JobConstants.SYSTEM, JobConstants.PRIORITY,
		 * JobConstants.PARENT_JOB_ID, JobConstants.SCHEDULE_AFTER_IN_MINS);
		 */

		// Add all the errors into a single list to save to the DB.
		// store the time DOC_ERRORS_SAVE_BEGIN
		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"DOC_ERRORS_SAVE_START", CLASS_NAME, METHOD_NAME, null);
		List<InwardTransDocError> errors = inwardDocError.saveErrors(errorMap,
				savedDocs, docKeyGen);
		// store the time DOC_ERRORS_SAVE_END
		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"DOC_ERRORS_SAVE_END", CLASS_NAME, METHOD_NAME, null);

		Set<String> gstinSet = new HashSet<>();
		for (InwardTransDocument doc : savedDocs) {
			if (GSTConstants.DataOriginTypeCodes.ERP_API.getDataOriginTypeCode()
					.equalsIgnoreCase(doc.getDataOriginTypeCode())) {
				String inGstin = doc.getCgstin();
				sourceId = doc.getDerivedSourceId();
				if (inGstin != null) {
					gstinSet.add(inGstin);
				}
			}
		}
		List<String> jobParamsList = new ArrayList<>();

		if (!gstinSet.isEmpty()) {
			for (String sgstin : gstinSet) {
				String gstin = sgstin;

				JsonObject jsonParams = new JsonObject();
				jsonParams.addProperty("gstin", gstin);
				jsonParams.addProperty("scenarioName",
						APIConstants.INWARD_ASP_ERP_PUSH);
				jobParamsList.add(jsonParams.toString());

				/*
				 * asyncJobsService.createJob(TenantContext.getTenantId(),
				 * JobConstants.ERROR_DOCS_REV_INTG, jsonParams.toString(),
				 * APIConstants.SYSTEM, JobConstants.PRIORITY,
				 * JobConstants.PARENT_JOB_ID,
				 * JobConstants.SCHEDULE_AFTER_IN_MINS);
				 */
			}
		}

		// Onboarding Config Params
		configureOnboardingParams(documents);
		// API Response
		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"INWARD_DOC_RESP_SAVE_START", CLASS_NAME, METHOD_NAME, null);
		List<InwardDocSaveRespDto> docSaveRespDtos = inwardDocSaveResp
				.createInwardDocSaveAPIResponse(oldDocIds, savedDocs, errors);

		finalRespDto.setSavedDocsResp(docSaveRespDtos);
		finalRespDto.setProcessingResults(processingResults);
		finalRespDto.setErrors(errorCount(savedDocs));
		finalRespDto.setTotalRecords(totalCount);
		finalRespDto.setJobParamsList(jobParamsList);
		/*
		 * finalRespDto
		 * .setMetaDataRevIntJobParamsList(metaDataRevIntjobParamsList);
		 */// store the time INWARD_DOC_SAVE_COMPLETE
		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"INWARD_DOC_RESP_SAVE_END", CLASS_NAME, METHOD_NAME, null);
		return finalRespDto;
	}

	private void softDeleteTheGstr2BLockedRecord(
			List<InwardTransDocument> documents) {

		List<String> docKeys = null;
		List<Object[]> orgDocDetails = new ArrayList<>();
		try {
			List<InwardTransDocument> docs = documents.stream()
					.filter(doc -> !doc.isDeleted())
					.collect(Collectors.toList());
			if (docs != null && !docs.isEmpty()) {
				Set<String> keys = new HashSet<>();
				docs.stream()
						.forEach(doc -> keys.add(DefaultInwardDocSave240Service
								.reconDocKeygeneration(doc)));

				docKeys = new ArrayList<>(keys);

				Config config = configManager.getConfig("EYInternal",
						"outward.save.chunksize");
				String chnkSizeStr = config != null ? config.getValue()
						: "2000";
				int chunkSize = Integer.parseInt(chnkSizeStr);

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Generated Size is %d,"
									+ " About to chunk the Dockeys list in "
									+ "orgsoftDeleteTheLOckedRecord doc check....",
							docKeys.size());
					LOGGER.debug(msg);
				}

				List<List<String>> docKeyChunks = Lists.partition(docKeys,
						chunkSize);

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Generated %d Chunks of DocKeys of each Size is "
									+ "%d in orgsoftDeleteTheLOckedRecord doc check",
							docKeyChunks.size(), chunkSize);
					LOGGER.debug(msg);
				}

				if (!docKeyChunks.isEmpty()) {
					docKeyChunks.forEach(chunk -> orgDocDetails
							.addAll(gstr2BReconResultRespPsdRepository
									.getDataByDocKey(chunk)));
				}

				if (orgDocDetails != null && !orgDocDetails.isEmpty()) {
					Map<String, Pair<String, String>> mapPair = new HashMap<>();
					for (Object data[] : orgDocDetails) {
						mapPair.put((String) data[0],
								new Pair<>((String) data[1], (String) data[2]));
					}
					for (InwardTransDocument doc : docs) {
						Pair<String, String> pairData = mapPair
								.get(DefaultInwardDocSave240Service
										.reconDocKeygeneration(doc));
						if (pairData != null) {
							if ((!Strings.isNullOrEmpty(pairData.getValue0())
									&& LOCK.contains(
											pairData.getValue0().toUpperCase()))
									|| !Strings.isNullOrEmpty(
											pairData.getValue1())) {
								doc.setDeleted(true);
								doc.setIsError(true);
								doc.setErrCodes(GSTConstants.ER1273);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error(
					"Error while doing loKup " + "on gstr2b locked records", e);

		}
	}

	private void gstinInfocache(ProcessingContext context) {
		List<GSTNDetailEntity> findDetails = gstinInfoRepository.findDetails();

		Map<String, String> gstinInfoMap = findDetails.stream()
				.collect(Collectors.toMap(GSTNDetailEntity::getGstin,
						GSTNDetailEntity::getRegistrationType));
		context.seAttribute("gstinInfoMap", gstinInfoMap);
	}

	private void settingFiledGstins(ProcessingContext context) {
		List<GstrReturnStatusEntity> filedRecords = gstrReturnStatusRepository
				.findByReturnTypeAndStatusIgnoreCaseAndIsCounterPartyGstinFalse(
						"GSTR6", "FILED");
		Set<String> filedSet = new HashSet<>();
		for (GstrReturnStatusEntity entity : filedRecords) {

			filedSet.add(
					entity.getGstin() + DOC_KEY_JOINER + entity.getTaxPeriod());
		}
		context.seAttribute("filedSet", filedSet);
	}

	private void bifurcateInwardDoc(List<InwardTransDocument> documents,
			Map<String, List<ProcessingResult>> processingResults,
			String groupCode, ProcessingContext context) {

		// Initialize an empty processing context and pass it to the
		// bifurcator.
		// ProcessingContext bifContext = new ProcessingContext();
		documents.stream()
				.filter(doc -> !"CAN".equalsIgnoreCase(doc.getSupplyType()))
				.forEach(doc -> {
					String formReturnType = doc.getFormReturnType();
					doc = defaultBifurcator.bifurcate(doc, context);
					if (!defaultBifurcator.isBifurcated(doc)
							&& FormReturnTypes.GSTR2.getType()
									.equals(formReturnType)) {
						String docKey = docKeyGen.generateKey(doc);
						List<ProcessingResult> prList = processingResults
								.get(docKey);
						ProcessingResult pr = new ProcessingResult("ASP",
								"ER1301",
								"Record cannot mapped to any tables as -CustomerGSTIN/DocumentType/"
										+ "SupplyType/ReverseChargeFlag/POS or combination is missing");
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"Bifurcation for Document {} for groupCode {}",
									doc, groupCode);
						}
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

					/*
					 * doc = simplifiedBifurcator.bifurcate(doc, bifContext); if
					 * (!simplifiedBifurcator.isBifurcated(doc) &&
					 * FormReturnTypes.ANX2.getType() .equals(formReturnType)) {
					 * String docKey = docKeyGen.generateKey(doc);
					 * List<ProcessingResult> prList = processingResults
					 * .get(docKey); ProcessingResult pr = new
					 * ProcessingResult("ASP", "ER1301",
					 * "Record cannot mapped to any tables as -CustomerGSTIN/DocumentType/"
					 * +
					 * "SupplyType/ReverseChargeFlag/POS or combination is missing"
					 * ); if (LOGGER.isDebugEnabled()) { LOGGER.debug(
					 * "Bifurcation for Document {} for groupCode {}", doc,
					 * groupCode); } // If the document does not have any
					 * validation errors, // then // the prList will be null. In
					 * that case we need to // create a // new list and add the
					 * errors to the list. if (prList != null) { prList.add(pr);
					 * } else { prList = new ArrayList<>(); prList.add(pr);
					 * processingResults.put(docKey, prList); } }
					 */
				});
	}

	private void configureOnboardingParams(
			List<InwardTransDocument> documents) {
		// Onboarding Config - Update Org. Hierarchy Attributes
		orgHierarchyMasterDataUpdation
				.updateOrgHierarchyMasterDataValForInwardDocs(documents);
		// Add Additional Master Data
		additionalMasterDataAddition.addAdditionalMasterData(documents);
	}

	private void markDuplicateDocuments(List<InwardTransDocument> docs) {

		// Get the map of documents will the keys and values as the list of
		// documents.
		// Map<String, List<OutwardTransDocument>> retResultMap = new
		// HashMap<>();

		Map<String, List<InwardTransDocument>> allDocsMap = docs.stream()
				.collect(Collectors
						.groupingBy(doc -> docKeyGen.generateKey(doc)));

		// Filter out the documents that have more than one element in the value
		// list.
		Map<String, List<InwardTransDocument>> duplicatesMap = allDocsMap
				.entrySet().stream().filter(e -> e.getValue().size() > 1)
				.collect(Collectors.toMap(Map.Entry::getKey,
						Map.Entry::getValue));

		// Iterate over all the documents in the duplicatesMap and set
		// isDuplicate = true.
		duplicatesMap.entrySet().forEach(entry -> {
			// String key = entry.getKey();
			List<InwardTransDocument> value = entry.getValue();
			int lastIndex = value.size() - 1;
			IntStream.range(0, value.size()).forEach(idx -> {
				InwardTransDocument item = value.get(idx);

				if (idx != lastIndex) {
					item.setDeleted(true);
					item.setIsError(true);
					item.setErrCodes(GSTConstants.ER1272);
				}
			});
		});
	}

	private int errorCount(List<InwardTransDocument> savedDocs) {

		List<InwardTransDocument> errorDocs = savedDocs.stream()
				.filter(doc -> doc.getIsError()).collect(Collectors.toList());
		return errorDocs.size();
	}

	private void softDeleteTheLockedRecord(
			List<InwardTransDocument> savedDocs) {
		List<String> docKeys = null;
		List<Object[]> orgDocDetails = new ArrayList<>();
		List<InwardTransDocument> docs = savedDocs.stream()
				.filter(doc -> !doc.isDeleted()).collect(Collectors.toList());
		if (docs != null && !docs.isEmpty()) {
			Set<String> keys = new HashSet<>();
			docs.stream().forEach(doc -> keys.add(
					DefaultInwardDocSave240Service.reconDocKey2aprgeneration(doc)));
			docKeys = new ArrayList<>(keys);

			Config config = configManager.getConfig("EYInternal",
					"outward.save.chunksize");
			String chnkSizeStr = config != null ? config.getValue() : "2000";
			int chunkSize = Integer.parseInt(chnkSizeStr);
			List<List<String>> docKeyChunks = Lists.partition(docKeys,
					chunkSize);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Generated %d Chunks of DocKeys of each Size is "
								+ "%d in orgsoftDeleteTheLOckedRecord doc check",
						docKeyChunks.size(), chunkSize);
				LOGGER.debug(msg);
			}

			if (!docKeyChunks.isEmpty()) {
				docKeyChunks.forEach(chunk -> orgDocDetails
						.addAll(gstr2ReconResultRespPsdRepository
								.getDataByDocKey(chunk)));
			}

			if (orgDocDetails != null && !orgDocDetails.isEmpty()) {
				Map<String, Pair<String, String>> mapPair = new HashMap<>();
				for (Object data[] : orgDocDetails) {
					mapPair.put((String) data[0],
							new Pair<>((String) data[1], (String) data[2]));
				}
				for (InwardTransDocument doc : docs) {
					Pair<String, String> pairData = mapPair
							.get(DefaultInwardDocSave240Service
									.reconDocKeygeneration(doc));
					if (pairData != null) {
						if ((!Strings.isNullOrEmpty(pairData.getValue0())
								&& LOCK.contains(
										pairData.getValue0().toUpperCase()))
								|| !Strings
										.isNullOrEmpty(pairData.getValue1())) {
							doc.setDeleted(true);
							doc.setIsError(true);
							doc.setErrCodes(GSTConstants.ER1273);
						}
					}
				}
			}
		}
	}

	private void duplicateCheckBasedOnOnboarding(
			List<InwardTransDocument> documents) {
		try {
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"duplicateCheckBasedOnOnboarding start", CLASS_NAME,
					METHOD_NAME, null);
			String paramI44 = CONFIG_PARAM_INWARD_QUE_KEY_ID.I44.name();
			String paramI46 = CONFIG_PARAM_INWARD_QUE_KEY_ID.I46.name();
			util = StaticContextHolder.getBean(
					"OnboardingQuestionValidationsUtil",
					OnboardingQuestionValidationsUtil.class);

			List<String> taxPeriodLevelDocs = new ArrayList<>();
			List<String> fyLevelDocs = new ArrayList<>();
			Map<String, String> configMap = new HashMap<>();

			for (InwardTransDocument document : documents) {

				String configKeyi44 = String.format("%s|%s",
						document.getEntityId(), paramI44);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("configKeyi44-: {} ", configKeyi44);
				}
				if (!configMap.containsKey(configKeyi44)) {
					String paramtrvalue = configPrtRepo
							.findByEntityAndQuestionType(
									document.getEntityId());

					configMap.putIfAbsent(configKeyi44, paramtrvalue);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("configKeyi44- paramtrvalue: {} ",
								paramtrvalue);
					}
				}
				if ("A".equals(configMap.get(configKeyi44))) {
					String configKeyi46 = String.format("%s|%s",
							document.getEntityId(), paramI46);
					if (!configMap.containsKey(configKeyi46)) {
						String paramtrValueI46 = configPrtRepo
								.findByEntityAndQuestionTypeSR(
										document.getEntityId());
						configMap.putIfAbsent(configKeyi46, paramtrValueI46);
					}
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("configKeyi44- A: {} ", configKeyi44);
					}
					if ("A".equals(configMap.get(configKeyi46))) {
						taxPeriodLevelDocs.add(docKeyGen.generateKey(document));
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("configKeyi46- A: {} ", configKeyi46);
						}
					} else if ("B".equals(configMap.get(configKeyi46))) {
						fyLevelDocs.add(docKeyGen.generateKey(document));
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("configKeyi46- B: {} ", configKeyi46);
						}
					}
				}
			}
			// fy+sgstin+cgstin+docNUm+docType+taxperiod
			Set<String> inwardDocKeysWithTaxPeriod = new HashSet<>();
			int chunkSize = 2000;
			List<List<String>> docKeyChunks = Lists
					.partition(taxPeriodLevelDocs, chunkSize);
			if (!docKeyChunks.isEmpty()) {
				for (List<String> chunk : docKeyChunks) {
					inwardDocKeysWithTaxPeriod.addAll(docHeaderRepository
							.findByDocKeyInAndIsDeletedFalseWithTaxPeriod(
									chunk));
				}
			}

			if (!inwardDocKeysWithTaxPeriod.isEmpty()) {
				for (InwardTransDocument docFile : documents) {
					if (inwardDocKeysWithTaxPeriod
							.contains(docFile.getTaxperiod()
									+ docKeyGen.generateKey(docFile))) {
						docFile.setDeleted(true);
						docFile.setIsError(true);
						docFile.setErrCodes("ER1277");
						docFile.setOldtaxperiod(docFile.getTaxperiod());
					}
				}
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("fy+sgstin+cgstin+docNUm+docType+taxperiod");
				}
			}
			// fy+sgstin+cgstin+docNUm+docType
			Map<String, String> inwardDocKeysWithFyMap = new HashMap<>();
			List<List<String>> docKeyChunksWithFy = Lists.partition(fyLevelDocs,
					chunkSize);
			if (!docKeyChunksWithFy.isEmpty()) {
				for (List<String> chunk : docKeyChunksWithFy) {
					/*
					 * inwardDocKeysWithFy.addAll(docHeaderRepository
					 * .findByDocKeyInAndIsDeletedFalse(chunk));
					 */
					List<String> inwardDocKeysWithFyList = docHeaderRepository
							.findByDocKeyInAndIsDeletedFalseWithTaxPeriod(
									chunk);
					if (inwardDocKeysWithFyList != null
							&& !inwardDocKeysWithFyList.isEmpty()) {
						inwardDocKeysWithFyList.stream().forEach(x -> {
							inwardDocKeysWithFyMap.put(x.substring(6),
									x.substring(0, 6));
						});
					}

				}
			}

			if (!inwardDocKeysWithFyMap.isEmpty()) {
				for (InwardTransDocument docFile : documents) {
					if (inwardDocKeysWithFyMap
							.containsKey(docKeyGen.generateKey(docFile))) {
						docFile.setDeleted(true);
						docFile.setIsError(true);
						docFile.setErrCodes("ER1277");
						docFile.setOldtaxperiod(inwardDocKeysWithFyMap
								.get(docKeyGen.generateKey(docFile)));
					}
				}
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("fy+sgstin+cgstin+docNUm+docType");
				}
			}
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"duplicateCheckBasedOnOnboarding end", CLASS_NAME,
					METHOD_NAME, null);
		} catch (Exception e) {
			LOGGER.error(
					"Exception while softdeleting records based on docKey and taxPeriod",
					e);
		}

	}
}
