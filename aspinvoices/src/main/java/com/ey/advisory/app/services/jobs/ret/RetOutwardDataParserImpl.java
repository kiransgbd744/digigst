package com.ey.advisory.app.services.jobs.ret;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.ret.GetRetOutwardEntity;
import com.ey.advisory.app.docs.dto.ret.RetItemDetailsDto;
import com.ey.advisory.app.docs.dto.ret.RetTbl3aDto;
import com.ey.advisory.app.docs.dto.ret.RetTbl3cDto;
import com.ey.advisory.app.docs.dto.ret.RetTbl3dDto;
import com.ey.advisory.app.docs.dto.ret.RetTbl3eDto;
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
@Service("RetOutwardDataParserImpl")
public class RetOutwardDataParserImpl implements RetOutwardGetDataParser {

	@Override
	public Set<GetRetOutwardEntity> parseOutwardGetData(
			RetGetInvoicesReqDto dto, String apiResp) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		Set<GetRetOutwardEntity> entities = new TreeSet<>();
		try {
			// 3a
			JsonObject jsonRet3a = (new JsonParser()).parse(apiResp)
					.getAsJsonObject().get(APIConstants.TBL3A.toLowerCase())
					.getAsJsonObject();
			RetTbl3aDto retTbl3aDto = gson.fromJson(jsonRet3a,
					RetTbl3aDto.class);
			String chksumRet3a = retTbl3aDto.getChksum();
			Map<String, RetItemDetailsDto> mapRet3a = getAsRet3aMap(
					retTbl3aDto);

			mapRet3a.forEach((key, val) -> {
				GetRetOutwardEntity entity = new GetRetOutwardEntity();
				entity.setChksum(chksumRet3a);
				entity.setGstin(dto.getGstin());
				entity.setBatchId(dto.getBatchId());
				entity.setReturnPeriod(dto.getReturnPeriod());
				entity.setDerivedTaxperiod(
						GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));

				setEntity(key, val, APIConstants.TBL3A.toLowerCase(), entity);

				entities.add(entity);

			});

			// 3c
			JsonObject jsonRet3c = (new JsonParser()).parse(apiResp)
					.getAsJsonObject().get(APIConstants.TBL3C.toLowerCase())
					.getAsJsonObject();
			RetTbl3cDto retTbl3cDto = gson.fromJson(jsonRet3c,
					RetTbl3cDto.class);
			String chksumRet3c = retTbl3cDto.getChksum();
			Map<String, RetItemDetailsDto> mapRet3c = getAsRet3cMap(
					retTbl3cDto);

			mapRet3c.forEach((key, val) -> {
				GetRetOutwardEntity entity = new GetRetOutwardEntity();
				entity.setChksum(chksumRet3c);
				entity.setGstin(dto.getGstin());
				entity.setBatchId(dto.getBatchId());
				entity.setReturnPeriod(dto.getReturnPeriod());
				entity.setDerivedTaxperiod(
						GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));

				setEntity(key, val, APIConstants.TBL3C.toLowerCase(), entity);
				entities.add(entity);
			});

			// 3d
			JsonObject jsonRet3d = (new JsonParser()).parse(apiResp)
					.getAsJsonObject().get(APIConstants.TBL3D.toLowerCase())
					.getAsJsonObject();
			RetTbl3dDto retTbl3dDto = gson.fromJson(jsonRet3d,
					RetTbl3dDto.class);
			String chksumRet3d = retTbl3dDto.getChksum();
			Map<String, RetItemDetailsDto> mapRet3d = getAsRet3dMap(
					retTbl3dDto);

			mapRet3d.forEach((key, val) -> {
				GetRetOutwardEntity entity = new GetRetOutwardEntity();
				entity.setChksum(chksumRet3d);
				entity.setGstin(dto.getGstin());
				entity.setBatchId(dto.getBatchId());
				entity.setReturnPeriod(dto.getReturnPeriod());
				entity.setDerivedTaxperiod(
						GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));

				setEntity(key, val, APIConstants.TBL3D.toLowerCase(), entity);
				entities.add(entity);

			});

			// 3e
			JsonObject jsonRet3e = (new JsonParser()).parse(apiResp)
					.getAsJsonObject().get(APIConstants.TBL3E.toLowerCase())
					.getAsJsonObject();
			RetTbl3eDto retTbl3eDto = gson.fromJson(jsonRet3e,
					RetTbl3eDto.class);
			Map<String, RetItemDetailsDto> mapRet3e = getAsRet3eMap(
					retTbl3eDto);

			mapRet3e.forEach((key, val) -> {
				GetRetOutwardEntity entity = new GetRetOutwardEntity();
				entity.setGstin(dto.getGstin());
				entity.setBatchId(dto.getBatchId());
				entity.setReturnPeriod(dto.getReturnPeriod());
				entity.setDerivedTaxperiod(
						GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));

				setEntity(key, val, APIConstants.TBL3E.toLowerCase(), entity);
				entities.add(entity);
			});

		} catch (Exception e) {
			String msg = "Failed to Parse Outward  Details";
			LOGGER.error(msg, e);
			throw new APIException(msg);
		}
		return entities;
	}

	private Map<String, RetItemDetailsDto> getAsRet3aMap(RetTbl3aDto dto) {
		Map<String, RetItemDetailsDto> map = new TreeMap<>();
		map.put(APIConstants.B2B, dto.getB2b());
		map.put(APIConstants.B2C, dto.getB2c());
		map.put(APIConstants.DE, dto.getDe());
		map.put(APIConstants.EXPLIAB, dto.getExpliab());
		map.put(APIConstants.EXPWOP, dto.getExpwop());
		map.put(APIConstants.EXPWP, dto.getExpwp());
		map.put(APIConstants.PRIORLIAB, dto.getPriorliab());
		map.put(APIConstants.SEZWOP, dto.getSezwop());
		map.put(APIConstants.SEZWP, dto.getSezwp());
		map.put(APIConstants.SUBTOTAL, dto.getSubtotal());
		return map;
	}

	private Map<String, RetItemDetailsDto> getAsRet3cMap(RetTbl3cDto dto) {
		Map<String, RetItemDetailsDto> map = new TreeMap<>();
		map.put(APIConstants.DN, dto.getDn());
		map.put(APIConstants.CN, dto.getCn());
		map.put(APIConstants.ADVREC, dto.getAdvrec());
		map.put(APIConstants.ADVADJ, dto.getAdvadj());
		map.put(APIConstants.RDCTN, dto.getRdctn());
		map.put(APIConstants.EXPLIAB, dto.getExpliab());
		map.put(APIConstants.SUBTOTAL, dto.getSubtotal());
		return map;
	}

	private Map<String, RetItemDetailsDto> getAsRet3dMap(RetTbl3dDto dto) {
		Map<String, RetItemDetailsDto> map = new TreeMap<>();
		map.put(APIConstants.EXPNIL, dto.getExpnil());
		map.put(APIConstants.NONGST, dto.getNongst());
		map.put(APIConstants.REVNT, dto.getRevnt());
		map.put(APIConstants.IMPGSEZ, dto.getImpgsez());

		map.put(APIConstants.SUBTOTAL, dto.getSubtotal());
		return map;
	}

	private Map<String, RetItemDetailsDto> getAsRet3eMap(RetTbl3eDto dto) {
		Map<String, RetItemDetailsDto> map = new TreeMap<>();
		map.put(APIConstants.SUBTOTAL, dto.getSubtotal());
		return map;
	}

	private GetRetOutwardEntity setEntity(String key, RetItemDetailsDto val,
			String type, GetRetOutwardEntity entity) {

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
