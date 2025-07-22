package com.ey.advisory.app.processors.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.services.jobs.gstr6.Gstr6InvoicesAtGstn;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.dto.Gstr6GetInvoicesReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Anand3.M
 *
 */
@Service("Gstr6GstnGetJobHandlerImpl")
@Slf4j
public class Gstr6GstnGetJobHandlerImpl implements Gstr6GstnGetJobHandler {

	@Autowired
	@Qualifier("Gstr6B2bInvoicesAtGstnImpl")
	private Gstr6InvoicesAtGstn gstr6B2bAtGstn;

	@Autowired
	@Qualifier("Gstr6B2baInvoicesAtGstnImpl")
	private Gstr6InvoicesAtGstn gstr6B2baAtGstn;

	@Autowired
	@Qualifier("Gstr6CdnInvoicesAtGstnImpl")
	private Gstr6InvoicesAtGstn gstr6CdnAtGstn;

	@Autowired
	@Qualifier("Gstr6CdnaInvoicesAtGstnImpl")
	private Gstr6InvoicesAtGstn gstr6CdnaATGstn;

	@Autowired
	@Qualifier("Gstr6IsdInvoicesAtGstnImpl")
	private Gstr6InvoicesAtGstn gstr6IsdATGstn;

	@Autowired
	@Qualifier("Gstr6IsdaInvoicesAtGstnImpl")
	private Gstr6InvoicesAtGstn gstr6IsdaATGstn;

	@Autowired
	@Qualifier("Gstr6ItcInvoicesAtGstnImpl")
	private Gstr6InvoicesAtGstn gstr6ItcATGstn;

	@Autowired
	@Qualifier("Gstr6LateFeeInvoicesAtGstnImpl")
	private Gstr6InvoicesAtGstn gstr6LateFeeATGstn;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	@Transactional(value = "clientTransactionManager")
	@Override
	public void gstr6GstnGetCall(String jsonReq, String groupCode) {

		JsonObject requestObject = (new JsonParser()).parse(jsonReq).getAsJsonObject();
		// JsonArray asJsonArray = requestObject.get("req").getAsJsonArray();
		Gson gson = GsonUtil.newSAPGsonInstance();
		Gstr6GetInvoicesReqDto dto = gson.fromJson(requestObject, Gstr6GetInvoicesReqDto.class);
		TenantContext.setTenantId(groupCode);

		GetAnx1BatchEntity batch = batchRepo.findByIdAndIsDeleteFalse(Long.valueOf(dto.getBatchId()));
		Long requestId = new Long(0);
		if (batch.getType().equalsIgnoreCase(APIConstants.B2B)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr6 {} Get Call is Started.", APIConstants.B2B);
			}
			requestId = gstr6B2bAtGstn.findInvFromGstn(dto, groupCode, APIConstants.B2B, batch.getId());

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr6 {} Get Call is Ended.", APIConstants.B2B);
			}
		}

		else if (batch.getType().equalsIgnoreCase(APIConstants.B2BA)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr6 {} Get Call is Started.", APIConstants.B2BA);
			}
			requestId = gstr6B2baAtGstn.findInvFromGstn(dto, groupCode, APIConstants.B2BA, batch.getId());

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr6 {} Get Call is Ended.", APIConstants.B2BA);
			}
		} else if (batch.getType().equalsIgnoreCase(APIConstants.CDN)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr6 {} Get Call is Started.", APIConstants.CDN);
			}
			requestId = gstr6CdnAtGstn.findInvFromGstn(dto, groupCode, APIConstants.CDN, batch.getId());

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr6 {} Get Call is Ended.", APIConstants.CDN);
			}
		} else if (batch.getType().equalsIgnoreCase(APIConstants.CDNA)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr6 {} Get Call is Started.", APIConstants.CDNA);
			}
			requestId = gstr6CdnaATGstn.findInvFromGstn(dto, groupCode, APIConstants.CDNA, batch.getId());

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr6 {} Get Call is Ended.", APIConstants.CDNA);
			}
		} else if (batch.getType().equalsIgnoreCase(APIConstants.ISD)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr6 {} Get Call is Started.", APIConstants.ISD);
			}
			requestId = gstr6IsdATGstn.findInvFromGstn(dto, groupCode, APIConstants.ISD, batch.getId());

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr6 {} Get Call is Ended.", APIConstants.ISD);
			}
		}

		else if (batch.getType().equalsIgnoreCase(APIConstants.ISDA)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr6 {} Get Call is Started.", APIConstants.ISDA);
			}
			requestId = gstr6IsdaATGstn.findInvFromGstn(dto, groupCode, APIConstants.ISDA, batch.getId());

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr6 {} Get Call is Ended.", APIConstants.ISDA);
			}
		} else if (batch.getType().equalsIgnoreCase(APIConstants.ITC)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr6 {} Get Call is Started.", APIConstants.ITC);
			}
			requestId = gstr6ItcATGstn.findInvFromGstn(dto, groupCode, APIConstants.ITC, batch.getId());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr6 {} Get Call is Ended.", APIConstants.ITC);
			}
		} else if (batch.getType().equalsIgnoreCase(APIConstants.LATEFEE)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr6 {} Get Call is Started.", APIConstants.LATEFEE);
			}
			requestId = gstr6LateFeeATGstn.findInvFromGstn(dto, groupCode, APIConstants.LATEFEE, batch.getId());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr6 {} Get Call is Ended.", APIConstants.LATEFEE);
			}

		} else {
			LOGGER.error("This section is not handled in GSTR6 GET  {} :", batch.getType());
		}
		if (requestId != null) {
			batch.setStatus(JobStatusConstants.IN_PROGRESS.toUpperCase());
			batch.setRequestId(requestId);
			batchRepo.save(batch);
		}

	}
}
