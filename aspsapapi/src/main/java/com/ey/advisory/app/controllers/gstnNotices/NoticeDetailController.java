package com.ey.advisory.app.controllers.gstnNotices;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.entities.client.asprecon.TblGetNoticesEntity;
import com.ey.advisory.app.get.notices.handlers.GetApiCallDashboardStatusService;
import com.ey.advisory.app.get.notices.handlers.GstnNoticeReqDto;
import com.ey.advisory.app.get.notices.handlers.NoticeDetailService;
import com.ey.advisory.app.get.notices.handlers.NoticeEntitySummaryDto;
import com.ey.advisory.app.get.notices.handlers.NoticeSummaryDto;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.ReconStatusConstants;
import com.ey.advisory.core.async.domain.master.GstnNoticeAlertCodeEntity;
import com.ey.advisory.core.async.domain.master.GstnNoticeModuleCodeEntity;
import com.ey.advisory.core.async.repositories.master.GstnNoticeAlertCodeRepo;
import com.ey.advisory.core.async.repositories.master.GstnNoticeModuleCodeRepo;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.gstr2.userdetails.EntityService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.jsonwebtoken.lang.Collections;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class NoticeDetailController {
	@Autowired
	@Qualifier("EntityServiceImpl")
	private EntityService entityService;

	@Autowired
	@Qualifier("GetApiCallDashboardStatusServiceImpl")
	GetApiCallDashboardStatusService getApiCallDashboardStatusService;

	@Autowired
	@Qualifier("NoticeDetailServiceImpl")
	NoticeDetailService noticeDetailService;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	GSTNDetailRepository gSTNDetailRepository;

	@Autowired
	@Qualifier("GstnNoticeModuleCodeRepo")
	GstnNoticeModuleCodeRepo moduleRepo;

	@Autowired
	@Qualifier("GstnNoticeAlertCodeRepo")
	GstnNoticeAlertCodeRepo alterRepo;

	public static final DateTimeFormatter formatter = DateTimeFormatter
			.ofPattern("dd-MM-yyyy");

	@PostMapping(value = "/ui/fetchNoticeSummary")
	public ResponseEntity<String> fetchNoticeSummary(
			@RequestBody String jsonParams) {

		LOGGER.debug("Entering fetchNoticeSummary with params: {}", jsonParams);
		JsonObject resp = new JsonObject();
		try {
			JsonObject requestObject = JsonParser.parseString(jsonParams)
					.getAsJsonObject();
			JsonObject respObject = requestObject.get("req").getAsJsonObject();

			Gson gson = GsonUtil.newSAPGsonInstance();
			GstnNoticeReqDto reqDto = gson.fromJson(respObject,
					GstnNoticeReqDto.class);

			if (reqDto.getEntityId() == null
					|| reqDto.getSelectionCriteria() == null)
				throw new Exception(
						"Invalid Entity/Criteria For Parmas is empty.");

			// Get all the gstin which is mapped to entity
			List<String> allGstinList = new ArrayList<>();

			if (reqDto.getGstins() != null
					&& !Collections.isEmpty(reqDto.getGstins())) {
				allGstinList = reqDto.getGstins();
			} else {
				List<Object[]> gstnObject = getFetchNotGstinList(
						reqDto.getEntityId());
				Map<String, String> gstnRegMap = gstnObject.stream()
						.collect(Collectors.toMap(obj -> (String) obj[0],
								obj -> (String) obj[1]));

				LOGGER.debug(" gstnRegMap {}", gstnRegMap);
				List<String> gstnsList = gstnRegMap.keySet().stream()
						.collect(Collectors.toList());
				LOGGER.debug(" gstnsList {}", gstnsList);

				allGstinList = gstnsList;
				LOGGER.debug(" allGstinList {}", allGstinList);
				reqDto.setGstins(allGstinList);
			}

			LOGGER.debug(" reqDto {} ", reqDto);
			List<NoticeEntitySummaryDto> masterGetCallStatusList = getApiCallDashboardStatusService
					.fetchNoticeSummary(allGstinList, reqDto);
			JsonObject response = new JsonObject();

			response.add("notices", gson.toJsonTree(masterGetCallStatusList));
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("S", null)));
			resp.add("resp", response);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception e) {
			LOGGER.error(" fetchNoticeSummary method eror ", e);
			String msg = "Unexpected error ";
			LOGGER.error(msg, e);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@PostMapping(value = "/ui/fetchGstinNoticeSummary")
	public ResponseEntity<String> fetchGstinNoticeSummary(
			@RequestBody String jsonParams) {

		JsonObject resp = new JsonObject();
		try {

			List<NoticeSummaryDto> respDto = new ArrayList<>();
			JsonObject requestObject = JsonParser.parseString(jsonParams)
					.getAsJsonObject();

			JsonObject hdr = requestObject.get("hdr").getAsJsonObject();
			JsonObject respObject = requestObject.get("req").getAsJsonObject();

			Gson gson = GsonUtil.newSAPGsonInstance();
			GstnNoticeReqDto reqDto = gson.fromJson(respObject,
					GstnNoticeReqDto.class);

			reqDto.setPageNum(hdr.get("pageNum").getAsInt());
			reqDto.setPageSize(hdr.get("pageSize").getAsInt());

			String logMsg = String.format("Entering fetchGstinNoticeSummary ");
			LOGGER.debug(logMsg);

			Pair<List<TblGetNoticesEntity>, Integer> records = noticeDetailService
					.fetchGstnNoticeStats(reqDto);

			if (records != null) {
				LOGGER.debug("Total notice records found: {}",
						records.getValue0() != null ? records.getValue0().size()
								: 0);
				Map<String, String> moduleAlertMap = new HashMap<>();
				List<GstnNoticeModuleCodeEntity> moduleList = moduleRepo
						.findByIsActiveTrue();
				List<GstnNoticeAlertCodeEntity> alertList = alterRepo
						.findByIsActiveTrue();

				moduleAlertMap.putAll(moduleList.stream()
						.collect(Collectors.toMap(
								m -> m.getModuleCode().toString(),
								m -> m.getDesc().toString())));

				moduleAlertMap.putAll(alertList.stream()
						.collect(Collectors.toMap(
								m -> m.getAlertCode().toString(),
								m -> m.getDesc().toString())));

				for (TblGetNoticesEntity record : records.getValue0()) {
					NoticeSummaryDto dto = new NoticeSummaryDto();
					dto.setGstin(record.getGstin());
					dto.setNoticeId(record.getReferenceId());
					dto.setDateOfIssue(persistString(record.getDateOfIssue()));
					dto.setDueDateOfResponse(
							persistString(record.getDueDateOfReply()));
					dto.setDateOfResponse(
							persistString(record.getDateOfResponse()));
					String status = "";
					String timeLeft = "";
					if (record.getDateOfResponse() != null) {
						status = ReconStatusConstants.Responded;
					} else if (record.getDueDateOfReply() != null) {
						LocalDate dueDate = record.getDueDateOfReply();
						LocalDate today = LocalDate.now();
						if (!dueDate.isBefore(today)) {
							status = ReconStatusConstants.Pending;
							long days = ChronoUnit.DAYS.between(LocalDate.now(),
									dueDate);
							timeLeft = ReconStatusConstants.Due_In + days + " "
									+ ReconStatusConstants.Days;
						} else {
							status = ReconStatusConstants.Overdue;
							long days = ChronoUnit.DAYS.between(dueDate,
									LocalDate.now());
							timeLeft = ReconStatusConstants.OverDueDays + days + " "
									+ ReconStatusConstants.Days;
						}
					}
					dto.setStatus(status);
					dto.setTimeLeft(
							ReconStatusConstants.Responded.equals(status) ? ""
									: timeLeft);
					dto.setCategoryOfNotice(
							record.getModuleCode() + (moduleAlertMap
									.containsKey(record.getModuleCode())
											? " - " + moduleAlertMap
													.get(record.getModuleCode())
											: null));
					dto.setTypeOfNotice(record.getAlertCode() + (moduleAlertMap
							.containsKey(record.getAlertCode())
							? " - " + moduleAlertMap
									.get(record.getAlertCode())
							: null) );
					
					String periodRange = formatTaxPeriodRange(
							record.getDerivedFromTaxPeriod(),
							record.getDerivedToTaxPeriod());
					dto.setPeriodRange(periodRange);
					respDto.add(dto);
				}
			}
			JsonObject response = new JsonObject();
			JsonObject hdrResp = new JsonObject();

			response.add("notices", gson.toJsonTree(respDto));
			hdrResp.addProperty("totalCount", records.getValue1());
			hdrResp.addProperty("pageNum", reqDto.getPageNum());
			hdrResp.addProperty("pageSize", reqDto.getPageSize());
			hdrResp.addProperty("status", "S");
			resp.add("hdr", hdrResp);
			resp.add("resp", response);

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception e) {
			LOGGER.error(" fetchGstinNoticeSummary error ", e);
			String msg = "Unexpected error ";
			LOGGER.error(msg, e);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	private String formatTaxPeriodRange(Integer derivedFromTaxPeriod,
			Integer derivedToTaxPeriod) {
		if (derivedFromTaxPeriod == null || derivedToTaxPeriod == null) {
			return "";
		}

		// Convert from YYYYMM to Year and Month
		int fromYear = derivedFromTaxPeriod / 100;
		int fromMonth = derivedFromTaxPeriod % 100;

		int toYear = derivedToTaxPeriod / 100;
		int toMonth = derivedToTaxPeriod % 100;

		// Convert numeric month to string (e.g. 1 => Jan)
		String[] monthNames = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul",
				"Aug", "Sep", "Oct", "Nov", "Dec" };

		String from = (fromMonth >= 1 && fromMonth <= 12)
				? monthNames[fromMonth - 1] + " " + fromYear
				: derivedFromTaxPeriod.toString();

		String to = (toMonth >= 1 && toMonth <= 12)
				? monthNames[toMonth - 1] + " " + toYear
				: derivedToTaxPeriod.toString();

		return from + " - " + to;
	}

	private String persistString(LocalDate dateOfIssue) {
		if(dateOfIssue ==null)
			return null;
		
		String formattedDate = dateOfIssue.format(formatter);
		return formattedDate;
	}

	public List<Object[]> getFetchNotGstinList(Long entityId) {

		List<Object[]> gstnObject = gSTNDetailRepository
				.getGstinBasedOnRegTypeforACD(entityId, Arrays.asList("REGULAR",
						"SEZ", "SEZU", "SEZD", "TDS", "ISD", "TCS", "ECOM"));

		return gstnObject;
	}

}