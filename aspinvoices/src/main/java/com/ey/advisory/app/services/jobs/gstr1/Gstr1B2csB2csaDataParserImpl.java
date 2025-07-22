package com.ey.advisory.app.services.jobs.gstr1;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.GetGstr1B2csHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1B2csItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1B2csaHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1B2csaItemEntity;
import com.ey.advisory.app.docs.dto.B2CSALineItem;
import com.ey.advisory.app.docs.dto.B2CSInvoices;
import com.ey.advisory.app.services.common.Gstr1GetKeyGenerator;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Umesha.M
 *
 */
@Component("gstr1B2csB2csaDataParserImpl")
@Slf4j
public class Gstr1B2csB2csaDataParserImpl implements Gstr1B2csB2csaDataParser {

	@Autowired
	private Gstr1GetKeyGenerator gstr1GetKeyGenerator;

	@Override
	public List<GetGstr1B2csHeaderEntity> parseB2csData(
			Gstr1GetInvoicesReqDto dto, String apiResp) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray respObject = null;
		List<GetGstr1B2csHeaderEntity> headerList = new ArrayList<>();

		if (apiResp == null) {
			return null;
		}
		JsonObject asJsonObject = (new JsonParser()).parse(apiResp)
				.getAsJsonObject();
		if (asJsonObject == null) {
			return null;
		}
		respObject = asJsonObject.get(APIConstants.B2CS) != null
				? asJsonObject.get(APIConstants.B2CS).getAsJsonArray() : null;
		if (respObject == null) {
			return null;
		}

		Type listType = new TypeToken<List<B2CSInvoices>>() {
		}.getType();
		// List of Header and Item Data
		List<B2CSInvoices> baseEntity = gson.fromJson(respObject, listType);
		for (B2CSInvoices eachInv : baseEntity) {

			List<GetGstr1B2csItemEntity> lineItems = new ArrayList<>();
			// New Item Entity
			GetGstr1B2csItemEntity item = new GetGstr1B2csItemEntity();
			/**
			 * B2csItemDetails data
			 */
			item.setReturnPeriod(dto.getReturnPeriod());
			if (dto.getReturnPeriod() != null
					&& dto.getReturnPeriod().length() > 0) {
				item.setDerivedTaxperiod(
						GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
			}
			item.setTaxRate(eachInv.getRate());
			item.setTaxVal(eachInv.getTaxableValue());
			item.setIgstAmt(eachInv.getIgstAmount());
			item.setCgstAmt(eachInv.getCgstAmount());
			item.setSgstAmt(eachInv.getSgstAmount());
			item.setCessAmt(eachInv.getCessAmount());

			lineItems.add(item);

			// New Header Entity
			GetGstr1B2csHeaderEntity header = new GetGstr1B2csHeaderEntity();
			/**
			 * Input data
			 */
			header.setGstin(dto.getGstin());
			header.setReturnPeriod(dto.getReturnPeriod());
			if (dto.getReturnPeriod() != null
					&& dto.getReturnPeriod().length() > 0) {
				header.setDerivedTaxperiod(
						GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
			}
			header.setBatchId(dto.getBatchId());

			/**
			 * B2CSInvoices data
			 */
			header.setChksum(eachInv.getCheckSum());
			header.setDiffPercent(eachInv.getDiffPercent());
			header.setFlag(eachInv.getInvoiceStatus());
			header.setPos(eachInv.getPointOfSupply());
			header.setEcomType(eachInv.getType());
			header.setEtin(eachInv.getEcomTin());
			header.setSuppType(eachInv.getSupplyType());
			

			// taxes at header level by summing the item values

			header.setTaxValue(eachInv.getTaxableValue());
			header.setIgstAmt(eachInv.getIgstAmount());
			header.setCgstAmt(eachInv.getCgstAmount());
			header.setSgstAmt(eachInv.getSgstAmount());
			header.setCessAmt(eachInv.getCessAmount());
			
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			header.setCreatedOn(convertNow);
			header.setCreatedBy("SYSTEM");

			String sgtin = dto.getGstin();
			String retPeriod = dto.getReturnPeriod();
			String type = eachInv.getType();
			String newPos = eachInv.getPointOfSupply();
			String eGstin = eachInv.getEcomTin();

			header.setDocKey(gstr1GetKeyGenerator.generateB2csKey(sgtin,
					retPeriod, type, newPos, eGstin));

			header.setLineItems(lineItems);
			lineItems.forEach(itm -> {
				itm.setDocument(header);
			});

			headerList.add(header);
		}

		return headerList;
	}

	@Override
	public List<GetGstr1B2csaHeaderEntity> parseB2csaData(
			Gstr1GetInvoicesReqDto dto, String apiResp) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray respObject = null;
		List<GetGstr1B2csaHeaderEntity> headerList = new ArrayList<>();

		if (apiResp == null) {
			return null;
		}
		JsonObject asJsonObject = (new JsonParser()).parse(apiResp)
				.getAsJsonObject();
		if (asJsonObject == null) {
			return null;
		}
		respObject = asJsonObject.get(APIConstants.B2CSA) != null
				? asJsonObject.get(APIConstants.B2CSA).getAsJsonArray() : null;
		if (respObject == null) {
			return null;
		}

		Type listType = new TypeToken<List<B2CSInvoices>>() {
		}.getType();

		// List of Header and Item Data
		List<B2CSInvoices> baseEntity = gson.fromJson(respObject, listType);
		for (B2CSInvoices eachInv : baseEntity) {
			BigDecimal taxVal = BigDecimal.ZERO;
			BigDecimal igstAmt = BigDecimal.ZERO;
			BigDecimal cgstAmt = BigDecimal.ZERO;
			BigDecimal sgstAmt = BigDecimal.ZERO;
			BigDecimal cessAmt = BigDecimal.ZERO;

			List<GetGstr1B2csaItemEntity> lineItems = new ArrayList<>();
			if (eachInv.getLineItems() != null) {
				for (B2CSALineItem eachInvData : eachInv.getLineItems()) {

					// New Item Entity
					GetGstr1B2csaItemEntity item = new GetGstr1B2csaItemEntity();
					/**
					 * B2csaItemDetails data
					 */
					item.setReturnPeriod(dto.getReturnPeriod());
					if (dto.getReturnPeriod() != null
							&& dto.getReturnPeriod().length() > 0) {
						item.setDerivedTaxperiod(GenUtil
								.convertTaxPeriodToInt(dto.getReturnPeriod()));
					}
					item.setTaxRate(eachInvData.getRate());
					item.setTaxValue(eachInvData.getTaxableValue());
					item.setIgstAmt(eachInvData.getIgstAmount());
					item.setCgstAmt(eachInvData.getCgstAmount());
					item.setSgstAmt(eachInvData.getSgstAmount());
					item.setCessAmt(eachInvData.getCessAmount());

					lineItems.add(item);
					// Header Amounts count
					if (item.getTaxValue() != null) {
						taxVal = taxVal.add(item.getTaxValue());
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
			}
			// New Header Entity
			GetGstr1B2csaHeaderEntity header = new GetGstr1B2csaHeaderEntity();
			/**
			 * Input data
			 */
			header.setGstin(dto.getGstin());
			header.setReturnPeriod(dto.getReturnPeriod());
			if (dto.getReturnPeriod() != null
					&& dto.getReturnPeriod().length() > 0) {
				header.setDerivedTaxperiod(
						GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
			}
			header.setBatchId(dto.getBatchId());

			/**
			 * B2CSAInvoices data
			 */

			header.setDiffPercent(eachInv.getDiffPercent());
			header.setEcomType(eachInv.getType());
			header.setEtin(eachInv.getEcomTin());
			header.setPos(eachInv.getPointOfSupply());
			header.setSuppType(eachInv.getSupplyType());
			header.setOrgInvMonth(eachInv.getOrgMonthInv());
			header.setOrgPos(eachInv.getOrgPlaceSupp());

			// taxes at header level by summing the item values

			header.setTaxValue(taxVal);
			header.setIgstAmt(igstAmt);
			header.setCgstAmt(cgstAmt);
			header.setSgstAmt(sgstAmt);
			header.setCessAmt(cessAmt);
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			header.setCreatedOn(convertNow);
			header.setCreatedBy("SYSTEM");

			String sgtin = dto.getGstin();
			String retPeriod = dto.getReturnPeriod();
			String type = eachInv.getType();
			String month = eachInv.getOrgMonthInv();
			String newPos = eachInv.getPointOfSupply();
			String eGstin = eachInv.getEcomTin();

			header.setDocKey(gstr1GetKeyGenerator.generateB2csaKey(sgtin,
					retPeriod, type, month, newPos, eGstin));

			header.setLineItems(lineItems);
			lineItems.forEach(item -> {
				item.setDocument(header);
			});

			headerList.add(header);
		}
		return headerList;
	}
}