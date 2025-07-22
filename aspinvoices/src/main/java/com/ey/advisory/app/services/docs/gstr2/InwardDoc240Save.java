package com.ey.advisory.app.services.docs.gstr2;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.admin.data.repositories.client.GroupInfoDetailsRepository;
import com.ey.advisory.admin.services.onboarding.EntityAtConfigKey;
import com.ey.advisory.admin.services.onboarding.MasterItemService;
import com.ey.advisory.admin.services.onboarding.MasterVendorService;
import com.ey.advisory.admin.services.onboarding.OrganizationService;
import com.ey.advisory.app.data.entities.client.InwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.data.repositories.client.gstr2.InwardTransDocRepository;
import com.ey.advisory.app.services.common.FileStatusPerfUtil;
import com.ey.advisory.app.services.docs.DocKeyGenerator;
import com.ey.advisory.app.services.docs.DuplicateDocCheckService;
import com.ey.advisory.app.services.gen.ClientGroupService;
import com.ey.advisory.app.util.AspDocumentConstants;
import com.ey.advisory.app.util.AspDocumentConstants.TransDocTypes;
import com.ey.advisory.app.util.AspDocumentUtil;
import com.ey.advisory.app.util.OnboardingConfigParamsCheck;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
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
@Service("InwardDoc240Save")
public class InwardDoc240Save {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(InwardDoc240Save.class);

	@Autowired
	@Qualifier("InwardTransDocRepository")
	private InwardTransDocRepository docHeaderRepository;

	@Autowired
	@Qualifier("DefaultInwardDuplicateDocCheckServiceImpl")
	private DuplicateDocCheckService duplicateDocService;

	@Autowired
	@Qualifier("DefaultInwardSupplyTypeResolver")
	InwardSupplyTypeResolver inwardSupplyTypeResolver;

	@Autowired
	@Qualifier("InwardField240Truncation")
	private InwardField240Truncation inwardField240Truncation;

	@Autowired
	@Qualifier("DefaultClientGroupService")
	private ClientGroupService clientGroupService;

	@Autowired
	@Qualifier("OnboardingConfigParamsCheck")
	private OnboardingConfigParamsCheck onboardingConfigParamCheck;

	@Autowired
	@Qualifier("InwardConfigParams")
	private InwardConfigParams inwardConfigParams;

	@Autowired
	@Qualifier("EligibleAndIneligibleAmountsCalculation")
	private EligibleAndIneligibleAmountsCalculation eligibleAndIneligibleAmountsCalculation;

	@Autowired
	@Qualifier("OrganizationServiceImpl")
	private OrganizationService orgSvc;

	@Autowired
	@Qualifier("groupInfoDetailsRepository")
	private GroupInfoDetailsRepository groupInfoDetailsRepository;

	@Autowired
	@Qualifier("masterVendorService")
	private MasterVendorService masterVendorSvc;

	@Autowired
	@Qualifier("masterItemService")
	private MasterItemService masterItemSvc;

	@Autowired
	private DocKeyGenerator<InwardTransDocument, String> docKeyGen;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	private static final List<String> DOC_TYPE = ImmutableList.of(
			GSTConstants.CR, GSTConstants.DR, GSTConstants.RCR,
			GSTConstants.RDR,GSTConstants.RNV);

	/**
	 * This method is responsible for converting all gstins,supply type, doc
	 * type to upper case and setting Supply Type to Header from Items based on
	 * business rule - BR_4 B: TAX with NIL / EXT / NON: TAX C: DTA with NIL /
	 * EXT / NON: DTA D: DXP with NIL / EXT / NON: DXP E: NIL / EXT / NON: NIL
	 * F: EXT / NON: EXT
	 * 
	 * @param documents
	 */
	public void convertCalcConfigAndSetValues(
			List<InwardTransDocument> documents, String sourceId,
			String headerPayloadId) {
		List<Long> entityIds = clientGroupService
				.findEntityDetailsForGroupCode();
		Map<String, Long> gstinAndEntityMap = clientGroupService
				.getGstinAndEntityMapForGroupCode(entityIds);
		Map<Long, List<EntityConfigPrmtEntity>> map = onboardingConfigParamCheck
				.getEntityAndConfParamMap();
		Map<EntityAtConfigKey, Map<Long, String>> entityAtConfigMap = orgSvc
				.getEntityAtConfMap(
						AspDocumentConstants.TransDocTypes.INWARD.getType());
		Map<Long, List<Pair<String, String>>> entityAtValMap = orgSvc
				.getAllEntityAtValMap();
		String groupCode = TenantContext.getTenantId();
		Long groupId = groupInfoDetailsRepository.findByGroupId(groupCode);
		List<String> allMasterVendorGstins = masterVendorSvc
				.getAllMasterVendorGstins();
		Map<Long, Map<String, List<Pair<Integer, BigDecimal>>>> allMasterItms = masterItemSvc
				.getAllMasterItems();

		documents.forEach(document -> {

			String cgstin = document.getCgstin();
			Long entityId = gstinAndEntityMap.get(cgstin);
			document.setGroupId(groupId);
			document.setEntityId(entityId);
			Map<String, String> questionAnsMap = onboardingConfigParamCheck
					.getQuestionAndAnswerMap(entityId, map);
			// Convert Doc Type Supply Type and all GSTINs to upper case
			convertToUpperCaseAndSetDocValues(document);
			setDerivedSourceIdAndPayloadId(document, sourceId, headerPayloadId);
			setDataOriginTypeCode(document);
			set240formatFlag(document);
			if (null != document.getLineItems()) {
				document.getLineItems().forEach(item -> {
					setHeaderValuesToItem(document, item);
					setAbsoluteValues(document, item);
					// calculateAndSetLineItemAmount(item);
					calculateAndSetTaxPayble(document, item);
					calculateAndSetTaxableValue(document, item);
					calculateAndSetItemTaxRate(document, item);
					calculateAndSetTotalTaxAmt(document, item);
					calculateInvAmounts(document, item);
					calculateAndSetDocAmount(document, item);
					computeAndSetAvailableTaxAmts(document, item);
					eligibleAndIneligibleAmountsCalculation
							.configParamForEligibleAndIneligibleItemAmts(item,
									questionAnsMap);
					setEligibleIneligibleHeaderAmts(document, item);
					calculateElInElAvailableTaxPayble(document, item);
					setOrigDocNoDocDateToHeader(document, item);
				});
			}
			// Onboarding Configurable Parameters
			document.setEntityConfigParamMap(map);
			document.setEntityAtConfMap(entityAtConfigMap);
			document.setEntityAtValMap(entityAtValMap);

			setFormReturnType(document, gstinAndEntityMap, questionAnsMap);
			// Check If Supplier GSTIN in Master Vendor Table
			boolean isSgstInMasterVendorTable = false;
			if (allMasterVendorGstins != null
					&& !allMasterVendorGstins.isEmpty()) {
				if (document.getSgstin() != null
						&& !document.getSgstin().trim().isEmpty()) {
					isSgstInMasterVendorTable = allMasterVendorGstins.stream()
							.anyMatch(document.getSgstin()::equalsIgnoreCase);
				}
			}
			document.setIsSgstInMasterVendor(isSgstInMasterVendorTable);
			// Get GSTIN Details for Entity Id from Master Item Table
			Map<String, List<Pair<Integer, BigDecimal>>> masterItemMap = allMasterItms
					.get(entityId);
			document.setMasterItemMap(masterItemMap);
			inwardConfigParams.configurableParameters(document, questionAnsMap);
		});
	}

	private void set240formatFlag(InwardTransDocument document) {
		document.setIs240Format(true);
	}

	private void setOrigDocNoDocDateToHeader(InwardTransDocument document,
			InwardTransDocLineItem item) {

		if (!Strings.isNullOrEmpty(item.getOrigDocNo())) {
			String origDocNo = document.getLineItems().get(0).getOrigDocNo();
			document.setOrigDocNo(origDocNo);// Set Original Doc Num
		}
		if (item.getOrigDocDate() != null) {
			LocalDate origDocDate = document.getLineItems().get(0)
					.getOrigDocDate();
			document.setOrigDocDate(origDocDate);// Set Original Doc Date
		}
	}

	

	private void setFormReturnType(InwardTransDocument document,
			Map<String, Long> gstinAndEntityMap,
			Map<String, String> questionAnsMap) {
		String gstin = document.getSgstin();
		Long entityId = gstinAndEntityMap.get(gstin);
		String docTaxPeriod = document.getTaxperiod();
		document.setFormReturnType(AspDocumentUtil.getFormReturnType(entityId,
				docTaxPeriod, questionAnsMap, TransDocTypes.INWARD.getType()));
		
	}

	/**
	 * This method is responsible for saving Inward Documents
	 * 
	 * @param docs
	 * @return
	 */
	public List<InwardTransDocument> saveDocs(List<InwardTransDocument> docs,
			DocKeyGenerator<InwardTransDocument, String> docKeyGen) {

		/**
		 * Start - Document Error Correction Below code is for updating the
		 * existing document The below code is executed in case of document
		 * error correction
		 * 
		 */
		// Get all the non-null ids from the list. This will be used to
		// mark the existing documents in the DB as deleted.

		FileStatusPerfUtil.logEvent("SAVE_DOC_SOFT_DELETE_BEGIN");
		List<Long> docIds = docs.stream().filter(doc -> doc.getId() != null && !doc.isDeleted() )
				.map(doc -> doc.getId()).collect(Collectors.toList());
		InwardTransDocument firstInwardDoc = docs.get(0);

		// Execute the repository query to udpate the isDelete to true for the
		// above ids. Also mark the updated date to the current date.
		// call the is delete to true repository method.
		if (!docIds.isEmpty()) {
			LocalDateTime updatedDate = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			docHeaderRepository.updateDocDeletion(docIds, updatedDate,
					firstInwardDoc.getCreatedBy(),
					"Doc number or Doc date changed from Invoice Management");
			docs.forEach(doc -> {
				doc.setGstnError(false);
				doc.setSaved(false);
				doc.setSent(false);
			});
		}
		FileStatusPerfUtil.logEvent("SAVE_DOC_SOFT_DELETE_END");
		// SAVE_DOC_SOFT_DELETE_END

		/**
		 * Start - Duplicate Document Check
		 */
		// SAVE_DOC_DUP_CHECK_
		FileStatusPerfUtil.logEvent("SAVE_DOC_DUP_CHECK_BEGIN");
		/*
		 * DuplicateInwardDocCheckDto duplicateDocCheckDto = duplicateDocService
		 * .checkInwardDuplicateDocuments(docs, docKeyGen, docHeaderRepository);
		 * 
		 * if (!duplicateDocCheckDto.getDocs().isEmpty()) { LocalDateTime
		 * updatedDate = LocalDateTime.now(); List<InwardTransDocument>
		 * duplicateDocs = duplicateDocCheckDto .getDocs(); List<Long>
		 * duplicateDocIds = new ArrayList<>(); if (!duplicateDocs.isEmpty()) {
		 * duplicateDocs.forEach(duplicateDoc -> {
		 * duplicateDocIds.add(duplicateDoc.getId()); }); if
		 * (!duplicateDocIds.isEmpty()) {
		 * docHeaderRepository.updateDocDeletion(duplicateDocIds, updatedDate,
		 * firstInwardDoc.getCreatedBy()); } } }
		 * 
		 * if (!duplicateDocCheckDto.getGstnSubmittedDocs().isEmpty()) { //
		 * TO-DO }
		 */
		List<InwardTransDocument> activeDocList = docs.stream()
				.filter(doc -> !doc.isDeleted()).collect(Collectors.toList());
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Active Doc List " +
					activeDocList);
		}
		if (!activeDocList.isEmpty()) {
			duplicateDocService.softDeleteDuplicateDocsInward(activeDocList);
		}

		FileStatusPerfUtil.logEvent("SAVE_DOC_DUP_CHECK_END");
		// SAVE_DOC_DUP_CHECK_

		/**
		 * End - Duplicate Document Check
		 */

		// explictly set the documeent ids to null in the input collection
		// to be saved.
		docs.forEach(doc -> doc.setId(null));

		/*
		 * docs.forEach(doc -> { doc.setId(null);
		 * doc.getLineItems().forEach(item -> item.setId(null)); });
		 */
		/**
		 * End - Document Error Correction
		 */
		// Setting preceeding values for Gstr6
		setPreceedingValuesHeaderAndItem(docs);

		// SAVE_DOC_SET_DATES
		FileStatusPerfUtil.logEvent("SAVE_DOC_SET_DATES_BEGIN");
		docs.forEach(document -> {
			String docKey = docKeyGen.generateKey(document);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Doc Key " + docKey + "for Doc No "
						+ document.getDocNo());
			}
			inwardField240Truncation.truncateHeaderFields(document);
			document.setDocKey(docKey);
			LocalDateTime createdDate = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			// document.setCreatedDate(LocalDateTime.now());
			document.setCreatedDate(createdDate);
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
			if (null != document.getLineItems()) {
				document.getLineItems().forEach(item -> {
					// Truncate Line Item Fields
					inwardField240Truncation.truncateLineItemFields(item);
					// set Document to Item
					item.setDocument(document);
				});
			}

			// Set Document Saving Time for Performance Testing
			// LocalDateTime beforeSavingTime = LocalDateTime.now();
			LocalDateTime beforeSavingTime = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			document.setBeforeSavingOn(beforeSavingTime);
		});
		// SAVE_DOC_SET_DATES
		FileStatusPerfUtil.logEvent("SAVE_DOC_SET_DATES_END");
		// save all the documents.

		// SAVE_DOC_SAVE_DB
		FileStatusPerfUtil.logEvent("SAVE_DOC_SAVE_DB_BEGING");
		List<InwardTransDocument> inDocs = docHeaderRepository.saveAll(docs);
		// SAVE_DOC_SAVE_DB
		FileStatusPerfUtil.logEvent("SAVE_DOC_SAVE_DB_END");

		return inDocs;
	}

	private void setEligibleIneligibleHeaderAmts(InwardTransDocument document,
			InwardTransDocLineItem item) {

		BigDecimal eligibleIgst = document.getEligibleIgst();
		BigDecimal eligibleCgst = document.getEligibleCgst();
		BigDecimal eligibleSgst = document.getEligibleSgst();
		BigDecimal eligibleCess = document.getEligibleCess();
		BigDecimal inEligibleIgst = document.getInEligibleIgst();
		BigDecimal inEligibleCgst = document.getInEligibleCgst();
		BigDecimal inEligibleSgst = document.getInEligibleSgst();
		BigDecimal inEligibleCess = document.getInEligibleCess();

		if (eligibleIgst == null) {
			eligibleIgst = BigDecimal.ZERO;
		}
		if (eligibleCgst == null) {
			eligibleCgst = BigDecimal.ZERO;
		}
		if (eligibleSgst == null) {
			eligibleSgst = BigDecimal.ZERO;
		}
		if (eligibleCess == null) {
			eligibleCess = BigDecimal.ZERO;
		}
		if (inEligibleIgst == null) {
			inEligibleIgst = BigDecimal.ZERO;
		}
		if (inEligibleCgst == null) {
			inEligibleCgst = BigDecimal.ZERO;
		}
		if (inEligibleSgst == null) {
			inEligibleSgst = BigDecimal.ZERO;
		}
		if (inEligibleCess == null) {
			inEligibleCess = BigDecimal.ZERO;
		}

		if (item.getEligibleIgst() != null) {
			eligibleIgst = eligibleIgst.add(item.getEligibleIgst());
			document.setEligibleIgst(eligibleIgst);
		}
		if (item.getEligibleCgst() != null) {
			eligibleCgst = eligibleCgst.add(item.getEligibleCgst());
			document.setEligibleCgst(eligibleCgst);
		}
		if (item.getEligibleSgst() != null) {
			eligibleSgst = eligibleSgst.add(item.getEligibleSgst());
			document.setEligibleSgst(eligibleSgst);
		}
		if (item.getEligibleCess() != null) {
			eligibleCess = eligibleCess.add(item.getEligibleCess());
			document.setEligibleCess(eligibleCess);
		}
		if (item.getInEligibleIgst() != null) {
			inEligibleIgst = inEligibleIgst.add(item.getInEligibleIgst());
			document.setInEligibleIgst(inEligibleIgst);
		}
		if (item.getInEligibleCgst() != null) {
			inEligibleCgst = inEligibleCgst.add(item.getInEligibleCgst());
			document.setInEligibleCgst(inEligibleCgst);
		}

		if (item.getInEligibleSgst() != null) {
			inEligibleSgst = inEligibleSgst.add(item.getInEligibleSgst());
			document.setInEligibleSgst(inEligibleSgst);
		}
		if (item.getInEligibleCess() != null) {
			inEligibleCess = inEligibleCess.add(item.getInEligibleCess());
			document.setInEligibleCess(inEligibleCess);
		}
	}

	private void convertToUpperCaseAndSetDocValues(
			InwardTransDocument document) {
		String supplyType = inwardSupplyTypeResolver.resolve(document);
		// Convert Doc Type, Supply Type and all GSTINs to upper case
		if (supplyType != null && !supplyType.trim().isEmpty()) {
			document.setSupplyType(supplyType.toUpperCase());
		}
		if (document.getDocType() != null
				&& !document.getDocType().trim().isEmpty()) {
			document.setDocType(document.getDocType().toUpperCase());
		}
		if (document.getDocNo() != null
				&& !document.getDocNo().trim().isEmpty()) {
			document.setDocNo(document.getDocNo().trim().toUpperCase());
		}
		if (document.getPurchaseVoucherNum() != null
				&& !document.getPurchaseVoucherNum().trim().isEmpty()) {
			document.setPurchaseVoucherNum(
					document.getPurchaseVoucherNum().toUpperCase());
		}
		if (document.getCgstin() != null
				&& !document.getCgstin().trim().isEmpty()) {
			document.setCgstin(document.getCgstin().toUpperCase());
		}
		if (document.getSgstin() != null
				&& !document.getSgstin().trim().isEmpty()) {
			document.setSgstin(document.getSgstin().toUpperCase());
		}
		if (document.getOrigSgstin() != null
				&& !document.getOrigSgstin().trim().isEmpty()) {
			document.setOrigSgstin(document.getOrigSgstin().toUpperCase());
		}
		if (document.getDiffPercent() == null) {
			document.setDiffPercent(GSTConstants.N);
		}
		if (!GSTConstants.Y.equalsIgnoreCase(document.getReverseCharge())) {
			document.setReverseCharge(GSTConstants.N);
		}
		document.setDerivedTaxperiod(
				GenUtil.convertTaxPeriodToInt(document.getTaxperiod()));

		// Set Profit Centers,Plant Code,Location to Header
		// Take the values of Smallest Line Item Number, and set it to header
		int smallestLineItemNo = Integer.MAX_VALUE;
		Integer index = null;
		if (document.getLineItems() != null) {
			for (InwardTransDocLineItem lineItem : document.getLineItems()) {
				int currentLineItemNo = 0;
				if (lineItem.getLineNo() != null) {
					currentLineItemNo = lineItem.getLineNo();
				}
				if (currentLineItemNo < smallestLineItemNo) {
					smallestLineItemNo = currentLineItemNo;
					index = document.getLineItems().indexOf(lineItem);
				}
			}
		}

		if (index != null) {
			InwardTransDocLineItem inwardTransDocLineItem = document
					.getLineItems().get(index);
			String profitCentre = inwardTransDocLineItem.getProfitCentre();
			String plantCode = inwardTransDocLineItem.getPlantCode();
			String profitCenter3 = inwardTransDocLineItem.getUserAccess1();
			String profitCenter4 = inwardTransDocLineItem.getUserAccess2();
			String profitCenter5 = inwardTransDocLineItem.getUserAccess3();
			String profitCenter6 = inwardTransDocLineItem.getUserAccess4();
			String profitCenter7 = inwardTransDocLineItem.getUserAccess5();
			String profitCenter8 = inwardTransDocLineItem.getUserAccess6();
			String location = inwardTransDocLineItem.getLocation();
			document.setProfitCentre(profitCentre);
			document.setPlantCode(plantCode);
			document.setUserAccess1(profitCenter3);
			document.setUserAccess2(profitCenter4);
			document.setUserAccess3(profitCenter5);
			document.setUserAccess4(profitCenter6);
			document.setUserAccess5(profitCenter7);
			document.setUserAccess6(profitCenter8);
			document.setLocation(location);
		}
	}

	private void calculateAndSetTotalTaxAmt(InwardTransDocument document,
			InwardTransDocLineItem item) {
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
		if (item.getIgstAmount() != null) {
			docIgstAmt = docIgstAmt.add(item.getIgstAmount());
			document.setIgstAmount(docIgstAmt);
		}
		if (item.getCgstAmount() != null) {
			docCgstAmt = docCgstAmt.add(item.getCgstAmount());
			document.setCgstAmount(docCgstAmt);
		}
		if (item.getSgstAmount() != null) {
			docSgstAmt = docSgstAmt.add(item.getSgstAmount());
			document.setSgstAmount(docSgstAmt);
		}
		if (item.getCessAmountSpecific() != null) {
			docCessAmtSpec = docCessAmtSpec.add(item.getCessAmountSpecific());
			document.setCessAmountSpecific(docCessAmtSpec);
		}
		if (item.getCessAmountAdvalorem() != null) {
			docCessAmtAdv = docCessAmtAdv.add(item.getCessAmountAdvalorem());
			document.setCessAmountAdvalorem(docCessAmtAdv);
		}
	}

	private void calculateAndSetItemTaxRate(InwardTransDocument document,
			InwardTransDocLineItem item) {
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
		
		item.setTaxRate(totalTaxRate);
	}

	private void calculateAndSetTaxableValue(InwardTransDocument document,
			InwardTransDocLineItem item) {
		BigDecimal taxableVal = document.getTaxableValue();
		if (taxableVal == null) {
			taxableVal = BigDecimal.ZERO;
		}
		if (item.getTaxableValue() != null) {
			taxableVal = taxableVal.add(item.getTaxableValue());
			document.setTaxableValue(taxableVal);
		}
		
	}

	private void calculateAndSetTaxPayble(InwardTransDocument document,
			InwardTransDocLineItem item) {
		// Calculate TaxPayable
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
		
		// Set TaxPayable to ITEM
		taxPayable = taxPayable.add(itemTaxPayable);
		// Set Total TaxPayable to Document Header
		document.setTaxPayable(taxPayable);
	}

	private void calculateAndSetDocAmount(InwardTransDocument document,
			InwardTransDocLineItem item) {
		// BigDecimal docAmt = document.getDocAmount();
		BigDecimal otherValues = document.getOtherValues();
		BigDecimal stateCessAmt = document.getStateCessAmount();

		/*
		 * if (docAmt == null) { docAmt = BigDecimal.ZERO; }
		 */
		if (otherValues == null) {
			otherValues = BigDecimal.ZERO;
		}
		if (stateCessAmt == null) {
			stateCessAmt = BigDecimal.ZERO;
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
	}

	private void computeAndSetAvailableTaxAmts(InwardTransDocument document,
			InwardTransDocLineItem item) {
		BigDecimal availableIgst = document.getAvailableIgst();
		BigDecimal availableCgst = document.getAvailableCgst();
		BigDecimal availableSgst = document.getAvailableSgst();
		BigDecimal availableCess = document.getAvailableCess();
		if (availableIgst == null) {
			availableIgst = BigDecimal.ZERO;
		}
		if (availableCgst == null) {
			availableCgst = BigDecimal.ZERO;
		}
		if (availableSgst == null) {
			availableSgst = BigDecimal.ZERO;
		}
		if (availableCess == null) {
			availableCess = BigDecimal.ZERO;
		}

		if (item.getAvailableIgst() != null) {
			availableIgst = availableIgst.add(item.getAvailableIgst());
			document.setAvailableIgst(availableIgst);
		}
		if (item.getAvailableCgst() != null) {
			availableCgst = availableCgst.add(item.getAvailableCgst());
			document.setAvailableCgst(availableCgst);
		}
		if (item.getAvailableSgst() != null) {
			availableSgst = availableSgst.add(item.getAvailableSgst());
			document.setAvailableSgst(availableSgst);
		}
		if (item.getAvailableCess() != null) {
			availableCess = availableCess.add(item.getAvailableCess());
			document.setAvailableCess(availableCess);
		}
	}

	private void calculateElInElAvailableTaxPayble(InwardTransDocument document,
			InwardTransDocLineItem item) {
		// Calculate EligibleTaxPayable
		BigDecimal eligibleTaxPayable = document.getEligibleTaxPayable();
		BigDecimal eligibleIgstAmount = item.getEligibleIgst();
		BigDecimal eligibleCgstAmount = item.getEligibleCgst();
		BigDecimal eligibleSgstAmount = item.getEligibleSgst();
		BigDecimal eligibleCess = item.getEligibleCess();

		if (eligibleTaxPayable == null) {
			eligibleTaxPayable = BigDecimal.ZERO;
		}
		if (eligibleIgstAmount == null) {
			eligibleIgstAmount = BigDecimal.ZERO;
		}
		if (eligibleCgstAmount == null) {
			eligibleCgstAmount = BigDecimal.ZERO;
		}
		if (eligibleSgstAmount == null) {
			eligibleSgstAmount = BigDecimal.ZERO;
		}
		if (eligibleCess == null) {
			eligibleCess = BigDecimal.ZERO;
		}
		BigDecimal itemEligibleTaxPayable = eligibleIgstAmount
				.add(eligibleCgstAmount).add(eligibleSgstAmount)
				.add(eligibleCess);
		item.setEligibleTaxPayable(itemEligibleTaxPayable);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Eligible Tax Payable " + itemEligibleTaxPayable
					+ "for Document " + document + "for Item " + item);
		}
		// Set EligibleTaxPayable to ITEM
		eligibleTaxPayable = eligibleTaxPayable.add(itemEligibleTaxPayable);
		// Set Total EligibleTaxPayable to Document Header
		document.setEligibleTaxPayable(eligibleTaxPayable);

		// Calculate InEligibleTaxPayable
		BigDecimal inEligibleTaxPayable = document.getInEligibleTaxPayable();
		BigDecimal inEligibleIgstAmount = item.getInEligibleIgst();
		BigDecimal inEligibleCgstAmount = item.getInEligibleCgst();
		BigDecimal inEligibleSgstAmount = item.getInEligibleSgst();
		BigDecimal inEligibleCess = item.getInEligibleCess();

		if (inEligibleTaxPayable == null) {
			inEligibleTaxPayable = BigDecimal.ZERO;
		}
		if (inEligibleIgstAmount == null) {
			inEligibleIgstAmount = BigDecimal.ZERO;
		}
		if (inEligibleCgstAmount == null) {
			inEligibleCgstAmount = BigDecimal.ZERO;
		}
		if (inEligibleSgstAmount == null) {
			inEligibleSgstAmount = BigDecimal.ZERO;
		}
		if (inEligibleCess == null) {
			inEligibleCess = BigDecimal.ZERO;
		}
		BigDecimal itemIneligibleTaxPayable = inEligibleIgstAmount
				.add(inEligibleCgstAmount).add(inEligibleSgstAmount)
				.add(inEligibleCess);
		item.setInEligibleTaxPayable(itemIneligibleTaxPayable);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("InEligible Tax Payable " + itemIneligibleTaxPayable
					+ "for Document " + document + "for Item " + item);
		}
		// Set InEligibleTaxPayable to ITEM
		inEligibleTaxPayable = inEligibleTaxPayable
				.add(itemIneligibleTaxPayable);
		// Set Total InEligibleTaxPayable to Document Header
		document.setInEligibleTaxPayable(inEligibleTaxPayable);

		// Calculate avialableTaxPayable
		BigDecimal availableTaxPayable = document.getAvailableTaxPayable();
		BigDecimal availableIgstAmount = item.getAvailableIgst();
		BigDecimal availableCgstAmount = item.getAvailableCgst();
		BigDecimal availableSgstAmount = item.getAvailableSgst();
		BigDecimal availableCess = item.getAvailableCess();

		if (availableTaxPayable == null) {
			availableTaxPayable = BigDecimal.ZERO;
		}
		if (availableIgstAmount == null) {
			availableIgstAmount = BigDecimal.ZERO;
		}
		if (availableCgstAmount == null) {
			availableCgstAmount = BigDecimal.ZERO;
		}
		if (availableSgstAmount == null) {
			availableSgstAmount = BigDecimal.ZERO;
		}
		if (availableCess == null) {
			availableCess = BigDecimal.ZERO;
		}
		BigDecimal itemAvailableTaxPayable = availableIgstAmount
				.add(availableCgstAmount).add(availableSgstAmount)
				.add(availableCess);
		item.setAvailableTaxPayable(itemAvailableTaxPayable);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Available Tax Payable " + itemAvailableTaxPayable
					+ "for Document " + document + "for Item " + item);
		}
		// Set EligibleTaxPayable to ITEM
		availableTaxPayable = availableTaxPayable.add(itemAvailableTaxPayable);
		// Set Total EligibleTaxPayable to Document Header
		document.setAvailableTaxPayable(availableTaxPayable);
	}

	private void setAbsoluteValues(InwardTransDocument document,
			InwardTransDocLineItem item) {
		String docType = document.getDocType();
		String supplyType = document.getSupplyType();
		final List<String> docTypes = ImmutableList.of("CR", "RCR", "RFV",
				"RRFV", "AV", "RAV","ADJ");
		final List<String> supplyTypes = ImmutableList.of("CAN");
		if (docTypes.contains(docType) || supplyTypes.contains(supplyType)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Setting Absolutes Values for Inward Doc Type "
						+ docType);
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
			if (item.getAvailableIgst() != null) {
				item.setAvailableIgst(item.getAvailableIgst().abs());
			}
			if (item.getAvailableCgst() != null) {
				item.setAvailableCgst(item.getAvailableCgst().abs());
			}
			if (item.getAvailableSgst() != null) {
				item.setAvailableSgst(item.getAvailableSgst().abs());
			}
			if (item.getAvailableCess() != null) {
				item.setAvailableCess(item.getAvailableCess().abs());
			}
			if (item.getFreeQuantity() != null) {
				item.setFreeQuantity(item.getFreeQuantity().abs());
			}
			if (item.getTotalItemAmount() != null) {
				item.setTotalItemAmount(item.getTotalItemAmount().abs());
			}
		}
	}

	private void calculateInvAmounts(InwardTransDocument document,
			InwardTransDocLineItem item) {

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
		}
	}

	private void setDerivedSourceIdAndPayloadId(InwardTransDocument document,
			String sourceId, String headerPayloadId) {

		// setting derivedSouceId based on companyCode
		document.setDerivedSourceId(sourceId);

		if (Strings.isNullOrEmpty(document.getPayloadId())) {
			document.setPayloadId(headerPayloadId);
		}
	}

	private void setDataOriginTypeCode(InwardTransDocument document) {
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

	// This method is the fix for Defect Error code "ER0037"
	private void setHeaderValuesToItem(InwardTransDocument document,
			InwardTransDocLineItem item) {

		item.setDivision(document.getDivision());
		item.setPurchaseOrganization(document.getPurchaseOrganization());
		item.setTaxperiod(document.getTaxperiod());
		item.setDerivedTaxperiod(document.getDerivedTaxperiod());
		item.setDocDate(document.getDocDate());
		item.setCustOrSuppType(document.getCustOrSuppType());
		item.setDiffPercent(document.getDiffPercent());
		item.setOrigSgstin(document.getOrigSgstin());
		item.setPos(document.getPos());
		item.setPortCode(document.getPortCode());
		item.setBillOfEntryNo(document.getBillOfEntryNo());
		item.setBillOfEntryDate(document.getBillOfEntryDate());
		item.setSection7OfIgstFlag(document.getSection7OfIgstFlag());
		item.setReverseCharge(document.getReverseCharge());
		item.setItcEntitlement(document.getItcEntitlement());
		item.setPostingDate(document.getPostingDate());
		item.seteWayBillNo(document.geteWayBillNo());
		item.seteWayBillDate(document.geteWayBillDate());
		item.setPurchaseVoucherDate(document.getPurchaseVoucherDate());
		item.setIrn(document.getIrn());
		item.setIrnDate(document.getIrnDate());
		item.setDispatcherGstin(document.getDispatcherGstin());
		item.setShipToGstin(document.getShipToGstin());
		item.setPaymentDueDate(document.getPaymentDueDate());
		item.setEgstin(document.getEgstin());
		item.setTcsFlag(document.getTcsFlag());
		item.setTdsFlag(document.getTdsFlag());
		//item.setSupplierTradeName(document.getLineItems().get(0).getSupplierTradeName());

		if (document.getUserDefinedField28() != null) {
			item.setUserDefinedField28(document.getUserDefinedField28());
		} else {
			document.setUserDefinedField28(
					document.getLineItems().get(0).getUserDefinedField28());
		}
		if (!Strings.isNullOrEmpty(document.getCompanyCode())) {
			item.setCompanyCode(document.getCompanyCode());
		}
		if (!Strings.isNullOrEmpty(document.getCustOrSuppCode())) {
			item.setCustOrSuppCode(document.getCustOrSuppCode());
		}
		//enabled
		if (!Strings.isNullOrEmpty(document.getCustOrSuppName())) {
			item.setCustOrSuppName(document.getCustOrSuppName());
		}
		if (!Strings.isNullOrEmpty(document.getCustOrSuppAddress1())) {
			item.setCustOrSuppAddress1(document.getCustOrSuppAddress1());
		}
		if (!Strings.isNullOrEmpty(document.getCustOrSuppAddress2())) {
			item.setCustOrSuppAddress2(document.getCustOrSuppAddress2());
		}
		if (!Strings.isNullOrEmpty(document.getCustOrSuppAddress3())) {
			item.setCustOrSuppAddress3(document.getCustOrSuppAddress3());
		}
		if (!Strings.isNullOrEmpty(document.getCustOrSuppAddress4())) {
			item.setCustOrSuppAddress4(document.getCustOrSuppAddress4());
		}
		if (!Strings.isNullOrEmpty(document.getClaimRefundFlag())) {
			item.setClaimRefundFlag(document.getClaimRefundFlag());
		}
		if (!Strings.isNullOrEmpty(document.getAutoPopToRefundFlag())) {
			item.setAutoPopToRefundFlag(document.getAutoPopToRefundFlag());
		}
		if (!Strings.isNullOrEmpty(document.getCrDrPreGst())) {
			item.setCrDrPreGst(document.getCrDrPreGst());
		}
		if (!Strings.isNullOrEmpty(document.getStateApplyingCess())) {
			item.setStateApplyingCess(document.getStateApplyingCess());
		}
		if (!Strings.isNullOrEmpty(document.getUserId())) {
			item.setUserId(document.getUserId());
		}
		if (!Strings.isNullOrEmpty(document.getSourceFileName())) {
			item.setSourceFileName(document.getSourceFileName());
		}
		if (!Strings.isNullOrEmpty(document.getPurchaseVoucherNum())) {
			if (document.getDataOriginTypeCode().equalsIgnoreCase("A")) {
				item.setPurchaseVoucherNum(document.getPurchaseVoucherNum());
			}
		}
	}

	public void setPreceedingValuesHeaderAndItem(
			List<InwardTransDocument> documents) {

		Set<String> docKeySet = new HashSet<>();
		List<String> docKeys = new ArrayList<>();
		List<InwardTransDocument> orgDocDetails = new ArrayList<>();

		List<InwardTransDocument> crDrDocs = documents.stream()
				.filter(doc -> DOC_TYPE.contains(doc.getDocType().toUpperCase())
						&& doc.getIsProcessed())
				.collect(Collectors.toList());

		crDrDocs.forEach(doc -> docKeySet.add(docKeyGen.generateOrgKey(doc)));
		docKeys = new ArrayList<>(docKeySet);

		if (LOGGER.isDebugEnabled()) {

			LOGGER.debug(
					"The number of dockeys recieved from the iteration is :{} "
							,docKeys.size());
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
					docHeaderRepository.findActiveOrgDocumentByDocKeys(chunk)));
		}
		crDrDocs.forEach(doc -> {
			String orgDocKey = docKeyGen.generateOrgKey(doc);
			if (!orgDocDetails.isEmpty()) {
				for (InwardTransDocument obj : orgDocDetails) {
					String key = obj.getDocKey();
					if (!orgDocKey.equalsIgnoreCase(key))
						continue;
					BigDecimal docAmt = obj.getDocAmount();
					BigDecimal taxableVal = obj.getTaxableValue();
					BigDecimal totalTaxAmt = obj.getTaxableValue();
					BigDecimal igst = obj.getIgstAmount();
					BigDecimal cgst = obj.getCgstAmount();
					BigDecimal sgst = obj.getSgstAmount();
					BigDecimal cess = obj.getCessAmountAdvalorem();

					doc.setPrecInvoiceValue(docAmt);
					doc.setPrecTaxableValue(taxableVal);
					doc.setPrecTotalTax(totalTaxAmt);
					doc.setPrecIgstAmt(igst);
					doc.setPrecCgstAmt(cgst);
					doc.setPrecSgstAmt(sgst);
					doc.setPrecCessAmt(cess);
				}
			}
		});
	}
}
