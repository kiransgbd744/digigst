package com.ey.advisory.app.services.jobs.ret;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.ret.GetRetPaymentOfTaxEntity;
import com.ey.advisory.app.docs.dto.ret.RetItemDetailsDto;
import com.ey.advisory.app.docs.dto.ret.RetPaymentTaxDto;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.dto.RetGetInvoicesReqDto;
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
@Service("RetPaymentOfTaxDataParserImpl")
public class RetPaymentOfTaxDataParserImpl
		implements RetPaymentOfTaxGetDataParser {

	@Override
	public Set<GetRetPaymentOfTaxEntity> parsePaymentTaxGetData(
			RetGetInvoicesReqDto dto, String apiResp) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		Set<GetRetPaymentOfTaxEntity> entities = new TreeSet<>();
		try {

			JsonObject jsonRetTablePaymentTax = (new JsonParser())
					.parse(apiResp).getAsJsonObject()
					.get(APIConstants.PAYMENTTAX.toLowerCase())
					.getAsJsonObject();
			RetPaymentTaxDto retPaymentTaxDto = gson
					.fromJson(jsonRetTablePaymentTax, RetPaymentTaxDto.class);

			Map<String, RetItemDetailsDto> mapRetTblPayTax = getAsRetTblPayTax(
					retPaymentTaxDto);

			mapRetTblPayTax.forEach((key, val) -> {
				GetRetPaymentOfTaxEntity entity = new GetRetPaymentOfTaxEntity();

				entity.setGstin(dto.getGstin());
				entity.setBatchId(dto.getBatchId());
				entity.setReturnPeriod(dto.getReturnPeriod());
				entity.setDerivedTaxperiod(
						GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));

				setEntity(key, val, APIConstants.PAYMENTTAX.toLowerCase(),
						entity);

				entities.add(entity);

			});

		} catch (Exception e) {
			String msg = "Failed to Parse PaymentTax  Details";
			LOGGER.error(msg, e);
			throw new APIException(msg);
		}
		return entities;
	}

	private Map<String, RetItemDetailsDto> getAsRetTblPayTax(
			RetPaymentTaxDto dto) {
		Map<String, RetItemDetailsDto> map = new TreeMap<>();
		map.put(APIConstants.TXPY, dto.getTxpy());
		map.put(APIConstants.PMTPD, dto.getPmtpd());
		map.put(APIConstants.NEGLIAB, dto.getNegliab());
		map.put(APIConstants.TXPAID, dto.getTaxPaid());
		map.put(APIConstants.NETTXPY, dto.getNettxpy());
		return map;
	}

	private GetRetPaymentOfTaxEntity setEntity(String key,
			RetItemDetailsDto val, String type,
			GetRetPaymentOfTaxEntity entity) {

		// entity.setTableType(type);
		entity.setTableSection(key);
		entity.setGetDescription(key);

		entity.setPaidItcIgst(val.getIgstAmount());
		entity.setPaidItcCgst(val.getCgstAmount());
		entity.setPaidItcSgst(val.getSgstAmount());
		entity.setPaidItcCess(val.getCessAmount());

		return entity;

	}
}
