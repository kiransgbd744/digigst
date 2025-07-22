package com.ey.advisory.app.services.jobs.gstr8;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.GetGstr8TcsHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr8TcsaHeaderEntity;
import com.ey.advisory.app.docs.dto.TcsLineItem;
import com.ey.advisory.app.docs.dto.TcsTcsaInvoiceData;
import com.ey.advisory.app.docs.dto.TcsaLineItem;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
@Component("Gstr8TcsTcsaDataParserImpl")
@Slf4j
public class Gstr8TcsTcsaDataParserImpl implements Gstr8TcsTcsaDataParser {

	@Override
	public List<GetGstr8TcsHeaderEntity> parseTcsData(
			Gstr1GetInvoicesReqDto dto, String apiResp) {

		Gson gson = GsonUtil.newSAPGsonInstance();

		List<GetGstr8TcsHeaderEntity> entities = new ArrayList<>();
		try {
			if (apiResp == null) {
				return null;
			}
			JsonObject asJsonObject = JsonParser.parseString(apiResp)
					.getAsJsonObject();
			if (asJsonObject == null) {
				return null;
			}
			TcsTcsaInvoiceData responseList1 = gson.fromJson(asJsonObject,
					TcsTcsaInvoiceData.class);

			if (responseList1 != null
					&& responseList1.getTcsLineItems() != null) {
				List<TcsLineItem> tcs = responseList1.getTcsLineItems();
				entities.addAll(convertTcsDtosIntoEntityList(tcs,
						responseList1.getGstin(),
						responseList1.getFinancialPeriod(),
						responseList1.getFromTime()));
			}

		} catch (Exception e) {
			String msg = "Failed to Parse Gstr8 Tcs  Details";
			LOGGER.error(msg, e);
			throw new APIException(msg);
		}
		return entities;
	}

	@Override
	public List<GetGstr8TcsaHeaderEntity> parseTcsaData(
			Gstr1GetInvoicesReqDto dto, String apiResp) {

		Gson gson = GsonUtil.newSAPGsonInstance();

		List<GetGstr8TcsaHeaderEntity> entities = new ArrayList<>();
		try {
			if (apiResp == null) {
				return null;
			}
			JsonObject asJsonObject = JsonParser.parseString(apiResp)
					.getAsJsonObject();
			if (asJsonObject == null) {
				return null;
			}
			TcsTcsaInvoiceData responseList1 = gson.fromJson(asJsonObject,
					TcsTcsaInvoiceData.class);

			if (responseList1 != null
					&& responseList1.getTcsLineItems() != null) {
				List<TcsaLineItem> tcsa = responseList1.getTcsaLineItems();
				entities.addAll(convertTcsaDtosIntoEntityList(tcsa,
						responseList1.getGstin(),
						responseList1.getFinancialPeriod(),
						responseList1.getFromTime()));
			}

		} catch (Exception e) {
			String msg = "Failed to Parse Gstr8 Tcsa  Details";
			LOGGER.error(msg, e);
			throw new APIException(msg);
		}
		return entities;
	}

	private Collection<? extends GetGstr8TcsHeaderEntity> convertTcsDtosIntoEntityList(
			List<TcsLineItem> tcsLineItems, String gstin, String taxPeriod,
			String time) {
		List<GetGstr8TcsHeaderEntity> entities = new ArrayList<>();
		GetGstr8TcsHeaderEntity entity = new GetGstr8TcsHeaderEntity();
		entity.setReturnPeriod(taxPeriod);
		entity.setGstin(gstin);
		entity.setFromTime(time);

		if (CollectionUtils.isNotEmpty(tcsLineItems)) {
			tcsLineItems.forEach(tcsDto -> {

				entity.setChksum(tcsDto.getCheckSum());
				entity.setStin(tcsDto.getSupplierGstin());
				entity.setSupplierGstinName(tcsDto.getSupplierGstin());
				entity.setSuppliesMadeRegistered(
						tcsDto.getGrossSuppliesMadeRegistered());
				entity.setSuppliesReturnedRegistered(
						tcsDto.getGrossSuppliesReturnedRegistered());
				entity.setSuppliesMadeUnRegistered(
						tcsDto.getGrossSuppliesMadeUnRegistered());
				entity.setSuppliesReturnedUnRegistered(
						tcsDto.getGrossSuppliesReturnedUnRegistered());
				entity.setNetAmount(tcsDto.getNetAmount());
				entity.setCgstAmt(tcsDto.getCentralAmount());
				entity.setSgstAmt(tcsDto.getStateTaxAmount());
				entity.setIgstAmt(tcsDto.getIntegratedTaxAmount());
				entity.setSupplierGstinName(tcsDto.getSupplierGstinLegelName());
				entity.setCreatedBy(APIConstants.SYSTEM);
				entity.setModifiedOn(LocalDateTime.now());
				LocalDateTime convertNow = EYDateUtil
						.toUTCDateTimeFromLocal(LocalDateTime.now());
				entity.setCreatedOn(convertNow);

				entities.add(entity);
			});
		}
		return entities;
	}

	private Collection<? extends GetGstr8TcsaHeaderEntity> convertTcsaDtosIntoEntityList(
			List<TcsaLineItem> tcsaLineItems, String gstin, String taxPeriod,
			String time) {
		List<GetGstr8TcsaHeaderEntity> entities = new ArrayList<>();
		GetGstr8TcsaHeaderEntity entity = new GetGstr8TcsaHeaderEntity();
		entity.setReturnPeriod(taxPeriod);
		entity.setGstin(gstin);
		entity.setFromTime(time);

		if (CollectionUtils.isNotEmpty(tcsaLineItems)) {
			tcsaLineItems.forEach(tcsaDto -> {

				entity.setChksum(tcsaDto.getCheckSum());
				entity.setStin(tcsaDto.getSupplierGstin());
				entity.setSupplierGstinName(tcsaDto.getSupplierGstin());
				entity.setOriginalSupplierGstin(
						tcsaDto.getOriginalSupplierGstin());
				entity.setOriginalReturnPeriod(
						tcsaDto.getOriginalFinancialPeriod());
				entity.setSuppliesMadeRegistered(
						tcsaDto.getGrossSuppliesMadeRegistered());
				entity.setSuppliesReturnedRegistered(
						tcsaDto.getGrossSuppliesReturnedRegistered());
				entity.setSuppliesMadeUnRegistered(
						tcsaDto.getGrossSuppliesMadeUnRegistered());
				entity.setSuppliesReturnedUnRegistered(
						tcsaDto.getGrossSuppliesReturnedUnRegistered());
				entity.setNetAmount(tcsaDto.getNetAmount());
				entity.setCgstAmt(tcsaDto.getCentralAmount());
				entity.setSgstAmt(tcsaDto.getStateTaxAmount());
				entity.setIgstAmt(tcsaDto.getIntegratedTaxAmount());
				entity.setSource(tcsaDto.getSource());
				entity.setAction(tcsaDto.getAction());
				entity.setSupplierGstinName(
						tcsaDto.getSupplierGstinLegalName());
				entity.setOriginalSupplierGstinName(
						tcsaDto.getOriginalSupplierGstinLegalName());
				entity.setCreatedBy(APIConstants.SYSTEM);
				entity.setModifiedOn(LocalDateTime.now());
				LocalDateTime convertNow = EYDateUtil
						.toUTCDateTimeFromLocal(LocalDateTime.now());
				entity.setCreatedOn(convertNow);

				entities.add(entity);
			});
		}
		return entities;
	}
}
