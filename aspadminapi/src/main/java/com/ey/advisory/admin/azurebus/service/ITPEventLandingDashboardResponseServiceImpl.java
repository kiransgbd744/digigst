package com.ey.advisory.admin.azurebus.service;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.LandingDashboardBatchRefreshEntity;
import com.ey.advisory.admin.data.repositories.client.LandingDashboardRefreshRepository;
import com.ey.advisory.app.dashboard.homeOld.DashboardHOOutwardSupplyUIDto;
import com.ey.advisory.app.dashboard.homeOld.DashboardHOReconSummary2bprDto;
import com.ey.advisory.app.dashboard.homeOld.DashboardHOReconSummaryDto;
import com.ey.advisory.app.dashboard.homeOld.DashboardHOReturnsStatusUIDto;
import com.ey.advisory.app.dashboard.homeOld.DashboardHOService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi.jain
 *
 */
@Slf4j
@Component("ITPEventLandingDashboardResponseServiceImpl")
public class ITPEventLandingDashboardResponseServiceImpl
		implements ITPEventLandingDashboardResponseService {

	private static final List<String> ERROR_CODES = ImmutableList.of("DIGI102",
			"DIGI103", "GEN501");

	ImmutableMap<String, String> immutableMap = ImmutableMap.of("DIGI102",
			"No Record found for the provided Inputs", "DIGI103",
			"Invalid API Request Parameters", "GEN501",
			"Missing Mandatory Params");

	@Autowired
	@Qualifier("DashboardHOServiceImpl")
	private DashboardHOService dashboardHOService;

	@Autowired
	LandingDashboardRefreshRepository ldRepo;

	@Override
	public String getReturnStatus(Long entityId, String taxPeriod,Integer id, JsonObject obj) {

		try {
			DashboardHOReturnsStatusUIDto dto = dashboardHOService
					.getDashBoardReturnStatus(entityId, taxPeriod);

			Gson gson = new Gson();

			int finYear = Integer.parseInt(taxPeriod.substring(2, 6));
			int month = Integer.parseInt(taxPeriod.substring(0, 2));
			String yearDivison = "";
			if (finYear < 2021) {
				if (month <= 6 && month >= 4) {
					yearDivison = "Q1";
				} else if (month <= 9 && month >= 7) {
					yearDivison = "Q2";
				} else if (month <= 12 && month >= 10) {
					yearDivison = "Q3";
				} else if (month <= 3 && month >= 1) {
					yearDivison = "Q4";
				}
			} else if (finYear == 2021) {
				if (month <= 6 && month >= 4) {
					yearDivison = "Q1";
				} else if (month <= 9 && month >= 7) {
					yearDivison = "Q2";
				} else if (month <= 3 && month >= 1) {
					yearDivison = "Q4";
				} else {
					yearDivison = "H2";
				}
			} else {
				if (month <= 9 && month >= 4) {
					yearDivison = "H1";
				} else {
					yearDivison = "H2";
				}
			}

			JsonObject resp = new JsonObject();
			JsonObject detResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			JsonElement divison = gson.toJsonTree(yearDivison);
			detResp.add("det", respBody);
			detResp.add("yearDivison", divison);
			detResp.add("id", gson.toJsonTree(id));
			detResp.add("requestParams", obj);

			
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", detResp);

			LOGGER.debug("getReturnStatus {}", resp.toString());

			return resp.toString();
		} catch (AppException e) {
			LOGGER.error("Exception while retriving the getReturnStatus ", e);
			throw new AppException(e);
		}
	}

	@Override
	public String getOutwardSupply(Long entityId, String taxPeriod,Integer id, JsonObject obj) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			DashboardHOOutwardSupplyUIDto dashBoardOutwardSupplyDtos = dashboardHOService
					.getDashBoardOutwardStatus(entityId, taxPeriod);

			String derivedTaxPeriod = taxPeriod.substring(2)
					.concat(taxPeriod.substring(0, 2));

			LandingDashboardBatchRefreshEntity compEntity = ldRepo
					.getCompletedLatestBatchId(derivedTaxPeriod, entityId);
			// System.out.println(dashBoardReturnStatusDtosList);
			LandingDashboardBatchRefreshEntity latestEntity = ldRepo
					.getLatestBatchId(derivedTaxPeriod, entityId);
			DateTimeFormatter formatter = DateTimeFormatter
					.ofPattern("dd-MM-yyyy : HH:mm:ss");

			String refreshTime = (compEntity != null
					? compEntity.getRefreshTime() != null
							? formatter
									.format(EYDateUtil.toISTDateTimeFromUTC(
											compEntity.getRefreshTime()))
									.toString()
							: null
					: null);
			String latestStatus = (latestEntity != null
					? latestEntity.getStatus() != null
							? latestEntity.getStatus().toString() : "COMPLETED"
					: "COMPLETED");

			JsonObject resp = new JsonObject();
			JsonObject detResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dashBoardOutwardSupplyDtos);
			detResp.add("det", respBody);
			detResp.add("id", gson.toJsonTree(id));
			detResp.add("requestParams", obj);

			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", detResp);

			resp.add("lastRefreshedOn", gson.toJsonTree(refreshTime));
			resp.add("latestStatus", gson.toJsonTree(latestStatus));

			LOGGER.debug("OutwardSupply response {} ", resp.toString());

			return resp.toString();
		} catch (AppException e) {
			LOGGER.error("Exception while retriving the getReturnStatus ", e);
			throw new AppException(e);
		}
	}

	@Override
	public String getReconSumary2A(Long entityId, String taxPeriod,Integer id, JsonObject obj) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			DashboardHOReconSummaryDto uiDto = dashboardHOService
					.getDashBoardReconSummary(entityId, taxPeriod);
			// System.out.println(dashBoardReturnStatusDtosList);
			JsonObject resp = new JsonObject();
			JsonObject detResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(uiDto);
			detResp.add("det", respBody);
			detResp.add("id", gson.toJsonTree(id));
			detResp.add("requestParams", obj);
			
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", detResp);
			
			LOGGER.debug("getReconSumary2APR response {} ", resp.toString());

			return resp.toString();
		} catch (AppException e) {
			LOGGER.error("Exception while retriving the getReconSumary2APR ",
					e);
			throw new AppException(e);
		}
	}

	@Override
	public String getReconSumary2BPR(Long entityId, String taxPeriod,Integer id, JsonObject obj) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			DashboardHOReconSummary2bprDto uiDto = dashboardHOService
					.getDashBoardReconSummary2bpr(entityId, taxPeriod);

			JsonObject resp = new JsonObject();
			JsonObject detResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(uiDto);
			detResp.add("det", respBody);
			detResp.add("id", gson.toJsonTree(id));
			detResp.add("requestParams", obj);
			
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", detResp);
			

			LOGGER.debug("getReconSumary2BPR response {} ", resp.toString());

			return resp.toString();
		} catch (AppException e) {
			LOGGER.error("Exception while retriving the getReconSumary2BPR ",
					e);
			throw new AppException(e);
		}
	}

}
