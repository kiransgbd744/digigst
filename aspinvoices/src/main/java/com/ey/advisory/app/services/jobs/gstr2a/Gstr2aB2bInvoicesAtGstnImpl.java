/*package com.ey.advisory.app.services.jobs.gstr2a;

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
 * 
 * @author Hemasundar.J
 *
 *//*
@Service("gstr2aB2bInvoicesAtGstnImpl")
@Slf4j
public class Gstr2aB2bInvoicesAtGstnImpl implements Gstr2aInvoicesAtGstn {

	@Autowired
	@Qualifier("Gstr2aDataAtGstnImpl")
	private Gstr2aDataAtGstn gstr2aDataAtGstn;

	@Autowired
	@Qualifier("gstr2aB2bB2baDataParserImpl")
	private Gstr2aB2bB2baDataParser gstr2aDataParser;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;
	
	@Autowired
	@Qualifier("Anx2GetBatchUtil")
	private Anx2GetBatchUtil batchUtil;
	
	@Autowired
	private GetGstr2B2bInvoicesRepository headerRepo;

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
			  if (APIConstants.B2B.equals(type)) {
				apiResp = "{'b2b':[{'inv':[{'itms':[{'num':1,'itm_det':{'csamt':0,'samt':0,'rt':5,'txval':11000,'camt':0,'iamt':550}}],'val':11550,'oinum':'UJIWP04','inv_typ':'R','pos':'33','idt':'17-10-2017','rchrg':'N','inum':'OIJAI01','oidt':'17-10-2017','chksum':'9a1317fd4982eae1e26f0d0d18f6d53c05b8f3edf7902d5c97820fe5718bf08b'},{'itms':[{'num':1,'itm_det':{'csamt':0,'samt':0,'rt':5,'txval':120010,'camt':0,'iamt':6000.5}}],'val':126010.5,'oinum':'UJIWP06','inv_typ':'R','pos':'33','idt':'17-10-2017','rchrg':'Y','inum':'UJIWP06','oidt':'17-10-2017','chksum':'0d188e77815ead545b73e6a6e2c663629edfed7b7d6f477d6f8bf4e2694e3fff'},{'itms':[{'num':1,'itm_det':{'csamt':0,'samt':0,'rt':5,'txval':15000,'camt':0,'iamt':750}}],'val':15750,'oinum':'UJIWP09','inv_typ':'R','pos':'33','idt':'17-10-2017','rchrg':'Y','inum':'UJIWP09','oidt':'17-10-2017','chksum':'d589e10d8848c85c7acc06d15682a1cc2b7b518eb2eac5655a3d276817354604'},{'itms':[{'num':1,'itm_det':{'csamt':0,'samt':0,'rt':5,'txval':80010,'camt':0,'iamt':4000.5}}],'val':84010.5,'oinum':'UJIWP33','inv_typ':'DE','pos':'33','idt':'17-10-2017','rchrg':'N','inum':'UJIWP33','oidt':'17-10-2017','chksum':'6d682863bcae3e4b46c88b48d5c2cee44ba2bf6d0966b9b2b9fd1024118f19df'}],'cfs':'N','ctin':'27GSPMH0482G1ZM'}]}";
			} else {
				apiResp = "{'b2ba':[{'ctin':'01AABCE2207R1Z5','cfs':'Y','inv':[{'chksum':'AflJufPlFStqKBZ','inum':'S008400','idt':'24-11-2016','oinum':'S008400','oidt':'24-11-2016','val':729248.16,'pos':'06','rchrg':'N','inv_typ':'R','diff_percent':0.65,'itms':[{'num':1,'itm_det':{'rt':1,'txval':6210.99,'iamt':0,'camt':614.44,'samt':5.68,'csamt':621.09}},{'num':2,'itm_det':{'rt':2,'txval':1000.05,'iamt':0,'camt':887.44,'samt':5.68,'csamt':50.12}}]}]}]}";
			}
			if (apiResp != null) {
			
				Set<GetGstr2aB2bInvoicesHeaderEntity> invoices = gstr2aDataParser
						.parseB2bData(dto, apiResp, type, batch.getId());
				// Update Batch as success
				batch.setStatus(APIConstants.SUCCESS);
				if (!invoices.isEmpty()) {
					//batch.setInvCount(invoices.size());
					// InActiveting Previous Header Records
					headerRepo.softlyDeleteB2bHeader(dto.getGstin(),
							dto.getReturnPeriod());
					// Save new Header
					headerRepo.saveAll(invoices);
					
				 GetGstr2aBatchEntity batch = new GetGstr2aBatchEntity();
					batch.setApiSection(APIConstants.GSTR2A);
					batch.setcGstin(dto.getGstin());
					batch.setTaxPeriod(dto.getReturnPeriod());
					batch.setType(type);
					batch.setCreatedBy(APIConstants.SYSTEM);
					batch.setCreatedOn(LocalDateTime.now());
					batch.setDelete(false);
					batch.setProcessingStatus(APIConstants.PROCESSING_STATUS);
					batch.setGstr2aB2bInvoices(invoices);
					invoices.forEach(invoice -> {
						invoice.setB2bBatchIdGstr2a(batch);
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