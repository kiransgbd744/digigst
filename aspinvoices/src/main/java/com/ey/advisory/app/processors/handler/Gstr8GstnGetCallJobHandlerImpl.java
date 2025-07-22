/**
 * 
 */
package com.ey.advisory.app.processors.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.services.jobs.gstr8.Gstr8InvoicesAtGstn;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */
@Service("Gstr8GstnGetCallJobHandlerImpl")
@Slf4j
public class Gstr8GstnGetCallJobHandlerImpl
		implements Gstr8GstnGetCallJobHandler {

	@Autowired
	@Qualifier("Gstr8UrdUrdaAtGstnImpl")
	private Gstr8InvoicesAtGstn gstr8UrdUrdaAtGstn;

	@Autowired
	@Qualifier("Gstr8TcsTcsaAtGstnImpl")
	private Gstr8InvoicesAtGstn gstr8TcsTcsaAtGstn;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	@Transactional(value = "clientTransactionManager")
	@Override
	public void gstr8GstnGetCall(String jsonReq, String groupCode) {

		JsonObject requestObject = JsonParser.parseString(jsonReq)
				.getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		Gstr1GetInvoicesReqDto dto = gson.fromJson(requestObject,
				Gstr1GetInvoicesReqDto.class);
		TenantContext.setTenantId(groupCode);

		GetAnx1BatchEntity batch = batchRepo
				.findByIdAndIsDeleteFalse(Long.valueOf(dto.getBatchId()));
		Long requestId = null;
		if (batch.getType().equalsIgnoreCase(APIConstants.TCS)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr8 {} Get Call is Started.", APIConstants.TCS);
			}
			requestId = gstr8TcsTcsaAtGstn.findInvFromGstn(dto, groupCode,
					APIConstants.TCS, batch.getId());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr8 {} Get Call is Ended.", APIConstants.TCS);
			}
		}
		if (batch.getType().equalsIgnoreCase(APIConstants.URD)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr8 {} Get Call is Started.", APIConstants.URD);
			}
			requestId = gstr8UrdUrdaAtGstn.findInvFromGstn(dto, groupCode,
					APIConstants.URD, batch.getId());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr8 {} Get Call is Ended.", APIConstants.URD);

			}
		} else {
			LOGGER.error("This section is not handled in GSTR8 GET  {} :",
					batch.getType());
		}
		if (requestId != null) {
			batch.setStatus(JobStatusConstants.IN_PROGRESS.toUpperCase());
			batch.setRequestId(requestId);
			batchRepo.save(batch);
		}
	}

}
