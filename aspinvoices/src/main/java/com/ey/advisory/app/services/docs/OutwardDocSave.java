package com.ey.advisory.app.services.docs;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.admin.data.repositories.client.GroupInfoDetailsRepository;
import com.ey.advisory.admin.services.onboarding.EntityAtConfigKey;
import com.ey.advisory.admin.services.onboarding.MasterCustomerService;
import com.ey.advisory.admin.services.onboarding.MasterProductService;
import com.ey.advisory.admin.services.onboarding.OrganizationService;
import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.repositories.client.NilAndHsnProcedureCallRepository;
import com.ey.advisory.app.services.common.FileStatusPerfUtil;
import com.ey.advisory.app.services.gen.ClientGroupService;
import com.ey.advisory.app.util.AspDocumentConstants;
import com.ey.advisory.app.util.AspDocumentConstants.TransDocTypes;
import com.ey.advisory.app.util.AspDocumentUtil;
import com.ey.advisory.app.util.OnboardingConfigParamsCheck;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

/**
 * 
 * @author Mohana.Dasari
 *
 */
@Component("OutwardDocSave")
public class OutwardDocSave {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(OutwardDocSave.class);

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docHeaderRepository;

	@Autowired
	@Qualifier("DefaultDuplicateDocCheckServiceImpl")
	private DuplicateDocCheckService duplicateDocCheckService;

	@Autowired
	@Qualifier("DefaultOutwardSupplyTypeResolver")
	OutwardSupplyTypeResolver outwardSupplyTypeResolver;

	@Autowired
	@Qualifier("OutwardFieldTruncation")
	private OutwardFieldTruncation outwardFieldTruncation;

	@Autowired
	@Qualifier("OutwardDocMemoValCalculation")
	private OutwardDocMemoValCalculation outwardDocMemoValCalc;

	@Autowired
	@Qualifier("OnboardingConfigParamsCheck")
	private OnboardingConfigParamsCheck onboardingConfigParamCheck;

	@Autowired
	@Qualifier("OutwardConfigParams")
	private OutwardConfigParams outwardConfigParams;

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

	@Autowired
	@Qualifier("NilAndHsnProcedureCallRepositoryImpl")
	private NilAndHsnProcedureCallRepository nilAndHsnProcedureCallRepository;

	/**
	 * This method is responsible for converting all gstins, supply type, doc
	 * type to upper case and setting Supply Type to Header from Items and form
	 * return type ANX1/GSTR1 based on Entity Tax Period
	 * 
	 * @param documents
	 */
	public void convertCalcConfigAndSetValues(
			List<OutwardTransDocument> documents) {
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
		documents.forEach(document -> {
			setPosAndStateCodeValues(document);
			document.setTransactionType(GSTConstants.O);
			// Convert Doc Type Supply Type and all GSTINs to upper case
			convertToUpperCaseAndSetDocValues(document);
			if (null != document.getLineItems()) {
				document.getLineItems().forEach(item -> {
					setHeaderValuesToItem(document, item);
					setAbsoluteValues(document, item);
					calculateAndSetTaxPayble(document, item);
					calculateAndSetTaxableValue(document, item);
					calculateAndSetItemTaxRate(document, item);
					calculateAndSetTotalTaxAmt(document, item);
					calculateAndSetDocAmount(document, item);
				});
			}
			// Set Onboarding Configurable Parameters
			String sgstin = document.getSgstin();
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
			outwardConfigParams.configurableParameters(document,
					questionAnsMap);
			outwardConfigParams.configurebleOnboardingLineItemAmt(document,
					questionAnsMap);

		});
	}

	/**
	 * 
	 * @param docs
	 * @param docKeyGen
	 * @return
	 */
	public List<OutwardTransDocument> saveDocs(List<OutwardTransDocument> docs,
			DocKeyGenerator<OutwardTransDocument, String> docKeyGen) {

		/**
		 * Start : Below code is for updating the existing document The below
		 * code is executed in case of document error correction
		 * 
		 */
		FileStatusPerfUtil.logEvent("SAVE_DOC_SOFT_DELETE_BEGIN");
		// Get all the non-null ids from the list. This will be used to
		// mark the existing documents in the DB as deleted.
		List<Long> docIds = docs.stream().filter(doc -> doc.getId() != null)
				.map(doc -> doc.getId()).collect(Collectors.toList());
		OutwardTransDocument firstOutwardDoc = docs.get(0);
		// Execute the repository query to udpate the isDelete to true for the
		// above ids. Also mark the updated date to the current date.
		// call the is delete to true repository method.
		if (!docIds.isEmpty()) {
			LocalDateTime updatedDate = LocalDateTime.now();

			docHeaderRepository.updateDocDeletion(docIds, updatedDate,
					firstOutwardDoc.getCreatedBy());

			docs.forEach(doc -> {
				doc.setGstnError(false);
				doc.setSaved(false);
				doc.setSent(false);
			});
		}
		FileStatusPerfUtil.logEvent("SAVE_DOC_SOFT_DELETION_END");

		/**
		 * Start - Duplicate Document Check
		 */
		FileStatusPerfUtil.logEvent("SAVE_DOC_DUP_CHECK_BEGIN");

		List<OutwardTransDocument> activeDocList = docs.stream()
				.filter(doc -> !doc.isDeleted()).collect(Collectors.toList());
		if(!activeDocList.isEmpty()){
			duplicateDocCheckService.softDeleteDuplicateDocuments(docs);
		}
		

		FileStatusPerfUtil.logEvent("SAVE_DOC_DUP_CHECK_END");
		/**
		 * End - Duplicate Document Check
		 */

		// explictly set the documeent ids to null in the input collection
		// to be saved.
		docs.forEach(doc -> doc.setId(null));
		/**
		 * End
		 */

		FileStatusPerfUtil.logEvent("DOCUMENT_OBJECT_WITH_LINE_ITEM_BEGIN");
		// Attach the document object with the line items.
		docs.forEach(document -> {
			String docKey = docKeyGen.generateKey(document);
			document.setCreatedDate(LocalDateTime.now());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Doc Key " + docKey);
			}
			document.setDocKey(docKey);
			if (document.isDeleted()) {
				document.setErrCodes(GSTConstants.ER15167);
			}
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
			if (null != document.getLineItems()) {
				document.getLineItems().forEach(item -> {
					// Truncate Line Item Fields
					outwardFieldTruncation.truncateLineItemFields(item);
					outwardDocMemoValCalc.calculateMemoValues(document, item);
					item.setDocument(document);
				});
			}
			// Set Document Saving Time for Performance Testing
			LocalDateTime beforeSavingTime = LocalDateTime.now();
			document.setBeforeSavingOn(beforeSavingTime);
		});
		FileStatusPerfUtil.logEvent("DOCUMENT_OBJECT_WITH_LINE_ITEM_END");
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("DefaultDocSaveService saveDocuments End");
		}

		// save all the documents.
		FileStatusPerfUtil.logEvent("SAVE_DOC_SAVE_DB_BEGIN");
		List<OutwardTransDocument> outWardTransList = docHeaderRepository
				.saveAll(docs);
		FileStatusPerfUtil.logEvent("SAVE_DOC_SAVE_DB_END");
		return outWardTransList;
	}

	private void convertToUpperCaseAndSetDocValues(
			OutwardTransDocument document) {
		String supplyType = outwardSupplyTypeResolver.resolve(document);
		document.setSupplyType(supplyType.toUpperCase());
		document.setDocType(document.getDocType().toUpperCase());
		if (document.getOrigDocType() != null
				&& !document.getOrigDocType().trim().isEmpty()) {
			document.setOrigDocType(document.getOrigDocType().toUpperCase());
		}
		document.setSgstin(document.getSgstin().toUpperCase());
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
		if (!GSTConstants.Y.equalsIgnoreCase(document.getReverseCharge())) {
			document.setReverseCharge(GSTConstants.N);
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
		document.setDerivedSgstinPan(document.getSgstin().substring(2, 12));
		// Set Derived CGSTIN PAN
		if (document.getCgstin() != null
				&& !document.getCgstin().trim().isEmpty()) {
			document.setDerivedCgstinPan(document.getCgstin().substring(2, 12));
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

	private void setAbsoluteValues(OutwardTransDocument document,
			OutwardTransDocLineItem item) {
		String docType = document.getDocType();
		String supplyType = document.getSupplyType();
		final List<String> docTypes = ImmutableList.of("CR", "RCR", "RFV",
				"RRFV", "AV", "RAV");
		final List<String> supplyTypes = ImmutableList.of("CAN");
		if (docTypes.contains(docType) || supplyTypes.contains(supplyType)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Setting Absolutes Values for Doc Type " + docType
						+ " Supply Type " + supplyType);
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
		}
	}

	private void setFormReturnType(OutwardTransDocument document,
			Map<String, Long> gstinAndEntityMap,
			Map<String, String> questionAnsMap) {
		String gstin = document.getSgstin();
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
		BigDecimal docAmt = document.getDocAmount();
		BigDecimal otherValues = document.getOtherValues();
		BigDecimal stateCessAmt = document.getStateCessAmount();

		if (docAmt == null) {
			docAmt = BigDecimal.ZERO;
		}
		if (otherValues == null) {
			otherValues = BigDecimal.ZERO;
		}
		if (stateCessAmt == null) {
			stateCessAmt = BigDecimal.ZERO;
		}
		if (item.getLineItemAmt() != null) {
			docAmt = docAmt.add(item.getLineItemAmt());
			document.setDocAmount(docAmt); // set Doc Amount
		}
		if (item.getOtherValues() != null) {
			otherValues = otherValues.add(item.getOtherValues());
			document.setOtherValues(otherValues); // set Other Values
		}
		if (item.getStateCessAmount() != null) {
			stateCessAmt = stateCessAmt.add(item.getStateCessAmount());
			document.setStateCessAmount(stateCessAmt);// set State Cess Amt
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
		item.setCustOrSuppAddress3(document.getCustOrSuppAddress3());
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
		item.setProfitCentre(document.getProfitCentre());
		item.setLocation(document.getLocation());
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
}
