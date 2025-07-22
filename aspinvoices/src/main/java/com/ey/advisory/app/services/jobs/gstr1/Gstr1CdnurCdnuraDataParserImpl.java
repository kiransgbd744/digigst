package com.ey.advisory.app.services.jobs.gstr1;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.GetGstr1CdnurHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1CdnurItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1CdnuraHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1CdnuraItemEntity;
import com.ey.advisory.app.docs.dto.CDNURInvoices;
import com.ey.advisory.app.docs.dto.CdnurLineItem;
import com.ey.advisory.app.docs.dto.CdnurLineItemDetail;
import com.ey.advisory.app.services.common.Anx1DocKeyGenerator;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.service.GstnApi;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Component("gstr1CdnurCdnuraDataParserImpl")
@Slf4j
public class Gstr1CdnurCdnuraDataParserImpl implements Gstr1CdnurCdnuraDataParser {

	@Autowired
	private GstnApi gstnapi;

	@Autowired
	private Anx1DocKeyGenerator docKeyGenerator;

	@Override
	public List<GetGstr1CdnurHeaderEntity> parseCdnurData(Gstr1GetInvoicesReqDto dto, String apiResp) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray respObject = null;
		List<GetGstr1CdnurHeaderEntity> headerList = new ArrayList<>();

		// try {

		respObject = (new JsonParser()).parse(apiResp).getAsJsonObject().get(APIConstants.CDNUR).getAsJsonArray();

		Type listType = new TypeToken<List<CDNURInvoices>>() {
		}.getType();

		// List of Header and Item Data
		List<CDNURInvoices> baseEntity = gson.fromJson(respObject, listType);

		for (CDNURInvoices eachInv : baseEntity) {

			BigDecimal taxRate = BigDecimal.ZERO;
			BigDecimal taxValue = BigDecimal.ZERO;
			BigDecimal igstAmt = BigDecimal.ZERO;
			BigDecimal cessAmt = BigDecimal.ZERO;
			List<GetGstr1CdnurItemEntity> lineItems = new ArrayList<>();
			if (eachInv.getCdnrLineItem() != null) {

				for (CdnurLineItem b2cItems : eachInv.getCdnrLineItem()) {

					CdnurLineItemDetail b2cItem = b2cItems.getItemDetail();
					// New Item Entity
					GetGstr1CdnurItemEntity item = new GetGstr1CdnurItemEntity();
					/**
					 * CDNURItemDetails data
					 */

					item.setReturnPeriod(dto.getReturnPeriod());
					if (dto.getReturnPeriod() != null && dto.getReturnPeriod().length() > 0) {
						item.setDerivedTaxperiod(GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
					}
					item.setTaxRate(b2cItem.getRate());
					item.setTaxValue(b2cItem.getTaxableValue());
					item.setIgstAmt(b2cItem.getIgstAmount());
					item.setInvValue(eachInv.getTotalNoteVal());
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

					if (item.getCessAmt() != null) {
						cessAmt = cessAmt.add(item.getCessAmt());
					}

				}
				// New Header Entity
				GetGstr1CdnurHeaderEntity header = new GetGstr1CdnurHeaderEntity();
				/**
				 * Input data
				 */
				header.setGstin(dto.getGstin());
				header.setReturnPeriod(dto.getReturnPeriod());
				if (dto.getReturnPeriod() != null && dto.getReturnPeriod().length() > 0) {
					header.setDerivedTaxperiod(GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
				}
				header.setBatchId(dto.getBatchId());

				/**
				 * CdnurInvoices data
				 */

				header.setInvChkSum(eachInv.getCheckSum());
				header.setDiffPercent(eachInv.getDiffPercent());
				header.setFlag(eachInv.getInvoiceStatus());
				header.setType(eachInv.getType());
				header.setNoteType(eachInv.getCredDebRefVoucher());
				String noteDate = eachInv.getCredDebRefVoucherDate();
				if (noteDate != null) {
					header.setNoteDate(String.format(noteDate, DateUtil.SUPPORTED_DATE_FORMAT2));
				}
				header.setNoteNum(eachInv.getCredDebRefVoucherNum());
				header.setInvNum(eachInv.getInvNum());
				header.setInvValue(eachInv.getTotalNoteVal());
				header.setIrnNum(eachInv.getIrn());
				String irnDate = eachInv.getIrngendate();
				if (irnDate != null) {
					header.setIrnGenDate(String.format(irnDate, DateUtil.SUPPORTED_DATE_FORMAT2));
				}
				header.setIrnSrcType(eachInv.getSrctyp());
				// if
				// (gstnapi.isDelinkingEligible(APIConstants.GSTR1.toUpperCase()))
				// {
				header.setPos(eachInv.getPos());
				header.setdFlag(eachInv.getDelinkStatus());

				// } else {
				header.setPreGst(eachInv.getPreGstRegNote());
				header.setInvNum(eachInv.getInvNum());

				String invDate = eachInv.getInvDate();
				if (invDate != null) {
					header.setInvDate(String.format(invDate, DateUtil.SUPPORTED_DATE_FORMAT2));
				}
				// }

				// taxes at header level by summing the item values

				header.setTaxValue(taxValue);
				header.setIgstAmt(igstAmt);
				header.setCessAmt(cessAmt);
				LocalDateTime convertNow = EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now());
				header.setCreatedOn(convertNow);
				header.setCreatedBy("SYSTEM");

				String docDate = eachInv.getCredDebRefVoucherDate();
				String docNum = eachInv.getCredDebRefVoucherNum();
				String sgtin = dto.getGstin();
				String docType = eachInv.getCredDebRefVoucher();
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
		 * } catch (
		 * 
		 * Exception ex) { String msg = "failed to parse Gstr1 Cdnur response";
		 * LOGGER.error(msg, ex); throw new APIException(msg); }
		 */
		return headerList;
	}

	@Override
	public List<GetGstr1CdnuraHeaderEntity> parseCdnuraData(Gstr1GetInvoicesReqDto dto, String apiResp) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray respObject = null;
		List<GetGstr1CdnuraHeaderEntity> headerList = new ArrayList<>();

		// try {

		respObject = (new JsonParser()).parse(apiResp).getAsJsonObject().get(APIConstants.CDNURA).getAsJsonArray();

		Type listType = new TypeToken<List<CDNURInvoices>>() {
		}.getType();

		// List of Header and Item Data
		List<CDNURInvoices> baseEntity = gson.fromJson(respObject, listType);

		for (CDNURInvoices eachInv : baseEntity) {

			BigDecimal taxRate = BigDecimal.ZERO;
			BigDecimal taxValue = BigDecimal.ZERO;
			BigDecimal igstAmt = BigDecimal.ZERO;
			BigDecimal cessAmt = BigDecimal.ZERO;
			List<GetGstr1CdnuraItemEntity> lineItems = new ArrayList<>();
			if (eachInv.getCdnrLineItem() != null) {

				for (CdnurLineItem b2cItems : eachInv.getCdnrLineItem()) {

					CdnurLineItemDetail b2cItem = b2cItems.getItemDetail();

					// New Item Entity
					GetGstr1CdnuraItemEntity item = new GetGstr1CdnuraItemEntity();
					/**
					 * CDNURAItemDetails data
					 */

					item.setReturnPeriod(dto.getReturnPeriod());
					if (dto.getReturnPeriod() != null && dto.getReturnPeriod().length() > 0) {
						item.setDerivedTaxperiod(GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
					}
					item.setTaxRate(b2cItem.getRate());
					item.setTaxValue(b2cItem.getTaxableValue());
					item.setInvValue(eachInv.getTotalNoteVal());
					item.setIgstAmt(b2cItem.getIgstAmount());
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

					if (item.getCessAmt() != null) {
						cessAmt = cessAmt.add(item.getCessAmt());
					}

				}
				// New Header Entity
				GetGstr1CdnuraHeaderEntity header = new GetGstr1CdnuraHeaderEntity();
				/**
				 * Input data
				 */
				header.setGstin(dto.getGstin());
				header.setReturnPeriod(dto.getReturnPeriod());
				if (dto.getReturnPeriod() != null && dto.getReturnPeriod().length() > 0) {
					header.setDerivedTaxperiod(GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
				}
				header.setBatchId(dto.getBatchId());

				/**
				 * CdnuraInvoices data
				 */

				header.setInvChkSum(eachInv.getCheckSum());
				header.setDiffPercent(eachInv.getDiffPercent());
				header.setFlag(eachInv.getInvoiceStatus());
				header.setType(eachInv.getType());
				header.setInvNum(eachInv.getInvNum());
				header.setNoteNum(eachInv.getCredDebRefVoucherNum());
				header.setNoteType(eachInv.getCredDebRefVoucher());
				String noteDate = eachInv.getCredDebRefVoucherDate();
				if (noteDate != null) {
					header.setNoteDate(String.format(noteDate, DateUtil.SUPPORTED_DATE_FORMAT2));
				}
				header.setInvValue(eachInv.getTotalNoteVal());

				// if
				// (gstnapi.isDelinkingEligible(APIConstants.GSTR1.toUpperCase()))
				// {
				header.setPos(eachInv.getPos());

				// } else {
				header.setPreGst(eachInv.getPreGstRegNote());
				header.setInvNum(eachInv.getInvNum());
				String invDate = eachInv.getInvDate();
				if (invDate != null) {
					header.setInvDate(String.format(invDate, DateUtil.SUPPORTED_DATE_FORMAT2));
				}
				header.setOrgNoteNum(eachInv.getOriCredDebNum());

				String orgInvDate = eachInv.getOriCredDebDate();
				if (orgInvDate != null) {
					header.setOrgNoteDate(LocalDate.parse(orgInvDate, DateUtil.SUPPORTED_DATE_FORMAT2));
				}

				// }

				// taxes at header level by summing the item values

				header.setTaxValue(taxValue);
				header.setIgstAmt(igstAmt);
				header.setCessAmt(cessAmt);
				LocalDateTime convertNow = EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now());
				header.setCreatedOn(convertNow);
				header.setCreatedBy("SYSTEM");

				String docDate = eachInv.getOriCredDebDate();
				String docNum = eachInv.getOriCredDebNum();
				String sgtin = dto.getGstin();
				String docType = eachInv.getCredDebRefVoucher();
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
		 * "failed to parse Gstr1 Cdnura response"; LOGGER.error(msg, ex); throw
		 * new APIException(msg); }
		 */
		return headerList;
	}
}
