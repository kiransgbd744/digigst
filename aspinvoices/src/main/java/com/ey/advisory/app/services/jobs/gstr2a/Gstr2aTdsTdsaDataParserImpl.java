package com.ey.advisory.app.services.jobs.gstr2a;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.GetGstr2aStagingTDSADetailsEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingTDSDetailsEntity;
import com.ey.advisory.app.docs.dto.TDSADetails;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("gstr2aTdsTdsaDataParserImpl")
public class Gstr2aTdsTdsaDataParserImpl implements Gstr2aTdsTdsaDataParser {
	
	@Override
	public List<GetGstr2aStagingTDSDetailsEntity> parseTdsData(
			final Gstr1GetInvoicesReqDto dto, final String apiResp,
			final String type,Long batchId) {

		List<GetGstr2aStagingTDSDetailsEntity> entities = new ArrayList<>();
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray jsonObject = null;
		//try {
				jsonObject = (new JsonParser().parse(apiResp)).getAsJsonObject()
						.get(APIConstants.TDS).getAsJsonArray();
			
			
			Type listType = new TypeToken<List<TDSADetails>>() {
			}.getType();
			
			List<TDSADetails> baseEntity = gson.fromJson(jsonObject, listType);
			GetGstr2aStagingTDSDetailsEntity tdsEntity = null;
			for (TDSADetails tdsDetails : baseEntity) {
				tdsEntity = new GetGstr2aStagingTDSDetailsEntity();
				
				tdsEntity.setGstin(dto.getGstin());
				tdsEntity.setReturnPeriod(dto.getReturnPeriod());
				Integer  derivedRetPeriod = GenUtil
						 .convertTaxPeriodToInt(dto.getReturnPeriod());
				/*if (dto.getReturnPeriod() != null && 
						dto.getReturnPeriod().matches("[0-9]+")) {
					if (dto.getReturnPeriod().length() == 6) {
					int months = 
					Integer.valueOf(dto.getReturnPeriod().substring(0, 2));
						int year = 
								Integer.
								valueOf(dto.getReturnPeriod().substring(2));
						if ((months < 12 && months > 01)
								&& (year < 9999 && year > 0000)) {
							 derivedRetPeriod = GenUtil
							 .convertTaxPeriodToInt(dto.getReturnPeriod());
						}}}*/
				tdsEntity.setDerReturnPeriod(derivedRetPeriod);
				if (type.equalsIgnoreCase(APIConstants.TDSA)) {
					tdsEntity.setOrggstindeductee(tdsDetails.
							getOrggstindeductee());
				}
				tdsEntity.setGstindeductee(tdsDetails.getGstindeductee());
				tdsEntity.setAmtdeducted(tdsDetails.getAmtdeducted());
				tdsEntity.setIntegratedamt(tdsDetails.getIntegratedamt());
				tdsEntity.setCentralamt(tdsDetails.getCentralamt());
				tdsEntity.setStateamt(tdsDetails.getStateamt());
				LocalDateTime convertNow = EYDateUtil
						.toUTCDateTimeFromLocal(LocalDateTime.now());
				tdsEntity.setCreatedOn(convertNow);
				tdsEntity.setCreatedBy("SYSTEM");
				
				entities.add(tdsEntity);
			    }
				
				/*} catch (Exception e) {
			String msg = "Failed to parse TDS and TDSAS Data";
			if (LOGGER.isErrorEnabled()) {
				LOGGER.error(msg, e);
			}
		}*/
		return entities;
	}

	@Override
	public List<GetGstr2aStagingTDSADetailsEntity> parseTdsaData(
			Gstr1GetInvoicesReqDto dto, String apiResp, String type,
			Long batchId) {
		
		List<GetGstr2aStagingTDSADetailsEntity> entities = new ArrayList<>();
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray jsonObject = null;
		
		//try {
		jsonObject = (new JsonParser().parse(apiResp)).getAsJsonObject()
				.get(APIConstants.TDSA).getAsJsonArray();
		Type listType = new TypeToken<List<TDSADetails>>() {
		}.getType();
		
		List<TDSADetails> baseEntity = gson.fromJson(jsonObject, listType);
		GetGstr2aStagingTDSADetailsEntity tdsEntity = null;
		for (TDSADetails tdsDetails : baseEntity) {
			tdsEntity = new GetGstr2aStagingTDSADetailsEntity();
			
			tdsEntity.setGstin(dto.getGstin());
			tdsEntity.setReturnPeriod(dto.getReturnPeriod());
			Integer  derivedRetPeriod = GenUtil
					 .convertTaxPeriodToInt(dto.getReturnPeriod());
			/*if (dto.getReturnPeriod() != null && 
					dto.getReturnPeriod().matches("[0-9]+")) {
				if (dto.getReturnPeriod().length() == 6) {
				int months = 
				Integer.valueOf(dto.getReturnPeriod().substring(0, 2));
					int year = 
							Integer.
							valueOf(dto.getReturnPeriod().substring(2));
					if ((months < 12 && months > 01)
							&& (year < 9999 && year > 0000)) {
						 derivedRetPeriod = GenUtil
						 .convertTaxPeriodToInt(dto.getReturnPeriod());
					}}}*/
			tdsEntity.setDerReturnPeriod(derivedRetPeriod);
			if (type.equalsIgnoreCase(APIConstants.TDSA)) {
				tdsEntity.setOrggstindeductee(tdsDetails.getOrggstindeductee());
				//tdsEntity.setOrgamtdeducted(tdsDetails.getOrgamtdeducted());
				//tdsEntity.setMnthded(tdsDetails.getMnthded());
				
			}
			tdsEntity.setGstindeductee(tdsDetails.getGstindeductee());
			tdsEntity.setAmtdeducted(tdsDetails.getAmtdeducted());
			tdsEntity.setIntegratedamt(tdsDetails.getIntegratedamt());
			tdsEntity.setCentralamt(tdsDetails.getCentralamt());
			tdsEntity.setStateamt(tdsDetails.getStateamt());
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			tdsEntity.setCreatedOn(convertNow);
			tdsEntity.setCreatedBy("SYSTEM");
			
			entities.add(tdsEntity);
		    }
			
			/*} catch (Exception e) {
		String msg = "Failed to parse TDS and TDSAS Data";
		if (LOGGER.isErrorEnabled()) {
			LOGGER.error(msg, e);
		}
	}*/
	return entities;

	}
	

}
