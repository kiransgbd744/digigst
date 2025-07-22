/**
 * 
 */
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
 * @author Hemasundar.J
 *
 */
@Service("Anx2ItcSumryInvoicesAtGstnImpl")
@Slf4j
public class Anx2ItcSumryInvoicesAtGstnImpl implements Anx2InvoicesAtGstn {

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
			Type listType = new TypeToken<List<Anx2GetInvoicesReqDto>>() {
			}.getType();
			List<Anx2GetInvoicesReqDto> dtos = gson.fromJson(asJsonArray,
					listType);
			dtos.forEach(dto -> {
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
				//String apiResp = "{'itcsum':[{'action':'A','val':56000,'igst':80,'sgst':80,'cgst':80,'cess':100},{'action':'R','val':56000,'igst':80,'sgst':80,'cgst':80,'cess':100},{'action':'P','val':56000,'igst':80,'sgst':80,'cgst':80,'cess':100},{'action':'C','val':56000,'igst':80,'sgst':80,'cgst':80,'cess':100}]}";
				LOGGER.debug("Gstn respose for {} is {}", dto, apiResp);
				/*if (apiResp != null) {
					if(token.isTokenResponse(apiResp)) {
						//To handle the token response.
						
						
						// Update Batch as Token Received.
						batch.setStatus(APIConstants.TOKEN_RECEIVED);
					} else {
					Set<GetAnx2ItcSummaryInvoicesEntity> invoices = anx2DataParser
							.parseItcSummryData(dto, apiResp, batch.getId());
					// Update Batch as success
					batch.setStatus(APIConstants.SUCCESS);
					if (!invoices.isEmpty()) {
						batch.setInvCount(invoices.size());
						// InActiveting Previous Header Records
						headerRepo.softlyDeleteItcSumryHeader(dto.getGstin(),
								dto.getReturnPeriod());
						// Save new Header
						headerRepo.saveAll(invoices);
						GetGstr2aBatchEntity batch = batchUtil.makeBatch(dto,
								type);
						batch.setAnx2ItcSumInvoices(invoices);
						invoices.forEach(invoice -> {
							invoice.setItcSumBatchIdAnx2(batch);
						});
						TenantContext.setTenantId(groupCode);
						batchRepo.save(batch);
						LOGGER.debug(
								"Response is to store into the tables sucessfully.");
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
}
