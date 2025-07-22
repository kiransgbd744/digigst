package com.ey.advisory.app.services.jobs.ret;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.ret.GetRetInwardEntity;
import com.ey.advisory.app.docs.dto.ret.RetItemDetailsDto;
import com.ey.advisory.app.docs.dto.ret.RetTbl3bDto;
import com.ey.advisory.app.docs.dto.ret.RetTbl4aDto;
import com.ey.advisory.app.docs.dto.ret.RetTbl4bDto;
import com.ey.advisory.app.docs.dto.ret.RetTbl4itcDto;
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
@Service("RetInwardDataParserImpl")
public class RetInwardDataParserImpl implements RetInwardGetDataParser {

	@Override
	public Set<GetRetInwardEntity> parseInwardGetData(RetGetInvoicesReqDto dto,
			String apiResp) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		Set<GetRetInwardEntity> entities = new TreeSet<>();
		try {
			// 3b
			JsonObject jsonRet3b = (new JsonParser()).parse(apiResp)
					.getAsJsonObject().get(APIConstants.TBL3B.toLowerCase())
					.getAsJsonObject();
			RetTbl3bDto retTbl3bDto = gson.fromJson(jsonRet3b,
					RetTbl3bDto.class);
			String chksumRet3b = retTbl3bDto.getChksum();
			Map<String, RetItemDetailsDto> mapRet3b = getAsRet3bMap(
					retTbl3bDto);

			mapRet3b.forEach((key, val) -> {
				GetRetInwardEntity entity = new GetRetInwardEntity();
				entity.setChksum(chksumRet3b);
				entity.setGstin(dto.getGstin());
				entity.setBatchId(dto.getBatchId());
				entity.setReturnPeriod(dto.getReturnPeriod());
				entity.setDerivedTaxperiod(
						GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));

				setEntity(key, val, APIConstants.TBL3B.toLowerCase(), entity);

				entities.add(entity);

			});

			// 4a
			JsonObject jsonRet4a = (new JsonParser()).parse(apiResp)
					.getAsJsonObject().get(APIConstants.TBL4A.toLowerCase())
					.getAsJsonObject();
			RetTbl4aDto retTbl4aDto = gson.fromJson(jsonRet4a,
					RetTbl4aDto.class);
			String chksumRet4a = retTbl4aDto.getChksum();
			Map<String, RetItemDetailsDto> mapRet4a = getAsRet4aMap(
					retTbl4aDto);

			mapRet4a.forEach((key, val) -> {
				GetRetInwardEntity entity = new GetRetInwardEntity();
				entity.setChksum(chksumRet4a);
				entity.setGstin(dto.getGstin());
				entity.setBatchId(dto.getBatchId());
				entity.setReturnPeriod(dto.getReturnPeriod());
				entity.setDerivedTaxperiod(
						GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));

				setEntity(key, val, APIConstants.TBL4A.toLowerCase(), entity);
				entities.add(entity);
			});

			// 4b
			JsonObject jsonRet4b = (new JsonParser()).parse(apiResp)
					.getAsJsonObject().get(APIConstants.TBL4B.toLowerCase())
					.getAsJsonObject();
			RetTbl4bDto retTbl4bDto = gson.fromJson(jsonRet4b,
					RetTbl4bDto.class);
			String chksumRet4b = retTbl4bDto.getChksum();
			Map<String, RetItemDetailsDto> mapRet4b = getAsRet4bMap(
					retTbl4bDto);

			mapRet4b.forEach((key, val) -> {
				GetRetInwardEntity entity = new GetRetInwardEntity();
				entity.setChksum(chksumRet4b);
				entity.setGstin(dto.getGstin());
				entity.setBatchId(dto.getBatchId());
				entity.setReturnPeriod(dto.getReturnPeriod());
				entity.setDerivedTaxperiod(
						GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));

				setEntity(key, val, APIConstants.TBL4B.toLowerCase(), entity);
				entities.add(entity);

			});

			// 4c
			JsonObject jsonRet4itc = (new JsonParser()).parse(apiResp)
					.getAsJsonObject().get(APIConstants.TBL4ITC.toLowerCase())
					.getAsJsonObject();
			RetTbl4itcDto retTbl4itcDto = gson.fromJson(jsonRet4itc,
					RetTbl4itcDto.class);
			String chksumRet4itc = retTbl4itcDto.getChksum();
			Map<String, RetItemDetailsDto> mapRet4itc = getAsRet4itcMap(
					retTbl4itcDto);

			mapRet4itc.forEach((key, val) -> {
				GetRetInwardEntity entity = new GetRetInwardEntity();
				entity.setChksum(chksumRet4itc);
				entity.setGstin(dto.getGstin());
				entity.setBatchId(dto.getBatchId());
				entity.setReturnPeriod(dto.getReturnPeriod());
				entity.setDerivedTaxperiod(
						GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));

				setEntity(key, val, APIConstants.TBL4ITC.toLowerCase(), entity);
				entities.add(entity);
			});

		} catch (Exception e) {
			String msg = "Failed to Parse Inward  Details";
			LOGGER.error(msg, e);
			throw new APIException(msg);
		}
		return entities;
	}

	private Map<String, RetItemDetailsDto> getAsRet3bMap(RetTbl3bDto dto) {
		Map<String, RetItemDetailsDto> map = new TreeMap<>();
		map.put(APIConstants.REV, dto.getRev());
		map.put(APIConstants.IMPS, dto.getImps());
		map.put(APIConstants.SUBTOTAL, dto.getSubtotal());
		return map;
	}

	private Map<String, RetItemDetailsDto> getAsRet4aMap(RetTbl4aDto dto) {
		Map<String, RetItemDetailsDto> map = new TreeMap<>();
		map.put(APIConstants.REJCN, dto.getRejcn());
		map.put(APIConstants.PENCN, dto.getPencn());
		map.put(APIConstants.ACCCN, dto.getAcccn());
		map.put(APIConstants.ELGCRDT, dto.getElgcrdt());
		map.put(APIConstants.REV, dto.getRev());
		map.put(APIConstants.IMPS, dto.getImps());
		map.put(APIConstants.IMPG, dto.getImpg());
		map.put(APIConstants.IMPGSEZ, dto.getImpgsez());
		map.put(APIConstants.ISDC, dto.getIsdc());
		map.put(APIConstants.PROVCRDT, dto.getProvcrdt());
		map.put(APIConstants.SUBTOTAL, dto.getSubtotal());
		return map;
	}

	private Map<String, RetItemDetailsDto> getAsRet4bMap(RetTbl4bDto dto) {
		Map<String, RetItemDetailsDto> map = new TreeMap<>();
		map.put(APIConstants.POSTRJCTDCREDITS, dto.getPostrjctdcredits());
		map.put(APIConstants.ITCREVINV, dto.getItcrevinv());
		map.put(APIConstants.INELGCREDITS, dto.getInelgcredits());
		map.put(APIConstants.ITCREV, dto.getItcrev());
		map.put(APIConstants.ITCREVOTH, dto.getItcrev());
		map.put(APIConstants.ITCREV, dto.getItcrev());
		map.put(APIConstants.ITCREV, dto.getItcrev());
		map.put(APIConstants.SUBTOTAL, dto.getSubtotal());
		return map;
	}

	private Map<String, RetItemDetailsDto> getAsRet4itcMap(RetTbl4itcDto dto) {
		Map<String, RetItemDetailsDto> map = new TreeMap<>();
		map.put(APIConstants.FIRSTMON, dto.getFirstmon());
		map.put(APIConstants.SECMON, dto.getSecmon());
		map.put(APIConstants.ITCCG, dto.getItccg());
		map.put(APIConstants.ITCCS, dto.getItccs());
		map.put(APIConstants.SUBTOTAL, dto.getSubtotal());
		map.put(APIConstants.ITCAVL, dto.getItcavl());
		map.put(APIConstants.NETITCAVL, dto.getNetitcavl());
		return map;
	}

	private GetRetInwardEntity setEntity(String key, RetItemDetailsDto val,
			String type, GetRetInwardEntity entity) {

		entity.setTableType(type);
		entity.setTableSection(key);
		entity.setGetDescription(key);

		entity.setTaxableValue(val.getTaxableValue());
		entity.setIgstAmt(val.getIgstAmount());
		entity.setCgstAmt(val.getCgstAmount());
		entity.setSgstAmt(val.getSgstAmount());
		entity.setCessAmt(val.getCessAmount());
		return entity;

	}

}
