package com.ey.advisory.app.services.bifurcation.gstr1a.sales;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.data.entities.client.BifurcationConstants;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocLineItem;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocument;
import com.ey.advisory.app.services.bifurcation.DocBifurcator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.google.common.collect.ImmutableList;

@Component("Gstr1ASimplifiedOutwardTransDocBifurcator")
public class Gstr1ASimplifiedOutwardTransDocBifurcator
		implements DocBifurcator<Gstr1AOutwardTransDocument> {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1ASimplifiedOutwardTransDocBifurcator.class);

	@Autowired
	@Qualifier("Ehcachegstin")
	private Ehcachegstin gstinDetailsCache;

	@Override
	public Gstr1AOutwardTransDocument bifurcate(
			Gstr1AOutwardTransDocument document, ProcessingContext context) {

		Gstr1AOutwardTransDocument doc = null;

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Bifurcation starting..");
		}

		try {
			doc = checkForB2b(document, context);
			if (isBifurcated(doc))
				return doc;

			doc = checkForB2c(document, context);
			if (isBifurcated(doc))
				return doc;

			doc = checkForExportsWithTax(doc, context);
			if (isBifurcated(doc))
				return doc;

			doc = checkForExportsWithOutTax(doc, context);
			if (isBifurcated(doc))
				return doc;

			doc = checkForSEZWithTax(doc, context);
			if (isBifurcated(doc))
				return doc;

			doc = checkForSEZWithOutTax(doc, context);
			if (isBifurcated(doc))
				return doc;

			doc = checkForDeemedExports(doc, context);
			if (isBifurcated(doc))
				return doc;

			doc = checkForExemptedNil(doc, context);
			if (isBifurcated(doc))
				return doc;

			doc = checkForNonRated(doc, context);
			if (isBifurcated(doc))
				return doc;

			doc = checkForReverseCharge(doc, context);
			if (isBifurcated(doc))
				return doc;

			doc = checkForSezToDTA(doc, context);
			if (isBifurcated(doc))
				return doc;

			doc = checkForAdjustmentVouchers(doc, context);
			if (isBifurcated(doc))
				return doc;

			doc = checkForRefundVouchers(doc, context);
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

	// Section - 3B and TaxDocType - B2B
	private Gstr1AOutwardTransDocument checkForB2b(
			Gstr1AOutwardTransDocument doc, ProcessingContext context) {

		LOGGER.error("Inside B2B method");

		// Check if Doc Type = INV/CR/DR
		// Check if Supply Type = TAX/DTA
		// If Supply type = DTA, then access the first line Item HSN
		// Code and check if it starts with 99 (else mark the section as 18E)

		final List<String> docTypes = ImmutableList.of("INV", "CR", "DR");
		final List<String> supplyTypes = ImmutableList.of("TAX", "DTA");

		String sgstin = doc.getSgstin();

		// Check if Supplier Gstin exists
		if (sgstin == null)
			return doc;
		String groupCode = TenantContext.getTenantId();
		GSTNDetailEntity gstinInfo = gstinDetailsCache.getGstinInfo(groupCode,
				sgstin);
		String registrationType = gstinInfo != null
				? gstinInfo.getRegistrationType() : "";

		String cgstin = doc.getCgstin();

		String reverseChargeStr = doc.getReverseCharge();

		boolean isReverseCharged = (reverseChargeStr != null
				&& reverseChargeStr.equalsIgnoreCase("Y"));

		// If the current document's supply type and doc type does not fall
		// in the list of valid B2B doc/supply types, then return the document
		// as un-bifurcated.
		if (isReverseCharged || !docTypes.contains(doc.getDocType())
				|| !supplyTypes.contains(doc.getSupplyType()) || cgstin == null)
			return doc;

		if (!"DTA".equals(doc.getSupplyType())) {
			if (!"SEZ".equalsIgnoreCase(registrationType)) {
				doc.setGstnBifurcationNew(
						BifurcationConstants.TAX_DOC_TYPE_B2B);
				doc.setTableTypeNew(BifurcationConstants.SECTION_3B);
				doc.setReturnType(BifurcationConstants.ANX1_RETURN_TYPE);
				return doc;
			} else {
				return doc;
			}
		}

		if (!"SEZ".equalsIgnoreCase(registrationType)) {
			doc.setGstnBifurcationNew(BifurcationConstants.TAX_DOC_TYPE_B2B);
			doc.setTableTypeNew(BifurcationConstants.SECTION_3B);
			doc.setReturnType(BifurcationConstants.ANX1_RETURN_TYPE);
			return doc;
		}

		BigDecimal totalTaxAmt = BigDecimal.ZERO;

		for (Gstr1AOutwardTransDocLineItem lineItem : doc.getLineItems()) {
			BigDecimal taxAmount = lineItem.getCgstAmount() == null
					? BigDecimal.ZERO
					: lineItem.getCgstAmount()
							.add(lineItem.getIgstAmount() == null
									? BigDecimal.ZERO
									: lineItem.getIgstAmount())
							.add(lineItem.getSgstAmount() == null
									? BigDecimal.ZERO
									: lineItem.getSgstAmount())
							.add(lineItem.getCessAmountSpecific() == null
									? BigDecimal.ZERO
									: lineItem.getCessAmountSpecific())
							.add(lineItem.getCessAmountAdvalorem() == null
									? BigDecimal.ZERO
									: lineItem.getCessAmountAdvalorem());
			totalTaxAmt = totalTaxAmt.add(taxAmount);
		}

		boolean isTaxApplicable = totalTaxAmt.compareTo(BigDecimal.ZERO) > 0;

		// If supply type = DTA (Domestic Tariff Area), then it implies that
		// this supply was made from an SEZ to a non-SEZ. According to the
		// rules, All Goods that are sold from an SEZ to non-SEZ should be
		// a part of B2B/3B. But, services are not included in this. We
		// check for goods by checking if the first 2 characters of the
		// HSN is 99. Also, we assume that the document contains only one
		// HSN. If there are multiple, HSNs, we are ignoring everything other
		// than the HSN of the first line item.
		Gstr1AOutwardTransDocLineItem firstLineItem = doc.getLineItems().get(0);
		String hsnCode = firstLineItem.getHsnSac();
		if (hsnCode == null) {
			return doc;
		}
		boolean isService = hsnCode.startsWith("99");

		if (!isService && isTaxApplicable) {
			doc.setGstnBifurcationNew(BifurcationConstants.TAX_DOC_TYPE_B2B);
			doc.setTableTypeNew(BifurcationConstants.SECTION_3B);
			doc.setReturnType(BifurcationConstants.ANX1_RETURN_TYPE);
			return doc;
		}

		if (isService) {
			doc.setGstnBifurcationNew(BifurcationConstants.TAX_DOC_TYPE_B2B);
			doc.setTableTypeNew(BifurcationConstants.SECTION_3B);
			doc.setReturnType(BifurcationConstants.ANX1_RETURN_TYPE);
			return doc;
		}
		return doc;

	}

	// Section - 3A and TaxDocType - B2C
	private Gstr1AOutwardTransDocument checkForB2c(
			Gstr1AOutwardTransDocument doc, ProcessingContext context) {

		LOGGER.error("Inside B2C method");

		// Check if Doc Type = INV/CR/DR
		// Check if Supply Type = TAX

		final List<String> docTypes = ImmutableList.of("INV", "CR", "DR");

		final List<String> totalDocTypes = ImmutableList.of("INV", "CR", "DR",
				"RNV", "RCR", "RDR");
		final List<String> supplyTypes = ImmutableList.of("TAX", "DTA");

		String cgstin = doc.getCgstin();

		String sgstin = doc.getSgstin();

		String reverseChargeStr = doc.getReverseCharge();

		boolean isReverseCharged = (reverseChargeStr != null
				&& reverseChargeStr.equalsIgnoreCase("Y"));

		// If the current document's supply type and doc type does not fall
		// in the list of valid B2C doc/supply types, Sgstin doesn't exist
		// and Cgstin exists then return the document
		// as un-bifurcated.
		if (isReverseCharged || !totalDocTypes.contains(doc.getDocType())
				|| !supplyTypes.contains(doc.getSupplyType()) || cgstin != null
				|| sgstin == null)
			return doc;

		doc.setGstnBifurcationNew(BifurcationConstants.TAX_DOC_TYPE_B2C);

		if (docTypes.contains(doc.getDocType())) {
			doc.setTableTypeNew(BifurcationConstants.SECTION_3A);
			doc.setReturnType(BifurcationConstants.ANX1_RETURN_TYPE);
		} else {
			doc.setReturnType(BifurcationConstants.ANX1A_RETURN_TYPE);
			doc.setTableTypeNew(BifurcationConstants.SECTION_3A);
		}
		LOGGER.error("Stamped B2C ");
		return doc;

	}

	// Section - 3C and TaxDocType - EXPT
	private Gstr1AOutwardTransDocument checkForExportsWithTax(
			Gstr1AOutwardTransDocument doc, ProcessingContext context) {

		LOGGER.error("Inside ExportsWithTax method");

		// Check if Doc Type = INV/CR/DR
		// Check if Supply Type = EXPT

		final List<String> docTypes = ImmutableList.of("INV", "CR", "DR");
		final List<String> totalDocTypes = ImmutableList.of("INV", "CR", "DR",
				"RNV", "RCR", "RDR");
		final List<String> supplyTypes = ImmutableList.of("EXPT");

		String cgstin = doc.getCgstin();

		String sgstin = doc.getSgstin();

		// If the current document's supply type and doc type does not fall
		// in the list of valid EXPT doc/supply types, then return the document
		// as un-bifurcated.
		if (!totalDocTypes.contains(doc.getDocType())
				|| !supplyTypes.contains(doc.getSupplyType().trim())
				|| cgstin != null || sgstin == null)
			return doc;

		doc.setGstnBifurcationNew(BifurcationConstants.TAX_DOC_TYPE_EXPT);

		if (docTypes.contains(doc.getDocType())) {
			doc.setReturnType(BifurcationConstants.ANX1_RETURN_TYPE);
			doc.setTableTypeNew(BifurcationConstants.SECTION_3C);
		} else {
			doc.setReturnType(BifurcationConstants.ANX1A_RETURN_TYPE);
			doc.setTableTypeNew(BifurcationConstants.SECTION_3C);
		}

		LOGGER.error("Stamped ExportsWithTax");

		return doc;

	}

	// Section - 3D and TaxDocType - EXPWT
	private Gstr1AOutwardTransDocument checkForExportsWithOutTax(
			Gstr1AOutwardTransDocument doc, ProcessingContext context) {

		LOGGER.error("Inside ExportsWithOutTax method");

		// Check if Doc Type = INV/CR/DR
		// Check if Supply Type = EXPWT

		final List<String> docTypes = ImmutableList.of("INV", "CR", "DR");
		final List<String> totalDocTypes = ImmutableList.of("INV", "CR", "DR",
				"RNV", "RCR", "RDR");
		final List<String> supplyTypes = ImmutableList.of("EXPWT");

		String cgstin = doc.getCgstin();

		String sgstin = doc.getSgstin();

		// If the current document's supply type and doc type does not fall
		// in the list of valid EXPWT doc/supply types, then return the document
		// as un-bifurcated.
		if (!totalDocTypes.contains(doc.getDocType())
				|| !supplyTypes.contains(doc.getSupplyType().trim())
				|| cgstin != null || sgstin == null)
			return doc;

		doc.setGstnBifurcationNew(BifurcationConstants.TAX_DOC_TYPE_EXPWT);

		if (docTypes.contains(doc.getDocType())) {
			doc.setReturnType(BifurcationConstants.ANX1_RETURN_TYPE);
			doc.setTableTypeNew(BifurcationConstants.SECTION_3D);
		} else {
			doc.setReturnType(BifurcationConstants.ANX1A_RETURN_TYPE);
			doc.setTableTypeNew(BifurcationConstants.SECTION_3D);
		}
		LOGGER.error("Stamped ExportsWithOutTax");

		return doc;

	}

	// Section - 3E and TaxDocType - SEZT
	private Gstr1AOutwardTransDocument checkForSEZWithTax(
			Gstr1AOutwardTransDocument doc, ProcessingContext context) {

		LOGGER.error("Inside SEZWithTax method");

		// Check if Doc Type = INV/CR/DR
		// Check if Supply Type = SEZT

		final List<String> docTypes = ImmutableList.of("INV", "CR", "DR", "RNV",
				"RCR", "RDR");
		final List<String> supplyTypes = ImmutableList.of("SEZT");
		String cgstin = doc.getCgstin();

		String sgstin = doc.getSgstin();

		String reverseChargeStr = doc.getReverseCharge();

		boolean isReverseCharged = (reverseChargeStr != null
				&& reverseChargeStr.equalsIgnoreCase("Y"));

		// If the current document's supply type and doc type does not fall
		// in the list of valid SEZT doc/supply types, then return the document
		// as un-bifurcated.
		if (isReverseCharged || !docTypes.contains(doc.getDocType())
				|| !supplyTypes.contains(doc.getSupplyType()) || cgstin == null
				|| sgstin == null)
			return doc;

		doc.setGstnBifurcationNew(BifurcationConstants.TAX_DOC_TYPE_SEZT);
		doc.setReturnType(BifurcationConstants.ANX1_RETURN_TYPE);
		doc.setTableTypeNew(BifurcationConstants.SECTION_3E);

		LOGGER.error("Stamped SEZWithTax");

		return doc;

	}

	// Section - 3F and TaxDocType - SEZWT
	private Gstr1AOutwardTransDocument checkForSEZWithOutTax(
			Gstr1AOutwardTransDocument doc, ProcessingContext context) {

		LOGGER.error("Inside SEZWithOutTax method");

		// Check if Doc Type = INV/CR/DR
		// Check if Supply Type = SEZWT

		final List<String> docTypes = ImmutableList.of("INV", "CR", "DR", "RNV",
				"RCR", "RDR");
		final List<String> supplyTypes = ImmutableList.of("SEZWT");

		String cgstin = doc.getCgstin();

		String sgstin = doc.getSgstin();

		String reverseChargeStr = doc.getReverseCharge();

		boolean isReverseCharged = (reverseChargeStr != null
				&& reverseChargeStr.equalsIgnoreCase("Y"));

		// If the current document's supply type and doc type does not fall
		// in the list of valid SEZWT doc/supply types, then return the document
		// as un-bifurcated.
		if (isReverseCharged || !docTypes.contains(doc.getDocType())
				|| !supplyTypes.contains(doc.getSupplyType()) || cgstin == null
				|| sgstin == null)
			return doc;

		doc.setGstnBifurcationNew(BifurcationConstants.TAX_DOC_TYPE_SEZWT);
		doc.setTableTypeNew(BifurcationConstants.SECTION_3F);
		doc.setReturnType(BifurcationConstants.ANX1_RETURN_TYPE);

		LOGGER.error("Stamped SEZWithOutTax");
		return doc;

	}

	// Section - 3G and TaxDocType - DXP
	private Gstr1AOutwardTransDocument checkForDeemedExports(
			Gstr1AOutwardTransDocument doc, ProcessingContext context) {

		LOGGER.error("Inside DeemedExports method");

		// Check if Doc Type = INV/CR/DR
		// Check if Supply Type = DXP

		final List<String> docTypes = ImmutableList.of("INV", "CR", "DR", "RNV",
				"RCR", "RDR");
		final List<String> supplyTypes = ImmutableList.of("DXP");

		String cgstin = doc.getCgstin();

		String sgstin = doc.getSgstin();

		String reverseChargeStr = doc.getReverseCharge();

		boolean isReverseCharged = (reverseChargeStr != null
				&& reverseChargeStr.equalsIgnoreCase("Y"));
		// If the current document's supply type and doc type does not fall
		// in the list of valid DXP doc/supply types, then return the document
		// as un-bifurcated.
		if (isReverseCharged || !docTypes.contains(doc.getDocType())
				|| !supplyTypes.contains(doc.getSupplyType()) || cgstin == null
				|| sgstin == null)
			return doc;

		doc.setGstnBifurcationNew(BifurcationConstants.TAX_DOC_TYPE_DXP);
		doc.setTableTypeNew(BifurcationConstants.SECTION_3G);
		doc.setReturnType(BifurcationConstants.ANX1_RETURN_TYPE);

		LOGGER.error("Stamped DeemedExports");
		return doc;

	}

	// RET1, Section - 3D1 and TaxDocType - EXT/NIL
	private Gstr1AOutwardTransDocument checkForExemptedNil(
			Gstr1AOutwardTransDocument doc, ProcessingContext context) {

		LOGGER.error("Inside ExemptedNil method");

		// Check if Supply Type = EXT/NIL

		final List<String> supplyTypes = ImmutableList.of("EXT", "NIL");
		final List<String> docTypes = ImmutableList.of("INV", "CR", "DR");

		// If the current document's supply type does not fall
		// in the list of valid EXT/NIL supply types, then return the document
		// as un-bifurcated.
		if (doc.getSgstin() == null
				|| !supplyTypes.contains(doc.getSupplyType())
				|| !docTypes.contains(doc.getDocType()))
			return doc;

		doc.setGstnBifurcationNew(BifurcationConstants.TAX_DOC_TYPE_EXT);
		doc.setTableTypeNew(BifurcationConstants.SECTION_RET_3D1);
		doc.setReturnType(BifurcationConstants.RET1_RETURN_TYPE);

		LOGGER.error("Stamped ExemptedNil");
		return doc;

	}

	// RET1, Section - 3D2 and TaxDocType - NON
	private Gstr1AOutwardTransDocument checkForNonRated(
			Gstr1AOutwardTransDocument doc, ProcessingContext context) {

		LOGGER.error("Inside NonRated method");

		// Check if Supply Type = NON

		final List<String> supplyTypes = ImmutableList.of("NON");
		final List<String> docTypes = ImmutableList.of("INV", "CR", "DR");
		// If the current document's supply type does not fall
		// in the list of valid NON supply types, then return the document
		// as un-bifurcated.
		if (doc.getSgstin() == null
				|| !supplyTypes.contains(doc.getSupplyType())
				|| !docTypes.contains(doc.getDocType()))
			return doc;

		doc.setGstnBifurcationNew(BifurcationConstants.TAX_DOC_TYPE_NON);
		doc.setTableTypeNew(BifurcationConstants.SECTION_RET_3D2);
		doc.setReturnType(BifurcationConstants.RET1_RETURN_TYPE);

		LOGGER.error("Stamped NonRated");
		return doc;

	}

	// RET1, Section - 3D3 and Supplies Reverse Charge
	private Gstr1AOutwardTransDocument checkForReverseCharge(
			Gstr1AOutwardTransDocument doc, ProcessingContext context) {

		LOGGER.error("Inside ReverseCharge method");

		final List<String> supplyTypes = ImmutableList.of("TAX");
		final List<String> docTypes = ImmutableList.of("INV", "CR", "DR");
		String reverseChargeStr = doc.getReverseCharge();
		String cgstin = doc.getCgstin();
		boolean isReverseCharged = (reverseChargeStr != null
				&& reverseChargeStr.equalsIgnoreCase("Y"));
		if (!isReverseCharged || doc.getSgstin() == null || cgstin == null
				|| !supplyTypes.contains(doc.getSupplyType())
				|| !docTypes.contains(doc.getDocType()))
			return doc;

		doc.setGstnBifurcationNew(BifurcationConstants.TAX_DOC_TYPE_RCM);
		doc.setTableTypeNew(BifurcationConstants.SECTION_RET_3D3);
		doc.setReturnType(BifurcationConstants.RET1_RETURN_TYPE);

		LOGGER.error("Stamped ReverseCharge");
		return doc;

	}

	// RET1, Section - 3D4 and TaxDocType - DTA
	private Gstr1AOutwardTransDocument checkForSezToDTA(
			Gstr1AOutwardTransDocument doc, ProcessingContext context) {

		LOGGER.error("Inside SezToDTA method");

		// Check if Supply Type = DTA
		// If Supply type = DTA, then access the first line Item HSN
		// Code and check if it starts with other than 99

		final List<String> supplyTypes = ImmutableList.of("DTA");
		final List<String> docTypes = ImmutableList.of("INV", "CR", "DR");

		String sgstin = doc.getSgstin();

		if (sgstin == null)
			return doc;

		String groupCode = TenantContext.getTenantId();
		GSTNDetailEntity gstinInfo = gstinDetailsCache.getGstinInfo(groupCode,
				sgstin);
		String registrationType = gstinInfo != null
				? gstinInfo.getRegistrationType() : "";

		String reverseChargeStr = doc.getReverseCharge();
		String cgstin = doc.getCgstin();
		boolean isReverseCharged = (reverseChargeStr != null
				&& reverseChargeStr.equalsIgnoreCase("Y"));

		if (isReverseCharged || !supplyTypes.contains(doc.getSupplyType())
				|| cgstin == null || !docTypes.contains(doc.getDocType())
				|| !"SEZ".equalsIgnoreCase(registrationType))
			return doc;

		// If supply type = DTA (Domestic Tariff Area), then it implies that
		// this supply was made from an SEZ to a non-SEZ. According to the
		// rules, All Goods that are sold from an SEZ to non-SEZ should be
		// a part of B2B/3B. But, services are not included in this. We
		// check for goods by checking if the first 2 characters of the
		// HSN is 99. Also, we assume that the document contains only one
		// HSN. If there are multiple, HSNs, we are ignoring everything other
		// than the HSN of the first line item.
		Gstr1AOutwardTransDocLineItem firstLineItem = doc.getLineItems().get(0);
		String hsnCode = firstLineItem.getHsnSac();
		if (hsnCode == null) {
			doc.setGstnBifurcationNew(BifurcationConstants.TAX_DOC_TYPE_DTA);
			doc.setTableTypeNew(BifurcationConstants.SECTION_RET_3D4);
			return doc;
		}
		boolean isService = hsnCode.startsWith("99");

		if (isService) {
			return doc;
		}
		doc.setReturnType(BifurcationConstants.RET1_RETURN_TYPE);
		doc.setGstnBifurcationNew(BifurcationConstants.TAX_DOC_TYPE_DTA);
		doc.setTableTypeNew(BifurcationConstants.SECTION_RET_3D4);

		LOGGER.error("Stamped SezToDTA");
		return doc;

	}

	// RET1, Section - 3C4 and TaxDocType - AV
	private Gstr1AOutwardTransDocument checkForAdjustmentVouchers(
			Gstr1AOutwardTransDocument doc, ProcessingContext context) {

		LOGGER.error("Inside AdjustmentVouchers method");

		// Check if Supply Type other than NSY/CAN

		final List<String> supplyTypes = ImmutableList.of("NSY", "CAN");
		final List<String> docTypes = ImmutableList.of("AV");

		// If the current document's supply type falls
		// in the list of valid NSY/CAN supply types, then return the document
		// as un-bifurcated.
		if (doc.getSgstin() == null || supplyTypes.contains(doc.getSupplyType())
				|| !docTypes.contains(doc.getDocType()))
			return doc;

		Gstr1AOutwardTransDocLineItem firstLineItem = doc.getLineItems().get(0);
		String hsnCode = firstLineItem.getHsnSac();
		if (hsnCode == null) {
			return doc;
		}
		boolean isService = hsnCode.startsWith("99");

		BigDecimal totalTaxAmt = BigDecimal.ZERO;

		for (Gstr1AOutwardTransDocLineItem lineItem : doc.getLineItems()) {
			BigDecimal taxAmount = lineItem.getCgstAmount() == null
					? BigDecimal.ZERO
					: lineItem.getCgstAmount()
							.add(lineItem.getIgstAmount() == null
									? BigDecimal.ZERO
									: lineItem.getIgstAmount())
							.add(lineItem.getSgstAmount() == null
									? BigDecimal.ZERO
									: lineItem.getSgstAmount())
							.add(lineItem.getCessAmountSpecific() == null
									? BigDecimal.ZERO
									: lineItem.getCessAmountSpecific())
							.add(lineItem.getCessAmountAdvalorem() == null
									? BigDecimal.ZERO
									: lineItem.getCessAmountAdvalorem());
			totalTaxAmt = totalTaxAmt.add(taxAmount);
		}

		boolean isTaxApplicable = totalTaxAmt.compareTo(BigDecimal.ZERO) > 0;

		if (!isService || !isTaxApplicable) {
			return doc;
		}

		doc.setGstnBifurcationNew(BifurcationConstants.TAX_DOC_TYPE_AV);
		doc.setTableTypeNew(BifurcationConstants.SECTION_RET_3C4);
		doc.setReturnType(BifurcationConstants.RET1_RETURN_TYPE);

		LOGGER.error("Stamped AdjustmentVouchers");
		return doc;

	}

	// RET1, Section - 3C3 and TaxDocType - RV
	private Gstr1AOutwardTransDocument checkForRefundVouchers(
			Gstr1AOutwardTransDocument doc, ProcessingContext context) {

		LOGGER.error("Inside RefundVouchers method");

		// Check if Supply Type other than NSY/CAN

		final List<String> supplyTypes = ImmutableList.of("NSY", "CAN");
		final List<String> docTypes = ImmutableList.of("RV", "RFV");
		String reverseChargeStr = doc.getReverseCharge();
		boolean isReverseCharged = (reverseChargeStr != null
				&& reverseChargeStr.equalsIgnoreCase("Y"));

		// If the current document's supply type falls
		// in the list of valid NSY/CAN supply types, then return the document
		// as un-bifurcated.
		if (isReverseCharged || doc.getSgstin() == null
				|| supplyTypes.contains(doc.getSupplyType())
				|| !docTypes.contains(doc.getDocType()))
			return doc;

		Gstr1AOutwardTransDocLineItem firstLineItem = doc.getLineItems().get(0);
		String hsnCode = firstLineItem.getHsnSac();
		if (hsnCode == null) {
			return doc;
		}
		boolean isService = hsnCode.startsWith("99");

		BigDecimal totalTaxAmt = BigDecimal.ZERO;

		for (Gstr1AOutwardTransDocLineItem lineItem : doc.getLineItems()) {
			BigDecimal taxAmount = lineItem.getCgstAmount() == null
					? BigDecimal.ZERO
					: lineItem.getCgstAmount()
							.add(lineItem.getIgstAmount() == null
									? BigDecimal.ZERO
									: lineItem.getIgstAmount())
							.add(lineItem.getSgstAmount() == null
									? BigDecimal.ZERO
									: lineItem.getSgstAmount())
							.add(lineItem.getCessAmountSpecific() == null
									? BigDecimal.ZERO
									: lineItem.getCessAmountSpecific())
							.add(lineItem.getCessAmountAdvalorem() == null
									? BigDecimal.ZERO
									: lineItem.getCessAmountAdvalorem());
			totalTaxAmt = totalTaxAmt.add(taxAmount);
		}

		boolean isTaxApplicable = totalTaxAmt.compareTo(BigDecimal.ZERO) > 0;

		if (!isService || !isTaxApplicable) {
			return doc;
		}

		doc.setGstnBifurcationNew(BifurcationConstants.TAX_DOC_TYPE_RV);
		doc.setTableTypeNew(BifurcationConstants.SECTION_RET_3C3);
		doc.setReturnType(BifurcationConstants.RET1_RETURN_TYPE);

		LOGGER.error("Stamped RefundVouchers");
		return doc;

	}

	@Override
	public boolean isBifurcated(Gstr1AOutwardTransDocument doc) {

		String msg = String.format(
				"Checking isBifurcated for Doc No : %s, "
						+ "Doc Date : %s, Supply Type : %s, Doc Type : %s, Cgstin %s ",
				doc.getDocNo(), doc.getDocDate().toString(),
				doc.getSupplyType(), doc.getDocType(), doc.getCgstin());

		LOGGER.error(msg);

		return (doc.getTableTypeNew() != null
				&& doc.getGstnBifurcationNew() != null);
	}

}
