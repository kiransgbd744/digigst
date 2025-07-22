package com.ey.advisory.app.services.jobs.gstr1;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.GetGstr1B2clHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1B2clItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1B2claHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1B2claItemEntity;
import com.ey.advisory.app.docs.dto.B2CLInvoiceData;
import com.ey.advisory.app.docs.dto.B2CLInvoices;
import com.ey.advisory.app.docs.dto.B2CLLineItem;
import com.ey.advisory.app.docs.dto.B2clLineItemDetail;
import com.ey.advisory.app.services.common.Anx1DocKeyGenerator;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Umesha.M
 *
 */
@Component("gstr1B2clB2claDataParserImpl")
@Slf4j
public class Gstr1B2clB2claDataParserImpl implements Gstr1B2clB2claDataParser {

	@Autowired
	private Anx1DocKeyGenerator docKeyGenerator;

	@Override
	public List<GetGstr1B2clHeaderEntity> parseB2clData(Gstr1GetInvoicesReqDto dto, String apiResp) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray respObject = null;
		List<GetGstr1B2clHeaderEntity> headerList = new ArrayList<>();

		// try {

		respObject = (new JsonParser()).parse(apiResp).getAsJsonObject().get(APIConstants.B2CL).getAsJsonArray();

		Type listType = new TypeToken<List<B2CLInvoices>>() {
		}.getType();

		// List of Header and Item Data
		List<B2CLInvoices> baseEntity = gson.fromJson(respObject, listType);

		for (B2CLInvoices eachInv : baseEntity) {

			for (B2CLInvoiceData eachInvData : eachInv.getB2CLInvoiceData()) {

				BigDecimal taxRate = BigDecimal.ZERO;
				BigDecimal taxValue = BigDecimal.ZERO;
				BigDecimal igstAmt = BigDecimal.ZERO;
				BigDecimal cessAmt = BigDecimal.ZERO;
				List<GetGstr1B2clItemEntity> lineItems = new ArrayList<>();

				if (eachInvData.getLineItems() != null) {

					for (B2CLLineItem b2cItems : eachInvData.getLineItems()) {

						B2clLineItemDetail b2cItem = b2cItems.getItemDetail();
						// New Item Entity
						GetGstr1B2clItemEntity item = new GetGstr1B2clItemEntity();
						/**
						 * B2CLItemDetails data
						 */

						item.setReturnPeriod(dto.getReturnPeriod());
						if (dto.getReturnPeriod() != null && dto.getReturnPeriod().length() > 0) {
							item.setDerivedTaxperiod(GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
						}
						item.setTaxRate(b2cItem.getRate());
						item.setTaxValue(b2cItem.getTaxableValue());
						item.setIgstAmt(b2cItem.getIgstAmount());
						item.setCessAmt(b2cItem.getCessAmount());
						item.setInvValue(eachInvData.getInvoiceValue());
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
					GetGstr1B2clHeaderEntity header = new GetGstr1B2clHeaderEntity();
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
					 * B2clInvoices data
					 */

					header.setChksum(eachInvData.getCheckSum());
					header.setPos(eachInv.getPointOfSupply());
					header.setEtin(eachInvData.getEcomTin());
					header.setDiffPercent(eachInvData.getDiffPercent());
					header.setFlag(eachInvData.getInvoiceStatus());
					header.setInvNum(eachInvData.getInvoiceNum());
					String invDate = eachInvData.getInvoiceDate();
					if (invDate != null) {
						header.setInvDate(String.format(invDate, DateUtil.SUPPORTED_DATE_FORMAT2));
					}
					header.setInvValue(eachInvData.getInvoiceValue());
					header.setInvType(eachInvData.getInvoiceType());
					header.setEtin(eachInvData.getEcomTin());
					LocalDateTime convertNow = EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now());
					header.setCreatedOn(convertNow);
					header.setCreatedBy("SYSTEM");

					// taxes at header level by summing the item values
					header.setTaxValue(taxValue);
					header.setIgstAmt(igstAmt);
					header.setCessAmt(cessAmt);

					String docDate = eachInvData.getInvoiceDate();
					String docNum = eachInvData.getInvoiceNum();
					String sgtin = dto.getGstin();
					String docType = eachInvData.getInvoiceType();
					String finYear = GenUtil.getFinYear(LocalDate.parse(docDate, DateUtil.SUPPORTED_DATE_FORMAT2));
					header.setDocKey(docKeyGenerator.generateKey(sgtin, docNum, finYear, docType));

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
		 * "failed to parse Gstr1 B2cl response"; LOGGER.error(msg, ex); throw
		 * new APIException(msg); }
		 */
		return headerList;
	}

	@Override
	public List<GetGstr1B2claHeaderEntity> parseB2claData(Gstr1GetInvoicesReqDto dto, String apiResp) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray respObject = null;
		List<GetGstr1B2claHeaderEntity> headerList = new ArrayList<>();

		// try {

		respObject = (new JsonParser()).parse(apiResp).getAsJsonObject().get(APIConstants.B2CLA).getAsJsonArray();

		Type listType = new TypeToken<List<B2CLInvoices>>() {
		}.getType();

		// List of Header and Item Data
		List<B2CLInvoices> baseEntity = gson.fromJson(respObject, listType);

		for (B2CLInvoices eachInv : baseEntity) {

			for (B2CLInvoiceData eachInvData : eachInv.getB2CLInvoiceData()) {

				BigDecimal taxRate = BigDecimal.ZERO;
				BigDecimal taxValue = BigDecimal.ZERO;
				BigDecimal igstAmt = BigDecimal.ZERO;
				BigDecimal cessAmt = BigDecimal.ZERO;
				List<GetGstr1B2claItemEntity> lineItems = new ArrayList<>();

				if (eachInvData.getLineItems() != null) {
					for (B2CLLineItem b2cItems : eachInvData.getLineItems()) {

						B2clLineItemDetail b2cItem = b2cItems.getItemDetail();
						// New Item Entity
						GetGstr1B2claItemEntity item = new GetGstr1B2claItemEntity();
						/**
						 * B2CLAItemDetails data
						 */

						item.setReturnPeriod(dto.getReturnPeriod());
						if (dto.getReturnPeriod() != null && dto.getReturnPeriod().length() > 0) {
							item.setDerivedTaxperiod(GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
						}
						item.setTaxRate(b2cItem.getRate());
						item.setTaxValue(b2cItem.getTaxableValue());
						item.setIgstAmt(b2cItem.getIgstAmount());
						item.setCessAmt(b2cItem.getCessAmount());
						item.setInvValue(eachInvData.getInvoiceValue());
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
					GetGstr1B2claHeaderEntity header = new GetGstr1B2claHeaderEntity();
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
					 * B2claInvoices data
					 */

					header.setChksum(eachInvData.getCheckSum());
					header.setPos(eachInv.getPointOfSupply());
					header.setEtin(eachInvData.getEcomTin());
					header.setDiffPercent(eachInvData.getDiffPercent());
					header.setFlag(eachInvData.getInvoiceStatus());
					header.setInvNum(eachInvData.getInvoiceNum());
					String invDate = eachInvData.getInvoiceDate();
					if (invDate != null) {
						header.setInvDate(String.format(invDate, DateUtil.SUPPORTED_DATE_FORMAT2));
					}
					header.setInvValue(eachInvData.getInvoiceValue());
					header.setInvType(eachInvData.getInvoiceType());
					header.setEtin(eachInvData.getEcomTin());
					LocalDateTime convertNow = EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now());
					header.setCreatedOn(convertNow);
					header.setCreatedBy("SYSTEM");

					// taxes at header level by summing the item values
					header.setTaxValue(taxValue);
					header.setIgstAmt(igstAmt);
					header.setCessAmt(cessAmt);
					header.setOrgInvNum(eachInvData.getOinum());
					header.setOrgInvDate(LocalDate.parse(eachInvData.getOidt(), DateUtil.SUPPORTED_DATE_FORMAT2));
					String docDate = eachInvData.getInvoiceDate();
					String docNum = eachInvData.getInvoiceNum();
					String sgtin = dto.getGstin();
					String docType = eachInvData.getInvoiceType();
					String finYear = GenUtil.getFinYear(LocalDate.parse(docDate, DateUtil.SUPPORTED_DATE_FORMAT2));
					header.setDocKey(docKeyGenerator.generateKey(sgtin, docNum, finYear, docType));

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
		 * "failed to parse Gstr1 B2cla response"; LOGGER.error(msg, ex); throw
		 * new APIException(msg); }
		 */
		return headerList;
	}
}
