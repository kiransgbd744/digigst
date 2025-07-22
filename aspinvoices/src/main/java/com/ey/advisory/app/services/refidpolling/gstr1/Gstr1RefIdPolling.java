package com.ey.advisory.app.services.refidpolling.gstr1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.docs.dto.ReturnStatusRefIdDto;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.PollingMessage;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Nandam
 *
 */
@Slf4j
@Component("Gstr1RefIdPolling")
public class Gstr1RefIdPolling {

	@Autowired
	@Qualifier("DefaultBatchIdPollingManager")
	private SaveBatchIdPollingManager batchManager;

	@Autowired
	@Qualifier("Gstr1ADefaultBatchIdPollingManager")
	private SaveBatchIdPollingManager gstr1AbatchManager;

	@Transactional(value = "clientTransactionManager")
	public ResponseEntity<String> processRefIds(PollingMessage reqDto,
			String groupcode) {

		Gson gson = new Gson();

		ReturnStatusRefIdDto b2bRefIdStatus = null;
		if (reqDto != null) {

			Gstr1SaveBatchEntity batch = new Gstr1SaveBatchEntity();
			batch.setId(reqDto.getBatchId());
			batch.setSgstin(reqDto.getGstin());
			batch.setReturnPeriod(reqDto.getTaxPeriod());
			batch.setRefId(reqDto.getRefId());
			batch.setSection(reqDto.getSection());
			batch.setReturnType(reqDto.getReturnType());
			batch.setGroupCode(groupcode);
			batch.setUserRequestId(reqDto.getUserRequestId());
			batch.setRetryCount(reqDto.getRetryCount());
			batch.setOperationType(reqDto.getOperationType());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Pooling Gstr1 ReturnStatus- batch is {} ", batch);
			}

			if (APIConstants.GSTR1A.equalsIgnoreCase(reqDto.getReturnType())) {
				b2bRefIdStatus = gstr1AbatchManager.processBatch(batch);

			} else {
				b2bRefIdStatus = batchManager.processBatch(batch);
			}
		}

		JsonObject resp = new JsonObject();
		JsonElement respBody = gson.toJsonTree(b2bRefIdStatus);
		resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
		resp.add("resp", respBody);
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

	}

}
