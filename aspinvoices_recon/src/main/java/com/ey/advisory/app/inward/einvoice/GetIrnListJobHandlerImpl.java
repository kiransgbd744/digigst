package com.ey.advisory.app.inward.einvoice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.core.dto.Gstr2aDataAtGstn;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Service("GetIrnListJobHandlerImpl")
@Slf4j
public class GetIrnListJobHandlerImpl implements GetIrnListJobHandler {

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	@Autowired
	@Qualifier("GetIrnDataAtGstnImpl")
	private Gstr2aDataAtGstn gstr2aDataAtGstn;

	@Override
	public void getIrnListCall(String jsonReq, String groupCode) {

		try {
			JsonObject requestObject = JsonParser.parseString(jsonReq)
					.getAsJsonObject();
			Gson gson = GsonUtil.newSAPGsonInstance();
			Gstr1GetInvoicesReqDto dto = gson.fromJson(requestObject,
					Gstr1GetInvoicesReqDto.class);
			GetAnx1BatchEntity batch = batchRepo
					.findByIdAndIsDeleteFalse(dto.getBatchId());
			Long requestId = null;

			if (batch != null) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(" Get IRN List Call for {} is Started.",
							batch.getType());
				}
				requestId = findInvFromGstn(dto, groupCode, batch.getType(),
						batch.getId(), batch.getApiSection());

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Get IRN List Call for {} is Ended.",
							batch.getType());
				}
			} else {
				LOGGER.error(
						"batch.getType() has NULL value for the params {} in IRN List GET Call client {} :",
						jsonReq, groupCode);
				return;
			}
			batch.setStatus(JobStatusConstants.IN_PROGRESS.toUpperCase());
			batch.setRequestId(requestId);
			batchRepo.save(batch);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new AppException(e.getMessage(), e);
		}

	}

	public Long findInvFromGstn(Gstr1GetInvoicesReqDto dto, String groupCode,
			String type, Long batchId, String apiSection) {

		Long reqId = null;
		Gson gson = GsonUtil.newSAPGsonInstance();

		if (dto.getGroupcode() == null
				|| dto.getGroupcode().trim().length() == 0) {
			dto.setGroupcode(groupCode);
		}
		dto.setBatchId(batchId);
		dto.setType(type);
		dto.setApiSection(apiSection);
		String json = gson.toJson(dto);
		reqId = gstr2aDataAtGstn.findGstr2aDataAtGstn(dto, groupCode, type,
				json);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					" Irn {} Get List BatchId {} and framework requestId {} .",
					type, batchId, reqId);
		}

		return reqId;
	}
}
