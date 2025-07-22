package com.ey.advisory.app.services.jobs.gstr1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Umesha.M
 *
 */
@Slf4j
@Component("gstr1B2clB2claAtGstnImpl")
public class Gstr1B2clB2claAtGstnImpl implements Gstr1InvoicesAtGstn {

	@Autowired
	@Qualifier("gstr1B2clB2claDataAtGstnImpl")
	Gstr1B2clB2claDataAtGstnImpl gstr1B2clB2claDataAtGstnImpl;
	
	/**
	 * Find the B2CL From GSTN
	 */
	public Long findInvFromGstn(Gstr1GetInvoicesReqDto dto,
			String groupCode, String type,Long batchId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("I AM FROM findBTCLFromGstn methods");
		}
		Long reqId = null;
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
/*
			batch = batchUtil.makeBatchGstr1(dto, type);
			TenantContext.setTenantId(groupCode);
			// InActiveting Previous Batch Records
			batchRepo.softlyDelete(type.toUpperCase(),
					APIConstants.GSTR1.toUpperCase(), dto.getGstin(),
					dto.getReturnPeriod());
			// Save new Batch
			batch = batchRepo.save(batch);*/

			if (dto.getGroupcode() == null
					|| dto.getGroupcode().trim().length() == 0) {
				dto.setGroupcode(groupCode);
			}
			dto.setType(type);
			dto.setBatchId(batchId);
			String json = gson.toJson(dto);

			reqId = gstr1B2clB2claDataAtGstnImpl.findB2CLDataAtGstn(dto,
					groupCode, type, json);

		} catch (Exception ex) {
			if (LOGGER.isErrorEnabled()) {
				LOGGER.error(ex.getMessage());
			}
			throw new APIException(
					"Unexpected error while executing Gstr1 Get B2cl/B2cla");

		}
		/*JsonObject resp = new JsonObject();
		JsonElement respBody = gson.toJsonTree(apiResp);
		resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
		resp.add("resp", respBody);*/

		return reqId;
	}
}
