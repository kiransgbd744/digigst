package com.ey.advisory.processors.test;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.asprecon.ControlGstnGetIrnStatusRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.InwardEInvoiceERPRequestRepository;
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
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("GetIrnChunkParserProcessor")
public class GetIrnChunkParserProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("ControlGstnGetIrnStatusRepository")
	private ControlGstnGetIrnStatusRepository controlGstnGetIrnStatusRepo;
	
	@Autowired
	@Qualifier("InwardGetIrnJsonDataParserImpl")
	InwardGetIrnListDataParser inwardGetIrnJsonDataParser;
	
	@Autowired
	private InwardEInvoiceERPRequestRepository erpRepo;

	@Autowired
	private AsyncJobsService asyncJobsService;
	
	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;


	@Override
	public void execute(Message message, AppExecContext context) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		String jsonString = message.getParamsJson();
		Long controlId = null;
		List<Long> resultIds = null;
		Long batchId = null;
		String section = null;
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			JsonArray idArray = requestObject.get("jsonIds").getAsJsonArray();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Received request::{}", requestObject.toString());
			}

			Type listType = new TypeToken<ArrayList<Long>>() {
			}.getType();
			resultIds = gson.fromJson(idArray, listType);
			controlId = requestObject.get("controlId").getAsLong();
			batchId = requestObject.get("batchId").getAsLong();
			JsonObject ctxParamsObj = JsonParser.parseString(requestObject.get("dto").getAsString())
					.getAsJsonObject();
			Gstr1GetInvoicesReqDto dto = gson.fromJson(ctxParamsObj,
					Gstr1GetInvoicesReqDto.class);
			if(dto.getGroupcode()!=null)
			{
			TenantContext.setTenantId(dto.getGroupcode());
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Received dto::{}", dto);
			}

			section = requestObject.get("section").getAsString();
			controlGstnGetIrnStatusRepo.updateJobStatus(APIConstants.INPROGRESS,
					LocalDateTime.now(), controlId);

			//parse logic
			List<String> increIrnList = new ArrayList<>();			
			increIrnList = inwardGetIrnJsonDataParser.parseIrnListData(resultIds, dto,
					batchId);
	
			if (LOGGER.isDebugEnabled()) {
			    LOGGER.debug("GET Call is Success for the batchId {} and increIrnList size is {}", 
			                 batchId, increIrnList.size());
			}			
			List<AsyncExecJob> jobList = new ArrayList<>();
			
			// Partition the list into sublists of 10 elements each
			if (increIrnList != null && !increIrnList.isEmpty()) {
			    List<List<String>> increIrnResultId = Lists.partition(increIrnList, 5);
			    LOGGER.debug("Partitioned increIrnList into {} chunks.", increIrnResultId.size());
			
			//List<List<String>> increIrnResultId = Lists.partition(increIrnList, 10);
			
			for (List<String> chunkIrnIds : increIrnResultId) {
			    List<Gstr1GetInvoicesReqDto> dtoList = new ArrayList<>(); // Collect all 10 DTOs

			    for (String irn : chunkIrnIds) {
			        Gstr1GetInvoicesReqDto dto1 = new Gstr1GetInvoicesReqDto();
			        
			        String[] irnLi = irn.split("-"); 
			        
			        if (irnLi.length > 1) { 
			            dto1.setIrn(irnLi[0]);
			            dto1.setIrnSts(irnLi[1]);
			        } else {
			            dto1.setIrn(irnLi[0]); 
			            dto1.setIrnSts(null);
			        }

			        dto1.setReturnPeriod(dto.getReturnPeriod());
			        dto1.setGstin(dto.getGstin());
			        dto1.setBatchId(batchId);
			        dto1.setType(dto.getSection());
			      //  LOGGER.debug("Created DTO: {}", dto1);
			        dtoList.add(dto1); 
			    }

			  
				JsonObject jsonParams = new JsonObject();
				jsonParams.addProperty("batchId", batchId);
				jsonParams.add("dtoList", gson.toJsonTree(dtoList)); // Add
				String jsonParam = jsonParams.toString(); 
			
			    jobList.add(
			        asyncJobsService.createJobAndReturn(
			            dto.getGroupcode(),
			            JobConstants.GET_IRN_DTL_JOB,
			            jsonParam,
			            "SYSTEM",
			            50L,
			            null,
			            null
			        )
			    );
			}
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Posting entry in erp table for batch id {}  "
								+ " and section {} and controlId {} ",
								batchId,dto.getType(), controlId);
			}
			
			if (!jobList.isEmpty())
				asyncJobsService.createJobs(jobList);
			
			controlGstnGetIrnStatusRepo.updateJobStatus(APIConstants.COMPLETED,
					LocalDateTime.now(), controlId);
			}
		} catch (Exception ee) {
			String msg = ee.getMessage().length() > 200
					? ee.getMessage().substring(0, 198) : ee.getMessage();
					controlGstnGetIrnStatusRepo.updateJobStatusAndErrorDesc(
					APIConstants.FAILED, msg, LocalDateTime.now(), controlId);
			LOGGER.error("Error while parsing jsons ", ee);
			LOGGER.error("Result Ids FAILED: {}", resultIds.toString());
			throw new AppException(ee);
		}
	}
	}
