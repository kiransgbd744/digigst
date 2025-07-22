package com.ey.advisory.app.services.jobs.ret;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.ret.GetRetTable5Entity;
import com.ey.advisory.app.docs.dto.ret.RetItemDetailsDto;
import com.ey.advisory.app.docs.dto.ret.RetTbl5Dto;
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
@Service("RetTable5DataParserImpl")
public class RetTable5DataParserImpl implements RetTable5GetDataParser {

	@Override
	public Set<GetRetTable5Entity> parseTable5GetData(RetGetInvoicesReqDto dto,
			String apiResp) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		Set<GetRetTable5Entity> entities = new TreeSet<>();
		try {

			JsonObject jsonRetTable5 = (new JsonParser()).parse(apiResp)
					.getAsJsonObject().get(APIConstants.TBL5.toLowerCase())
					.getAsJsonObject();
			RetTbl5Dto retTbl5Dto = gson.fromJson(jsonRetTable5,
					RetTbl5Dto.class);
			String chksumRetTbl5 = retTbl5Dto.getChksum();
			Map<String, RetItemDetailsDto> mapRetTbl5 = getAsRetTbl5(
					retTbl5Dto);

			mapRetTbl5.forEach((key, val) -> {
				GetRetTable5Entity entity = new GetRetTable5Entity();
				entity.setChksum(chksumRetTbl5);
				entity.setGstin(dto.getGstin());
				entity.setBatchId(dto.getBatchId());
				entity.setReturnPeriod(dto.getReturnPeriod());
				entity.setDerivedTaxperiod(
						GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));

				setEntity(key, val, APIConstants.TBL5.toLowerCase(), entity);

				entities.add(entity);

			});

		} catch (Exception e) {
			String msg = "Failed to Parse Table5  Details";
			LOGGER.error(msg, e);
			throw new APIException(msg);
		}
		return entities;
	}

	private Map<String, RetItemDetailsDto> getAsRetTbl5(RetTbl5Dto dto) {
		Map<String, RetItemDetailsDto> map = new TreeMap<>();
		map.put(APIConstants.TDS, dto.getTds());
		map.put(APIConstants.TCS, dto.getTcs());
		map.put(APIConstants.SUBTOTAL, dto.getSubtotal());
		return map;
	}

	private GetRetTable5Entity setEntity(String key, RetItemDetailsDto val,
			String type, GetRetTable5Entity entity) {

		entity.setTableType(type);
		entity.setTableSection(key);
		entity.setGetDescription(key);

		entity.setIgstAmt(val.getIgstAmount());
		entity.setCgstAmt(val.getCgstAmount());
		entity.setSgstAmt(val.getSgstAmount());

		return entity;

	}

}
