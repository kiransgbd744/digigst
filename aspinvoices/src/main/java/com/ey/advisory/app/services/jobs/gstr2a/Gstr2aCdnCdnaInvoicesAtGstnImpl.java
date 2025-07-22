/*package com.ey.advisory.app.services.jobs.gstr2a;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.gstr2.GetAnx1BatchRepository;
import com.ey.advisory.app.docs.dto.anx1.Gstr1GetInvoicesReqDto;
import com.ey.advisory.app.services.jobs.anx2.Anx2GetBatchUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Component("gstr2aCdnCdnaInvoicesAtGstnImpl")
@Slf4j
public class Gstr2aCdnCdnaInvoicesAtGstnImpl
		implements Gstr2aInvoicesAtGstn {

	@Autowired
	@Qualifier("Gstr2aDataAtGstnImpl")
	private Gstr2aDataAtGstn gstr2aDataAtGstn;

	@Autowired
	@Qualifier("gstr2aCdnCdnaDataParserImpl")
	private Gstr2aCdnCdnaDataParser gstr2aCdnCdnaDataParser;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;
	
	@Autowired
	@Qualifier("Anx2GetBatchUtil")
	private Anx2GetBatchUtil batchUtil;

	
	@Autowired
	private GetGstr2aCdnCdnaInvoicesRepository headerRepo;

	@Override
	public Long findInvFromGstn(Gstr1GetInvoicesReqDto dto,
			String groupCode, String type,Long batchId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("I am from findCDNFromGstn Method type: {}", type);
		}
		Long reqId = null;
		Gson gson = GsonUtil.newSAPGsonInstance();
	//	GetAnx1BatchEntity batch;
		try {
			JsonObject requestObject = (new JsonParser().parse(jsonReq))
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
			
			if (APIConstants.CDN.equals(type)) {
				apiResp = "{'cdn':[{'ctin':'01AAAAP1208Q1ZS','cfs':'Y','nt':[{'chksum':'AflJufPlFStqKBZ','ntty':'C','nt_num':'533515','nt_dt':'23-09-2016','p_gst':'N','inum':'915914','idt':'23-09-2016','val':729248.16,'diff_percent':0.65,'itms':[{'num':1,'itm_det':{'rt':10.1,'txval':6210.99,'iamt':0,'camt':614.44,'samt':5.68,'csamt':621.09}}]}]}]}";

			} else {
				apiResp = "{'cdna':[{'ctin':'01AAAAP1208Q1ZS','cfs':'Y','nt':[{'chksum':'AflJufPlFStqKBZ','ntty':'C','nt_num':'533515','nt_dt':'23-09-2016','ont_num':'533515','ont_dt':'23-09-2016','p_gst':'N','inum':'915914','idt':'23-09-2016','val':729248.16,'diff_percent':0.65,'itms':[{'num':1,'itm_det':{'rt':10.1,'txval':6210.99,'iamt':0,'camt':614.44,'samt':5.68,'csamt':621.09}}]}]}]}";
			}
			if (apiResp != null) {
			
				Set<GetGstr2aCdnCdnaInvoicesHeaderEntity> invoices = gstr2aCdnCdnaDataParser
						.parseCdnCdnaData(dto, apiResp, type, batch.getId());

				// Update Batch as success
				batch.setStatus(APIConstants.SUCCESS);
				if (!invoices.isEmpty()) {
					//batch.setInvCount(invoices.size());
					// InActiveting Previous Header Records
					headerRepo.softlyDeleteCdnCdnaHeader(dto.getGstin(),
							dto.getReturnPeriod());
					// Save new Header
					headerRepo.saveAll(invoices);
				
					GetGstr2aBatchEntity batch = new GetGstr2aBatchEntity();
					batch.setcGstin(dto.getGstin());
					batch.setTaxPeriod(dto.getReturnPeriod());
					batch.setType(type);
					batch.setCreatedBy("SYSTEM");
					batch.setCreatedOn(LocalDateTime.now());
					batch.setDelete(false);
					batch.setCdnInvoices((invoices));
					invoices.forEach(invoice -> {
						invoice.setCdnBatchIdGstr2a(batch);
					});

					TenantContext.setTenantId(groupCode);
					batchRepo.save(batch);
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