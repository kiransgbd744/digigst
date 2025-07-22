package com.ey.advisory.app.services.bifurcation.sales;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.app.data.entities.client.BifurcationConstants;
import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.services.bifurcation.DocBifurcator;
import com.ey.advisory.app.services.docs.DocKeyGenerator;
import com.ey.advisory.app.util.OnboardingQuestionValidationsUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import lombok.extern.slf4j.Slf4j;


@Component("DefaultOutwardTransDocBifurcator")
@Slf4j
public class DefaultOutwardTransDocBifurcator
		implements DocBifurcator<OutwardTransDocument> {

	@Autowired
	private DocKeyGenerator<OutwardTransDocument, String> docKeyGen;

	@Autowired
	@Qualifier("DocRepository")
	DocRepository docRepository;

	@Autowired
	@Qualifier("OnboardingQuestionValidationsUtil")
	private OnboardingQuestionValidationsUtil util;
	
	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	private static final BigDecimal thresholdDocAmount = new BigDecimal(250000);
	
    public static final String CONFI_KEY = "b2cs.threshold.value";

   
	@Override
	public OutwardTransDocument bifurcate(OutwardTransDocument document,
			ProcessingContext context) {

		OutwardTransDocument doc = null;
		String docNum = document.getDocNo();
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"About to bifurcate the invoice, Invoice Number is '%s'",
					docNum);
			LOGGER.debug(msg);
		}

		try {

			doc = checkBasedOnOnboardingAnswer(document, context);
			if (isBifurcated(doc))
				return doc;

			doc = checkForTable14II(document, context);
			if (isBifurcated(doc))
				return doc;

			doc = checkForTable15(document, context);
			if (isBifurcated(doc))
				return doc;

			doc = checkForTable4(document, context);
			if (isBifurcated(doc))
				return doc;

			doc = checkForTable5(document, context);
			if (isBifurcated(doc))
				return doc;

			doc = checkForTable6(document, context);
			if (isBifurcated(doc))
				return doc;

			doc = checkForTable7(document, context);
			if (isBifurcated(doc))
				return doc;

			doc = checkForTable8(document, context);
			if (isBifurcated(doc))
				return doc;

			doc = checkForTable9(document, context);
			if (isBifurcated(doc))
				return doc;

			/*
			 * doc = checkForTable10(document, context); if (isBifurcated(doc))
			 * return doc;
			 * 
			 * doc = checkForRefundVouchers(document, context); if
			 * (isBifurcated(doc)) return doc;
			 */
			doc = checkForAdvanceVouchers(document, context);
			if (isBifurcated(doc))
				return doc;

			doc = checkForAdvanceAdjustments(document, context);
			if (isBifurcated(doc))
				return doc;

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Document bifurcation failed. "
						+ "Check the bifurcation rules");
			}
		} catch (Exception ex) {
			LOGGER.error("Bifurcation failed due to exception.", ex);
		}
		return doc;
	}

	private OutwardTransDocument checkBasedOnOnboardingAnswer(
			OutwardTransDocument document, ProcessingContext context) {

		String paramkryId = "O22"; // question code

		util = StaticContextHolder.getBean("OnboardingQuestionValidationsUtil",
				OnboardingQuestionValidationsUtil.class);
		Map<Long, List<EntityConfigPrmtEntity>> entityConfigParamMap = document
				.getEntityConfigParamMap();
		String paramtrvalue = util.valid(entityConfigParamMap, paramkryId,
				document.getEntityId());

		if ("A".equalsIgnoreCase(paramtrvalue)) {

			final List<String> supplyTypes = ImmutableList.of("NIL", "EXT",
					"NON");

			final List<String> docType1 = ImmutableList.of("INV");
			final List<String> docType2 = ImmutableList.of("CR", "DR");

			if (Strings.isNullOrEmpty(document.getCgstin())
					|| "URP".equalsIgnoreCase(document.getCgstin())
					|| !supplyTypes.contains(document.getSupplyType())) {
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Invoice :'%s' doesn't belong to Table ,"
									+ " Hence moving for next rule",
							document.getDocNo());
					LOGGER.debug(msg);
				}

				return document;
			}

			if (docType1.contains(document.getDocType())) {
				if (GSTConstants.Y
						.equalsIgnoreCase(document.getReverseCharge())) {
					document.setTableType(GSTConstants.GSTR1_4B);

					document.setGstnBifurcation(GSTConstants.GSTR1_B2B);
					document.setGstrReturnType(
							BifurcationConstants.GSTR1_RETURN_TYPE);
				} else {
					document.setTableType(GSTConstants.GSTR1_4A);

					document.setGstnBifurcation(GSTConstants.GSTR1_B2B);
					document.setGstrReturnType(
							BifurcationConstants.GSTR1_RETURN_TYPE);
				}
			}

			if (docType2.contains(document.getDocType())) {
				document.setTableType(GSTConstants.GSTR1_9B);
				document.setGstnBifurcation(GSTConstants.CDNR);
				document.setGstrReturnType(
						BifurcationConstants.GSTR1_RETURN_TYPE);
			}
		}
		return document;

	}

	public boolean isBifurcated(OutwardTransDocument doc) {

		return (doc.getTableType() != null && doc.getGstnBifurcation() != null);
	}

	private OutwardTransDocument checkForTable4(OutwardTransDocument document,
			ProcessingContext context) {
		String docNum = document.getDocNo();
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"checking the invoice for Table4, Invoice Number is '%s'",
					docNum);
			LOGGER.debug(msg);
		}

		final List<String> supplyTypes = ImmutableList.of("TAX", "DTA");
		final List<String> docTypes = ImmutableList.of("INV");
		boolean isRevCharge = (GSTConstants.Y
				.equalsIgnoreCase(document.getReverseCharge()));
		String cgstin = document.getCgstin();

		if (Strings.isNullOrEmpty(cgstin) || "URP".equalsIgnoreCase(cgstin)
				|| !supplyTypes.contains(document.getSupplyType())
				|| !docTypes.contains(document.getDocType())) {
			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("Invoice :'%s' doesn't belong to Table4,"
								+ " Hence moving for next rule", docNum);
				LOGGER.debug(msg);
			}

			return document;
		}

		if (isRevCharge)
			document.setTableType(GSTConstants.GSTR1_4B);
		else
			document.setTableType(GSTConstants.GSTR1_4A);

		document.setGstnBifurcation(GSTConstants.GSTR1_B2B);
		document.setGstrReturnType(BifurcationConstants.GSTR1_RETURN_TYPE);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Invoice has been bifurcated to Table4, Invoice Number is '%s'",
					docNum);
			LOGGER.debug(msg);
		}
		return document;
	}

	private OutwardTransDocument checkForTable5(OutwardTransDocument document,
			ProcessingContext context) {
		
		Integer derivedTaxPeriod = document.getDerivedTaxperiod();
		Object attribute = context.getAttribute(CONFI_KEY);
		
       BigDecimal threshold = getThresholdForTaxPeriod((String) attribute, derivedTaxPeriod);

		final List<String> supplyTypes = ImmutableList.of("TAX", "DTA");
		final List<String> docTypes = ImmutableList.of("INV");
		String docNum = document.getDocNo();
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"checking the invoice for Table5, Invoice Number is '%s'",
					docNum);
			LOGGER.debug(msg);
		}

		boolean isRevCharge = (GSTConstants.N
				.equalsIgnoreCase(document.getReverseCharge()));

		String sgstin = document.getSgstin();
		String pos = document.getPos();
		String cgstin = document.getCgstin();
		if (!(Strings.isNullOrEmpty(cgstin) || "URP".equalsIgnoreCase(cgstin))
				|| !docTypes.contains(document.getDocType())
				|| !supplyTypes.contains(document.getSupplyType())
				|| !isRevCharge || sgstin == null || pos == null) {

			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("Invoice :'%s' doesn't belong to Table5,"
								+ " Hence moving for next rule", docNum);
				LOGGER.debug(msg);
			}

			return document;
		}
		BigDecimal docAmnt = document.getDocAmount() == null ? BigDecimal.ZERO
				: document.getDocAmount();
		if ("DTA".equalsIgnoreCase(document.getSupplyType())
				&& docAmnt.compareTo(threshold) > 0) {
			document.setTableType(GSTConstants.GSTR1_5A);
			document.setGstnBifurcation(GSTConstants.GSTR1_B2CL);
			document.setGstrReturnType(BifurcationConstants.GSTR1_RETURN_TYPE);
		} else if (docAmnt.compareTo(threshold) > 0
				&& !(sgstin.substring(0, 2).equalsIgnoreCase(pos))) {
			document.setTableType(GSTConstants.GSTR1_5A);
			document.setGstnBifurcation(GSTConstants.GSTR1_B2CL);
			document.setGstrReturnType(BifurcationConstants.GSTR1_RETURN_TYPE);

		}
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Invoice has been bifurcated to Table5, Invoice Number is '%s'",
					docNum);
			LOGGER.debug(msg);
		}

		return document;
	}

	private OutwardTransDocument checkForTable6(OutwardTransDocument document,
			ProcessingContext context) {

		String docNum = document.getDocNo();
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"checking the invoice for Table6, Invoice Number is '%s'",
					docNum);
			LOGGER.debug(msg);
		}

		final List<String> supplyTypes6A = ImmutableList.of("EXPT", "EXPWT");
		final List<String> supplyTypes6B = ImmutableList.of("SEZWP", "SEZWOP");
		final List<String> supplyTypes6C = ImmutableList.of("DXP");

		final List<String> docTypes = ImmutableList.of("INV", "BOS");

		String cgstin = document.getCgstin();
		boolean isRev = GSTConstants.N
				.equalsIgnoreCase(document.getReverseCharge());

		if (document.getDocType() == null
				|| !docTypes.contains(document.getDocType())
				|| (!supplyTypes6A.contains(document.getSupplyType())
						&& !supplyTypes6B.contains(document.getSupplyType())
						&& !supplyTypes6C.contains(document.getSupplyType()))) {

			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("Invoice :'%s' doesn't belong to Table6,"
								+ " Hence moving for next rule", docNum);
				LOGGER.debug(msg);
			}

			return document;
		}

		boolean isExp = (Strings.isNullOrEmpty(cgstin)
				|| "URP".equalsIgnoreCase(cgstin)
						&& supplyTypes6A.contains(document.getSupplyType())
						&& docTypes.contains(document.getDocType()));

		boolean isSez = (cgstin != null && !"URP".equalsIgnoreCase(cgstin)
				&& supplyTypes6B.contains(document.getSupplyType()));

		boolean isDxp = (cgstin != null && !"URP".equalsIgnoreCase(cgstin)
				&& supplyTypes6C.contains(document.getSupplyType()));

		if (isExp && isRev) {
			document.setTableType(GSTConstants.GSTR1_6A);
			document.setGstnBifurcation(GSTConstants.GSTR1_EXP);
			document.setGstrReturnType(BifurcationConstants.GSTR1_RETURN_TYPE);
		}
		if (isSez) {
			document.setTableType(GSTConstants.GSTR1_6B);
			document.setGstnBifurcation(GSTConstants.GSTR1_B2B);
			document.setGstrReturnType(BifurcationConstants.GSTR1_RETURN_TYPE);
		}
		if (isDxp) {
			document.setTableType(GSTConstants.GSTR1_6C);
			document.setGstnBifurcation(GSTConstants.GSTR1_B2B);
			document.setGstrReturnType(BifurcationConstants.GSTR1_RETURN_TYPE);
		}

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Invoice has been bifurcated to Table6, Invoice Number is '%s'",
					docNum);
			LOGGER.debug(msg);
		}
		return document;
	}

	private OutwardTransDocument checkForTable7(OutwardTransDocument document,
			ProcessingContext context) {
		
		
		Integer derivedTaxPeriod = document.getDerivedTaxperiod();
		Object attribute = context.getAttribute(CONFI_KEY);
		
       BigDecimal threshold = getThresholdForTaxPeriod((String) attribute, derivedTaxPeriod);

		String docNum = document.getDocNo();
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"checking the invoice for Table7, Document Details: '%s'",
					document.toString());
			LOGGER.debug(msg);
		}

		String sgstin = document.getSgstin();
		String pos = document.getPos();
		String docType = document.getDocType();
		String cgstin = document.getCgstin();
		String isRevCharge = document.getReverseCharge();

		final List<String> docType1 = ImmutableList.of("INV");
		final List<String> docType2 = ImmutableList.of("DR", "CR");
		final List<String> supplyTypes = ImmutableList.of("TAX", "DTA");

		if (!(Strings.isNullOrEmpty(cgstin) || "URP".equalsIgnoreCase(cgstin))
				|| (!docType1.contains(docType) && !docType2.contains(docType))
				|| !supplyTypes.contains(document.getSupplyType())
				|| sgstin == null || pos == null) {
			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("Invoice :'%s' doesn't belong to Table7,"
								+ " Hence moving for next rule", docNum);
				LOGGER.debug(msg);
			}

			return document;
		}

		boolean isSamePos = document.getSgstin().substring(0, 2)
				.equalsIgnoreCase(document.getPos());
		BigDecimal curDocAmount = document.getDocAmount() == null
				? BigDecimal.ZERO : document.getDocAmount();

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Details of Table7, DocNum : %s ,DocType : %s ,"
							+ "CurrentDocAmount: %s , isSamePos : %s ",
					docNum, docType, curDocAmount, isSamePos);
			LOGGER.debug(msg);
		}

		if (isSamePos && "TAX".equalsIgnoreCase(document.getSupplyType())) {
			document.setTableType(GSTConstants.GSTR1_7A1);
			document.setGstnBifurcation(GSTConstants.GSTR1_B2CS);
			document.setGstrReturnType(BifurcationConstants.GSTR1_RETURN_TYPE);
			return document;
		}

		if ("DTA".equalsIgnoreCase(document.getSupplyType())
				&& threshold.compareTo(curDocAmount) >= 0
				&& docType1.contains(docType)) {
			document.setTableType(GSTConstants.GSTR1_7B1);
			document.setGstnBifurcation(GSTConstants.GSTR1_B2CS);
			document.setGstrReturnType(BifurcationConstants.GSTR1_RETURN_TYPE);
			return document;
		} else if (!"DTA".equalsIgnoreCase(document.getSupplyType())
				&& !isSamePos && threshold.compareTo(curDocAmount) >= 0
				&& docType1.contains(docType)) {

			document.setTableType(GSTConstants.GSTR1_7B1);
			document.setGstnBifurcation(GSTConstants.GSTR1_B2CS);
			document.setGstrReturnType(BifurcationConstants.GSTR1_RETURN_TYPE);
			return document;

		}
		
		if (!("N".equalsIgnoreCase(isRevCharge)))
			return document;

		String orgDocKey = docKeyGen.generateOrgKey(document);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"checking the invoice for Table7, Original DocKey is '%s'",
					orgDocKey);
			LOGGER.debug(msg);
		}

		List<Object[]> orgDocDetails = docRepository
				.findActiveOrgDocsByDocKeys(orgDocKey);

		if (orgDocDetails == null)
			orgDocDetails = new ArrayList<>();

		boolean isDocExist = orgDocDetails != null && !orgDocDetails.isEmpty();

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Original Document exist : %s",
					isDocExist);
			LOGGER.debug(msg);
		}
		BigDecimal docAmount = BigDecimal.ZERO;

		if (isDocExist) {

			for (Object[] obj : orgDocDetails) {
				String docKey = String.valueOf(obj[0]);
				LocalDate docDate = (LocalDate) obj[1];

				if (docKey.equalsIgnoreCase(orgDocKey) && docDate
						.isEqual(document.getPreceedingInvoiceDate())) {
					docAmount = (BigDecimal) obj[2];
					break;
				}
			}

			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("Details of Table7 check for orgDoc, orgDocKey : "
								+ "%s ,orgDocAmount  %s", orgDocKey, docAmount);
				LOGGER.debug(msg);
			}

			if (docAmount == null
					|| threshold.compareTo(docAmount) < 0)
				return document;

			if (docType2.contains(docType)
					&& "DTA".equalsIgnoreCase(document.getSupplyType())) {
				document.setTableType(GSTConstants.GSTR1_7B1);
				document.setGstnBifurcation(GSTConstants.GSTR1_B2CS);
				document.setGstrReturnType(
						BifurcationConstants.GSTR1_RETURN_TYPE);
			} else if (docType2.contains(docType) && !isSamePos) {
				document.setTableType(GSTConstants.GSTR1_7B1);
				document.setGstnBifurcation(GSTConstants.GSTR1_B2CS);
				document.setGstrReturnType(
						BifurcationConstants.GSTR1_RETURN_TYPE);
			}

		} else {

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Original document does not exist for docnum :%s",
						docNum);
				LOGGER.debug(msg);
			}

			if (docType2.contains(docType)
					&& "DTA".equalsIgnoreCase(document.getSupplyType())) {
				document.setTableType(GSTConstants.GSTR1_7B1);
				document.setGstnBifurcation(GSTConstants.GSTR1_B2CS);
				document.setGstrReturnType(
						BifurcationConstants.GSTR1_RETURN_TYPE);
			} else if (docType2.contains(docType) && !isSamePos) {
				document.setTableType(GSTConstants.GSTR1_7B1);
				document.setGstnBifurcation(GSTConstants.GSTR1_B2CS);
				document.setGstrReturnType(
						BifurcationConstants.GSTR1_RETURN_TYPE);
			}
		}

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Invoice has been bifurcated to Table7, Invoice Number is '%s'",
					docNum);
			LOGGER.debug(msg);
		}

		return document;
	}

	private OutwardTransDocument checkForTable8(OutwardTransDocument document,
			ProcessingContext context) {

		String docNum = document.getDocNo();
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"checking the invoice for Table8, Invoice Number is '%s'",
					docNum);
			LOGGER.debug(msg);
		}

		String sgstin = document.getSgstin();
		String pos = document.getPos();
		String cgstin = document.getCgstin();

		final List<String> docTypes = ImmutableList.of("INV", "DR", "CR", "RNV",
				"RDR", "RCR", "BOS");
		final List<String> supplyTypes = ImmutableList.of("NIL", "EXT", "NON",
				"SCH3");

		if (document.getDocType() == null || document.getSupplyType() == null
				|| document.getSgstin() == null || document.getPos() == null
				|| !docTypes.contains(document.getDocType())
				|| !supplyTypes.contains(document.getSupplyType())
				|| sgstin == null || pos == null) {
			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("Invoice :'%s' doesn't belong to Table8,"
								+ " Hence moving for next rule", docNum);
				LOGGER.debug(msg);
			}

			return document;
		}

		boolean isSame = document.getSgstin().substring(0, 2)
				.equalsIgnoreCase(pos);

		if (cgstin != null && !"URP".equalsIgnoreCase(cgstin)) {

			if (isSame)
				document.setTableType(GSTConstants.GSTR1_8B);
			else
				document.setTableType(GSTConstants.GSTR1_8A);
		} else if (Strings.isNullOrEmpty(cgstin)
				|| "URP".equalsIgnoreCase(cgstin)) {
			if (isSame)
				document.setTableType(GSTConstants.GSTR1_8D);
			else
				document.setTableType(GSTConstants.GSTR1_8C);
		}
		document.setGstnBifurcation(GSTConstants.NIL_EXT_NON);
		document.setGstrReturnType(BifurcationConstants.GSTR1_RETURN_TYPE);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Invoice has been bifurcated to Table8, Invoice Number is '%s'",
					docNum);
			LOGGER.debug(msg);
		}

		return document;

	}

	private OutwardTransDocument checkForTable9(OutwardTransDocument document,
			ProcessingContext context) {
		
		Integer derivedTaxPeriod = document.getDerivedTaxperiod();
		Object attribute = context.getAttribute(CONFI_KEY);
		
       BigDecimal threshold = getThresholdForTaxPeriod((String) attribute, derivedTaxPeriod);

		String docNum = document.getDocNo();
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"checking the invoice for Table9, Document details is %s",
					document.toString());
			LOGGER.debug(msg);
		}

		String docType = document.getDocType();
		String supplyType = document.getSupplyType();
		String cgstin = document.getCgstin();
		String isRevCharge = document.getReverseCharge();

		final List<String> docType9A = ImmutableList.of("RNV");
		final List<String> docType9B = ImmutableList.of("DR", "CR", "RFV");
		final List<String> docType9C = ImmutableList.of("RCR", "RDR", "RRFV");

		final List<String> supplyTypes1 = ImmutableList.of("TAX", "DTA",
				"SEZWOP", "SEZWP", "DXP");
		final List<String> supplyTypes2 = ImmutableList.of("TAX", "DTA");
		final List<String> supplyTypes3 = ImmutableList.of("EXPT", "EXPWT");

		if (docType == null || supplyType == null
				|| (!docType9A.contains(docType) && !docType9B.contains(docType)
						&& !docType9C.contains(docType))
				|| (!supplyTypes1.contains(supplyType)
						&& !supplyTypes2.contains(supplyType)
						&& !supplyTypes3.contains(supplyType))) {
			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("Invoice :'%s' doesn't belong to Table9,"
								+ " Hence moving for next rule", docNum);
				LOGGER.debug(msg);
			}

			return document;
		}

		if (!Strings.isNullOrEmpty(cgstin) && !"URP".equalsIgnoreCase(cgstin)) {

			if (supplyTypes1.contains(supplyType)) {
				if (docType9A.contains(docType)) {
					document.setTableType(GSTConstants.GSTR1_9A);
					document.setGstnBifurcation(GSTConstants.GSTR1_B2BA);

				} else if (docType9B.contains(docType)) {
					document.setTableType(GSTConstants.GSTR1_9B);
					document.setGstnBifurcation(GSTConstants.CDNR);
				} else {
					document.setTableType(GSTConstants.GSTR1_9C);
					document.setGstnBifurcation(GSTConstants.GSTR1_CDNRA);
				}
				document.setGstrReturnType(
						BifurcationConstants.GSTR1_RETURN_TYPE);

				return document;
			}

		}

		if (Strings.isNullOrEmpty(cgstin) || "URP".equalsIgnoreCase(cgstin)) {

			if (supplyTypes3.contains(supplyType)) {
				if (docType9A.contains(docType)) {
					document.setTableType(GSTConstants.GSTR1_9A);
					document.setGstnBifurcation(GSTConstants.GSTR1_EXPA);
				} else if (docType9B.contains(docType)) {
					document.setTableType(GSTConstants.GSTR1_9B);
					document.setGstnBifurcation(GSTConstants.CDNUR_EXPORTS);
				} else {
					document.setTableType(GSTConstants.GSTR1_9C);
					document.setGstnBifurcation(GSTConstants.GSTR1_CDNURA);
				}
				document.setGstrReturnType(
						BifurcationConstants.GSTR1_RETURN_TYPE);
				return document;
			}

			if (document.getSgstin() == null || document.getPos() == null)
				return document;

			boolean isSamePos = document.getSgstin().substring(0, 2)
					.equalsIgnoreCase(document.getPos());

			if ("DTA".equalsIgnoreCase(supplyType)
					&& docType9C.contains(docType)
					&& "L".equalsIgnoreCase(isRevCharge)) {
				document.setTableType(GSTConstants.GSTR1_9C);
				document.setGstnBifurcation(GSTConstants.GSTR1_CDNURA);
				document.setGstrReturnType(
						BifurcationConstants.GSTR1_RETURN_TYPE);
				return document;
			} else if (!("DTA".equalsIgnoreCase(supplyType))
					&& docType9C.contains(docType)
					&& "L".equalsIgnoreCase(isRevCharge) && !isSamePos) {
				document.setTableType(GSTConstants.GSTR1_9C);
				document.setGstnBifurcation(GSTConstants.GSTR1_CDNURA);
				document.setGstrReturnType(
						BifurcationConstants.GSTR1_RETURN_TYPE);
				return document;
			}

			/*
			 * if (isSamePos) return document;
			 */
			if ("DTA".equalsIgnoreCase(supplyType)
					&& docType9B.contains(docType)
					&& "L".equalsIgnoreCase(isRevCharge)) {
				document.setTableType(GSTConstants.GSTR1_9B);
				document.setGstnBifurcation(GSTConstants.CDNUR_B2CL);
				document.setGstrReturnType(
						BifurcationConstants.GSTR1_RETURN_TYPE);
				return document;
			} else if (!("DTA".equalsIgnoreCase(supplyType))
					&& docType9B.contains(docType)
					&& "L".equalsIgnoreCase(isRevCharge) && !isSamePos) {
				document.setTableType(GSTConstants.GSTR1_9B);
				document.setGstnBifurcation(GSTConstants.CDNUR_B2CL);
				document.setGstrReturnType(
						BifurcationConstants.GSTR1_RETURN_TYPE);
				return document;
			}

			String orgDocKey = docKeyGen.generateOrgKey(document);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"checking the invoice for Table9, Original DocKey is '%s'",
						orgDocKey);
				LOGGER.debug(msg);
			}

			List<Object[]> orgDocDetails = docRepository
					.findActiveOrgDocsByDocKeys(orgDocKey);
			BigDecimal curDocAmount = document.getDocAmount() == null
					? BigDecimal.ZERO : document.getDocAmount();

			if (orgDocDetails == null)
				orgDocDetails = new ArrayList<>();

			boolean isDocExist = orgDocDetails != null
					&& !orgDocDetails.isEmpty();

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("Original Document exist : %s",
						isDocExist);
				LOGGER.debug(msg);
			}
			BigDecimal docAmount = BigDecimal.ZERO;

			if (isDocExist) {

				for (Object[] obj : orgDocDetails) {
					String docKey = String.valueOf(obj[0]);
					LocalDate docDate = (LocalDate) obj[1];

					if (docKey.equalsIgnoreCase(orgDocKey) && docDate
							.isEqual(document.getPreceedingInvoiceDate())) {
						docAmount = (BigDecimal) obj[2];
						break;
					}
				}

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Details of Table9 check for orgDoc, orgDocKey : "
									+ "%s ,orgDocAmount  %s",
							orgDocKey, docAmount);
					LOGGER.debug(msg);
				}

				if (docAmount == null
						|| threshold.compareTo(docAmount) > 0) {
					return document;
				}

				if ("DTA".equalsIgnoreCase(supplyType)
						&& docType9B.contains(docType)
						&& ("N".equalsIgnoreCase(isRevCharge)
								|| isRevCharge == null
								|| isRevCharge.isEmpty())) {
					document.setTableType(GSTConstants.GSTR1_9B);
					document.setGstnBifurcation(GSTConstants.CDNUR_B2CL);
					document.setGstrReturnType(
							BifurcationConstants.GSTR1_RETURN_TYPE);
					return document;
				} else if (!("DTA".equalsIgnoreCase(supplyType))
						&& docType9B.contains(docType) && !isSamePos
						&& ("N".equalsIgnoreCase(isRevCharge)
								|| isRevCharge == null
								|| isRevCharge.isEmpty())) {
					document.setTableType(GSTConstants.GSTR1_9B);
					document.setGstnBifurcation(GSTConstants.CDNUR_B2CL);
					document.setGstrReturnType(
							BifurcationConstants.GSTR1_RETURN_TYPE);
					return document;
				}

			} else {

				if (LOGGER.isDebugEnabled()) {
					String msg = String
							.format("Details of Table9 with No orgDoc ,"
									+ "curDocAmount  %s", curDocAmount);
					LOGGER.debug(msg);
				}
				if (curDocAmount == null
						|| threshold.compareTo(curDocAmount) > 0)
					return document;
			}
			if ("DTA".equalsIgnoreCase(supplyType)
					&& docType9A.contains(docType)) {
				document.setTableType(GSTConstants.GSTR1_9A);
				document.setGstnBifurcation(GSTConstants.GSTR1_B2CLA);
				document.setGstrReturnType(
						BifurcationConstants.GSTR1_RETURN_TYPE);
			} else if (!("DTA".equalsIgnoreCase(supplyType))
					&& docType9A.contains(docType) && !isSamePos) {
				document.setTableType(GSTConstants.GSTR1_9A);
				document.setGstnBifurcation(GSTConstants.GSTR1_B2CLA);
				document.setGstrReturnType(
						BifurcationConstants.GSTR1_RETURN_TYPE);
			}

		}

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Invoice has been bifurcated to Table9, Invoice Number is '%s'",
					docNum);
			LOGGER.debug(msg);
		}

		return document;

	}

	private OutwardTransDocument checkForTable10(OutwardTransDocument document,
			ProcessingContext context) {

		String docNum = document.getDocNo();
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"checking the invoice for Table10, Invoice Number is '%s'",
					docNum);
			LOGGER.debug(msg);
		}
		String sgstin = document.getSgstin();
		String pos = document.getPos();
		String cgstin = document.getCgstin();

		final List<String> docTypes = ImmutableList.of("RNV", "RDR", "RCR");
		final List<String> supplyTypes = ImmutableList.of("TAX", "DTA");

		if (!Strings.isNullOrEmpty(cgstin) || document.getDocType() == null
				|| document.getSupplyType() == null || sgstin == null
				|| pos == null || !docTypes.contains(document.getDocType())
				|| !supplyTypes.contains(document.getSupplyType())) {

			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("Invoice :'%s' doesn't belong to Table10,"
								+ " Hence moving for next rule", docNum);
				LOGGER.debug(msg);
			}
			return document;
		}

		boolean isSame = document.getSgstin().substring(0, 2)
				.equalsIgnoreCase(pos);

		if (isSame) {
			document.setTableType(GSTConstants.GSTR1_10A);
			document.setGstnBifurcation(GSTConstants.GSTR1_B2CSA);
			document.setGstrReturnType(BifurcationConstants.GSTR1_RETURN_TYPE);
		}

		BigDecimal totalFob = document.getLineItems().get(0).getFob() == null
				? BigDecimal.ZERO : document.getLineItems().get(0).getFob();
		if (!isSame && totalFob.compareTo(thresholdDocAmount) <= 0) {
			document.setTableType(GSTConstants.GSTR1_10B);
			document.setGstnBifurcation(GSTConstants.GSTR1_B2CSA);
			document.setGstrReturnType(BifurcationConstants.GSTR1_RETURN_TYPE);
		}

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Invoice has been bifurcated to Table10, Invoice Number is '%s'",
					docNum);
			LOGGER.debug(msg);
		}

		return document;

	}

	private OutwardTransDocument checkForRefundVouchers(
			OutwardTransDocument document, ProcessingContext context) {

		String docNum = document.getDocNo();
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"checking the invoice for RefundVouchers, Invoice Number is '%s'",
					docNum);
			LOGGER.debug(msg);
		}

		String sgstin = document.getSgstin();
		String pos = document.getPos();

		if (sgstin == null || pos == null) {
			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("Invoice :'%s' doesn't belong to RefundVouchers,"
								+ " Hence moving for next rule", docNum);
				LOGGER.debug(msg);
			}
			return document;
		}

		final List<String> docTypes = ImmutableList.of("RV", "RRV");
		String docType = document.getDocType();

		if (!docTypes.contains(docType)) {
			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("Invoice :'%s' doesn't belong to RefundVouchers,"
								+ " Hence moving for next rule", docNum);
				LOGGER.debug(msg);
			}

			return document;
		}

		String supplierStateCd = sgstin.substring(0, 2);

		BigDecimal totalTaxAmt = BigDecimal.ZERO;

		for (OutwardTransDocLineItem lineItem : document.getLineItems()) {
			BigDecimal cgstAmnt = lineItem.getCgstAmount() == null
					? BigDecimal.ZERO : lineItem.getCgstAmount();
			BigDecimal igstAmnt = lineItem.getIgstAmount() == null
					? BigDecimal.ZERO : lineItem.getIgstAmount();
			BigDecimal sgstAmnt = lineItem.getSgstAmount() == null
					? BigDecimal.ZERO : lineItem.getSgstAmount();
			BigDecimal cessAmntSpecific = lineItem
					.getCessAmountSpecific() == null ? BigDecimal.ZERO
							: lineItem.getCessAmountSpecific();
			BigDecimal cessAmntAdvalorem = lineItem
					.getCessAmountAdvalorem() == null ? BigDecimal.ZERO
							: lineItem.getCessAmountAdvalorem();
			BigDecimal taxAmount = cgstAmnt.add(igstAmnt).add(sgstAmnt)
					.add(cessAmntSpecific).add(cessAmntAdvalorem);
			totalTaxAmt = totalTaxAmt.add(taxAmount);
		}

		boolean isTaxApplicable = totalTaxAmt.compareTo(BigDecimal.ZERO) > 0;

		if (!isTaxApplicable) {
			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("Invoice :'%s' doesn't belong to RefundVouchers,"
								+ " Hence moving for next rule", docNum);
				LOGGER.debug(msg);
			}
			return document;
		}

		if (supplierStateCd.equalsIgnoreCase(pos)) {
			document.setTableType(docType.equalsIgnoreCase("RV")
					? BifurcationConstants.SECTION_11_PART1_A1
					: BifurcationConstants.SECTION_11_PART2_A1);
		} else {
			document.setTableType(docType.equalsIgnoreCase("RV")
					? BifurcationConstants.SECTION_11_PART1_A2
					: BifurcationConstants.SECTION_11_PART2_A2);
		}
		document.setGstnBifurcation(docType.equalsIgnoreCase("RV")
				? BifurcationConstants.TAX_DOC_TYPE_ADV_REC
				: BifurcationConstants.TAX_DOC_TYPE_ADV_RECA);
		document.setGstrReturnType(BifurcationConstants.GSTR1_RETURN_TYPE);

		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("Invoice has been bifurcated to RefundVouchers, "
							+ "Invoice Number is '%s'", docNum);
			LOGGER.debug(msg);
		}

		return document;

	}

	private OutwardTransDocument checkForAdvanceVouchers(
			OutwardTransDocument document, ProcessingContext context) {

		String docNum = document.getDocNo();
		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("checking the invoice for AdvanceVouchers, "
							+ "Invoice Number is '%s'", docNum);
			LOGGER.debug(msg);
		}

		final List<String> docTypes = ImmutableList.of(GSTConstants.ADV);
		final List<String> supplyTypes = ImmutableList.of("TAX", "DXP", "NIL",
				"NON", "EXT", "SEZWP", "SEZWOP", "DTA", "EXPT", "EXPWT");
		final List<String> othSupplyTypes = ImmutableList.of("SEZWP", "SEZWOP",
				"DTA", "EXPT", "EXPWT");

		String sgstin = document.getSgstin();
		String pos = document.getPos();
		String supplyType = document.getSupplyType();
		String docType = document.getDocType();
		boolean reverseCharge = document.getReverseCharge() == null
				|| document.getReverseCharge().equalsIgnoreCase("N") ? true
						: false;

		if (Strings.isNullOrEmpty(sgstin) || Strings.isNullOrEmpty(pos)
				|| Strings.isNullOrEmpty(supplyType)
				|| !supplyTypes.contains(supplyType)
				|| !docTypes.contains(docType) || !reverseCharge) {
			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("Invoice :'%s' doesn't belong to AdvanceVouchers,"
								+ " Hence moving for next rule", docNum);
				LOGGER.debug(msg);
			}
			return document;
		}

		if (othSupplyTypes.contains(supplyType)) {
			document.setTableType(BifurcationConstants.SECTION_11_PART1_A2);
		} else {
			boolean isSame = sgstin.substring(0, 2).equalsIgnoreCase(pos);
			if (isSame) {
				document.setTableType(BifurcationConstants.SECTION_11_PART1_A1);
			} else {
				document.setTableType(BifurcationConstants.SECTION_11_PART1_A2);
			}
		}

		document.setGstnBifurcation(GSTConstants.AT_STR);
		document.setGstrReturnType(BifurcationConstants.GSTR1_RETURN_TYPE);

		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("Invoice has been bifurcated to AdvanceVouchers, "
							+ "Invoice Number is '%s'", docNum);
			LOGGER.debug(msg);
		}

		return document;

	}

	private OutwardTransDocument checkForAdvanceAdjustments(
			OutwardTransDocument document, ProcessingContext context) {
		String docNum = document.getDocNo();
		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("checking the invoice for AdvanceVouchers, "
							+ "Invoice Number is '%s'", docNum);
			LOGGER.debug(msg);
		}
		final List<String> docTypes = ImmutableList.of(GSTConstants.ADJ);
		final List<String> supplyTypes = ImmutableList.of("TAX", "DXP", "NIL",
				"NON", "EXT", "SEZWP", "SEZWOP", "DTA", "EXPT", "EXPWT");
		final List<String> othSupplyTypes = ImmutableList.of("SEZWP", "SEZWOP",
				"DTA", "EXPT", "EXPWT");
		String sgstin = document.getSgstin();
		String pos = document.getPos();
		String supplyType = document.getSupplyType();
		String docType = document.getDocType();
		boolean reverseCharge = document.getReverseCharge() == null
				|| document.getReverseCharge().equalsIgnoreCase("N") ? true
						: false;
		if (Strings.isNullOrEmpty(sgstin) || Strings.isNullOrEmpty(pos)
				|| Strings.isNullOrEmpty(supplyType)
				|| !supplyTypes.contains(supplyType)
				|| !docTypes.contains(docType) || !reverseCharge) {
			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("Invoice :'%s' doesn't belong to AdvanceVouchers,"
								+ " Hence moving for next rule", docNum);
				LOGGER.debug(msg);
			}
			return document;
		}
		if (othSupplyTypes.contains(supplyType)) {
			document.setTableType(BifurcationConstants.SECTION_11_PART1_B2);

		} else {
			boolean isSame = sgstin.substring(0, 2).equalsIgnoreCase(pos);
			if (isSame) {
				document.setTableType(BifurcationConstants.SECTION_11_PART1_B1);
			} else {
				document.setTableType(BifurcationConstants.SECTION_11_PART1_B2);
			}
		}
		document.setGstnBifurcation(GSTConstants.TXP_STR);
		document.setGstrReturnType(BifurcationConstants.GSTR1_RETURN_TYPE);
		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("Invoice has been bifurcated to AdvanceVouchers, "
							+ "Invoice Number is '%s'", docNum);
			LOGGER.debug(msg);
		}
		return document;

	}

	private OutwardTransDocument checkForTable14II(
			OutwardTransDocument document, ProcessingContext context) {
		String docNum = document.getDocNo();
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"checking the invoice for Table4, Invoice Number is '%s'",
					docNum);
			LOGGER.debug(msg);
		}

		final List<String> supplyTypes = ImmutableList.of("TAX", "DTA", "DXP",
				"SEZWP", "SEZWOP");
		final List<String> docTypes = ImmutableList.of("INV", "CR", "DR");
		boolean isRevCharge = (GSTConstants.N
				.equalsIgnoreCase(document.getReverseCharge()));
		boolean isTcsFlag = (GSTConstants.E
				.equalsIgnoreCase(document.getTcsFlag()));
		String ecomGstin = document.getEgstin();

		if (!isRevCharge || !isTcsFlag || Strings.isNullOrEmpty(ecomGstin)
				|| !supplyTypes.contains(document.getSupplyType())
				|| !docTypes.contains(document.getDocType())) {
			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("Invoice :'%s' doesn't belong to Table14,"
								+ " Hence moving for next rule", docNum);
				LOGGER.debug(msg);
			}
			return document;
		}
		document.setTableType(GSTConstants.GSTR1_14II);
		document.setGstnBifurcation(GSTConstants.SUP_ECOM);
		document.setGstrReturnType(BifurcationConstants.GSTR1_RETURN_TYPE);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Invoice has been bifurcated to Table4, Invoice Number is '%s'",
					docNum);
			LOGGER.debug(msg);
		}
		return document;
	}

	private OutwardTransDocument checkForTable15(OutwardTransDocument document,
			ProcessingContext context) {

		final List<String> supplyTypes = ImmutableList.of("TAX", "DTA", "DXP",
				"SEZWP", "SEZWOP");
		final List<String> docTypes = ImmutableList.of("INV", "CR", "DR");
		final List<String> regRecDocTypes = ImmutableList.of("CR", "DR");
		String docNum = document.getDocNo();
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"checking the invoice for Table5, Invoice Number is '%s'",
					docNum);
			LOGGER.debug(msg);
		}

		boolean isRevCharge = (GSTConstants.N
				.equalsIgnoreCase(document.getReverseCharge()));

		boolean isTcsFlag = (GSTConstants.O
				.equalsIgnoreCase(document.getTcsFlag()));

		String egstin = document.getEgstin();
		String cgstin = document.getCgstin();

		if (regRecDocTypes.contains(document.getDocType())
				&& !Strings.isNullOrEmpty(cgstin)) {//
			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("Invoice :'%s' doesn't belong to Table15,"
								+ " Hence moving for next rule", docNum);
				LOGGER.debug(msg);
			}
			// document.setTableType(GSTConstants.GSTR1_9B);
			return document;
		}

		if (!docTypes.contains(document.getDocType())
				|| !supplyTypes.contains(document.getSupplyType())
				|| !isRevCharge || !isTcsFlag) {
			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("Invoice :'%s' doesn't belong to Table15,"
								+ " Hence moving for next rule", docNum);
				LOGGER.debug(msg);
			}
			return document;
		}

		if (!Strings.isNullOrEmpty(cgstin) && !Strings.isNullOrEmpty(egstin)) {
			document.setTableType(GSTConstants.GSTR1_15I);
		} else if (Strings.isNullOrEmpty(cgstin)
				&& !Strings.isNullOrEmpty(egstin)) {
			document.setTableType(GSTConstants.GSTR1_15II);
		} else if (!Strings.isNullOrEmpty(cgstin)
				&& Strings.isNullOrEmpty(egstin)) {
			document.setTableType(GSTConstants.GSTR1_15III);
		} else {
			document.setTableType(GSTConstants.GSTR1_15IV);
		}
		document.setGstnBifurcation(GSTConstants.ECOM_SUP);
		document.setGstrReturnType(BifurcationConstants.GSTR1_RETURN_TYPE);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Invoice has been bifurcated to Table4, Invoice Number is '%s'",
					docNum);
			LOGGER.debug(msg);
		}

		return document;
	}

	/*public static void main(String[] args) {
		  String configString = "201704-202408|250000;202409-202812|100000";
	        int derivedTaxPeriod = 202409; // Example derived tax period
	        
	        BigDecimal threshold = getThresholdForTaxPeriod(configString, derivedTaxPeriod);
	        System.out.println(threshold);

	}*/
	 public static BigDecimal getThresholdForTaxPeriod(String configString, int derivedTaxPeriod) {
	        
		 String[] entries = configString.split(";");

	        for (String entry : entries) {
	            String[] parts = entry.split("\\|");
	            String periodRange = parts[0];
	            BigDecimal threshold = new BigDecimal(parts[1]);

	            String[] periods = periodRange.split("-");
	            int startPeriod = Integer.parseInt(periods[0]);
                int endPeriod = Integer.parseInt(periods[1]);

	            if (derivedTaxPeriod >= startPeriod && derivedTaxPeriod <= endPeriod) {
	                return threshold;
	            }
	        }

	        return thresholdDocAmount;
	    }
}
