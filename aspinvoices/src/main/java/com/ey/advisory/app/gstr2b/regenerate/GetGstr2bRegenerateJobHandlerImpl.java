package com.ey.advisory.app.gstr2b.regenerate;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.util.HitGstnServer;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.domain.client.Gstr2bRegenerateSaveBatchEntity;
import com.ey.advisory.repositories.client.Gstr2bRegenerateBatchRepository;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Service("GetGstr2bRegenerateJobHandlerImpl")
@Slf4j
public class GetGstr2bRegenerateJobHandlerImpl
		implements GetGstr2bRegenerateJobHandler {

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	@Autowired
	private Gstr2bRegenerateBatchRepository gstr2bBatchRepo;

	@Autowired
	@Qualifier("hitGstnServer")
	private HitGstnServer hitGstnServer;

	@Autowired
	@Qualifier("DefaultAPIExecutor")
	private APIExecutor apiExecutor;

	@Override
	public void getGstr2bRegenerateCall(String jsonReq, String groupCode) {

		JsonObject requestObject = JsonParser.parseString(jsonReq)
				.getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		Gstr1GetInvoicesReqDto dto = gson.fromJson(requestObject,
				Gstr1GetInvoicesReqDto.class);
		Long saveId = dto.getSaveId();

		GetAnx1BatchEntity batch = batchRepo
				.findByIdAndIsDeleteFalse(dto.getBatchId());

		Gstr2bRegenerateSaveBatchEntity savebatch = gstr2bBatchRepo
				.findById(saveId).get();

		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(" Get gstr2b regenerate Call for {} is Started.",
						batch.getType());
			}

			String refId = null;

			APIResponse resp = new APIResponse();
			TenantContext.setTenantId(groupCode);
			// Invoke the GSTN API - Get Return Status API and get the JSON.

			/*APIParam param1 = new APIParam("rtin", dto.getGstin());
			APIParam param2 = new APIParam("itcprd", dto.getReturnPeriod());
			*/APIParam param1 = new APIParam("ret_period", dto.getReturnPeriod());
			APIParam param2 = new APIParam("gstin", dto.getGstin());
			
			APIParams params = null;
			JsonObject obj = new JsonObject();
			obj.addProperty("rtin", dto.getGstin());
			obj.addProperty("itcprd", dto.getReturnPeriod());
			
			params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.GSTR2B_REGENERATE, param1, param2);
			
			LocalDateTime now = LocalDateTime.now();

			resp = apiExecutor.execute(params, gson.toJson(obj));
			
			LOGGER.error(" GSTR2B Regeneration api response - {} ",resp);
			
			if (resp.isSuccess()) {

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"GSTR2B ReGenerate Completed"
									+ " for Polling and recieved response as :%s",
							resp.getResponse());
					LOGGER.debug(msg);
				}

				String saveJsonResp = resp.getResponse();
				JsonParser jsonParser = new JsonParser();
				JsonObject jsonObject = (JsonObject) jsonParser
						.parse(saveJsonResp);
				refId = jsonObject.get(APIIdentifiers.REFERENECE_ID)
						.getAsString();
				savebatch.setGstnSaveRefId(refId);
				savebatch.setStatus("SAVE_SUCCESS");
				savebatch.setGetResponsePayload(
						GenUtil.convertStringToClob(resp.toString()));
				batch.setStatus(APIConstants.INPROGRESS);
			} else if ("IMS0004".equals(resp.getError().getErrorCode()) 
			        || "IMS0005".equals(resp.getError().getErrorCode())) {

			    String errorCode = resp.getError().getErrorCode();
			    String errorDesc;
			    
			    if ("IMS0004".equals(errorCode)) {
			        errorDesc = String.format(
			                "There is no record available for GSTR 2B of %s return period.",
			                dto.getReturnPeriod());
			    } else {
			        errorDesc = "2B cannot be generated as there are no changes";
			    }

			    savebatch.setStatus("Success_No New Records");
			    savebatch.setErrorCode(errorCode);
			    savebatch.setErrorDesc(errorDesc);
			    batch.setStatus("Success_No New Records");
			    batch.setErrorCode(errorCode);
			    batch.setErrorDesc(errorDesc);
			} else {

				String errorCode = resp.getError().getErrorCode();
				String errorDesc = resp.getError().getErrorDesc();
				savebatch.setStatus("SAVE_FAILED");
				savebatch.setErrorCode(errorCode);
				savebatch.setErrorDesc(errorDesc);
				batch.setStatus(APIConstants.FAILED);
				batch.setErrorCode(errorCode);
				batch.setErrorDesc(errorDesc);
			}

			savebatch.setModifiedOn(now);
			savebatch.setModifiedBy("SYSTEM");
			savebatch.setGstnRespDate(now);

			batchRepo.save(batch);
			gstr2bBatchRepo.save(savebatch);

		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			batch.setStatus(APIConstants.FAILED);
			batch.setErrorDesc(e.getLocalizedMessage());
			savebatch.setStatus("SAVE_FAILED");
			batchRepo.save(batch);
			gstr2bBatchRepo.save(savebatch);
			throw new AppException(e.getMessage(), e);
		}

	}
}
