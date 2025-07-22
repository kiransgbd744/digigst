package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.get.notices.handlers.GstNoticeDataAtGstn;
import com.ey.advisory.app.get.notices.handlers.GstNoticesReqDto;
import com.ey.advisory.app.ims.handlers.GetImsInvoicesJobHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.async.JobStatusConstants;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi.jain
 *
 */
@Service("GstNoticesGetProcessor")
@Slf4j
public class GstNoticesGetProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("GetImsInvoicesJobHandlerImpl")
	private GetImsInvoicesJobHandler imsInvoicesJobHandler;
	
	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	@Autowired
	@Qualifier("GstNoticeDataAtGstnImpl")
	private GstNoticeDataAtGstn gstr2aDataAtGstn;


	@Override
	public void execute(Message message, AppExecContext context) {
		try {
			String groupCode = message.getGroupCode();
			String jsonString = message.getParamsJson();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gst IMS Invoices Gstn Data Execute method is ON with "
								+ "groupcode {} and params {}",
						groupCode, jsonString);
			}
			getGstNoticesGetCall(jsonString, groupCode);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Get IMS Invoices Gstn Processed with args {} ",
						jsonString);

			}
		} catch (Exception ex) {
			String msg = "GetImsInvoicesProcessor got interrupted. "
					+ "Jobs might not be completed. Marking as 'Failed'";
			LOGGER.error(msg);
			throw new AppException(msg, ex);
		}
	}
	
	public void getGstNoticesGetCall(String jsonReq, String groupCode) {

		try {
			JsonObject requestObject = JsonParser.parseString(jsonReq)
					.getAsJsonObject();
			Gson gson = GsonUtil.newSAPGsonInstance();
			GstNoticesReqDto dto = gson.fromJson(requestObject,
					GstNoticesReqDto.class);
			GetAnx1BatchEntity batch = batchRepo
					.findByIdAndIsDeleteFalse(dto.getBatchId());
			Long requestId = null;

			if (batch != null) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(" Get IMS INVOICES Call for {} is Started.",
							batch.getType());
				}
				requestId = findInvFromGstn(dto, groupCode, batch.getType(),
						batch.getId(), batch.getApiSection());

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Get IMS INVOICES Call for {} is Ended.",
							batch.getType());
				}
			} else {
				LOGGER.error(
						"batch.getType() has NULL value for the params {} in IMS INVOICES GET Call client {} :",
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

	public Long findInvFromGstn(GstNoticesReqDto dto, String groupCode,
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
		reqId = gstr2aDataAtGstn.getGstNoticesData(dto, groupCode, type,
				json);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					" Irn {} Get List BatchId {} and framework requestId {} .",
					type, batchId, reqId);
		}

		return reqId;
	}
}
