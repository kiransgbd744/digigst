package com.ey.advisory.app.services.jobs.gstr1A;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.gstr1A.entities.client.GetGstr1ANilRatedEntity;
import com.ey.advisory.app.docs.dto.NilInvoices;
import com.ey.advisory.app.docs.dto.NilSupplies;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

@Component("gstr1ANilRatedSupDataParserImpl")
@Slf4j
public class GSTR1ANilRatedSupDataParserImpl
		implements GSTR1ANilRatedSupDataParser {

	@Override
	public List<GetGstr1ANilRatedEntity> gstr1NilRatedSupDataParser(
			Gstr1GetInvoicesReqDto dto, String apiResp) {

		List<GetGstr1ANilRatedEntity> entities = new ArrayList<>();

		JsonObject jsonObject = null;
		Gson gson = GsonUtil.newSAPGsonInstance();
		//try {
			if (APIConstants.NIL.equalsIgnoreCase(dto.getType())) {
				jsonObject = (new JsonParser().parse(apiResp)).getAsJsonObject()
						.get(APIConstants.NIL).getAsJsonObject();
			}
			Type listType = new TypeToken<NilSupplies>() {
			}.getType();

			NilSupplies nilSupplies = gson.fromJson(jsonObject, listType);
			List<NilInvoices> nilInvoices = nilSupplies.getNilInbvoices();
			nilInvoices.forEach(nilInvoice -> {
				GetGstr1ANilRatedEntity gstr1NilDetailsEntity = new GetGstr1ANilRatedEntity();
				/**
				 * Input data
				 */
				gstr1NilDetailsEntity.setGstin(dto.getGstin());
				gstr1NilDetailsEntity.setReturnPeriod(dto.getReturnPeriod());
				
				if (dto.getReturnPeriod() != null && dto.getReturnPeriod().length() > 0) {
					gstr1NilDetailsEntity.setDerivedTaxperiod(GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
				}
				gstr1NilDetailsEntity.setBatchId(dto.getBatchId());

				gstr1NilDetailsEntity.setFlag(nilSupplies.getFlag());
				gstr1NilDetailsEntity.setInvChksum(nilSupplies.getCheckSum());
				gstr1NilDetailsEntity
						.setSuppType(nilInvoice.getNatureOfSupType());

				gstr1NilDetailsEntity
						.setNilAmt(nilInvoice.getTotalNilRatedOutwordSup());
				gstr1NilDetailsEntity
						.setExptAmt(nilInvoice.getTotalExemptedOutwordSup());
				gstr1NilDetailsEntity
						.setNonSupAmt(nilInvoice.getTotalNonGstOutwordSup());
				entities.add(gstr1NilDetailsEntity);
			});

		/*} catch (Exception e) {
			String msg = "Failed to Parse Nil Rated Supply";
			LOGGER.error(msg, e);
			throw new APIException(msg);
		}*/
		return entities;
	}

}
