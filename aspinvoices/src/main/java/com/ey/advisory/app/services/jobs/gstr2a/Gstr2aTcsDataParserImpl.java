/**
 * 
 */
package com.ey.advisory.app.services.jobs.gstr2a;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.GetGstr2aStagingTcsInvoicesEntity;
import com.ey.advisory.app.docs.dto.TcsCreditData;
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

/**
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("Gstr2aTcsDataParserImpl")
public class Gstr2aTcsDataParserImpl implements Gstr2aTcsDataParser {
	
	@Override
	public List<GetGstr2aStagingTcsInvoicesEntity> parseTcsData(
			Gstr1GetInvoicesReqDto dto, String apiResp, String type,
			Long batchId) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		List<GetGstr2aStagingTcsInvoicesEntity> enities = new ArrayList<>();

		JsonArray respObject = null;

		respObject = (new JsonParser()).parse(apiResp).getAsJsonObject()
				.get(APIConstants.TCS).getAsJsonArray();

		Type listType = new TypeToken<List<TcsCreditData>>() {
		}.getType();
		List<TcsCreditData> baseEntity = gson.fromJson(respObject, listType);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr2a GET TCS call Data Paring is in execution.");
		}
		for (TcsCreditData eachInv : baseEntity) {

			GetGstr2aStagingTcsInvoicesEntity entity = new GetGstr2aStagingTcsInvoicesEntity();

			entity.setChkSum(eachInv.getCheckSum());
			entity.setETin(eachInv.getETin());
			entity.setMerchantId(eachInv.getMId());
			entity.setSupVal(eachInv.getSupVal());
			entity.setTaxbleVal(eachInv.getTaxVal());
			//entity.setIgstRate(eachInv.getIgstRate());
			entity.setIgstAmt(eachInv.getIgstAmt());
		//	entity.setCgstRate(eachInv.getCgstRate());
			entity.setCgstAmt(eachInv.getCgstAmt());
			//entity.setSgstRate(eachInv.getSgstRate());
			entity.setSgstAmt(eachInv.getSgstAmt());
			//entity.setCessRate(eachInv.getCessRate());
			entity.setCessAmt(eachInv.getCessAmt());
			String returnPeriod = dto.getReturnPeriod();
			Integer  derivedRetPeriod = GenUtil
					 .convertTaxPeriodToInt(dto.getReturnPeriod());
			/*if (dto.getReturnPeriod() != null && 
					dto.getReturnPeriod().matches("[0-9]+")) {
				if (dto.getReturnPeriod().length() == 6) {
					int months = 
							Integer.valueOf(dto.getReturnPeriod().substring(0, 2));
					int year = 
							Integer.valueOf(dto.getReturnPeriod().substring(2));
					if ((months < 12 && months > 01)
							&& (year < 9999 && year > 0000)) {
						 derivedRetPeriod = GenUtil
								.convertTaxPeriodToInt(dto.getReturnPeriod());
					}}}*/
			entity.setRetPeriod(returnPeriod);
			entity.setDerRetPeriod(derivedRetPeriod);
			entity.setBatchId(batchId);
			entity.setCreatedBy(APIConstants.SYSTEM);
			entity.setModifiedOn(LocalDateTime.now());
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			entity.setCreatedOn(convertNow);
			entity.setCreatedBy("SYSTEM");

			enities.add(entity);

		}
		return enities;
	}

}
