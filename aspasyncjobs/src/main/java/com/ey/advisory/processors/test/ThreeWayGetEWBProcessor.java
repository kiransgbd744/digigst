/**
 * 
 */
package com.ey.advisory.processors.test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.asprecon.EwbUploadProcessedHeaderRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.EwbUploadProcessedItemRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GetEwbLogRepo;
import com.ey.advisory.app.service.upload.way3recon.Ewb3WayGetAPIService;
import com.ey.advisory.app.service.upload.way3recon.Ewb3WayReconCommUtility;
import com.ey.advisory.app.service.upload.way3recon.GetEwbLogEntity;
import com.ey.advisory.app.services.ewb.EwbBusinessService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.ewb.dto.GetEwbResponseDto;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Reddy
 *
 * 
 */
@Slf4j
@Component("ThreeWayGetEWBProcessor")
public class ThreeWayGetEWBProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("EwbUploadProcessedHeaderRepository")
	private EwbUploadProcessedHeaderRepository psdHeaderRepo;

	@Autowired
	@Qualifier("EwbUploadProcessedItemRepository")
	private EwbUploadProcessedItemRepository psdItemRepo;

	@Autowired
	@Qualifier("EwbBusinessServiceImpl")
	EwbBusinessService ewbService;

	@Autowired
	Ewb3WayReconCommUtility ewbCommonUtility;

	@Autowired
	@Qualifier("Ewb3WayGetAPIServiceImpl")
	Ewb3WayGetAPIService ewbGetApiService;

	@Autowired
	private GetEwbLogRepo getEwbLogRepo;

	@Override
	public void execute(Message message, AppExecContext context) {

		JsonParser parser = new JsonParser();
		String jsonString = message.getParamsJson();
		JsonObject json = (JsonObject) parser.parse(jsonString);
		String ewbDetails = json.get("ewbNos").getAsString();
		List<String> ewbList = new ArrayList<String>(
				Arrays.asList(ewbDetails.split(",")));
		String gstin = json.get("gstin").getAsString();
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		try {

			List<GetEwbResponseDto> ewbDtoList = new ArrayList<GetEwbResponseDto>();

			List<GetEwbLogEntity> logEntityList = new ArrayList<GetEwbLogEntity>();

			for (String ewbNo : ewbList) {
				String msg = null;
				GetEwbLogEntity logEntity = new GetEwbLogEntity();
				GetEwbResponseDto resp;
				try {
					resp = ewbService.getEWB(ewbNo, gstin);
					logEntity.setEwbNo(ewbNo);
					if (Strings.isNullOrEmpty(resp.getErrorCode())) {
						ewbDtoList.add(resp);
						logEntity.setEwbResp(gson.toJson(resp, GetEwbResponseDto.class));
					} else {
						LOGGER.error(
								"NIC GET EWB API call has returned error response {}",
								resp.getErrorMessage());
						msg = resp.getErrorCode() + "-" + resp.getErrorMessage();
					}
				} catch (Exception e) {
					String errMsg = String.format(
							"Error occured while invoking GET Ewb by No API for GSTIN %s and Ewb No %s",
							gstin, ewbNo);
					msg = e.getMessage();
					LOGGER.error(errMsg, e);
				}
					logEntity.setSuppGstin(gstin);
					logEntity.setErrMsg(msg);
					logEntity.setCreatedOn(LocalDateTime.now());
					logEntityList.add(logEntity);
				//}
			}

			if (!ewbDtoList.isEmpty()) {
				ewbGetApiService.prepareGetEwayBillData(ewbDtoList);
			}
			if (!logEntityList.isEmpty()) {
				getEwbLogRepo.saveAll(logEntityList);
			}
		} catch (Exception ex) {
			String errMsg = "Error occured while invoking GET eway bill call";
			LOGGER.error(errMsg, ex);
			throw new AppException(ex,
					ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);

		}
	}
}
