package com.ey.advisory.app.services.jobs.gstr2a;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.GetGstr2aStagingCdnInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingCdnInvoicesItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingCdnaInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingCdnaInvoicesItemEntity;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.docs.dto.CDNRInvoices;
import com.ey.advisory.app.docs.dto.CdnrLineItem;
import com.ey.advisory.app.docs.dto.CdnrLineItemDetail;
import com.ey.advisory.app.docs.dto.CreditDebitNote;
import com.ey.advisory.app.services.docs.DocKeyGenerator;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("gstr2aCdnCdnaDataParserImpl")
public class Gstr2aCdnCdnaDataParserImpl implements Gstr2aCdnCdnaDataParser {

	@Autowired
	@Qualifier("DefaultInwardTransDocKeyGenerator")
	private DocKeyGenerator<InwardTransDocument, String> docKeyGenerator;
	
	private static final String DOC_KEY_JOINER = "|";
	
	@Override
	public List<GetGstr2aStagingCdnInvoicesHeaderEntity> parseCdnData(
			final Gstr1GetInvoicesReqDto dto, final String apiResp,
			final String type, Long batchId) {

		List<GetGstr2aStagingCdnInvoicesHeaderEntity> invoiceList = new ArrayList<>();
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray respObject = null;
		Type listType = null;
		// try {
		respObject = (new JsonParser().parse(apiResp)).getAsJsonObject()
				.get(APIConstants.CDN).getAsJsonArray();

		listType = new TypeToken<List<CDNRInvoices>>() {
		}.getType();

		List<CDNRInvoices> baseEntity = gson.fromJson(respObject, listType);
		for (CDNRInvoices eachInv : baseEntity) {
			for (CreditDebitNote creddebnote : eachInv
					.getCreditDebitNoteDetails()) {
				List<GetGstr2aStagingCdnInvoicesItemEntity> itemList = new ArrayList<>();
				GetGstr2aStagingCdnInvoicesHeaderEntity invoice = setInvoiceCdnData(
						eachInv, creddebnote, dto, batchId);
				invoiceList.add(invoice);
				BigDecimal igst = BigDecimal.ZERO;
				BigDecimal cgst = BigDecimal.ZERO;
				BigDecimal sgst = BigDecimal.ZERO;
				BigDecimal cess = BigDecimal.ZERO;
				BigDecimal taxableValue = BigDecimal.ZERO;
				for (CdnrLineItem lineItem : creddebnote.getCdnrLineItem()) {
					GetGstr2aStagingCdnInvoicesItemEntity item = setItemCdnData(
							lineItem);
					if (dto.getReturnPeriod() != null
							&& dto.getReturnPeriod().length() > 0) {
						item.setDerReturnPeriod(GenUtil
								.convertTaxPeriodToInt(dto.getReturnPeriod()));
					}
					itemList.add(item);
					CdnrLineItemDetail detail = lineItem.getItemDetail();
					BigDecimal detailsIgst = detail.getIgstAmount() != null
							? detail.getIgstAmount() : BigDecimal.ZERO;
					BigDecimal detailsCgst = detail.getCgstAmount() != null
							? detail.getCgstAmount() : BigDecimal.ZERO;
					BigDecimal detailsSgst = detail.getSgstAmount() != null
							? detail.getSgstAmount() : BigDecimal.ZERO;
					BigDecimal detailsCess = detail.getCessAmount() != null
							? detail.getCessAmount() : BigDecimal.ZERO;
					BigDecimal taxValue = detail.getTaxableValue() != null
							? detail.getTaxableValue() : BigDecimal.ZERO;

					igst = igst.add(detailsIgst);
					cgst = cgst.add(detailsCgst);
					sgst = sgst.add(detailsSgst);
					cess = cess.add(detailsCess);
					taxableValue = taxableValue.add(taxValue);
				}
				/**
				 * setting summary rate info at invoice level.
				 */
				invoice.setIgstamt(igst);
				invoice.setCgstamt(cgst);
				invoice.setSgstamt(sgst);
				invoice.setCessamt(cess);
				invoice.setTaxVal(taxableValue);
				invoice.setLineItems(itemList);
				// invoice.setAction(APIConstants.N);
				invoice.setCreatedBy("SYSTEM");
				LocalDateTime convertNow = EYDateUtil
						.toUTCDateTimeFromLocal(LocalDateTime.now());
				invoice.setCreatedOn(convertNow);
				itemList.forEach(item -> {
					item.setHeader(invoice);
				});

			}
		}
		/*
		 * } catch (Exception ex) { String msg =
		 * "failed to parse Gstr2a CDN/CDNAresponse"; LOGGER.error(msg, ex); }
		 */
		return invoiceList;
	}

	@Override
	public List<GetGstr2aStagingCdnaInvoicesHeaderEntity> parseCdnaData(
			Gstr1GetInvoicesReqDto dto, String apiResp, String type,
			Long batchId) {
		List<GetGstr2aStagingCdnaInvoicesHeaderEntity> invoiceList = new ArrayList<>();
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray respObject = null;
		Type listType = null;
		// try {

		respObject = (new JsonParser().parse(apiResp)).getAsJsonObject()
				.get(APIConstants.CDNA).getAsJsonArray();

		listType = new TypeToken<List<CDNRInvoices>>() {
		}.getType();

		List<CDNRInvoices> baseEntity = gson.fromJson(respObject, listType);
		for (CDNRInvoices eachInv : baseEntity) {
			for (CreditDebitNote creddebnote : eachInv
					.getCreditDebitNoteDetails()) {
				List<GetGstr2aStagingCdnaInvoicesItemEntity> itemList = new ArrayList<>();
				GetGstr2aStagingCdnaInvoicesHeaderEntity invoice = setInvoiceCdnaData(
						eachInv, creddebnote, dto, batchId);
				invoiceList.add(invoice);
				BigDecimal igst = BigDecimal.ZERO;
				BigDecimal cgst = BigDecimal.ZERO;
				BigDecimal sgst = BigDecimal.ZERO;
				BigDecimal cess = BigDecimal.ZERO;
				BigDecimal taxableValue = BigDecimal.ZERO;
				for (CdnrLineItem lineItem : creddebnote.getCdnrLineItem()) {
					GetGstr2aStagingCdnaInvoicesItemEntity item = setItemCdnaData(
							lineItem);
					Integer derivedRetPeriod = GenUtil
							.convertTaxPeriodToInt(dto.getReturnPeriod());
					item.setDerReturnPeriod(derivedRetPeriod);
					itemList.add(item);
					CdnrLineItemDetail detail = lineItem.getItemDetail();

					BigDecimal detailsIgst = detail.getIgstAmount() != null
							? detail.getIgstAmount() : BigDecimal.ZERO;
					BigDecimal detailsCgst = detail.getCgstAmount() != null
							? detail.getCgstAmount() : BigDecimal.ZERO;
					BigDecimal detailsSgst = detail.getSgstAmount() != null
							? detail.getSgstAmount() : BigDecimal.ZERO;
					BigDecimal detailsCess = detail.getCessAmount() != null
							? detail.getCessAmount() : BigDecimal.ZERO;
					BigDecimal taxValue = detail.getTaxableValue() != null
							? detail.getTaxableValue() : BigDecimal.ZERO;

					igst = igst.add(detailsIgst);
					cgst = cgst.add(detailsCgst);
					sgst = sgst.add(detailsSgst);
					cess = cess.add(detailsCess);
					taxableValue = taxableValue.add(taxValue);
				}
				/**
				 * setting summary rate info at invoice level.
				 */
				invoice.setIgstamt(igst);
				invoice.setCgstamt(cgst);
				invoice.setSgstamt(sgst);
				invoice.setCessamt(cess);
				invoice.setTaxVal(taxableValue);
				invoice.setLineItems(itemList);
				itemList.forEach(item -> {
					item.setHeader(invoice);
				});

			}
		}
		/*
		 * } catch (Exception ex) { String msg =
		 * "failed to parse Gstr2a CDN/CDNAresponse"; LOGGER.error(msg, ex); }
		 */
		return invoiceList;
	}

	private GetGstr2aStagingCdnInvoicesHeaderEntity setInvoiceCdnData(
			CDNRInvoices eachInv, CreditDebitNote creddebnote,
			Gstr1GetInvoicesReqDto dto, Long batchId) {
		GetGstr2aStagingCdnInvoicesHeaderEntity invoice = new GetGstr2aStagingCdnInvoicesHeaderEntity();

		String cfs = eachInv.getCounFillStatus();
		String sgstin = eachInv.getCpGstin();
		String checkSum = creddebnote.getCheckSum();
		String credDebRefVoucher = creddebnote.getCredDebRefVoucher();
		String credDebRefVoucherNum = creddebnote.getCredDebRefVoucherNum();
		String credDebRefVoucherDate = creddebnote.getCredDebRefVoucherDate();
		String oriCredDebDate = creddebnote.getOriCredDebDate();
		String oriCredDebNum = creddebnote.getOriCredDebNum();
		String invNum = creddebnote.getInvNum();
		String invoiceType = creddebnote.getInvoiceType();
		String preGst = creddebnote.getPreGstRegNote();
		BigDecimal diffPercent = creddebnote.getDiffPercent();
		BigDecimal noteValue = creddebnote.getTotalNoteVal();
		String irn = creddebnote.getIrn();
		String sourceType = creddebnote.getSrctyp();
		String irnDate = creddebnote.getIrngendate();

		String cgstin = dto.getGstin();
		invoice.setTaxPeriod(dto.getReturnPeriod());

		if (dto.getReturnPeriod() != null
				&& dto.getReturnPeriod().length() > 0) {
			invoice.setDerReturnPeriod(
					GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
		}
		// adding inv_key
		String docType = invoiceType == "R" ? "INV"
				: (invoiceType == "C" ? "CR"
						: (invoiceType == "D" ? "DR"
								: (invoiceType == "B" ? "BOS" : invoiceType)));

		LOGGER.debug("DOCTYPE {} ", docType);

		String fy = GenUtil.getFinYear(LocalDate.parse(credDebRefVoucherDate,
				DateUtil.SUPPORTED_DATE_FORMAT2));

		
		InwardTransDocument inwardDoc = new InwardTransDocument();
		inwardDoc.setFinYear(fy);
		inwardDoc.setDocNo(credDebRefVoucherNum);
		inwardDoc.setDocType(docType);
		inwardDoc.setSgstin(sgstin);// output
		inwardDoc.setCgstin(dto.getGstin());// input

		String generateKey = docKeyGenerator.generateKey(inwardDoc);
		
		LOGGER.debug("generateKey {} ", generateKey);
		
		if(invoiceType!=null && (invoiceType.equalsIgnoreCase("C") ||invoiceType.equalsIgnoreCase("CR") ))
		{
		invoice.setLnkingDocKey(deriveLinkingKey(LocalDate.parse(credDebRefVoucherDate,
				DateUtil.SUPPORTED_DATE_FORMAT2),
				dto.getGstin(), sgstin, "CR", credDebRefVoucherNum));
		}else if(invoiceType!=null && (invoiceType.equalsIgnoreCase("D") ||invoiceType.equalsIgnoreCase("DR") ))
		{

			invoice.setLnkingDocKey(deriveLinkingKey(LocalDate.parse(credDebRefVoucherDate,
					DateUtil.SUPPORTED_DATE_FORMAT2),
					dto.getGstin(), sgstin, "DR", credDebRefVoucherNum));
		}
		// String generateKey = docKeyGenerator.generateKey(sgstin,
		// credDebRefVoucherNum, fy, docType);
		invoice.setInvKey(generateKey);
		/**
		 * setting CDNInvoices
		 */
		if (APIConstants.SUBMITTED.equalsIgnoreCase(cfs)) {
			cfs = APIConstants.Y;
		} else if (APIConstants.NOT_SUBMITTED.equalsIgnoreCase(cfs)) {
			cfs = APIConstants.N;
		}
		invoice.setCfsGstr1(cfs);
		invoice.setGstin(sgstin);
		invoice.setCountergstin(cgstin);
		LocalDateTime convertNow = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());
		invoice.setCreatedOn(convertNow);
		invoice.setCreatedBy("SYSTEM");
		/**
		 * setting CredDebNotes
		 */
		invoice.setCheckSum(checkSum);
		invoice.setCredDebRefVoucher(credDebRefVoucher);
		invoice.setCredDebRefVoucherNum(credDebRefVoucherNum);
		invoice.setIrnSrcType(sourceType);
		invoice.setIrnNum(irn);
		if (!Strings.isNullOrEmpty(irnDate)) {
			invoice.setIrnGenDate(
					LocalDate.parse(irnDate, DateUtil.SUPPORTED_DATE_FORMAT2));
		}
		// invoice.setn(oriCredDebNum);

		if (credDebRefVoucherDate != null
				&& credDebRefVoucherDate.trim().length() > 0) {
			invoice.setCredDebRefVoucherDate(
					LocalDate.parse(credDebRefVoucherDate,
							DateTimeFormatter.ofPattern("dd-MM-yyyy")));
		} else {
			invoice.setCredDebRefVoucherDate(null);
		}
		/*
		 * if (credDebRefVoucherDate != null &&
		 * credDebRefVoucherDate.trim().length() > 0) {
		 * invoice.setCredDebRefVoucherDate(
		 * LocalDate.parse(credDebRefVoucherDate,
		 * DateTimeFormatter.ofPattern("dd-MM-yyyy"))); } else {
		 * invoice.setCredDebRefVoucherDate(null); }
		 */

		invoice.setInvNum(invNum);
		invoice.setInvType(invoiceType);
		invoice.setNotevalue(noteValue);
		invoice.setPreGst(preGst);
		invoice.setDiffvalue(diffPercent);
		invoice.setCdnBatchIdGstr2a(batchId);
		invoice.setDelete(false);

		// if (gstnapi.isDelinkingEligible(APIConstants.GSTR2A.toUpperCase())) {
		// version 2.0 changes here...
		String cfsGstr3b = eachInv.getCfsGstr3b();
		String cancelDate = eachInv.getCancelDate();
		String filedDate = eachInv.getFiledDate();
		String filedPeriod = eachInv.getFiledPeriod();
		String orgInvPeriod = creddebnote.getOrgInvPeriod();
		String orgInvType = creddebnote.getOrgInvType();
		String pos = creddebnote.getPos();
		String rcrg = creddebnote.getReverseCharge();
		String invType = creddebnote.getInvoiceType();
		String dLinkFlag = creddebnote.getDelinkStatus();

		invoice.setCfsGstr3B(cfsGstr3b);
		if (cancelDate != null) {
			invoice.setCancelDate(LocalDate.parse(cancelDate,
					DateUtil.SUPPORTED_DATE_FORMAT7));
		}
		if (filedDate != null) {
			invoice.setFileDate(LocalDate.parse(filedDate,
					DateUtil.SUPPORTED_DATE_FORMAT7));
		}
		invoice.setFilePeriod(filedPeriod);
		invoice.setOrgInvAmdPeriod(orgInvPeriod);
		invoice.setOrgInvAmdType(orgInvType);

		invoice.setPos(pos);
		invoice.setRcrg(rcrg);
		invoice.setInvType(invType);
		invoice.setDLinkFlag(dLinkFlag);

		// }

		return invoice;
	}

	private GetGstr2aStagingCdnInvoicesItemEntity setItemCdnData(
			CdnrLineItem lineItem) {
		GetGstr2aStagingCdnInvoicesItemEntity item = new GetGstr2aStagingCdnInvoicesItemEntity();
		item.setItemnum(lineItem.getLineNumber());
		CdnrLineItemDetail detail = lineItem.getItemDetail();
		item.setTaxval(detail.getTaxableValue());
		item.setTaxrate(detail.getRate());
		item.setIgstamt(detail.getIgstAmount());
		item.setCgstamt(detail.getCgstAmount());
		item.setSgstamt(detail.getSgstAmount());
		item.setCessamt(detail.getCessAmount());
		item.setTaxval(detail.getTaxableValue());
		return item;
	}

	private GetGstr2aStagingCdnaInvoicesItemEntity setItemCdnaData(
			CdnrLineItem lineItem) {
		GetGstr2aStagingCdnaInvoicesItemEntity item = new GetGstr2aStagingCdnaInvoicesItemEntity();
		item.setItemnum(lineItem.getLineNumber());
		CdnrLineItemDetail detail = lineItem.getItemDetail();
		item.setTaxval(detail.getTaxableValue());
		item.setTaxrate(detail.getRate());
		item.setIgstamt(detail.getIgstAmount());
		item.setCgstamt(detail.getCgstAmount());
		item.setSgstamt(detail.getSgstAmount());
		item.setCessamt(detail.getCessAmount());
		item.setTaxval(detail.getTaxableValue());
		return item;

	}

	private GetGstr2aStagingCdnaInvoicesHeaderEntity setInvoiceCdnaData(
			CDNRInvoices eachInv, CreditDebitNote creddebnote,
			Gstr1GetInvoicesReqDto dto, Long batchId) {
		GetGstr2aStagingCdnaInvoicesHeaderEntity invoice = new GetGstr2aStagingCdnaInvoicesHeaderEntity();
		String cfs = eachInv.getCounFillStatus();
		String sgstin = eachInv.getCpGstin();
		String checkSum = creddebnote.getCheckSum();
		String credDebRefVoucher = creddebnote.getCredDebRefVoucher();
		String credDebRefVoucherNum = creddebnote.getCredDebRefVoucherNum();
		String oriCredDebNum = creddebnote.getOriCredDebNum();
		String credDebRefVoucherDate = creddebnote.getCredDebRefVoucherDate();
		String oriCredDebDate = creddebnote.getOriCredDebDate();
		String invNum = creddebnote.getInvNum();
		String invoiceType = creddebnote.getInvoiceType();
		String preGst = creddebnote.getPreGstRegNote();
		BigDecimal diffPercent = creddebnote.getDiffPercent();
		BigDecimal noteValue = creddebnote.getTotalNoteVal();

		String cgstin = dto.getGstin();
		invoice.setTaxPeriod(dto.getReturnPeriod());

		if (dto.getReturnPeriod() != null
				&& dto.getReturnPeriod().length() > 0) {
			invoice.setDerReturnPeriod(
					GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
		}
		// adding inv_key
		String docType = invoiceType == "R" ? "INV"
				: (invoiceType == "C" ? "CR"
						: (invoiceType == "D" ? "DR"
								: (invoiceType == "B" ? "BOS" : invoiceType)));
		String fy = GenUtil.getFinYear(LocalDate.parse(credDebRefVoucherDate,
				DateUtil.SUPPORTED_DATE_FORMAT2));

		InwardTransDocument inwardDoc = new InwardTransDocument();
		inwardDoc.setFinYear(fy);
		inwardDoc.setDocNo(credDebRefVoucherNum);
		inwardDoc.setDocType(docType);
		inwardDoc.setSgstin(sgstin);// output
		inwardDoc.setCgstin(dto.getGstin());// input

		String generateKey = docKeyGenerator.generateKey(inwardDoc);
		// String generateKey = docKeyGenerator.generateKey(sgstin,
		// credDebRefVoucherNum, fy, docType);
		invoice.setInvKey(generateKey);
		
		if(invoiceType!=null && (invoiceType.equalsIgnoreCase("C") ||invoiceType.equalsIgnoreCase("RCR") ))
		{
		invoice.setLnkingDocKey(deriveLinkingKey(LocalDate.parse(credDebRefVoucherDate,
				DateUtil.SUPPORTED_DATE_FORMAT2),
				dto.getGstin(), sgstin, "RCR", credDebRefVoucherNum));
		}else if(invoiceType!=null && (invoiceType.equalsIgnoreCase("D") ||invoiceType.equalsIgnoreCase("RDR") ))
		{

			invoice.setLnkingDocKey(deriveLinkingKey(LocalDate.parse(credDebRefVoucherDate,
					DateUtil.SUPPORTED_DATE_FORMAT2),
					dto.getGstin(), sgstin, "RDR", credDebRefVoucherNum));
		}

		
		LocalDateTime convertNow = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());
		invoice.setCreatedOn(convertNow);
		invoice.setCreatedBy("SYSTEM");
		/**
		 * setting CDNInvoices
		 */
		if (APIConstants.SUBMITTED.equalsIgnoreCase(cfs)) {
			cfs = APIConstants.Y;
		} else if (APIConstants.NOT_SUBMITTED.equalsIgnoreCase(cfs)) {
			cfs = APIConstants.N;
		}
		invoice.setCfsGstr1(cfs);
		invoice.setGstin(sgstin);
		invoice.setCountergstin(cgstin);
		/**
		 * setting CredDebNotes
		 */
		invoice.setCheckSum(checkSum);
		invoice.setCredDebRefVoucher(credDebRefVoucher);
		invoice.setCredDebRefVoucherNum(credDebRefVoucherNum);
		invoice.setOriCredDebNum(oriCredDebNum);
		if (credDebRefVoucherDate != null
				&& credDebRefVoucherDate.trim().length() > 0) {
			invoice.setCredDebRefVoucherDate(
					LocalDate.parse(credDebRefVoucherDate,
							DateTimeFormatter.ofPattern("dd-MM-yyyy")));
		} else {
			invoice.setCredDebRefVoucherDate(null);
		}

		if (oriCredDebDate != null && oriCredDebDate.trim().length() > 0) {
			invoice.setOriCredDebDate(LocalDate.parse(oriCredDebDate,
					DateTimeFormatter.ofPattern("dd-MM-yyyy")));
		} else {
			invoice.setOriCredDebDate(null);
		}

		invoice.setInvNum(invNum);
		invoice.setInvType(invoiceType);
		invoice.setNotevalue(noteValue);
		invoice.setPreGst(preGst);
		invoice.setDiffvalue(diffPercent);

		invoice.setCdnBatchIdGstr2a(batchId);
		invoice.setDelete(false);
		// if (gstnapi.isDelinkingEligible(APIConstants.GSTR2A.toUpperCase())) {
		// version 2.0 changes here...
		String cfsGstr3b = eachInv.getCfsGstr3b();
		String cancelDate = eachInv.getCancelDate();
		String filedDate = eachInv.getFiledDate();
		String filedPeriod = eachInv.getFiledPeriod();
		String orgInvPeriod = creddebnote.getOrgInvPeriod();
		String orgInvType = creddebnote.getOrgInvType();
		String pos = creddebnote.getPos();
		String rcrg = creddebnote.getReverseCharge();
		String invType = creddebnote.getInvoiceType();
		String dLinkFlag = creddebnote.getDelinkStatus();

		invoice.setCfsGstr3B(cfsGstr3b);
		if (cancelDate != null) {
			invoice.setCancelDate(LocalDate.parse(cancelDate,
					DateUtil.SUPPORTED_DATE_FORMAT7));
		}
		if (filedDate != null) {
			invoice.setFileDate(LocalDate.parse(filedDate,
					DateUtil.SUPPORTED_DATE_FORMAT7));
		}
		invoice.setFilePeriod(filedPeriod);
		invoice.setOrgInvAmdPeriod(orgInvPeriod);
		invoice.setOrgInvAmdType(orgInvType);
		invoice.setPos(pos);
		invoice.setRcrg(rcrg);
		invoice.setDLinkFlag(dLinkFlag);
		// }

		return invoice;

	}
	
	private String deriveLinkingKey(LocalDate date, String cgstin,
			String sgstin, String docType, String documentNumber) {
		String finYear = GenUtil.getFinYear(date);

		return new StringJoiner(DOC_KEY_JOINER).add(finYear).add(cgstin)
				.add(sgstin).add(docType).add(documentNumber).toString();
	}
	

}
