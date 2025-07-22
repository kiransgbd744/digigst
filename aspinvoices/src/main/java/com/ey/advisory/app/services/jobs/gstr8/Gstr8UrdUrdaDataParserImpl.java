package com.ey.advisory.app.services.jobs.gstr8;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.GetGstr8UrdHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr8UrdaHeaderEntity;
import com.ey.advisory.app.docs.dto.UrdLineItem;
import com.ey.advisory.app.docs.dto.UrdUrdaInvoiceData;
import com.ey.advisory.app.docs.dto.UrdaLineItem;
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
@Component("Gstr8UrdUrdaDataParserImpl")
@Slf4j
public class Gstr8UrdUrdaDataParserImpl implements Gstr8UrdUrdaDataParser {

	@Override
	public List<GetGstr8UrdHeaderEntity> parseUrdData(
			Gstr1GetInvoicesReqDto dto, String apiResp) {

		Gson gson = GsonUtil.newSAPGsonInstance();

		List<GetGstr8UrdHeaderEntity> entities = new ArrayList<>();
		try {
			if (apiResp == null) {
				return null;
			}
			JsonObject asJsonObject = JsonParser.parseString(apiResp)
					.getAsJsonObject();
			if (asJsonObject == null) {
				return null;
			}
			UrdUrdaInvoiceData responseList1 = gson.fromJson(asJsonObject,
					UrdUrdaInvoiceData.class);

			if (responseList1 != null
					&& responseList1.getUrdLineItems() != null) {
				List<UrdLineItem> urd = responseList1.getUrdLineItems();
				entities.addAll(convertUrdDtosIntoEntityList(urd,
						responseList1.getGstin(),
						responseList1.getFinancialPeriod(),
						responseList1.getFromTime()));
			}

		} catch (Exception e) {
			String msg = "Failed to Parse Gstr8 Urd Details";
			LOGGER.error(msg, e);
			throw new APIException(msg);
		}
		return entities;
	}

	@Override
	public List<GetGstr8UrdaHeaderEntity> parseUrdaData(
			Gstr1GetInvoicesReqDto dto, String apiResp) {

		Gson gson = GsonUtil.newSAPGsonInstance();

		List<GetGstr8UrdaHeaderEntity> entities = new ArrayList<>();
		try {
			if (apiResp == null) {
				return null;
			}
			JsonObject asJsonObject = JsonParser.parseString(apiResp)
					.getAsJsonObject();
			if (asJsonObject == null) {
				return null;
			}
			UrdUrdaInvoiceData responseList1 = gson.fromJson(asJsonObject,
					UrdUrdaInvoiceData.class);

			if (responseList1 != null
					&& responseList1.getUrdLineItems() != null) {
				List<UrdaLineItem> urda = responseList1.getUrdaLineItems();
				entities.addAll(convertUrdaDtosIntoEntityList(urda,
						responseList1.getGstin(),
						responseList1.getFinancialPeriod(),
						responseList1.getFromTime()));
			}

		} catch (Exception e) {
			String msg = "Failed to Parse Gstr8 Urda Details";
			LOGGER.error(msg, e);
			throw new APIException(msg);
		}
		return entities;
	}

	private Collection<? extends GetGstr8UrdHeaderEntity> convertUrdDtosIntoEntityList(
			List<UrdLineItem> urdLineItems, String gstin, String taxPeriod,
			String time) {
		List<GetGstr8UrdHeaderEntity> entities = new ArrayList<>();
		GetGstr8UrdHeaderEntity entity = new GetGstr8UrdHeaderEntity();
		entity.setReturnPeriod(taxPeriod);
		entity.setGstin(gstin);
		entity.setFromTime(time);

		if (CollectionUtils.isNotEmpty(urdLineItems)) {
			urdLineItems.forEach(urdDto -> {

				entity.setChksum(urdDto.getCheckSum());
				entity.setEnrolmentId(urdDto.getEnrolmentId());
				entity.setGrossSupplierMade(
						urdDto.getGrossSuppliesMadeRegistered());
				entity.setGrossSuppliesReturned(
						urdDto.getGrossSuppliesReturnedRegistered());
				entity.setNetAmount(urdDto.getNetAmount());
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

	private Collection<? extends GetGstr8UrdaHeaderEntity> convertUrdaDtosIntoEntityList(
			List<UrdaLineItem> urdaLineItems, String gstin, String taxPeriod,
			String time) {
		List<GetGstr8UrdaHeaderEntity> entities = new ArrayList<>();
		GetGstr8UrdaHeaderEntity entity = new GetGstr8UrdaHeaderEntity();
		entity.setReturnPeriod(taxPeriod);
		entity.setGstin(gstin);
		entity.setFromTime(time);

		if (CollectionUtils.isNotEmpty(urdaLineItems)) {
			urdaLineItems.forEach(urdaDto -> {

				entity.setChksum(urdaDto.getCheckSum());
				entity.setEnrolmentId(urdaDto.getEnrolmentId());
				entity.setOriginalReturnPeriod(
						urdaDto.getOriginalFinancialPeriod());
				entity.setOriginalEnrolmentId(urdaDto.getOriginalEnrolmentId());
				entity.setGrossSupplierMade(
						urdaDto.getGrossSuppliesMadeRegistered());
				entity.setGrossSuppliesReturned(
						urdaDto.getGrossSuppliesReturnedRegistered());
				entity.setNetAmount(urdaDto.getNetAmount());
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
