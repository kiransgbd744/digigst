package com.ey.advisory.app.services.jobs.anx2;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.repositories.client.Anx2B2BDESummaryAtGstnRepository;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.docs.dto.anx2.Anx2GetInvoicesReqDto;
import com.ey.advisory.app.services.jobs.gstr1.GetBatchPayloadHandler;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.api.APIResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Siva.Nandam
 *
 */
@Service("Itc04SummaryAtGstnImpl")
@Slf4j
public class Itc04SummaryAtGstnImpl {

	
	@Autowired
	@Qualifier("Itc04SummaryDataParserImpl")
	private Itc04SummaryDataParserImpl summaryDataParser;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;
	
	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	@Autowired
	private Anx2GetBatchUtil batchUtil2;

	private GetAnx1BatchEntity batch;
	
	@Autowired
	@Qualifier("DefaultNonStubExecutor")
	private APIExecutor apiExecutor;
	
	@Autowired
	@Qualifier("Anx2B2BDESummaryAtGstnRepository")
	private Anx2B2BDESummaryAtGstnRepository anx2B2BDESummaryAtGstnRepository;
	
	@Autowired
	private GetBatchPayloadHandler batchPayloadHelper;

	//@Override
	public String getGstr6Summary(Anx2GetInvoicesReqDto dto,
			String groupCode) {

		String apiResp = null;
		try {
			String type = APIIdentifiers.ITC04_GETSUM;
			batch = makeBatch(dto, type);
			TenantContext.setTenantId(groupCode);
			// InActiveting Previous Batch Records
			batchRepo.softlyDelete(type.toUpperCase(),
					APIConstants.ITC04_RETURN_TYPE.toUpperCase(), dto.getGstin(),
					dto.getReturnPeriod());
			// Save new Batch
			batch = batchRepo.save(batch);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("itc04 get summery api Started");
			}
			apiResp = gstr7Summary(dto, groupCode);

			if (apiResp != null) {
				saveJsonAsRecords(apiResp, groupCode, dto, batch);
			}else {
				LOGGER.error("No response received for Gstr1 Summary GET from GSTN.");
			}

		
		} catch (Exception ex) {

			batch.setEndTime(LocalDateTime.now());
			batch.setStatus(APIConstants.FAILED);
			batchRepo.save(batch);

			String msg = "failed to parse the response";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);

		}
		return apiResp;
	}


	
	public void saveJsonAsRecords(String apiResp, String groupCode,
			Anx2GetInvoicesReqDto dto, GetAnx1BatchEntity batch) {

		if (apiResp != null && !apiResp.trim().isEmpty()) {
			batchPayloadHelper.dumpGetResponsePayload(groupCode, dto.getGstin(),
					dto.getReturnPeriod(), batch.getId(), apiResp,
					APIConstants.SYSTEM);
			
			summaryDataParser.itc04SummaryData(dto, apiResp,
					batch.getId());
			batchUtil.updateById(batch.getId(), APIConstants.SUCCESS, null,
					null, false);

		} else {
			batchUtil.updateById(batch.getId(),
					APIConstants.SUCCESS_WITH_NO_DATA, null, null, false);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("No response from gstn");
			}
		}

	}

	public  String gstr7Summary(Anx2GetInvoicesReqDto dto,
			String groupCode) {


		// Invoke the GSTN API - Get Return Status API and get the JSON.
		APIParam param1 = new APIParam("gstin", dto.getGstin());
		APIParam param2 = new APIParam("ret_period", dto.getReturnPeriod());
		
		APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
				APIIdentifiers.ITC04_GETSUM, param1, param2);
		APIResponse resp = apiExecutor.execute(params, null);

		String apiResp = resp.getResponse();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("itc04 get summery api respons: {}",apiResp);
		}
		return apiResp;
	}
	
	public GetAnx1BatchEntity makeBatch(Anx2GetInvoicesReqDto dto,
			String type) {
		LocalDateTime now = EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now());
		GetAnx1BatchEntity batch = new GetAnx1BatchEntity();
		batch.setApiSection(APIConstants.ITC04_RETURN_TYPE.toUpperCase());
		batch.setSgstin(dto.getGstin());
		batch.setTaxPeriod(dto.getReturnPeriod());
		
		batch.setAction(dto.getAction());
		batch.setCtin(dto.getCtin());
		batch.setETin(dto.getEtin());
		batch.setFromTime(dto.getFromTime());
		/*batch.setFromTime(
				dto.getFromTime() != null && !dto.getFromTime().isEmpty()
						? DateUtil.stringToTime(dto.getFromTime(),
								DateUtil.DATE_FORMAT1)
						: null);*/
		batch.setType(type != null ? type.toUpperCase() : null);
		batch.setCreatedBy(APIConstants.SYSTEM.toUpperCase());
		batch.setCreatedOn(now);
		batch.setStartTime(now);
		batch.setStatus(APIConstants.INITIATED);
		
		
		return batch;
	}

}
