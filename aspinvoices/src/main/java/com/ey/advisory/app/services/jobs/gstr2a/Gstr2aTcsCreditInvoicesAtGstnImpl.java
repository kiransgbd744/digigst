/**
 * 
 *//*
package com.ey.advisory.app.services.jobs.gstr2a;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.gstr2.GetAnx1BatchRepository;
import com.ey.advisory.app.docs.dto.anx1.Gstr1GetInvoicesReqDto;
import com.ey.advisory.app.services.jobs.anx2.Anx2GetBatchUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

*//**
 * @author Hemasundar.J
 *
 *//*
@Slf4j
@Service("Gstr2aTcsCreditInvoicesAtGstnImpl")
public class Gstr2aTcsCreditInvoicesAtGstnImpl implements Gstr2aInvoicesAtGstn {

	@Autowired
	@Qualifier("Gstr2aDataAtGstnImpl")
	private Gstr2aDataAtGstn gstr2aDataAtGstn;

	@Autowired
	@Qualifier("Gstr2aTcsDataParserImpl")
	private Gstr2aTcsDataParser gstr2aDataParser;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;
	
	@Autowired
	@Qualifier("Anx2GetBatchUtil")
	private Anx2GetBatchUtil batchUtil;
	
	@Autowired
	private GetGstr2aTcsInvoicesRepository headerRepo;

	@Override
	public Long findInvFromGstn(Gstr1GetInvoicesReqDto dto,
			String groupCode, String type,Long batchId) {

		Long reqId = null;
		Gson gson = GsonUtil.newSAPGsonInstance();
	//	GetAnx1BatchEntity batch;
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonReq)
					.getAsJsonObject();
			JsonObject reqObject = requestObject.get("req").getAsJsonObject();
			Gstr1GetInvoicesReqDto dto = gson.fromJson(reqObject,
					Gstr1GetInvoicesReqDto.class);
			batch = batchUtil.makeBatchForGstr2B2b(dto, type);
			TenantContext.setTenantId(groupCode);
			//InActiveting Previous Batch Records 
			batchRepo.softlyDelete(type.toUpperCase(), 
					APIConstants.GSTR2A.toUpperCase(),dto.getGstin(), 
					dto.getReturnPeriod());
			//Save new Batch
			batch = batchRepo.save(batch);
			if(dto.getGroupcode() == null || dto.getGroupcode().trim().length() == 0) {
				dto.setGroupcode(groupCode);
				}
				dto.setBatchId(batchId);
				dto.setType(type);
				String json = gson.toJson(dto);
				reqId = gstr2aDataAtGstn.findGstr2aDataAtGstn(dto, groupCode,
			 type,json);
			// static data for testing when otp is inactive
			
			apiResp = "{'tcs_data':[{'chksum':'AflJufPlFStqKBZ','etin':'12DEFPS5555D1Z2','m_id':'2132132131','sup_val':123213.87,'tx_val':6775.77,'irt':9.8,'iamt':2000,'crt':3.5,'camt':3000.5,'srt':3.5,'samt':3200.9,'csrt':0,'csamt':0}]}";
			
			if (apiResp != null) {
			
				Set<GetGstr2aTcsInvoicesEntity> invoices = gstr2aDataParser
						.parseTcsData(dto, apiResp, type, batch.getId());
				// Update Batch as success
				batch.setStatus(APIConstants.SUCCESS);
				if (!invoices.isEmpty()) {
					//batch.setInvCount(invoices.size());
					// InActiveting Previous Header Records
					headerRepo.softlyDeleteByGstnRetPeriod(dto.getGstin(),
							dto.getReturnPeriod());
					// Save new Header
					headerRepo.saveAll(invoices);
					
				
				}
				
				// Update Batch
				batch.setEndTime(LocalDateTime.now());
				batch = batchRepo.save(batch);
			}
		} catch (Exception ex) {

			throw new AppException(ex);

		}
		return reqId;
	}

}
*/