package com.ey.advisory.app.services.jobs.anx2;

import java.lang.reflect.Type;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.docs.dto.anx2.Anx2GetInvoicesReqDto;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Service("anx2SezwpInvoicesAtGstnImpl")
@Slf4j
public class Anx2SezwpInvoicesAtGstnImpl implements Anx2InvoicesAtGstn {

	@Autowired
	@Qualifier("anx2DataAtGstnImpl")
	private Anx2DataAtGstn anx2DataAtGstn;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	@Autowired
	@Qualifier("Anx2GetBatchUtil")
	private Anx2GetBatchUtil batchUtil;
	
	private GetAnx1BatchEntity batch;

	@Override
	public ResponseEntity<String> findInvFromGstn(String jsonReq,
			String groupCode, String type) {
		LOGGER.debug(
				"findInvFromGstn with req and {}, groupcode {} and type {}",
				jsonReq, groupCode, type);
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			/*JsonArray asJsonArray = (new JsonParser()).parse(jsonReq)
					.getAsJsonArray();*/
			JsonObject requestObject = (new JsonParser()).parse(jsonReq)
					.getAsJsonObject();
			JsonArray asJsonArray = requestObject.get("req").getAsJsonArray();
			Type listType = new TypeToken<List<Anx2GetInvoicesReqDto>>() 
			{}.getType();
			List<Anx2GetInvoicesReqDto> dtos = gson.fromJson(asJsonArray,
					listType);
			dtos.forEach(dto ->{
				batch = batchUtil.makeBatch(dto, type);
				TenantContext.setTenantId(groupCode);
				//InActiveting Previous Batch Records 
				batchRepo.softlyDelete(type.toUpperCase(), 
						APIConstants.ANX2.toUpperCase(),dto.getGstin(), 
						dto.getReturnPeriod());
				//Save new Batch
				batch = batchRepo.save(batch);
				if(dto.getGroupcode() == null || dto.getGroupcode().trim().length() == 0) {
					dto.setGroupcode(groupCode);
					}
					dto.setBatchId(batch.getId());
					dto.setType(type);
					String json = gson.toJson(dto);
				String apiResp = anx2DataAtGstn.findAnx2DataAtGstn(dto, groupCode,
			 type, json);
			// static data
			//String apiResp = "{'sezwp':[{'ctin':'32SSSKL8363S1ZF','docs':[{'diffprcnt':0.65,'doctyp':'I','rfndelg':'Y','pos':'03','action':'R','cfs':'NF','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279','clmrfnd':'Y','itcent':'Y','upldt':'25-04-2019','doc':{'num':'INV200','dt':'25-04-2019','val':25000},'items':[{'hsn':'223456','txval':1000,'rate':8,'igst':80,'cess':100}]}]}]}";
			LOGGER.debug("Gstn respose for {} is {}", dto, apiResp);
			/*if (apiResp != null) {
				if(token.isTokenResponse(apiResp)) {
					//To handle the token response.
					
					
					// Update Batch as Token Received.
					batch.setStatus(APIConstants.TOKEN_RECEIVED);
				} else {
				Set<GetAnx2SezwpInvoicesHeaderEntity> invoices = anx2DataParser
						.parseSezwpData(dto, apiResp, batch.getId());

				// Update Batch as success
				batch.setStatus(APIConstants.SUCCESS);
				if (!invoices.isEmpty()) {
					batch.setInvCount(invoices.size());
					// InActiveting Previous Header Records
					headerRepo.softlyDeleteSezwpHeader(dto.getGstin(),
							dto.getReturnPeriod());
					// Save new Header
					headerRepo.saveAll(invoices);
					GetGstr2aBatchEntity batch = batchUtil.makeBatch(dto, type);
					batch.setAnx2SezwpInvoices(invoces);
					invoces.forEach(invoice -> {
						invoice.setSezwpBatchIdAnx2(batch);
					});

					TenantContext.setTenantId(groupCode);
					GetGstr2aBatchEntity save = batchRepo.save(batch);
					LOGGER.debug(
							"Response is stored into the tables sucessfully.");
					// String jsonParam = "{'batchId':'" + save.getId() + "'}";
					Anx2Reconciliation2aDto reqDto = new Anx2Reconciliation2aDto();
					reqDto.setBatchId(batch.getId());
					reqDto.setSection(batch.getType());
					String jsonParam = gson.toJson(reqDto,
							Anx2Reconciliation2aDto.class);
					createAnx2ReconJob2a(jsonParam);
				}
			}
				// Update Batch
				batch.setEndTime(LocalDateTime.now());
				batch = batchRepo.save(batch);
			}*/
			});
		} catch (Exception ex) {

			String msg = "App Exeption";
			LOGGER.error(msg, ex);
			JsonObject resp1 = new JsonObject();
			resp1.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

		}
		JsonObject resp1 = new JsonObject();
		JsonElement respBody = gson.toJsonTree(gson.toJson("Success"));
		resp1.add("resp", respBody);
		return new ResponseEntity<>(resp1.toString(), HttpStatus.OK);
	}

	/*public Long createAnx2ReconJob2a(String jsonParam) {
		String groupCode = TenantContext.getTenantId();
		LOGGER.debug("Anx2 Recon2a job Entry is insrted with param {} .",
				jsonParam);
		AsyncExecJob job = asyncJobsService.createJob(groupCode,
				JobConstants.ANX2_RECON_2A, jsonParam, JobConstants.SYSTEM,
				JobConstants.PRIORITY, JobConstants.PARENT_JOB_ID,
				JobConstants.SCHEDULE_AFTER_IN_MINS);
		return job.getJobId();
	}*/
}
