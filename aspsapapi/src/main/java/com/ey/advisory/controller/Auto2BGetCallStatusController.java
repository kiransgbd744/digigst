package com.ey.advisory.controller;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.Gstr2bAutoCommEntity;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.dashboard.apiCall.APICAllDtls;
import com.ey.advisory.app.dashboard.apiCall.ApiGstinDetailsDto;
import com.ey.advisory.app.dashboard.apiCall.TaxPeriodDetailsDto;
import com.ey.advisory.app.data.services.Auto2bCallStatusService;
import com.ey.advisory.app.docs.dto.Auto2bGetCallStatusReqDto;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.async.domain.master.PeriodicExecJob;
import com.ey.advisory.core.async.repositories.master.PeriodicExecJobRepository;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.ey.advisory.gstr2.userdetails.EntityService;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Ravindra V S
 *
 */

@Slf4j
@RestController
public class Auto2BGetCallStatusController {

	@Autowired
	@Qualifier("EntityServiceImpl")
	private EntityService entityService;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	GSTNDetailRepository gSTNDetailRepository;

	@Autowired
	@Qualifier("Auto2bCallStatusServiceImpl")
	private Auto2bCallStatusService auto2bCallStatusService;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	GSTNAuthTokenService authTokenService;

	@Autowired
	PeriodicExecJobRepository periodicExecJobRepo;

	@PostMapping(value = "/ui/gstr2bAutoGetCallStatus", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGSTINsandStatus(
			@RequestBody String jsonString) {

		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Entered Auto 2B call status with input %s",
						jsonString);
				LOGGER.debug(msg);
			}

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
			String reqJson = requestObject.get("req").getAsJsonObject()
					.toString();
			Auto2bGetCallStatusReqDto reqCallStatusDto = gson.fromJson(reqJson,
					Auto2bGetCallStatusReqDto.class);

			if (reqCallStatusDto.getEntityId() == null
					|| reqCallStatusDto.getFy() == null
					|| reqCallStatusDto.getReturnType() == null) {

				String msg = "Entity Id, Financial year,"
						+ " Return type should be mandatory";
				JsonObject resp = new JsonObject();
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", gson.toJsonTree(msg));
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			}

			Long entityId = reqCallStatusDto.getEntityId();

			String financialPeriod = reqCallStatusDto.getFy();

			String returnType = reqCallStatusDto.getReturnType();

			List<Object[]> gstnObject = gSTNDetailRepository
					.getGstinBasedOnRegTypeforACD(entityId,
							GenUtil.getRegTypesBasedOnTypeForACD(returnType));

			Map<String, String> gstnRegMap = gstnObject.stream()
					.collect(Collectors.toMap(obj -> (String) obj[0],
							obj -> (String) obj[1]));

			List<String> gstnsList = gstnRegMap.keySet().stream()
					.collect(Collectors.toList());

			Collections.sort(gstnsList);

			if (LOGGER.isDebugEnabled()) {
				String msg = "EntityServiceImpl"
						+ ".getGSTINsForEntity Preparing Response Object";
				LOGGER.debug(msg);
			}
			String startMonth = "04";
			String endMonth = "03";
			String appendMonthYear = null;
			String appendMonthYear1 = null;
			if (financialPeriod != null && !financialPeriod.isEmpty()) {
				String[] arrOfStr = financialPeriod.split("-", 2);
				appendMonthYear = arrOfStr[0] + startMonth;
				appendMonthYear1 = "20" + arrOfStr[1] + endMonth;
			}
			int derivedStartPeriod = Integer.parseInt(appendMonthYear);

			int derivedEndPeriod = Integer.parseInt(appendMonthYear1);

			List<Gstr2bAutoCommEntity> getBatchEntityDetails = auto2bCallStatusService
					.fetchGetStatus(gstnsList, derivedStartPeriod,
							derivedEndPeriod, returnType);

			List<String> taxPeriods = GenUtil
					.extractTaxPeriodsFromFY(financialPeriod, "");

			List<ApiGstinDetailsDto> sGstinTaxperiodDetails = null;

			if (!getBatchEntityDetails.isEmpty()) {
				sGstinTaxperiodDetails = auto2bCallStatusService
						.getTaxPeriodDetails(getBatchEntityDetails, returnType);

				sGstinTaxperiodDetails.stream().forEach(
						x -> populateDefaultStatus(x.getTaxPeriodDetails(),
								taxPeriods));

				List<String> gstinsWithGetCall = new ArrayList<>();

				gstinsWithGetCall = sGstinTaxperiodDetails.stream()
						.map(o -> o.getGstin()).collect(Collectors.toList());

				gstnsList.removeAll(gstinsWithGetCall);

				List<ApiGstinDetailsDto> notInitDto = populateDefaultValues(
						gstnsList, taxPeriods);

				sGstinTaxperiodDetails.addAll(notInitDto);

			} else {
				sGstinTaxperiodDetails = populateDefaultValues(gstnsList,
						taxPeriods);
			}

			sGstinTaxperiodDetails.forEach(o -> {
				o.setAuthStatus(authTokenService
						.getAuthTokenStatusForGstin(o.getGstin()));
				o.setRegistrationType(gstnRegMap.get(o.getGstin()));
			});

			APICAllDtls apiDtls = new APICAllDtls();
			apiDtls.setApiGstinDetails(sGstinTaxperiodDetails);

			String scheduledDate = getNextScheduledTime();
			apiDtls.setScheduledDateAndTime(scheduledDate);

			JsonObject resp = new JsonObject();
			JsonObject gstinResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(apiDtls);
			gstinResp.add("gstins", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = " Unexpected error occured";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private List<ApiGstinDetailsDto> populateDefaultValues(
			List<String> gstnsList, List<String> taxPeriods) {

		List<ApiGstinDetailsDto> list = new ArrayList<>();
		gstnsList.stream().forEach(o -> {
			ApiGstinDetailsDto dto = new ApiGstinDetailsDto();
			dto.setGstin(o);
			List<TaxPeriodDetailsDto> defaultStatus = new ArrayList<>();
			taxPeriods.stream().forEach(x -> defaultStatus
					.add(new TaxPeriodDetailsDto(x, "NOT_INITIATED")));
			dto.setTaxPeriodDetails(defaultStatus);

			list.add(dto);
		});

		return list;
	}

	private void populateDefaultStatus(
			List<TaxPeriodDetailsDto> taxPeriodDetails,
			List<String> taxPeriods) {

		List<String> totalTaxPeriods = new ArrayList<>(taxPeriods);

		List<String> availableTaxPeriods = taxPeriodDetails.stream()
				.map(o -> o.getTaxPeriod()).collect(Collectors.toList());

		totalTaxPeriods.removeAll(availableTaxPeriods);

		totalTaxPeriods.stream().forEach(o -> taxPeriodDetails
				.add(new TaxPeriodDetailsDto(o, "NOT_INITIATED")));

	}

	private String getNextScheduledTime() {

		PeriodicExecJob job = periodicExecJobRepo.findByJobCategory("MonitorGstr2BGetCall");

		if (job != null) {

			CronExpression cronGenerator = CronExpression.parse(job.getCronExpression());

			ZonedDateTime jobStartDateTime = ZonedDateTime.ofInstant(job.getJobstartDate().toInstant(),
					ZoneId.systemDefault());

			ZonedDateTime nextTime = cronGenerator.next(jobStartDateTime);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

			formatter.format(EYDateUtil.toISTDateTimeFromUTC(Date.from(nextTime.toInstant())));

		}
		return null;
	}

}
