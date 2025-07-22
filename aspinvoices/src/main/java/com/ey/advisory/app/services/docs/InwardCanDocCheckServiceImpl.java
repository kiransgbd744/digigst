package com.ey.advisory.app.services.docs;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.app.caches.StateCache;
import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.Gstr2BReconResultRespPsdRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2ReconResultRespPsdRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.InwardTransDocRepository;
import com.ey.advisory.app.services.validation.sales.DataSecurityApplicabilityChecker;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.app.util.OnboardingConstant.CONFIG_PARAM_INWARD_QUE_KEY_ID;
import com.ey.advisory.app.util.OnboardingConstant.CONFIG_PARAM_OUTWARD_ANS_KEY_ID;
import com.ey.advisory.app.util.OnboardingQuestionValidationsUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.PerfamanceEventConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

@Service("InwardCanDocCheckServiceImpl")
@Slf4j
public class InwardCanDocCheckServiceImpl implements OriginalDocCheckService {

	@Autowired
	@Qualifier("Ehcachegstin")
	private Ehcachegstin ehcachegstin;

	@Autowired
	@Qualifier("DefaultStateCache")
	private StateCache stateCache;

	@Autowired
	private DocKeyGenerator<InwardTransDocument, String> docKeyGen;
	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Autowired
	@Qualifier("InwardTransDocRepository")
	private InwardTransDocRepository docRepository;

	@Autowired
	@Qualifier("DataSecurityApplicabilityChecker")
	private DataSecurityApplicabilityChecker onboardAttributesChecking;
	@Autowired
	@Qualifier("OnboardingQuestionValidationsUtil")
	private OnboardingQuestionValidationsUtil util;

	@Autowired
	@Qualifier("Gstr2ReconResultRespPsdRepository")
	private Gstr2ReconResultRespPsdRepository gstr2ReconResultRespPsdRepository;

	@Autowired
	@Qualifier("Gstr2BReconResultRespPsdRepository")
	private Gstr2BReconResultRespPsdRepository gstr2BReconResultRespPsdRepository;

	private static final String CLASS_NAME = "InwardCanDocCheckServiceImpl";

	private static final String PIPE = "|";

	private static final List<String> LOCK = ImmutableList.of("LOCK", "LOCK2");
	
	private static final List<String> IMPG_DOC_TYPES = ImmutableList.of("INV",
			"SLF", "CR", "DR", "RNV", "RSLF", "RCR", "RDR");

	private static final List<String> DOC_TYPES = ImmutableList.of("INV", "CR",
			"DR", "RNV", "RCR", "RDR");

	public List<ProcessingResult> validate(InwardTransDocument doc) {
		String groupCode = TenantContext.getTenantId();
		List<ProcessingResult> errors = new ArrayList<>();
		if (GSTConstants.URP.equalsIgnoreCase(doc.getSgstin()))
			return errors;

		if (doc.getCgstin() != null && !doc.getCgstin().isEmpty()) {
			ehcachegstin = StaticContextHolder.getBean("Ehcachegstin",
					Ehcachegstin.class);

			GSTNDetailEntity gstin = ehcachegstin.getGstinInfo(groupCode,
					doc.getCgstin());
			if (gstin == null) {

				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.RecipientGSTIN);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER1036",
						" Recipient GSTIN is not as per On-Boarding data",
						location));
			}
		}

		return errors;
	}

	public List<ProcessingResult> validateFreezRecords(
			InwardTransDocument document, ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();
		String gstin = document.getCgstin();
		String taxPeriod = document.getTaxperiod();
		Set<String> filedSet = (Set<String>) context.getAttribute("filedSet");
		String key = gstin + PIPE + taxPeriod;
		if (filedSet != null && filedSet.contains(key)) {
			errorLocations.add(GSTConstants.DOC_NO);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER1276",
					"GSTR6 for selected tax period  is already filed",
					location));
			document.setDeleted(true);
			document.setIsError(true);
			document.setErrCodes("ER1276");
		}

		return errors;
	}

	@Override
	public Map<String, List<ProcessingResult>> checkForInwardCrDrOrgInvoices(
			List<InwardTransDocument> docs, ProcessingContext context) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Inward Function to check whether Cancel invoice is present or not in the db with ");
			LOGGER.debug(msg);
		}

		Map<String, List<ProcessingResult>> canLooksProcessingResult = CanDocLookUp(
				docs, context);

		return canLooksProcessingResult;
	}

	private Map<String, List<ProcessingResult>> CanDocLookUp(
			List<InwardTransDocument> docs, ProcessingContext context) {

		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"ORG_DOC_CHECK_BUSINESS_VALIDATION_START", CLASS_NAME,
				"CanDocLookUp", null);

		Map<String, List<ProcessingResult>> retResultMap = new HashMap<>();
		try {
			Set<String> docKeySet = new HashSet<>();
			List<String> docKeys = new ArrayList<>();
			List<Object[]> orgDocDetails = new ArrayList<>();

			List<InwardTransDocument> canDocs = docs.stream()
					.filter(doc -> GSTConstants.CAN.equalsIgnoreCase(
							doc.getSupplyType()) && !doc.isDeleted())
					.collect(Collectors.toList());

			canDocs.forEach(doc -> docKeySet.add(docKeyGen.generateKey(doc)));
			docKeys = new ArrayList<>(docKeySet);

			if (LOGGER.isDebugEnabled()) {

				LOGGER.debug("To check cancel Invoices The number of dockeys "
						+ "recieved from the iteration is : " + docKeys.size());
			}

			Config config = configManager.getConfig("EYInternal",
					"outward.save.chunksize");
			String chnkSizeStr = config != null ? config.getValue() : "2000";
			int chunkSize = Integer.parseInt(chnkSizeStr);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Generated DocKeys : %s, Size is %d,"
								+ " About to chunk the Dockeys list in org doc check....",
						docKeys, docKeys.size());
				LOGGER.debug(msg);
			}

			List<List<String>> docKeyChunks = Lists.partition(docKeys,
					chunkSize);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Generated %d Chunks of DocKeys of each Size is %d in org doc check",
						docKeyChunks.size(), chunkSize);
				LOGGER.debug(msg);
			}

			if (!docKeyChunks.isEmpty()) {
				docKeyChunks.forEach(chunk -> orgDocDetails.addAll(
						docRepository.findCancelDocsCountsByDocKeys(chunk)));
			}
			if (LOGGER.isDebugEnabled()) {

				LOGGER.debug(
						"The Org Details Size %d: "
								+ orgDocDetails.size());
				LOGGER.debug(
						"The Org Details %s : "
								+ orgDocDetails.toString());
			}

			Map<String, Boolean> orgDocKeyMap = orgDocDetails.stream()
					.collect(Collectors.toMap(obj -> String.valueOf(obj[0]),
							obj -> (Boolean) obj[1], (obj1, obj2) -> obj1));

			if (LOGGER.isDebugEnabled()) {

				LOGGER.debug(
						"The number of original records fetch from db is : "
								+ orgDocKeyMap.size());
				LOGGER.debug(
						"The number of original records fetch from db is : "
								+ orgDocKeyMap);
			}
			List<ProcessingResult> validateLockedRecord = softDeleteTheLockedRecord(
					canDocs, docKeySet, false, retResultMap);
			List<ProcessingResult> validate2BLockedRecord = softDeleteTheGstr2BLockedRecord(
					canDocs, docKeySet, false, retResultMap);
			canDocs.forEach(doc -> {
				List<ProcessingResult> results = new ArrayList<>();
				List<ProcessingResult> validate = validate(doc);
				List<ProcessingResult> validateFreez = validateFreezRecords(doc,
						context);
				List<ProcessingResult> validateGstin = validate(doc, context);

				results.addAll(validate);
				results.addAll(validateFreez);
				results.addAll(validateGstin);
				results.addAll(validateLockedRecord);
				results.addAll(validate2BLockedRecord);
				String docKey = docKeyGen.generateKey(doc);
				Boolean IsSubmittedFlag = orgDocKeyMap.get(docKey);
				if (IsSubmittedFlag == null) {
					List<String> errorLocations = new ArrayList<>();
					errorLocations.add(GSTConstants.DOC_NO);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							errorLocations.toArray());

					results.add(new ProcessingResult(APP_VALIDATION, "ER1156",
							"Document cannot be cancelled as the "
									+ "same was not reported to ASP System",
							location));
				}
				if (IsSubmittedFlag != null) {
					if (IsSubmittedFlag) {
						List<String> errorLocations = new ArrayList<>();
						errorLocations.add(GSTConstants.DOC_NO);
						TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
								errorLocations.toArray());
						results.add(
								new ProcessingResult(APP_VALIDATION, "ER1157",
										"Document cannot be cancelled as the same has "
												+ "been submitted on GSTN portal",
										location));
					}

				}

				if (!results.isEmpty()) {
					Boolean complianceError = results.stream().anyMatch(
							r -> r.getType() == ProcessingResultType.ERROR);

					if (complianceError) {
						doc.setIsProcessed(false);
						doc.setIsError(true);

					} else {
						doc.setIsProcessed(true);
						doc.setIsError(false);

					}
				} else {
					doc.setIsProcessed(true);
					doc.setIsError(false);

				}

				retResultMap.put(docKey, results);

			});
		} catch (Exception ex) {
			LOGGER.error("An exception occured while looking for the "
					+ "orginal doccument for Inward CAN invoices : Exception "
					+ "is : ", ex);
		}

		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"ORG_DOC_CHECK_BUSINESS_VALIDATION_END", CLASS_NAME,
				"CanDocLookUp", null);

		return retResultMap;

	}

	@Override
	public Map<String, List<ProcessingResult>> checkForCrDrOrgInvoices(
			List<OutwardTransDocument> docs, Boolean isIntegrated,
			ProcessingContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<ProcessingResult> validate(InwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		@SuppressWarnings("unused")
		String groupCode = TenantContext.getTenantId();

		util = StaticContextHolder.getBean("OnboardingQuestionValidationsUtil",
				OnboardingQuestionValidationsUtil.class);
		Map<Long, List<EntityConfigPrmtEntity>> entityConfigParamMap = document
				.getEntityConfigParamMap();

		String gstin = document.getCgstin();

		if (gstin != null && !gstin.isEmpty()) {
			String paramkeyId12 = CONFIG_PARAM_INWARD_QUE_KEY_ID.I12.name();
			String paramterValue12 = util.valid(entityConfigParamMap,
					paramkeyId12, document.getEntityId());
			if (CONFIG_PARAM_OUTWARD_ANS_KEY_ID.A.name()
					.equalsIgnoreCase(paramterValue12)
					&& document.getDataOriginTypeCode().equalsIgnoreCase("E")) {

				onboardAttributesChecking = StaticContextHolder.getBean(
						"DataSecurityApplicabilityChecker",
						DataSecurityApplicabilityChecker.class);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("gstinPermissionValidator begin");
				}
				boolean isAttrUserValid = onboardAttributesChecking.isAttrValid(
						OnboardingConstant.GSTIN, gstin, document.getEntityId(),
						document.getCreatedBy());
				if (!isAttrUserValid) {
					errorLocations.add(GSTConstants.RecipientGSTIN);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER1279",
							"User is not authorized for GSTIN provided in upload file.",
							location));
				}
			}
		}

		return errors;
	}

	public List<ProcessingResult> softDeleteTheLockedRecord(
			List<InwardTransDocument> docs, Set<String> dockeySet,
			boolean flag, Map<String, List<ProcessingResult>> retResultMap) {

		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();

		List<String> docKeys = null;
		List<Object[]> orgDocDetails = new ArrayList<>();
		try {
			if (docs != null && !docs.isEmpty()) {
				Set<String> keys = new HashSet<>();
				docs.stream()
						.forEach(doc -> keys.add(reconDocKey2aprgeneration(doc)));
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
				
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Dockeys- {}",docKeys.toString());
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
							.addAll(gstr2ReconResultRespPsdRepository
									.getDataByDocKey(chunk)));
				}
				
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("orgDocDetails- {}",orgDocDetails.toString());
				}

				if (!orgDocDetails.isEmpty()) {
					Map<String, Pair<String, String>> mapPair = new HashMap<>();
					for (Object data[] : orgDocDetails) {
						mapPair.put((String) data[0],
								new Pair<>((String) data[1], (String) data[2]));
					}
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("mapPair- {}",mapPair.toString());
					}

					for (InwardTransDocument doc : docs) {
						Pair<String, String> pairData = mapPair
								.get(reconDocKey2aprgeneration(doc));
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("pairData- {}",pairData);
						}
						if (pairData != null) {
							if ((!Strings.isNullOrEmpty(pairData.getValue0())
									&& LOCK.contains(
											pairData.getValue0().toUpperCase()))
									|| !Strings.isNullOrEmpty(
											pairData.getValue1())) {
								if (!flag) {
									errorLocations.add(GSTConstants.DOC_NO);
									TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
											null, errorLocations.toArray());
									errors.add(new ProcessingResult(APP_VALIDATION,
											"ER1273",
											"Document already exists and locked as Force Match/GSTR 3B Response",
											location));
									doc.setDeleted(true);
									doc.setIsError(true);
									doc.setErrCodes(GSTConstants.ER1273);
									if (LOGGER.isDebugEnabled()) {
										LOGGER.debug("errors- {}",errors.toString());
									}
								}
								dockeySet.add(doc.getDocKey());
								retResultMap.put(doc.getDocKey(), errors);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error(
					"error while doing lokUP " + "on gstr2A locked records", e);
		}
		return errors;
	}

	public List<ProcessingResult> softDeleteTheGstr2BLockedRecord(
			List<InwardTransDocument> documents, Set<String> dockeySet,
			boolean flag, Map<String, List<ProcessingResult>> retResultMap) {

		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();

		List<String> docKeys = null;
		List<Object[]> orgDocDetails = new ArrayList<>();
		try {
			List<InwardTransDocument> docs = documents.stream()
					.filter(doc -> !doc.isDeleted())
					.collect(Collectors.toList());
			if (docs != null && !docs.isEmpty()) {
				Set<String> keys = new HashSet<>();
				docs.stream()
						.forEach(doc -> keys.add(reconDocKeygeneration(doc)));

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
				if (LOGGER.isDebugEnabled()
						&& (orgDocDetails == null || orgDocDetails.isEmpty())) {
					LOGGER.debug("2b locked records data is empty ");
				}
				if (orgDocDetails != null && !orgDocDetails.isEmpty()) {
					Map<String, Pair<String, String>> mapPair = new HashMap<>();
					for (Object data[] : orgDocDetails) {
						mapPair.put((String) data[0],
								new Pair<>((String) data[1], (String) data[2]));
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("2B locked record data map:{}",
									mapPair);
						}
					}

					for (InwardTransDocument doc : docs) {
						Pair<String, String> pairData = mapPair
								.get(reconDocKeygeneration(doc));
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("2B locked record data pair:{}",
									pairData);
						}
						if (pairData != null) {
							if ((!Strings.isNullOrEmpty(pairData.getValue0())
									&& LOCK.contains(
											pairData.getValue0().toUpperCase()))
									|| !Strings.isNullOrEmpty(
											pairData.getValue1())) {
								if (LOGGER.isDebugEnabled()) {
									LOGGER.debug(
											"Marking docKey:{} as softDeleted",
											doc.getDocKey());
								}
								if (!flag) {
									errorLocations.add(GSTConstants.DOC_NO);
									TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
											null, errorLocations.toArray());
									errors.add(new ProcessingResult(
											APP_VALIDATION, "ER1273",
											"Document already exists and locked as Force Match/GSTR 3B Response",
											location));
									doc.setDeleted(true);
									doc.setIsError(true);
									doc.setErrCodes(GSTConstants.ER1273);
								}
								dockeySet.add(doc.getDocKey());
								retResultMap.put(doc.getDocKey(), errors);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error(
					"Error while doing loKup " + "on gstr2b locked records", e);

		}
		return errors;
	}
	
	public static String reconDocKeygeneration(InwardTransDocument doc) {
		String cgstin = doc.getCgstin();
		String gstin = doc.getSgstin();
		String processingKey = "";
		String billOfEntryDate = "";
		if (doc.getBillOfEntryDate() != null) {
			LocalDate now = doc.getBillOfEntryDate();
			String day=String.valueOf(now.getDayOfMonth());
	    	if(day.length()==1){
	    		day="0"+day;
	    	}
	    	String month=String.valueOf(now.getMonthValue());
	    	if(month.length()==1){
	    		month="0"+month;
	    	}
	    	
	    	//System.out.println(LocalDate.now());
	    	//System.out.println(day+"-"+month+"-"+now.getYear());
	    	billOfEntryDate=day+"-"+month+"-"+now.getYear();
		}
		if (Strings.isNullOrEmpty(gstin)) {
			// IMPG and //IMPGA
			 processingKey = new StringJoiner(PIPE).add(cgstin)
					.add(doc.getBillOfEntryNo()).add(billOfEntryDate).toString();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("processing key:{} for dockey:{}", processingKey,
						doc.getDocKey());
			}
			return processingKey;

		} else if (!Strings.isNullOrEmpty(gstin)) {
			// IMPGS and //IMPGSA
			 processingKey = new StringJoiner(PIPE).add(cgstin)
					.add(gstin).add(doc.getBillOfEntryNo()).add(billOfEntryDate)
					.toString();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("processing key{} for dockey{}", processingKey,
						doc.getDocKey());
			}
			return processingKey;

		}
		return processingKey;
	}
	
	public static String reconDocKey2aprgeneration(InwardTransDocument doc) {
		String cgstin = doc.getCgstin();
		String gstin = doc.getSgstin();
		String processingKey = "";
		String billOfEntryDate = "";
		if (doc.getBillOfEntryDate() != null) {
			LocalDate now = doc.getBillOfEntryDate();
			String day=String.valueOf(now.getDayOfMonth());
	    	if(day.length()==1){
	    		day="0"+day;
	    	}
	    	String month=String.valueOf(now.getMonthValue());
	    	if(month.length()==1){
	    		month="0"+month;
	    	}
	    	billOfEntryDate=now.getYear()+"-"+month+"-"+day;
		}
		if (Strings.isNullOrEmpty(gstin)) {
			// IMPG and //IMPGA
			 processingKey = new StringJoiner(PIPE).add(cgstin)
					.add(doc.getBillOfEntryNo()).add(billOfEntryDate)
					.toString();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("processing key:{} for dockey:{}", processingKey,
						doc.getDocKey());
			}
			return processingKey;

		} else if (!Strings.isNullOrEmpty(gstin)) {
			// IMPGS and //IMPGSA
			 processingKey = new StringJoiner(PIPE).add(cgstin)
					.add(gstin).add(doc.getBillOfEntryNo()).add(billOfEntryDate)
					.toString();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("processing key{} for dockey{}", processingKey,
						doc.getDocKey());
			}
			return processingKey;

		}
		return processingKey;
	}

}
