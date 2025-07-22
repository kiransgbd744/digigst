package com.ey.advisory.app.processors.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.services.jobs.gstr6a.Gstr6aInvoicesAtGstn;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.dto.Gstr6aGetInvoicesReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Anand3.M
 *
 */
@Service("Gstr6aGstnGetJobHandlerImpl")
@Slf4j
public class Gstr6aGstnGetJobHandlerImpl implements Gstr6aGstnGetJobHandler {

	@Autowired
	@Qualifier("Gstr6aB2bInvoicesAtGstnImpl")
	private Gstr6aInvoicesAtGstn gstr6aB2bAtGstn;

	@Autowired
	@Qualifier("Gstr6aB2baInvoicesAtGstnImpl")
	private Gstr6aInvoicesAtGstn gstr6aB2baAtGstn;

	@Autowired
	@Qualifier("Gstr6aCdnInvoicesAtGstnImpl")
	private Gstr6aInvoicesAtGstn gstr6aCdnAtGstn;

	@Autowired
	@Qualifier("Gstr6aCdnaInvoicesAtGstnImpl")
	private Gstr6aInvoicesAtGstn gstr6aCdnaATGstn;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	@Transactional(value = "clientTransactionManager")
	@Override
	public void gstr6aGstnGetCall(String jsonReq, String groupCode) {

		JsonObject requestObject = (new JsonParser()).parse(jsonReq).getAsJsonObject();
		// JsonArray asJsonArray = requestObject.get("req").getAsJsonArray();
		Gson gson = GsonUtil.newSAPGsonInstance();
		Gstr6aGetInvoicesReqDto dto = gson.fromJson(requestObject, Gstr6aGetInvoicesReqDto.class);
		TenantContext.setTenantId(groupCode);

		GetAnx1BatchEntity batch = batchRepo.findByIdAndIsDeleteFalse(Long.valueOf(dto.getBatchId()));
		Long requestId = new Long(0);
		if (batch.getType().equalsIgnoreCase(APIConstants.B2B)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr6a {} Get Call is Started.", APIConstants.B2B);
			}
			requestId = gstr6aB2bAtGstn.findInvFromGstn(dto, groupCode, APIConstants.B2B, batch.getId());

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr6a {} Get Call is Ended.", APIConstants.B2B);
			}
		}

		else if (batch.getType().equalsIgnoreCase(APIConstants.B2BA)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr6a {} Get Call is Started.", APIConstants.B2BA);
			}
			requestId = gstr6aB2baAtGstn.findInvFromGstn(dto, groupCode, APIConstants.B2BA, batch.getId());

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr6a {} Get Call is Ended.", APIConstants.B2BA);
			}
		} else if (batch.getType().equalsIgnoreCase(APIConstants.CDN)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr6a {} Get Call is Started.", APIConstants.CDN);
			}
			requestId = gstr6aCdnAtGstn.findInvFromGstn(dto, groupCode, APIConstants.CDN, batch.getId());

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr6a {} Get Call is Ended.", APIConstants.CDN);
			}
		} else if (batch.getType().equalsIgnoreCase(APIConstants.CDNA)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr6a {} Get Call is Started.", APIConstants.CDNA);
			}
			requestId = gstr6aCdnaATGstn.findInvFromGstn(dto, groupCode, APIConstants.CDNA, batch.getId());

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr6a {} Get Call is Ended.", APIConstants.CDNA);
			}
		} else {
			LOGGER.error("This section is not handled in GSTR6a GET  {} :", batch.getType());
		}
		if (requestId != null) {
			batch.setStatus(JobStatusConstants.IN_PROGRESS.toUpperCase());
			batch.setRequestId(requestId);
			batchRepo.save(batch);
		}

	}
}
