package com.ey.advisory.app.processors.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.services.jobs.gstr7.Gstr7InvoicesAtGstn;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.dto.Gstr7GetInvoicesReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Anand3.M
 *
 */
@Service("Gstr7GstnGetJobHandlerImpl")
@Slf4j
public class Gstr7GstnGetJobHandlerImpl implements Gstr7GstnGetJobHandler {

	@Autowired
	@Qualifier("Gstr7TdsInvoicesAtGstnImpl")
	private Gstr7InvoicesAtGstn gstr7B2bAtGstn;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	@Transactional(value = "clientTransactionManager")
	@Override
	public void gstr7GstnGetCall(String jsonReq, String groupCode) {

		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonReq)
					.getAsJsonObject();

			Gson gson = GsonUtil.newSAPGsonInstance();
			Gstr7GetInvoicesReqDto dto = gson.fromJson(requestObject,
					Gstr7GetInvoicesReqDto.class);
			GetAnx1BatchEntity batch = batchRepo
					.findByIdAndIsDeleteFalse(Long.valueOf(dto.getBatchId()));

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr7 {} Get Call is Started.", APIConstants.TDS);
			}
			Long requestId = gstr7B2bAtGstn.findInvFromGstn(dto, groupCode,
					APIConstants.TDS, batch.getId());

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr7 {} Get Call is Ended.", APIConstants.TDS);
			}

			if (requestId != null) {
				batch.setStatus(JobStatusConstants.IN_PROGRESS.toUpperCase());
				batch.setRequestId(requestId);
				batchRepo.save(batch);
			}
		} catch (Exception e) {
			String msg = "Exception While Invoking the Inv Method";
			LOGGER.error(msg, e);
			throw new APIException(msg);
		}

	}

}
