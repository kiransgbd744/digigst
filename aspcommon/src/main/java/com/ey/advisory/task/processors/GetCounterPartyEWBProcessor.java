/**
 * 
 */
package com.ey.advisory.task.processors;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.clientBusiness.CounterPartyEwbBillRepository;
import com.ey.advisory.app.data.repositories.clientBusiness.CounterPartyInvocControlRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.ewb.api.GetOtherPartyEWB;
import com.ey.advisory.ewb.app.api.APIExecutor;
import com.ey.advisory.ewb.app.api.APIResponse;
import com.ey.advisory.ewb.common.EyEwbUtilMethods;
import com.ey.advisory.ewb.data.entities.clientBusiness.CounterPartyEwbBillEntity;
import com.ey.advisory.ewb.dto.GetCounterPartyResDto;
import com.ey.advisory.ewb.dto.GetCounterPartyRespListDto;
import com.ey.advisory.processing.message.GetCounterPartyMsg;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun.KA
 *
 * 
 */
@Slf4j
@Component("GetCounterPartyEWBProcessor")
public class GetCounterPartyEWBProcessor implements TaskProcessor {

	@Autowired
	CounterPartyInvocControlRepository counterPartyInvocControlRepo;

	@Autowired
	CounterPartyEwbBillRepository counterPartyEwbBillRepo;

	@Autowired
	@Qualifier("DefaultEWBAPIExecutor")
	private APIExecutor apiExecutor;

	@Autowired
	@Qualifier("GetOtherPartyEWBImpl")
	GetOtherPartyEWB getOtherPartyEWB;

	public static final DateTimeFormatter DOC_DATE_FORMATTER = DateTimeFormatter
			.ofPattern("dd/MM/yyyy");

	public static final DateTimeFormatter EWB_DATE_FORMATTER = DateTimeFormatter
			.ofPattern("dd/MM/yyyy hh:mm:ss a");

	@Override
	public void execute(Message message, AppExecContext context) {

		String jsonString = message.getParamsJson();
		JsonParser parser = new JsonParser();
		JsonObject json = (JsonObject) parser.parse(jsonString);
		Gson gson = GsonUtil.newSAPGsonInstance();

		GetCounterPartyMsg msg = gson.fromJson(json, GetCounterPartyMsg.class);

		LocalDateTime curDate = LocalDateTime.now();
		counterPartyInvocControlRepo.updateStatusAndStartDate(msg.getGstin(),
				curDate, null, msg.getDate());

		try {
			APIResponse response = getOtherPartyEWB
					.getCounterPartyEwb(msg.getGstin(), msg.getDate());

			if (!response.isSuccess()) {
				String errMsg = String.format(
						"Error response returned by NIC counter party GET call : %s",
						response.getErrors());
				
				String errDesc = response.getErrors().get(0).getErrorDesc();
				LOGGER.error(errMsg);
				throw new AppException(errDesc);

			}
			GetCounterPartyRespListDto resp = createGetCounterPartyResponse(
					response);

			List<GetCounterPartyResDto> respList = resp
					.getCounterPartyRespList();

			List<CounterPartyEwbBillEntity> counterPartyEntities = respList
					.stream().map(o -> convertToEntity(o, msg))
					.collect(Collectors.toList());

			counterPartyEwbBillRepo.saveAll(counterPartyEntities);

			counterPartyInvocControlRepo.updateStatusAndEndDate(msg.getGstin(),
					curDate, "SUCCESS", null, msg.getDate());

		} catch (Exception ex) {
			String errMsg = "Error occured while invoking GET counter party api call";

			counterPartyInvocControlRepo.updateStatusAndEndDate(msg.getGstin(),
					curDate, "FAILED", ex.getMessage(), msg.getDate());

			LOGGER.error(errMsg, ex);
			throw new AppException(errMsg, ex);

		}

	}

	private CounterPartyEwbBillEntity convertToEntity(GetCounterPartyResDto o,
			GetCounterPartyMsg msg) {

		CounterPartyEwbBillEntity obj = new CounterPartyEwbBillEntity();

		obj.setDocNo(o.getDocNo());
		obj.setDocDate(toDocDate(o.getDocDate(), DOC_DATE_FORMATTER));
		obj.setEwbDate(toEwbDate(o.getEwayBillDate(), EWB_DATE_FORMATTER));
		obj.setEwbNo(String.valueOf(o.getEwbNo()));
		obj.setFetchStatus("NOT_INITIATED");
		obj.setFromGstin(o.getFromgstin());
		obj.setFromTradeName(o.getFromTradename());
		obj.setGenGstin(o.getGenGstin());
		obj.setGenMode(o.getGenMode());
		obj.setHsnCode(o.getHsnCode());
		obj.setHsnDesc(o.getHsndesc());
		obj.setToGstin(o.getTogstin());
		obj.setToTradeName(o.getToTradename());
		obj.setTotInvVal(o.getTotInvValue());
		obj.setRejectStatus(o.getRejectStatus());
		obj.setStatus(o.getStatus());
		obj.setControlId(msg.getControlId());
		obj.setClientGstin(msg.getGstin());
		obj.setRevIntStatus("NOT_INITIATED");
		obj.setEwbGenDate(msg.getDate());
		obj.setCreatedOn(LocalDateTime.now());

		return obj;
	}

	private GetCounterPartyRespListDto createGetCounterPartyResponse(
			APIResponse response) {
		if (response.isSuccess()) {
			String jsonResp = "";
			Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
			jsonResp = response.getResponse();

			Type typeList = new TypeToken<List<GetCounterPartyResDto>>() {
			}.getType();
			List<GetCounterPartyResDto> respList = gson.fromJson(jsonResp,
					typeList);

			GetCounterPartyRespListDto dto = new GetCounterPartyRespListDto();
			dto.setCounterPartyRespList(respList);
			return dto;
		} else {
			Pair<String, String> errCodeDesc = EyEwbUtilMethods
					.createErrorResponse(response.getErrors());
			GetCounterPartyRespListDto resp = new GetCounterPartyRespListDto();
			resp.setErrorCode(errCodeDesc.getValue0());
			resp.setErrorDesc(errCodeDesc.getValue1());
			return resp;
		}

	}

	public LocalDateTime toDocDate(String date, DateTimeFormatter formatter) {

		return LocalDate.parse(date, formatter).atStartOfDay();
	}

	public LocalDateTime toEwbDate(String date, DateTimeFormatter formatter) {

		return LocalDateTime.parse(date, formatter);
	}

}
