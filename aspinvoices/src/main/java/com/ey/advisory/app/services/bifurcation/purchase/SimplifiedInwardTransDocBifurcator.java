package com.ey.advisory.app.services.bifurcation.purchase;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.data.entities.client.BifurcationConstants;
import com.ey.advisory.app.data.entities.client.InwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.services.bifurcation.DocBifurcator;
import com.ey.advisory.common.ProcessingContext;
import com.google.common.collect.ImmutableList;

@Component("SimplifiedInwardTransDocBifurcator")
public class SimplifiedInwardTransDocBifurcator
		implements DocBifurcator<InwardTransDocument> {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(SimplifiedInwardTransDocBifurcator.class);
	
	@Autowired
	@Qualifier("Ehcachegstin")
	private Ehcachegstin gstinDetailsCache;

	@Override
	public InwardTransDocument bifurcate(InwardTransDocument document,
			ProcessingContext context) {

		InwardTransDocument doc = null;

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Bifurcation starting..");
		}

		try {
			doc = checkForIMPS(document, context);
			if (isBifurcated(doc))
				return doc;

			doc = checkForIMPG(document, context);
			if (isBifurcated(doc))
				return doc;

			doc = checkForSEZG(doc, context);
			if (isBifurcated(doc))
				return doc;

			doc = checkForReverseCharge(doc, context);
			if (isBifurcated(doc))
				return doc;

			doc = checkForANX2SEZS(doc, context);
			if (isBifurcated(doc))
				return doc;

			doc = checkForANX2SEZG(doc, context);
			if (isBifurcated(doc))
				return doc;

			doc = checkForSEZWithOrWithOutPayment(doc, context);
			if (isBifurcated(doc))
				return doc;

			doc = checkForANX2DeemedExports(doc, context);
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

	// Section - 3I and TaxDocType - IMPS
	private InwardTransDocument checkForIMPS(InwardTransDocument doc,
			ProcessingContext context) {

		// Check if Doc Type = INV/CR/DR
		// Check if Supply Type = SEZT
		String reverseChargeStr = doc.getReverseCharge();
		String sgstin = doc.getSgstin();
		String cgstin = doc.getCgstin();
		boolean isReverseCharged = (reverseChargeStr != null
				&& reverseChargeStr.equalsIgnoreCase("Y"));
		final List<String> docTypes = ImmutableList.of("SLF", "CR", "DR");
		final List<String> amendmentDocTypes = ImmutableList.of("RSLF", "RCR",
				"RDR");
		final List<String> supplyTypes = ImmutableList.of("IMPS");

		// If the current document's supply type and doc type does not fall
		// in the list of valid IMPS doc/supply types, then return the document
		// as un-bifurcated.
		if (!isReverseCharged || (sgstin != null && !sgstin.trim().isEmpty())
				|| cgstin == null
				|| (!docTypes.contains(doc.getDocType())
						&& !amendmentDocTypes.contains(doc.getDocType()))
				|| !supplyTypes.contains(doc.getSupplyType()))
			return doc;

		doc.setGstnBifurcationNew(BifurcationConstants.TAX_DOC_TYPE_IMPS);
		if (docTypes.contains(doc.getDocType())) {
			doc.setReturnType(BifurcationConstants.ANX1_RETURN_TYPE);
			doc.setTableTypeNew(BifurcationConstants.SECTION_3I);
		} else {
			doc.setReturnType(BifurcationConstants.ANX1A_RETURN_TYPE);
			doc.setTableTypeNew(BifurcationConstants.SECTION_3I);
		}

		return doc;

	}

	// Section - 3J and TaxDocType - IMPG
	private InwardTransDocument checkForIMPG(InwardTransDocument doc,
			ProcessingContext context) {

		// Check if Doc Type = INV/CR/DR
		// Check if Supply Type = SEZWT

		final List<String> docTypes = ImmutableList.of("INV", "CR", "DR");

		final List<String> amendmentDocTypes = ImmutableList.of("RNV");
		final List<String> supplyTypes = ImmutableList.of("IMPG");
		String sgstin = doc.getSgstin();
		String cgstin = doc.getCgstin();
		// If the current document's supply type and doc type does not fall
		// in the list of valid IMPG doc/supply types, then return the document
		// as un-bifurcated.
		if ((sgstin != null && !sgstin.trim().isEmpty()) || cgstin == null
				|| (!docTypes.contains(doc.getDocType())
						&& !amendmentDocTypes.contains(doc.getDocType()))
				|| !supplyTypes.contains(doc.getSupplyType()))
			return doc;

		doc.setGstnBifurcationNew(BifurcationConstants.TAX_DOC_TYPE_IMPG);
		if (docTypes.contains(doc.getDocType())) {
			doc.setReturnType(BifurcationConstants.ANX1_RETURN_TYPE);
			doc.setTableTypeNew(BifurcationConstants.SECTION_3J);
		} else {
			doc.setReturnType(BifurcationConstants.ANX1A_RETURN_TYPE);
			doc.setTableTypeNew(BifurcationConstants.SECTION_3J);
		}

		return doc;

	}

	// Section - 3K and TaxDocType - SEZG
	private InwardTransDocument checkForSEZG(InwardTransDocument doc,
			ProcessingContext context) {

		// Check if Doc Type = INV/CR/DR
		// Check if Supply Type = DXP

		final List<String> docTypes = ImmutableList.of("INV", "CR", "DR");
		final List<String> amendmentDocTypes = ImmutableList.of("RNV");
		final List<String> supplyTypes = ImmutableList.of("SEZG");
		String sgstin = doc.getSgstin();
		String cgstin = doc.getCgstin();
		String portCode = doc.getPortCode();
		String billOfEntry = doc.getBillOfEntryNo();
		LocalDate billOfEntryDate = doc.getBillOfEntryDate();
		boolean isBillOfEntryDetailsAvailabe = portCode != null
				&& billOfEntry != null && billOfEntryDate != null;
		// If the current document's supply type and doc type does not fall
		// in the list of valid SEZG doc/supply types, then return the document
		// as un-bifurcated.
		if (!isBillOfEntryDetailsAvailabe || sgstin == null || cgstin == null
				|| (!docTypes.contains(doc.getDocType())
						&& !amendmentDocTypes.contains(doc.getDocType()))
				|| !supplyTypes.contains(doc.getSupplyType()))
			return doc;

		doc.setGstnBifurcationNew(BifurcationConstants.TAX_DOC_TYPE_SEZG);
		if (docTypes.contains(doc.getDocType())) {
			doc.setReturnType(BifurcationConstants.ANX1_RETURN_TYPE);
			doc.setTableTypeNew(BifurcationConstants.SECTION_3K);
		} else {
			doc.setReturnType(BifurcationConstants.ANX1A_RETURN_TYPE);
			doc.setTableTypeNew(BifurcationConstants.SECTION_3K);
		}

		return doc;

	}

	// Section - 3H and TaxDocType - RCM
	private InwardTransDocument checkForReverseCharge(InwardTransDocument doc,
			ProcessingContext context) {

		String reverseChargeStr = doc.getReverseCharge();
		String sgstin = doc.getSgstin();
		String cgstin = doc.getCgstin();
		final List<String> supplyTypes = ImmutableList.of("TAX", "DTA");
		final List<String> totalDocTypes = ImmutableList.of("SLF", "INV", "CR",
				"DR");
		final List<String> crDrDocTypes = ImmutableList.of("CR", "DR");
		boolean isReverseCharged = (reverseChargeStr != null
				&& reverseChargeStr.equalsIgnoreCase("Y"));
		if (!isReverseCharged || sgstin == null || cgstin == null
				|| !totalDocTypes.contains(doc.getDocType())
				|| !supplyTypes.contains(doc.getSupplyType()))
			return doc;

		if (!crDrDocTypes.contains(doc.getDocType())) {
			if ((sgstin.length() == 15
					&& !"INV".equalsIgnoreCase(doc.getDocType()))
					|| (sgstin.length() == 10
							&& !"SLF".equalsIgnoreCase(doc.getDocType()))) {
				return doc;
			}

		}
		doc.setReturnType(BifurcationConstants.ANX1_RETURN_TYPE);
		doc.setGstnBifurcationNew(BifurcationConstants.TAX_DOC_TYPE_RCM);
		doc.setTableTypeNew(BifurcationConstants.SECTION_3H);

		return doc;

	}

	// ANX2 - Section - 3A and TaxDocType - TAX, SEZS
	private InwardTransDocument checkForANX2SEZS(InwardTransDocument doc,
			ProcessingContext context) {

		// Check if Doc Type = INV/CR/DR
		// Check if Supply Type = DXP

		final List<String> docTypes = ImmutableList.of("INV", "CR", "DR");
		final List<String> supplyTypes = ImmutableList.of("TAX", "SEZS");
		String reverseChargeStr = doc.getReverseCharge();
		String sgstin = doc.getSgstin();
		String cgstin = doc.getCgstin();

		boolean isReverseCharged = (reverseChargeStr != null
				&& reverseChargeStr.equalsIgnoreCase("Y"));

		// If the current document's supply type and doc type does not fall
		// in the list of valid SEZG doc/supply types, then return the document
		// as un-bifurcated.
		if (isReverseCharged || sgstin == null || cgstin == null
				|| !docTypes.contains(doc.getDocType())
				|| !supplyTypes.contains(doc.getSupplyType()))
			return doc;

		// sgstin should be a GSTIN
		if (sgstin.length() != 15)
			return doc;

		doc.setReturnType(BifurcationConstants.ANX2_RETURN_TYPE);
		doc.setGstnBifurcationNew(BifurcationConstants.TAX_DOC_TYPE_B2B);
		doc.setTableTypeNew(BifurcationConstants.SECTION_3B);

		return doc;

	}

	// ANX2 - Section - 3A and TaxDocType - SEZG
	private InwardTransDocument checkForANX2SEZG(InwardTransDocument doc,
			ProcessingContext context) {

		// Check if Doc Type = INV/CR/DR
		// Check if Supply Type = DXP

		final List<String> docTypes = ImmutableList.of("INV", "CR", "DR");
		final List<String> supplyTypes = ImmutableList.of("SEZG");
		String reverseChargeStr = doc.getReverseCharge();
		String sgstin = doc.getSgstin();
		String cgstin = doc.getCgstin();

		boolean isReverseCharged = (reverseChargeStr != null
				&& reverseChargeStr.equalsIgnoreCase("Y"));

		// If the current document's supply type and doc type does not fall
		// in the list of valid SEZG doc/supply types, then return the document
		// as un-bifurcated.
		if (isReverseCharged || sgstin == null || cgstin == null
				|| !docTypes.contains(doc.getDocType())
				|| !supplyTypes.contains(doc.getSupplyType()))
			return doc;

		// sgstin should be a GSTIN
		if (sgstin.length() != 15)
			return doc;
		doc.setReturnType(BifurcationConstants.ANX2_RETURN_TYPE);
		doc.setGstnBifurcationNew(BifurcationConstants.TAX_DOC_TYPE_B2B);
		doc.setTableTypeNew(BifurcationConstants.SECTION_3B);

		return doc;

	}

	// ANX2 - Section - 3A and TaxDocType - SEZWP
	private InwardTransDocument checkForSEZWithOrWithOutPayment(
			InwardTransDocument doc, ProcessingContext context) {

		// Check if Doc Type = INV/CR/DR
		// Check if Supply Type = DXP

		final List<String> docTypes = ImmutableList.of("INV", "CR", "DR");
		final List<String> supplyTypes = ImmutableList.of("DTA");
		String reverseChargeStr = doc.getReverseCharge();
		String sgstin = doc.getSgstin();
		String cgstin = doc.getCgstin();

		boolean isReverseCharged = (reverseChargeStr != null
				&& reverseChargeStr.equalsIgnoreCase("Y"));

		// If the current document's supply type and doc type does not fall
		// in the list of valid DXP doc/supply types, then return the document
		// as un-bifurcated.
		if (isReverseCharged || sgstin == null || cgstin == null
				|| !docTypes.contains(doc.getDocType())
				|| !supplyTypes.contains(doc.getSupplyType()))
			return doc;

		// sgstin should be a GSTIN
		if (sgstin.length() != 15)
			return doc;

		BigDecimal totalIgstAmount = BigDecimal.ZERO;
		BigDecimal totalCessAmountSpecific = BigDecimal.ZERO;
		BigDecimal totalCessAmountAdvalorem = BigDecimal.ZERO;

		for (InwardTransDocLineItem lineItem : doc.getLineItems()) {
			totalIgstAmount = totalIgstAmount
					.add(lineItem.getIgstAmount() == null ? BigDecimal.ZERO
							: lineItem.getIgstAmount());
			totalCessAmountSpecific = totalCessAmountSpecific.add(
					lineItem.getCessAmountSpecific() == null ? BigDecimal.ZERO
							: lineItem.getCessAmountSpecific());
			totalCessAmountAdvalorem = totalCessAmountAdvalorem.add(
					lineItem.getCessAmountAdvalorem() == null ? BigDecimal.ZERO
							: lineItem.getCessAmountAdvalorem());
		}

		boolean isTaxCharged = totalIgstAmount.add(totalCessAmountAdvalorem)
				.add(totalCessAmountSpecific).compareTo(BigDecimal.ZERO) > 0;

		doc.setGstnBifurcationNew(
				isTaxCharged ? BifurcationConstants.TAX_DOC_TYPE_SEZWP
						: BifurcationConstants.TAX_DOC_TYPE_SEZWOP);
		doc.setTableTypeNew(isTaxCharged ? BifurcationConstants.SECTION_3E
				: BifurcationConstants.SECTION_3F);
		doc.setReturnType(BifurcationConstants.ANX2_RETURN_TYPE);
		return doc;

	}

	// ANX2 - Section - 3A and TaxDocType - DXP
	private InwardTransDocument checkForANX2DeemedExports(
			InwardTransDocument doc, ProcessingContext context) {

		// Check if Doc Type = INV/CR/DR
		// Check if Supply Type = DXP

		final List<String> docTypes = ImmutableList.of("INV", "CR", "DR");
		final List<String> supplyTypes = ImmutableList.of("DXP");
		String reverseChargeStr = doc.getReverseCharge();
		String sgstin = doc.getSgstin();
		String cgstin = doc.getCgstin();

		boolean isReverseCharged = (reverseChargeStr != null
				&& reverseChargeStr.equalsIgnoreCase("Y"));

		// If the current document's supply type and doc type does not fall
		// in the list of valid DXP doc/supply types, then return the document
		// as un-bifurcated.
		if (isReverseCharged || sgstin == null || cgstin == null
				|| !docTypes.contains(doc.getDocType())
				|| !supplyTypes.contains(doc.getSupplyType()))
			return doc;

		// sgstin should be a GSTIN
		if (sgstin.length() != 15)
			return doc;
		doc.setReturnType(BifurcationConstants.ANX2_RETURN_TYPE);
		doc.setGstnBifurcationNew(BifurcationConstants.TAX_DOC_TYPE_DXP);
		doc.setTableTypeNew(BifurcationConstants.SECTION_3G);

		return doc;

	}
	

	@Override
	public boolean isBifurcated(InwardTransDocument doc) {

		return (doc.getTableTypeNew() != null
				&& doc.getGstnBifurcationNew() != null);
	}

}
