package com.ey.advisory.app.processors.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.services.jobs.itc04.Itc04InvoicesAtGstn;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.dto.Itc04GetInvoicesReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Anand3.M
 *
 */
@Service("Itc04GstnGetJobHandlerImpl")
@Slf4j
public class Itc04GstnGetJobHandlerImpl implements Itc04GstnGetJobHandler {

	@Autowired
	@Qualifier("Itc04InvoicesAtGstnImpl")
	private Itc04InvoicesAtGstn itc04InvoicesAtGstn;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	@Transactional(value = "clientTransactionManager")
	@Override
	public void itc04GstnGetCall(String jsonReq, String groupCode) {

		JsonObject requestObject = (new JsonParser()).parse(jsonReq)
				.getAsJsonObject();

		Gson gson = GsonUtil.newSAPGsonInstance();
		Itc04GetInvoicesReqDto dto = gson.fromJson(requestObject,
				Itc04GetInvoicesReqDto.class);
		TenantContext.setTenantId(groupCode);

		GetAnx1BatchEntity batch = batchRepo
				.findByIdAndIsDeleteFalse(Long.valueOf(dto.getBatchId()));
		Long requestId = new Long(0);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Itc04 {} Get Call is Started.", APIConstants.GET);
		}
		requestId = itc04InvoicesAtGstn.findInvFromGstn(dto, groupCode,
				APIConstants.GET, batch.getId());

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Itc04 {} Get Call is Ended.", APIConstants.GET);
		}

		else {
			LOGGER.error("This section is not handled in ITC04 GET  {} :",
					batch.getType());
		}

		if (requestId != null) {
			batch.setStatus(JobStatusConstants.IN_PROGRESS.toUpperCase());
			batch.setRequestId(requestId);
			batchRepo.save(batch);
		}

	}

}
