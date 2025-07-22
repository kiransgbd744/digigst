/**
 * 
 */
package com.ey.advisory.app.services.docs.einvoice;

import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.GroupInfoDetailsRepository;
import com.ey.advisory.admin.services.onboarding.EntityAtConfigKey;
import com.ey.advisory.admin.services.onboarding.MasterCustomerService;
import com.ey.advisory.admin.services.onboarding.MasterProductService;
import com.ey.advisory.admin.services.onboarding.OrganizationService;
import com.ey.advisory.app.data.entities.client.AdditionalDocDetails;
import com.ey.advisory.app.data.entities.client.AttributeDetails;
import com.ey.advisory.app.data.entities.client.ContractDetails;
import com.ey.advisory.app.data.entities.client.OutwardTransDocError;
import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.entities.client.PreceedingDocDetails;
import com.ey.advisory.app.data.entities.client.SourceInfoEntity;
import com.ey.advisory.app.data.repositories.client.AdditionalDocDetailsRepository;
import com.ey.advisory.app.data.repositories.client.ContractDetailsRepository;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.repositories.client.PreceedingDocDetailsRepository;
import com.ey.advisory.app.data.repositories.client.SourceInfoRepository;
import com.ey.advisory.app.services.docs.DocKeyGenerator;
import com.ey.advisory.app.services.gen.ClientGroupService;
import com.ey.advisory.app.util.AspDocumentConstants;
import com.ey.advisory.app.util.AspDocumentConstants.TransDocTypes;
import com.ey.advisory.app.util.AspDocumentUtil;
import com.ey.advisory.app.util.OnboardingConfigParamsCheck;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AspInvoiceStatus;
import com.ey.advisory.common.EInvoiceStatus;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.EwbProcessingStatus;
import com.ey.advisory.common.EwbStatusNew;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.IrnStatusMaster;
import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.PerfamanceEventConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.common.service.GstnApi;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * 
 * @author Laxmi.Salukuti
 *
 */
@Component("EInvoiceOutwardDocSave")
public class EInvoiceOutwardDocSave {

	public static void main(String[] args) {

		OutwardTransDocument document = new OutwardTransDocument();
		document.setDataOriginTypeCode(
				GSTConstants.DataOriginTypeCodes.SFTP.getDataOriginTypeCode());

		if (!document.getDataOriginTypeCode().equalsIgnoreCase(
				GSTConstants.DataOriginTypeCodes.SFTP.getDataOriginTypeCode())
				|| document.getDataOriginTypeCode().equalsIgnoreCase(
						GSTConstants.DataOriginTypeCodes.EXCL_UPLOAD
								.getDataOriginTypeCode())) {

			System.out.println("If");
		} else {
			System.out.println("else");
		}
	}

	private static final Logger LOGGER = LoggerFactory
			.getLogger(EInvoiceOutwardDocSave.class);
	private static final String CLASS_NAME = "EInvoiceOutwardDocSave";

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docHeaderRepository;

	@Autowired
	@Qualifier("EInvoiceDefaultDuplicateDocCheckServiceImpl")
	private EInvoiceDuplicateDocCheckService duplicateDocCheckService;

	@Autowired
	@Qualifier("EInvoiceDefaultOutwardSupplyTypeResolver")
	EInvoiceOutwardSupplyTypeResolver outwardSupplyTypeResolver;

	@Autowired
	@Qualifier("EInvoiceOutwardFieldTruncation")
	private EInvoiceOutwardFieldTruncation outwardFieldTruncation;

	@Autowired
	@Qualifier("EInvoiceOutwardDocMemoValCalculation")
	private EInvoiceOutwardDocMemoValCalculation outwardDocMemoValCalc;

	@Autowired
	@Qualifier("OnboardingConfigParamsCheck")
	private OnboardingConfigParamsCheck onboardingConfigParamCheck;

	@Autowired
	@Qualifier("EinvoiceOutwardConfigParams")
	private EinvoiceOutwardConfigParams einvoiceOutwardConfigParams;

	@Autowired
	@Qualifier("DefaultClientGroupService")
	private ClientGroupService clientGroupService;

	@Autowired
	@Qualifier("OrganizationServiceImpl")
	private OrganizationService orgSvc;

	@Autowired
	@Qualifier("groupInfoDetailsRepository")
	private GroupInfoDetailsRepository groupInfoDetailsRepository;

	@Autowired
	@Qualifier("masterCustomerService")
	private MasterCustomerService masterCustomerSvc;

	@Autowired
	@Qualifier("masterProductService")
	private MasterProductService masterProductSvc;

	/*
	 * @Autowired
	 * 
	 * @Qualifier("EinvEwbJobCheckService") private EinvEwbJobCheckService
	 * einvEwbJobCheckService;
	 */

	@Autowired
	private DocKeyGenerator<OutwardTransDocument, String> docKeyGen;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docRepository;

	@Autowired
	@Qualifier("ContractDetailsRepository")
	private ContractDetailsRepository contractDetailsRepository;

	@Autowired
	@Qualifier("AdditionalDocDetailsRepository")
	private AdditionalDocDetailsRepository additionalDocDetailsRepository;

	@Autowired
	@Qualifier("SourceInfoRepository")
	private SourceInfoRepository sourceInfoRepository;

	@Autowired
	@Qualifier("PreceedingDocDetailsRepository")
	private PreceedingDocDetailsRepository preceedingDocDetailsRepository;

	@Autowired
	@Qualifier("GstnApi")
	private GstnApi gstnApi;

	private static final List<String> DOC_TYPE = ImmutableList.of(
			GSTConstants.CR, GSTConstants.DR, GSTConstants.RCR,
			GSTConstants.RDR, GSTConstants.RNV);

	private static final List<String> REVERSE_CHARGE = ImmutableList
			.of(GSTConstants.Y, GSTConstants.L);

	private static final List<String> DATA_ORIGION_TYPE_API = ImmutableList
			.of("A", "AI", "E", "EI");
	private static final List<String> DATA_ORIGION_TYPE_BCAPI = ImmutableList
			.of("B", "BI");
	private static final String DOC_KEY_JOINER = "|";
	private static final List<String> DATA_ORIGION_TYPE_CORRECTION_API = ImmutableList
			.of("AI", "EI", "SI", "BI", "CI");
	private static final List<String> DATA_ORIGION_TYPE_ERP_AUTO = ImmutableList
			.of("A", "B");

	/**
	 * This method is responsible for converting all gstins, supply type, doc
	 * type to upper case and setting Supply Type to Header from Items and form
	 * return type ANX1/GSTR1 based on Entity Tax Period
	 * 
	 * @param documents
	 */
	public void convertCalcConfigAndSetValues(
			List<OutwardTransDocument> documents, String sourceId,
			String headerPayloadId) {
		List<Long> entityIds = clientGroupService
				.findEntityDetailsForGroupCode();
		Map<String, Long> gstinAndEntityMap = clientGroupService
				.getGstinAndEntityMapForGroupCode(entityIds);
		Map<Long, List<EntityConfigPrmtEntity>> map = onboardingConfigParamCheck
				.getEntityAndConfParamMap();
		Map<EntityAtConfigKey, Map<Long, String>> entityAtConfigMap = orgSvc
				.getEntityAtConfMap(
						AspDocumentConstants.TransDocTypes.OUTWARD.getType());
		Map<Long, List<Pair<String, String>>> entityAtValMap = orgSvc
				.getAllEntityAtValMap();
		String groupCode = TenantContext.getTenantId();
		Long groupId = groupInfoDetailsRepository.findByGroupId(groupCode);
		List<String> allMasterCustomerGstins = masterCustomerSvc
				.getAllMasterCustomerGstins();
		Map<Long, Map<String, List<Pair<Integer, BigDecimal>>>> allMasterProds = masterProductSvc
				.getAllMasterProducts();

		/*
		 * Map<String, Pair<Integer, Integer>> einvEwbMap =
		 * einvEwbJobCheckService .getJobByGstin(documents);
		 */

		Map<String, Pair<Integer, Integer>> preferenceMap = new HashMap<>();

		documents.forEach(document -> {

			setPosAndStateCodeValues(document);
			setDataOriginTypeCode(document);
			setDerivedSourceIdAndPayloadId(document, sourceId, headerPayloadId);
			setEwbInvDetails(document);
			// Convert Doc Type Supply Type and all GSTINs to upper case
			convertToUpperCaseAndSetDocValues(document);
			trimForTaxScheme(document);
			setAbsoluteValuesHeaderLevel(document);
			if (null != document.getLineItems()) {
				document.getLineItems().forEach(item -> {
					setHeaderValuesToItem(document, item);
					setAbsoluteValuesItemLevel(document, item);
					setItemAmountsToZero(document, item);
					setLineItemAmtByItemAmt(item);
					calculateAndSetTaxPayble(document, item);
					calculateAndSetTaxableValue(document, item);
					calculateAndSetItemTaxRate(document, item);
					calculateAndSetTotalTaxAmt(document, item);
					calculateInvAmounts(document, item);
					calculateAndSetDocAmount(document, item);
				});
			}
			if (document.getPreDocDtls() != null
					&& !document.getPreDocDtls().isEmpty()) {
				document.getPreDocDtls().forEach(preDetls -> {
					setValuesToPreceeding(document, preDetls);

					PreceedingDocDetails preFirstRec = document.getPreDocDtls()
							.get(0);
					if (preFirstRec != null) {
						document.setPreceedingInvoiceNumber(
								preFirstRec.getPreceedingInvoiceNumber());
						document.setPreceedingInvoiceDate(
								preFirstRec.getPreceedingInvoiceDate());
						if (document.getLineItems() != null) {
							document.getLineItems().forEach(item -> {
								setInvRefValToItem(document, item, preFirstRec);
							});
						}
					}
				});
			}
			if (document.getContrDtls() != null
					&& !document.getContrDtls().isEmpty()) {
				document.getContrDtls().forEach(contrDetails -> {
					setValuesToContr(document, contrDetails);

					ContractDetails contractDetFirstRec = document
							.getContrDtls().get(0);
					if (contractDetFirstRec != null) {
						if (document.getLineItems() != null) {
							document.getLineItems().forEach(item -> {
								setContractDetailsToItem(item,
										contractDetFirstRec);
							});
						}
					}
				});
			}
			if (document.getAddlDocDtls() != null
					&& !document.getAddlDocDtls().isEmpty()) {
				document.getAddlDocDtls().forEach(addlDetails -> {
					setValuestoAddlDetails(document, addlDetails);

					AdditionalDocDetails additionalDetFirstRec = document
							.getAddlDocDtls().get(0);
					if (additionalDetFirstRec != null) {
						if (document.getLineItems() != null) {
							document.getLineItems().forEach(item -> {
								setAdditionalDetailsToItem(item,
										additionalDetFirstRec);
							});
						}
					}
				});
			}
			if (document.getLineItems() != null) {
				document.getLineItems().forEach(item -> {
					if (item.getAttribDtls() != null
							&& !item.getAttribDtls().isEmpty()) {
						AttributeDetails attDetFirstRecord = item
								.getAttribDtls().get(0);
						setAttrinutesToItem(item, attDetFirstRecord);
						item.getAttribDtls().forEach(attDetails -> {
							setValuestoAttDetails(document, item, attDetails);
						});
					}
				});
			}

			// Set Onboarding Configurable Parameters
			String sgstin = document.getSgstin();
			if (GSTConstants.I
					.equalsIgnoreCase(document.getTransactionType())) {
				if (document.getCgstin() != null
						&& !document.getCgstin().isEmpty()
						&& document.getCgstin().length() == 15) {
					sgstin = document.getCgstin();
				}
			}
			Long entityId = gstinAndEntityMap.get(sgstin);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("sgstin " + sgstin + " - entityId " + entityId);
			}
			document.setGroupId(groupId);
			document.setEntityId(entityId);
			document.setEntityConfigParamMap(map);
			document.setEntityAtConfMap(entityAtConfigMap);
			document.setEntityAtValMap(entityAtValMap);
			Map<String, String> questionAnsMap = onboardingConfigParamCheck
					.getQuestionAndAnswerMap(entityId, map);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("questionAnsMap " + questionAnsMap
						+ " for entityId " + entityId);
			}
			String serviceOption = questionAnsMap
					.get(OnboardingConstant.CONFIG_PARAM_GENERAL_QUE_KEY_ID.G10
							.name());

			String ewbGenerateOpt = questionAnsMap
					.get(OnboardingConstant.CONFIG_PARAM_GENERAL_QUE_KEY_ID.G11
							.name());

			String optedForEinvoice = questionAnsMap
					.get(OnboardingConstant.CONFIG_PARAM_GENERAL_QUE_KEY_ID.G12
							.name());

			String optedForEwb = questionAnsMap
					.get(OnboardingConstant.CONFIG_PARAM_GENERAL_QUE_KEY_ID.G13
							.name());
			String ifUqcNotFoundInMaster = questionAnsMap
					.get(OnboardingConstant.CONFIG_PARAM_OUTWARD_QUE_KEY_ID.O19
							.name());

			if (entityId != null) {
				document.setServiceOption(serviceOption);
				document.setEwbGenerateOption(ewbGenerateOpt);
				document.setOptedForEinv(optedForEinvoice);
				document.setOptedForEwb(optedForEwb);
				document.setUqcNotFoundInMaster(ifUqcNotFoundInMaster);
			} else {
				document.setServiceOption(null);
				document.setOptedForEinv(null);
				document.setOptedForEwb(null);
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("serviceOption {}" + serviceOption);
			}
			if (document.getEntityId() != null) {
				Pair<Integer, Integer> preferencePair = getPreference(document,
						preferenceMap);

				if (LOGGER.isDebugEnabled()) {
					if (preferencePair == null) {
						LOGGER.debug(
								"we are not able to set the ewb and eInvoive "
										+ "prefernces because from DB  we are getting "
										+ "null values for docKey{}",
								document.getDocKey());
					}
				}

				if (preferencePair != null) {
					document.setEinvJob(preferencePair.getValue0());
					document.setEwbJob(preferencePair.getValue1());
					LOGGER.debug(
							"einvoiceprefernce{} and  einvoiceprefernce{} for docKey{}",
							preferencePair.getValue0(),
							preferencePair.getValue1(), document.getDocKey());
				}
			}

			// Set form type as ANX1 or GSTR1 based on Entity Tax period
			setFormReturnType(document, gstinAndEntityMap, questionAnsMap);
			// Check If Customer GSTIN in Master Customer Table
			boolean isCgstInMasterCustTable = false;
			if (allMasterCustomerGstins != null
					&& !allMasterCustomerGstins.isEmpty()) {
				if (document.getCgstin() != null
						&& !document.getCgstin().trim().isEmpty()) {
					isCgstInMasterCustTable = allMasterCustomerGstins.stream()
							.anyMatch(document.getCgstin()::equalsIgnoreCase);
				}
			}
			document.setIsCgstInMasterCust(isCgstInMasterCustTable);
			// Get GSTIN Details for Entity Id from Master Product Table
			Map<String, List<Pair<Integer, BigDecimal>>> masterProdMap = allMasterProds
					.get(entityId);
			document.setMasterProductMap(masterProdMap);
			einvoiceOutwardConfigParams.configurableParameters(document,
					questionAnsMap);
			einvoiceOutwardConfigParams.configurebleOnboardingLineItemAmt(
					document, questionAnsMap);

		});
	}

	private void trimForTaxScheme(OutwardTransDocument document) {

		if (!Strings.isNullOrEmpty(document.getTaxScheme())) {
			document.setTaxScheme(document.getTaxScheme().trim());
		}
	}

	private void setEwbInvDetails(OutwardTransDocument document) {

		if (GSTConstants.DataOriginTypeCodes.BC_API.getDataOriginTypeCode()
				.equalsIgnoreCase(document.getDataOriginTypeCode())
				|| GSTConstants.DataOriginTypeCodes.BC_API_INV_MANAGMENT_CORR
						.getDataOriginTypeCode()
						.equalsIgnoreCase(document.getDataOriginTypeCode())) {

			if (document.getEwbDetails() != null) {
				if (!Strings.isNullOrEmpty(
						document.getEwbDetails().getEwayBillNo())) {
					document.setEwbNoresp(Long
							.valueOf(document.getEwbDetails().getEwayBillNo()));
				}
				document.setEwbDateResp(
						document.getEwbDetails().getEwayBillDate());
			}
			if (document.geteInvDetails() != null) {
				document.setIrnResponse(document.geteInvDetails().getIrn());
				document.setAckDate(document.geteInvDetails().getAckDate());
				document.setAckNum(document.geteInvDetails().getAckNo());
			}
		}
	}

	private void setDerivedSourceIdAndPayloadId(OutwardTransDocument document,
			String sourceId, String headerPayloadId) {
		// setting derivedSouceId based on companyCode
		document.setDerivedSourceId(sourceId);

		if (Strings.isNullOrEmpty(document.getPayloadId())) {
			document.setPayloadId(headerPayloadId);
		}
	}

	private void setLineItemAmtByItemAmt(OutwardTransDocLineItem item) {
		// setting lineItemAmt with itemAmount when lineItemAmt is not there
		if (item.getLineItemAmt() == null) {
			item.setLineItemAmt(item.getItemAmount());
		}
	}

	public List<OutwardTransDocument> saveDocs(List<OutwardTransDocument> docs,
			DocKeyGenerator<OutwardTransDocument, String> docKeyGen) {

		/**
		 * Start : Below code is for updating the existing document The below
		 * code is executed in case of document error correction
		 * 
		 */
		User user = SecurityContext.getUser();
		// Get all the non-null ids from the list. This will be used to
		// mark the existing documents in the DB as deleted.
		List<Long> docIds = docs.stream()
				.filter(doc -> doc.getId() != null && !doc.isDeleted())
				.map(doc -> doc.getId()).collect(Collectors.toList());
		OutwardTransDocument firstOutwardDoc = docs.get(0);
		// Execute the repository query to udpate the isDelete to true for the
		// above ids. Also mark the updated date to the current date.
		// call the is delete to true repository method.
		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"UPDATE_DOC_DELETE_START", CLASS_NAME, "saveDocs", null);
		if (!docIds.isEmpty()) {
			// LocalDateTime updatedDate = LocalDateTime.now();
			LocalDateTime updatedDate = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			docHeaderRepository.updateDocDeletion(docIds, updatedDate,
					firstOutwardDoc.getCreatedBy());

			/*
			 * List<OutwardTransDocument> dbDocs =
			 * docHeaderRepository.findAllById(docIds);
			 * 
			 * dbDocs.forEach(inactiveDocs -> { inactiveDocs.setDeleted(true);
			 * inactiveDocs.setUpdatedDate(updatedDate);
			 * inactiveDocs.setModifiedBy(firstOutwardDoc.getCreatedBy());
			 * if(inactiveDocs.getContrDtls() != null){
			 * inactiveDocs.getContrDtls().forEach(contrctDetails -> {
			 * contrctDetails.setDeleted(true);
			 * contrctDetails.setUpdatedDate(updatedDate);
			 * contrctDetails.setModifiedBy(firstOutwardDoc.getCreatedBy()); });
			 * } if(inactiveDocs.getPreDocDtls() != null){
			 * inactiveDocs.getPreDocDtls().forEach(precDetails -> {
			 * precDetails.setDeleted(true);
			 * precDetails.setUpdatedDate(updatedDate);
			 * precDetails.setModifiedBy(firstOutwardDoc.getCreatedBy()); }); }
			 * if(inactiveDocs.getAddlDocDtls() != null){
			 * inactiveDocs.getAddlDocDtls().forEach(addlDetails -> {
			 * addlDetails.setDeleted(true);
			 * addlDetails.setUpdatedDate(updatedDate);
			 * addlDetails.setModifiedBy(firstOutwardDoc.getCreatedBy()); }); }
			 * }); docRepository.saveAll(dbDocs);
			 */

			docs.forEach(doc -> {
				doc.setGstnError(false);
				doc.setSaved(false);
				doc.setSent(false);
			});
		}
		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"UPDATE_DOC_DELETE_END", CLASS_NAME, "saveDocs", null);
		/**
		 * Start - Duplicate Document Check
		 */
		/*
		 * EInvoiceDuplicateDocCheckDto duplicateDocCheckDto =
		 * duplicateDocCheckService .checkDuplicateDocuments(docs);
		 * 
		 * if (!duplicateDocCheckDto.getDocs().isEmpty()) { LocalDateTime
		 * updatedDate = LocalDateTime.now(); List<OutwardTransDocument>
		 * duplicateDocs = duplicateDocCheckDto .getDocs(); List<Long>
		 * duplicateDocIds = new ArrayList<>(); if (!duplicateDocs.isEmpty()) {
		 * duplicateDocs.forEach(duplicateDoc -> {
		 * duplicateDocIds.add(duplicateDoc.getId()); }); if
		 * (!duplicateDocIds.isEmpty()) {
		 * docHeaderRepository.updateDocDeletion(duplicateDocIds, updatedDate,
		 * firstOutwardDoc.getCreatedBy()); } } }
		 * 
		 * if (!duplicateDocCheckDto.getGstnSubmittedDocs().isEmpty()) { //
		 * TO-DO }
		 */
		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"SAVE_DOC_SOFT_DELETE_START", CLASS_NAME, "saveDocs", null);

		List<OutwardTransDocument> activeDocList = docs.parallelStream()
				.filter(doc -> !doc.isDeleted()).collect(Collectors.toList());
		if (!activeDocList.isEmpty()) {
			duplicateDocCheckService
					.softDeleteDupDocsAndRetainEwbDetails(activeDocList);
		}

		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"SAVE_DOC_SOFT_DELETE_END", CLASS_NAME, "saveDocs", null);
		/**
		 * End - Duplicate Document Check
		 */

		// explictly set the documeent ids to null in the input collection
		// to be saved.
		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"EINV_PROCESSING_STATUS_START", CLASS_NAME, "saveDocs", null);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(" DOCS {} ", docs.toArray());
			LOGGER.debug(" AspInvoiceStatus.ASP_ERROR  {} ",
					AspInvoiceStatus.ASP_ERROR.getAspInvoiceStatusCode());
			LOGGER.debug(" AspInvoiceStatus.ASP_Processed {} ",
					AspInvoiceStatus.ASP_PROCESSED.getAspInvoiceStatusCode());

		}
		docs.forEach(doc -> {

			doc.setId(null);

			if (doc.geteInvStatus() != null
					&& doc.getEwbProcessingStatus() != null) {
				if (doc.geteInvStatus() == EInvoiceStatus.NOT_OPTED
						.geteInvoiceStatusCode()) {
					if (doc.getEwbProcessingStatus() == EwbProcessingStatus.NOT_APPLICABLE
							.getEwbProcessingStatusCode()) {
						if (!doc.getComplianceApplicable() && !GSTConstants.DLC
								.equalsIgnoreCase(doc.getDocType())) {
							doc.setAspInvoiceStatus(AspInvoiceStatus.ASP_ERROR
									.getAspInvoiceStatusCode());
							LOGGER.error("Inside Loop 1 -- ");
						} else {
							if (doc.getIsProcessed()) {
								LOGGER.error("Inside Loop2 1 -- ");
								doc.setAspInvoiceStatus(
										AspInvoiceStatus.ASP_PROCESSED
												.getAspInvoiceStatusCode());
							} else if (doc.getIsError()) {
								LOGGER.error("Inside Loop2 2 -- ");
								doc.setAspInvoiceStatus(
										AspInvoiceStatus.ASP_ERROR
												.getAspInvoiceStatusCode());
							}
						}
					}

					if (doc.getEwbProcessingStatus() == EwbProcessingStatus.NOT_OPTED
							.getEwbProcessingStatusCode()) {
						if (!doc.getComplianceApplicable() && !GSTConstants.DLC
								.equalsIgnoreCase(doc.getDocType())) {
							LOGGER.error("Inside Loop 2 -- ");
							doc.setAspInvoiceStatus(AspInvoiceStatus.ASP_ERROR
									.getAspInvoiceStatusCode());
						} else {
							if (doc.getIsProcessed()) {
								LOGGER.error("Inside Loop 2 3-- ");
								doc.setAspInvoiceStatus(
										AspInvoiceStatus.ASP_PROCESSED
												.getAspInvoiceStatusCode());
							} else if (doc.getIsError()) {
								doc.setAspInvoiceStatus(
										AspInvoiceStatus.ASP_ERROR
												.getAspInvoiceStatusCode());

							}
						}
					}
					if (((doc
							.getEwbProcessingStatus() == EwbProcessingStatus.ASP_PROCESSED
									.getEwbProcessingStatusCode())
							|| (doc.getEwbProcessingStatus() == EwbProcessingStatus.EWAY_BILL_GENERATED_ERP
									.getEwbProcessingStatusCode())
							|| (doc.getEwbProcessingStatus() == EwbProcessingStatus.EWAY_BILL_NOT_GENERATED_ERP
									.getEwbProcessingStatusCode()))
							&& (!doc.getComplianceApplicable())) {
						LOGGER.error("Inside Loop 2 4-- ");
						doc.setAspInvoiceStatus(AspInvoiceStatus.ASP_PROCESSED
								.getAspInvoiceStatusCode());
					} else if ((doc
							.getEwbProcessingStatus() == EwbProcessingStatus.ASP_ERROR
									.getEwbProcessingStatusCode())
							&& (!doc.getComplianceApplicable()
									&& !GSTConstants.DLC.equalsIgnoreCase(
											doc.getDocType()))) {
						LOGGER.error("Inside Loop 3 -- ");
						doc.setAspInvoiceStatus(AspInvoiceStatus.ASP_ERROR
								.getAspInvoiceStatusCode());
					} else if (((doc
							.getEwbProcessingStatus() == EwbProcessingStatus.ASP_PROCESSED
									.getEwbProcessingStatusCode())
							|| (doc.getEwbProcessingStatus() == EwbProcessingStatus.EWAY_BILL_GENERATED_ERP
									.getEwbProcessingStatusCode())
							|| (doc.getEwbProcessingStatus() == EwbProcessingStatus.EWAY_BILL_NOT_GENERATED_ERP
									.getEwbProcessingStatusCode()))
							&& (doc.getIsProcessed())
							&& (doc.getComplianceApplicable())) {
						LOGGER.error("Inside Loop 2 4-- ");
						doc.setAspInvoiceStatus(AspInvoiceStatus.ASP_PROCESSED
								.getAspInvoiceStatusCode());
					} else if (((doc
							.getEwbProcessingStatus() == EwbProcessingStatus.ASP_PROCESSED
									.getEwbProcessingStatusCode())
							|| (doc.getEwbProcessingStatus() == EwbProcessingStatus.EWAY_BILL_GENERATED_ERP
									.getEwbProcessingStatusCode())
							|| (doc.getEwbProcessingStatus() == EwbProcessingStatus.EWAY_BILL_NOT_GENERATED_ERP
									.getEwbProcessingStatusCode()))
							&& (doc.getIsError())
							&& (doc.getComplianceApplicable())) {
						doc.setAspInvoiceStatus(AspInvoiceStatus.ASP_ERROR
								.getAspInvoiceStatusCode());
					} else if ((doc
							.getEwbProcessingStatus() == EwbProcessingStatus.ASP_ERROR
									.getEwbProcessingStatusCode())
							&& (doc.getIsProcessed())
							&& (doc.getComplianceApplicable())) {
						doc.setAspInvoiceStatus(AspInvoiceStatus.ASP_ERROR
								.getAspInvoiceStatusCode());
					} else if ((doc
							.getEwbProcessingStatus() == EwbProcessingStatus.ASP_ERROR
									.getEwbProcessingStatusCode())
							&& (doc.getIsError())
							&& (doc.getComplianceApplicable())) {
						doc.setAspInvoiceStatus(AspInvoiceStatus.ASP_ERROR
								.getAspInvoiceStatusCode());
					}
				} else if (doc.geteInvStatus() == EInvoiceStatus.NOT_APPLICABLE
						.geteInvoiceStatusCode()) {
					if (doc.getEwbProcessingStatus() == EwbProcessingStatus.NOT_APPLICABLE
							.getEwbProcessingStatusCode()) {
						if (!doc.getComplianceApplicable() && !GSTConstants.DLC
								.equalsIgnoreCase(doc.getDocType())) {
							LOGGER.error("Inside Loop 4 -- ");
							doc.setAspInvoiceStatus(AspInvoiceStatus.ASP_ERROR
									.getAspInvoiceStatusCode());
						} else {
							if (doc.getIsProcessed()) {
								LOGGER.error("Inside Loop 2 5-- ");
								doc.setAspInvoiceStatus(
										AspInvoiceStatus.ASP_PROCESSED
												.getAspInvoiceStatusCode());
							} else if (doc.getIsError()) {
								doc.setAspInvoiceStatus(
										AspInvoiceStatus.ASP_ERROR
												.getAspInvoiceStatusCode());
							}
						}
					} else if (doc
							.getEwbProcessingStatus() == EwbProcessingStatus.NOT_OPTED
									.getEwbProcessingStatusCode()) {
						if (!doc.getComplianceApplicable() && !GSTConstants.DLC
								.equalsIgnoreCase(doc.getDocType())) {
							LOGGER.error("Inside Loop 5 -- ");
							doc.setAspInvoiceStatus(AspInvoiceStatus.ASP_ERROR
									.getAspInvoiceStatusCode());
						} else {
							if (doc.getIsProcessed()) {
								LOGGER.error("Inside Loop 2 6-- ");
								doc.setAspInvoiceStatus(
										AspInvoiceStatus.ASP_PROCESSED
												.getAspInvoiceStatusCode());
							} else if (doc.getIsError()) {
								doc.setAspInvoiceStatus(
										AspInvoiceStatus.ASP_ERROR
												.getAspInvoiceStatusCode());
							}
						}
					}
					if (((doc
							.getEwbProcessingStatus() == EwbProcessingStatus.ASP_PROCESSED
									.getEwbProcessingStatusCode())
							|| (doc.getEwbProcessingStatus() == EwbProcessingStatus.EWAY_BILL_GENERATED_ERP
									.getEwbProcessingStatusCode())
							|| (doc.getEwbProcessingStatus() == EwbProcessingStatus.EWAY_BILL_NOT_GENERATED_ERP
									.getEwbProcessingStatusCode()))
							&& (!doc.getComplianceApplicable())) {
						LOGGER.error("Inside Loop 2 7-- ");
						doc.setAspInvoiceStatus(AspInvoiceStatus.ASP_PROCESSED
								.getAspInvoiceStatusCode());
					} else if ((doc
							.getEwbProcessingStatus() == EwbProcessingStatus.ASP_ERROR
									.getEwbProcessingStatusCode())
							&& (!doc.getComplianceApplicable()
									&& !GSTConstants.DLC.equalsIgnoreCase(
											doc.getDocType()))) {
						LOGGER.error("Inside Loop 6 -- ");
						doc.setAspInvoiceStatus(AspInvoiceStatus.ASP_ERROR
								.getAspInvoiceStatusCode());

					} else if (((doc
							.getEwbProcessingStatus() == EwbProcessingStatus.ASP_PROCESSED
									.getEwbProcessingStatusCode())
							|| (doc.getEwbProcessingStatus() == EwbProcessingStatus.EWAY_BILL_GENERATED_ERP
									.getEwbProcessingStatusCode())
							|| (doc.getEwbProcessingStatus() == EwbProcessingStatus.EWAY_BILL_NOT_GENERATED_ERP
									.getEwbProcessingStatusCode()))
							&& (doc.getIsProcessed())
							&& (doc.getComplianceApplicable())) {
						doc.setAspInvoiceStatus(AspInvoiceStatus.ASP_PROCESSED
								.getAspInvoiceStatusCode());
						LOGGER.error("Inside Loop 2 8-- ");
					} else if (((doc
							.getEwbProcessingStatus() == EwbProcessingStatus.ASP_PROCESSED
									.getEwbProcessingStatusCode())
							|| (doc.getEwbProcessingStatus() == EwbProcessingStatus.EWAY_BILL_GENERATED_ERP
									.getEwbProcessingStatusCode())
							|| (doc.getEwbProcessingStatus() == EwbProcessingStatus.EWAY_BILL_NOT_GENERATED_ERP
									.getEwbProcessingStatusCode()))
							&& (doc.getIsError())
							&& (doc.getComplianceApplicable())) {
						doc.setAspInvoiceStatus(AspInvoiceStatus.ASP_ERROR
								.getAspInvoiceStatusCode());
					} else if ((doc
							.getEwbProcessingStatus() == EwbProcessingStatus.ASP_ERROR
									.getEwbProcessingStatusCode())
							&& (doc.getIsProcessed())
							&& (doc.getComplianceApplicable())) {
						doc.setAspInvoiceStatus(AspInvoiceStatus.ASP_ERROR
								.getAspInvoiceStatusCode());
					} else if ((doc
							.getEwbProcessingStatus() == EwbProcessingStatus.ASP_ERROR
									.getEwbProcessingStatusCode())
							&& (doc.getIsError())
							&& (doc.getComplianceApplicable())) {
						doc.setAspInvoiceStatus(AspInvoiceStatus.ASP_ERROR
								.getAspInvoiceStatusCode());
					}
				}

				if (doc.getEwbProcessingStatus() == EwbProcessingStatus.NOT_APPLICABLE
						.getEwbProcessingStatusCode()) {
					if ((doc.geteInvStatus() == EInvoiceStatus.ASP_PROCESSED
							.geteInvoiceStatusCode())
							&& (!doc.getComplianceApplicable())) {
						doc.setAspInvoiceStatus(AspInvoiceStatus.ASP_PROCESSED
								.getAspInvoiceStatusCode());
						LOGGER.error("Inside Loop 2 9-- ");
					} else if ((doc.geteInvStatus() == EInvoiceStatus.ASP_ERROR
							.geteInvoiceStatusCode())
							&& (!doc.getComplianceApplicable()
									&& !GSTConstants.DLC.equalsIgnoreCase(
											doc.getDocType()))) {
						LOGGER.error("Inside Loop 7 -- ");
						doc.setAspInvoiceStatus(AspInvoiceStatus.ASP_ERROR
								.getAspInvoiceStatusCode());
					} else if ((doc
							.geteInvStatus() == EInvoiceStatus.ASP_PROCESSED
									.geteInvoiceStatusCode())
							&& (doc.getIsProcessed())
							&& (doc.getComplianceApplicable())) {
						doc.setAspInvoiceStatus(AspInvoiceStatus.ASP_PROCESSED
								.getAspInvoiceStatusCode());
						LOGGER.error("Inside Loop 2 10-- ");
					} else if ((doc.geteInvStatus() == EInvoiceStatus.ASP_ERROR
							.geteInvoiceStatusCode()) && (doc.getIsProcessed())
							&& (doc.getComplianceApplicable())) {
						doc.setAspInvoiceStatus(AspInvoiceStatus.ASP_ERROR
								.getAspInvoiceStatusCode());
					} else if ((doc
							.geteInvStatus() == EInvoiceStatus.ASP_PROCESSED
									.geteInvoiceStatusCode())
							&& (doc.getIsError())
							&& (doc.getComplianceApplicable())) {
						doc.setAspInvoiceStatus(AspInvoiceStatus.ASP_ERROR
								.getAspInvoiceStatusCode());
					} else if ((doc.geteInvStatus() == EInvoiceStatus.ASP_ERROR
							.geteInvoiceStatusCode()) && (doc.getIsError())
							&& (doc.getComplianceApplicable())) {
						doc.setAspInvoiceStatus(AspInvoiceStatus.ASP_ERROR
								.getAspInvoiceStatusCode());
					}
				}

				if (doc.getEwbProcessingStatus() == EwbProcessingStatus.NOT_OPTED
						.getEwbProcessingStatusCode()) {
					if ((doc.geteInvStatus() == EInvoiceStatus.ASP_PROCESSED
							.geteInvoiceStatusCode())
							&& (!doc.getComplianceApplicable())) {
						doc.setAspInvoiceStatus(AspInvoiceStatus.ASP_PROCESSED
								.getAspInvoiceStatusCode());
						LOGGER.error("Inside Loop 2 11-- ");
					} else if ((doc.geteInvStatus() == EInvoiceStatus.ASP_ERROR
							.geteInvoiceStatusCode())
							&& (!doc.getComplianceApplicable()
									&& !GSTConstants.DLC.equalsIgnoreCase(
											doc.getDocType()))) {
						LOGGER.error("Inside Loop 10 -- ");
						doc.setAspInvoiceStatus(AspInvoiceStatus.ASP_ERROR
								.getAspInvoiceStatusCode());
					} else if ((doc
							.geteInvStatus() == EInvoiceStatus.ASP_PROCESSED
									.geteInvoiceStatusCode())
							&& (doc.getIsProcessed())
							&& (doc.getComplianceApplicable())) {
						doc.setAspInvoiceStatus(AspInvoiceStatus.ASP_PROCESSED
								.getAspInvoiceStatusCode());
					} else if ((doc
							.geteInvStatus() == EInvoiceStatus.ASP_PROCESSED
									.geteInvoiceStatusCode())
							&& (doc.getIsError())
							&& (doc.getComplianceApplicable())) {
						doc.setAspInvoiceStatus(AspInvoiceStatus.ASP_ERROR
								.getAspInvoiceStatusCode());
						LOGGER.error("Inside Loop 2 11-- ");
					} else if ((doc.geteInvStatus() == EInvoiceStatus.ASP_ERROR
							.geteInvoiceStatusCode()) && (doc.getIsProcessed())
							&& (doc.getComplianceApplicable())) {
						doc.setAspInvoiceStatus(AspInvoiceStatus.ASP_ERROR
								.getAspInvoiceStatusCode());
					} else if ((doc.geteInvStatus() == EInvoiceStatus.ASP_ERROR
							.geteInvoiceStatusCode()) && (doc.getIsError())
							&& (doc.getComplianceApplicable())) {
						doc.setAspInvoiceStatus(AspInvoiceStatus.ASP_ERROR
								.getAspInvoiceStatusCode());
					}
				}
				if (!doc.getComplianceApplicable()) {
					if ((doc.geteInvStatus() == EInvoiceStatus.ASP_PROCESSED
							.geteInvoiceStatusCode())
							&& ((doc.getEwbProcessingStatus() == EwbProcessingStatus.ASP_PROCESSED
									.getEwbProcessingStatusCode())
									|| (doc.getEwbProcessingStatus() == EwbProcessingStatus.EWAY_BILL_GENERATED_ERP
											.getEwbProcessingStatusCode())
									|| (doc.getEwbProcessingStatus() == EwbProcessingStatus.EWAY_BILL_NOT_GENERATED_ERP
											.getEwbProcessingStatusCode()))) {
						doc.setAspInvoiceStatus(AspInvoiceStatus.ASP_PROCESSED
								.getAspInvoiceStatusCode());
						LOGGER.error("Inside Loop 2 12-- ");
					} else if ((doc.geteInvStatus() == EInvoiceStatus.ASP_ERROR
							.geteInvoiceStatusCode())
							&& ((doc.getEwbProcessingStatus() == EwbProcessingStatus.ASP_PROCESSED
									.getEwbProcessingStatusCode())
									|| (doc.getEwbProcessingStatus() == EwbProcessingStatus.EWAY_BILL_GENERATED_ERP
											.getEwbProcessingStatusCode())
									|| (doc.getEwbProcessingStatus() == EwbProcessingStatus.EWAY_BILL_NOT_GENERATED_ERP
											.getEwbProcessingStatusCode()))) {
						LOGGER.error("Inside Loop 11 -- ");
						doc.setAspInvoiceStatus(AspInvoiceStatus.ASP_ERROR
								.getAspInvoiceStatusCode());
					} else if ((doc
							.geteInvStatus() == EInvoiceStatus.ASP_PROCESSED
									.geteInvoiceStatusCode())
							&& (doc.getEwbProcessingStatus() == EwbProcessingStatus.ASP_ERROR
									.getEwbProcessingStatusCode())) {
						doc.setAspInvoiceStatus(AspInvoiceStatus.ASP_ERROR
								.getAspInvoiceStatusCode());
					} else if ((doc.geteInvStatus() == EInvoiceStatus.ASP_ERROR
							.geteInvoiceStatusCode())
							&& (doc.getEwbProcessingStatus() == EwbProcessingStatus.ASP_ERROR
									.getEwbProcessingStatusCode())) {
						LOGGER.error("Inside Loop 12 -- ");
						doc.setAspInvoiceStatus(AspInvoiceStatus.ASP_ERROR
								.getAspInvoiceStatusCode());
					}
				} else {

					if ((doc.geteInvStatus() == EInvoiceStatus.ASP_PROCESSED
							.geteInvoiceStatusCode())
							&& ((doc.getEwbProcessingStatus() == EwbProcessingStatus.ASP_PROCESSED
									.getEwbProcessingStatusCode())
									|| (doc.getEwbProcessingStatus() == EwbProcessingStatus.EWAY_BILL_GENERATED_ERP
											.getEwbProcessingStatusCode())
									|| (doc.getEwbProcessingStatus() == EwbProcessingStatus.EWAY_BILL_NOT_GENERATED_ERP
											.getEwbProcessingStatusCode()))
							&& (doc.getIsProcessed())) {

						LOGGER.error("Inside Loop 2 13-- ");
						doc.setAspInvoiceStatus(AspInvoiceStatus.ASP_PROCESSED
								.getAspInvoiceStatusCode());
					} else if ((doc
							.geteInvStatus() == EInvoiceStatus.ASP_PROCESSED
									.geteInvoiceStatusCode())
							&& ((doc.getEwbProcessingStatus() == EwbProcessingStatus.ASP_PROCESSED
									.getEwbProcessingStatusCode())
									|| (doc.getEwbProcessingStatus() == EwbProcessingStatus.EWAY_BILL_GENERATED_ERP
											.getEwbProcessingStatusCode())
									|| (doc.getEwbProcessingStatus() == EwbProcessingStatus.EWAY_BILL_NOT_GENERATED_ERP
											.getEwbProcessingStatusCode()))
							&& (doc.getIsError())) {
						doc.setAspInvoiceStatus(AspInvoiceStatus.ASP_ERROR
								.getAspInvoiceStatusCode());
					} else if ((doc
							.geteInvStatus() == EInvoiceStatus.ASP_PROCESSED
									.geteInvoiceStatusCode())
							&& (doc.getEwbProcessingStatus() == EwbProcessingStatus.ASP_ERROR
									.getEwbProcessingStatusCode())
							&& (doc.getIsProcessed())) {
						LOGGER.error("Inside Loop 13 -- ");
						doc.setAspInvoiceStatus(AspInvoiceStatus.ASP_ERROR
								.getAspInvoiceStatusCode());
					} else if ((doc
							.geteInvStatus() == EInvoiceStatus.ASP_PROCESSED
									.geteInvoiceStatusCode())
							&& (doc.getEwbProcessingStatus() == EwbProcessingStatus.ASP_ERROR
									.getEwbProcessingStatusCode())
							&& (doc.getIsError())) {
						LOGGER.error("Inside Loop 2 14-- ");
						doc.setAspInvoiceStatus(AspInvoiceStatus.ASP_ERROR
								.getAspInvoiceStatusCode());
					}

					else if ((doc.geteInvStatus() == EInvoiceStatus.ASP_ERROR
							.geteInvoiceStatusCode())
							&& ((doc.getEwbProcessingStatus() == EwbProcessingStatus.ASP_PROCESSED
									.getEwbProcessingStatusCode())
									|| (doc.getEwbProcessingStatus() == EwbProcessingStatus.EWAY_BILL_GENERATED_ERP
											.getEwbProcessingStatusCode())
									|| (doc.getEwbProcessingStatus() == EwbProcessingStatus.EWAY_BILL_NOT_GENERATED_ERP
											.getEwbProcessingStatusCode()))
							&& (doc.getIsProcessed())) {
						LOGGER.error("Inside Loop 14 -- ");
						doc.setAspInvoiceStatus(AspInvoiceStatus.ASP_ERROR
								.getAspInvoiceStatusCode());
					} else if ((doc.geteInvStatus() == EInvoiceStatus.ASP_ERROR
							.geteInvoiceStatusCode())
							&& ((doc.getEwbProcessingStatus() == EwbProcessingStatus.ASP_PROCESSED
									.getEwbProcessingStatusCode())
									|| (doc.getEwbProcessingStatus() == EwbProcessingStatus.EWAY_BILL_GENERATED_ERP
											.getEwbProcessingStatusCode())
									|| (doc.getEwbProcessingStatus() == EwbProcessingStatus.EWAY_BILL_NOT_GENERATED_ERP
											.getEwbProcessingStatusCode()))
							&& (doc.getIsError())) {
						LOGGER.error("Inside Loop 15 -- ");
						doc.setAspInvoiceStatus(AspInvoiceStatus.ASP_ERROR
								.getAspInvoiceStatusCode());
					} else if ((doc.geteInvStatus() == EInvoiceStatus.ASP_ERROR
							.geteInvoiceStatusCode())
							&& (doc.getEwbProcessingStatus() == EwbProcessingStatus.ASP_ERROR
									.getEwbProcessingStatusCode())
							&& (doc.getIsProcessed())) {
						doc.setAspInvoiceStatus(AspInvoiceStatus.ASP_ERROR
								.getAspInvoiceStatusCode());
					} else if ((doc.geteInvStatus() == EInvoiceStatus.ASP_ERROR
							.geteInvoiceStatusCode())
							&& (doc.getEwbProcessingStatus() == EwbProcessingStatus.ASP_ERROR
									.getEwbProcessingStatusCode())
							&& (doc.getIsError())) {
						LOGGER.error("Inside Loop 16 -- ");
						doc.setAspInvoiceStatus(AspInvoiceStatus.ASP_ERROR
								.getAspInvoiceStatusCode());
					}
				}
			}

			if (DATA_ORIGION_TYPE_API.contains(
					trimAndConvToUpperCase(doc.getDataOriginTypeCode()))) {
				if ((!Strings.isNullOrEmpty(doc.getIrn()))
						&& doc.getIrn().length() == 64) {

					doc.seteInvStatus(EInvoiceStatus.IRN_GENERATED_IN_ERP
							.geteInvoiceStatusCode());
					doc.setIrnStatus(IrnStatusMaster.ALREADY_GENERATED_BY_USER
							.getIrnStatusMaster());
				}
				if ((!Strings.isNullOrEmpty(doc.geteWayBillNo()))
						&& doc.geteWayBillNo().length() == 12) {
					doc.setEwbProcessingStatus(
							EwbProcessingStatus.EWAY_BILL_GENERATED_ERP
									.getEwbProcessingStatusCode());
					doc.setEwbStatus(EwbStatusNew.ALREADY_GENERATED_BY_USER
							.getEwbNewStatusCode());
				}
			}
			if (DATA_ORIGION_TYPE_BCAPI.contains(
					trimAndConvToUpperCase(doc.getDataOriginTypeCode()))) {
				if (doc.geteInvDetails() != null) {
					if ((!Strings.isNullOrEmpty(doc.geteInvDetails().getIrn()))
							&& doc.geteInvDetails().getIrn().length() == 64) {
						if (!GSTConstants.CAN
								.equalsIgnoreCase(doc.getSupplyType())) {
							doc.seteInvStatus(
									EInvoiceStatus.IRN_GENERATED_IN_ERP
											.geteInvoiceStatusCode());
							doc.setIrnStatus(
									IrnStatusMaster.ALREADY_GENERATED_BY_USER
											.getIrnStatusMaster());
						} else {
							doc.seteInvStatus(EInvoiceStatus.IRN_CANCELLED
									.geteInvoiceStatusCode());
							doc.setIrnStatus(IrnStatusMaster.CANCELLED
									.getIrnStatusMaster());
						}
					}
				}
				if (doc.getEwbDetails() != null) {
					if ((!Strings
							.isNullOrEmpty(doc.getEwbDetails().getEwayBillNo()))
							&& doc.getEwbDetails().getEwayBillNo()
									.length() == 12) {
						doc.setEwbProcessingStatus(
								EwbProcessingStatus.EWAY_BILL_GENERATED_ERP
										.getEwbProcessingStatusCode());
						if (doc.getEwbDetails().getValidUpto() == null) {
							doc.setEwbStatus(EwbStatusNew.PART_A_GENERATED
									.getEwbNewStatusCode());
						} else {
							doc.setEwbStatus(EwbStatusNew.EWB_ACTIVE
									.getEwbNewStatusCode());
						}
					}
				}
			}
			if (!doc.getComplianceApplicable()
					&& doc.getAspInvoiceStatus() == AspInvoiceStatus.ASP_ERROR
							.getAspInvoiceStatusCode()
					&& doc.getEwbProcessingStatus() == EwbProcessingStatus.EWAY_BILL_GENERATED_ERP
							.getEwbProcessingStatusCode()
					&& doc.geteInvStatus() == EInvoiceStatus.IRN_GENERATED_IN_ERP
							.geteInvoiceStatusCode()) {
				doc.setErrCodes(GSTConstants.ER15171);
			}
			
		});

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(" DOCS {} ", docs.toArray());
		}
		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"EINV_PROCESSING_STATUS_END", CLASS_NAME, "saveDocs", null);

		UQCConversion(docs);

		setHeaderPrecInvAmtTaxableValue(docs);
		// Attach the document object with the line items.
		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"DOCUMENT_OBJECT_WITH_LINE_ITEM_START", CLASS_NAME, "saveDocs",
				null);

		docs.forEach(document -> {
			String docKey = docKeyGen.generateKey(document);
			LocalDateTime createdDate = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			// document.setCreatedDate(LocalDateTime.now());
			document.setCreatedDate(createdDate);
			if (LOGGER.isDebugEnabled()) {

				LOGGER.debug(
						"ewbProcessStatus{} and EinvoiceStatus{}  Doc Key{}",
						document.getEwbProcessingStatus(),
						document.geteInvStatus(), docKey);
			}
			// SCL status not changing start

			if (document.geteInvStatus() != null
					&& document.getEwbProcessingStatus() != null
					&& (document
							.geteInvStatus() == EInvoiceStatus.NOT_APPLICABLE
									.geteInvoiceStatusCode()
							|| document
									.geteInvStatus() == EInvoiceStatus.NOT_OPTED
											.geteInvoiceStatusCode())
					&& document
							.getEwbProcessingStatus() == EwbProcessingStatus.EWAY_BILL_GENERATED_ERP
									.getEwbProcessingStatusCode()
					&& !document.getComplianceApplicable()
					&& !document.getIsError()) {

				document.setAspInvoiceStatus(AspInvoiceStatus.ASP_PROCESSED
						.getAspInvoiceStatusCode());
			}

			// SCL status not changing end
			if (document.getAspInvoiceStatus() != null && document
					.getAspInvoiceStatus() == AspInvoiceStatus.ASP_PROCESSED
							.getAspInvoiceStatusCode()) {
				if (document.getIrnStatus() != null
						&& document.getIrnStatus() == IrnStatusMaster.GENERATED
								.getIrnStatusMaster()) {
					document.seteInvStatus(EInvoiceStatus.IRN_GENERATED
							.geteInvoiceStatusCode());
				}
				if (document.getEwbStatus() != null && (document
						.getEwbStatus() == EwbStatusNew.PART_A_GENERATED
								.getEwbNewStatusCode()
						|| document.getEwbStatus() == EwbStatusNew.EWB_ACTIVE
								.getEwbNewStatusCode())) {
					document.setEwbProcessingStatus(
							EwbProcessingStatus.PART_A_GENERATED
									.getEwbProcessingStatusCode());
				}
			}

			if ((document.getEwbProcessingStatus() != null && document
					.getEwbProcessingStatus() == EwbProcessingStatus.NOT_OPTED
							.getEwbProcessingStatusCode())
					&& (document.getAspInvoiceStatus() == 1)
					&& (!document.getComplianceApplicable())
					&& !GSTConstants.DLC
							.equalsIgnoreCase(document.getDocType())) {
				document.setErrCodes(GSTConstants.ER15171);
			}

			if ((!GSTConstants.CAN.equalsIgnoreCase(document.getSupplyType()))
					&& (document
							.getEwbProcessingStatus() == EwbProcessingStatus.NOT_APPLICABLE
									.getEwbProcessingStatusCode())
					&& (document.getAspInvoiceStatus() == 1)
					&& (!document.getComplianceApplicable())
					&& !GSTConstants.DLC
							.equalsIgnoreCase(document.getDocType())) {
				document.setErrCodes(GSTConstants.ER15171);
			}
			if (GSTConstants.ER15171.equalsIgnoreCase(document.getErrCodes())
					&& document.getAspInvoiceStatus() != null
					&& (document
							.getAspInvoiceStatus() == AspInvoiceStatus.ASP_ERROR
									.getAspInvoiceStatusCode())
					&& (!document.getComplianceApplicable())) {
				document.setIsError(true);
			}
			if (document.isDeleted() && document.getAspInvoiceStatus() != null
					&& (document
							.getAspInvoiceStatus() == AspInvoiceStatus.ASP_ERROR
									.getAspInvoiceStatusCode())) {
				if (document.getErrCodes().contains("ER1276")) {
					document.setErrCodes("ER1276");
				} else if (document.getErrCodes().contains("ER1277")) {
					document.setErrCodes("ER1277");
				} else {
					document.setErrCodes(GSTConstants.ER15167);
				}

			}

			document.setDocKey(docKey);
			// Start - Extracted Batch Id and Extracted Date for ERP API
			if (GSTConstants.DataOriginTypeCodes.ERP_API.getDataOriginTypeCode()
					.equalsIgnoreCase(document.getDataOriginTypeCode())) {
				LocalDate extractedDate = (document.getExtractedOn() != null)
						? document.getExtractedOn().toLocalDate() : null;
				document.setExtractedDate(extractedDate);
			}
			// Set Extracted Batch Id and Extracted Date as file id and
			// received date respectively for file upload
			if (GSTConstants.DataOriginTypeCodes.EXCL_UPLOAD
					.getDataOriginTypeCode()
					.equalsIgnoreCase(document.getDataOriginTypeCode())) {
				document.setExtractedBatchId(document.getAcceptanceId());
				document.setExtractedDate(document.getReceivedDate());
			}
			// End - Extracted Batch Id and Extracted Date
			// Truncate Header Fields
			outwardFieldTruncation.truncateHeaderFields(document);
			if (null != document.getPreDocDtls()) {
				document.getPreDocDtls().forEach(preDocDetails -> {
					preDocDetails.setDocKey(docKey);
					preDocDetails.setCreatedDate(createdDate);
					preDocDetails.setCreatedBy(user.getUserPrincipalName());
					preDocDetails.setPreDocDetails(document);
				});
			}
			if (null != document.getAddlDocDtls()) {
				document.getAddlDocDtls().forEach(addlDocDetails -> {
					addlDocDetails.setDocKey(docKey);
					addlDocDetails.setCreatedDate(createdDate);
					addlDocDetails.setCreatedBy(user.getUserPrincipalName());
					addlDocDetails.setAddtitionalDocDetails(document);
				});
			}
			if (null != document.getContrDtls()) {
				document.getContrDtls().forEach(contDetails -> {
					contDetails.setDocKey(docKey);
					contDetails.setCreatedDate(createdDate);
					contDetails.setCreatedBy(user.getUserPrincipalName());
					contDetails.setContractDetails(document);
				});
			}
			if (null != document.getLineItems()) {
				document.getLineItems().forEach(item -> {
					// Truncate Line Item Fields
					outwardFieldTruncation.truncateLineItemFields(item);
					outwardDocMemoValCalc.calculateMemoValues(document, item);
					item.setDocument(document);
					if (item.getAttribDtls() != null) {
						item.getAttribDtls().forEach(attDetails -> {
							attDetails.setDocKey(docKey);
							attDetails.setCreatedDate(createdDate);
							attDetails
									.setCreatedBy(user.getUserPrincipalName());
							attDetails.setAttDetails(item);
						});
					}
				});
			}

			// Set Document Saving Time for Performance Testing
			// LocalDateTime beforeSavingTime = LocalDateTime.now();
			LocalDateTime beforeSavingTime = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			document.setBeforeSavingOn(beforeSavingTime);
		});
		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"DOCUMENT_OBJECT_WITH_LINE_ITEM_END", CLASS_NAME, "saveDocs",
				null);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("DefaultDocSaveService saveDocuments End");
		}
		// save all the documents.
		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"SAVE_DOC_SAVE_DB_START", CLASS_NAME, "saveDocs", null);
		List<OutwardTransDocument> outWardTransList = docHeaderRepository
				.saveAll(docs);
		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"SAVE_DOC_SAVE_DB_END", CLASS_NAME, "saveDocs", null);

		return outWardTransList;
	}

	private void setDataOriginTypeCode(OutwardTransDocument document) {
		/*
		 * Fix for external API call, only when dataorigintypecode is coming as
		 * NULL from external API, setting to A
		 */
		if (Strings.isNullOrEmpty(document.getDataOriginTypeCode())) {
			document.setDataOriginTypeCode(
					GSTConstants.DataOriginTypeCodes.ERP_API
							.getDataOriginTypeCode());
		}
	}

	private void setPosAndStateCodeValues(OutwardTransDocument document) {
		if (!Strings.isNullOrEmpty(document.getPos())) {
			String pos = document.getPos().length() == 1
					? GSTConstants.ZERO + document.getPos() : document.getPos();
			document.setPos(pos);
		}

		if (!Strings.isNullOrEmpty(document.getSupplierStateCode())) {
			String suppStateCode = document.getSupplierStateCode().length() == 1
					? GSTConstants.ZERO + document.getSupplierStateCode()
					: document.getSupplierStateCode();
			document.setSupplierStateCode(suppStateCode);

		}

		if (!Strings.isNullOrEmpty(document.getDispatcherStateCode())) {
			String dispStateCode = document.getDispatcherStateCode()
					.length() == 1
							? GSTConstants.ZERO
									+ document.getDispatcherStateCode()
							: document.getDispatcherStateCode();
			document.setDispatcherStateCode(dispStateCode);

		}

		if (!Strings.isNullOrEmpty(document.getCustOrSuppCode())) {
			String custStateCode = document.getCustOrSuppCode().length() == 1
					? GSTConstants.ZERO + document.getCustOrSuppCode()
					: document.getCustOrSuppCode();
			document.setCustOrSuppCode(custStateCode);

		}

		if (!Strings.isNullOrEmpty(document.getShipToState())) {
			String shipToStateCode = document.getShipToState().length() == 1
					? GSTConstants.ZERO + document.getShipToState()
					: document.getShipToState();
			document.setShipToState(shipToStateCode);

		}

	}

	private void setItemAmountsToZero(OutwardTransDocument document,
			OutwardTransDocLineItem item) {

		if (DATA_ORIGION_TYPE_ERP_AUTO.contains(
				trimAndConvToUpperCase(document.getDataOriginTypeCode()))) {

			if (item.getTaxableValue() == null) {
				item.setTaxableValue(BigDecimal.ZERO);
			}
			if (item.getCessAmountAdvalorem() == null) {
				item.setCessAmountAdvalorem(BigDecimal.ZERO);
			}
			if (item.getCessAmountSpecific() == null) {
				item.setCessAmountSpecific(BigDecimal.ZERO);
			}
			if (item.getCessRateAdvalorem() == null) {
				item.setCessRateAdvalorem(BigDecimal.ZERO);
			}
			if (item.getIgstAmount() == null) {
				item.setIgstAmount(BigDecimal.ZERO);
			}
			if (item.getCgstAmount() == null) {
				item.setCgstAmount(BigDecimal.ZERO);
			}
			if (item.getSgstAmount() == null) {
				item.setSgstAmount(BigDecimal.ZERO);
			}
			if (item.getIgstRate() == null) {
				item.setIgstRate(BigDecimal.ZERO);
			}
			if (item.getCgstRate() == null) {
				item.setCgstRate(BigDecimal.ZERO);
			}
			if (item.getSgstRate() == null) {
				item.setSgstRate(BigDecimal.ZERO);
			}
			if (item.getItemDiscount() == null) {
				item.setItemDiscount(BigDecimal.ZERO);
			}
			if (item.getPreTaxAmount() == null) {
				item.setPreTaxAmount(BigDecimal.ZERO);
			}
			if (item.getStateCessSpecificRate() == null) {
				item.setStateCessSpecificRate(BigDecimal.ZERO);
			}
			if (item.getStateCessSpecificAmt() == null) {
				item.setStateCessSpecificAmt(BigDecimal.ZERO);
			}
			if (item.getStateCessAmount() == null) {
				item.setStateCessAmount(BigDecimal.ZERO);
			}
			if (item.getItemAmount() == null) {
				item.setItemAmount(BigDecimal.ZERO);
			}
			if (item.getTotalItemAmount() == null) {
				item.setTotalItemAmount(BigDecimal.ZERO);
			}
			if (item.getUnitPrice() == null) {
				item.setUnitPrice(BigDecimal.ZERO);
			}
		}
	}

	private void convertToUpperCaseAndSetDocValues(
			OutwardTransDocument document) {
		String supplyType = outwardSupplyTypeResolver.resolve(document);
		document.setSupplyType(supplyType.toUpperCase());
		document.setDocType(document.getDocType().toUpperCase());
		document.setDocNo(document.getDocNo().toUpperCase());
		if (document.getAccountingVoucherNumber() != null
				&& !document.getAccountingVoucherNumber().trim().isEmpty()) {
			document.setAccountingVoucherNumber(
					document.getAccountingVoucherNumber().toUpperCase());
		}
		if (document.getOrigDocType() != null
				&& !document.getOrigDocType().trim().isEmpty()) {
			document.setOrigDocType(document.getOrigDocType().toUpperCase());
		}
		if (document.getSgstin() != null
				&& !document.getSgstin().trim().isEmpty()) {
			document.setSgstin(document.getSgstin().toUpperCase());
		}
		if (document.getCgstin() != null
				&& !document.getCgstin().trim().isEmpty()) {
			document.setCgstin(document.getCgstin().toUpperCase());
		}
		if (document.getOrigCgstin() != null
				&& !document.getOrigCgstin().trim().isEmpty()) {
			document.setOrigCgstin(document.getOrigCgstin().toUpperCase());
		}
		if (document.getEgstin() != null
				&& !document.getEgstin().trim().isEmpty()) {
			document.setEgstin(document.getEgstin().toUpperCase());
		}
		if (document.getCrDrPreGst() == null) {
			document.setCrDrPreGst(GSTConstants.N);
		}
		if (document.getTcsFlag() == null) {
			document.setTcsFlag(GSTConstants.N);
		}
		if (document.getDiffPercent() == null) {
			document.setDiffPercent(GSTConstants.N);
		}
		if (!REVERSE_CHARGE.contains(document.getReverseCharge())) {
			document.setReverseCharge(GSTConstants.N);
		}
		if (document.getTransactionType() == null
				|| document.getTransactionType().isEmpty()) {
			document.setTransactionType(GSTConstants.O);
		}
		document.setDerivedTaxperiod(
				GenUtil.convertTaxPeriodToInt(document.getTaxperiod()));
		/*
		 * String origDocNo = document.getLineItems().get(0).getOrigDocNo();
		 * document.setOrigDocNo(origDocNo);// Set Original Doc Num LocalDate
		 * origDocDate = document.getLineItems().get(0).getOrigDocDate();
		 * document.setOrigDocDate(origDocDate);// Set Original Doc Date
		 */
		// setting preceedingNo,preceeding instead origDocDate, origDocNo
		String preInvNum = document.getLineItems().get(0)
				.getPreceedingInvoiceNumber();
		document.setPreceedingInvoiceNumber(preInvNum);
		LocalDate preDocDate = document.getLineItems().get(0)
				.getPreceedingInvoiceDate();
		document.setPreceedingInvoiceDate(preDocDate);

		// Set Derived SGSTIN PAN
		if (document.getSgstin() != null && !document.getSgstin().isEmpty()
				&& document.getSgstin().length() == 15) {
			document.setDerivedSgstinPan(document.getSgstin().substring(2, 12));
		}
		// Set Derived CGSTIN PAN
		if (document.getCgstin() != null && !document.getCgstin().isEmpty()
				&& document.getCgstin().length() == 15) {
			if (document.getCgstin() != null
					&& !document.getCgstin().trim().isEmpty()) {
				document.setDerivedCgstinPan(
						document.getCgstin().substring(2, 12));
			}
		}
		// Set Profit Center,Plant Code,Location to Header
		// Take the values of Smallest Line Item Number, and set it to header
		int smallestLineItemNo = Integer.MAX_VALUE;
		Integer index = null;
		if (document.getLineItems() != null) {
			for (OutwardTransDocLineItem lineItem : document.getLineItems()) {
				int currentLineItemNo = 0;
				if (lineItem.getLineNo() != null) {
					currentLineItemNo = Integer.parseInt(lineItem.getLineNo());
				}

				if (currentLineItemNo < smallestLineItemNo) {
					smallestLineItemNo = currentLineItemNo;
					index = document.getLineItems().indexOf(lineItem);
				}
				lineItem.setSupplyType(
						!Strings.isNullOrEmpty(lineItem.getSupplyType())
								? lineItem.getSupplyType().toUpperCase()
								: null);
			}
		}

		if (index != null) {
			OutwardTransDocLineItem outwardTransDocLineItem = document
					.getLineItems().get(index);
			// String profitCentre = outwardTransDocLineItem.getProfitCentre();
			String plantCode = outwardTransDocLineItem.getPlantCode();
			String profitCenter3 = outwardTransDocLineItem.getUserAccess1();
			String profitCenter4 = outwardTransDocLineItem.getUserAccess2();
			String profitCenter5 = outwardTransDocLineItem.getUserAccess3();
			String profitCenter6 = outwardTransDocLineItem.getUserAccess4();
			String profitCenter7 = outwardTransDocLineItem.getUserAccess5();
			String profitCenter8 = outwardTransDocLineItem.getUserAccess6();
			// String location = outwardTransDocLineItem.getLocation();
			// document.setProfitCentre(profitCentre);
			document.setPlantCode(plantCode);
			document.setUserAccess1(profitCenter3);
			document.setUserAccess2(profitCenter4);
			document.setUserAccess3(profitCenter5);
			document.setUserAccess4(profitCenter6);
			document.setUserAccess5(profitCenter7);
			document.setUserAccess6(profitCenter8);
			// document.setLocation(location);
		}
	}

	private void setAbsoluteValuesHeaderLevel(OutwardTransDocument document) {
		String docType = document.getDocType();
		String supplyType = document.getSupplyType();
		final List<String> docTypes = ImmutableList.of("CR", "RCR", "RFV",
				"RRFV", "AV", "RAV");
		final List<String> supplyTypes = ImmutableList.of("CAN");
		if (docTypes.contains(docType) || supplyTypes.contains(supplyType)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Setting Absolutes Values for Doc Type header level"
								+ docType + " Supply Type " + supplyType);

				if (document.getInvoiceOtherCharges() != null) {
					document.setInvoiceOtherCharges(
							document.getInvoiceOtherCharges().abs());
				}
				if (document.getInvoiceAssessableAmount() != null) {
					document.setInvoiceAssessableAmount(
							document.getInvoiceAssessableAmount().abs());
				}
				if (document.getInvoiceIgstAmount() != null) {
					document.setInvoiceIgstAmount(
							document.getInvoiceIgstAmount().abs());
				}
				if (document.getInvoiceCgstAmount() != null) {
					document.setInvoiceCgstAmount(
							document.getInvoiceCgstAmount().abs());
				}
				if (document.getInvoiceSgstAmount() != null) {
					document.setInvoiceSgstAmount(
							document.getInvoiceSgstAmount().abs());
				}
				if (document.getInvoiceCessAdvaloremAmount() != null) {
					document.setInvoiceCessAdvaloremAmount(
							document.getInvoiceCessAdvaloremAmount().abs());
				}
				if (document.getInvoiceCessSpecificAmount() != null) {
					document.setInvoiceCessSpecificAmount(
							document.getInvoiceCessSpecificAmount().abs());
				}
				if (document.getInvoiceStateCessAmount() != null) {
					document.setInvoiceStateCessAmount(
							document.getInvoiceStateCessAmount().abs());
				}
				if (document.getInvStateCessSpecificAmt() != null) {
					document.setInvStateCessSpecificAmt(
							document.getInvStateCessSpecificAmt().abs());
				}
			}
		}
	}

	private void setAbsoluteValuesItemLevel(OutwardTransDocument document,
			OutwardTransDocLineItem item) {
		String docType = document.getDocType();
		String supplyType = document.getSupplyType();
		final List<String> docTypes = ImmutableList.of("CR", "RCR", "RFV",
				"RRFV", "AV", "RAV");
		final List<String> supplyTypes = ImmutableList.of("CAN");
		if (docTypes.contains(docType) || supplyTypes.contains(supplyType)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Setting Absolutes Values for Doc Type item level "
						+ docType + " Supply Type " + supplyType);
			}
			if (item.getTaxableValue() != null) {
				item.setTaxableValue(item.getTaxableValue().abs());
			}
			if (item.getIgstAmount() != null) {
				item.setIgstAmount(item.getIgstAmount().abs());
			}
			if (item.getCgstAmount() != null) {
				item.setCgstAmount(item.getCgstAmount().abs());
			}
			if (item.getSgstAmount() != null) {
				item.setSgstAmount(item.getSgstAmount().abs());
			}
			if (item.getCessAmountAdvalorem() != null) {
				item.setCessAmountAdvalorem(
						item.getCessAmountAdvalorem().abs());
			}
			if (item.getCessAmountSpecific() != null) {
				item.setCessAmountSpecific(item.getCessAmountSpecific().abs());
			}
			if (item.getStateCessAmount() != null) {
				item.setStateCessAmount(item.getStateCessAmount().abs());
			}
			if (item.getOtherValues() != null) {
				item.setOtherValues(item.getOtherValues().abs());
			}
			if (item.getLineItemAmt() != null) {
				item.setLineItemAmt(item.getLineItemAmt().abs());
			}
			if (item.getAdjustedTaxableValue() != null) {
				item.setAdjustedTaxableValue(
						item.getAdjustedTaxableValue().abs());
			}
			if (item.getAdjustedIgstAmt() != null) {
				item.setAdjustedIgstAmt(item.getAdjustedIgstAmt().abs());
			}
			if (item.getAdjustedCgstAmt() != null) {
				item.setAdjustedCgstAmt(item.getAdjustedCgstAmt().abs());
			}
			if (item.getAdjustedSgstAmt() != null) {
				item.setAdjustedSgstAmt(item.getAdjustedSgstAmt().abs());
			}
			if (item.getAdjustedCessAmtAdvalorem() != null) {
				item.setAdjustedCessAmtAdvalorem(
						item.getAdjustedCessAmtAdvalorem().abs());
			}
			if (item.getAdjustedCessAmtSpecific() != null) {
				item.setAdjustedCessAmtSpecific(
						item.getAdjustedCessAmtSpecific().abs());
			}
			if (item.getAdjustedStateCessAmt() != null) {
				item.setAdjustedStateCessAmt(
						item.getAdjustedStateCessAmt().abs());
			}
			if (item.getStateCessSpecificAmt() != null) {
				item.setStateCessSpecificAmt(
						item.getStateCessSpecificAmt().abs());
			}
			if (item.getTotalItemAmount() != null) {
				item.setTotalItemAmount(item.getTotalItemAmount().abs());
			}
			if (item.getInvoiceAssessableAmount() != null) {
				item.setInvoiceAssessableAmount(
						item.getInvoiceAssessableAmount().abs());
			}
			if (item.getPaidAmount() != null) {
				item.setPaidAmount(item.getPaidAmount().abs());
			}
			if (item.getBalanceAmount() != null) {
				item.setBalanceAmount(item.getBalanceAmount().abs());
			}
			if (item.getInvoiceOtherCharges() != null) {
				item.setInvoiceOtherCharges(
						item.getInvoiceOtherCharges().abs());
			}
			if (item.getInvoiceAssessableAmount() != null) {
				item.setInvoiceAssessableAmount(
						item.getInvoiceAssessableAmount().abs());
			}
			if (item.getInvoiceIgstAmount() != null) {
				item.setInvoiceIgstAmount(item.getInvoiceIgstAmount().abs());
			}
			if (item.getInvoiceCgstAmount() != null) {
				item.setInvoiceCgstAmount(item.getInvoiceCgstAmount().abs());
			}
			if (item.getInvoiceSgstAmount() != null) {
				item.setInvoiceSgstAmount(item.getInvoiceSgstAmount().abs());
			}
			if (item.getInvoiceCessAdvaloremAmount() != null) {
				item.setInvoiceCessAdvaloremAmount(
						item.getInvoiceCessAdvaloremAmount().abs());
			}
			if (item.getInvoiceCessSpecificAmount() != null) {
				item.setInvoiceCessSpecificAmount(
						item.getInvoiceCessSpecificAmount().abs());
			}
			if (item.getInvoiceStateCessAmount() != null) {
				item.setInvoiceStateCessAmount(
						item.getInvoiceStateCessAmount().abs());
			}
			if (item.getInvStateCessSpecificAmt() != null) {
				item.setInvStateCessSpecificAmt(
						item.getInvStateCessSpecificAmt().abs());
			}
		}
	}

	private void setFormReturnType(OutwardTransDocument document,
			Map<String, Long> gstinAndEntityMap,
			Map<String, String> questionAnsMap) {
		String gstin = document.getSgstin();
		if (GSTConstants.I.equalsIgnoreCase(document.getTransactionType())) {
			if (document.getCgstin() != null && !document.getCgstin().isEmpty()
					&& document.getCgstin().length() == 15) {
				gstin = document.getCgstin();
			}
		}
		Long entityId = gstinAndEntityMap.get(gstin);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("setFormReturnType method gstin " + gstin
					+ " Entity Id " + entityId);
		}
		String entityTaxPeriod = questionAnsMap.get(entityId);
		String docTaxPeriod = document.getTaxperiod();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("setFormReturnType method entityTaxPeriod "
					+ entityTaxPeriod + " docTaxPeriod " + docTaxPeriod);
		}
		document.setFormReturnType(AspDocumentUtil.getFormReturnType(entityId,
				docTaxPeriod, questionAnsMap, TransDocTypes.OUTWARD.getType()));
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("FormReturnType for entityTaxPeriod " + entityTaxPeriod
					+ " docTaxPeriod " + docTaxPeriod + " is set to document");
		}
	}

	private void calculateAndSetDocAmount(OutwardTransDocument document,
			OutwardTransDocLineItem item) {
		// BigDecimal docAmt = document.getDocAmount();
		BigDecimal otherValues = document.getOtherValues();
		BigDecimal stateCessAmt = document.getStateCessAmount();
		BigDecimal stateCessSpecificAmt = document.getStateCessSpecificAmt();

		/*
		 * if (docAmt == null) { docAmt = BigDecimal.ZERO; }
		 */
		if (otherValues == null) {
			otherValues = BigDecimal.ZERO;
		}
		if (stateCessAmt == null) {
			stateCessAmt = BigDecimal.ZERO;
		}
		if (stateCessSpecificAmt == null) {
			stateCessSpecificAmt = BigDecimal.ZERO;
		}
		/*
		 * if (item.getLineItemAmt() != null) { docAmt =
		 * docAmt.add(item.getLineItemAmt()); document.setDocAmount(docAmt); //
		 * set Doc Amount }
		 */
		if (item.getOtherValues() != null) {
			otherValues = otherValues.add(item.getOtherValues());
			document.setOtherValues(otherValues); // set Other Values
		}
		if (item.getStateCessAmount() != null) {
			stateCessAmt = stateCessAmt.add(item.getStateCessAmount());
			document.setStateCessAmount(stateCessAmt);// set State Cess Amt
		}
		if (item.getStateCessSpecificAmt() != null) {
			stateCessSpecificAmt = stateCessSpecificAmt
					.add(item.getStateCessSpecificAmt());
			document.setStateCessSpecificAmt(stateCessSpecificAmt);
			;// set State Cess Specific Amt
		}
	}

	private void calculateAndSetTotalTaxAmt(OutwardTransDocument document,
			OutwardTransDocLineItem item) {
		BigDecimal docIgstAmt = document.getIgstAmount();
		BigDecimal docCgstAmt = document.getCgstAmount();
		BigDecimal docSgstAmt = document.getSgstAmount();
		BigDecimal docCessAmtSpec = document.getCessAmountSpecific();
		BigDecimal docCessAmtAdv = document.getCessAmountAdvalorem();
		if (docIgstAmt == null) {
			docIgstAmt = BigDecimal.ZERO;
		}
		if (docCgstAmt == null) {
			docCgstAmt = BigDecimal.ZERO;
		}
		if (docSgstAmt == null) {
			docSgstAmt = BigDecimal.ZERO;
		}
		if (docCessAmtSpec == null) {
			docCessAmtSpec = BigDecimal.ZERO;
		}
		if (docCessAmtAdv == null) {
			docCessAmtAdv = BigDecimal.ZERO;
		}
		// Set Igst Amount to Document Header
		if (item.getIgstAmount() != null) {
			docIgstAmt = docIgstAmt.add(item.getIgstAmount());
			document.setIgstAmount(docIgstAmt);
		}
		// Set Cgst Amount to Document Header
		if (item.getCgstAmount() != null) {
			docCgstAmt = docCgstAmt.add(item.getCgstAmount());
			document.setCgstAmount(docCgstAmt);
		}
		// Set Sgst Amount to Document Header
		if (item.getSgstAmount() != null) {
			docSgstAmt = docSgstAmt.add(item.getSgstAmount());
			document.setSgstAmount(docSgstAmt);
		}
		// Set Cess Amt Specific to Document Header
		if (item.getCessAmountSpecific() != null) {
			docCessAmtSpec = docCessAmtSpec.add(item.getCessAmountSpecific());
			document.setCessAmountSpecific(docCessAmtSpec);
		}
		// Set Cess Amt Advalorem to Document Header
		if (item.getCessAmountAdvalorem() != null) {
			docCessAmtAdv = docCessAmtAdv.add(item.getCessAmountAdvalorem());
			document.setCessAmountAdvalorem(docCessAmtAdv);
		}
	}

	private void calculateAndSetItemTaxRate(OutwardTransDocument document,
			OutwardTransDocLineItem item) {
		// Calculate Tax Rate
		BigDecimal igstRate = item.getIgstRate();
		BigDecimal cgstRate = item.getCgstRate();
		BigDecimal sgstRate = item.getSgstRate();
		if (igstRate == null) {
			igstRate = BigDecimal.ZERO;
		}
		if (cgstRate == null) {
			cgstRate = BigDecimal.ZERO;
		}
		if (sgstRate == null) {
			sgstRate = BigDecimal.ZERO;
		}
		BigDecimal totalTaxRate = igstRate.add(cgstRate).add(sgstRate);

		if (item.getIgstRate() == null && item.getCgstRate() == null
				&& item.getSgstRate() == null) {
			totalTaxRate = null;
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Tax Rate " + totalTaxRate + " for Doc Key "
					+ document.getDocKey() + " for Item " + item);
		}
		item.setTaxRate(totalTaxRate);// Set Tax Rate
	}

	private void calculateAndSetTaxPayble(OutwardTransDocument document,
			OutwardTransDocLineItem item) {
		BigDecimal taxPayable = document.getTaxPayable();
		BigDecimal igstAmount = item.getIgstAmount();
		BigDecimal cgstAmount = item.getCgstAmount();
		BigDecimal sgstAmount = item.getSgstAmount();
		BigDecimal cessAmountSpecific = item.getCessAmountSpecific();
		BigDecimal cessAmountAdvalorem = item.getCessAmountAdvalorem();
		if (taxPayable == null) {
			taxPayable = BigDecimal.ZERO;
		}
		if (igstAmount == null) {
			igstAmount = BigDecimal.ZERO;
		}
		if (cgstAmount == null) {
			cgstAmount = BigDecimal.ZERO;
		}
		if (sgstAmount == null) {
			sgstAmount = BigDecimal.ZERO;
		}
		if (cessAmountSpecific == null) {
			cessAmountSpecific = BigDecimal.ZERO;
		}
		if (cessAmountAdvalorem == null) {
			cessAmountAdvalorem = BigDecimal.ZERO;
		}
		BigDecimal itemTaxPayable = igstAmount.add(cgstAmount).add(sgstAmount)
				.add(cessAmountSpecific).add(cessAmountAdvalorem);
		item.setTaxPayable(itemTaxPayable);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Tax Payable " + itemTaxPayable + "for Document "
					+ document + "for Item " + item);
		}
		taxPayable = taxPayable.add(itemTaxPayable);// Set TaxPayable to ITEM
		document.setTaxPayable(taxPayable);// Set Total TaxPayable to Doc Header
	}

	private void calculateAndSetTaxableValue(OutwardTransDocument document,
			OutwardTransDocLineItem item) {
		BigDecimal taxableVal = document.getTaxableValue();
		if (taxableVal == null) {
			taxableVal = BigDecimal.ZERO;
		}
		// Set Taxable Value to Document Header
		if (item.getTaxableValue() != null) {
			taxableVal = taxableVal.add(item.getTaxableValue());
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Taxable Value  " + taxableVal + " for Doc Key "
					+ document.getDocKey());
		}
		document.setTaxableValue(taxableVal);
	}

	private void calculateInvAmounts(OutwardTransDocument document,
			OutwardTransDocLineItem item) {

		if (GSTConstants.DataOriginTypeCodes.ERP_API.getDataOriginTypeCode()
				.equalsIgnoreCase(document.getDataOriginTypeCode())
				|| GSTConstants.DataOriginTypeCodes.ERP_API_INV_MANAGMENT_CORR
						.getDataOriginTypeCode()
						.equalsIgnoreCase(document.getDataOriginTypeCode())) {

			item.setInvoiceOtherCharges(document.getInvoiceOtherCharges());
			item.setInvoiceAssessableAmount(
					document.getInvoiceAssessableAmount());
			item.setInvoiceIgstAmount(document.getInvoiceIgstAmount());
			item.setInvoiceCgstAmount(document.getInvoiceCgstAmount());
			item.setInvoiceSgstAmount(document.getInvoiceSgstAmount());
			item.setInvoiceCessAdvaloremAmount(
					document.getInvoiceCessAdvaloremAmount());
			item.setInvoiceCessSpecificAmount(
					document.getInvoiceCessSpecificAmount());
			item.setInvoiceStateCessAmount(
					document.getInvoiceStateCessAmount());
			item.setInvStateCessSpecificAmt(
					document.getInvStateCessSpecificAmt());
		}
		if (GSTConstants.DataOriginTypeCodes.EXCL_UPLOAD.getDataOriginTypeCode()
				.equalsIgnoreCase(document.getDataOriginTypeCode())
				|| GSTConstants.DataOriginTypeCodes.EXCL_UPLOAD_INV_MANAGMENT_CORR
						.getDataOriginTypeCode()
						.equalsIgnoreCase(document.getDataOriginTypeCode())) {

			BigDecimal invOtherCharges = document.getInvoiceOtherCharges();
			BigDecimal invAssbleAmt = document.getInvoiceAssessableAmount();
			BigDecimal invIgstAmt = document.getInvoiceIgstAmount();
			BigDecimal invCgst = document.getInvoiceCgstAmount();
			BigDecimal invSgst = document.getInvoiceSgstAmount();
			BigDecimal invCessAdvAmt = document.getInvoiceCessAdvaloremAmount();
			BigDecimal invCessSpecificAmt = document
					.getInvoiceCessSpecificAmount();
			BigDecimal invStateCessAmt = document.getInvoiceStateCessAmount();
			BigDecimal invStateCessSpecificAmt = document
					.getInvStateCessSpecificAmt();

			if (invOtherCharges == null) {
				invOtherCharges = BigDecimal.ZERO;
			}
			if (invAssbleAmt == null) {
				invAssbleAmt = BigDecimal.ZERO;
			}
			if (invIgstAmt == null) {
				invIgstAmt = BigDecimal.ZERO;
			}
			if (invCgst == null) {
				invCgst = BigDecimal.ZERO;
			}
			if (invSgst == null) {
				invSgst = BigDecimal.ZERO;
			}
			if (invCessAdvAmt == null) {
				invCessAdvAmt = BigDecimal.ZERO;
			}
			if (invCessSpecificAmt == null) {
				invCessSpecificAmt = BigDecimal.ZERO;
			}
			if (invStateCessAmt == null) {
				invStateCessAmt = BigDecimal.ZERO;
			}
			if (invStateCessSpecificAmt == null) {
				invStateCessSpecificAmt = BigDecimal.ZERO;
			}
			if (item.getInvoiceOtherCharges() != null) {
				invOtherCharges = document.getLineItems().get(0)
						.getInvoiceOtherCharges();
				document.setInvoiceOtherCharges(invOtherCharges);
			}
			if (item.getInvoiceAssessableAmount() != null) {
				invAssbleAmt = document.getLineItems().get(0)
						.getInvoiceAssessableAmount();
				document.setInvoiceAssessableAmount(invAssbleAmt);
			}
			if (item.getInvoiceIgstAmount() != null) {
				invIgstAmt = document.getLineItems().get(0)
						.getInvoiceIgstAmount();
				document.setInvoiceIgstAmount(invIgstAmt);
			}
			if (item.getInvoiceCgstAmount() != null) {
				invCgst = document.getLineItems().get(0).getInvoiceCgstAmount();
				document.setInvoiceCgstAmount(invCgst);
			}
			if (item.getInvoiceSgstAmount() != null) {
				invSgst = document.getLineItems().get(0).getInvoiceSgstAmount();
				document.setInvoiceSgstAmount(invSgst);
			}

			if (item.getInvoiceCessAdvaloremAmount() != null) {
				invCessAdvAmt = document.getLineItems().get(0)
						.getInvoiceCessAdvaloremAmount();
				document.setInvoiceCessAdvaloremAmount(invCessAdvAmt);
			}

			if (item.getInvoiceCessSpecificAmount() != null) {
				invCessSpecificAmt = document.getLineItems().get(0)
						.getInvoiceCessSpecificAmount();
				document.setInvoiceCessSpecificAmount(invCessSpecificAmt);
			}
			if (item.getInvoiceStateCessAmount() != null) {
				invStateCessAmt = document.getLineItems().get(0)
						.getInvoiceStateCessAmount();
				document.setInvoiceStateCessAmount(invStateCessAmt);
			}
			if (item.getInvStateCessSpecificAmt() != null) {
				invStateCessSpecificAmt = document.getLineItems().get(0)
						.getInvStateCessSpecificAmt();
				document.setInvStateCessSpecificAmt(invStateCessSpecificAmt);
			}

			/*
			 * if (item.getInvoiceOtherCharges() != null) { invOtherCharges =
			 * invOtherCharges .add(item.getInvoiceOtherCharges());
			 * document.setInvoiceOtherCharges(invOtherCharges); } if
			 * (item.getInvoiceAssessableAmount() != null) { invAssbleAmt =
			 * invAssbleAmt .add(item.getInvoiceAssessableAmount());
			 * document.setInvoiceAssessableAmount(invAssbleAmt); } if
			 * (item.getInvoiceIgstAmount() != null) { invIgstAmt =
			 * invIgstAmt.add(item.getInvoiceIgstAmount());
			 * document.setInvoiceIgstAmount(invIgstAmt); } if
			 * (item.getInvoiceCgstAmount() != null) { invCgst =
			 * invCgst.add(item.getInvoiceCgstAmount());
			 * document.setInvoiceCgstAmount(invCgst); } if
			 * (item.getInvoiceSgstAmount() != null) { invSgst =
			 * invSgst.add(item.getInvoiceSgstAmount());
			 * document.setInvoiceSgstAmount(invSgst); }
			 * 
			 * if (item.getInvoiceCessAdvaloremAmount() != null) { invCessAdvAmt
			 * = invCessAdvAmt .add(item.getInvoiceCessAdvaloremAmount());
			 * document.setInvoiceCessAdvaloremAmount(invCessAdvAmt); }
			 * 
			 * if (item.getInvoiceCessSpecificAmount() != null) {
			 * invCessSpecificAmt = invCessSpecificAmt
			 * .add(item.getInvoiceCessSpecificAmount());
			 * document.setInvoiceCessSpecificAmount(invCessSpecificAmt); } if
			 * (item.getInvoiceStateCessAmount() != null) { invStateCessAmt =
			 * invStateCessAmt .add(item.getInvoiceStateCessAmount());
			 * document.setInvoiceStateCessAmount(invStateCessAmt); } if
			 * (item.getInvStateCessSpecificAmt() != null) {
			 * invStateCessSpecificAmt = invStateCessSpecificAmt
			 * .add(item.getInvStateCessSpecificAmt());
			 * document.setInvStateCessSpecificAmt(invStateCessSpecificAmt); }
			 */ }
	}

	// This method is the fix for Defect Error code "ER0037"
	private void setHeaderValuesToItem(OutwardTransDocument document,
			OutwardTransDocLineItem item) {
		item.setUserId(document.getUserId());
		item.setSourceFileName(document.getSourceFileName());
		item.setDivision(document.getDivision());
		item.setSalesOrgnization(document.getSalesOrgnization());
		item.setDistributionChannel(document.getDistributionChannel());
		/*
		 * item.setUserAccess1(document.getUserAccess1());
		 * item.setUserAccess2(document.getUserAccess2());
		 * item.setUserAccess3(document.getUserAccess3());
		 * item.setUserAccess4(document.getUserAccess4());
		 * item.setUserAccess5(document.getUserAccess5());
		 * item.setUserAccess6(document.getUserAccess6());
		 */
		item.setTaxperiod(document.getTaxperiod());
		item.setDerivedTaxperiod(document.getDerivedTaxperiod());
		item.setDocDate(document.getDocDate());
		item.setOrigDocType(document.getOrigDocType());
		item.setCrDrPreGst(document.getCrDrPreGst());
		item.setCgstin(document.getCgstin());
		item.setCustOrSuppType(document.getCustOrSuppType());
		item.setDiffPercent(document.getDiffPercent());
		item.setOrigCgstin(document.getOrigCgstin());
		item.setCustOrSuppName(document.getCustOrSuppName());
		item.setCustOrSuppCode(document.getCustOrSuppCode());
		item.setCustOrSuppAddress1(document.getCustOrSuppAddress1());
		item.setCustOrSuppAddress2(document.getCustOrSuppAddress2());
		// item.setCustOrSuppAddress3(document.getCustOrSuppAddress3());
		item.setCustOrSuppAddress4(document.getCustOrSuppAddress4());
		item.setBillToState(document.getBillToState());
		item.setShipToState(document.getShipToState());
		item.setPos(document.getPos());
		item.setStateApplyingCess(document.getStateApplyingCess());
		item.setPortCode(document.getPortCode());
		
		if (!Strings.isNullOrEmpty(document.getShippingBillNo())) {

			String str = document.getShippingBillNo().toString().trim();
		    if (str.length() > 7) {
		        str = str.substring(0, 7);
		    }
			
		item.setShippingBillNo(str);
		}
		
		//item.setShippingBillNo(document.getShippingBillNo());
		item.setShippingBillDate(document.getShippingBillDate());
		item.setSection7OfIgstFlag(document.getSection7OfIgstFlag());
		item.setReverseCharge(document.getReverseCharge());
		item.setTcsFlag(document.getTcsFlag());
		item.setEgstin(document.getEgstin());
		item.setClaimRefundFlag(document.getClaimRefundFlag());
		item.setAutoPopToRefundFlag(document.getAutoPopToRefundFlag());
		item.setAccountingVoucherNumber(document.getAccountingVoucherNumber());
		item.setAccountingVoucherDate(document.getAccountingVoucherDate());
		item.seteWayBillNo(document.geteWayBillNo());
		item.seteWayBillDate(document.geteWayBillDate());
		item.setCancellationReason(document.getCancellationReason());
		item.setDocCategory(document.getDocCategory());
		item.setSupplierLegalName(document.getSupplierLegalName());
		item.setSupplierBuildingNumber(document.getSupplierBuildingNumber());
		item.setSupplierLocation(document.getSupplierLocation());
		item.setSupplierPincode(document.getSupplierPincode());
		item.setSupplierStateCode(document.getSupplierStateCode());
		item.setDispatcherTradeName(document.getDispatcherTradeName());
		item.setDispatcherBuildingNumber(
				document.getDispatcherBuildingNumber());
		item.setDispatcherLocation(document.getDispatcherLocation());
		item.setDispatcherPincode(document.getDispatcherPincode());
		item.setDispatcherStateCode(document.getDispatcherStateCode());
		item.setShipToLegalName(document.getShipToLegalName());
		item.setShipToBuildingNumber(document.getShipToBuildingNumber());
		item.setShipToLocation(document.getShipToLocation());
		item.setShipToPincode(document.getShipToPincode());
		item.setForeignCurrency(document.getForeignCurrency());
		item.setCountryCode(document.getCountryCode());
		item.setInvoiceValueFc(document.getInvoiceValueFc());
		item.setInvoicePeriodStartDate(document.getInvoicePeriodStartDate());
		item.setInvoicePeriodEndDate(document.getInvoicePeriodEndDate());
		item.setTransactionType(document.getTransactionType());
		item.setSubSupplyType(document.getSubSupplyType());
		item.setOtherSupplyTypeDescription(
				document.getOtherSupplyTypeDescription());
		item.setTransporterID(document.getTransporterID());
		item.setTransporterName(document.getTransporterName());
		item.setTransportMode(document.getTransportMode());
		item.setTransportDocNo(document.getTransportDocNo());
		item.setTransportDocDate(document.getTransportDocDate());
		item.setDistance(document.getDistance());
		item.setVehicleNo(document.getVehicleNo());
		item.setVehicleType(document.getVehicleType());
		item.setProfitCentre(document.getProfitCentre());
		item.setLocation(document.getLocation());
		item.setSupplierBuildingName(document.getSupplierBuildingName());
		item.setDispatcherBuildingName(document.getDispatcherBuildingName());
		item.setShipToBuildingName(document.getShipToBuildingName());
		item.setSupplierEmail(document.getSupplierEmail());
		item.setCustomerEmail(document.getCustomerEmail());

		LOGGER.debug("DataType OriginType {} ",
				document.getDataOriginTypeCode());
		if (document.getDataOriginTypeCode().equalsIgnoreCase(
				GSTConstants.DataOriginTypeCodes.SFTP.getDataOriginTypeCode())
				|| document.getDataOriginTypeCode().equalsIgnoreCase(
						GSTConstants.DataOriginTypeCodes.EXCL_UPLOAD
								.getDataOriginTypeCode())) {
			if (item.getUserDefinedField28() != null) {
				document.setUserDefinedField28(
						document.getLineItems().get(0).getUserDefinedField28());
			}
		} else {
			if (document.getUserDefinedField28() != null) {
				item.setUserDefinedField28(document.getUserDefinedField28());
			} else {
				document.setUserDefinedField28(
						document.getLineItems().get(0).getUserDefinedField28());
			}
		}
		if (!Strings.isNullOrEmpty(document.getUserDefinedField29())) {
			item.setUserDefinedField29(document.getUserDefinedField29());
		} else {
			document.setUserDefinedField29(
					document.getLineItems().get(0).getUserDefinedField29());
		}
		if (!Strings.isNullOrEmpty(document.getUserDefinedField30())) {
			item.setUserDefinedField30(document.getUserDefinedField30());
		} else {
			document.setUserDefinedField30(
					document.getLineItems().get(0).getUserDefinedField30());
		}

		item.setProfitCentre2(document.getProfitCentre2());
		item.setGlCodeIgst(document.getGlCodeIgst());
		item.setGlCodeCgst(document.getGlCodeCgst());
		item.setGlCodeSgst(document.getGlCodeSgst());
		item.setGlCodeAdvCess(document.getGlCodeAdvCess());
		item.setGlCodeSpCess(document.getGlCodeSpCess());
		item.setGlCodeStateCess(document.getGlCodeStateCess());
		item.setGlStateCessSpecific(document.getGlStateCessSpecific());
	}

	public void setHeaderPrecInvAmtTaxableValue(
			List<OutwardTransDocument> documents) {

		Set<String> docKeySet = new HashSet<>();
		List<OutwardTransDocument> orgDocDetails = new ArrayList<>();

		if (LOGGER.isDebugEnabled()) {
			for (OutwardTransDocument doc : documents) {
				if (doc.getAspInvoiceStatus() == null) {
					LOGGER.debug("ApInvoiceStatus as null for dockey{}",
							doc.getDocKey());
				} else {
					LOGGER.debug("ApInvoiceStatus{} for dockey{}",
							doc.getAspInvoiceStatus(), doc.getDocKey());
				}
			}
		}
		List<OutwardTransDocument> crDrDocs = documents.stream()
				.filter(doc -> DOC_TYPE.contains(doc.getDocType())
						&& (doc.getAspInvoiceStatus() != null)
						&& (AspInvoiceStatus.ASP_PROCESSED
								.getAspInvoiceStatusCode() == doc
										.getAspInvoiceStatus()))
				.collect(Collectors.toList());

		crDrDocs.forEach(doc -> docKeySet.add(docKeyGen.generateOrgKey(doc)));
		List<String> docKeys = new ArrayList<>(docKeySet);

		LOGGER.debug("uploaded DocKey {} ", docKeys);

		if (LOGGER.isDebugEnabled()) {

			LOGGER.debug(
					"The number of dockeys recieved from the iteration is : "
							+ docKeys.size());
		}
		Config config = configManager.getConfig("EYInternal",
				"outward.save.chunksize");
		String chnkSizeStr = config != null ? config.getValue() : "2000";
		int chunkSize = Integer.parseInt(chnkSizeStr);

		List<List<String>> docKeyChunks = Lists.partition(docKeys, chunkSize);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Generated %d Chunks of DocKeys of each Size is %d in org doc check",
					docKeyChunks.size(), chunkSize);
			LOGGER.debug(msg);
		}
		if (!docKeyChunks.isEmpty()) {
			docKeyChunks.forEach(chunk -> orgDocDetails.addAll(
					docRepository.findActiveOrgDocumentByDocKeys(chunk)));
		}

		LOGGER.debug("orgDocDetails {} ", orgDocDetails);
		Map<String, OutwardTransDocument> orgDocDetailsMap = orgDocDetails
				.stream().collect(Collectors.toMap(
						OutwardTransDocument::getDocKey, Function.identity()));
		LOGGER.debug("orgDocDetails  map {} ", orgDocDetailsMap);

		crDrDocs.forEach(doc -> {
			List<OutwardTransDocLineItem> lineItem = doc.getLineItems();
			String orgDocKey = docKeyGen.generateOrgKey(doc);
			LOGGER.debug("orgDocKey {}", orgDocKey);
			if (orgDocDetailsMap.containsKey(orgDocKey)) {
				OutwardTransDocument obj = orgDocDetailsMap.get(orgDocKey);
				BigDecimal docAmt = obj.getDocAmount();
				BigDecimal taxableVal = obj.getTaxableValue();
				List<OutwardTransDocLineItem> originalLineitem = obj
						.getLineItems();
				setItemPrecInvAmtTaxableValue(lineItem, originalLineitem);
				doc.setPreceedingInvAmt(docAmt);
				doc.setPreceedingTaxableVal(taxableVal);

			}
		});
	}

	public void setItemPrecInvAmtTaxableValue(
			List<OutwardTransDocLineItem> lineItem,
			List<OutwardTransDocLineItem> originalLineitem) {
		BigDecimal lineItemAmt = BigDecimal.ZERO;
		BigDecimal taxbleValue = BigDecimal.ZERO;

		for (OutwardTransDocLineItem item : originalLineitem) {
			if (item.getLineItemAmt() != null) {
				lineItemAmt = lineItemAmt.add(item.getLineItemAmt());
			}
			if (item.getTaxableValue() != null) {
				taxbleValue = taxbleValue.add(item.getTaxableValue());
			}
		}
		lineItem.get(0).setPreceedingInvAmt(lineItemAmt);
		lineItem.get(0).setPreceedingTaxableVal(taxbleValue);
	}

	private void setValuesToPreceeding(OutwardTransDocument document,
			PreceedingDocDetails preDetls) {

		preDetls.setSgstin(document.getSgstin());
		preDetls.setTaxperiod(document.getTaxperiod());
		preDetls.setDerivedTaxperiod(document.getDerivedTaxperiod());
		preDetls.setDocType(document.getDocType());
		preDetls.setDocNo(document.getDocNo());
		preDetls.setDocDate(document.getDocDate());
		preDetls.setSupplyType(document.getSupplyType());
	}

	private void setValuesToContr(OutwardTransDocument document,
			ContractDetails contrDetails) {

		contrDetails.setSgstin(document.getSgstin());
		contrDetails.setTaxperiod(document.getTaxperiod());
		contrDetails.setDerivedTaxperiod(document.getDerivedTaxperiod());
		contrDetails.setDocType(document.getDocType());
		contrDetails.setDocNo(document.getDocNo());
		contrDetails.setDocDate(document.getDocDate());
		contrDetails.setSupplyType(document.getSupplyType());
	}

	private void setValuestoAddlDetails(OutwardTransDocument document,
			AdditionalDocDetails addlDetails) {

		addlDetails.setSgstin(document.getSgstin());
		addlDetails.setTaxperiod(document.getTaxperiod());
		addlDetails.setDerivedTaxperiod(document.getDerivedTaxperiod());
		addlDetails.setDocType(document.getDocType());
		addlDetails.setDocNo(document.getDocNo());
		addlDetails.setDocDate(document.getDocDate());
		addlDetails.setSupplyType(document.getSupplyType());
	}

	private void setValuestoAttDetails(OutwardTransDocument document,
			OutwardTransDocLineItem item, AttributeDetails attDetails) {
		attDetails.setSgstin(document.getSgstin());
		attDetails.setTaxperiod(document.getTaxperiod());
		attDetails.setDerivedTaxperiod(document.getDerivedTaxperiod());
		attDetails.setDocType(document.getDocType());
		attDetails.setDocNo(document.getDocNo());
		attDetails.setDocDate(document.getDocDate());
		attDetails.setSupplyType(item.getSupplyType());
		Integer lineNo = 0;
		if (item.getLineNo() != null) {
			lineNo = Integer.valueOf(item.getLineNo());
		}
		attDetails.setLineNo(lineNo);
	}

	private void setInvRefValToItem(OutwardTransDocument document,
			OutwardTransDocLineItem item, PreceedingDocDetails preFirstRec) {
		item.setInvoiceReference(preFirstRec.getInvoiceReference());
	}

	private void setContractDetailsToItem(OutwardTransDocLineItem item,
			ContractDetails contractDetFirstRec) {

		item.setReceiptAdviceReference(
				contractDetFirstRec.getReceiptAdviceReference());
		item.setReceiptAdviceDate(contractDetFirstRec.getReceiptAdviceDate());
		item.setTenderReference(contractDetFirstRec.getTenderReference());
		item.setContractReference(contractDetFirstRec.getContractReference());
		item.setExternalReference(contractDetFirstRec.getExternalReference());
		item.setProjectReference(contractDetFirstRec.getProjectReference());
		item.setCustomerPOReferenceNumber(
				contractDetFirstRec.getCustomerPOReferenceNumber());
		item.setCustomerPOReferenceDate(
				contractDetFirstRec.getCustomerPOReferenceDate());
	}

	private void setAdditionalDetailsToItem(OutwardTransDocLineItem item,
			AdditionalDocDetails additionalDetFirstRec) {
		item.setSupportingDocURL(additionalDetFirstRec.getSupportingDocURL());
		item.setSupportingDocBase64(
				additionalDetFirstRec.getSupportingDocBase64());
		item.setAdditionalInformation(
				additionalDetFirstRec.getAdditionalInformation());
	}

	private void setAttrinutesToItem(OutwardTransDocLineItem item,
			AttributeDetails attDetFirstRecord) {
		item.setAttributeName(attDetFirstRecord.getAttributeName());
		item.setAttributeValue(attDetFirstRecord.getAttributeValue());
	}

	private Pair<Integer, Integer> getPreference(OutwardTransDocument document,
			Map<String, Pair<Integer, Integer>> preferenceMap) {
		String gstin = "";

		if (GSTConstants.I.equalsIgnoreCase(document.getTransactionType())) {
			gstin = (document.getCgstin() != null) ? document.getCgstin().trim()
					: "";
		} else {
			gstin = (document.getSgstin() != null) ? document.getSgstin().trim()
					: "";
		}

		String plant = (document.getPlantCode() != null)
				? document.getPlantCode().trim() : "";

		String entityIdAndPlantAndGstin = new StringJoiner(DOC_KEY_JOINER)
				.add(document.getEntityId().toString()).add(gstin).add(plant)
				.toString();

		String entityIdAndGstin = new StringJoiner(DOC_KEY_JOINER)
				.add(document.getEntityId().toString()).add(gstin).toString();

		if (preferenceMap.containsKey(entityIdAndPlantAndGstin)) {
			return preferenceMap.get(entityIdAndPlantAndGstin);
		}

		if (preferenceMap.containsKey(entityIdAndGstin)) {
			return preferenceMap.get(entityIdAndGstin);
		}

		if (preferenceMap.containsKey(document.getEntityId())) {
			return preferenceMap.get(document.getEntityId());
		}

		Pair<Integer, Integer> pair = null;
		SourceInfoEntity Status = sourceInfoRepository
				.findByEntityIdAndPlantAndGstinAndIsDeleteFalse(
						document.getEntityId(), document.getPlantCode(), gstin);
		if (Status != null) {
			pair = new Pair<>(Status.getEInvJob(), Status.getEwbJob());
			preferenceMap.put(entityIdAndPlantAndGstin, pair);
			return pair;
		}
		if (Status == null) {
			Status = sourceInfoRepository
					.findByEntityIdAndGstinAndPlantIsNullAndIsDeleteFalse(
							document.getEntityId(), gstin);

			if (Status != null) {
				pair = new Pair<>(Status.getEInvJob(), Status.getEwbJob());
				preferenceMap.put(entityIdAndGstin, pair);
				return pair;
			}
		}
		if (Status == null) {
			Status = sourceInfoRepository
					.findByEntityIdAndGstinIsNullAndPlantIsNullAndIsDeleteFalse(
							document.getEntityId());
			if (Status != null) {
				pair = new Pair<>(Status.getEInvJob(), Status.getEwbJob());

				preferenceMap.put(document.getEntityId().toString(), pair);
				return pair;
			}
		}
		preferenceMap.put(entityIdAndPlantAndGstin, pair);
		return null;
	}

	private void UQCConversion(List<OutwardTransDocument> documents) {
		Config config = configManager.getConfig("GSTNAPI",
				"api.gstn.id(CONSIDER_NA_UQC).from_period");
		Integer cutOverPeriod = null;
		if (config != null && config.getValue() != null) {
			cutOverPeriod = GenUtil.convertTaxPeriodToInt(config.getValue());
		}
		for (OutwardTransDocument doc : documents) {
			Integer derivedTaxPeriod = GenUtil
					.convertTaxPeriodToInt(doc.getTaxperiod());
			boolean naConsideredAsUqcValueInHsn = cutOverPeriod <= derivedTaxPeriod
					? Boolean.TRUE : Boolean.FALSE;
			List<OutwardTransDocLineItem> items = doc.getLineItems();
			IntStream.range(0, items.size()).forEach(idx -> {
				OutwardTransDocLineItem item = items.get(idx);
				String hsn = item.getHsnSac();
				if (Strings.isNullOrEmpty(item.getItemUqcUser())) {
					item.setItemUqcUser(item.getUom());
				}
				if (item.getItemQtyUser() == null) {
					item.setItemQtyUser(item.getQty());
				}
				if (DATA_ORIGION_TYPE_CORRECTION_API.contains(
						trimAndConvToUpperCase(doc.getDataOriginTypeCode()))) {

					if (!Strings.isNullOrEmpty(item.getItemUqcUser())) {
						item.setUom(item.getItemUqcUser());
					}
					if (item.getItemQtyUser() != null) {
						item.setQty(item.getItemQtyUser());
					}
					if (item.getItemQtyUser() == null) {
						item.setItemQtyUser(BigDecimal.ZERO);
					}
					if ((!Strings.isNullOrEmpty(hsn))
							&& (!GSTConstants.SERVICES_CODE
									.equalsIgnoreCase(hsn.substring(0, 2)))) {

						if ((Strings.isNullOrEmpty(item.getItemUqcUser()))
								|| (GSTConstants.NA.equalsIgnoreCase(
										item.getItemUqcUser()))) {
							item.setUom(GSTConstants.OTH);
							item.setItemUqcUser(GSTConstants.OTH);
						}
					}
					if ((!Strings.isNullOrEmpty(hsn))
							&& (GSTConstants.SERVICES_CODE
									.equalsIgnoreCase(hsn.substring(0, 2)))) {

						item.setQty(BigDecimal.ZERO);

						if ((Strings.isNullOrEmpty(item.getItemUqcUser()))
								|| (GSTConstants.NA.equalsIgnoreCase(
										item.getItemUqcUser()))) {
							item.setUom(GSTConstants.NA);
							item.setItemUqcUser(GSTConstants.NA);
						}
					}
				}

				if (naConsideredAsUqcValueInHsn) {

					if ((!Strings.isNullOrEmpty(hsn))
							&& (GSTConstants.SERVICES_CODE
									.equalsIgnoreCase(hsn.substring(0, 2)))) {

						if (!Strings.isNullOrEmpty(item.getUom())) {
							item.setUom(GSTConstants.NA);
						}
						item.setQty(BigDecimal.ZERO);

						if (!Strings.isNullOrEmpty(doc.getUqcNotFoundInMaster())
								&& GSTConstants.A.equalsIgnoreCase(
										doc.getUqcNotFoundInMaster())) {
							if (Strings.isNullOrEmpty(item.getUom())) {
								item.setUom(null);
								item.setItemUqcUser(null);
							}
						}

					}
				}
			});
		}
	}
}
