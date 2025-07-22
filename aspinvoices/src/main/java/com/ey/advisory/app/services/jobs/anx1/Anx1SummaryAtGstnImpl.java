/**
 * 
 */
package com.ey.advisory.app.services.jobs.anx1;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.entities.client.GetAnx1SummaryEntity;
import com.ey.advisory.app.data.repositories.client.Anx1SummaryAtGstnRepository;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.services.jobs.gstr1.GetBatchPayloadHandler;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.dto.Anx1GetInvoicesReqDto;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Laxmi.Salukuti
 *
 */
@Component("Anx1SummaryAtGstnImpl")
@Slf4j
public class Anx1SummaryAtGstnImpl implements Anx1SummaryAtGstn {

	@Autowired
	@Qualifier("Anx1SummaryAtGstnRepository")
	private Anx1SummaryAtGstnRepository repository;

	@Autowired
	@Qualifier("Anx1SummaryDataAtGstnImpl")
	private Anx1SummaryDataAtGstn anx1SummaryDataAtGstn;

	@Autowired
	@Qualifier("Anx1SummaryDataParserImpl")
	private Anx1SummaryDataParserImpl anx1SummaryDataParser;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;
	
	@Autowired
	private GetBatchPayloadHandler batchPayloadHelper;


	@Override
	public String getAnx1Summary(Anx1GetInvoicesReqDto dto,
			String groupCode) {

		String apiResp = null;
		GetAnx1BatchEntity batch = null;
		try {
			String type = APIIdentifiers.ANX1_GETSUM;
			batch = batchUtil.makeBatch(dto, type);
			TenantContext.setTenantId(groupCode);
			// InActiveting Previous Batch Records
			batchRepo.softlyDelete(type.toUpperCase(),
					APIConstants.ANX1.toUpperCase(), dto.getGstin(),
					dto.getReturnPeriod());
			// Save new Batch
			batch = batchRepo.save(batch);
			// If Generate Summary has value(Reference ID) only then we
			// are calling GetSummary api.
			if (generateAnx1Summary(dto, groupCode)) {
				apiResp = anx1SummaryDataAtGstn.getAnx1Summary(dto, groupCode);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("ANX1 Get Summary API response is {} ",
							apiResp);
				}
				// static data
				// apiResp =
				// "{'gstin':'19AHLPP7361BMZJ','rtnprd':'092019','summtyp':'H','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279','secsum':[{'cptysum':[{'ctin':'19AHLPP7361BOZH','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279','ttldoc':1,'ttlval':9000,'ttligst':675,'ttlcgst':0,'ttlsgst':0,'ttlcess':9000,'nettax':9000},{'ctin':'19AHLPP7361BNZI','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279','ttldoc':3,'ttlval':0,'ttligst':3510,'ttlcgst':0,'ttlsgst':0,'ttlcess':0,'nettax':30000}],'actionsum':[{'action':'A','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279','ttldoc':1,'ttlval':9000,'ttligst':675,'ttlcgst':0,'ttlsgst':0,'ttlcess':9000,'nettax':9000},{'action':'C','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279','ttldoc':3,'ttlval':0,'ttligst':3510,'ttlcgst':0,'ttlsgst':0,'ttlcess':0,'nettax':30000}],'doctypsum':[{'action':'C','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279','ttldoc':1,'ttlval':0,'ttligst':0,'ttlcgst':0,'ttlsgst':0,'ttlcess':0,'nettax':30000},{'action':'D','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279','ttldoc':1,'ttlval':0,'ttligst':0,'ttlcgst':0,'ttlsgst':0,'ttlcess':0,'nettax':30000},{'action':'I','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279','ttldoc':2,'ttlval':9000,'ttligst':4185,'ttlcgst':0,'ttlsgst':0,'ttlcess':9000,'nettax':39000}],'secnm':'B2B','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279','ttldoc':4,'ttlval':9000,'ttligst':4185,'ttlcgst':0,'ttlsgst':0,'ttlcess':9000,'nettax':39000},{'secnm':'B2C','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279','ttldoc':4,'ttlval':0,'ttligst':130675,'ttlcgst':0,'ttlsgst':0,'ttlcess':104000,'nettax':99100},{'cptysum':[{'ctin':'19AHLPP7361BNZI','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279','ttldoc':4,'ttlval':9000,'ttligst':255,'ttlcgst':97.5,'ttlsgst':97.5,'ttlcess':1000,'nettax':19000}],'actionsum':[{'action':'A','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279','ttldoc':4,'ttlval':9000,'ttligst':255,'ttlcgst':97.5,'ttlsgst':97.5,'ttlcess':1000,'nettax':19000}],'doctypsum':[{'action':'C','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279','ttldoc':1,'ttlval':0,'ttligst':195,'ttlcgst':0,'ttlsgst':0,'ttlcess':0,'nettax':10000},{'action':'D','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279','ttldoc':2,'ttlval':9000,'ttligst':450,'ttlcgst':0,'ttlsgst':0,'ttlcess':1000,'nettax':19000},{'action':'I','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279','ttldoc':1,'ttlval':0,'ttligst':0,'ttlcgst':97.5,'ttlsgst':97.5,'ttlcess':0,'nettax':10000}],'secnm':'DE','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279','ttldoc':4,'ttlval':9000,'ttligst':255,'ttlcgst':97.5,'ttlsgst':97.5,'ttlcess':1000,'nettax':19000},{'cptysum':[],'secnm':'ECOM','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279','ttldoc':1,'ttlval':0,'ttligst':12,'ttlcgst':0,'ttlsgst':0,'ttlcess':0,'nettax':0},{'doctypsum':[{'action':'C','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279','ttldoc':1,'ttlval':0,'ttligst':0,'ttlcgst':0,'ttlsgst':0,'ttlcess':0,'nettax':65000},{'action':'D','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279','ttldoc':1,'ttlval':0,'ttligst':0,'ttlcgst':0,'ttlsgst':0,'ttlcess':0,'nettax':65000},{'action':'I','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279','ttldoc':1,'ttlval':0,'ttligst':0,'ttlcgst':0,'ttlsgst':0,'ttlcess':0,'nettax':65000}],'secnm':'EXPWOP','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279','ttldoc':3,'ttlval':0,'ttligst':0,'ttlcgst':0,'ttlsgst':0,'ttlcess':0,'nettax':65000},{'doctypsum':[{'action':'C','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279','ttldoc':1,'ttlval':0,'ttligst':1950,'ttlcgst':0,'ttlsgst':0,'ttlcess':0,'nettax':65000},{'action':'D','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279','ttldoc':1,'ttlval':0,'ttligst':1950,'ttlcgst':0,'ttlsgst':0,'ttlcess':0,'nettax':65000},{'action':'I','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279','ttldoc':2,'ttlval':9000,'ttligst':2475,'ttlcgst':0,'ttlsgst':0,'ttlcess':90000,'nettax':72000}],'secnm':'EXPWP','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279','ttldoc':4,'ttlval':9000,'ttligst':2475,'ttlcgst':0,'ttlsgst':0,'ttlcess':90000,'nettax':72000},{'secnm':'IMPG','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279','ttldoc':3,'ttlval':0,'ttligst':1620,'ttlcgst':0,'ttlsgst':0,'ttlcess':0,'nettax':162000},{'cptysum':[{'ctin':'33AACCA0121EAZG','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279','ttldoc':3,'ttlval':1000,'ttligst':360,'ttlcgst':0,'ttlsgst':0,'ttlcess':0,'nettax':36000}],'secnm':'IMPGSEZ','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279','ttldoc':3,'ttlval':1000,'ttligst':360,'ttlcgst':0,'ttlsgst':0,'ttlcess':0,'nettax':36000},{'secnm':'IMPS','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279','ttldoc':2,'ttlval':0,'ttligst':1012,'ttlcgst':0,'ttlsgst':0,'ttlcess':1000,'nettax':65121},{'secnm':'MIS','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279','ttldoc':0,'ttlval':0,'ttligst':0,'ttlcgst':0,'ttlsgst':0,'ttlcess':0,'nettax':0},{'cptysum':[],'secnm':'REV','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279','ttldoc':2,'ttlval':0,'ttligst':1068.25,'ttlcgst':0,'ttlsgst':0,'ttlcess':0,'nettax':651200},{'cptysum':[{'ctin':'33AACCA0121EAZG','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279','ttldoc':17,'ttlval':7000,'ttligst':0,'ttlcgst':0,'ttlsgst':0,'ttlcess':0,'nettax':7000}],'actionsum':[{'action':'C','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279','ttldoc':17,'ttlval':7000,'ttligst':0,'ttlcgst':0,'ttlsgst':0,'ttlcess':0,'nettax':7000}],'doctypsum':[{'action':'C','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279','ttldoc':5,'ttlval':5000,'ttligst':0,'ttlcgst':0,'ttlsgst':0,'ttlcess':0,'nettax':5000},{'action':'D','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279','ttldoc':6,'ttlval':6000,'ttligst':0,'ttlcgst':0,'ttlsgst':0,'ttlcess':0,'nettax':6000},{'action':'I','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279','ttldoc':6,'ttlval':6000,'ttligst':0,'ttlcgst':0,'ttlsgst':0,'ttlcess':0,'nettax':6000}],'secnm':'SEZWOP','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279','ttldoc':17,'ttlval':7000,'ttligst':0,'ttlcgst':0,'ttlsgst':0,'ttlcess':0,'nettax':7000},{'cptysum':[{'ctin':'33AACCA0121EAZG','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279','ttldoc':17,'ttlval':7000,'ttligst':136.5,'ttlcgst':0,'ttlsgst':0,'ttlcess':0,'nettax':7000}],'actionsum':[{'action':'A','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279','ttldoc':17,'ttlval':7000,'ttligst':136.5,'ttlcgst':0,'ttlsgst':0,'ttlcess':0,'nettax':7000}],'doctypsum':[{'action':'C','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279','ttldoc':5,'ttlval':5000,'ttligst':97.5,'ttlcgst':0,'ttlsgst':0,'ttlcess':0,'nettax':5000},{'action':'D','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279','ttldoc':6,'ttlval':6000,'ttligst':117,'ttlcgst':0,'ttlsgst':0,'ttlcess':0,'nettax':6000},{'action':'I','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279','ttldoc':6,'ttlval':6000,'ttligst':117,'ttlcgst':0,'ttlsgst':0,'ttlcess':0,'nettax':6000}],'secnm':'SEZWP','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279','ttldoc':17,'ttlval':7000,'ttligst':136.5,'ttlcgst':0,'ttlsgst':0,'ttlcess':0,'nettax':7000}]}";

				if (apiResp != null) {
					saveJsonAsRecords(apiResp, groupCode, dto, batch);
				}

			} else {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"No Reference Id got generated for ANX1 Generate Summary API.");
				}
			}
		} catch (Exception ex) {

			batch.setEndTime(LocalDateTime.now());
			batch.setStatus(APIConstants.FAILED);
			batchRepo.save(batch);
			String msg = "App Exeption";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
		return apiResp;
	}
	
	@Override
	public Boolean generateAnx1Summary(Anx1GetInvoicesReqDto dto,
			String groupCode) {
		
		Gson gson = GsonUtil.newSAPGsonInstance();
		String data = gson.toJson(dto);
		String apiResp = anx1SummaryDataAtGstn.generateAnx1Summary(dto,
				groupCode, data);
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("ANX1 Generate Summary API response is {} ", apiResp);
		}
		if (apiResp != null) {
			return true;
		}
		return false;

	}
	
	public void saveJsonAsRecords(String apiResp, String groupCode,
			Anx1GetInvoicesReqDto dto, GetAnx1BatchEntity batch) {
		
		if (apiResp != null) {
			batchPayloadHelper.dumpGetResponsePayload(groupCode, dto.getGstin(),
					dto.getReturnPeriod(), batch.getId(), apiResp,
					APIConstants.SYSTEM);
			GetAnx1SummaryEntity invoces = anx1SummaryDataParser
					.parseAnx1SummaryData(dto, apiResp, batch.getId());

			// InActiveting Previous Header Records
			repository.softlyDeleteAnx1Data(dto.getGstin(),
					dto.getReturnPeriod());
			// Save new Header
			repository.save(invoces);

			// Update Batch as success
			batch.setStatus(APIConstants.SUCCESS);

			// Update Batch
			batch.setEndTime(LocalDateTime.now());
			batch = batchRepo.save(batch);
		} else {
			// Update Batch as success
			batch.setStatus(APIConstants.SUCCESS_WITH_NO_DATA);
			// Update Batch
			batch.setEndTime(LocalDateTime.now());
			batch = batchRepo.save(batch);
		}
		
	}
}
