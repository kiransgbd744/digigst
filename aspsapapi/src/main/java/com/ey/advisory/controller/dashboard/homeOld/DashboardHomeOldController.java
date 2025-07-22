package com.ey.advisory.controller.dashboard.homeOld;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.LandingDashboardBatchRefreshEntity;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.LandingDashboardRefreshRepository;
import com.ey.advisory.app.dashboard.homeOld.DashBoardHOReqDto;
import com.ey.advisory.app.dashboard.homeOld.DashboardHOOutwardSupplyUIDto;
import com.ey.advisory.app.dashboard.homeOld.DashboardHOReconSummary2bprDto;
import com.ey.advisory.app.dashboard.homeOld.DashboardHOReconSummaryDto;
import com.ey.advisory.app.dashboard.homeOld.DashboardHOReturnComplinceWithAuthCountDto;
import com.ey.advisory.app.dashboard.homeOld.DashboardHOReturnsStatusUIDto;
import com.ey.advisory.app.dashboard.homeOld.DashboardHOService;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author mohit.basak
 *
 */
@Slf4j
@RestController
public class DashboardHomeOldController {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	// autowiring serivce
	@Autowired
	@Qualifier("DashboardHOServiceImpl")
	private DashboardHOService dashboardHOService;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	LandingDashboardRefreshRepository ldRepo;

	@PostMapping(value = {"/ui/getDashBoardHomeOldReturnStatus"}, produces = {
					MediaType.APPLICATION_JSON_VALUE }, consumes = {
							MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getDashBoardHomeOldReturnStatusDet(
			@RequestBody String jsonReq) {

		JsonObject requestObject = JsonParser.parseString(jsonReq)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {

			DashBoardHOReqDto reqDto = gson.fromJson(json,
					DashBoardHOReqDto.class);

			Long entityId = reqDto.getEntityId();
			String taxPeriod = reqDto.getTaxPeriod();
			// getting info of GSTIN and setting for a particular entity
			if (LOGGER.isDebugEnabled()) {
				String msg = "Invoked DashboardHOServiceImpl"
						+ ".getDashBoardHomeOldReturnStatusDet"
						+ " Preparing Response Object";
				LOGGER.debug(msg);
			}

			DashboardHOReturnsStatusUIDto uiDto = dashboardHOService
					.getDashBoardReturnStatus(entityId, taxPeriod);

			JsonObject resp = new JsonObject();
			JsonObject detResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(uiDto);
			JsonElement divison = gson.toJsonTree(GenUtil.getYearDivisonItcO4(taxPeriod));
			detResp.add("det", respBody);
			detResp.add("yearDivison", divison);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", detResp);
			if (LOGGER.isDebugEnabled()) {
				String msg = "End DashboardHomeOldController"
						+ ".getDashBoardHomeOldReturnStatusDet, before returning response";
				LOGGER.debug(msg);
			}
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Data Status ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}

	// for return Compliance Status

	@PostMapping(value = { "/ui/getDashBoardHomeOldReturnComplianceStatus",
			"/itp/getDashBoardHomeOldReturnComplianceStatus" }, produces = {
					MediaType.APPLICATION_JSON_VALUE }, consumes = {
							MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getDashBoardHomeOldReturnComplianceStatusDet(
			@RequestBody String jsonReq) {

		JsonObject requestObject = JsonParser.parseString(jsonReq)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {

			DashBoardHOReqDto reqDto = gson.fromJson(json,
					DashBoardHOReqDto.class);

			Long entityId = reqDto.getEntityId();
			String taxPeriod = reqDto.getTaxPeriod();
			// getting info of GSTIN and setting for a particular entity
			if (LOGGER.isDebugEnabled()) {
				String msg = "Invoked DashboardHOServiceImpl"
						+ ".getDashBoardHomeOldReturnStatusDet"
						+ " Preparing Response Object";
				LOGGER.debug(msg);
			}

			DashboardHOReturnComplinceWithAuthCountDto dashBoardReturnComplianceStatusDtosList = dashboardHOService
					.getDashBoardReturnComplianceStatus(entityId, taxPeriod);

			JsonObject resp = new JsonObject();
			JsonObject detResp = new JsonObject();
			JsonElement respBody = gson
					.toJsonTree(dashBoardReturnComplianceStatusDtosList);
			detResp.add("det", respBody);

			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", detResp);
			if (LOGGER.isDebugEnabled()) {
				String msg = "End DashboardHomeOldController"
						+ ".getDashBoardHomeOldReturnStatusDet, before returning response";
				LOGGER.debug(msg);
			}
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Data Status ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}

	// for outward Supply getDashBoardHomeOldOutwardStatusDetails
	@PostMapping(value = { "/ui/getDashBoardHomeOldOutwardStatusDetails",
			"/itp/getDashBoardHomeOldOutwardStatusDetails" }, produces = {
					MediaType.APPLICATION_JSON_VALUE }, consumes = {
							MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getDashBoardHomeOldOutwardStatusDetailsDet(
			@RequestBody String jsonReq) {

		JsonObject requestObject = JsonParser.parseString(jsonReq)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {

			DashBoardHOReqDto reqDto = gson.fromJson(json,
					DashBoardHOReqDto.class);

			Long entityId = reqDto.getEntityId();
			String taxPeriod = reqDto.getTaxPeriod();
			// getting info of GSTIN and setting for a particular entity
			if (LOGGER.isDebugEnabled()) {
				String msg = "Invoked DashboardHOServiceImpl"
						+ ".getDashBoardHomeOldOutwardStatusDetailsDet"
						+ " Preparing Response Object";
				LOGGER.debug(msg);
			}

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
							? latestEntity.getStatus().toString() : null
					: "COMPLETED");

			JsonObject resp = new JsonObject();
			JsonObject detResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dashBoardOutwardSupplyDtos);
			detResp.add("det", respBody);

			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", detResp);

			resp.add("lastRefreshedOn", gson.toJsonTree(refreshTime));
			resp.add("latestStatus", gson.toJsonTree(latestStatus));

			if (LOGGER.isDebugEnabled()) {
				String msg = "End DashboardHomeOldController"
						+ ".getDashBoardHomeOldOutwardStatusDetailsDet,"
						+ "  before returning response";
				LOGGER.debug(msg);
			}
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Data Status ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}

	// for Recon Summary
	@PostMapping(value = { "/ui/getDashBoardHomeOldReconSummary",
			"/itp/getDashBoardHomeOldReconSummary" }, produces = {
					MediaType.APPLICATION_JSON_VALUE }, consumes = {
							MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getDashBoardHomeOldReconSummaryDet(
			@RequestBody String jsonReq) {

		JsonObject requestObject = JsonParser.parseString(jsonReq)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {

			DashBoardHOReqDto reqDto = gson.fromJson(json,
					DashBoardHOReqDto.class);

			Long entityId = reqDto.getEntityId();
			String taxPeriod = reqDto.getTaxPeriod();
			// getting info of GSTIN and setting for a particular entity
			if (LOGGER.isDebugEnabled()) {
				String msg = "Invoked DashboardHOServiceImpl"
						+ ".getDashBoardHomeOldReconSummaryDet"
						+ " Preparing Response Object";
				LOGGER.debug(msg);
			}

			DashboardHOReconSummaryDto uiDto = dashboardHOService
					.getDashBoardReconSummary(entityId, taxPeriod);
			// System.out.println(dashBoardReturnStatusDtosList);
			JsonObject resp = new JsonObject();
			JsonObject detResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(uiDto);
			detResp.add("det", respBody);

			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", detResp);
			if (LOGGER.isDebugEnabled()) {
				String msg = "End DashboardHomeOldController"
						+ ".getDashBoardHomeOldReconSummaryDet, before returning response";
				LOGGER.debug(msg);
			}
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Data Status ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}

	@PostMapping(value = "/ui/getDashBoardHomeReconSummary2bpr", produces = {
			MediaType.APPLICATION_JSON_VALUE }, consumes = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getDashBoardHomeReconSummary2bpr(
			@RequestBody String jsonReq) {

		JsonObject requestObject = JsonParser.parseString(jsonReq)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {

			DashBoardHOReqDto reqDto = gson.fromJson(json,
					DashBoardHOReqDto.class);

			Long entityId = reqDto.getEntityId();
			String taxPeriod = reqDto.getTaxPeriod();
			// getting info of GSTIN and setting for a particular entity
			if (LOGGER.isDebugEnabled()) {
				String msg = "Invoked DashboardHOServiceImpl"
						+ ".getDashBoardHomeReconSummary2bpr"
						+ " Preparing Response Object";
				LOGGER.debug(msg);
			}

			DashboardHOReconSummary2bprDto uiDto = dashboardHOService
					.getDashBoardReconSummary2bpr(entityId, taxPeriod);
			// System.out.println(dashBoardReturnStatusDtosList);
			JsonObject resp = new JsonObject();
			JsonObject detResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(uiDto);
			detResp.add("det", respBody);

			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", detResp);
			if (LOGGER.isDebugEnabled()) {
				String msg = "End DashboardHomeOldController"
						+ ".getDashBoardHomeReconSummary2bpr, before returning response";
				LOGGER.debug(msg);
			}
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Data Status ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}

	@PostMapping(value = "/ui/landingDashboardRefreshButton", produces = {
			MediaType.APPLICATION_JSON_VALUE }, consumes = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> landingDashboardRefreshButton(
			@RequestBody String jsonReq) {

		JsonObject requestObject = JsonParser.parseString(jsonReq)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {

			User user = SecurityContext.getUser();
			String userName = user.getUserPrincipalName();

			DashBoardHOReqDto reqDto = gson.fromJson(json,
					DashBoardHOReqDto.class);

			Long entityId = reqDto.getEntityId();
			String taxPeriod = reqDto.getTaxPeriod();
			// getting info of GSTIN and setting for a particular entity
			if (LOGGER.isDebugEnabled()) {
				String msg = "Invoked landingDashboardRefreshButton"
						+ ".landingDashboardRefreshButton"
						+ " Preparing Response Object";
				LOGGER.debug(msg);
			}

			String derivedTaxPeriod = taxPeriod.substring(2)
					.concat(taxPeriod.substring(0, 2));

			// Long batchId = generateCustomId(entityManager);
			LandingDashboardBatchRefreshEntity entity = new LandingDashboardBatchRefreshEntity();
			// entity.setBatchId(batchId);
			entity.setDerRetPeriod(derivedTaxPeriod);
			entity.setEntityId(entityId);
			entity.setCreatedOn(LocalDateTime.now());
			entity.setCreatedBy(userName);
			entity.setStatus("INITIATED");
			// doubt
			entity.setIsdelete(true);
			entity = ldRepo.save(entity);
			Long batchId = entity.getBatchId();
			JsonObject jsonParams = new JsonObject();
			jsonParams.addProperty("batchId", batchId);
			jsonParams.addProperty("entityId", entityId);
			jsonParams.addProperty("derivedTaxPeriod", derivedTaxPeriod);

			asyncJobsService.createJob(TenantContext.getTenantId(),
					JobConstants.Landing_Dashboard, jsonParams.toString(),
					"SYSTEM", 50L, null, null);

			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp",
					gson.toJsonTree("Refresh initiated successfully."));

			if (LOGGER.isDebugEnabled()) {
				String msg = "End landingDashboardRefreshButton"
						+ "  before returning response";
				LOGGER.debug(msg);
			}
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Data Status ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}

	private static Long getNextSequencevalue(EntityManager entityManager) {

		String queryStr = "SELECT LD_BATCH_REFRESH_SEQ.nextval " + "FROM DUMMY";

		Query query = entityManager.createNativeQuery(queryStr);

		Long seqId = ((Long) query.getSingleResult());

		return seqId;
	}

	private static Long generateCustomId(EntityManager entityManager) {
		String reportId = "";
		String digits = "";
		Long nextSequencevalue = getNextSequencevalue(entityManager);

		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
		String currentDate = currentYear + (currentMonth < 10
				? ("0" + currentMonth) : String.valueOf(currentMonth));
		if (nextSequencevalue != null && nextSequencevalue > 0) {
			digits = String.format("%06d", nextSequencevalue);
			reportId = currentDate.concat(digits);
		}

		return Long.valueOf(reportId);
	}

}
