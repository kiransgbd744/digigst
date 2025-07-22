package com.ey.advisory.app.services.docs;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.repositories.client.SignAndFileRepository;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.app.services.validation.eInvoice.EwbStatusValidation;
import com.ey.advisory.app.services.validation.eInvoice.LuTransactionTypeValidation;
import com.ey.advisory.app.services.validation.eInvoice.SuSupplyTypeValidation;
import com.ey.advisory.app.services.validation.eInvoice.SubSupplyTypeMandatory;
import com.ey.advisory.app.services.validation.eInvoice.TransactionTypeMandatory;
import com.ey.advisory.app.services.validation.sales.DataSecurityApplicabilityChecker;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.app.util.OnboardingQuestionValidationsUtil;
import com.ey.advisory.app.util.OnboardingConstant.CONFIG_PARAM_OUTWARD_ANS_KEY_ID;
import com.ey.advisory.app.util.OnboardingConstant.CONFIG_PARAM_OUTWARD_QUE_KEY_ID;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AspInvoiceStatus;
import com.ey.advisory.common.EInvoiceStatus;
import com.ey.advisory.common.EinvEwbJobStatus;
import com.ey.advisory.common.EwbProcessingStatus;
import com.ey.advisory.common.EwbStatusNew;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.IrnStatusMaster;
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

@Service("CanDocCheckServiceImpl")
@Slf4j
public class CanDocCheckServiceImpl implements OriginalDocCheckService {

	@Autowired
	@Qualifier("Ehcachegstin")
	private Ehcachegstin ehcachegstin;

	@Autowired
	@Qualifier("OnboardingQuestionValidationsUtil")
	private OnboardingQuestionValidationsUtil util;

	@Autowired
	@Qualifier("DataSecurityApplicabilityChecker")
	private DataSecurityApplicabilityChecker onboardAttributesChecking;

	@Autowired
	private DocKeyGenerator<OutwardTransDocument, String> docKeyGen;
	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Autowired
	@Qualifier("DocRepository")
	DocRepository docRepository;

	@Autowired
	@Qualifier("SignAndFileRepository")
	SignAndFileRepository signAndFileRepository;
	private static final String DOC_KEY_JOINER = "|";

	private static final List<String> DOCTYPE_IMPORTS = ImmutableList
			.of(GSTConstants.INV, GSTConstants.CR, GSTConstants.CR);

	private static final List<String> REGYPE_ONBOARD_IMPORTS = ImmutableList
			.of(GSTConstants.SEZD, GSTConstants.SEZU, GSTConstants.REGULAR);

	private static final List<String> REGYPE_IMPORTS = ImmutableList.of(
			GSTConstants.SEZD, /* GSTConstants.SEZU, */ GSTConstants.REGULAR);
	private static final List<String> TAX_SCHEMA_LIST_EWB = ImmutableList
			.of(GSTConstants.NEWB, GSTConstants.NBOTH, GSTConstants.NRETEWB);
	private static final List<String> TAX_SCHEMA_LIST_INVOICE = ImmutableList
			.of(GSTConstants.NBOTH, GSTConstants.NEINV, GSTConstants.NRETEINV);
	private static final List<Integer> CAN_REASON = ImmutableList.of(

			1, 2, 3, 4);

	private static final List<String> CGSTIN_FORMAT = ImmutableList
			.of(GSTConstants.UN, GSTConstants.ON);
	private static final String CLASS_NAME = "CanDocCheckServiceImpl";
	private static final DocRulesValidator[] EWB_APPLICABLE = {
			new TransactionTypeMandatory(), new SubSupplyTypeMandatory(),
			new SuSupplyTypeValidation(), new LuTransactionTypeValidation(), };

	@Override
	public Map<String, List<ProcessingResult>> checkForCrDrOrgInvoices(
			List<OutwardTransDocument> docs, Boolean isIntegrated,
			ProcessingContext context) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("Function to check whether Cancel invoice is present or not in the db with "
							+ "isIntegrated : " + isIntegrated);
			LOGGER.debug(msg);
		}

		Map<String, List<ProcessingResult>> canLooksProcessingResult = CanDocLookUp(
				docs, isIntegrated, context);
		/*
		 * List<OutwardTransDocument> returnDocs = docs.stream() .filter(doc ->
		 * doc.getComplianceApplicable() && !doc.isDeleted())
		 * .collect(Collectors.toList()); Map<String, List<ProcessingResult>>
		 * uniqueRecordsProcessingResult = uniqueRecordwithiFinancialYear(
		 * returnDocs, isIntegrated);
		 * 
		 * HashMap<String, List<ProcessingResult>> processingCrdrResults = new
		 * HashMap<>( canLooksProcessingResult);
		 * 
		 * uniqueRecordsProcessingResult .forEach((key, value) ->
		 * processingCrdrResults.merge(key, value, (v1, v2) -> Stream.of(v1,
		 * v2).flatMap(x -> x.stream()) .collect(Collectors.toList())));
		 */

		return canLooksProcessingResult;
	}

	public List<ProcessingResult> validate(OutwardTransDocument document) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (!document.getComplianceApplicable())
			return errors;
		String groupCode = TenantContext.getTenantId();
		if (GSTConstants.I.equalsIgnoreCase(document.getTransactionType())) {
			if (Strings.isNullOrEmpty(document.getCgstin())) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.CGSTIN);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER0049",
						"Recipient GSTIN cannot be left Blank.", location));
				return errors;
			}
			if (GSTConstants.URP.equalsIgnoreCase(document.getCgstin())) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.CGSTIN);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER15168",
						"Customer GSTIN is not as per On-Boarding data",
						location));
				return errors;
			}
			ehcachegstin = StaticContextHolder.getBean("Ehcachegstin",
					Ehcachegstin.class);

			GSTNDetailEntity gstin = ehcachegstin.getGstinInfo(groupCode,
					document.getCgstin());
			if (gstin == null) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.CGSTIN);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER15168",
						"Customer GSTIN is not as per On-Boarding data",
						location));
			}

			return errors;

		} else {
			if (Strings.isNullOrEmpty(document.getSgstin())) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.SGSTIN);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER0025",
						"Supplier GSTIN cannot be left balnk.", location));
			}
			if (GSTConstants.URP.equalsIgnoreCase(document.getSgstin())) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.SGSTIN);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER0026",
						"Supplier GSTIN is not as per On-Boarding data",
						location));
				return errors;
			}
			ehcachegstin = StaticContextHolder.getBean("Ehcachegstin",
					Ehcachegstin.class);

			GSTNDetailEntity gstin = ehcachegstin.getGstinInfo(groupCode,
					document.getSgstin());
			if (gstin == null) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.SGSTIN);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER0026",
						"Supplier GSTIN is not as per On-Boarding data",
						location));
				return errors;
			}
			if (!REGYPE_ONBOARD_IMPORTS.contains(gstin.getRegistrationType())) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.SGSTIN);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(
						new ProcessingResult(APP_VALIDATION, "ER15031",
								"Registration Type of Supplier GSTIN is other "
										+ "than Regular / SEZU / SEZD",
								location));
			}
		}
		return errors;
	}

	public Map<String, List<ProcessingResult>> validateFreezRecords(
			List<OutwardTransDocument> documents, ProcessingContext context) {
		try {
			String paramkryId = CONFIG_PARAM_OUTWARD_QUE_KEY_ID.O36.name();
			OnboardingQuestionValidationsUtil util = StaticContextHolder
					.getBean("OnboardingQuestionValidationsUtil",
							OnboardingQuestionValidationsUtil.class);
			Set<String> filedSet = (Set<String>) context
					.getAttribute("filedSet");

			LOGGER.debug("documents {}  ", documents.size());

			List<String> taxPeriodLevelDocs = new ArrayList<>();
			Map<Long, String> entityOptedFreeze = new HashMap<>();

			for (OutwardTransDocument document : documents) {
				Map<Long, List<EntityConfigPrmtEntity>> entityConfigParamMap = document
						.getEntityConfigParamMap();
				String paramtrvalue = null;
				if (entityOptedFreeze.containsKey(document.getEntityId())) {
					paramtrvalue = entityOptedFreeze
							.get(document.getEntityId());
				} else {
					paramtrvalue = util.valid(entityConfigParamMap, paramkryId,
							document.getEntityId());
				}
				if (paramtrvalue != null && CONFIG_PARAM_OUTWARD_ANS_KEY_ID.A
						.name().equalsIgnoreCase(paramtrvalue)) {
					taxPeriodLevelDocs.add(docKeyGen.generateKey(document));
				}
			}

			Map<String, List<ProcessingResult>> retResultMap = new HashMap<>();

			LOGGER.debug("taxPeriodLevelDocs {}  ", taxPeriodLevelDocs);

			if (taxPeriodLevelDocs.isEmpty())
				return retResultMap;

			Set<String> outwardDocKeysWithTaxPeriod = new HashSet<>();

			int chunkSize = 2000;
			List<List<String>> docKeyChunks = Lists
					.partition(taxPeriodLevelDocs, chunkSize);
			if (!docKeyChunks.isEmpty()) {
				for (List<String> chunk : docKeyChunks) {
					outwardDocKeysWithTaxPeriod.addAll(docRepository
							.findByDocKeyInAndIsDeletedFalseWithTaxPeriod(
									chunk));
				}
			}

			LOGGER.debug(" outwardDocKeysWithTaxPeriod {} ",
					outwardDocKeysWithTaxPeriod);

			// docKey,previoustaxperiod
			Map<String, String> outwrdDocMap = outwardDocKeysWithTaxPeriod
					.stream()
					.collect(Collectors.toMap(
							p -> String.valueOf(p).substring(6),
							p -> String.valueOf(p).substring(0, 6)));

			LOGGER.debug(" outwrdDocMap {} ", outwrdDocMap);

			for (OutwardTransDocument docFile : documents) {

				String key = docKeyGen.generateKey(docFile);
				LOGGER.debug(" key1 {} docFile {} ", key, docFile);
				if (outwrdDocMap.containsKey(key)
						&& !outwardDocKeysWithTaxPeriod.isEmpty()) {

					String prevActiveTaxPeriod = outwrdDocMap
							.get(docKeyGen.generateKey(docFile));

					String key2 = docFile.getSgstin() + "|"
							+ prevActiveTaxPeriod;

					LOGGER.debug(" key2 {} ", key2);

					LOGGER.debug("filedSet {} ", filedSet);
					// taxperiod|gstin
					if (filedSet.contains(key2)) {

						List<ProcessingResult> results = new ArrayList<>();

						Set<String> errorLocations = new HashSet<>();
						errorLocations.add(GSTConstants.DOC_NO);
						TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
								null, errorLocations.toArray());
						LOGGER.debug(" inside error block ");

						if (docFile.getTaxperiod()
								.equalsIgnoreCase(prevActiveTaxPeriod)) {
							ProcessingResult result = new ProcessingResult(
									APP_VALIDATION, ProcessingResultType.ERROR,
									"ER1276",
									"GSTR1 for this tax period is already filed",
									location);
							docFile.setDeleted(true);
							docFile.setAspInvoiceStatus(
									AspInvoiceStatus.ASP_ERROR
											.getAspInvoiceStatusCode());
							results.add(result);
						} else {
							ProcessingResult result = new ProcessingResult(
									APP_VALIDATION, ProcessingResultType.ERROR,
									"ER1277",
									String.format(
											"Document is already uploaded/active in DigiGST - Tax Period : %s",
											prevActiveTaxPeriod),
									location);
							docFile.setDeleted(true);
							docFile.setAspInvoiceStatus(
									AspInvoiceStatus.ASP_ERROR
											.getAspInvoiceStatusCode());
							docFile.setOldtaxperiod(prevActiveTaxPeriod);
							results.add(result);
						}
						retResultMap.put(key, results);
					}
				} else {

					String key2 = docFile.getSgstin() + "|"
							+ docFile.getTaxperiod();

					LOGGER.debug(" key2 {} ", key2);

					LOGGER.debug("filedSet {} ", filedSet);
					// taxperiod|gstin
					if (filedSet.contains(key2)) {

						List<ProcessingResult> results = new ArrayList<>();

						Set<String> errorLocations = new HashSet<>();
						errorLocations.add(GSTConstants.DOC_NO);
						TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
								null, errorLocations.toArray());
						LOGGER.debug(" inside error block ");

						ProcessingResult result = new ProcessingResult(
								APP_VALIDATION, ProcessingResultType.ERROR,
								"ER1276",
								"GSTR1 for this tax period is already filed",
								location);
						docFile.setDeleted(true);
						docFile.setAspInvoiceStatus(AspInvoiceStatus.ASP_ERROR
								.getAspInvoiceStatusCode());
						results.add(result);
						retResultMap.put(key, results);
						
					}
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"fy+sgstin+cgstin+docNUm+docType+taxperiod");
					}
				}
			}
			return retResultMap;

		} catch (Exception ex) {
			String msg = "exception in gstr1 freeze ";
			LOGGER.error(msg);
			throw new AppException(ex);
		}
	}

	private Map<String, List<ProcessingResult>> CanDocLookUp(
			List<OutwardTransDocument> docs, Boolean isIntegrated,
			ProcessingContext context) {

		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"ORG_DOC_CHECK_BUSINESS_VALIDATION_START", CLASS_NAME,
				"CanDocLookUp", null);

		Map<String, List<ProcessingResult>> retResultMap = new HashMap<>();
		try {
			Set<String> docKeySet = new HashSet<>();
			List<String> docKeys = new ArrayList<>();
			List<Object[]> orgDocDetails = new ArrayList<>();

			List<OutwardTransDocument> canDocs = docs.stream()
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

			Map<String, Boolean> orgDocKeyMap = orgDocDetails.stream()
					.collect(Collectors.toMap(obj -> String.valueOf(obj[0]),
							obj -> (Boolean) obj[1], (obj1, obj2) -> obj1));
			if (LOGGER.isDebugEnabled()) {

				LOGGER.debug(
						"The number of original records fetch from db is : "
								+ orgDocKeyMap.size());
			}
			Map<String, List<ProcessingResult>> validateFreez = validateFreezRecords(
					canDocs, context);

			canDocs.forEach(doc -> {
				List<ProcessingResult> results = new ArrayList<>();
				List<ProcessingResult> validate = validate(doc);
				List<ProcessingResult> validateGstin = validate(doc, context);
				results.addAll(validate);
				
				if (!validateFreez.isEmpty()) {
					List<ProcessingResult> result = validateFreez
							.get(docKeyGen.generateKey(doc));
					LOGGER.debug(" result {} ",result);
					
					if (!result.isEmpty()) {
						results.addAll(result);
					}
				}
				results.addAll(validateGstin);
				String docKey = docKeyGen.generateKey(doc);
				Boolean IsSubmittedFlag = orgDocKeyMap.get(docKey);
				boolean canLookUp = true;
				if (IsSubmittedFlag == null && doc.getComplianceApplicable()) {
					canLookUp = false;
					List<String> errorLocations = new ArrayList<>();
					errorLocations.add(GSTConstants.DOC_NO);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							errorLocations.toArray());

					results.add(new ProcessingResult(APP_VALIDATION, "ER0518",
							"Document cannot be cancelled as the "
									+ "same was not reported to ASP System",
							location));
				}
				if (IsSubmittedFlag != null) {
					if (IsSubmittedFlag && doc.getComplianceApplicable()) {
						canLookUp = false;
						List<String> errorLocations = new ArrayList<>();
						errorLocations.add(GSTConstants.DOC_NO);
						TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
								errorLocations.toArray());
						results.add(
								new ProcessingResult(APP_VALIDATION, "ER0519",
										"Document cannot be cancelled as the same has "
												+ "been submitted on GSTN portal",
										location));
					}

				}

				if (isIntegrated) {

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
				}
				List<ProcessingResult> presults1 = new ArrayList<>();
				boolean ewbTaxSchema = TAX_SCHEMA_LIST_EWB
						.contains(trimAndConvToUpperCase(doc.getTaxScheme()));
				boolean hsnCheck = EwbStatusValidation.HsnEwbcheck(doc);
				boolean ewbApplCheck = EwbStatusValidation.validate(doc);
				if (ewbTaxSchema) {
					doc.setEwbApplicable(false);
				} else {
					if (hsnCheck && ewbApplCheck) {
						doc.setEwbApplicable(true);
					} else {
						doc.setEwbApplicable(false);
					}
				}
				if (GSTConstants.A.equalsIgnoreCase(doc.getOptedForEwb())) {
					if (doc.getEwbJob() == null) {
						doc.setEwbProcessingStatus(
								EwbProcessingStatus.NOT_APPLICABLE
										.getEwbProcessingStatusCode());
						doc.setEwbStatus(EwbStatusNew.NOT_APPLICABLE
								.getEwbNewStatusCode());
					}
					if (doc.getEwbJob() != null) {
						if (doc.getEwbJob() == EinvEwbJobStatus.EWB_ERP_AUTO
								.getEinvEwbJobStatusCode()
								|| doc.getEwbJob() == EinvEwbJobStatus.EWB_ERP_MANUAL
										.getEinvEwbJobStatusCode()) {
							if (doc.geteWayBillNo() != null
									&& !doc.geteWayBillNo().isEmpty()) {
								doc.setEwbProcessingStatus(
										EwbProcessingStatus.EWAY_BILL_GENERATED_ERP
												.getEwbProcessingStatusCode());

								doc.setEwbStatus(
										EwbStatusNew.EWAY_BILL_GENERATED_ERP
												.getEwbNewStatusCode());

							} else {
								doc.setEwbProcessingStatus(
										EwbProcessingStatus.EWAY_BILL_NOT_GENERATED_ERP
												.getEwbProcessingStatusCode());

								doc.setEwbStatus(
										EwbStatusNew.EWAY_BILL_NOT_GENERATED_ERP
												.getEwbNewStatusCode());
							}
						} else {
							if (ewbTaxSchema) {

								doc.setEwbProcessingStatus(
										EwbProcessingStatus.NOT_APPLICABLE
												.getEwbProcessingStatusCode());
								doc.setEwbStatus(EwbStatusNew.NOT_APPLICABLE
										.getEwbNewStatusCode());
							} else {
								if (hsnCheck) {

									DocRulesValidator[] EWBAPPLICABLE = EWB_APPLICABLE;
									for (DocRulesValidator<OutwardTransDocument> ewbValidator : EWBAPPLICABLE) {
										if (ewbValidator == null) {
											LOGGER.warn(
													"Validator cannot be null. "
															+ "Ignoring the null validator!!");
											continue;
										}
										// Get the class name of the validator,
										// for
										// logging in case
										// of
										// any errors.
										String ewbValidatorCls = ewbValidator
												.getClass().getSimpleName();
										try {
											if (LOGGER.isDebugEnabled()) {
												String msg = String.format(
														"Executing validator '%s'",
														ewbValidatorCls);
												LOGGER.debug(msg);
											}
											List<ProcessingResult> tmpResults = ewbValidator
													.validate(doc, null);
											presults1.addAll(tmpResults);
											if (LOGGER.isDebugEnabled()) {
												String msg = String.format(
														"Executed validator '%s'. No: of Results "
																+ "available: '%d'",
														ewbValidatorCls,
														(tmpResults != null)
																? tmpResults
																		.size()
																: 0);
												LOGGER.debug(msg);
											}

										} catch (Exception ex) {
											// create a processing result to
											// inform that
											// the
											// validator
											// failed.
											String exName = ex.getClass()
													.getSimpleName();
											String msg = String.format(
													"Error while executing the validator '%s'. "
															+ "Exception: '%s'",
													ewbValidatorCls, exName);
											ProcessingResult resultErr = new ProcessingResult(
													"LOCAL",
													ProcessingResultType.ERROR,
													"ER9999", msg, null);
											LOGGER.error(msg);
											presults1.add(resultErr);
										}
									}
									if (!presults1.isEmpty()) {
										doc.setEwbProcessingStatus(
												EwbProcessingStatus.ASP_ERROR
														.getEwbProcessingStatusCode());
										doc.setEwbStatus(EwbStatusNew.ASP_ERROR
												.getEwbNewStatusCode());
									} else {
										if (ewbApplCheck) {

											List<ProcessingResult> validate1 = validateCanReason(
													doc);
											List<ProcessingResult> validate2 = validateCanRemark(
													doc);
											presults1.addAll(validate1);
											presults1.addAll(validate2);
											if (!presults1.isEmpty()) {
												doc.setEwbProcessingStatus(
														EwbProcessingStatus.ASP_ERROR
																.getEwbProcessingStatusCode());
												doc.setEwbStatus(
														EwbStatusNew.ASP_ERROR
																.getEwbNewStatusCode());
											} else {
												doc.setEwbProcessingStatus(
														EwbProcessingStatus.ASP_PROCESSED
																.getEwbProcessingStatusCode());
												doc.setEwbStatus(
														EwbStatusNew.PENDING
																.getEwbNewStatusCode());
											}

										} else {
											doc.setEwbProcessingStatus(
													EwbProcessingStatus.NOT_APPLICABLE
															.getEwbProcessingStatusCode());
											doc.setEwbStatus(
													EwbStatusNew.NOT_APPLICABLE
															.getEwbNewStatusCode());
										}
									}
								} else {
									doc.setEwbProcessingStatus(
											EwbProcessingStatus.NOT_APPLICABLE
													.getEwbProcessingStatusCode());
									doc.setEwbStatus(EwbStatusNew.NOT_APPLICABLE
											.getEwbNewStatusCode());
								}
							}
						}
					}
				} else {
					doc.setEwbProcessingStatus(EwbProcessingStatus.NOT_OPTED
							.getEwbProcessingStatusCode());
					doc.setEwbStatus(
							EwbStatusNew.NOT_OPTED.getEwbNewStatusCode());
				}
				List<ProcessingResult> presults2 = new ArrayList<>();
				boolean einvTaxSchema = TAX_SCHEMA_LIST_INVOICE
						.contains(trimAndConvToUpperCase(doc.getTaxScheme()));
				if (einvTaxSchema) {
					doc.setEinvApplicable(false);
				} else {
					doc.setEinvApplicable(eInvoiceApplicability(doc));
				}
				if (GSTConstants.A.equalsIgnoreCase(doc.getOptedForEinv())) {
					if (doc.getEinvJob() == null) {
						doc.seteInvStatus(EInvoiceStatus.NOT_APPLICABLE
								.geteInvoiceStatusCode());
						doc.setIrnStatus(IrnStatusMaster.NOT_APPLICABLE
								.getIrnStatusMaster());
					}
					if (doc.getEinvJob() != null) {
						if (doc.getEinvJob() == EinvEwbJobStatus.EINV_ERP_AUTO
								.getEinvEwbJobStatusCode()
								|| doc.getEinvJob() == EinvEwbJobStatus.EINV_ERP_MANUAL
										.getEinvEwbJobStatusCode()) {
							if (doc.getIrn() == null
									|| doc.getIrn().isEmpty()) {
								doc.seteInvStatus(
										EInvoiceStatus.IRN_NOT_GENERATED_IN_ERP
												.geteInvoiceStatusCode());
							} else {
								doc.seteInvStatus(
										EInvoiceStatus.IRN_GENERATED_IN_ERP
												.geteInvoiceStatusCode());
							}
						} else {
							if (einvTaxSchema) {

								doc.seteInvStatus(EInvoiceStatus.NOT_APPLICABLE
										.geteInvoiceStatusCode());
								doc.setIrnStatus(IrnStatusMaster.NOT_APPLICABLE
										.getIrnStatusMaster());
							} else {
								if (doc.isEinvApplicable() && canLookUp) {

									List<ProcessingResult> validate1 = validateCanReason(
											doc);
									List<ProcessingResult> validate2 = validateCanRemark(
											doc);
									presults2.addAll(validate1);
									presults2.addAll(validate2);
									if (!presults2.isEmpty()) {
										doc.seteInvStatus(
												EInvoiceStatus.ASP_ERROR
														.geteInvoiceStatusCode());
										doc.setIrnStatus(
												IrnStatusMaster.ASP_ERROR
														.getIrnStatusMaster());
									} else {
										doc.seteInvStatus(
												EInvoiceStatus.ASP_PROCESSED
														.geteInvoiceStatusCode());
										doc.setIrnStatus(IrnStatusMaster.PENDING
												.getIrnStatusMaster());
									}

								} else {
									doc.seteInvStatus(
											EInvoiceStatus.NOT_APPLICABLE
													.geteInvoiceStatusCode());
									doc.setIrnStatus(
											IrnStatusMaster.NOT_APPLICABLE
													.getIrnStatusMaster());
								}
							}
						}
					}
				} else {
					doc.seteInvStatus(
							EInvoiceStatus.NOT_OPTED.geteInvoiceStatusCode());
					doc.setIrnStatus(
							IrnStatusMaster.NOT_OPTED.getIrnStatusMaster());
				}
				results.addAll(presults1);
				results.addAll(presults2);
				retResultMap.put(docKey, results);

			});
		} catch (Exception ex) {
			LOGGER.error("An exception occured while looking for the "
					+ "orginal doccument for CAN invoices : Exception "
					+ "is : ", ex);
		}

		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"ORG_DOC_CHECK_BUSINESS_VALIDATION_END", CLASS_NAME,
				"CanDocLookUp", null);

		return retResultMap;

	}

	private boolean eInvoiceApplicability(OutwardTransDocument document) {

		if (Strings.isNullOrEmpty(document.getSgstin())
				|| GSTConstants.URP.equalsIgnoreCase(document.getSgstin()))
			return false;

		if (document.getDocDate() == null)
			return false;
		LocalDate docDate = document.getDocDate();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");

		LocalDate gstStartDate = LocalDate.parse("01102020", formatter);
		if (docDate.compareTo(gstStartDate) < 0) {
			return false;
		}

		if (Strings.isNullOrEmpty(document.getTransactionType())) {
			document.setTransactionType(GSTConstants.O);
		}
		ehcachegstin = StaticContextHolder.getBean("Ehcachegstin",
				Ehcachegstin.class);
		String groupCode = TenantContext.getTenantId();

		if ((!Strings.isNullOrEmpty(document.getCgstin()))
				&& (document.getCgstin().length() == 15) && CGSTIN_FORMAT
						.contains(document.getCgstin().substring(12, 14))) {

			return false;

		}
		GSTNDetailEntity gstin = ehcachegstin.getGstinInfo(groupCode,
				document.getSgstin());
		if (gstin == null)
			return false;
		String regType = gstin.getRegistrationType().toUpperCase();
		if (document.getTransactionType().equalsIgnoreCase(GSTConstants.O)
				&& DOCTYPE_IMPORTS
						.contains(trimAndConvToUpperCase(document.getDocType()))
				&& REGYPE_IMPORTS.contains(regType)) {
			return true;
		}
		return false;
	}

	private Map<String, List<ProcessingResult>> uniqueRecordwithiFinancialYear(
			List<OutwardTransDocument> docs, Boolean isIntegrated) {

		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"SIGNINGANDFILING_VALIDATION_START", CLASS_NAME,
				"uniqueRecordwithiFinancialYear", null);

		Map<String, List<ProcessingResult>> retResultMap = new HashMap<>();
		try {
			Set<String> docKeySet = new HashSet<>();
			List<String> docKeys = new ArrayList<>();
			List<String> orgDocDetails = new ArrayList<>();

			docs.forEach(doc -> docKeySet.add(keyGeneration(doc)));
			docKeys = new ArrayList<>(docKeySet);

			if (LOGGER.isDebugEnabled()) {

				LOGGER.debug(
						"To check unique Record withiFinancialYear Invoices The number of dockeys "
								+ "recieved from the iteration is : "
								+ docKeys.size());
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
				docKeyChunks.forEach(
						chunk -> orgDocDetails.addAll(signAndFileRepository
								.findSubmitDocsCountsByDocKeys(chunk)));
			}

			docs.forEach(doc -> {
				List<ProcessingResult> results = new ArrayList<>();
				String docKey = keyGeneration(doc);

				if (!orgDocDetails.isEmpty()
						&& !orgDocDetails.contains(docKey)) {
					List<String> errorLocations = new ArrayList<>();
					errorLocations.add(GSTConstants.RETURN_PERIOD);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							errorLocations.toArray());
					results.add(new ProcessingResult(APP_VALIDATION, "ER0516",
							"Record has been reported in earlier Return Period",
							location));

				}

				if (isIntegrated) {

					if (!results.isEmpty()) {
						Boolean complianceError = results.stream().anyMatch(
								r -> r.getType() == ProcessingResultType.ERROR);

						if (complianceError) {
							doc.setIsProcessed(false);
							doc.setIsError(true);
						}
					}
				}

				retResultMap.put(docKeyGen.generateKey(doc), results);

			});
		} catch (Exception ex) {
			LOGGER.error("An exception occured while looking for the "
					+ "orginal doccument for  invoices : Exception " + "is : ",
					ex);
		}

		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"SIGNINGANDFILING_VALIDATION_END", CLASS_NAME,
				"uniqueRecordwithiFinancialYear", null);

		return retResultMap;

	}

	public List<ProcessingResult> validateCanReason(
			OutwardTransDocument document) {
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		@SuppressWarnings("unused")
		String groupCode = TenantContext.getTenantId();
		if (document.getSgstin() == null || document.getSgstin().isEmpty())
			return errors;

		String paramkryId = CONFIG_PARAM_OUTWARD_QUE_KEY_ID.O15.name();

		util = StaticContextHolder.getBean("OnboardingQuestionValidationsUtil",
				OnboardingQuestionValidationsUtil.class);
		Map<Long, List<EntityConfigPrmtEntity>> entityConfigParamMap = document
				.getEntityConfigParamMap();
		String paramtrvalue = util.valid(entityConfigParamMap, paramkryId,
				document.getEntityId());

		String cancellationReason = document.getCancellationReason();
		if (cancellationReason == null || cancellationReason.isEmpty()) {
			if (CONFIG_PARAM_OUTWARD_ANS_KEY_ID.A.name()
					.equalsIgnoreCase(paramtrvalue)) {
				errorLocations.add(GSTConstants.CAN_REASON);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER15169",
						"CancellationReason Cannot be left blank", location));
			}
			if (CONFIG_PARAM_OUTWARD_ANS_KEY_ID.B.name()
					.equalsIgnoreCase(paramtrvalue)) {
				document.setCancellationReason("1");
			}
			if (CONFIG_PARAM_OUTWARD_ANS_KEY_ID.C.name()
					.equalsIgnoreCase(paramtrvalue)) {
				document.setCancellationReason("2");
			}
			if (CONFIG_PARAM_OUTWARD_ANS_KEY_ID.D.name()
					.equalsIgnoreCase(paramtrvalue)) {
				document.setCancellationReason("3");
			}
			if (CONFIG_PARAM_OUTWARD_ANS_KEY_ID.E.name()
					.equalsIgnoreCase(paramtrvalue)) {
				document.setCancellationReason("4");
			}
			return errors;
		} else {
			try {
				int canReason = Integer.parseInt(cancellationReason);

				if (!CAN_REASON.contains(canReason)) {

					errorLocations.add(GSTConstants.CAN_REASON);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER15008",
							"Invalid Cancellation reason", location));
					return errors;
				}
			} catch (Exception e) {
				errorLocations.add(GSTConstants.CAN_REASON);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER15008",
						"Invalid Cancellation reason", location));
				return errors;

			}
		}
		return errors;
	}

	public List<ProcessingResult> validateCanRemark(
			OutwardTransDocument document) {
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		@SuppressWarnings("unused")
		String groupCode = TenantContext.getTenantId();
		if (document.getSgstin() == null || document.getSgstin().isEmpty())
			return errors;

		String paramkryId = CONFIG_PARAM_OUTWARD_QUE_KEY_ID.O15.name();

		util = StaticContextHolder.getBean("OnboardingQuestionValidationsUtil",
				OnboardingQuestionValidationsUtil.class);
		Map<Long, List<EntityConfigPrmtEntity>> entityConfigParamMap = document
				.getEntityConfigParamMap();
		String paramtrvalue = util.valid(entityConfigParamMap, paramkryId,
				document.getEntityId());

		String cancellationReason = document.getCancellationReason();
		if (cancellationReason == null || cancellationReason.isEmpty()) {
			if (CONFIG_PARAM_OUTWARD_ANS_KEY_ID.E.name()
					.equalsIgnoreCase(paramtrvalue)) {
				if (document.getCancellationRemarks() == null
						|| document.getCancellationRemarks().isEmpty()) {
					errorLocations.add(GSTConstants.REMARK);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER15170",
							"CancellationRemarks Cannot be left blank",
							location));
					return errors;
				}
			}
			return errors;
		} else {
			try {
				int canReason = Integer.parseInt(cancellationReason);

				if (canReason == 4) {
					if (document.getCancellationRemarks() == null
							|| document.getCancellationRemarks().isEmpty()) {
						errorLocations.add(GSTConstants.REMARK);
						TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
								null, errorLocations.toArray());
						errors.add(
								new ProcessingResult(APP_VALIDATION, "ER15170",
										"CancellationRemarks Cannot be left blank",
										location));
						return errors;
					}

				}
			} catch (Exception e) {

			}
		}
		return errors;
	}

	private String keyGeneration(OutwardTransDocument doc) {
		String gstin = "";
		String returnPeriod = (doc.getTaxperiod() != null)
				? doc.getTaxperiod().trim() : "";
		if (GSTConstants.I.equalsIgnoreCase(doc.getTransactionType())) {
			gstin = (doc.getCgstin() != null) ? doc.getCgstin().trim() : "";
		} else {
			gstin = (doc.getSgstin() != null) ? doc.getSgstin().trim() : "";
		}

		return new StringJoiner(DOC_KEY_JOINER).add(gstin).add(returnPeriod)
				.toString();

	}

	@Override
	public Map<String, List<ProcessingResult>> checkForInwardCrDrOrgInvoices(
			List<InwardTransDocument> docs, ProcessingContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		@SuppressWarnings("unused")
		String groupCode = TenantContext.getTenantId();

		util = StaticContextHolder.getBean("OnboardingQuestionValidationsUtil",
				OnboardingQuestionValidationsUtil.class);
		Map<Long, List<EntityConfigPrmtEntity>> entityConfigParamMap = document
				.getEntityConfigParamMap();

		String gstin = document.getSgstin();

		if (gstin != null && !gstin.isEmpty()) {
			String paramkeyId12 = CONFIG_PARAM_OUTWARD_QUE_KEY_ID.O12.name();
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
					errorLocations.add(GSTConstants.SGSTIN);
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

}
