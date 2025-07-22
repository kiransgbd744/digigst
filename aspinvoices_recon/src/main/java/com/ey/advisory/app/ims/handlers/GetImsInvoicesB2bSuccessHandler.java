package com.ey.advisory.app.ims.handlers;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ImsERPRequestRepository;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.gstnapi.SuccessHandler;
import com.ey.advisory.gstnapi.SuccessResult;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Service("GetImsInvoicesB2bSuccessHandler")
@Slf4j
public class GetImsInvoicesB2bSuccessHandler implements SuccessHandler {

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	@Autowired
	@Qualifier("ImsInvoicesDataParserImpl")
	private ImsInvoicesDataParser imsListParser;
	
	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docRepository;

	@Autowired
	private AsyncJobsService asyncJobsService;
	
	@Autowired
	@Qualifier("ImsInvoicesProcCallServiceImpl")
	private ImsInvoicesProcCallService imsProcCallInvoiceParser;


	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;
	
	@Autowired
	@Qualifier("imsERPRequestRepository")
	private ImsERPRequestRepository imsERPRequestRepo;

	@Override
	public void handleSuccess(SuccessResult result, String apiParams) {
		Long batchId = null;
		try {
			List<Long> resultIds = result.getSuccessIds();
			
			//TODO Method for B2B  token
			String ctxParams = result.getCtxParams();
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject ctxParamsObj = JsonParser.parseString(ctxParams)
					.getAsJsonObject();
			Gstr1GetInvoicesReqDto dto = gson.fromJson(ctxParamsObj,
					Gstr1GetInvoicesReqDto.class);
			batchId = dto.getBatchId();

			String apiSection = dto.getApiSection();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"GET Call is Success for the batchId {} inside "
								+ "GetImsInvoicesB2bSuccessHandler.java",
						batchId);
			}
			
			if (resultIds.size() > 1
					&& APIConstants.IMS_TYPE_B2B.equalsIgnoreCase(dto.getType())) {
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
								+ "GetInwardIrnListSectionSuccessHandler.java and before parsing the JSON",
						batchId);
			}
	
			if (APIConstants.IMS_INVOICE.equalsIgnoreCase(apiSection)) {
				imsListParser.parseImsInvoicesData(resultIds, dto,
						batchId,null);
			}	
			

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"calling dup check proc and update for batchid {}  "
								+ " and section {}",
						batchId, dto.getType());
			}

			if (APIConstants.IMS_INVOICE.equalsIgnoreCase(apiSection)) {
				imsProcCallInvoiceParser.procCall(dto, batchId);
			}
			

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						" dup check proc executed for batchid {}  "
								+ " and section {}",
						batchId, dto.getType());
			}


			batchUtil.updateById(batchId, APIConstants.SUCCESS, null, null,
					false);
			
			ImsERPRequestEntity erpEntity = new ImsERPRequestEntity();
			
			erpEntity.setBatchId(batchId);
			erpEntity.setSupplyType(dto.getType());
			erpEntity.setGstin(dto.getGstin());
			erpEntity.setStatus("INITIATED");
			erpEntity.setCreatedOn(LocalDateTime.now());
			
			imsERPRequestRepo.save(erpEntity);
		} catch (Exception ex) {
			LOGGER.error("Error while parsing irn list {} ",ex);
			batchUtil.updateById(batchId,
					APIConstants.FAILED, null,"Error while parsing irn list", false);
			
			throw new AppException();
		}
	}

}