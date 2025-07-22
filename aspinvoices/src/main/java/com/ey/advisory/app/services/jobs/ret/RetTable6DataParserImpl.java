package com.ey.advisory.app.services.jobs.ret;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.ret.GetRetInterestLateFeeEntity;
import com.ey.advisory.app.docs.dto.ret.RetItemDetailsDto;
import com.ey.advisory.app.docs.dto.ret.RetTbl6Dto;
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
@Service("RetTable6DataParserImpl")
public class RetTable6DataParserImpl implements RetTable6GetDataParser {

	@Override
	public Set<GetRetInterestLateFeeEntity> parseTable6GetData(
			RetGetInvoicesReqDto dto, String apiResp) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		Set<GetRetInterestLateFeeEntity> entities = new TreeSet<>();
		try {

			JsonObject jsonRetTable6 = (new JsonParser()).parse(apiResp)
					.getAsJsonObject().get(APIConstants.TBL6.toLowerCase())
					.getAsJsonObject();
			RetTbl6Dto retTbl6Dto = gson.fromJson(jsonRetTable6,
					RetTbl6Dto.class);
			String chksumRetTbl6 = retTbl6Dto.getChksum();
			Map<String, RetItemDetailsDto> mapRetTbl6 = getAsRetTbl6(
					retTbl6Dto);

			mapRetTbl6.forEach((key, val) -> {
				GetRetInterestLateFeeEntity entity = new GetRetInterestLateFeeEntity();
				entity.setChksum(chksumRetTbl6);
				entity.setGstin(dto.getGstin());
				entity.setBatchId(dto.getBatchId());
				entity.setReturnPeriod(dto.getReturnPeriod());
				entity.setDerivedTaxperiod(
						GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));

				setEntity(key, val, APIConstants.TBL6.toLowerCase(), entity);

				entities.add(entity);

			});

		} catch (Exception e) {
			String msg = "Failed to Parse Table6  Details";
			LOGGER.error(msg, e);
			throw new APIException(msg);
		}
		return entities;
	}

	private Map<String, RetItemDetailsDto> getAsRetTbl6(RetTbl6Dto dto) {
		Map<String, RetItemDetailsDto> map = new TreeMap<>();
		map.put(APIConstants.INTRLATEFILING, dto.getIntrlatefiling());
		map.put(APIConstants.INTRLATEREPORT, dto.getIntrlatereport());
		map.put(APIConstants.INTRREJA, dto.getIntrreja());
		map.put(APIConstants.INTRITCREV, dto.getIntritcrev());
		map.put(APIConstants.INTRREV, dto.getIntrrev());
		map.put(APIConstants.OTHRLIAB, dto.getOthrliab());
		map.put(APIConstants.SUBTOTAL, dto.getSubtotal());
		return map;
	}

	private GetRetInterestLateFeeEntity setEntity(String key,
			RetItemDetailsDto val, String type,
			GetRetInterestLateFeeEntity entity) {

		// entity.setTableType(type);
		entity.setTableSection(key);
		entity.setGetDescription(key);

		entity.setInterestIgstAmt(val.getIgstAmount());
		entity.setInterestCgstAmt(val.getCgstAmount());
		entity.setInterestSgstAmt(val.getSgstAmount());
		entity.setInterestCessAmt(val.getCessAmount());
		entity.setLatefeeSgstAmt(val.getLateFee().getSgstAmount());
		entity.setLatefeeCgstAmt(val.getLateFee().getCgstAmount());
		return entity;

	}

}
