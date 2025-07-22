package com.ey.advisory.admin.azurebus.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.client.LandingDashboardRefreshRepository;
import com.ey.advisory.app.dashboard.homeOld.DashBoardHOReqDto;
import com.ey.advisory.app.dashboard.homeOld.DashboardHOService;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.services.dashboard.fiori.EInvoiceDistributionDto;
import com.ey.advisory.app.services.dashboard.fiori.EinvGenerationDto1;
import com.ey.advisory.app.services.dashboard.fiori.EinvGenerationDto2;
import com.ey.advisory.app.services.dashboard.fiori.EinvHeaderDetailsDto;
import com.ey.advisory.app.services.dashboard.fiori.EinvIrnDto;
import com.ey.advisory.app.services.dashboard.fiori.EinvoiceFioriDashboardDao;
import com.ey.advisory.app.services.dashboard.fiori.EinvoiceFioriDashboardService;
import com.ey.advisory.app.services.dashboard.fiori.EinvoiceStatusDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi.jain
 *
 */
@Slf4j
@Component("ITPEventEinvoiceDashboardResponseServiceImpl")
public class ITPEventEinvoiceDashboardResponseServiceImpl
		implements ITPEventEinvoiceDashboardResponseService {

	ImmutableMap<String, String> immutableMap = ImmutableMap.of("DIGI102",
			"No Record found for the provided Inputs", "DIGI103",
			"Invalid API Request Parameters", "GEN501",
			"Missing Mandatory Params");

	@Autowired
	@Qualifier("DashboardHOServiceImpl")
	private DashboardHOService dashboardHOService;

	@Autowired
	LandingDashboardRefreshRepository ldRepo;

	@Autowired
	@Qualifier("entityInfoRepository")
	private EntityInfoRepository entityInfoRepository;

	@Autowired
	@Qualifier("EinvoiceFioriDashboardDaoImpl")
	private EinvoiceFioriDashboardDao dashboardDao;

	@Autowired
	private EinvoiceFioriDashboardService dashboardService;

	@Autowired
	private GSTNDetailRepository gSTNDetailRepository;

	@Override
	public String getEinvDist(JsonObject obj, List<String> gstins,
			Long entityId) {

		try {
			Gson gson = new Gson();

			DashBoardHOReqDto requestDto = gson.fromJson(obj,
					DashBoardHOReqDto.class);

			Pair<LocalDate, LocalDate> pairDates = getMonthDates(
					requestDto.getTaxPeriod());

			List<EInvoiceDistributionDto> dto = dashboardService
					.getEinvDistributionData(gstins, pairDates.getValue0(),
							pairDates.getValue1());

			String jsonEINV = gson.toJson(dto);

			JsonObject resp = new JsonObject();

			JsonElement einvDistributionElement = new JsonParser().parse(jsonEINV);

			resp.add("det", einvDistributionElement);
			resp.add("id", gson.toJsonTree(requestDto.getId()));
			resp.add("requestParams", obj);
			
			JsonElement respBody = gson.toJsonTree(resp);
			

			JsonObject resps = new JsonObject();
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			LOGGER.debug("getEinvDist {}", resps.toString());
			return resps.toString();
		} catch (AppException e) {
			LOGGER.error("Exception while retriving the getEinvDist ", e);
			throw new AppException(e);
		}
	}

	@Override
	public String getEinvSts(JsonObject obj, List<String> gstins,
			Long entityId) {
		try {
			Gson gson = new Gson();

			DashBoardHOReqDto requestDto = gson.fromJson(obj,
					DashBoardHOReqDto.class);

			Pair<LocalDate, LocalDate> pairDates = getMonthDates(
					requestDto.getTaxPeriod());

			List<EinvoiceStatusDto> einvoiceStatus = dashboardService
					.getEinvStatusTable(gstins, pairDates.getValue0(),
							pairDates.getValue1());

			JsonObject resp = new JsonObject();

			String jsonEINV = gson.toJson(einvoiceStatus);
			JsonElement einvoiceStatusElement = new JsonParser()
					.parse(jsonEINV);

			resp.add("det", einvoiceStatusElement);
			JsonObject resps = new JsonObject();
			resp.add("id", gson.toJsonTree(requestDto.getId()));
			resp.add("requestParams", obj);

			
			JsonElement respBody = gson.toJsonTree(resp);
			
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);

			LOGGER.debug("einvoiceStatus {}", resps.toString());
			return resps.toString();

		} catch (AppException e) {
			LOGGER.error("Exception while retriving the einvoiceStatus ", e);
			throw new AppException(e);
		}
	}

	@Override
	public String getEinvGenTrends(JsonObject obj, List<String> gstins,
			Long entityId) {

		try {
			Gson gson = new Gson();

			DashBoardHOReqDto requestDto = gson.fromJson(obj,
					DashBoardHOReqDto.class);

			Pair<LocalDate, LocalDate> pairDates = getMonthDates(
					requestDto.getTaxPeriod());

			List<EinvGenerationDto1> einvGenTrends = dashboardService
					.getEinvGenTredForGenAndTotal(gstins, pairDates.getValue0(),
							pairDates.getValue1());

			JsonObject resp = new JsonObject();

			String jsonEINV = gson.toJson(einvGenTrends);
			JsonElement einvGenTrendsElement = new JsonParser().parse(jsonEINV);

			resp.add("det", einvGenTrendsElement);
			JsonObject resps = new JsonObject();
			resp.add("id", gson.toJsonTree(requestDto.getId()));
			resp.add("requestParams", obj);

			
			JsonElement respBody = gson.toJsonTree(resp);

			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);

			LOGGER.debug("einvGenTrendsElement {}", resps.toString());
			return resps.toString();
		} catch (AppException e) {
			LOGGER.error("Exception while retriving the einvGenTrendsElement ",
					e);
			throw new AppException(e);
		}
	}

	@Override
	public String getEinvErrorTrends(JsonObject obj, List<String> gstins,
			Long entityId) {

		try {
			Gson gson = new Gson();

			DashBoardHOReqDto requestDto = gson.fromJson(obj,
					DashBoardHOReqDto.class);

			Pair<LocalDate, LocalDate> pairDates = getMonthDates(
					requestDto.getTaxPeriod());

			List<EinvGenerationDto2> einvGenTrends = dashboardService
					.getEinvGenTredForCanDupAndErr(gstins,
							pairDates.getValue0(), pairDates.getValue1());

			JsonObject resp = new JsonObject();

			String jsonEINV = gson.toJson(einvGenTrends);
			JsonElement einvGenTrendsElement = new JsonParser().parse(jsonEINV);

			resp.add("det", einvGenTrendsElement);
			JsonObject resps = new JsonObject();
			resp.add("id", gson.toJsonTree(requestDto.getId()));
			resp.add("requestParams", obj);

			
			JsonElement respBody = gson.toJsonTree(resp);

			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);

			LOGGER.debug("einvGenTrendsError {}", resps.toString());
			return resps.toString();

		} catch (AppException e) {
			LOGGER.error("Exception while retriving the einvGenTrendsError ",
					e);
			throw new AppException(e);
		}
	}

	@Override
	public String getEinvSummry(JsonObject obj, List<String> gstins,
			Long entityId) {

		try {
			Gson gson = new Gson();

			DashBoardHOReqDto requestDto = gson.fromJson(obj,
					DashBoardHOReqDto.class);

			Pair<LocalDate, LocalDate> pairDates = getMonthDates(
					requestDto.getTaxPeriod());

			EinvHeaderDetailsDto headerDetails = dashboardService
					.getEinvHeaderDetails(gstins,
							pairDates.getValue0(), pairDates.getValue1());
			List<EinvHeaderDetailsDto> irnDataHdrList = new ArrayList<>();
			
			EinvIrnDto irnData = dashboardService.getAvgIrnGenPerMonth(gstins,
					pairDates.getValue0(), pairDates.getValue1());
			
			headerDetails.setIrn(irnData.getIrn());
			headerDetails.setLastUpdatedOn(irnData.getLastUpdatedOn());
			
			irnDataHdrList.add(headerDetails);
			
			JsonObject resp = new JsonObject();

			String jsonEINV = gson.toJson(irnDataHdrList);
			JsonElement einvSummary = new JsonParser().parse(jsonEINV);
			
			resp.add("det", einvSummary);
			JsonObject resps = new JsonObject();

			resp.add("id", gson.toJsonTree(requestDto.getId()));
			resp.add("requestParams", obj);

			JsonElement respBody = gson.toJsonTree(resp);

			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			
			LOGGER.debug("getEinvSummry {}", resps.toString());
			return resps.toString();
		} catch (AppException e) {
			LOGGER.error("Exception while retriving the getEinvSummry ",
					e);
			throw new AppException(e);
		}
	}

	@Override
	public List<String> getSuppGstins(Long entityId) {
		List<String> gstins = gSTNDetailRepository
				.findgstinByEntityId(entityId);

		List<String> list = dashboardDao.getSupGstinList(gstins);

		return list;
	}

	private Pair<LocalDate, LocalDate> getMonthDates(String taxPeriod) {

		LocalDate fromDate = LocalDate.of(
				Integer.parseInt(taxPeriod.substring(2, 6)),
				Integer.parseInt(taxPeriod.substring(0, 2)), 1);

		YearMonth yearMonth = YearMonth.of(
				Integer.parseInt(taxPeriod.substring(2, 6)),
				Integer.parseInt(taxPeriod.substring(0, 2)));
		int lastDay = yearMonth.lengthOfMonth();

		LocalDate toDate = LocalDate.of(
				Integer.parseInt(taxPeriod.substring(2, 6)),
				Integer.parseInt(taxPeriod.substring(0, 2)), lastDay);

		return new Pair(fromDate, toDate);
	}

}
