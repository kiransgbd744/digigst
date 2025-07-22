package com.ey.advisory.app.services.jobs.gstr7;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.Gstr7TdsDetailsEntity;
import com.ey.advisory.app.docs.dto.gstr7.Gstr7TdsDto;
import com.ey.advisory.app.docs.dto.gstr7.Gstr7TdsaDto;
import com.ey.advisory.app.docs.dto.gstr7.SaveGstr7;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.dto.Gstr7GetInvoicesReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Anand3.M
 *
 */
@Slf4j
@Service("Gstr7TdsDataParserImpl")
public class Gstr7TdsDataParserImpl implements Gstr7TdsGetDataParser {

	@Override
	public List<Gstr7TdsDetailsEntity> parseTdsGetData(
			Gstr7GetInvoicesReqDto dto, String apiResp, String type,
			Long batchId, LocalDateTime now) {

		Gson gson = GsonUtil.newSAPGsonInstance();

		List<Gstr7TdsDetailsEntity> entities = new ArrayList<>();
		try {
			if (apiResp == null) {
				return null;
			}
			JsonObject asJsonObject = (new JsonParser()).parse(apiResp)
					.getAsJsonObject();
			if (asJsonObject == null) {
				return null;
			}
			SaveGstr7 responseList1 = gson.fromJson(asJsonObject,
					SaveGstr7.class);

			if (responseList1 != null
					&& responseList1.getTdsInvoice() != null) {
				List<Gstr7TdsDto> tds = responseList1.getTdsInvoice();
				entities.addAll(convertTdsDtosIntoEntityList(tds, dto,
						responseList1.getReqTime()));
			}

			if (responseList1 != null
					&& responseList1.getTdsInvoice() != null) {
				List<Gstr7TdsaDto> tdsa = responseList1.getTdsaInvoice();
				entities.addAll(convertTdsaDtosIntoEntityList(tdsa, dto,
						responseList1.getReqTime()));
			}

		} catch (Exception e) {
			String msg = "Failed to Parse Gstr7 Tds  Details";
			LOGGER.error(msg, e);
			throw new APIException(msg);
		}
		return entities;
	}

	private Collection<? extends Gstr7TdsDetailsEntity> convertTdsaDtosIntoEntityList(
			List<Gstr7TdsaDto> tdsaDto, Gstr7GetInvoicesReqDto dto,
			String time) {
		List<Gstr7TdsDetailsEntity> entities = new ArrayList<>();
		Gstr7TdsDetailsEntity entity = new Gstr7TdsDetailsEntity();
		entity.setReturnPeriod(dto.getReturnPeriod());
		if (dto.getReturnPeriod() != null
				&& dto.getReturnPeriod().length() > 0) {
			entity.setDerReturnPeriod(
					GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
		}
		entity.setTdsDeductorGstin(dto.getGstin());
		entity.setReqTime(time);

		if (CollectionUtils.isNotEmpty(tdsaDto)) {
			tdsaDto.forEach(tdsaDtos -> {

				entity.setTdsDeducteeGstin(tdsaDtos.getGstin_ded());
				entity.setDeducteeName(tdsaDtos.getDeducteeName());
				entity.setOrgTdsGstin(tdsaDtos.getOgstin_ded());
				entity.setChksum(tdsaDtos.getChksum());
				entity.setAmtDeducted(tdsaDtos.getAmt_ded());
				entity.setOrgMonth(tdsaDtos.getOmonth());
				entity.setIgst(tdsaDtos.getIamt());
				entity.setSgst(tdsaDtos.getSamt());
				entity.setCgst(tdsaDtos.getCamt());
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

	private Collection<? extends Gstr7TdsDetailsEntity> convertTdsDtosIntoEntityList(
			List<Gstr7TdsDto> tdsDto, Gstr7GetInvoicesReqDto dto, String time) {
		List<Gstr7TdsDetailsEntity> entities = new ArrayList<>();
		Gstr7TdsDetailsEntity entity = new Gstr7TdsDetailsEntity();
		entity.setReturnPeriod(dto.getReturnPeriod());
		if (dto.getReturnPeriod() != null
				&& dto.getReturnPeriod().length() > 0) {
			entity.setDerReturnPeriod(
					GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
		}
		entity.setTdsDeductorGstin(dto.getGstin());
		entity.setReqTime(time);

		if (CollectionUtils.isNotEmpty(tdsDto)) {
			tdsDto.forEach(tdsaDtos -> {

				entity.setTdsDeducteeGstin(tdsaDtos.getGstin_ded());
				entity.setDeducteeName(tdsaDtos.getDeducteeName());
				entity.setChksum(tdsaDtos.getChksum());
				entity.setAmtDeducted(tdsaDtos.getAmt_ded());
				entity.setIgst(tdsaDtos.getIamt());
				entity.setSgst(tdsaDtos.getSamt());
				entity.setCgst(tdsaDtos.getCamt());
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

	/*
	 * private static String getFormatDate(DateTime dateTime){ SimpleDateFormat
	 * f= new SimpleDateFormat(); f.setTimeZone(TimeZone.getTimeZone("UTC"));
	 * String formattedDate = f.format(dateTime.toDate()); return formattedDate;
	 * 
	 * }
	 */

	LocalDateTime now = LocalDateTime.now();
	// System.out.println("Before : " + now);
	DateTimeFormatter formatter = DateTimeFormatter
			.ofPattern("dd-MM-yyyy HH-mm");
	String FormatDateTime = now.format(formatter);
	// System.out.println("After : " + FormatDateTime);

	public static void main(String[] args) {

		String str = "14-05-2018 11:23";

		LocalDateTime dateTime = LocalDateTime.parse(str,
				DateUtil.SUPPORTED_DATE_FORMAT8);
		DateTimeFormatter formatter = DateTimeFormatter
				.ofPattern("dd-MM-yyyy HH:mm");
		String form = dateTime.format(formatter);
		LocalDateTime dateTim = LocalDateTime.parse(form, formatter);

		// LocalDateTime tim1=FormatDateTime;
		System.out.println(form);
	}

}
