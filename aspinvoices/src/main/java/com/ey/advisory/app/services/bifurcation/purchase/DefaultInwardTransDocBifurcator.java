package com.ey.advisory.app.services.bifurcation.purchase;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.data.entities.client.BifurcationConstants;
import com.ey.advisory.app.data.entities.client.InwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.services.bifurcation.DocBifurcator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

/**
 * 
 * @author Mohana.Dasari
 *
 */
@Component("DefaultInwardTransDocBifurcator")
public class DefaultInwardTransDocBifurcator
		implements DocBifurcator<InwardTransDocument> {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DefaultInwardTransDocBifurcator.class);

	@Autowired
	@Qualifier("Ehcachegstin")
	private Ehcachegstin gstinDetailsCache;

	@Override
	public InwardTransDocument bifurcate(InwardTransDocument document,
			ProcessingContext context) {
		InwardTransDocument doc = null;
		LOGGER.debug("INWARD Bifurcation starting..");

		try {

			doc = checkForGSTR6Table3(document, context);
			if (isBifurcated(doc))
				return doc;

			doc = checkForGSTR6Table6A(document, context);
			if (isBifurcated(doc))
				return doc;

			doc = checkForGSTR6Table6B(document, context);
			if (isBifurcated(doc))
				return doc;

			doc = checkForGSTR6Table6C(document, context);
			if (isBifurcated(doc))
				return doc;

			doc = checkForTableISD(document, context);
			if (isBifurcated(doc))
				return doc;

			doc = checkForTableB2bCdnB2baCdna(document, context);
			if (isBifurcated(doc))
				return doc;

			doc = checkForTableRcurdImpgImps(document, context);
			if (isBifurcated(doc))
				return doc;

			doc = checkForTableIMPG(document, context);
			if (isBifurcated(doc))
				return doc;

			doc = checkForGSTR2Table7(document, context);
			if (isBifurcated(doc))
				return doc;

		} catch (Exception ex) {
			LOGGER.error("Bifurcation failed due to exception.", ex);
		}

		return doc;
	}

	public boolean isBifurcated(InwardTransDocument doc) {
		return (doc.getTableType() != null && doc.getGstnBifurcation() != null);
	}

	public InwardTransDocument checkForTableB2bCdnB2baCdna(
			InwardTransDocument document, ProcessingContext context) {

		final List<String> docType1 = ImmutableList.of("INV");
		final List<String> docType2 = ImmutableList.of("CR", "DR");
		final List<String> docType3 = ImmutableList.of("RNV");
		final List<String> docType4 = ImmutableList.of("RCR", "RDR");

		String sgstin = document.getSgstin();
		String docType = document.getDocType();
		String cgstin = document.getCgstin();

		String groupCode = TenantContext.getTenantId();
		GSTNDetailEntity gstinInfo = gstinDetailsCache.getGstinInfo(groupCode,
				cgstin);
		String registrationType = gstinInfo != null
				? gstinInfo.getRegistrationType() : "";

		if ("ISD".equalsIgnoreCase(registrationType))
			return document;

		String supplyType = document.getSupplyType();
		boolean isSupplyType = checkSupplyType(document, supplyType);

		if (sgstin == null || sgstin.length() != 15 || docType == null
				|| !isSupplyType) {
			return document;
		}

		if (docType1.contains(docType)) {
			document.setGstnBifurcation(BifurcationConstants.TAX_DOC_TYPE_B2B);
			document.setTableType(BifurcationConstants.SECTION_1);
			document.setGstrReturnType(BifurcationConstants.GSTR2_RETURN_TYPE);
		} else if (docType2.contains(docType)) {
			document.setGstnBifurcation(BifurcationConstants.TAX_DOC_TYPE_CDN);
			document.setTableType(BifurcationConstants.SECTION_3);
			document.setGstrReturnType(BifurcationConstants.GSTR2_RETURN_TYPE);
		} else if (docType3.contains(docType)) {
			document.setGstnBifurcation(BifurcationConstants.TAX_DOC_TYPE_B2BA);
			document.setTableType(BifurcationConstants.SECTION_2);
			document.setGstrReturnType(BifurcationConstants.GSTR2_RETURN_TYPE);
		} else if (docType4.contains(docType)) {
			document.setGstnBifurcation(BifurcationConstants.TAX_DOC_TYPE_CDNA);
			document.setTableType(BifurcationConstants.SECTION_4);
			document.setGstrReturnType(BifurcationConstants.GSTR2_RETURN_TYPE);
		} else {
			return document;
		}

		return document;
	}

	private boolean checkSupplyType(InwardTransDocument document,
			String supplyType) {

		final List<String> supplyType1 = ImmutableList.of("TAX", "DTA");
		final List<String> supplyType2 = ImmutableList.of("TAX", "NON", "EXT",
				"NIL", "SCH3");
		final List<String> supplyType3 = ImmutableList.of("SEZS");
		final List<String> supplyType4 = ImmutableList.of("DXP");
		final List<String> supplyType5 = ImmutableList.of("SEZG");
		final List<String> supplyType6 = ImmutableList.of("DTA");

		//String portCode = document.getPortCode();
		String billOfEntry = document.getBillOfEntryNo();
		LocalDate billOfEntryDate = document.getBillOfEntryDate();
		boolean isBillOfEntryDetailsAvailabe = billOfEntry != null && billOfEntryDate != null;

		BigDecimal totalTaxAmt = BigDecimal.ZERO;

		for (InwardTransDocLineItem lineItem : document.getLineItems()) {
			BigDecimal taxAmount = lineItem.getCgstAmount() == null
					? BigDecimal.ZERO
					: lineItem.getCgstAmount()
							.add(checkForNULL(lineItem.getIgstAmount()))
							.add(checkForNULL(lineItem.getSgstAmount()))
							.add(checkForNULL(lineItem.getCessAmountSpecific()))
							.add(checkForNULL(
									lineItem.getCessAmountAdvalorem()));
			totalTaxAmt = totalTaxAmt.add(taxAmount);
		}

		boolean isTaxApplicable = totalTaxAmt.compareTo(BigDecimal.ZERO) > 0;

		String revCharge = document.getReverseCharge();

		final List<String> isRevCharge = ImmutableList.of("Y", "y");
		final List<String> isNotRevCharge = ImmutableList.of("N", "n");

		boolean isSupplyType = false;

		if ((supplyType1.contains(supplyType)
				&& isRevCharge.contains(revCharge))
				|| (supplyType2.contains(supplyType)
						&& isNotRevCharge.contains(revCharge))
				|| (supplyType3.contains(supplyType)
						&& isNotRevCharge.contains(revCharge))
				|| (supplyType4.contains(supplyType)
						&& isNotRevCharge.contains(revCharge))
				|| (supplyType5.contains(supplyType)
						&& !isBillOfEntryDetailsAvailabe
						&& isNotRevCharge.contains(revCharge))
				|| (supplyType6.contains(supplyType) && isTaxApplicable
						&& isNotRevCharge.contains(revCharge))
				|| (supplyType6.contains(supplyType) && !isTaxApplicable
						&& isNotRevCharge.contains(revCharge))) {
			isSupplyType = true;
		}

		return isSupplyType;
	}

	public InwardTransDocument checkForTableRcurdImpgImps(
	        InwardTransDocument document, ProcessingContext context) {

	    final List<String> supplyType1 = ImmutableList.of("TAX", "DTA");
	    final List<String> supplyType2 = ImmutableList.of("IMPS");
	    final List<String> supplyType3 = ImmutableList.of("IMPG");
	    final List<String> supplyType4 = ImmutableList.of("NIL", "NON", "SCH3", "EXT");
	    final List<String> docType1 = ImmutableList.of("INV", "SLF", "CR", "DR");
	    final List<String> docType2 = ImmutableList.of("RNV", "RSLF", "RCR", "RDR");
	    final List<String> docTypeImpg = ImmutableList.of("INV", "CR", "DR");
	    final List<String> docTypeImpga = ImmutableList.of("RNV", "RCR", "RDR");

	    String supplyType = document.getSupplyType();
	    String docType = document.getDocType();
	    String sgstin = document.getSgstin();
	    boolean isRevCharge = (GSTConstants.Y.equalsIgnoreCase(document.getReverseCharge()));
	    String cgstin = document.getCgstin();
	    String groupCode = TenantContext.getTenantId();
	    GSTNDetailEntity gstinInfo = gstinDetailsCache.getGstinInfo(groupCode, cgstin);
	    String registrationType = gstinInfo != null ? gstinInfo.getRegistrationType() : "";

	    if ("ISD".equalsIgnoreCase(registrationType))
	        return document;

	    if (docType1.contains(docType)) {
	        if (supplyType1.contains(supplyType) && isRevCharge
	                && (Strings.isNullOrEmpty(sgstin) || sgstin.length() == 10 || "URP".equalsIgnoreCase(sgstin))) {
	            document.setGstnBifurcation(BifurcationConstants.TAX_DOC_TYPE_RCURD);
	            document.setTableType(BifurcationConstants.SECTION_12);
	            document.setGstrReturnType(BifurcationConstants.GSTR2_RETURN_TYPE);
	        } else if (supplyType2.contains(supplyType) && isRevCharge
	                && (Strings.isNullOrEmpty(sgstin) || "URP".equalsIgnoreCase(sgstin))) {
	            document.setGstnBifurcation(BifurcationConstants.TAX_DOC_TYPE_IMPS);
	            document.setTableType(BifurcationConstants.SECTION_10);
	            document.setGstrReturnType(BifurcationConstants.GSTR2_RETURN_TYPE);
	        } else if (supplyType3.contains(supplyType) && docTypeImpg.contains(docType)
	                && (Strings.isNullOrEmpty(sgstin) || "URP".equalsIgnoreCase(sgstin))) {
	            document.setGstnBifurcation(BifurcationConstants.TAX_DOC_TYPE_IMPG);
	            document.setTableType(BifurcationConstants.SECTION_10);
	            document.setGstrReturnType(BifurcationConstants.GSTR2_RETURN_TYPE);
	        } else if (supplyType4.contains(supplyType) && !isRevCharge
	                && (Strings.isNullOrEmpty(sgstin) || sgstin.length() == 10 || "URP".equalsIgnoreCase(sgstin))) {
	            document.setGstnBifurcation(BifurcationConstants.TAX_DOC_TYPE_RCURD);
	            document.setTableType(BifurcationConstants.SECTION_12);
	            document.setGstrReturnType(BifurcationConstants.GSTR2_RETURN_TYPE);
	        }

	    } else if (docType2.contains(docType)) {
	        if (supplyType1.contains(supplyType) && isRevCharge
	                && (Strings.isNullOrEmpty(sgstin) || sgstin.length() == 10 || "URP".equalsIgnoreCase(sgstin))) {
	            document.setGstnBifurcation(BifurcationConstants.TAX_DOC_TYPE_RCURDA);
	            document.setTableType(BifurcationConstants.SECTION_13);
	            document.setGstrReturnType(BifurcationConstants.GSTR2_RETURN_TYPE);
	        } else if (supplyType2.contains(supplyType) && isRevCharge
	                && (Strings.isNullOrEmpty(sgstin) || "URP".equalsIgnoreCase(sgstin))) {
	            document.setGstnBifurcation(BifurcationConstants.TAX_DOC_TYPE_IMPSA);
	            document.setTableType(BifurcationConstants.SECTION_11);
	            document.setGstrReturnType(BifurcationConstants.GSTR2_RETURN_TYPE);
	        } else if (supplyType3.contains(supplyType) && docTypeImpga.contains(docType)
	                && (Strings.isNullOrEmpty(sgstin) || "URP".equalsIgnoreCase(sgstin))) {
	            document.setGstnBifurcation(BifurcationConstants.TAX_DOC_TYPE_IMPGA);
	            document.setTableType(BifurcationConstants.SECTION_11);
	            document.setGstrReturnType(BifurcationConstants.GSTR2_RETURN_TYPE);
	        } else if (supplyType4.contains(supplyType) && !isRevCharge
	                && (Strings.isNullOrEmpty(sgstin) || sgstin.length() == 10 || "URP".equalsIgnoreCase(sgstin))) {
	            document.setGstnBifurcation(BifurcationConstants.TAX_DOC_TYPE_RCURDA);
	            document.setTableType(BifurcationConstants.SECTION_13);
	            document.setGstrReturnType(BifurcationConstants.GSTR2_RETURN_TYPE);
	        }

	    } else {
	        return document;
	    }

	    return document;
	}
	
	

	public InwardTransDocument checkForTableISD(InwardTransDocument document,
			ProcessingContext context) {

		final List<String> supplyTypes = ImmutableList.of("TAX");
		final List<String> docType1 = ImmutableList.of("INV", "CR");
		final List<String> docType2 = ImmutableList.of("RNV", "RCR");

		String supplyType = document.getSupplyType();
		String docType = document.getDocType();
		String sgstin = document.getSgstin();
		boolean isRevCharge = (GSTConstants.Y
				.equalsIgnoreCase(document.getReverseCharge()));

		if (!supplyTypes.contains(supplyType) || isRevCharge || sgstin == null)
			return document;

		Map<String, String> gstinInfoMap = (Map<String, String>) context
				.getAttribute("gstinInfoMap");

		if ((gstinInfoMap.containsKey(sgstin)
				&& GSTConstants.ISD.equalsIgnoreCase(gstinInfoMap.get(sgstin)))
				|| "I".equalsIgnoreCase(document.getCustOrSuppType())) {
			if (docType1.contains(docType)) {
				document.setGstnBifurcation(
						BifurcationConstants.TAX_DOC_TYPE_ISD);
				document.setTableType(BifurcationConstants.SECTION_5);
				document.setGstrReturnType(
						BifurcationConstants.GSTR2_RETURN_TYPE);
			} else if (docType2.contains(docType)) {
				document.setGstnBifurcation(
						BifurcationConstants.TAX_DOC_TYPE_ISDA);
				document.setTableType(BifurcationConstants.SECTION_6);
				document.setGstrReturnType(
						BifurcationConstants.GSTR2_RETURN_TYPE);
			}
		} else {
			return document;
		}

		return document;
	}

	public InwardTransDocument checkForTableIMPG(InwardTransDocument document,
			ProcessingContext context) {

		final List<String> docType1 = ImmutableList.of("INV", "CR", "DR");
		final List<String> docType2 = ImmutableList.of("RNV", "RCR", "RDR");
		final List<String> supplyType1 = ImmutableList.of("SEZG");

		String sgstin = document.getSgstin();
		String supplyType = document.getSupplyType();
		String docType = document.getDocType();
		String billOfEntry = document.getBillOfEntryNo();
		LocalDate billOfEntryDate = document.getBillOfEntryDate();
		boolean isBillOfEntryDetailsAvailabe = billOfEntry != null && billOfEntryDate != null;
		String cgstin = document.getCgstin();

		String groupCode = TenantContext.getTenantId();
		GSTNDetailEntity gstinInfo = gstinDetailsCache.getGstinInfo(groupCode,
				cgstin);
		String registrationType = gstinInfo != null
				? gstinInfo.getRegistrationType() : "";

		if ("ISD".equalsIgnoreCase(registrationType))
			return document;

		if (supplyType1.contains(supplyType) && isBillOfEntryDetailsAvailabe
				&& sgstin != null && sgstin.length() == 15) {
			if (docType1.contains(docType)) {
				document.setGstnBifurcation(
						BifurcationConstants.TAX_DOC_TYPE_IMPGS);
				document.setTableType(BifurcationConstants.SECTION_10);
				document.setGstrReturnType(
						BifurcationConstants.GSTR2_RETURN_TYPE);
			} else if (docType2.contains(docType)) {
				document.setGstnBifurcation(
						BifurcationConstants.TAX_DOC_TYPE_IMPGSA);
				document.setTableType(BifurcationConstants.SECTION_11);
				document.setGstrReturnType(
						BifurcationConstants.GSTR2_RETURN_TYPE);
			}
		} else {
			return document;
		}

		return document;
	}

	// GSTR6 - Section - 3 and TaxDocType - B2B
	private InwardTransDocument checkForGSTR6Table3(InwardTransDocument doc,
			ProcessingContext context) {

		final List<String> docTypes = ImmutableList.of("INV");
		final List<String> supplyTypes = ImmutableList.of("TAX", "SEZS", "NIL",
				"NON", "EXT");
		String sgstin = doc.getSgstin();
		String cgstin = doc.getCgstin();
		String reverseChargeStr = doc.getReverseCharge();
		boolean isReverseCharged = (reverseChargeStr != null
				&& reverseChargeStr.equalsIgnoreCase("Y"));
		String groupCode = TenantContext.getTenantId();
		GSTNDetailEntity gstinInfo = gstinDetailsCache.getGstinInfo(groupCode,
				cgstin);
		String registrationType = gstinInfo != null
				? gstinInfo.getRegistrationType() : "";

		if (isReverseCharged || sgstin == null || cgstin == null
				|| !registrationType.equalsIgnoreCase("ISD")
				|| !docTypes.contains(doc.getDocType())
				|| !supplyTypes.contains(doc.getSupplyType())
				|| "URP".equalsIgnoreCase(sgstin))
			return doc;

		doc.setGstrReturnType(BifurcationConstants.GSTR6_RETURN_TYPE);
		doc.setGstnBifurcation(BifurcationConstants.TAX_DOC_TYPE_B2B);
		doc.setTableType(BifurcationConstants.SECTION_3);

		return doc;
	}

	// GSTR6 - Section - 6A and TaxDocType - B2BA
	private InwardTransDocument checkForGSTR6Table6A(InwardTransDocument doc,
			ProcessingContext context) {
		final List<String> docTypes = ImmutableList.of("RNV");
		final List<String> supplyTypes = ImmutableList.of("TAX", "SEZS", "NIL",
				"NON", "EXT");
		String sgstin = doc.getSgstin();
		String cgstin = doc.getCgstin();
		String reverseChargeStr = doc.getReverseCharge();
		boolean isReverseCharged = (reverseChargeStr != null
				&& reverseChargeStr.equalsIgnoreCase("Y"));
		String groupCode = TenantContext.getTenantId();
		GSTNDetailEntity gstinInfo = gstinDetailsCache.getGstinInfo(groupCode,
				cgstin);
		String registrationType = gstinInfo != null
				? gstinInfo.getRegistrationType() : "";

		if (isReverseCharged || sgstin == null || cgstin == null
				|| !registrationType.equalsIgnoreCase("ISD")
				|| !docTypes.contains(doc.getDocType())
				|| !supplyTypes.contains(doc.getSupplyType())
				|| "URP".equalsIgnoreCase(sgstin))
			return doc;

		doc.setGstrReturnType(BifurcationConstants.GSTR6_RETURN_TYPE);
		doc.setGstnBifurcation(BifurcationConstants.TAX_DOC_TYPE_B2BA);
		doc.setTableType(BifurcationConstants.SECTION_6A);

		return doc;
	}

	// GSTR6 - Section - 6B and TaxDocType - CDN
	private InwardTransDocument checkForGSTR6Table6B(InwardTransDocument doc,
			ProcessingContext context) {
		final List<String> docTypes = ImmutableList.of("CR", "DR");
		final List<String> supplyTypes = ImmutableList.of("TAX", "SEZS", "NIL",
				"NON", "EXT");
		String sgstin = doc.getSgstin();
		String cgstin = doc.getCgstin();
		String reverseChargeStr = doc.getReverseCharge();
		boolean isReverseCharged = (reverseChargeStr != null
				&& reverseChargeStr.equalsIgnoreCase("Y"));
		String groupCode = TenantContext.getTenantId();
		GSTNDetailEntity gstinInfo = gstinDetailsCache.getGstinInfo(groupCode,
				cgstin);
		String registrationType = gstinInfo != null
				? gstinInfo.getRegistrationType() : "";

		if (isReverseCharged || sgstin == null || cgstin == null
				|| !registrationType.equalsIgnoreCase("ISD")
				|| !docTypes.contains(doc.getDocType())
				|| !supplyTypes.contains(doc.getSupplyType())
				|| "URP".equalsIgnoreCase(sgstin))
			return doc;

		doc.setGstrReturnType(BifurcationConstants.GSTR6_RETURN_TYPE);
		doc.setGstnBifurcation(BifurcationConstants.TAX_DOC_TYPE_CDN);
		doc.setTableType(BifurcationConstants.SECTION_6B);

		return doc;
	}

	// GSTR6 - Section - 6C and TaxDocType - CDNA
	private InwardTransDocument checkForGSTR6Table6C(InwardTransDocument doc,
			ProcessingContext context) {
		final List<String> docTypes = ImmutableList.of("RCR", "RDR");
		final List<String> supplyTypes = ImmutableList.of("TAX", "SEZS", "NIL",
				"NON", "EXT");
		String sgstin = doc.getSgstin();
		String cgstin = doc.getCgstin();
		String reverseChargeStr = doc.getReverseCharge();
		boolean isReverseCharged = (reverseChargeStr != null
				&& reverseChargeStr.equalsIgnoreCase("Y"));
		String groupCode = TenantContext.getTenantId();
		GSTNDetailEntity gstinInfo = gstinDetailsCache.getGstinInfo(groupCode,
				cgstin);
		String registrationType = gstinInfo != null
				? gstinInfo.getRegistrationType() : "";

		if (isReverseCharged || sgstin == null || cgstin == null
				|| !registrationType.equalsIgnoreCase("ISD")
				|| !docTypes.contains(doc.getDocType())
				|| !supplyTypes.contains(doc.getSupplyType())
				|| "URP".equalsIgnoreCase(sgstin))
			return doc;

		doc.setGstrReturnType(BifurcationConstants.GSTR6_RETURN_TYPE);
		doc.setGstnBifurcation(BifurcationConstants.TAX_DOC_TYPE_CDNA);
		doc.setTableType(BifurcationConstants.SECTION_6C);

		return doc;
	}

	private BigDecimal checkForNULL(BigDecimal amt) {
		return amt == null ? BigDecimal.ZERO : amt;
	}

	public InwardTransDocument checkForGSTR2Table7(InwardTransDocument document,
			ProcessingContext context) {

		final List<String> supplyType1 = ImmutableList.of("TAX");
		final List<String> supplyType2 = ImmutableList.of("IMPS");

		final List<String> docType1 = ImmutableList.of("ADV", "ADJ");

		String supplyType = document.getSupplyType();
		String docType = document.getDocType();
		String sgstin = document.getSgstin();
		boolean isRevCharge = (GSTConstants.Y
				.equalsIgnoreCase(document.getReverseCharge()));

		String cgstin = document.getCgstin();

		String groupCode = TenantContext.getTenantId();
		GSTNDetailEntity gstinInfo = gstinDetailsCache.getGstinInfo(groupCode,
				cgstin);
		String registrationType = gstinInfo != null
				? gstinInfo.getRegistrationType() : "";

		if ("ISD".equalsIgnoreCase(registrationType))
			return document;

		if (docType1.contains(docType)) {
			if (supplyType1.contains(supplyType) && isRevCharge
					&& (Strings.isNullOrEmpty(sgstin) || sgstin.length() == 15
							|| "URP".equalsIgnoreCase(sgstin)
							|| sgstin.length() == 10)) {
				document.setGstnBifurcation(
						BifurcationConstants.TAX_DOC_TYPE_RCMADV);
				document.setTableType(BifurcationConstants.SECTION_7);
				document.setGstrReturnType(
						BifurcationConstants.GSTR2_RETURN_TYPE);
			} else if (supplyType2.contains(supplyType) && isRevCharge
					&& (Strings.isNullOrEmpty(sgstin)
							|| "URP".equalsIgnoreCase(sgstin))) {
				document.setGstnBifurcation(
						BifurcationConstants.TAX_DOC_TYPE_RCMADV);
				document.setTableType(BifurcationConstants.SECTION_7);
				document.setGstrReturnType(
						BifurcationConstants.GSTR2_RETURN_TYPE);
			}
		} else {
			return document;
		}

		return document;
	}
}
