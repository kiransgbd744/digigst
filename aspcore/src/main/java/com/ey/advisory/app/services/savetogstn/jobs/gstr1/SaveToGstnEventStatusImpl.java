package com.ey.advisory.app.services.savetogstn.jobs.gstr1;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.SaveToGstnEventStatusEntity;
import com.ey.advisory.app.data.repositories.client.SaveToGstnEventStatusRepository;
import com.ey.advisory.app.data.repositories.client.SaveToGstnMasterStatusRepository;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.SaveToGstnEventStatusDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;
/**
 * 
 * @author Hemasundar.J
 *
 */
@Service("saveToGstnEventStatusImpl")
@Slf4j
public class SaveToGstnEventStatusImpl implements SaveToGstnEventStatus {

	@Autowired
	@Qualifier("saveToGstnEventStatusRepository")
	SaveToGstnEventStatusRepository saveToGstnEventStatusRepository;

	@Autowired
	@Qualifier("saveToGstnMasterStatusRepository")
	SaveToGstnMasterStatusRepository saveToGstnMasterStatusRepository;

	@Override
	public JsonObject findLatestStatus(String jsonString) {

		JsonObject reqObject = null;
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			Gson gson = GsonUtil.newSAPGsonInstance();
			reqObject = requestObject.get("req").getAsJsonObject();
			SaveToGstnEventStatusDto dto = gson.fromJson(reqObject,
					SaveToGstnEventStatusDto.class);
			String retPeriod = dto.getRetPeriod();
			String sgstin = dto.getSgstin();
			Integer code = saveToGstnEventStatusRepository.findStatsCode(retPeriod,
					sgstin);
			reqObject.addProperty("statusCode", code);
			String status = saveToGstnMasterStatusRepository.findByCode(code);
			reqObject.addProperty("saveStatus", status);
		} catch (JsonParseException e) {
			String msg = "Json Parse Exception";
			LOGGER.error(msg, e);
		} catch (DataAccessException e) {
			String msg = "Database Interaction Exception";
			LOGGER.error(msg, e);
		}
		return reqObject;
	}

	@Override
	public SaveToGstnEventStatusEntity EventEntry(String taxPeriod,
			String gstin, int statusCode, String groupCode) {
		LocalDateTime now = EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now());

		SaveToGstnEventStatusEntity entity = new SaveToGstnEventStatusEntity();
		TenantContext.setTenantId(groupCode);

		int derTaxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriod);

		entity.setTaxPeriod(taxPeriod);
		entity.setGstin(gstin);
		entity.setDerivedTaxperiod(derTaxPeriod);
		entity.setCreatedDate(now);
		entity.setCreatedBy(APIConstants.SYSTEM.toUpperCase());
		entity.setStatusCode(statusCode);
		entity = saveToGstnEventStatusRepository.save(entity);
		return entity;
	}
	
	@Override
	public SaveToGstnEventStatusEntity EventEntry(String taxPeriod,
			String gstin, int statusCode, String groupCode, String section) {
		LocalDateTime now = EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now());

		SaveToGstnEventStatusEntity entity = new SaveToGstnEventStatusEntity();
		TenantContext.setTenantId(groupCode);

		int derTaxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriod);

		entity.setTaxPeriod(taxPeriod);
		entity.setGstin(gstin);
		entity.setDerivedTaxperiod(derTaxPeriod);
		entity.setCreatedDate(now);
		entity.setCreatedBy(APIConstants.SYSTEM.toUpperCase());
		entity.setStatusCode(statusCode);
		entity.setSection(section);
		entity = saveToGstnEventStatusRepository.save(entity);
		return entity;
	}

	
	@Override
	public Integer findLastJobStatusCode(String gstin, String retPeriod,
			String groupCode) {
		
		TenantContext.setTenantId(groupCode);
		Integer code = saveToGstnEventStatusRepository.findStatsCode(retPeriod,
				gstin);

		return code;
	}
	
	@Override
	public SaveToGstnEventStatusEntity Itc04EventEntry(String taxPeriod,
			String gstin, int statusCode, String groupCode) {
		LocalDateTime now = EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now());

		SaveToGstnEventStatusEntity entity = new SaveToGstnEventStatusEntity();
		TenantContext.setTenantId(groupCode);

		//int derTaxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriod);

		entity.setTaxPeriod(taxPeriod);
		entity.setGstin(gstin);
		//entity.setDerivedTaxperiod(derTaxPeriod);
		entity.setCreatedDate(now);
		entity.setCreatedBy(APIConstants.SYSTEM.toUpperCase());
		entity.setStatusCode(statusCode);
		entity = saveToGstnEventStatusRepository.save(entity);
		return entity;
	}

}
