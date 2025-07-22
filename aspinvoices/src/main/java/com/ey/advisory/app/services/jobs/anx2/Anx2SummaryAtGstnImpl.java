package com.ey.advisory.app.services.jobs.anx2;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.repositories.client.Anx1SummaryAtGstnRepository;
import com.ey.advisory.app.data.repositories.client.Anx2B2BDESummaryAtGstnRepository;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.docs.dto.anx2.Anx2GetInvoicesReqDto;
import com.ey.advisory.app.services.jobs.gstr1.GetBatchPayloadHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIIdentifiers;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Dibyakanta.Sahoo
 *
 */
@Service("Anx2SummaryAtGstnImpl")
@Slf4j
public class Anx2SummaryAtGstnImpl implements Anx2SummaryAtGstn {

	@Autowired
	@Qualifier("Anx1SummaryAtGstnRepository")
	private Anx1SummaryAtGstnRepository repository;

	@Autowired
	@Qualifier("Anx2SummaryDataParserImpl")
	private Anx2SummaryDataParserImpl anx2SummaryDataParser;

	@Autowired
	@Qualifier("Anx2SummaryDataAtGstnImpl")
	private Anx2SummaryDataAtGstn anx2SummaryDataAtGstn;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	@Autowired
	private Anx2GetBatchUtil batchUtil;

	@Autowired
	@Qualifier("Anx2B2BDESummaryAtGstnRepository")
	private Anx2B2BDESummaryAtGstnRepository anx2B2BDESummaryAtGstnRepository;

	@Autowired
	private GetBatchPayloadHandler batchPayloadHelper;
	
	@Override
	public String getAnx2Summary(Anx2GetInvoicesReqDto dto,
			String groupCode) {

		String apiResp = null;
		GetAnx1BatchEntity batch = null;
		try {
			String type = APIIdentifiers.ANX2_GETSUM;
			batch = batchUtil.makeBatch(dto, type);
			TenantContext.setTenantId(groupCode);
			// InActiveting Previous Batch Records
			batchRepo.softlyDelete(type.toUpperCase(),
					APIConstants.ANX2.toUpperCase(), dto.getGstin(),
					dto.getReturnPeriod());
			// Save new Batch
			batch = batchRepo.save(batch);
			// If Generate Summary has value(Reference ID) only then we
			// are calling GetSummary api.

			if (generateAnx2Summary(dto, groupCode)) {
				// if (true) {
				apiResp = anx2SummaryDataAtGstn.getAnx2Summary(dto, groupCode);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("ANX2 Get Summary API response is {} ",
							apiResp);
				}

				/*
				 * apiResp =
				 * "{'gstin':'32SSSKL8363S1ZG','rtnprd':'032019','summtyp':" +
				 * "'L','chksum':'847d6bbbd4aa4410f03e1b72358b16e299a8ffe8277382e2726e18b45711db95','secsum':[{'secnm':'b2b','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279','ttldoc':10,'ttlval':12345,'ttligst':124.99,'ttlcgst':3423,'ttlsgst':5589.87,'ttlcess':3423,'nettax':1234,'actionsum':[{'action':'A','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279','ttldoc':10,'ttlval':12345,'ttligst':124.99,'ttlcgst':3423,'ttlsgst':5589.87,'ttlcess':3423,'nettax':1234}],'cptysum':[{'ctin':'20GRRHF2562D3A3','totactsum':[{'action':'accept','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279','ttldoc':10,'nettax':2341}]}]},{'secnm':'de','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279','ttldoc':10,'ttlval':12345,'ttligst':124.99,'ttlcgst':3423,'ttlsgst':5589.87,'ttlcess':3423,'nettax':1234,'actionsum':[{'action':'R','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279','ttldoc':10,'ttlval':12345,'ttligst':124.99,'ttlcgst':3423,'ttlsgst':5589.87,'ttlcess':3423,'nettax':1234}],'cptysum':[{'ctin':'20GRRHF2562D3A3','totactsum':[{'action':'accept','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279','ttldoc':10,'nettax':2341}]}]},{'secnm':'isdc','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279','ttldoc':10,'ttlval':12345,'ttligst':124.99,'ttlcgst':3423,'ttlsgst':5589.87,'ttlcess':3423,'nettax':1234}]}";
				 */
				if (apiResp != null) {
					saveJsonAsRecords(apiResp, groupCode, dto, batch);
				}

			} else {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"No Reference Id got generated for ANX2 Generate Summary API.");
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
	public Boolean generateAnx2Summary(Anx2GetInvoicesReqDto dto,
			String groupCode) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		String data = gson.toJson(dto);
		String apiResp = anx2SummaryDataAtGstn.generateAnx2Summary(dto,
				groupCode, data);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("ANX2 Generate Summary API response is {} ", apiResp);
		}
		if (apiResp != null) {
			return true;
		}
		return false;

	}
	
	public void saveJsonAsRecords(String apiResp, String groupCode,
			Anx2GetInvoicesReqDto dto, GetAnx1BatchEntity batch) {
		
		if (apiResp != null) {
			batchPayloadHelper.dumpGetResponsePayload(groupCode, dto.getGstin(),
					dto.getReturnPeriod(), batch.getId(), apiResp,
					APIConstants.SYSTEM);
			// InActiveting Previous Header Records
			repository.softlyDeleteAnx1Data(dto.getGstin(),
					dto.getReturnPeriod());

			// Parse and Save new Header
			anx2SummaryDataParser.parseAnx2SummaryData(dto, apiResp,
					batch.getId());

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
