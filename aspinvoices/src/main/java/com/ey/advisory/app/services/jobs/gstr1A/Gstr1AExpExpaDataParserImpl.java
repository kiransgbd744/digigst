package com.ey.advisory.app.services.jobs.gstr1A;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.gstr1A.entities.client.GetGstr1AExpHeaderEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.GetGstr1AExpItemEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.GetGstr1AExpaHeaderEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.GetGstr1AExpaItemEntity;
import com.ey.advisory.app.docs.dto.EXPInvoiceData;
import com.ey.advisory.app.docs.dto.EXPInvoices;
import com.ey.advisory.app.docs.dto.EXPLineItem;
import com.ey.advisory.app.services.common.Anx1DocKeyGenerator;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

@Component("gstr1AExpExpaDataParserImpl")
@Slf4j
public class Gstr1AExpExpaDataParserImpl implements Gstr1AExpExpaDataParser {

	@Autowired
	private Anx1DocKeyGenerator docKeyGenerator;

	@Override
	public List<GetGstr1AExpHeaderEntity> parseExpData(
			Gstr1GetInvoicesReqDto dto, String apiResp) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray respObject = null;
		List<GetGstr1AExpHeaderEntity> headerList = new ArrayList<>();

		// try {

		respObject = (new JsonParser()).parse(apiResp).getAsJsonObject()
				.get(APIConstants.EXP).getAsJsonArray();

		Type listType = new TypeToken<List<EXPInvoices>>() {
		}.getType();

		// List of Header and Item Data
		List<EXPInvoices> baseEntity = gson.fromJson(respObject, listType);

		for (EXPInvoices eachInv : baseEntity) {

			for (EXPInvoiceData eachInvData : eachInv.getExpInvoiceData()) {

				BigDecimal taxRate = BigDecimal.ZERO;
				BigDecimal taxValue = BigDecimal.ZERO;
				BigDecimal igstAmt = BigDecimal.ZERO;
				BigDecimal cessAmt = BigDecimal.ZERO;
				List<GetGstr1AExpItemEntity> lineItems = new ArrayList<>();

				if (eachInvData.getLineItems() != null) {
					for (EXPLineItem eachInvDa : eachInvData.getLineItems()) {

						// New Item Entity
						GetGstr1AExpItemEntity item = new GetGstr1AExpItemEntity();
						/**
						 * EXPItemDetails data
						 */
						item.setReturnPeriod(dto.getReturnPeriod());
						if (dto.getReturnPeriod() != null
								&& dto.getReturnPeriod().length() > 0) {
							item.setDerivedTaxperiod(
									GenUtil.convertTaxPeriodToInt(
											dto.getReturnPeriod()));
						}
						item.setTaxRate(eachInvDa.getRate());
						item.setTaxVal(eachInvDa.getTaxableValue());
						item.setIgstAmt(eachInvDa.getIgstAmount());
						item.setCessAmt(eachInvDa.getCessAmount());
						item.setInvValue(eachInvData.getInvoiceValue());

						lineItems.add(item);
						// Header Amounts count
						if (item.getTaxRate() != null) {
							taxRate = taxRate.add(item.getTaxRate());
						}
						if (item.getTaxVal() != null) {
							taxValue = taxValue.add(item.getTaxVal());
						}
						if (item.getIgstAmt() != null) {

							igstAmt = igstAmt.add(item.getIgstAmt());
						}

						if (item.getCessAmt() != null) {
							cessAmt = cessAmt.add(item.getCessAmt());
						}

					}
					// New Header Entity
					GetGstr1AExpHeaderEntity header = new GetGstr1AExpHeaderEntity();
					/**
					 * Input data
					 */
					header.setGstin(dto.getGstin());
					header.setReturnPeriod(dto.getReturnPeriod());
					if (dto.getReturnPeriod() != null
							&& dto.getReturnPeriod().length() > 0) {
						header.setDerivedTaxperiod(GenUtil
								.convertTaxPeriodToInt(dto.getReturnPeriod()));
					}
					header.setBatchId(dto.getBatchId());

					/**
					 * ExpInvoices data
					 */
					header.setInvChksum(eachInvData.getCheckSum());
					header.setDiffPercent(eachInvData.getDiffPercent());
					header.setFlag(eachInvData.getInvoiceStatus());
					header.setExportType(eachInv.getExportType());
					header.setInvNum(eachInvData.getInvoiceNum());
					String invDate = eachInvData.getInvoiceDate();
					if (invDate != null) {
						header.setInvDate(String.format(invDate,
								DateUtil.SUPPORTED_DATE_FORMAT2));
					}
					header.setInvValue(eachInvData.getInvoiceValue());
					header.setShipBillPortCode(
							eachInvData.getShipBillPortCode());
					header.setShipBillNum(eachInvData.getShipBillNum());
					String shipBillDate = eachInvData.getShipBillDate();
					if (shipBillDate != null) {
						header.setShipBillDate(String.format(shipBillDate,
								DateUtil.SUPPORTED_DATE_FORMAT2));
					}
					header.setIrnNum(eachInvData.getIrn());
					String irnDate = eachInvData.getIrngendate();
					if (irnDate != null) {
						header.setIrnGenDate(String.format(irnDate,
								DateUtil.SUPPORTED_DATE_FORMAT2));
					}
					header.setIrnSrcType(eachInvData.getSrctyp());

					// taxes at header level by summing the item values

					header.setTaxValue(taxValue);
					header.setIgstAmt(igstAmt);
					header.setCessAmt(cessAmt);

					LocalDateTime convertNow = EYDateUtil
							.toUTCDateTimeFromLocal(LocalDateTime.now());
					header.setCreatedOn(convertNow);
					header.setCreatedBy("SYSTEM");

					String docDate = eachInvData.getInvoiceDate();
					String docNum = eachInvData.getInvoiceNum();
					String sgtin = dto.getGstin();
					String docType = dto.getType();
					String finYear = GenUtil.getFinYear(LocalDate.parse(docDate,
							DateUtil.SUPPORTED_DATE_FORMAT2));
					header.setDocKey(docKeyGenerator.generateKey(sgtin, docNum,
							finYear, docType));

					header.setLineItems(lineItems);
					lineItems.forEach(item -> {
						item.setDocument(header);
					});

					headerList.add(header);
				}
			}
		}
		/*
		 * } catch (Exception ex) { String msg =
		 * "failed to parse Gstr1 EXP response"; LOGGER.error(msg, ex); throw
		 * new APIException(msg); }
		 */
		return headerList;
	}

	@Override
	public List<GetGstr1AExpaHeaderEntity> parseExpaData(
			Gstr1GetInvoicesReqDto dto, String apiResp) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray respObject = null;
		List<GetGstr1AExpaHeaderEntity> headerList = new ArrayList<>();

		// try {

		respObject = (new JsonParser()).parse(apiResp).getAsJsonObject()
				.get(APIConstants.EXPA).getAsJsonArray();

		Type listType = new TypeToken<List<EXPInvoices>>() {
		}.getType();

		// List of Header and Item Data
		List<EXPInvoices> baseEntity = gson.fromJson(respObject, listType);

		for (EXPInvoices eachInv : baseEntity) {

			for (EXPInvoiceData eachInvData : eachInv.getExpInvoiceData()) {

				BigDecimal taxRate = BigDecimal.ZERO;
				BigDecimal taxValue = BigDecimal.ZERO;
				BigDecimal igstAmt = BigDecimal.ZERO;
				BigDecimal cessAmt = BigDecimal.ZERO;
				List<GetGstr1AExpaItemEntity> lineItems = new ArrayList<>();

				if (eachInvData.getLineItems() != null) {
					for (EXPLineItem eachInvDa : eachInvData.getLineItems()) {

						// New Item Entity
						GetGstr1AExpaItemEntity item = new GetGstr1AExpaItemEntity();
						/**
						 * EXPAItemDetails data
						 */
						item.setReturnPeriod(dto.getReturnPeriod());
						if (dto.getReturnPeriod() != null
								&& dto.getReturnPeriod().length() > 0) {
							item.setDerivedTaxperiod(
									GenUtil.convertTaxPeriodToInt(
											dto.getReturnPeriod()));
						}
						item.setTaxRate(eachInvDa.getRate());
						item.setTaxVal(eachInvDa.getTaxableValue());
						item.setIgstAmt(eachInvDa.getIgstAmount());
						item.setCessAmt(eachInvDa.getCessAmount());
						item.setInvVal(eachInvData.getInvoiceValue());

						lineItems.add(item);
						// Header Amounts count
						if (item.getTaxRate() != null) {
							taxRate = taxRate.add(item.getTaxRate());
						}
						if (item.getTaxVal() != null) {
							taxValue = taxValue.add(item.getTaxVal());
						}
						if (item.getIgstAmt() != null) {

							igstAmt = igstAmt.add(item.getIgstAmt());
						}

						if (item.getCessAmt() != null) {
							cessAmt = cessAmt.add(item.getCessAmt());
						}

					}
					// New Header Entity
					GetGstr1AExpaHeaderEntity header = new GetGstr1AExpaHeaderEntity();
					/**
					 * Input data
					 */
					header.setGstin(dto.getGstin());
					header.setReturnPeriod(dto.getReturnPeriod());
					if (dto.getReturnPeriod() != null
							&& dto.getReturnPeriod().length() > 0) {
						header.setDerivedTaxperiod(GenUtil
								.convertTaxPeriodToInt(dto.getReturnPeriod()));
					}
					header.setBatchId(dto.getBatchId());

					/**
					 * ExpInvoices data
					 */
					header.setInvChksum(eachInvData.getCheckSum());
					header.setDiffPercent(eachInvData.getDiffPercent());
					header.setFlag(eachInvData.getInvoiceStatus());
					header.setExportType(eachInv.getExportType());
					header.setInvNum(eachInvData.getInvoiceNum());
					String invDate = eachInvData.getInvoiceDate();
					if (invDate != null) {
						header.setInvDate(String.format(invDate,
								DateUtil.SUPPORTED_DATE_FORMAT2));
					}
					header.setInvValue(eachInvData.getInvoiceValue());
					header.setShipBillPortCode(
							eachInvData.getShipBillPortCode());
					header.setShipBillNum(eachInvData.getShipBillNum());
					String shipBillDate = eachInvData.getShipBillDate();
					if (shipBillDate != null) {
						header.setShipBillDate(String.format(shipBillDate,
								DateUtil.SUPPORTED_DATE_FORMAT2));
					}

					header.setCtin(eachInvData.getCouPartyGstn());

					// taxes at header level by summing the item values

					header.setTaxValue(taxValue);
					header.setIgstAmt(igstAmt);
					header.setCessAmt(cessAmt);
					header.setOrgInvNum(eachInvData.getOinum());
					header.setOrgInvDate(String.format(eachInvData.getOidt(),
							DateUtil.SUPPORTED_DATE_FORMAT2));
					LocalDateTime convertNow = EYDateUtil
							.toUTCDateTimeFromLocal(LocalDateTime.now());
					header.setCreatedOn(convertNow);
					header.setCreatedBy("SYSTEM");

					String docDate = eachInvData.getInvoiceDate();
					String docNum = eachInvData.getInvoiceNum();
					String sgtin = dto.getGstin();
					String docType = dto.getType();
					String finYear = GenUtil.getFinYear(LocalDate.parse(docDate,
							DateUtil.SUPPORTED_DATE_FORMAT2));
					header.setDocKey(docKeyGenerator.generateKey(sgtin, docNum,
							finYear, docType));

					header.setLineItems(lineItems);
					lineItems.forEach(item -> {
						item.setDocument(header);
					});

					headerList.add(header);
				}
			}
		}
		/*
		 * } catch (Exception ex) { String msg =
		 * "failed to parse Gstr1 EXPA response"; LOGGER.error(msg, ex); throw
		 * new APIException(msg); }
		 */
		return headerList;
	}
}