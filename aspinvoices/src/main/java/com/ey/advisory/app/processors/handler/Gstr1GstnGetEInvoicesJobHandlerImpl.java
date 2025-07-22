package com.ey.advisory.app.processors.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.services.jobs.gstr1.Gstr1InvoicesAtGstn;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Anand3.M
 *
 */
@Service("Gstr1GstnGetEInvoicesJobHandlerImpl")
@Slf4j
public class Gstr1GstnGetEInvoicesJobHandlerImpl
		implements Gstr1GstnGetJobHandler {

	@Autowired
	@Qualifier("Gstr1B2bEInvAtGstnImpl")
	private Gstr1InvoicesAtGstn gstr1B2bAtGstn;

	@Autowired
	@Qualifier("Gstr1CdnEInvAtGstnImpl")
	private Gstr1InvoicesAtGstn gstr1CdnAtGstn;

	@Autowired
	@Qualifier("Gstr1CdnurEInvAtGstnImpl")
	private Gstr1InvoicesAtGstn gstr1CdnurAtGstn;

	@Autowired
	@Qualifier("Gstr1ExpEInvAtGstnImpl")
	private Gstr1InvoicesAtGstn gstr1ExpATGstn;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	@Transactional(value = "clientTransactionManager")
	@Override
	public void gstr1GstnGetCall(String jsonReq, String groupCode) {

		JsonObject requestObject = (new JsonParser()).parse(jsonReq)
				.getAsJsonObject();
		// JsonArray asJsonArray = requestObject.get("req").getAsJsonArray();
		Gson gson = GsonUtil.newSAPGsonInstance();
		Gstr1GetInvoicesReqDto dto = gson.fromJson(requestObject,
				Gstr1GetInvoicesReqDto.class);

		GetAnx1BatchEntity batch = batchRepo
				.findByIdAndIsDeleteFalse(Long.valueOf(dto.getBatchId()));
		Long requestId = new Long(0);
		if (batch != null) {
			if (batch.getType().equalsIgnoreCase(APIConstants.EINV_B2B)) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Gstr1 {} Get Einvoices Call is Started.",
							APIConstants.EINV_B2B);
				}
				requestId = gstr1B2bAtGstn.findInvFromGstn(dto, groupCode,
						APIConstants.EINV_B2B, batch.getId());

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Gstr1 {} Get Einvoices Call is Ended.",
							APIConstants.EINV_B2B);
				}
			}

			else if (batch.getType().equalsIgnoreCase(APIConstants.EINV_CDNR)) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Gstr1 {} Get Einvoices Call is Started.",
							APIConstants.EINV_CDNR);
				}
				requestId = gstr1CdnAtGstn.findInvFromGstn(dto, groupCode,
						APIConstants.EINV_CDNR, batch.getId());

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Gstr1 {} Get Einvoices Call is Ended.",
							APIConstants.EINV_CDNR);
				}
			} else if (batch.getType()
					.equalsIgnoreCase(APIConstants.EINV_CDNUR)) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Gstr1 {} Get Einvoices Call is Started.",
							APIConstants.EINV_CDNUR);
				}
				requestId = gstr1CdnurAtGstn.findInvFromGstn(dto, groupCode,
						APIConstants.EINV_CDNUR, batch.getId());

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Gstr1 {} Get Einvoices Call is Ended.",
							APIConstants.EINV_CDNUR);
				}
			} else if (batch.getType()
					.equalsIgnoreCase(APIConstants.EINV_EXP)) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Gstr1 {} Get Einvoices Call is Started.",
							APIConstants.EINV_EXP);
				}
				requestId = gstr1ExpATGstn.findInvFromGstn(dto, groupCode,
						APIConstants.EINV_EXP, batch.getId());

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Gstr1 {} Get Einvoices Call is Ended.",
							APIConstants.EINV_EXP);
				}
			}

			else {
				LOGGER.error(
						"This section is not handled in GSTR1 GET-EInvoices  {} :",
						batch.getType());
			}
		}

		if (requestId != null) {
			batch.setStatus(JobStatusConstants.IN_PROGRESS.toUpperCase());
			batch.setRequestId(requestId);
			batchRepo.save(batch);
		}

	}
}
