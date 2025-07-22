package com.ey.advisory.app.services.jobs.anx2;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.entities.gstr6.GetGstr6ItcDetailsEntity;
import com.ey.advisory.app.data.repositories.client.Anx1SummaryAtGstnRepository;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.data.repositories.client.Gstr6GetItcGstnRepository;
import com.ey.advisory.app.services.jobs.gstr1.GetBatchPayloadHandler;
import com.ey.advisory.app.services.jobs.gstr6.Gstr6ItcGetDataParser;
import com.ey.advisory.app.services.jobs.gstr6.Gstr6SummaryDataAtGstnImpl;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr6GetInvoicesReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Siva.Nandam
 *
 */
@Service("Gstr6SummaryAtGstnImpl")
@Slf4j
public class Gstr6SummaryAtGstnImpl/* implements Gstr6SummaryAtGstn*/ {

	@Autowired
	@Qualifier("Anx1SummaryAtGstnRepository")
	private Anx1SummaryAtGstnRepository repository;

	@Autowired
	@Qualifier("Gstr6SummaryDataParserImpl")
	private Gstr6SummaryDataParserImpl gsr6SummaryDataParser;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;
	
	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	private GetAnx1BatchEntity batch;
	
	@Autowired
	@Qualifier("Gstr6SummaryDataAtGstnImpl")
	private Gstr6SummaryDataAtGstnImpl gstr6SummaryDataAtGstnImpl;
	
	@Autowired
	private GetBatchPayloadHandler batchPayloadHelper;
	
	@Autowired
	@Qualifier("Gstr6ItcGetDataParserImpl")
	private Gstr6ItcGetDataParser gstr6ItcGetDataParser;

	@Autowired
	@Qualifier("Gstr6GetItcGstnRepository")
	private Gstr6GetItcGstnRepository itcHeaderRepo;

	//@Override
	public ResponseEntity<String> getGstr6Summary(Gstr6GetInvoicesReqDto dto,
			String groupCode) {

		String apiResp = null;
		try {
			String type = APIIdentifiers.GSTR6_GETSUM;
			batch = batchUtil.makeBatchGstr6(dto, type);
			TenantContext.setTenantId(groupCode);
			// InActiveting Previous Batch Records
			batchRepo.softlyDelete(type.toUpperCase(),
					APIConstants.GSTR6.toUpperCase(), dto.getGstin(),
					dto.getReturnPeriod());
			// Save new Batch
			batch = batchRepo.save(batch);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("gstr6 get summery api Started");
			}
			apiResp = gstr6SummaryDataAtGstnImpl.gstr6Summary(dto, groupCode);

			if (apiResp != null) {
				saveJsonAsRecords(apiResp, groupCode, dto, batch);
			}

			String msg = "Success";
			// LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("S", msg)));

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {

			batch.setEndTime(LocalDateTime.now());
			batch.setStatus(APIConstants.FAILED);
			batchRepo.save(batch);
			String msg = "App Exeption";
			LOGGER.error(msg, ex);
			String msg1 = "Getting Error From SandBox";
			// LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg1)));

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
		//return apiResp;
	}
	
	//@Override
	private void saveJsonAsRecords(String apiResp, String groupCode,
			Gstr6GetInvoicesReqDto dto, GetAnx1BatchEntity batch) {

		if (apiResp != null && !apiResp.trim().isEmpty()) {
			batchPayloadHelper.dumpGetResponsePayload(groupCode, dto.getGstin(),
					dto.getReturnPeriod(), batch.getId(), apiResp,
					APIConstants.SYSTEM);
			gsr6SummaryDataParser.parseAnx2SummaryData(dto, apiResp,
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

	/*private GetAnx1BatchEntity makeBatch(Gstr6GetInvoicesReqDto dto,
			String type) {
		LocalDateTime now = EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now());
		GetAnx1BatchEntity batch = new GetAnx1BatchEntity();
		batch.setApiSection(APIConstants.GSTR6.toUpperCase());
		batch.setSGstin(dto.getGstin());
		batch.setTaxPeriod(dto.getReturnPeriod());
		
		batch.setAction(dto.getAction());
		batch.setCtin(dto.getCtin());
		//batch.setETin(dto.getEtin());
		batch.setFromTime(
				dto.getFromTime() != null && !dto.getFromTime().isEmpty()
						? DateUtil.stringToTime(dto.getFromTime(),
								DateUtil.DATE_FORMAT1)
						: null);
		batch.setDerTaxPeriod(GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
		//batch.setProcessingStatus(APIConstants.PROCESSING_STATUS);
		batch.setType(type != null ? type.toUpperCase() : null);
		batch.setCreatedBy(APIConstants.SYSTEM.toUpperCase());
		batch.setCreatedOn(now);
		
		batch.setIsDelete(false);
		batch.setStartTime(now);
		batch.setStatus(APIConstants.INITIATED);
		
		//Extra column added only in ANX2 GET to Generate Request ID
		//batch.setRequestId(dto.getRequestId());
		return batch;
	}
	*/
	
	
	
	public ResponseEntity<String> getGstr6ITCDetailsSummary(Gstr6GetInvoicesReqDto dto,
			String groupCode) {

		String apiResp = null;
		try {
			String type = APIConstants.ITC;
			batch = batchUtil.makeBatchGstr6(dto, type);
			TenantContext.setTenantId(groupCode);
			// InActiveting Previous Batch Records
			batchRepo.softlyDelete(type.toUpperCase(),
					APIConstants.GSTR6.toUpperCase(), dto.getGstin(),
					dto.getReturnPeriod());
			// Save new Batch
			batch = batchRepo.save(batch);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("gstr6 get summery api Started");
			}
			apiResp = gstr6SummaryDataAtGstnImpl.gstr6ItcDetailsSummary(dto, groupCode);

			if (apiResp != null) {
				saveJsonAsRecordsIntoItcTables(apiResp, groupCode, dto, batch);
			}

			String msg = "Success";
			// LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("S", msg)));

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {

			batch.setEndTime(LocalDateTime.now());
			batch.setStatus(APIConstants.FAILED);
			batchRepo.save(batch);
			String msg = "App Exeption";
			LOGGER.error(msg, ex);
			String msg1 = "Getting Error From SandBox";
			// LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg1)));

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
		//return apiResp;
	}
	
	private void saveJsonAsRecordsIntoItcTables(String apiResp, String groupCode,
			Gstr6GetInvoicesReqDto dto, GetAnx1BatchEntity batch) {
		LocalDateTime now = EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now());
		if (apiResp != null && !apiResp.trim().isEmpty()) {
			batchPayloadHelper.dumpGetResponsePayload(groupCode, dto.getGstin(),
					dto.getReturnPeriod(), batch.getId(), apiResp,
					APIConstants.SYSTEM);
			
			itcHeaderRepo.softlyDeleteItcHeader(dto.getGstin(),
					dto.getReturnPeriod(), now);
			List<GetGstr6ItcDetailsEntity> itcEntities = gstr6ItcGetDataParser
					.parseItcGetData(dto, apiResp, dto.getType(), batch.getId(),
							now);
			if (itcEntities != null && !itcEntities.isEmpty()) {
				itcHeaderRepo.saveAll(itcEntities);
			}
			
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
}
