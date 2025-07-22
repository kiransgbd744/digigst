package com.ey.advisory.app.ims.handlers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.InwardEInvoiceERPRequestRepository;
import com.ey.advisory.app.inward.einvoice.InwardEInvoiceERPRequestEntity;
import com.ey.advisory.app.inward.einvoice.InwardGetIrnListDataParser;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi.jain
 *
 */
@Slf4j
@Component("GetIrnSingleResponseProcessor")
public class GetIrnSingleResponseProcessor
        implements GetIrnJsonResponseProcessor {

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	@Autowired
	@Qualifier("InwardGetIrnListDataParserImpl")
	private InwardGetIrnListDataParser irnListParser;

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docRepository;

	@Autowired
	private InwardEInvoiceERPRequestRepository erpRepo;

	@Autowired
	@Qualifier("DefaultGetIrnResponseProcessor")
	private GetIrnJsonProcessor defaultFactoryImpl;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Autowired
	@Qualifier("InwardGetIrnJsonDataParserImpl")
	InwardGetIrnListDataParser inwardGetIrnJsonDataParser;

	@Override
	public void processJsonResponse(String gstin, String taxPeriod,
	        Long batchId, List<Long> resultIds, boolean isAuto, String section,
	        Gstr1GetInvoicesReqDto dto) {

		try {
			if(dto.getGroupcode()!=null)
			{
		
			TenantContext.setTenantId(dto.getGroupcode());
			}
			List<String> increIrnList = new ArrayList<>();
			Gson gson = GsonUtil.newSAPGsonInstance();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
				        "GET Call is Success for GSTIN: {}, TaxPeriod: {}, BatchId: {}, Section: {}, increIrnList size: {}.",
				        gstin, taxPeriod, batchId, section,
				        increIrnList.size());

				LOGGER.debug(
				        "ResultIds size: {}, List: {}, for GSTIN: {}, TaxPeriod: {}, BatchId: {}, Section: {}",
				        resultIds.size(), resultIds, gstin, taxPeriod, batchId,
				        section);
			}
			increIrnList = inwardGetIrnJsonDataParser
			        .parseIrnListData(resultIds, dto, batchId);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
				        "GET Call is Success for the batchId {} and increIrnList size is {}",
				        batchId, increIrnList.size());
			}

			List<AsyncExecJob> jobList = new ArrayList<>();

			if (increIrnList != null && !increIrnList.isEmpty()) {
				List<List<String>> increIrnResultId = Lists
				        .partition(increIrnList, 5);

				for (List<String> chunkIrnIds : increIrnResultId) {
					List<Gstr1GetInvoicesReqDto> dtoList = new ArrayList<>();

					for (String irn : chunkIrnIds) {
						Gstr1GetInvoicesReqDto dto1 = new Gstr1GetInvoicesReqDto();

						String[] irnLi = irn.split("-");

						dto1.setIrn(irnLi[0]);
						dto1.setIrnSts(irnLi[1]);

						dto1.setReturnPeriod(dto.getReturnPeriod());
						dto1.setGstin(dto.getGstin());
						dto1.setBatchId(batchId);
						dto1.setType(section);
						LOGGER.debug("Created DTO: {}", dto1);
						dtoList.add(dto1);
					}

					//String jsonParam = gson.toJson(dtoList); // Convert entire
					                                         // list
					                                         // to JSON
					JsonObject jsonParams = new JsonObject();
					jsonParams.addProperty("batchId", batchId);
					jsonParams.add("dtoList", gson.toJsonTree(dtoList)); // Add
					String jsonParam = jsonParams.toString(); 
					
					jobList.add(asyncJobsService.createJobAndReturn(
					        dto.getGroupcode(), JobConstants.GET_IRN_DTL_JOB,
					        jsonParam, "SYSTEM", 50L,
					        null, null));
				}

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
					        "Posting entry in erp table for batch id {}  "
					                + " and section {}",
					        batchId, dto.getType());
				}

				if (!jobList.isEmpty())
					asyncJobsService.createJobs(jobList);

				InwardEInvoiceERPRequestEntity erpEntity = new InwardEInvoiceERPRequestEntity();

				erpEntity.setBatchId(batchId);
				erpEntity.setSupplyType(section);
				erpEntity.setGstin(dto.getGstin());
				erpEntity.setTaxPeriod(dto.getReturnPeriod());
				erpEntity.setCreatedOn(LocalDateTime.now());
				erpEntity.setStatus("INITIATED");

				erpRepo.save(erpEntity);

			}
			
			batchUtil.updateById(batchId, APIConstants.SUCCESS, null, null,
			        false);
			
		} catch (Exception ex) {
			LOGGER.error("Error while parsing irn list {} ", ex);
			batchUtil.updateById(batchId, APIConstants.FAILED, null,
			        "Error while parsing irn list", false);
			throw new AppException();
		}
	}
}
