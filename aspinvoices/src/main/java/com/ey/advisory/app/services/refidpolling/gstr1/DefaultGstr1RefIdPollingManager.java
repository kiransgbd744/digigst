package com.ey.advisory.app.services.refidpolling.gstr1;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.ReturnStatusRefIdDto;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.PollingMessage;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("DefaultGSTR1RefIdPollingManager")
public class DefaultGstr1RefIdPollingManager
		implements GSTR1RefIdPollingManager {

	@Autowired
	@Qualifier("DefaultBatchIdPollingManager")
	private SaveBatchIdPollingManager batchManager;

	@Autowired
	@Qualifier("batchSaveStatusRepository")
	private Gstr1BatchRepository gstr1BatchRepository;

	@Override
	public ResponseEntity<String> processGstr1RefIds(PollingMessage reqDto,
			String groupcode) {

		// Fetch all the batches that have gstn status as 'E' or NULL and
		// the REFID as non null.
		// InvoicesRefIdStatus invRefStatus = new InvoicesRefIdStatus();
		List<Gstr1SaveBatchEntity> batches = new ArrayList<Gstr1SaveBatchEntity>();
		Gson gson = new Gson();
		List<ReturnStatusRefIdDto> b2bRefIdStatusList = new ArrayList<ReturnStatusRefIdDto>();
		TenantContext.setTenantId(groupcode);
		try {
			// batches = gstr1BatchRepository.findReferenceId();
			batches = gstr1BatchRepository.findReferenceIdForPooling(
					reqDto.getReturnType().toUpperCase());

			batches.forEach(batch -> {
				ReturnStatusRefIdDto b2bRefIdStatus = batchManager
						.processBatch(batch);
				b2bRefIdStatusList.add(b2bRefIdStatus);
			});

			// invRefStatus.setResp(b2bRefIdStatusList);
			// return gson.toJson(invRefStatus);
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(b2bRefIdStatusList);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			String msg = "Unexpected error while updating documents to GSTN";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}

	@Override
	public ResponseEntity<String> processAnx1RefIds(PollingMessage reqDto,
			String groupcode) {
		return null;
	}

}
