/**
 * 
 */
package com.ey.advisory.app.service.ims.supplier;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.gstnapi.SuccessHandler;
import com.ey.advisory.gstnapi.SuccessResult;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Service("SupplierImsSuccessHandler")
@Slf4j
public class SupplierImsSuccessHandler implements SuccessHandler {
	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	@Autowired
	@Qualifier("SupplierImsInvoicesDataParserImpl")
	private SupplierImsInvoicesDataParserImpl imsInvoiceParser;

	@Autowired
	@Qualifier("SupplierImsProcCallServiceImpl")
	private SupplierImsProcCallServiceImpl imsProcCallInvoiceParser;

	@Autowired
	private AsyncJobsService asyncJobsService;
	
	@Override
	public void handleSuccess(SuccessResult result, String apiParams) {
		Long batchId = null;
		try {
			List<Long> resultIds = result.getSuccessIds();
			String ctxParams = result.getCtxParams();
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject ctxParamsObj = JsonParser.parseString(ctxParams)
					.getAsJsonObject();
			Gstr1GetInvoicesReqDto dto = gson.fromJson(ctxParamsObj,
					Gstr1GetInvoicesReqDto.class);
			batchId = dto.getBatchId();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"GET Call is Success for the batchId {} inside "
								+ "SupplierImsSuccessHandler",
						batchId);
			}
			
			if (resultIds.size() > 1) {
				JsonObject jobParams = new JsonObject();
				JsonArray jsonArray = gson.toJsonTree(resultIds)
						.getAsJsonArray();
				jobParams.add("resultIds", jsonArray);
				jobParams.addProperty("ctxParams", ctxParams);
				jobParams.addProperty("transactionId", result.getTransactionId());
				asyncJobsService.createJob(TenantContext.getTenantId(),
						JobConstants.GET_IMS_B2B_TOKEN_LIST, jobParams.toString(),
						"SYSTEM", 1L, null, null);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"This is a Token Case B2B ims get invoices, hence creating token case async job for Batch Id {} and Group Code {} ",
							batchId, TenantContext.getTenantId());
				}
				return;
			}
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"GET Call is Success for the batchId {} inside "
								+ "SupplierImsSuccessHandler.java and before parsing the JSON",
						batchId);
			}

			imsInvoiceParser.parseImsInvoicesData(resultIds, dto, batchId,
					null);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"calling dup check proc and update for batchid {}  "
								+ " and section {} and returnType {}",
						batchId, dto.getSection(), dto.getReturnType());
			}

			imsProcCallInvoiceParser.procCall(dto, batchId);

			batchUtil.updateById(batchId, APIConstants.SUCCESS, null, null,
					false);

		} catch (Exception ex) {
			LOGGER.error("Error while parsing ims invoices {} ", ex);
			batchUtil.updateById(batchId, APIConstants.FAILED, null,
					"Error while parsing ims invoices", false);
			throw new AppException();
		}
	}

}
