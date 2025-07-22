package com.ey.advisory.app.services.jobs.gstr1;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.GetGstr1TxpHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1TxpItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1TxpaHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1TxpaItemEntity;
import com.ey.advisory.app.docs.dto.TXPInvoices;
import com.ey.advisory.app.docs.dto.TXPItemDetails;
import com.ey.advisory.app.services.common.Gstr1GetKeyGenerator;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

@Component("gstr1TxpTxpaDataParserImpl")
@Slf4j
public class Gstr1TxpTxpaDataParserImpl implements Gstr1TxpTxpaDataParser {

	@Autowired
	private Gstr1GetKeyGenerator gstr1GetKeyGenerator;

	@Override
	public List<GetGstr1TxpHeaderEntity> parseTxpData(Gstr1GetInvoicesReqDto dto, String apiResp) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray respObject = null;
		List<GetGstr1TxpHeaderEntity> headerList = new ArrayList<>();

		// try {

		respObject = (new JsonParser()).parse(apiResp).getAsJsonObject().get(APIConstants.TXPD.toLowerCase())
				.getAsJsonArray();

		Type listType = new TypeToken<List<TXPInvoices>>() {
		}.getType();

		// List of Header and Item Data
		List<TXPInvoices> baseEntity = gson.fromJson(respObject, listType);

		for (TXPInvoices eachInv : baseEntity) {
			BigDecimal taxRate = BigDecimal.ZERO;
			BigDecimal advAdjAmt = BigDecimal.ZERO;
			BigDecimal igstAmt = BigDecimal.ZERO;
			BigDecimal cgstAmt = BigDecimal.ZERO;
			BigDecimal sgstAmt = BigDecimal.ZERO;
			BigDecimal cessAmt = BigDecimal.ZERO;
			

			List<GetGstr1TxpItemEntity> lineItems = new ArrayList<>();
			if (eachInv.getItemDetails() != null) {
				for (TXPItemDetails eachInvData : eachInv.getItemDetails()) {

					// New Item Entity
					GetGstr1TxpItemEntity item = new GetGstr1TxpItemEntity();
					/**
					 * TXPItemDetails data
					 */
					item.setReturnPeriod(dto.getReturnPeriod());
					if (dto.getReturnPeriod() != null && dto.getReturnPeriod().length() > 0) {
						item.setDerivedTaxperiod(GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
					}
					item.setTaxRate(eachInvData.getRateOfInvoice());
					item.setAdvAdjAmt(eachInvData.getAdvanceToBeAdjested());
					item.setCgstAmt(eachInvData.getCGstAmount());
					item.setCessAmt(eachInvData.getCsGstAmount());
					item.setIgstAmt(eachInvData.getIGstAmount());
					item.setSgstAmt(eachInvData.getSGstAmount());

					lineItems.add(item);
					// Header Amounts count
					if (item.getTaxRate() != null) {
						taxRate = taxRate.add(item.getTaxRate());
					}
					if (item.getAdvAdjAmt() != null) {
						advAdjAmt = advAdjAmt.add(item.getAdvAdjAmt());
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
				GetGstr1TxpHeaderEntity header = new GetGstr1TxpHeaderEntity();
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
				 * TXPInvoices data
				 */
				header.setChksum(eachInv.getInvoiceCheckSum());
				header.setDiffPercent(eachInv.getDiffPercent());
				header.setFlag(eachInv.getInvoiceStatus());
				header.setPos(eachInv.getPlaceOfSupply());
				header.setSuppType(eachInv.getSupplyType());

				// taxes at header level by summing the item values

				header.setAdvAdjAmt(advAdjAmt);
				header.setIgstAmt(igstAmt);
				header.setCgstAmt(cgstAmt);
				header.setSgstAmt(sgstAmt);
				header.setCessAmt(cessAmt);
				
				LocalDateTime convertNow = EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now());
				header.setCreatedOn(convertNow);
				header.setCreatedBy("SYSTEM");

				String sgtin = dto.getGstin();
				String retPeriod = dto.getReturnPeriod();
				String type = eachInv.getSupplyType();
				String newPos = eachInv.getPlaceOfSupply();
				header.setDocKey(gstr1GetKeyGenerator.generateAtKey(sgtin, retPeriod, type, newPos));

				header.setLineItems(lineItems);
				lineItems.forEach(item -> {
					item.setDocument(header);
				});

				headerList.add(header);
			}
		}
		/*
		 * } catch (Exception ex) { String msg =
		 * "failed to parse Gstr1 TXP response"; LOGGER.error(msg, ex); throw
		 * new APIException(msg); }
		 */
		return headerList;
	}

	@Override
	public List<GetGstr1TxpaHeaderEntity> parseTxpaData(Gstr1GetInvoicesReqDto dto, String apiResp) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray respObject = null;
		List<GetGstr1TxpaHeaderEntity> headerList = new ArrayList<>();

		// try {

		respObject = (new JsonParser()).parse(apiResp).getAsJsonObject().get(APIConstants.TXPDA.toLowerCase())
				.getAsJsonArray();

		Type listType = new TypeToken<List<TXPInvoices>>() {
		}.getType();

		// List of Header and Item Data
		List<TXPInvoices> baseEntity = gson.fromJson(respObject, listType);

		for (TXPInvoices eachInv : baseEntity) {
			BigDecimal taxRate = BigDecimal.ZERO;
			BigDecimal advAdjAmt = BigDecimal.ZERO;
			BigDecimal igstAmt = BigDecimal.ZERO;
			BigDecimal cgstAmt = BigDecimal.ZERO;
			BigDecimal sgstAmt = BigDecimal.ZERO;
			BigDecimal cessAmt = BigDecimal.ZERO;
			
			List<GetGstr1TxpaItemEntity> lineItems = new ArrayList<>();
			if (eachInv.getItemDetails() != null) {
				for (TXPItemDetails eachInvData : eachInv.getItemDetails()) {

					// New Item Entity
					GetGstr1TxpaItemEntity item = new GetGstr1TxpaItemEntity();
					/**
					 * TXPAItemDetails data
					 */
					item.setReturnPeriod(dto.getReturnPeriod());
					if (dto.getReturnPeriod() != null && dto.getReturnPeriod().length() > 0) {
						item.setDerivedTaxperiod(GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
					}
					item.setTaxRate(eachInvData.getRateOfInvoice());
					item.setAdvAdjAmt(eachInvData.getAdvanceToBeAdjested());
					item.setCgstAmt(eachInvData.getCGstAmount());
					item.setCessAmt(eachInvData.getCsGstAmount());
					item.setIgstAmt(eachInvData.getIGstAmount());
					item.setSgstAmt(eachInvData.getSGstAmount());

					lineItems.add(item);
					// Header Amounts count
					if (item.getTaxRate() != null) {
						taxRate = taxRate.add(item.getTaxRate());
					}
					if (item.getAdvAdjAmt() != null) {
						advAdjAmt = advAdjAmt.add(item.getAdvAdjAmt());
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
				GetGstr1TxpaHeaderEntity header = new GetGstr1TxpaHeaderEntity();
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
				 * TXPAInvoices data
				 */
				header.setChksum(eachInv.getInvoiceCheckSum());
				header.setDiffPercent(eachInv.getDiffPercent());
				header.setFlag(eachInv.getInvoiceStatus());
				header.setPos(eachInv.getPlaceOfSupply());
				header.setSuppType(eachInv.getSupplyType());
				header.setOrgMonth(eachInv.getOriginalMonth());

				// taxes at header level by summing the item values

				header.setAdvAdjAmt(advAdjAmt);
				header.setIgstAmt(igstAmt);
				header.setCgstAmt(cgstAmt);
				header.setSgstAmt(sgstAmt);
				header.setCessAmt(cessAmt);
				
				LocalDateTime convertNow = EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now());
				header.setCreatedOn(convertNow);
				header.setCreatedBy("SYSTEM");

				String sgtin = dto.getGstin();
				String retPeriod = dto.getReturnPeriod();
				String type = eachInv.getSupplyType();
				String month = eachInv.getOriginalMonth();
				String newPos = eachInv.getPlaceOfSupply();
				header.setDocKey(gstr1GetKeyGenerator.generateAtaKey(sgtin, retPeriod, type, month, newPos));

				header.setLineItems(lineItems);
				lineItems.forEach(item -> {
					item.setDocument(header);
				});

				headerList.add(header);
			}
		}
		/*
		 * } catch (Exception ex) { String msg =
		 * "failed to parse Gstr1 TXPA response"; LOGGER.error(msg, ex); throw
		 * new APIException(msg); }
		 */
		return headerList;
	}
}