package com.ey.advisory.app.services.jobs.gstr1A;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.gstr1A.entities.client.GetGstr1ACdnrHeaderEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.GetGstr1ACdnrItemEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.GetGstr1ACdnraHeaderEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.GetGstr1ACdnraItemEntity;
import com.ey.advisory.app.docs.dto.CDNRInvoices;
import com.ey.advisory.app.docs.dto.CdnrLineItem;
import com.ey.advisory.app.docs.dto.CdnrLineItemDetail;
import com.ey.advisory.app.docs.dto.CreditDebitNote;
import com.ey.advisory.app.services.common.Anx1DocKeyGenerator;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.service.GstnApi;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

@Component("gstr1ACdnrCdnraDataParserImpl")
@Slf4j
public class Gstr1ACdnrCdnraDataParserImpl implements Gstr1ACdnrCdnraDataParser {

	@Autowired
	private GstnApi gstnapi;

	@Autowired
	private Anx1DocKeyGenerator docKeyGenerator;

	@Override
	public List<GetGstr1ACdnrHeaderEntity> parseCdnrData(Gstr1GetInvoicesReqDto dto, String apiResp) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray respObject = null;
		List<GetGstr1ACdnrHeaderEntity> headerList = new ArrayList<>();

		// try {

		respObject = (new JsonParser()).parse(apiResp).getAsJsonObject().get(APIConstants.CDNR).getAsJsonArray();

		Type listType = new TypeToken<List<CDNRInvoices>>() {
		}.getType();

		// List of Header and Item Data
		List<CDNRInvoices> baseEntity = gson.fromJson(respObject, listType);

		for (CDNRInvoices eachInv : baseEntity) {

			for (CreditDebitNote eachInvData : eachInv.getCreditDebitNoteDetails()) {

				BigDecimal taxRate = BigDecimal.ZERO;
				BigDecimal taxValue = BigDecimal.ZERO;
				BigDecimal igstAmt = BigDecimal.ZERO;
				BigDecimal cgstAmt = BigDecimal.ZERO;
				BigDecimal sgstAmt = BigDecimal.ZERO;
				BigDecimal cessAmt = BigDecimal.ZERO;
				List<GetGstr1ACdnrItemEntity> lineItems = new ArrayList<>();

				for (CdnrLineItem b2cItems : eachInvData.getCdnrLineItem()) {

					CdnrLineItemDetail b2cItem = b2cItems.getItemDetail();
					// New Item Entity
					GetGstr1ACdnrItemEntity item = new GetGstr1ACdnrItemEntity();
					/**
					 * CDNRItemDetails data
					 */

					item.setReturnPeriod(dto.getReturnPeriod());
					if (dto.getReturnPeriod() != null && dto.getReturnPeriod().length() > 0) {
						item.setDerivedTaxperiod(GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
					}
					item.setTaxRate(b2cItem.getRate());
					item.setTaxValue(b2cItem.getTaxableValue());
					item.setInvValue(eachInvData.getTotalNoteVal());
					item.setIgstAmt(b2cItem.getIgstAmount());
					item.setCgstAmt(b2cItem.getCgstAmount());
					item.setSgstAmt(b2cItem.getSgstAmount());
					item.setCessAmt(b2cItem.getCessAmount());
					item.setSerialNum(b2cItems.getLineNumber());

					lineItems.add(item);

					// Header Amounts count
					if (item.getTaxRate() != null) {
						taxRate = taxRate.add(item.getTaxRate());
					}
					if (item.getTaxValue() != null) {
						taxValue = taxValue.add(item.getTaxValue());
					}
					if (item.getIgstAmt() != null) {

						igstAmt = igstAmt.add(item.getIgstAmt());
					}
					if (item.getCgstAmt() != null) {

						cgstAmt = cgstAmt.add(item.getCgstAmt());
					}
					if (item.getSgstAmt() != null) {

						sgstAmt = sgstAmt.add(item.getSgstAmt());
					}

					if (item.getCessAmt() != null) {
						cessAmt = cessAmt.add(item.getCessAmt());
					}

				}
				// New Header Entity
				GetGstr1ACdnrHeaderEntity header = new GetGstr1ACdnrHeaderEntity();
				/**
				 * Input data
				 */
				header.setActionRequired(APIConstants.N);
				header.setGstin(dto.getGstin());
				header.setReturnPeriod(dto.getReturnPeriod());
				if (dto.getReturnPeriod() != null && dto.getReturnPeriod().length() > 0) {
					header.setDerivedTaxperiod(GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
				}
				header.setBatchId(dto.getBatchId());

				/**
				 * CdnrInvoices data
				 */

				header.setChksum(eachInvData.getCheckSum());
				header.setDiffPercent(eachInvData.getDiffPercent());
				header.setFlag(eachInvData.getInvoiceStatus());
				header.setcFlag(eachInvData.getCPartyFlag());
				header.setOrgPeriod(eachInvData.getOrgPeriod());
				header.setInvUpdBy(eachInvData.getUploadedBy());
				header.setNoteNum(eachInvData.getCredDebRefVoucherNum());
				header.setIrnNum(eachInvData.getIrn());
				String irnDate = eachInvData.getIrngendate();
				if (irnDate != null) {
					header.setIrnGenDate(String.format(irnDate, DateUtil.SUPPORTED_DATE_FORMAT2));
				}
				header.setIrnSrcType(eachInvData.getSrctyp());
				header.setCtin(eachInv.getCpGstin());
				String noteDate = eachInvData.getCredDebRefVoucherDate();
				if (noteDate != null) {
					header.setNoteDate(String.format(noteDate, DateUtil.SUPPORTED_DATE_FORMAT2));
				}

				header.setInvValue(eachInvData.getTotalNoteVal());
				// if
				// (gstnapi.isDelinkingEligible(APIConstants.GSTR1.toUpperCase()))
				// {
				header.setPos(eachInvData.getPos());
				header.setRevCharge(eachInvData.getReverseCharge());
				header.setInvType(eachInvData.getInvoiceType());
				header.setNoteType(eachInvData.getCredDebRefVoucher());
				header.setdFlag(eachInvData.getDelinkStatus());

				// } else {
				header.setPreGst(eachInvData.getPreGstRegNote());
				header.setInvNum(eachInvData.getInvNum());

				String invDate = eachInvData.getInvDate();
				if (invDate != null) {
					header.setInvDate(String.format(invDate, DateUtil.SUPPORTED_DATE_FORMAT2));
				}

				// }

				// taxes at header level by summing the item values

				header.setTaxValue(taxValue);
				header.setIgstAmt(igstAmt);
				header.setCgstAmt(cgstAmt);
				header.setSgstAmt(sgstAmt);
				header.setCessAmt(cessAmt);
				LocalDateTime convertNow = EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now());
				header.setCreatedOn(convertNow);
				header.setCreatedBy("SYSTEM");

				String docDate = eachInvData.getCredDebRefVoucherDate();
				String docNum = eachInvData.getCredDebRefVoucherNum();
				String sgtin = dto.getGstin();
				String docType = eachInvData.getInvoiceType();
				String finYear = null;
				if (docDate != null) {
					finYear = GenUtil.getFinYear(LocalDate.parse(docDate, DateUtil.SUPPORTED_DATE_FORMAT2));
				}

				header.setDocKey(docKeyGenerator.generateKey(sgtin, docNum, finYear, docType));

				header.setLineItems(lineItems);
				lineItems.forEach(item -> {
					item.setDocument(header);
				});

				headerList.add(header);
			}
		}
		/*
		 * } catch (Exception ex) { String msg =
		 * "failed to parse Gstr1 Cdnr response"; LOGGER.error(msg, ex); throw
		 * new APIException(msg); }
		 */
		return headerList;
	}

	@Override
	public List<GetGstr1ACdnraHeaderEntity> parseCdnraData(Gstr1GetInvoicesReqDto dto, String apiResp) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray respObject = null;
		List<GetGstr1ACdnraHeaderEntity> headerList = new ArrayList<>();

		// try {

		respObject = (new JsonParser()).parse(apiResp).getAsJsonObject().get(APIConstants.CDNRA).getAsJsonArray();

		Type listType = new TypeToken<List<CDNRInvoices>>() {
		}.getType();

		// List of Header and Item Data
		List<CDNRInvoices> baseEntity = gson.fromJson(respObject, listType);

		for (CDNRInvoices eachInv : baseEntity) {

			for (CreditDebitNote eachInvData : eachInv.getCreditDebitNoteDetails()) {

				BigDecimal taxRate = BigDecimal.ZERO;
				BigDecimal taxValue = BigDecimal.ZERO;
				BigDecimal igstAmt = BigDecimal.ZERO;
				BigDecimal cgstAmt = BigDecimal.ZERO;
				BigDecimal sgstAmt = BigDecimal.ZERO;
				BigDecimal cessAmt = BigDecimal.ZERO;
				List<GetGstr1ACdnraItemEntity> lineItems = new ArrayList<>();

				for (CdnrLineItem b2cItems : eachInvData.getCdnrLineItem()) {

					CdnrLineItemDetail b2cItem = b2cItems.getItemDetail();
					// New Item Entity
					GetGstr1ACdnraItemEntity item = new GetGstr1ACdnraItemEntity();
					/**
					 * CDNRAItemDetails data
					 */

					item.setReturnPeriod(dto.getReturnPeriod());
					if (dto.getReturnPeriod() != null && dto.getReturnPeriod().length() > 0) {
						item.setDerivedTaxperiod(GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
					}
					item.setTaxRate(b2cItem.getRate());
					item.setTaxValue(b2cItem.getTaxableValue());
					item.setInvValue(eachInvData.getTotalNoteVal());
					item.setIgstAmt(b2cItem.getIgstAmount());
					item.setCgstAmt(b2cItem.getCgstAmount());
					item.setSgstAmt(b2cItem.getSgstAmount());
					item.setCessAmt(b2cItem.getCessAmount());
					item.setSerialNum(b2cItems.getLineNumber());

					lineItems.add(item);

					// Header Amounts count
					if (item.getTaxRate() != null) {
						taxRate = taxRate.add(item.getTaxRate());
					}
					if (item.getTaxValue() != null) {
						taxValue = taxValue.add(item.getTaxValue());
					}
					if (item.getIgstAmt() != null) {

						igstAmt = igstAmt.add(item.getIgstAmt());
					}

					if (item.getCgstAmt() != null) {

						cgstAmt = cgstAmt.add(item.getCgstAmt());
					}
					if (item.getSgstAmt() != null) {

						sgstAmt = sgstAmt.add(item.getSgstAmt());
					}

					if (item.getCessAmt() != null) {
						cessAmt = cessAmt.add(item.getCessAmt());
					}

				}
				// New Header Entity
				GetGstr1ACdnraHeaderEntity header = new GetGstr1ACdnraHeaderEntity();
				/**
				 * Input data
				 */
				header.setActionRequired(APIConstants.N);
				header.setGstin(dto.getGstin());
				header.setReturnPeriod(dto.getReturnPeriod());
				if (dto.getReturnPeriod() != null && dto.getReturnPeriod().length() > 0) {
					header.setDerivedTaxperiod(GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
				}
				header.setBatchId(dto.getBatchId());

				/**
				 * CdnraInvoices data
				 */

				header.setChksum(eachInvData.getCheckSum());
				header.setDiffPercent(eachInvData.getDiffPercent());
				header.setFlag(eachInvData.getInvoiceStatus());
				header.setcFlag(eachInvData.getCPartyFlag());
				header.setCtin(eachInv.getCpGstin());
				header.setOrgPeriod(eachInvData.getOrgPeriod());
				header.setInvUpdBy(eachInvData.getUploadedBy());
				header.setNoteNum(eachInvData.getCredDebRefVoucherNum());
				String noteDate = eachInvData.getCredDebRefVoucherDate();

				if (noteDate != null) {
					header.setNoteDate(noteDate);
				}
				header.setOrgNoteNum(eachInvData.getOriCredDebNum());

				header.setInvValue(eachInvData.getTotalNoteVal());
				// if
				// (gstnapi.isDelinkingEligible(APIConstants.GSTR1.toUpperCase()))
				// {
				header.setPos(eachInvData.getPos());
				header.setRevCharge(eachInvData.getReverseCharge());
				header.setInvType(eachInvData.getInvoiceType());
				header.setNoteType(eachInvData.getCredDebRefVoucher());
				header.setdFlag(eachInvData.getDelinkStatus());

				// } else {
				header.setPreGst(eachInvData.getPreGstRegNote());
				header.setInvNum(eachInvData.getInvNum());
				String invDate = eachInvData.getInvDate();
				if (invDate != null) {
					header.setInvDate(String.format(invDate, DateUtil.SUPPORTED_DATE_FORMAT2));
				}
				header.setOrgNoteNum(eachInvData.getOriCredDebNum());

				String orgInvDate = eachInvData.getOriCredDebDate();
				if (orgInvDate != null) {
					header.setOrgNoteDate(LocalDate.parse(orgInvDate, DateUtil.SUPPORTED_DATE_FORMAT2));
				}

				// }

				// taxes at header level by summing the item values

				header.setTaxValue(taxValue);
				header.setIgstAmt(igstAmt);
				header.setCgstAmt(cgstAmt);
				header.setSgstAmt(sgstAmt);
				header.setCessAmt(cessAmt);
				LocalDateTime convertNow = EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now());
				header.setCreatedOn(convertNow);
				header.setCreatedBy("SYSTEM");

				String docDate = eachInvData.getOriCredDebDate();
				String docNum = eachInvData.getOriCredDebNum();
				String sgtin = dto.getGstin();
				String docType = eachInvData.getInvoiceType();
				String finYear = null;
				if (docDate != null) {
					finYear = GenUtil.getFinYear(LocalDate.parse(docDate, DateUtil.SUPPORTED_DATE_FORMAT2));
				}
				header.setDocKey(docKeyGenerator.generateKey(sgtin, docNum, finYear, docType));

				header.setLineItems(lineItems);
				lineItems.forEach(item -> {
					item.setDocument(header);
				});

				headerList.add(header);
			}
		}
		/*
		 * } catch (Exception ex) { String msg =
		 * "failed to parse Gstr1 Cdnra response"; LOGGER.error(msg, ex); throw
		 * new APIException(msg); }
		 */
		return headerList;
	}
}