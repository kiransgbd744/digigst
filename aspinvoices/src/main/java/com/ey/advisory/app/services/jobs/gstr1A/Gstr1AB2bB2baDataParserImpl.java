package com.ey.advisory.app.services.jobs.gstr1A;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.GetGstr1B2bHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1B2bItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1B2baHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1B2baItemEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.GetGstr1AB2bHeaderEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.GetGstr1AB2bItemEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.GetGstr1AB2baHeaderEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.GetGstr1AB2baItemEntity;
import com.ey.advisory.app.docs.dto.B2BInvoiceData;
import com.ey.advisory.app.docs.dto.B2BInvoices;
import com.ey.advisory.app.docs.dto.B2bLineItem;
import com.ey.advisory.app.docs.dto.B2bLineItemDetail;
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
 * @author Hemasundar.J
 *
 */
@Component("gstr1AB2bB2baDataParserImpl")
@Slf4j
public class Gstr1AB2bB2baDataParserImpl implements Gstr1AB2bB2baDataParser {

	@Autowired
	private Anx1DocKeyGenerator docKeyGenerator;
	
	@Override
	public List<GetGstr1AB2bHeaderEntity> parseB2bData(Gstr1GetInvoicesReqDto dto, String apiResp) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray respObject = null;
		List<GetGstr1AB2bHeaderEntity> headerList = new ArrayList<>();

		// try {

		respObject = (new JsonParser()).parse(apiResp).getAsJsonObject().get(APIConstants.B2B).getAsJsonArray();

		Type listType = new TypeToken<List<B2BInvoices>>() {
		}.getType();

		// List of Header and Item Data
		List<B2BInvoices> baseEntity = gson.fromJson(respObject, listType);

		for (B2BInvoices eachInv : baseEntity) {

			for (B2BInvoiceData eachInvData : eachInv.getB2bInvoiceData()) {

				BigDecimal taxRate = BigDecimal.ZERO;
				BigDecimal taxValue = BigDecimal.ZERO;
				BigDecimal igstAmt = BigDecimal.ZERO;
				BigDecimal cgstAmt = BigDecimal.ZERO;
				BigDecimal sgstAmt = BigDecimal.ZERO;
				BigDecimal cessAmt = BigDecimal.ZERO;
				List<GetGstr1AB2bItemEntity> lineItems = new ArrayList<>();

				if (eachInvData.getLineItems() != null) {

					for (B2bLineItem b2cItems : eachInvData.getLineItems()) {

						B2bLineItemDetail b2cItem = b2cItems.getItemDetail();
						// New Item Entity
						GetGstr1AB2bItemEntity item = new GetGstr1AB2bItemEntity();
						/**
						 * B2BItemDetails data
						 */

						item.setReturnPeriod(dto.getReturnPeriod());
						if (dto.getReturnPeriod() != null && dto.getReturnPeriod().length() > 0) {
							item.setDerivedTaxperiod(GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
						}
						item.setTaxRate(b2cItem.getRate());
						item.setTaxValue(b2cItem.getTaxableValue());
						item.setIgstAmt(b2cItem.getIgstAmount());
						item.setCgstAmt(b2cItem.getCgstAmount());
						item.setSgstAmt(b2cItem.getSgstAmount());
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
					GetGstr1AB2bHeaderEntity header = new GetGstr1AB2bHeaderEntity();
					/**
					 * Input data
					 */
					header.setAction(APIConstants.N);
					header.setGstin(dto.getGstin());
					header.setReturnPeriod(dto.getReturnPeriod());
					if (dto.getReturnPeriod() != null && dto.getReturnPeriod().length() > 0) {
						header.setDerivedTaxperiod(GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
					}
					header.setBatchId(dto.getBatchId());

					/**
					 * B2BInvoices data
					 */

					header.setInvChksum(eachInvData.getCheckSum());
					header.setDiffPercent(eachInvData.getDiffPercent());
					header.setFlag(eachInvData.getInvoiceStatus());
					header.setInvNum(eachInvData.getInvoiceNumber());
					String invDate = eachInvData.getInvoiceDate();
					if (invDate != null) {
						header.setInvDate(String.format(invDate, DateUtil.SUPPORTED_DATE_FORMAT2));
					}
					header.setPos(eachInvData.getPos());
					header.setRchrg(eachInvData.getReverseCharge());
					header.setEtin(eachInvData.getEcomTin());
					header.setInvUpdby(eachInvData.getUpdby());
					header.setCflag(eachInvData.getCflag());
					header.setOrgPeriod(eachInvData.getOpd());
					header.setInvValue(eachInvData.getInvoiceValue());
					header.setInvType(eachInvData.getInvoiceType());
					header.setIrnNum(eachInvData.getIrn());
					String irnDate = eachInvData.getIrngendate();
					if (irnDate != null) {
						header.setIrnGenDate(String.format(irnDate, DateUtil.SUPPORTED_DATE_FORMAT2));
					}
					header.setIrnSrcType(eachInvData.getSrctyp());
					header.setCtin(eachInv.getCgstin());
					LocalDateTime convertNow = EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now());
					header.setCreatedOn(convertNow);
					header.setCreatedBy("SYSTEM");

					// taxes at header level by summing the item values
					header.setTaxValue(taxValue);
					header.setIgstAmt(igstAmt);
					header.setCgstAmt(cgstAmt);
					header.setSgstAmt(sgstAmt);
					header.setCessAmt(cessAmt);

					String docDate = eachInvData.getInvoiceDate();
					String docNum = eachInvData.getInvoiceNumber();
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
		 * "failed to parse Gstr1 B2B response"; LOGGER.error(msg, ex); throw
		 * new APIException(msg); }
		 */
		return headerList;
	}

	@Override
	public List<GetGstr1AB2baHeaderEntity> parseB2baData(Gstr1GetInvoicesReqDto dto, String apiResp) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray respObject = null;
		List<GetGstr1AB2baHeaderEntity> headerList = new ArrayList<>();

		// try {

		respObject = (new JsonParser()).parse(apiResp).getAsJsonObject().get(APIConstants.B2BA).getAsJsonArray();

		Type listType = new TypeToken<List<B2BInvoices>>() {
		}.getType();

		// List of Header and Item Data
		List<B2BInvoices> baseEntity = gson.fromJson(respObject, listType);

		for (B2BInvoices eachInv : baseEntity) {

			for (B2BInvoiceData eachInvData : eachInv.getB2bInvoiceData()) {

				BigDecimal taxRate = BigDecimal.ZERO;
				BigDecimal taxValue = BigDecimal.ZERO;
				BigDecimal igstAmt = BigDecimal.ZERO;
				BigDecimal cgstAmt = BigDecimal.ZERO;
				BigDecimal sgstAmt = BigDecimal.ZERO;
				BigDecimal cessAmt = BigDecimal.ZERO;
				List<GetGstr1AB2baItemEntity> lineItems = new ArrayList<>();

				if (eachInvData.getLineItems() != null) {

					for (B2bLineItem b2cItems : eachInvData.getLineItems()) {

						B2bLineItemDetail b2cItem = b2cItems.getItemDetail();
						// New Item Entity
						GetGstr1AB2baItemEntity item = new GetGstr1AB2baItemEntity();
						/**
						 * B2BAItemDetails data
						 */

						item.setReturnPeriod(dto.getReturnPeriod());
						if (dto.getReturnPeriod() != null && dto.getReturnPeriod().length() > 0) {
							item.setDerivedTaxperiod(GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
						}
						item.setTaxRate(b2cItem.getRate());
						item.setTaxValue(b2cItem.getTaxableValue());
						item.setIgstAmt(b2cItem.getIgstAmount());
						item.setCgstAmt(b2cItem.getCgstAmount());
						item.setSgstAmt(b2cItem.getSgstAmount());
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
					GetGstr1AB2baHeaderEntity header = new GetGstr1AB2baHeaderEntity();
					/**
					 * Input data
					 */
					header.setAction(APIConstants.N);
					header.setGstin(dto.getGstin());
					header.setReturnPeriod(dto.getReturnPeriod());
					if (dto.getReturnPeriod() != null && dto.getReturnPeriod().length() > 0) {
						header.setDerivedTaxperiod(GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
					}
					header.setBatchId(dto.getBatchId());

					/**
					 * B2BAInvoices data
					 */

					header.setFlag(eachInvData.getInvoiceStatus());
					header.setInvNum(eachInvData.getInvoiceNumber());
					String invDate = eachInvData.getInvoiceDate();
					if (invDate != null) {
						header.setInvDate(String.format(invDate, DateUtil.SUPPORTED_DATE_FORMAT2));
					}
					header.setOrgInvNum(eachInvData.getOrigInvNumber());
					header.setOrgInvDate(LocalDate.parse(eachInvData.getOrigInvDate(), DateUtil.SUPPORTED_DATE_FORMAT2));
							
					header.setRchrg(eachInvData.getReverseCharge());
					header.setEtin(eachInvData.getEcomTin());
					header.setInvUpdby(eachInvData.getUpdby());
					header.setCflag(eachInvData.getCflag());
					header.setOrgPeriod(eachInvData.getOpd());
					header.setInvValue(eachInvData.getInvoiceValue());
					header.setInvType(eachInvData.getInvoiceType());
					header.setCtin(eachInv.getCgstin());
					LocalDateTime convertNow = EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now());
					header.setCreatedOn(convertNow);
					header.setCreatedBy("SYSTEM");

					// taxes at header level by summing the item values
					header.setTaxValue(taxValue);
					header.setIgstAmt(igstAmt);
					header.setCgstAmt(cgstAmt);
					header.setSgstAmt(sgstAmt);
					header.setCessAmt(cessAmt);

					String docDate = eachInvData.getInvoiceDate();
					String docNum = eachInvData.getInvoiceNumber();
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
		 * "failed to parse Gstr1 B2BA response"; LOGGER.error(msg, ex); throw
		 * new APIException(msg); }
		 */
		return headerList;
	}
}
