package com.ey.advisory.processors.test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.asprecon.InwardEInvoiceERPRequestRepository;
import com.ey.advisory.app.inward.einvoice.InwardEInvoiceERPRequestEntity;
import com.ey.advisory.app.inward.einvoice.InwardGetIrnListDataParser;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi.jain
 *
 */
@Service("GetIrnB2BTokenListProcessor")
@Slf4j
public class GetIrnB2BTokenListProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("InwardGetIrnListDataParserImpl")
	private InwardGetIrnListDataParser irnListParser;
	
	@Autowired
	private InwardEInvoiceERPRequestRepository erpRepo;
	
	@Autowired
	private AsyncJobsService asyncJobsService;
	
	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;
	
	@Override
	public void execute(Message message, AppExecContext context) {
		
		List<Long> resultIds = new ArrayList<>();
		Long batchId = null;
		try {
			String jsonParam = null;
			if (LOGGER.isDebugEnabled()) {
				String logMsg = String.format(
						"Executing "
								+ " Gstr2AB2BTokenCaseProcessor for Group %s",
						TenantContext.getTenantId());
				LOGGER.debug(logMsg);
			}
			
			String jsonString = message.getParamsJson();
			JsonObject json = JsonParser.parseString(jsonString)
					.getAsJsonObject();
			for (JsonElement element : json.get("resultIds").getAsJsonArray()) {
				Long resultId = element.getAsLong();
				resultIds.add(resultId);
			}
			String ctxParams = json.get("ctxParams").getAsString();
			Long transactionId = json.get("transactionId").getAsLong();
			
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject ctxParamsObj = JsonParser.parseString(ctxParams)
					.getAsJsonObject();
			Gstr1GetInvoicesReqDto dto = gson.fromJson(ctxParamsObj,
					Gstr1GetInvoicesReqDto.class);
			batchId = dto.getBatchId();

			String apiSection = dto.getApiSection();
			
			List<String> increIrnList = new ArrayList<>();
			if (APIConstants.GET_IRN_LIST.equalsIgnoreCase(apiSection)) {
				increIrnList = irnListParser.parseIrnListData(resultIds, dto,
						batchId);
			}	
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"GET Call is Success for the batchId {} inside "
								+ "increIrnList",
								increIrnList);
			}
			List<AsyncExecJob> jobList = new ArrayList<>();

			for (String irn : increIrnList) {
				Gstr1GetInvoicesReqDto dto1 = new Gstr1GetInvoicesReqDto();
				String irnLi[] = irn.split("-");
				dto1.setIrn(irnLi[0]!=null?irnLi[0]:null);
				dto1.setReturnPeriod(dto.getReturnPeriod());
				dto1.setGstin(dto.getGstin());
				dto1.setBatchId(batchId);
				dto1.setIrnSts(irnLi[1]!=null?irnLi[1].toString():null);
				dto1.setType(dto.getType());
				jsonParam = gson.toJson(dto1);
				jobList.add(
						asyncJobsService.createJobAndReturn(dto.getGroupcode(),
								JobConstants.GET_IRN_DTL_JOB, jsonParam,
								"SYSTEM", JobConstants.PRIORITY,
								JobConstants.PARENT_JOB_ID, 1L));
			}
			
			
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Posting entry in erp table for batch id {}  "
								+ " and section {}",
								batchId,dto.getType());
			}

			if (!jobList.isEmpty())
				asyncJobsService.createJobs(jobList);
			
			batchUtil.updateById(batchId,APIConstants.SUCCESS, null,null, false);
			
			InwardEInvoiceERPRequestEntity erpEntity = new InwardEInvoiceERPRequestEntity();
			
			erpEntity.setBatchId(batchId);
			erpEntity.setSupplyType(dto.getType());
			erpEntity.setGstin(dto.getGstin());
			erpEntity.setTaxPeriod(dto.getReturnPeriod());
			erpEntity.setStatus("INITIATED");
			erpEntity.setCreatedOn(LocalDateTime.now());
			
			erpRepo.save(erpEntity);
			
		} catch (Exception ex) {
			LOGGER.error("Error while parsing irn list {}",ex);
			batchUtil.updateById(batchId,
					APIConstants.FAILED, null,"Error while parsing irn list", false);
			
			throw new AppException();
		}
	}
}
