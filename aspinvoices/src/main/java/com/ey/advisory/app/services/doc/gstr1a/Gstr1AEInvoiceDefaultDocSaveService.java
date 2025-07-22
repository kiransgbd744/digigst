/**
* 
 */
package com.ey.advisory.app.services.doc.gstr1a;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.apache.commons.lang3.StringUtils;
import org.javatuples.Sextet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.admin.data.repositories.client.GroupConfigPrmtRepository;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.data.entities.client.GstrReturnStatusEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1ADocRateSummary;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocLineItem;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocument;
import com.ey.advisory.app.data.gstr1A.entities.client.OutwardTransDocErrorGstr1A;
import com.ey.advisory.app.data.gstr1A.repositories.client.DocRepositoryGstr1A;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1ADocErrorRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1ADocRateSummaryRepository;
import com.ey.advisory.app.data.repositories.client.EinvoiceRepository;
import com.ey.advisory.app.data.repositories.client.GstrReturnStatusRepository;
import com.ey.advisory.app.data.services.ewb.EwbDbUtilService;
import com.ey.advisory.app.docs.dto.einvoice.EInvoiceDocSaveRespDto;
import com.ey.advisory.app.docs.dto.einvoice.EInvoiceOutwardDocSaveRespDto;
import com.ey.advisory.app.services.bifurcation.DocBifurcator;
import com.ey.advisory.app.services.docs.DocKeyGenerator;
import com.ey.advisory.app.services.docs.common.Gstr1AOrgHierarchyMasterDataUpdation;
import com.ey.advisory.app.services.docs.common.Gstr1AOutwardAdditionalMasterDataAddition;
import com.ey.advisory.app.services.docs.einvoice.DuplicatesRemove;
import com.ey.advisory.app.services.validation.DocRulesValidationResult;
import com.ey.advisory.app.util.OnboardingQuestionValidationsUtil;
import com.ey.advisory.app.util.AspDocumentConstants.FormReturnTypes;
import com.ey.advisory.app.util.OnboardingConstant.CONFIG_PARAM_OUTWARD_ANS_KEY_ID;
import com.ey.advisory.app.util.OnboardingConstant.CONFIG_PARAM_OUTWARD_QUE_KEY_ID;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AspInvoiceStatus;
import com.ey.advisory.common.EInvoiceStatus;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.EinvEwbJobStatus;
import com.ey.advisory.common.EwbProcessingStatus;
import com.ey.advisory.common.EwbStatusNew;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.IrnStatusMaster;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.PerfamanceEventConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.client.domain.B2COnBoardingConfigEntity;
import com.ey.advisory.common.client.domain.B2CQRAmtConfigEntity;
import com.ey.advisory.common.client.repositories.B2COnBoardingConfigRepo;
import com.ey.advisory.common.client.repositories.B2CQRAmtConfigRepo;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.domain.client.B2CQRCodeRequestLogEntity;
import com.ey.advisory.einv.client.EinvoiceEntity;
import com.ey.advisory.einv.dto.CancelIrnReqDto;
import com.ey.advisory.einv.dto.GenerateEWBByIrnERPReqDto;
import com.ey.advisory.ewb.client.repositories.EwbLifecycleRepository;
import com.ey.advisory.ewb.dto.CancelEwbReqDto;
import com.ey.advisory.ewb.dto.CancelEwbResponseDto;
import com.ey.advisory.repositories.client.B2CQRCodeLoggerRepository;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * 
 * * @author Shashikant.Shukla
 *
 */
@Service("Gstr1AEInvoiceDefaultDocSaveService")
public class Gstr1AEInvoiceDefaultDocSaveService
		implements Gstr1AEInvoiceDocSaveService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1AEInvoiceDefaultDocSaveService.class);
	private static final String CLASS_NAME = "Gstr1AEInvoiceDefaultDocSaveService";

	public static final String CONFI_KEY = "b2cs.threshold.value";

	private final List<String> NILNONSUPPLYTYPES = ImmutableList.of("NIL",
			"EXT", "NON");

	@Autowired
	@Qualifier("Gstr1ADocErrorRepository")
	private Gstr1ADocErrorRepository docErrorRepository;

	@Autowired
	@Qualifier("Gstr1AEinvoiceSalesDocRulesValidatorService")
	private Gstr1AEinvoiceSalesDocRulesValidatorService salesDocRulesValSvc;

	@Autowired
	@Qualifier("Gstr1ADocRateSummaryRepository")
	private Gstr1ADocRateSummaryRepository docRateSummaryRepository;
	//
	// private static final List<String> TAX_DOC_TYPE = ImmutableList.of("B2CS",
	// "B2CL", "CDNUR-B2CL");
	@Autowired
	@Qualifier("Ehcachegstin")
	private Ehcachegstin ehcachegstin;
	@Autowired
	private DocKeyGenerator<Gstr1AOutwardTransDocument, String> docKeyGen;

	@Autowired
	@Qualifier("Gstr1ADefaultOutwardTransDocBifurcator")
	private DocBifurcator<Gstr1AOutwardTransDocument> defaultBifurcator;

	@Autowired
	@Qualifier("Gstr1ADefaultOutwardLineTransDocBifurcator")
	private DocBifurcator<Gstr1AOutwardTransDocument> defaultLineBifurcator;

	@Autowired
	@Qualifier("B2COnBoardingConfigRepo")
	private B2COnBoardingConfigRepo b2COnBoardingConfigRepo;

	@Autowired
	@Qualifier("Gstr1ASimplifiedOutwardTransDocBifurcator")
	private DocBifurcator<Gstr1AOutwardTransDocument> simplifiedBifurcator;

	@Autowired
	@Qualifier("Gstr1AEInvoiceOutwardDocSaveResp")
	private Gstr1AEInvoiceOutwardDocSaveResp outwardDocSaveResp;

	@Autowired
	@Qualifier("EwbDbUtilServiceImpl")
	private EwbDbUtilService ewbDbService;

	private static final String AND = "&";

	@Autowired
	@Qualifier("Gstr1AEInvoiceOutwardDocSave")
	private Gstr1AEInvoiceOutwardDocSave outwardDocSave;

	@Autowired
	private Gstr1AEinvoiceDocSaveUtility docSaveUtility;

	@Autowired
	@Qualifier("Gstr1AOrgHierarchyMasterDataUpdation")
	private Gstr1AOrgHierarchyMasterDataUpdation orgHierarchyMasterDataUpdation;

	@Autowired
	@Qualifier("Gstr1AOutwardAdditionalMasterDataAddition")
	private Gstr1AOutwardAdditionalMasterDataAddition additionalMasterDataAddition;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("gstrReturnStatusRepository")
	private GstrReturnStatusRepository gstrReturnStatusRepository;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Autowired
	@Qualifier("B2CQRCodeLoggerRepository")
	B2CQRCodeLoggerRepository qrCodeRepo;

	@Autowired
	@Qualifier("DocRepositoryGstr1A")
	private DocRepositoryGstr1A docRepository;

	@Autowired
	@Qualifier("B2CQRAmtConfigRepo")
	B2CQRAmtConfigRepo b2CQRAmtConfigRepo;

	@Autowired
	@Qualifier("EinvoiceRepository")
	private EinvoiceRepository einvoiceRepository;

	@Autowired
	@Qualifier("Gstr1ADefaultOrginalDocCheckServiceImpl")
	private Gstr1AOriginalDocCheckService originalDocCheckService;

	@Autowired
	@Qualifier("Gstr1ACanDocCheckServiceImpl")
	private Gstr1AOriginalDocCheckService canDocCheckService;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("EwbLifecycleRepository")
	private EwbLifecycleRepository ewbLCeRepo;

	@Autowired
	@Qualifier("GroupConfigPrmtRepository")
	private GroupConfigPrmtRepository groupConfigPrmtRepository;

	private static final String PIPE = "|";

	private static final List<String> DATA_ORIGION_TYPE_API = ImmutableList
			.of("A", "AI", "E", "EI");
	// private static final List<String> DOC_TYPE = ImmutableList.of("INV",
	// "CR",
	// "DR", "BOS");
	private static final List<String> TAX_SCHEMA_LIST_RET = ImmutableList
			.of(GSTConstants.NRET, GSTConstants.NRETEWB, GSTConstants.NRETEINV);
	private static final List<String> TAX_SCHEMA_LIST_EWB = ImmutableList
			.of(GSTConstants.NEWB, GSTConstants.NBOTH, GSTConstants.NRETEWB);
	private static final List<String> TAX_SCHEMA_LIST_INVOICE = ImmutableList
			.of(GSTConstants.NBOTH, GSTConstants.NEINV, GSTConstants.NRETEINV);

	@Transactional(value = "clientTransactionManager")
	@Override
	public EInvoiceOutwardDocSaveRespDto saveDocuments(
			List<Gstr1AOutwardTransDocument> documents, String sourceId,
			String headerPayloadId, List<AsyncExecJob> jobList) {
		EInvoiceOutwardDocSaveRespDto finalRespDto = new EInvoiceOutwardDocSaveRespDto();
		try {
			int totalCount = documents.size();
			String groupCode = TenantContext.getTenantId();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Entered saveDocuments for groupCode{} ",
						groupCode);
			}

			// Take the groupCode from a thread local variable.
			ProcessingContext context = new ProcessingContext();
			context.seAttribute("groupCode", groupCode);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Validating documents for groupCode{} ",
						groupCode);
			}
			/*
			 * Convert Calculate Configure And Set Values to both Header and
			 * Item
			 */

			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"CALC_CALC_CONFIG_START", CLASS_NAME, "saveDocuments",
					null);
			outwardDocSave.convertCalcConfigAndSetValues(documents, sourceId,
					headerPayloadId);

			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"CALC_CALC_CONFIG_END", CLASS_NAME, "saveDocuments", null);
			// Invoke the validation service and the processing results.

			markDuplicateDocuments(documents);
			settingFiledGstins(context);

			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"BIFURCATION_START", CLASS_NAME, "saveDocuments", null);
			bifurcateOutwardDoc(documents, new HashMap<>(), groupCode);
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"BIFURCATION_END", CLASS_NAME, "saveDocuments", null);

			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"BUSINESS_GSTR1_START", CLASS_NAME, "saveDocuments", null);

			Map<String, List<ProcessingResult>> isFreezeprocessingResults = isGstr1FreezeCheckBasedOnOnboarding(
					documents, context);

			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"BUSINESS_GSTR1_END", CLASS_NAME, "saveDocuments", null);

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
			/*
			 * Completed all the rule validation Starting to check for original
			 * document number is present in the db or not for CR/DR documents
			 * 
			 */

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"All rule validations are finished and starting "
								+ "to check for orginal document is exist "
								+ "in db or not for group code : {}",
						groupCode);
				if (ruleProcessingResults != null)
					LOGGER.debug(
							"The size of the rule error list map is : {}"
									+ " for group code : {}",
							ruleProcessingResults.size(), groupCode);
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"All rule validations for gstr1 filed "
								+ "to check is exist "
								+ "in db or not for group code : {}",
						groupCode);
				if (isFreezeprocessingResults != null)
					LOGGER.debug(
							"The size of the rule error list map is : {}"
									+ " for group code : {}",
							isFreezeprocessingResults.size(), groupCode);
			}

			Map<String, List<ProcessingResult>> OrgDocChkprocessingResults = originalDocCheckService
					.checkForCrDrOrgInvoices(documents, true, context);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Completed checking for presence of"
								+ " original invoice for group code : {}",
						groupCode);
				if (OrgDocChkprocessingResults != null)
					LOGGER.debug(
							"The size of the org doc check error list map is : {} for group code : {}",
							OrgDocChkprocessingResults.size(), groupCode);
			}

			Map<String, List<ProcessingResult>> canChkprocessingResults = canDocCheckService
					.checkForCrDrOrgInvoices(documents, true, context);

			HashMap<String, List<ProcessingResult>> processingCrdrResults = new HashMap<>(
					ruleProcessingResults);

			OrgDocChkprocessingResults.forEach(
					(key, value) -> processingCrdrResults.merge(key, value,
							(v1, v2) -> Stream.of(v1, v2)
									.flatMap(x -> x.stream()).collect(
											Collectors.toList())));

			// isfreezeprocessingresults addition
			isFreezeprocessingResults.forEach(
					(key, value) -> processingCrdrResults.merge(key, value,
							(v1, v2) -> Stream.of(v1, v2)
									.flatMap(x -> x.stream()).collect(
											Collectors.toList())));

			HashMap<String, List<ProcessingResult>> processingMergeResults = new HashMap<>(
					processingCrdrResults);

			canChkprocessingResults.forEach(
					(key, value) -> processingMergeResults.merge(key, value,
							(v1, v2) -> Stream.of(v1, v2)
									.flatMap(x -> x.stream()).collect(
											Collectors.toList())));

			Map<String, List<ProcessingResult>> processingResults = DuplicatesRemove
					.eliminateDuplicates(processingMergeResults);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Validated documents for groupCode{} ", groupCode);
			}
			// Bifurcate Outward Documents

			// This is for locating the error
			Map<String, Gstr1AOutwardTransDocument> errDocMap = new HashMap<>();
			// For each document with errors, set the isError to true.
			if (!processingResults.isEmpty()) {
				for (Gstr1AOutwardTransDocument doc : documents) {
					String docKey = docKeyGen.generateKey(doc);
					if (isError(processingResults, docKey)) {
						errDocMap.put(docKey, doc);
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

			// Keep the list of errors ready.
			Map<String, List<OutwardTransDocErrorGstr1A>> errorMap = convertErrors(
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
			List<Gstr1AOutwardTransDocument> savedDocs = outwardDocSave
					.saveDocs(documents, docKeyGen);
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"SAVE_DB_DOC_END", CLASS_NAME, "saveDocuments", null);
			for (Gstr1AOutwardTransDocument savedoc : savedDocs) {
				Long id = savedoc.getId();
				setItemCountInHeader(savedoc);
				docSaveUtility.saveEinvoiceEntity(savedoc, id);
				docSaveUtility.saveEwbLifeCycleEntity(savedoc, id);
				savedoc.getLineItems().forEach(item -> {
					item.getAttribDtls().forEach(attribute -> {
						attribute.setDocHeaderID(id);
					});
				});
			}
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"MAP_DOC_TO_ERRORS_START", CLASS_NAME, "saveDocuments",
					null);
			List<Gstr1AOutwardTransDocument> updateSave = new ArrayList<>();

			if (!processingResults.isEmpty()) {
				for (Gstr1AOutwardTransDocument doc : savedDocs) {

					if (doc.isDeleted() || (doc.getAspInvoiceStatus() != null
							&& (doc.getAspInvoiceStatus() != AspInvoiceStatus.ASP_PROCESSED
									.getAspInvoiceStatusCode()))) {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"this document document either "
											+ "deleted or not processed-{}",
									doc.getDocKey());
						}
						continue;
					}

					if (!GSTConstants.CAN
							.equalsIgnoreCase(doc.getSupplyType())) {

						boolean isEinvOpted = GSTConstants.A
								.equalsIgnoreCase(doc.getOptedForEinv());

						boolean isEwbOpted = GSTConstants.A
								.equalsIgnoreCase(doc.getOptedForEwb());
						if (!isEinvOpted && !isEwbOpted) {
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug(
										"Neither ewayBill nor eInv are opted for docKey - {}",
										doc.getDocKey());
							}
							continue;
						}

						Integer ewbPreference = doc.getEwbJob();
						Integer eInvPreference = doc.getEinvJob();
						if (ewbPreference == null || eInvPreference == null) {
							LOGGER.debug(
									"either ewbPreference or eInvPreference "
											+ "is null for docKey {}",
									doc.getDocKey());
						}
						if (isEinvOpted && isEwbOpted && eInvPreference != null
								&& ewbPreference != null) {
							if (eInvPreference == EinvEwbJobStatus.EINV_CLOUD_AUTO
									.getEinvEwbJobStatusCode()
									&& ewbPreference == EinvEwbJobStatus.EWB_CLOUD_AUTO
											.getEinvEwbJobStatusCode()) {

								if (LOGGER.isDebugEnabled()) {
									LOGGER.debug("einvJob {} and ewbJob {}",
											eInvPreference, ewbPreference);
								}
								if (doc.getEwbProcessingStatus() != null
										&& doc.geteInvStatus() != null
										&& (doc.getEwbProcessingStatus() == EwbProcessingStatus.ASP_PROCESSED
												.getEwbProcessingStatusCode())
										&& (doc.geteInvStatus() == EInvoiceStatus.NOT_APPLICABLE
												.geteInvoiceStatusCode()
												|| isIrnAvailable(doc))
										&& (doc.getEwbNoresp() == null)) {
									LOGGER.debug(
											"ewb  job is calling for docKey {}",
											doc.getDocKey());
									Long id = doc.getId();

									jobList.add(createEwbAsyncJob(id));

									doc.setEwbProcessingStatus(
											EwbProcessingStatus.INPROGRESS_GENERATION
													.getEwbProcessingStatusCode());
									doc.setEwbStatus(EwbStatusNew.PUSHED_TO_NIC
											.getEwbNewStatusCode());
								}
								if (doc.getEwbProcessingStatus() != null
										&& doc.geteInvStatus() != null

										&& (doc.getEwbProcessingStatus() == EwbProcessingStatus.ASP_PROCESSED
												.getEwbProcessingStatusCode()
												|| doc.getEwbProcessingStatus() == EwbProcessingStatus.NOT_APPLICABLE
														.getEwbProcessingStatusCode())
										&& (doc.geteInvStatus() == EInvoiceStatus.ASP_PROCESSED
												.geteInvoiceStatusCode())
										&& (doc.getEwbNoresp() == null)
										&& (Strings.isNullOrEmpty(
												doc.getIrnResponse()))) {
									Long id = doc.getId();
									if (Strings.isNullOrEmpty(doc.getIrn())
											|| doc.getIrn().length() != 64) {
										if (doc.getEwbProcessingStatus() == EwbProcessingStatus.ASP_PROCESSED
												.getEwbProcessingStatusCode()) {

											LOGGER.debug(
													"ewb or eInv "
															+ "jobs are calling for docKey {}",
													doc.getDocKey());
											jobList.add(createEinvoiceEwbJob(id,
													true));
											doc.setEwbProcessingStatus(
													EwbProcessingStatus.INPROGRESS_GENERATION
															.getEwbProcessingStatusCode());
											doc.setEwbStatus(
													EwbStatusNew.PUSHED_TO_NIC
															.getEwbNewStatusCode());
										} else {
											LOGGER.debug(
													"ewb job  calling for docKey {}",
													doc.getDocKey());
											jobList.add(createEinvoiceEwbJob(id,
													false));
										}
										doc.seteInvStatus(
												EInvoiceStatus.IRN_IN_PROGRESS
														.geteInvoiceStatusCode());
										doc.setIrnStatus(
												IrnStatusMaster.PUSHED_TO_NIC
														.getIrnStatusMaster());
										updateSave.add(doc);
									}
								}
							} else if (eInvPreference == EinvEwbJobStatus.EINV_CLOUD_MANUAL
									.getEinvEwbJobStatusCode()
									&& ewbPreference == EinvEwbJobStatus.EWB_CLOUD_AUTO
											.getEinvEwbJobStatusCode()) {
								if (doc.getEwbProcessingStatus() != null
										&& doc.geteInvStatus() != null
										&& (doc.getEwbProcessingStatus() == EwbProcessingStatus.ASP_PROCESSED
												.getEwbProcessingStatusCode())
										&& (doc.geteInvStatus() == EInvoiceStatus.NOT_APPLICABLE
												.geteInvoiceStatusCode())
										&& (doc.getEwbNoresp() == null)) {
									Long id = doc.getId();
									if (LOGGER.isDebugEnabled()) {
										LOGGER.debug(
												"onlyEwb job calling for doc key {}",
												doc.getDocKey());
									}

									jobList.add(createEwbAsyncJob(id));
									doc.setEwbProcessingStatus(
											EwbProcessingStatus.INPROGRESS_GENERATION
													.getEwbProcessingStatusCode());
									doc.setEwbStatus(EwbStatusNew.PUSHED_TO_NIC
											.getEwbNewStatusCode());
									updateSave.add(doc);
								}
							} else if (eInvPreference == EinvEwbJobStatus.EINV_CLOUD_AUTO
									.getEinvEwbJobStatusCode()
									&& ewbPreference == EinvEwbJobStatus.EWB_CLOUD_MANUAL
											.getEinvEwbJobStatusCode()) {

								if (doc.geteInvStatus() != null) {
									if ((doc.geteInvStatus() == EInvoiceStatus.ASP_PROCESSED
											.geteInvoiceStatusCode())
											&& (Strings.isNullOrEmpty(
													doc.getIrnResponse()))) {
										Long id = doc.getId();
										if (Strings.isNullOrEmpty(doc.getIrn())
												|| doc.getIrn()
														.length() != 64) {
											if (LOGGER.isDebugEnabled()) {
												LOGGER.debug(
														"onlyeinvice job "
																+ "calling for doc key",
														doc.getDocKey());
											}
											jobList.add(createEinvoiceEwbJob(id,
													false));
											doc.seteInvStatus(
													EInvoiceStatus.IRN_IN_PROGRESS
															.geteInvoiceStatusCode());
											doc.setIrnStatus(
													IrnStatusMaster.PUSHED_TO_NIC
															.getIrnStatusMaster());

											updateSave.add(doc);
										}
									}
								}
							}
						}
						if (isEinvOpted && !isEwbOpted) {
							if (eInvPreference != null
									&& eInvPreference == EinvEwbJobStatus.EINV_CLOUD_AUTO
											.getEinvEwbJobStatusCode()) {
								if (doc.geteInvStatus() != null
										&& (doc.geteInvStatus() == EInvoiceStatus.ASP_PROCESSED
												.geteInvoiceStatusCode())

										&& (Strings.isNullOrEmpty(
												doc.getIrnResponse()))) {
									if (Strings.isNullOrEmpty(doc.getIrn())
											|| doc.getIrn().length() != 64) {
										Long id = doc.getId();
										if (LOGGER.isDebugEnabled()) {
											LOGGER.debug(
													"onlyeinvoice job calling for doc key {}",
													doc.getDocKey());
										}
										jobList.add(createEinvoiceEwbJob(id,
												false));
										doc.seteInvStatus(
												EInvoiceStatus.IRN_IN_PROGRESS
														.geteInvoiceStatusCode());
										doc.setIrnStatus(
												IrnStatusMaster.PUSHED_TO_NIC
														.getIrnStatusMaster());
										updateSave.add(doc);
									}
								}
							}
						}

						if (!isEinvOpted && isEwbOpted) {
							if (ewbPreference != null
									&& ewbPreference == EinvEwbJobStatus.EWB_CLOUD_AUTO
											.getEinvEwbJobStatusCode()) {

								if (doc.getEwbProcessingStatus() != null
										&& (doc.getEwbProcessingStatus() == EwbProcessingStatus.ASP_PROCESSED
												.getEwbProcessingStatusCode())

										&& (doc.getEwbNoresp() == null)) {
									Long id = doc.getId();
									if (LOGGER.isDebugEnabled()) {
										LOGGER.debug(
												"only EWB job "
														+ "calling for doc key",
												doc.getDocKey());
									}
									jobList.add(createEwbAsyncJob(id));

									doc.setEwbProcessingStatus(
											EwbProcessingStatus.INPROGRESS_GENERATION
													.getEwbProcessingStatusCode());
									doc.setEwbStatus(EwbStatusNew.PUSHED_TO_NIC
											.getEwbNewStatusCode());
									updateSave.add(doc);
								}

							}
						}

					}
					if (LOGGER.isDebugEnabled()) {
						if (doc.getEinvJob() == null) {
							LOGGER.debug("ewbStatus null for docKey{}",
									doc.getDocKey());
						} else {
							LOGGER.debug("ewbStatus {} for docKey{}",
									doc.getEinvJob(), doc.getDocKey());
						}
					}
					if (!TAX_SCHEMA_LIST_EWB.contains(
							trimAndConvToUpperCase(doc.getTaxScheme()))) {
						if (GSTConstants.CAN
								.equalsIgnoreCase(doc.getSupplyType())
								&& (doc.getEwbNoresp() != null)
								&& doc.getEwbJob() != null
								&& (doc.getEwbJob() == EinvEwbJobStatus.EWB_CLOUD_AUTO
										.getEinvEwbJobStatusCode())) {
							if (doc.getEwbStatus() != null
									&& (doc.getEwbStatus() == EwbStatusNew.EWB_ACTIVE
											.getEwbNewStatusCode())
									|| doc.getEwbStatus() == EwbStatusNew.PART_A_GENERATED
											.getEwbNewStatusCode()) {

								CancelEwbReqDto cancelDto = new CancelEwbReqDto();
								cancelDto.setDocHeaderId(doc.getId());
								if (doc.getTransactionType() != null
										&& GSTConstants.I.equalsIgnoreCase(
												doc.getTransactionType())) {
									cancelDto.setGstin(doc.getCgstin());
								} else {
									cancelDto.setGstin(doc.getSgstin());
								}
								cancelDto.setEwbNo(
										doc.getEwbNoresp().toString());
								cancelDto.setCancelRsnCode(
										doc.getCancellationReason());
								cancelDto.setCancelRmrk(
										doc.getCancellationRemarks());
								if (LOGGER.isDebugEnabled()) {

									LOGGER.debug(
											"CAN Ewb job calling for docKey{}",
											doc.getDocKey());

								}
								jobList.add(createCanEwbjob(cancelDto));

							}

						}
					}
					if (!TAX_SCHEMA_LIST_INVOICE.contains(
							trimAndConvToUpperCase(doc.getTaxScheme()))) {
						if (GSTConstants.CAN
								.equalsIgnoreCase(doc.getSupplyType())
								&& (!Strings
										.isNullOrEmpty(doc.getIrnResponse()))
								&& doc.getEinvJob() != null
								&& doc.getEinvJob() == EinvEwbJobStatus.EINV_CLOUD_AUTO
										.getEinvEwbJobStatusCode()
								&& doc.getIrnStatus() != null
								&& doc.getIrnStatus() != IrnStatusMaster.CANCELLED
										.getIrnStatusMaster()) {

							CancelIrnReqDto cancelDto = new CancelIrnReqDto();
							cancelDto.setIrn(doc.getIrnResponse());
							cancelDto.setCnlRsn(doc.getCancellationReason());
							cancelDto.setCnlRem(doc.getCancellationRemarks());
							cancelDto.setDocHeaderId(doc.getId());
							cancelDto.setGstin(doc.getSgstin());
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug(
										"CAN Invoice job calling for docKey{}",
										doc.getDocKey());
							}
							jobList.add(createCanEInvoicejob(cancelDto));
						}
					}
				}
			}
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"MAP_DOC_TO_ERRORS_END", CLASS_NAME, "saveDocuments", null);

			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"SAVE_DB_DOC_START", CLASS_NAME, "saveDocuments", null);
			if (!updateSave.isEmpty()) {
				docRepository.saveAll(updateSave);
			}
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"SAVE_DB_DOC_END", CLASS_NAME, "saveDocuments", null);
			// Map the Ids of the saved documents to the errors associated with
			// it.
			savedDocs.forEach(doc -> {
				String docKey = docKeyGen.generateKey(doc);
				List<OutwardTransDocErrorGstr1A> errList = errorMap.get(docKey);
				Long id = doc.getId();
				if (errList != null && !errList.isEmpty()) {
					errList.forEach(err -> {

						err.setDocHeaderId(id);
						err.setSgstin(doc.getSgstin());
						err.setTaxperiod(doc.getTaxperiod());
						err.setDerivedTaxperiod(doc.getDerivedTaxperiod());
						err.setAcceptanceId(doc.getAcceptanceId());
					});
				}
			});

			// Add all the errors into a single list to save to the DB.
			List<OutwardTransDocErrorGstr1A> outError = new ArrayList<>();
			errorMap.entrySet().forEach(e -> {
				List<OutwardTransDocErrorGstr1A> errorList = e.getValue();
				errorList.forEach(error -> {
					if (error.getDocHeaderId() != null) {
						outError.add(error);
					}
				});
			});
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"DOC_ERROR_SAVE_START", CLASS_NAME, "saveDocuments", null);
			if (!outError.isEmpty()) {
				docErrorRepository.saveAll(outError);
			}
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"DOC_ERROR_SAVE_END", CLASS_NAME, "saveDocuments", null);
			// docErrorRepository.saveAll(errorsToSave);

			List<String> jobParamsList = new ArrayList<>();
			if (GSTConstants.DataOriginTypeCodes.ERP_API.getDataOriginTypeCode()
					.equalsIgnoreCase(
							savedDocs.get(0).getDataOriginTypeCode())) {
				for (Gstr1AOutwardTransDocument doc : savedDocs) {
					String inGstin = doc.getSgstin();
					if (inGstin != null) {
						JsonObject jsonParams = new JsonObject();
						jsonParams.addProperty("gstin", inGstin);
						jsonParams.addProperty("scenarioName",
								APIConstants.OUTWARD_ASP_ERP_PUSH);
						jobParamsList.add(jsonParams.toString());

					}
				}
			}

			// Create the rate level invoices.
			List<Gstr1ADocRateSummary> docRateSummaryList = createRateLevelSummary(
					savedDocs);

			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"RATE_SUMMARY_START", CLASS_NAME, "saveDocuments", null);
			docRateSummaryRepository.saveAll(docRateSummaryList);
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"RATE_SUMMARY_END", CLASS_NAME, "saveDocuments", null);
			// Onboarding Config Params
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"CONFIGURATION_PARAMS_START", CLASS_NAME, "saveDocuments",
					null);
			configureOnboardingParams(documents);

			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"CONFIGURATION_PARAMS_END", CLASS_NAME, "saveDocuments",
					null);
			// Finally create response to return.
			if (!GSTConstants.DataOriginTypeCodes.EXCL_UPLOAD
					.getDataOriginTypeCode().equalsIgnoreCase(
							documents.get(0).getDataOriginTypeCode())) {
				PerfUtil.logEventToFile(
						PerfamanceEventConstants.FILE_PROCESSING,
						"CREATE_OUTWARD_DOC_SAVE_START", CLASS_NAME,
						"saveDocuments", null);
				List<EInvoiceDocSaveRespDto> docSaveRespDtos = outwardDocSaveResp
						.createOutwardDocSaveAPIResponse(oldDocIds, savedDocs,
								errorMap);
				finalRespDto.setSavedDocsResp(docSaveRespDtos);
				finalRespDto.setProcessingResults(processingResults);
				finalRespDto.setErrors(errorCount(savedDocs));
				finalRespDto.setTotalRecords(totalCount);
				finalRespDto.setJobParamsList(jobParamsList);
				PerfUtil.logEventToFile(
						PerfamanceEventConstants.FILE_PROCESSING,
						"CREATE_OUTWARD_DOC_SAVE_END", CLASS_NAME,
						"saveDocuments", null);
			}
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

	private void setItemCountInHeader(Gstr1AOutwardTransDocument doc) {
		doc.setItemRowCount(Long.valueOf(doc.getLineItems().size()));
	}

	private AsyncExecJob createEwbAsyncJob(Long id) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("About to create EWB AsyncJob for  docId_ {}", id);
		}
		JsonObject jsonParams = new JsonObject();
		jsonParams.addProperty("id", id);
		jsonParams.addProperty("updateDb", true);

		return asyncJobsService.createJobAndReturn(TenantContext.getTenantId(),
				JobConstants.GENERATE_EWAYBILL, jsonParams.toString(), "SYSTEM",
				1L, null, 1L);

	}

	private AsyncExecJob createCanEwbjob(CancelEwbReqDto cancelDto) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("About to create CAN EWB AsyncJob ");
		}

		Gson gson = new Gson();

		return asyncJobsService.createJobAndReturn(TenantContext.getTenantId(),
				JobConstants.EWB_CANCEL, gson.toJson(cancelDto), "SYSTEM", 1L,
				null, 1L);

	}

	private AsyncExecJob createCanEInvoicejob(CancelIrnReqDto cancelDto) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("About to create CAN Einvoice AsyncJob ");
		}

		Gson gson = new Gson();

		return asyncJobsService.createJobAndReturn(TenantContext.getTenantId(),
				JobConstants.EINV_CANCEL, gson.toJson(cancelDto), "SYSTEM", 1L,
				null, 1L);

	}

	private AsyncExecJob createEinvoiceEwbJob(Long id, boolean isEwbRequired) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("isEwbRequired {} for docId_ {}", isEwbRequired, id);
		}
		JsonObject jsonParams = new JsonObject();
		if (isEwbRequired)
			jsonParams.addProperty("isEwbRequired", "true");
		else
			jsonParams.addProperty("isEwbRequired", "false");
		jsonParams.addProperty("id", id);

		return asyncJobsService.createJobAndReturn(TenantContext.getTenantId(),
				JobConstants.EINVOICE_ASYNC, jsonParams.toString(), "SYSTEM",
				1L, null, 1L);

	}

	private Map<String, List<OutwardTransDocErrorGstr1A>> convertErrors(
			Map<String, List<ProcessingResult>> results,
			Map<String, Gstr1AOutwardTransDocument> errDocMap) {
		Map<String, List<OutwardTransDocErrorGstr1A>> map = new HashMap<>();
		results.keySet().stream().forEach(key -> {
			List<ProcessingResult> pResults = results.get(key);
			Gstr1AOutwardTransDocument outwardTransDocument = errDocMap
					.get(key);

			List<OutwardTransDocErrorGstr1A> errors = new ArrayList<>();
			pResults.forEach(pr -> {
				// Instantiate the ent
				OutwardTransDocErrorGstr1A error = new OutwardTransDocErrorGstr1A();
				TransDocProcessingResultLoc loc = (TransDocProcessingResultLoc) pr
						.getLocation();
				if (null != loc) { // In case of bifurcation failure, loc is
									// null
					Object[] arr = loc.getFieldIdentifiers();
					if (arr != null) {
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

	public List<Gstr1ADocRateSummary> createRateLevelSummary(
			List<Gstr1AOutwardTransDocument> documents) {
		Map<Long, List<Gstr1ADocRateSummary>> docRateMap = new HashMap<>();
		List<Gstr1ADocRateSummary> ratLevelList = new ArrayList<>();
		documents.forEach(doc -> {
			List<Gstr1AOutwardTransDocLineItem> lineItems = doc.getLineItems();
			lineItems.forEach(lineItem -> {
				Gstr1ADocRateSummary docRateSummary = new Gstr1ADocRateSummary();
				docRateSummary.setDocHeaderId(lineItem.getDocument().getId());
				docRateSummary.setTaxRate(
						lineItem.getTaxRate() == null ? BigDecimal.ZERO
								: lineItem.getTaxRate().setScale(2,
										RoundingMode.HALF_UP));
				docRateSummary.setIgstAmt(lineItem.getIgstAmount() == null
						? BigDecimal.ZERO : lineItem.getIgstAmount());
				docRateSummary.setCgstAmt(lineItem.getCgstAmount() == null
						? BigDecimal.ZERO : lineItem.getCgstAmount());
				docRateSummary.setSgstAmt(lineItem.getSgstAmount() == null
						? BigDecimal.ZERO : lineItem.getSgstAmount());
				BigDecimal cessAmtAdvalorem = lineItem.getCessAmountAdvalorem();
				BigDecimal cessAmtSpecific = lineItem.getCessAmountSpecific();
				if (cessAmtAdvalorem == null) {
					cessAmtAdvalorem = BigDecimal.ZERO;
				}
				if (cessAmtSpecific == null) {
					cessAmtSpecific = BigDecimal.ZERO;
				}
				docRateSummary
						.setCessAmt(cessAmtAdvalorem.add(cessAmtSpecific));
				docRateSummary.setTaxValue(lineItem.getTaxableValue() == null
						? BigDecimal.ZERO : lineItem.getTaxableValue());
				LOGGER.debug("lineItem.getItmGstnBifurcation() {} ",
						lineItem.getItmGstnBifurcation());
				docRateSummary.setItmGstnBifurcation(
						Strings.isNullOrEmpty(lineItem.getItmGstnBifurcation())
								? APIConstants.GSTN
								: lineItem.getItmGstnBifurcation());
				docRateMap
						.computeIfAbsent(doc.getId(), obj -> new ArrayList<>())
						.add(docRateSummary);
			});
		});

		LOGGER.debug("docRate {}", docRateMap);
		docRateMap.forEach((key, value) -> {
			Map<String, List<Gstr1ADocRateSummary>> taxRateMap = value.stream()
					.collect(Collectors
							.groupingBy(obj -> taxRateBifurcationString(obj)));
			taxRateMap.forEach((rate, obj) -> {
				LOGGER.debug("taxRate Map {}", taxRateMap);
				LOGGER.debug("Rate {}", rate);
				Gstr1ADocRateSummary dto = obj.stream().reduce(
						new Gstr1ADocRateSummary(), (a, b) -> addDto(a, b));
				dto.setDocHeaderId(key);
				dto.setTaxRate(new BigDecimal(rate.split("\\|")[0]));
				dto.setItmGstnBifurcation(rate.split("\\|")[1]);
				ratLevelList.add(dto);
			});
		});
		LOGGER.debug("ratLevelList {}", ratLevelList);
		return ratLevelList;
	}

	private String taxRateBifurcationString(Gstr1ADocRateSummary rateSummary) {

		return String.format("%s|%s", rateSummary.getTaxRate(),
				rateSummary.getItmGstnBifurcation());// 18|
	}

	private void bifurcateOutwardDoc(List<Gstr1AOutwardTransDocument> documents,
			Map<String, List<ProcessingResult>> processingResults,
			String groupCode) {
		// Initialize an empty processing context and pass it to the
		// bifurcator.

		for (Gstr1AOutwardTransDocument document : documents) {
			if (GSTConstants.I.equalsIgnoreCase(document.getTransactionType())
					|| TAX_SCHEMA_LIST_RET.contains(
							trimAndConvToUpperCase(document.getTaxScheme()))) {
				document.setCmplianceApplicable(false);
			} else if (document.getDocType().equalsIgnoreCase(GSTConstants.DLC)
					&& document.getSupplyType()
							.equalsIgnoreCase(GSTConstants.CAN)) {
				// Have added this else if as part of User Story 125802: GSTR-1
				// | Processing of DLC doc type transactions into Cloud
				document.setCmplianceApplicable(false);
			} else {
				document.setCmplianceApplicable(true);
			}
		}
		ProcessingContext bifContext = new ProcessingContext();

		Map<String, Config> confiMap = configManager.getConfigs("OUTWARD",
				CONFI_KEY, "DEFAULT");

		String configString = confiMap.get(CONFI_KEY) == null
				? "201704-202406|250000;202407-202812|100000"
				: confiMap.get(CONFI_KEY).getValue();

		bifContext.seAttribute(CONFI_KEY, configString);
		documents.stream().filter(doc -> !"CAN"
				.equalsIgnoreCase(doc.getSupplyType()) && !doc.getIsError()
				&& (!GSTConstants.I.equalsIgnoreCase(doc.getTransactionType()))
				&& (!TAX_SCHEMA_LIST_RET
						.contains(trimAndConvToUpperCase(doc.getTaxScheme()))))
				.forEach(doc -> {
					// if document has business rule error, it will not be
					// eligible for
					// bifurcation
					doc.setCmplianceApplicable(true);
					String formReturnType = doc.getFormReturnType();
					doc = defaultBifurcator.bifurcate(doc, bifContext);
					List<ProcessingResult> results = new ArrayList<>();
					if (!defaultBifurcator.isBifurcated(doc)
							&& !FormReturnTypes.ANX1.getType()
									.equals(formReturnType)) {
						String docKey = docKeyGen.generateKey(doc);

						String[] errorLocations = new String[] {
								GSTConstants.DOC_NO };
						TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
								null, errorLocations);
						List<ProcessingResult> prList = processingResults
								.get(docKey);
						ProcessingResult pr = new ProcessingResult("ASP",
								"ER0501",
								"Transaction cannot be mapped to any of the Tables "
										+ "of the GSTR-1 Return Form",
								location);

						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Bifurcation for Document" + doc
									+ "  for groupCode " + groupCode);
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

						doc.setCmplianceApplicable(false);
						results.add(pr);
						processingResults.put(docKey, results);
					}

					/*
					 * doc = simplifiedBifurcator.bifurcate(doc, bifContext); if
					 * (!simplifiedBifurcator.isBifurcated(doc) &&
					 * !FormReturnTypes.GSTR1.getType() .equals(formReturnType))
					 * { String docKey = docKeyGen.generateKey(doc); //
					 * List<ProcessingResult> prList = //
					 * processingResults.get(docKey); String[] errorLocations =
					 * new String[] { GSTConstants.DOC_NO };
					 * TransDocProcessingResultLoc location = new
					 * TransDocProcessingResultLoc( null, errorLocations);
					 * ProcessingResult pr = new ProcessingResult("ASP",
					 * "ER0501",
					 * "Transaction cannot be mapped to any of the Tables " +
					 * "of the Annexure-1 Return Form", location); if
					 * (LOGGER.isDebugEnabled()) {
					 * LOGGER.debug("Bifurcation for Document" + doc +
					 * "  for groupCode " + groupCode); } // If the document
					 * does not have any validation errors, // then // the
					 * prList will be null. In that case we need to // create a
					 * // new list and add the errors to the list.
					 * 
					 * if (prList != null) { prList.add(pr); } else { prList =
					 * new ArrayList<>(); prList.add(pr);
					 * processingResults.put(docKey, prList); }
					 * 
					 * doc.setCmplianceApplicable(false); results.add(pr);
					 * processingResults.put(docKey, results); }
					 */
				});

		// Map<String, Config> configMap = configManager.getConfigs(
		// "MULTILVLSUPPINV", "eligible.multilvlsupplytype", "DEFAULT");
		//
		// String eligibleGrps = configMap != null
		// && configMap.get("eligible.multilvlsupplytype.groups") != null
		// ? configMap.get("eligible.multilvlsupplytype.groups")
		// .getValue()
		// : null;
		String multiSupplyTypeAns = groupConfigPrmtRepository
				.findAnswerForMultiSupplyType();
		if ("B".equalsIgnoreCase(multiSupplyTypeAns)) {
			String logMsg = String
					.format("There is no Eligible Grps Config in the Master ,"
							+ " Hence returning this");
			LOGGER.error(logMsg);
			return;
		} else {
			documents.stream().filter(doc -> !"CAN"
					.equalsIgnoreCase(doc.getSupplyType())
					&& !doc.getIsError()
					&& (!GSTConstants.I
							.equalsIgnoreCase(doc.getTransactionType()))
					&& (defaultBifurcator.isBifurcated(doc))
					&& (!TAX_SCHEMA_LIST_RET.contains(
							trimAndConvToUpperCase(doc.getTaxScheme()))))
					.forEach(doc -> {
						// if document has business rule error, it will not be
						// eligible for
						// bifurcation
						doc = defaultLineBifurcator.bifurcate(doc, bifContext);
					});
		}

		// List<String> grpDtls = Arrays.asList(eligibleGrps.split(","));

		// if (!grpDtls.contains(groupCode)) {
		// String logMsg = String.format(
		// "Group Code %s is not a Part of Multi Inv Supply Type, Hence
		// returning this",
		// groupCode);
		// LOGGER.error(logMsg);
		// return;
		// }

	}

	private void configureOnboardingParams(
			List<Gstr1AOutwardTransDocument> documents) {
		// Update Org. Hierarchy Attributes
		orgHierarchyMasterDataUpdation
				.updateOrgHierarchyMasterDataValForOutwardDocs(documents);
		// Add Additional Master Data
		additionalMasterDataAddition.addAdditionalMasterData(documents);
	}

	private void populateErrorCodeAndErrorDescription(
			Map<String, List<ProcessingResult>> processingResults,
			String docKey, Gstr1AOutwardTransDocument doc) {
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
				Gstr1AOutwardTransDocLineItem item = doc.getLineItems()
						.get(idx);
				if (aitemErrorMap.get(idx) != null) {
					item.setErrCodes(aitemErrorMap.get(idx).stream()
							.collect(Collectors.joining(",")));
				}
			});
		}

		if (aiteInfoMap != null && aiteInfoMap.size() > 0) {
			IntStream.range(0, doc.getLineItems().size()).forEach(idx -> {
				Gstr1AOutwardTransDocLineItem item = doc.getLineItems()
						.get(idx);
				if (aiteInfoMap.get(idx) != null) {
					item.setInfoCodes(aiteInfoMap.get(idx).stream()
							.collect(Collectors.joining(",")));
				}
			});
		}

	}

	private void markDuplicateDocuments(List<Gstr1AOutwardTransDocument> docs) {

		// Get the map of documents will the keys and values as the list of
		// documents.
		// Map<String, List<Gstr1AOutwardTransDocument>> retResultMap = new
		// HashMap<>();

		Map<String, List<Gstr1AOutwardTransDocument>> allDocsMap = docs.stream()
				.collect(Collectors
						.groupingBy(doc -> docKeyGen.generateKey(doc)));

		// Filter out the documents that have more than one element in the value
		// list.
		Map<String, List<Gstr1AOutwardTransDocument>> duplicatesMap = allDocsMap
				.entrySet().stream().filter(e -> e.getValue().size() > 1)
				.collect(Collectors.toMap(Map.Entry::getKey,
						Map.Entry::getValue));

		// Iterate over all the documents in the duplicatesMap and set

		duplicatesMap.entrySet().forEach(entry -> {
			// String key = entry.getKey();
			List<Gstr1AOutwardTransDocument> value = entry.getValue();
			int lastIndex = value.size() - 1;
			IntStream.range(0, value.size()).forEach(idx -> {
				Gstr1AOutwardTransDocument item = value.get(idx);

				if (idx != lastIndex) {
					item.setDeleted(true);
					item.setEwbProcessingStatus(EwbProcessingStatus.ASP_ERROR
							.getEwbProcessingStatusCode());
					item.setEwbStatus(
							EwbStatusNew.ASP_ERROR.getEwbNewStatusCode());
					item.setIsError(true);
					// item.setErrCodes(GSTConstants.ER15167);
					item.setAspInvoiceStatus(AspInvoiceStatus.ASP_ERROR
							.getAspInvoiceStatusCode());
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

	private int errorCount(List<Gstr1AOutwardTransDocument> savedDocs) {

		List<Gstr1AOutwardTransDocument> errorDocs = savedDocs.stream()
				.filter(doc -> AspInvoiceStatus.ASP_ERROR
						.getAspInvoiceStatusCode() == doc.getAspInvoiceStatus())
				.collect(Collectors.toList());
		return errorDocs.size();
	}

	private boolean isIrnAvailable(Gstr1AOutwardTransDocument doc) {
		return !Strings.isNullOrEmpty(doc.getIrnResponse())
				|| (!Strings.isNullOrEmpty(doc.getIrn())
						&& doc.getIrn().length() == 64);
	}

	private void dynamicQrcodeSave(List<Gstr1AOutwardTransDocument> savedDocs) {
		try {
			List<B2CQRCodeRequestLogEntity> qrCodeList = new ArrayList<>();
			Map<String, B2COnBoardingConfigEntity> preferenceMap = new HashMap<>();
			Map<String, B2CQRAmtConfigEntity> getMamValueMap = new HashMap<>();
			Map<String, String> dockeyMap = new HashMap<>();
			Map<String, Sextet<String, String, String, String, String, String>> onBoardMap = new HashMap<>();
			for (Gstr1AOutwardTransDocument doc : savedDocs) {
				if (doc.getAspInvoiceStatus() != null
						&& (doc.getAspInvoiceStatus() == AspInvoiceStatus.ASP_PROCESSED
								.getAspInvoiceStatusCode())
						&& DATA_ORIGION_TYPE_API
								.contains(trimAndConvToUpperCase(
										doc.getDataOriginTypeCode()))
						&& !GSTConstants.CAN
								.equalsIgnoreCase(doc.getSupplyType())
						&& GSTConstants.O
								.equalsIgnoreCase(doc.getTransactionType())) {
					B2CQRCodeRequestLogEntity dQcentity = new B2CQRCodeRequestLogEntity();
					/*
					 * GSTNDetailEntity gstinData = ehcachegstin.getGstinInfo(
					 * TenantContext.getTenantId(), doc.getSgstin());
					 */

					String url = getUrl(doc, preferenceMap, onBoardMap,
							getMamValueMap);
					if (url == null)
						continue;
					dQcentity.setCreatedOn(LocalDateTime.now());
					dQcentity.setDocHeaderId(doc.getId());
					dQcentity.setPan(doc.getSgstin().substring(2, 12));
					dQcentity.setUrlCreatedOn(LocalDateTime.now());
					dQcentity.setRespPayload(url);
					dQcentity.setDockey(doc.getDocKey());
					qrCodeList.add(dQcentity);
				} else if (doc.getAspInvoiceStatus() != null
						&& (doc.getAspInvoiceStatus() == AspInvoiceStatus.ASP_PROCESSED
								.getAspInvoiceStatusCode())
						&& DATA_ORIGION_TYPE_API
								.contains(trimAndConvToUpperCase(
										doc.getDataOriginTypeCode()))
						&& GSTConstants.CAN
								.equalsIgnoreCase(doc.getSupplyType())
						&& GSTConstants.O
								.equalsIgnoreCase(doc.getTransactionType())) {
					B2CQRCodeRequestLogEntity dQcentity = new B2CQRCodeRequestLogEntity();
					String url = getUrlBydocKey(doc.getDocKey(), dockeyMap);
					if (url == null)
						continue;
					dQcentity.setCreatedOn(LocalDateTime.now());
					dQcentity.setDocHeaderId(doc.getId());
					dQcentity.setPan(doc.getSgstin().substring(2, 12));
					dQcentity.setUrlCreatedOn(LocalDateTime.now());
					dQcentity.setRespPayload(url);
					dQcentity.setDockey(doc.getDocKey());
					qrCodeList.add(dQcentity);
				}
			}
			if (!qrCodeList.isEmpty()) {
				qrCodeRepo.saveAll(qrCodeList);
			}
		} catch (Exception e) {
			LOGGER.error("Exception Occured:{} ", e);
		}
	}

	private String getUrl(Gstr1AOutwardTransDocument document,
			Map<String, B2COnBoardingConfigEntity> preferenceMap,
			Map<String, Sextet<String, String, String, String, String, String>> map,
			Map<String, B2CQRAmtConfigEntity> getMamValueMap) {
		String pan = document.getSgstin().substring(2, 12);
		B2COnBoardingConfigEntity data = getOnboardingQuestion(preferenceMap,
				pan);
		if (data == null)
			return null;
		String optionSelected = data.getOptionSelected();
		Sextet<String, String, String, String, String, String> getValuesfromEntity = getOnBoardingdata(
				optionSelected, document.getPlantCode(),
				document.getProfitCentre(), document.getSgstin(), map);
		if (getValuesfromEntity == null)
			return null;
		B2CQRAmtConfigEntity mamValue = getMamValue(getMamValueMap, pan);
		String payeeAddress = getValuesfromEntity.getValue0();
		String payeeMerCode = getValuesfromEntity.getValue1();
		String payeeName = getValuesfromEntity.getValue2();
		String transMode = getValuesfromEntity.getValue3();
		String qrExpiryTime = getValuesfromEntity.getValue4();
		String transQRMed = getValuesfromEntity.getValue5();
		return signedQrCodeGeneration(document, transMode, transQRMed,
				payeeAddress, payeeName, payeeMerCode, qrExpiryTime, mamValue);

	}

	private B2CQRAmtConfigEntity getMamValue(
			Map<String, B2CQRAmtConfigEntity> getMamValueMap, String pan) {

		if (getMamValueMap.containsKey(pan)) {
			return getMamValueMap.get(pan);
		}
		B2CQRAmtConfigEntity amtOnboardingEntity = b2CQRAmtConfigRepo
				.findByPanAndIsActiveTrue(pan);
		if (amtOnboardingEntity == null) {
			String errMsg = String.format(
					"No Data Available for Minimum Amount for "
							+ "the Pan %s Hence Generating 14 attribute URL",
					pan);
			LOGGER.error(errMsg);

		}
		getMamValueMap.put(pan, amtOnboardingEntity);

		return amtOnboardingEntity;
	}

	private Sextet<String, String, String, String, String, String> getOnBoardingdata(
			String optionSelected, String plantCode, String profitCentre,
			String sgstin,
			Map<String, Sextet<String, String, String, String, String, String>> map) {
		String pan = sgstin.substring(2, 12);
		Sextet<String, String, String, String, String, String> getValuesfromEntity = null;
		String allComb = new StringJoiner(PIPE).add(optionSelected)
				.add(plantCode).add(profitCentre).add(sgstin).add(pan)
				.toString();
		if (map.containsKey(allComb))
			return map.get(allComb);
		/*
		 * getValuesfromEntity = b2cOnBoardingCommonUtility
		 * .getValuesfromEntityBasedOnOption(optionSelected, plantCode,
		 * profitCentre, sgstin, pan);
		 */
		if (getValuesfromEntity != null) {
			map.put(allComb, getValuesfromEntity);
		}
		return getValuesfromEntity;
	}

	private B2COnBoardingConfigEntity getOnboardingQuestion(
			Map<String, B2COnBoardingConfigEntity> preferenceMap, String pan) {
		if (preferenceMap.containsKey(pan)) {
			return preferenceMap.get(pan);
		}
		List<B2COnBoardingConfigEntity> bcOnboardingEntity = b2COnBoardingConfigRepo
				.findByPanAndIsActiveTrue(pan);
		if (bcOnboardingEntity.isEmpty())
			return null;
		preferenceMap.put(pan, bcOnboardingEntity.get(0));
		return bcOnboardingEntity.get(0);
		/*
		 * return b2COnBoardingConfigRepo
		 * .findByEntityIdAndIsActiveTrue(gstinData.getEntityId());
		 */
	}

	private String getUrlBydocKey(String dockey,
			Map<String, String> dockeyMap) {
		if (dockeyMap.containsKey(dockey)) {
			return dockeyMap.get(dockey);
		}
		List<B2CQRCodeRequestLogEntity> findByDockey = qrCodeRepo
				.findByDockey(dockey);
		if (findByDockey == null || findByDockey.isEmpty())
			return null;
		String url = findByDockey.get(findByDockey.size() - 1).getRespPayload();
		dockeyMap.put(dockey, url);
		return url;
	}

	public static String signedQrCodeGeneration(Gstr1AOutwardTransDocument out,
			String a15, String a19, String payeAddress, String payeeName,
			String marchentCode, String a20, B2CQRAmtConfigEntity mamValue) {
		StringBuilder result = new StringBuilder();

		result.append("upi://pay?ver=01");
		if (!Strings.isNullOrEmpty(a15)) {
			result.append(AND);
			result.append("mode=");
			result.append(a15);
		}
		result.append(AND);
		result.append("tr=");
		result.append(out.getDocNo());
		result.append(AND);
		result.append("tn=00");
		if (!Strings.isNullOrEmpty(payeAddress)) {
			result.append(AND);
			result.append("pa=");
			result.append(payeAddress);
		}
		if (!Strings.isNullOrEmpty(payeeName)) {
			result.append(AND);
			result.append("pn=");
			result.append(payeeName);
		}
		if (!Strings.isNullOrEmpty(marchentCode)) {
			result.append(AND);
			result.append("mc=");
			result.append(marchentCode);
		}
		result.append(AND);
		result.append("am=");
		result.append(out.getDocAmount());
		result.append(AND);
		result.append("gstBrkUp=");
		result.append(amount(out));
		if (!Strings.isNullOrEmpty(a19)) {
			result.append(AND);
			result.append("qrMedium=");
			result.append(a19);
		}
		result.append(AND);
		result.append("invoiceNo=");
		result.append(out.getDocNo());
		result.append(AND);
		result.append("InvoiceDate=");
		result.append(out.getDocDate().atStartOfDay() + ":00+5:30");
		if (!Strings.isNullOrEmpty(a20)) {
			result.append(AND);
			result.append("QRexpire=");
			LocalDateTime nowDate = EYDateUtil.toUTCDateTimeFromLocal(
					LocalDateTime.now().plusDays(Integer.parseInt(a20)));
			String qRexpire = LocalDateTime.of(nowDate.getYear(),
					nowDate.getMonth(), nowDate.getDayOfMonth(),
					nowDate.getHour(), nowDate.getMinute(), nowDate.getSecond())
					+ "+5:30";
			result.append(qRexpire);
		}
		result.append(AND);
		result.append("gstin=");
		result.append(out.getSgstin());
		if (mamValue != null) {
			String amtIdentifier = mamValue.getIdentifier();
			String value = mamValue.getValue();
			if (amtIdentifier.equalsIgnoreCase("percent")) {
				BigDecimal percentageAmount = out.getDocAmount();
				percentageAmount = percentageAmount.multiply(
						new BigDecimal((double) Double.valueOf(value) / 100));
				result.append(AND + "mam=" + percentageAmount.setScale(2,
						BigDecimal.ROUND_HALF_EVEN));

			} else {
				result.append(AND + "mam=" + value);
			}
		}
		return result.toString().replaceAll(" ", "%20");
	}

	private static String amount(Gstr1AOutwardTransDocument out) {
		StringBuilder amount = new StringBuilder();
		BigDecimal igstAmount = out.getIgstAmount();
		BigDecimal cgstAmount = out.getCgstAmount();
		BigDecimal sgstAmount = out.getSgstAmount();
		if (igstAmount == null) {
			igstAmount = BigDecimal.ZERO;
		}
		if (cgstAmount == null) {
			cgstAmount = BigDecimal.ZERO;
		}
		if (sgstAmount == null) {
			sgstAmount = BigDecimal.ZERO;
		}
		BigDecimal allAmount = igstAmount.add(cgstAmount).add(sgstAmount);
		if (allAmount.compareTo(BigDecimal.ZERO) == 0) {
			amount.append("IGST:" + igstAmount);
		} else {

			if (igstAmount.compareTo(BigDecimal.ZERO) == 0) {
				amount.append("CGST:" + cgstAmount).append(PIPE)
						.append("SGST:" + sgstAmount);
			} else {
				amount.append("IGST:" + igstAmount);
			}
		}
		BigDecimal cessAmountAdvalorem = out.getCessAmountAdvalorem();
		BigDecimal cessAmountSpecific = out.getCessAmountSpecific();
		BigDecimal stateCessSpecificAmt = out.getStateCessSpecificAmt();
		BigDecimal stateCessAmount = out.getStateCessAmount();
		if (cessAmountAdvalorem != null || cessAmountSpecific != null
				|| stateCessSpecificAmt != null || stateCessAmount == null) {
			if (cessAmountAdvalorem == null) {
				cessAmountAdvalorem = BigDecimal.ZERO;
			}

			if (cessAmountSpecific == null) {
				cessAmountSpecific = BigDecimal.ZERO;
			}

			if (stateCessAmount == null) {
				stateCessAmount = BigDecimal.ZERO;
			}

			if (stateCessSpecificAmt == null) {
				stateCessSpecificAmt = BigDecimal.ZERO;
			}
			BigDecimal cessAmount = cessAmountAdvalorem.add(cessAmountSpecific)
					.add(stateCessSpecificAmt).add(stateCessAmount);
			amount.append(PIPE).append("CESS:" + cessAmount);
		}

		return amount.toString();

	}

	@Override
	public EInvoiceOutwardDocSaveRespDto saveCanEwbDocuments(
			List<CancelEwbReqDto> documents, String sourceId,
			String headerPayloadId) {

		int totalCount = documents.size();
		int proceesedCount = 0;
		int errorCount = 0;

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("DefaultDocSaveService saveDocuments Begining");
		}
		EInvoiceOutwardDocSaveRespDto finalRespDto = new EInvoiceOutwardDocSaveRespDto();
		String groupCode = TenantContext.getTenantId();
		List<EInvoiceDocSaveRespDto> saveList = new ArrayList<>();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Entered saveDocuments for groupCode " + groupCode);
		}
		try {

			for (CancelEwbReqDto savedoc : documents) {
				LOGGER.debug("Save Doc {} ", savedoc);
				EInvoiceDocSaveRespDto docSaveRespDto = new EInvoiceDocSaveRespDto();
				Optional<Gstr1AOutwardTransDocument> doc = docRepository
						.findDocByEwbNo(Long.valueOf(savedoc.getEwbNo()));
				LOGGER.debug("Document Records {} ", doc);
				if (doc.isPresent()) {
					LOGGER.debug("Inside Doc {}");
					savedoc.setDocHeaderId(doc.get().getId());
					doc.get().setPayloadId(headerPayloadId);
					CancelEwbResponseDto resp = new CancelEwbResponseDto();
					if (savedoc.getEwbDetails() != null) {
						resp.setCancelDate(
								savedoc.getEwbDetails().getCancelDate());
						resp.setEwayBillNo(
								savedoc.getEwbDetails().getEwayBillNo());
					}
					ewbDbService.cancelEwbDbUpdate(savedoc, resp);
					docSaveRespDto.setDocNo(doc.get().getDocNo());
					docSaveRespDto.setId(doc.get().getId());
					docSaveRespDto.setDocType(doc.get().getDocType());
					docSaveRespDto.setSupplierGstin(doc.get().getSgstin());
					docSaveRespDto.setAccountVoucherNo(
							doc.get().getAccountingVoucherNumber());
					docSaveRespDto.setDocDate(doc.get().getDocDate());
					docSaveRespDto.setEwbNo(savedoc.getEwbNo());
					proceesedCount++;
				} else {
					docSaveRespDto.setEwbNo(savedoc.getEwbNo());
					errorCount++;
				}
				saveList.add(docSaveRespDto);
			}
			LOGGER.debug("Save List {} ", saveList);
			finalRespDto.setSavedDocsResp(saveList);
			finalRespDto.setTotalRecords(totalCount);
			finalRespDto.setProcessed(proceesedCount);
			finalRespDto.setErrors(errorCount);

		} catch (Exception e) {
			LOGGER.error("Save Docs : Exception Occured:{} ", e);
			throw new AppException("Exception while saving the documents ",
					e.getMessage());
		}
		return finalRespDto;
	}

	@Override
	@Transactional(value = "clientTransactionManager")
	public EInvoiceOutwardDocSaveRespDto saveGenEwbIrnDocuments(
			List<GenerateEWBByIrnERPReqDto> documents, String headerPayloadId) {

		int totalCount = documents.size();
		int proceesedCount = 0;
		int errorCount = 0;

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("DefaultDocSaveService saveDocuments Begining");
		}
		EInvoiceOutwardDocSaveRespDto finalRespDto = new EInvoiceOutwardDocSaveRespDto();
		String groupCode = TenantContext.getTenantId();
		List<EInvoiceDocSaveRespDto> saveList = new ArrayList<>();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Entered saveDocuments for groupCode " + groupCode);
		}
		try {
			for (GenerateEWBByIrnERPReqDto reqDto : documents) {
				EInvoiceDocSaveRespDto docSaveRespDto = new EInvoiceDocSaveRespDto();
				Optional<EinvoiceEntity> einvRecord = einvoiceRepository
						.getEinvDetails(reqDto.getIrn());
				if (einvRecord.isPresent()) {
					Long docHeaderId = einvRecord.get().getDocHeaderId();
					Optional<Gstr1AOutwardTransDocument> outDoc = docRepository
							.getActiveRecord(docHeaderId);
					entityManager.detach(outDoc.get());
					if (outDoc.isPresent()) {
						outDoc.get().setPayloadId(headerPayloadId);
						docSaveUtility.updateDocwithGenIrnReq(outDoc.get(),
								reqDto, docHeaderId, docSaveRespDto);
						proceesedCount++;
					}
				} else {
					errorCount++;
					docSaveRespDto
							.setEwbNo(reqDto.getEwbDetails().getEwayBillNo());
				}
				saveList.add(docSaveRespDto);
			}
			finalRespDto.setSavedDocsResp(saveList);
			finalRespDto.setTotalRecords(totalCount);
			finalRespDto.setProcessed(proceesedCount);
			finalRespDto.setErrors(errorCount);

		} catch (Exception e) {
			LOGGER.error("Save Docs : Exception Occured:{} ", e);
			throw new AppException("Exception while saving the documents ",
					e.getMessage());
		}
		return finalRespDto;
	}

	private Gstr1ADocRateSummary addDto(Gstr1ADocRateSummary a,
			Gstr1ADocRateSummary b) {
		Gstr1ADocRateSummary dto = new Gstr1ADocRateSummary();
		dto.setCessAmt(
				a.getCessAmt() == null ? BigDecimal.ZERO.add(b.getCessAmt())
						: a.getCessAmt().add(b.getCessAmt()));
		dto.setIgstAmt(
				a.getIgstAmt() == null ? BigDecimal.ZERO.add(b.getIgstAmt())
						: a.getIgstAmt().add(b.getIgstAmt()));
		dto.setCgstAmt(
				a.getCgstAmt() == null ? BigDecimal.ZERO.add(b.getCgstAmt())
						: a.getCgstAmt().add(b.getCgstAmt()));
		dto.setSgstAmt(
				a.getSgstAmt() == null ? BigDecimal.ZERO.add(b.getSgstAmt())
						: a.getSgstAmt().add(b.getSgstAmt()));
		dto.setTaxValue(
				a.getTaxValue() == null ? BigDecimal.ZERO.add(b.getTaxValue())
						: a.getTaxValue().add(b.getTaxValue()));

		LOGGER.debug("getItmGstnBifurcation {} ", a.getItmGstnBifurcation());
		dto.setItmGstnBifurcation(a.getItmGstnBifurcation());
		return dto;

	}

	private Map<String, List<ProcessingResult>> isGstr1FreezeCheckBasedOnOnboarding(
			List<Gstr1AOutwardTransDocument> documents,
			ProcessingContext context) {
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

			for (Gstr1AOutwardTransDocument document : documents) {
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
				if (paramtrvalue != null || CONFIG_PARAM_OUTWARD_ANS_KEY_ID.A
						.name().equalsIgnoreCase(paramtrvalue)) {
					taxPeriodLevelDocs.add(docKeyGen.generateKey(document));
				}
			}

			LOGGER.debug("taxPeriodLevelDocs {}  ", taxPeriodLevelDocs);
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

			Map<String, List<ProcessingResult>> retResultMap = new HashMap<>();

			if (!outwardDocKeysWithTaxPeriod.isEmpty()) {
				for (Gstr1AOutwardTransDocument docFile : documents) {

					String key = docKeyGen.generateKey(docFile);
					LOGGER.debug(" key1 {} docFile {} ", key, docFile);
					if (outwrdDocMap.containsKey(key)) {

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
										APP_VALIDATION,
										ProcessingResultType.ERROR, "ER1276",
										"GSTR1A for this tax period is already filed",
										location);
								docFile.setDeleted(true);
								results.add(result);
							} else {
								ProcessingResult result = new ProcessingResult(
										APP_VALIDATION,
										ProcessingResultType.ERROR, "ER1277",
										String.format(
												"Document is already uploaded/active in DigiGST - Tax Period : %s",
												prevActiveTaxPeriod),
										location);
								docFile.setDeleted(true);
								results.add(result);
							}
							retResultMap.put(key, results);

							/*
							 * docFile.setIsError(true);
							 * docFile.setErrCodes("ER1276");
							 * docFile.seteInvErrorDesc(
							 * "GSTR1 for this tax period is already filed");
							 */ }
					}
				}
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("fy+sgstin+cgstin+docNUm+docType+taxperiod");
				}
			}
			return retResultMap;

		} catch (Exception ex) {
			String msg = "exception in gstr1 freeze ";
			LOGGER.error(msg);
			throw new AppException(ex);
		}
	}
}
