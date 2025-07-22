/**
 * 
 */
package com.ey.advisory.app.services.jobs.anx1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Anx1GetInvoicesReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Service("anx1EcomInvoicesAtGstnImpl")
@Slf4j
public class Anx1EcomInvoicesAtGstnImpl implements Anx1InvoicesAtGstn {

	@Autowired
	@Qualifier("anx1DataAtGstnImpl")
	private Anx1DataAtGstn anx1DataAtGstn;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	private GetAnx1BatchEntity batch;

	@Override
	public ResponseEntity<String> findInvFromGstn(Anx1GetInvoicesReqDto dto,
			String groupCode, String type) {

		String apiResp = null;

		Gson gson = GsonUtil.newSAPGsonInstance();

		try {

			batch = batchUtil.makeBatch(dto, type);
			TenantContext.setTenantId(groupCode);
			// InActiveting Previous Batch Records
			batchRepo.softlyDelete(type.toUpperCase(),
					APIConstants.ANX1.toUpperCase(), dto.getGstin(),
					dto.getReturnPeriod());
			// Save new Batch
			batch = batchRepo.save(batch);

			if (dto.getGroupcode() == null
					|| dto.getGroupcode().trim().length() == 0) {
				dto.setGroupcode(groupCode);
			}
			dto.setBatchId(batch.getId());
			String json = gson.toJson(dto);

			apiResp = anx1DataAtGstn.findAnx1DataAtGstn(dto, groupCode, type,
					json);

		} catch (Exception ex) {
			if (LOGGER.isErrorEnabled()) {
				LOGGER.error(ex.getMessage());
			}
			throw new APIException(
					"Unexpected error while executing Anx1 Get Ecom");

		}
		JsonObject resp = new JsonObject();
		JsonElement respBody = gson.toJsonTree(apiResp);
		resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
		resp.add("resp", respBody);

		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

}
