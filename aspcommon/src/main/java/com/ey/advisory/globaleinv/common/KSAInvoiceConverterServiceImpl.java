package com.ey.advisory.globaleinv.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.EYDateUtil;
import com.google.common.base.Strings;

@Component("KSAInvoiceConverterServiceImpl")
public class KSAInvoiceConverterServiceImpl implements GlobalInvoiceConverter {

	@Autowired
	@Qualifier("LineItemAmtExtractorServiceImpl")
	LineItemAmtExtractor lineItemAmt;

	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuuMMddHHmmss");

	@Override
	public Invoice convert(SAPERPRoot sourceDto) {

		Invoice ksaInvoice = new Invoice();

		ksaInvoice.setID(sourceDto.getHeader().getInvoiceno());

		ksaInvoice.setInvoiceTypeCode(sourceDto.getHeader().getInvoicetype());

		ksaInvoice.setIssueDate(sourceDto.getHeader().getInvoicedate());

		ksaInvoice.setDocumentCurrencyCode(
				sourceDto.getHeader().getCurrencycode());

		String creationDateTime = sourceDto.getHeader().getCreationDateTime();
		if (!Strings.isNullOrEmpty(creationDateTime)) {

			LocalDateTime creationDateFormatted = LocalDateTime
					.parse(creationDateTime, formatter);
			ksaInvoice
					.setCreationDateTime(EYDateUtil.fmt(creationDateFormatted));
		}else{
			ksaInvoice
			.setCreationDateTime(EYDateUtil.fmt(LocalDateTime.now()));
		}

		List<SAPERPParty> erpParty = sourceDto.getParty();

		populatePartyDetails(erpParty, ksaInvoice);

		List<SAPERPItemParticular> erpItemParti = sourceDto
				.getItemParticulars();

		List<InvoiceLine> invLineList = new ArrayList<>();
		List<SAPERPCondition> conditions = sourceDto.getConditions();

		for (SAPERPItemParticular erpItemDtls : erpItemParti) {
			InvoiceLine lineItemDtls = new InvoiceLine();
			Item itemDto = new Item();
			Price priceDtls = new Price();
			AllowanceCharge allowanceChargeDtls = new AllowanceCharge();

			lineItemDtls.setID(erpItemDtls.getInvoicelineno());
			lineItemDtls
					.setInvoicedQuantity(erpItemDtls.getQuantity().intValue());
			itemDto.setName(erpItemDtls.getItemname());

			lineItemDtls.setID(erpItemDtls.getInvoicelineno());
			lineItemDtls
					.setInvoicedQuantity(erpItemDtls.getQuantity().intValue());

			allowanceChargeDtls.setBaseAmount(erpItemDtls.getItemgrossprice());
			priceDtls.setAllowanceCharge(allowanceChargeDtls);
			priceDtls.setPriceAmount(erpItemDtls.getItemgrossprice());
			lineItemDtls.setPrice(priceDtls);
			lineItemDtls.setItem(itemDto);
			lineItemAmt.extractAndPopulateLineAmt(lineItemDtls, erpItemDtls,
					conditions);

			invLineList.add(lineItemDtls);
		}
		if (!invLineList.isEmpty()) {
			ksaInvoice.setInvoiceList(invLineList);

			Double lineExtnAmount = 0.0;
			Double taxExclAmount = 0.0;
			Double taxAmount = 0.0;
			Double taxRoundAmt = 0.0;

			for (InvoiceLine invLine : invLineList) {

				lineExtnAmount += invLine.getPrice().getAllowanceCharge()
						.getBaseAmount();
				taxExclAmount += invLine.getPrice().getPriceAmount();
				taxAmount += isAnyObjectNull(invLine.getTaxTotal(), "taxamt")
						? invLine.getTaxTotal().getTaxAmount() : 0;
				taxRoundAmt += isAnyObjectNull(invLine.getTaxTotal(),
						"roundamt") ? invLine.getTaxTotal().getRoundingAmount()
								: 0;
			}

			// Double lineExtnAmount1 = invLineList.stream().mapToDouble(
			// sla -> sla.getPrice().getAllowanceCharge().getBaseAmount())
			// .sum();
			//
			// Double taxExclAmount1 = invLineList.stream()
			// .mapToDouble(sla -> sla.getPrice().getPriceAmount()).sum();
			//
			// Double taxAmount1 = invLineList.stream()
			// .mapToDouble(
			// sla -> isAnyObjectNull(sla.getTaxTotal(), "taxamt")
			// ? sla.getTaxTotal().getTaxAmount() : 0)
			// .sum();
			// Double taxRoundAmt1 = invLineList.stream().mapToDouble(
			// sla -> isAnyObjectNull(sla.getTaxTotal(), "roundamt")
			// ? sla.getTaxTotal().getRoundingAmount() : 0)
			// .sum();

			LegalMonetaryTotal lglMonTotal = new LegalMonetaryTotal();
			lglMonTotal.setLineExtensionAmount(lineExtnAmount);
			lglMonTotal.setTaxExclusiveAmount(taxExclAmount);
			lglMonTotal.setTaxInclusiveAmount(taxRoundAmt);

			TaxTotal txTotal = new TaxTotal();
			txTotal.setTaxAmount(taxAmount);
			ksaInvoice.setLegalMonetaryTotal(lglMonTotal);
			ksaInvoice.setTaxTotal(txTotal);
		}

		return ksaInvoice;

	}

	public static boolean isAnyObjectNull(TaxTotal a, String type) {

		if ("taxamt".equalsIgnoreCase(type)) {
			return (a != null && a.getTaxAmount() != null);
		} else if ("roundamt".equalsIgnoreCase(type)) {
			return (a != null && a.getRoundingAmount() != null);
		} else {
			return false;
		}

	}

	private void populatePartyDetails(List<SAPERPParty> erpParty,
			Invoice ksaInvoice) {

		for (SAPERPParty partyDtls : erpParty) {
			Party partyDetails = new Party();
			PartyName partyNameDtls = new PartyName();
			PostalAddress postalDetails = new PostalAddress();
			PartyIdentification partyIdn = new PartyIdentification();

			AccountingParty party = new AccountingParty();
			partyNameDtls.setName(partyDtls.getPartyname());
			postalDetails.setStreetName(partyDtls.getStreet());
			postalDetails.setAdditionalStreetName(partyDtls.getAddstreet());
			postalDetails.setBuildingNumber(partyDtls.getBuildno());
			postalDetails.setCityName(partyDtls.getCity());
			postalDetails.setPostalZone(partyDtls.getPostalcode());
			partyIdn.setID(partyDtls.getTaxnumber());
			partyDetails.setPartyName(partyNameDtls);
			partyDetails.setPostalAddress(postalDetails);
			partyDetails.setPartyIdentification(partyIdn);
			party.setParty(partyDetails);

			if ("Seller".equalsIgnoreCase(partyDtls.getPartytype())) {
				ksaInvoice.setAccountingSupplierParty(party);
			}
			if ("Buyer".equalsIgnoreCase(partyDtls.getPartytype())) {
				ksaInvoice.setAccountingCustomerParty(party);
			}
		}

	}

}
