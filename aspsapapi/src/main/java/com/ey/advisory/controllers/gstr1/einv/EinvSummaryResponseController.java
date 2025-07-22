package com.ey.advisory.controllers.gstr1.einv;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.entities.client.EinvReconRespConfigEntity;
import com.ey.advisory.app.data.entities.client.EinvReconRespGSTINEntity;
import com.ey.advisory.app.data.entities.client.EinvReconSummRespReportEntity;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.EinvReconRespConfigRepository;
import com.ey.advisory.app.data.repositories.client.EinvReconRespGSTINRepository;
import com.ey.advisory.app.data.repositories.client.EinvReconSummRespReportRepository;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.docs.dto.FileStatusReportDto;
import com.ey.advisory.app.gstr1.einv.EinvSummaryResponseService;
import com.ey.advisory.app.services.search.filestatussearch.AsyncReportHandler;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;


/**
 * @author Saif.S
 *
 */

@RestController
@Slf4j
public class EinvSummaryResponseController {

	private static final ImmutableMap<String, Integer> immutableMap = ImmutableMap
			.<String, Integer>builder().put("addlGSTINForR1", 1)
			.put("avbinDigiGST", 3).put("addlDigiGST", 4)
			.put("notAvbinDigiGSTR1", 5).put("delfailedStatus", 6).build();

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("AsyncFileStatusReportHandlerImpl")
	AsyncReportHandler asyncFileStatusReportHandler;

	@Autowired
	@Qualifier("EinvReconRespGSTINRepository")
	private EinvReconRespGSTINRepository einvReconRespGSTINRepo;

	@Autowired
	@Qualifier("EinvReconRespConfigRepository")
	private EinvReconRespConfigRepository einvReconRespConfigRepo;

	@Autowired
	@Qualifier("EinvSummaryResponseServiceImpl")
	private EinvSummaryResponseService einvSummaryRespService;

	@Autowired
	@Qualifier("EinvReconSummRespReportRepository")
	private EinvReconSummRespReportRepository einvReconSummRespReptRepo;

	private static final String REPORT_TYPE = "Einv Consolidated";
	private static final String REPORT_CATEGORY = "EinvSummary";
	private static final String DATA_TYPE = "Outward";

	@PostMapping(value = "/ui/downloadEinvConsolidatedReport")
	public ResponseEntity<String> downloadEinvConsolidatedReport(
			@RequestBody String jsonParams) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside EinvSummaryResponseController in Async Report CSV Download EinvConsolidated Report";
			LOGGER.debug(msg);
		}

		JsonObject requestObject = (new JsonParser()).parse(jsonParams)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		String groupCode = TenantContext.getTenantId();

		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName();

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Request for Download EinvConsolidated CSV Report : %s",
					json.toString());
			LOGGER.debug(msg);
		}

		try {
			FileStatusReportDto reqDto = gson.fromJson(json,
					FileStatusReportDto.class);

			FileStatusDownloadReportEntity entity = new FileStatusDownloadReportEntity();

			if (LOGGER.isDebugEnabled()) {
				String msg = "Setting request data to entity to SaveorPersist";
				LOGGER.debug(msg);
			}
			reqDto.setType(REPORT_TYPE);
			reqDto.setReportCateg(REPORT_CATEGORY);
			reqDto.setDataType(DATA_TYPE);

			asyncFileStatusReportHandler.setDataToEntity(entity, reqDto);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Request for Download EinvConsolidated CSV Report : %s",
						json.toString());
				LOGGER.debug(msg);
			}

			entity = fileStatusDownloadReportRepo.save(entity);

			Long id = entity.getId();

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Successfully saved to DB with Report Id : %d", id);
				LOGGER.debug(msg);
			}

			JsonObject jobParams = new JsonObject();
			jobParams.addProperty("id", id);
			asyncJobsService.createJob(groupCode,
					JobConstants.EINV_CONSOLIDATED_REPORT, jobParams.toString(),
					userName, 1L, null, null);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Successfully created JOB with job params as : %s",
						jobParams.toString());
				LOGGER.debug(msg);
			}

			String reportType = "Einv Consolidated Records";

			jobParams.addProperty("reportType", reportType);
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(jobParams);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);

			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed",
					"Unexpected occured in EinvSummaryResponseController in Async Report CSV Download EinvConsolidated Report"
							+ e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Error Occured in EinvSummaryResponseController in Async Report CSV Download EinvConsolidated Report";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		}

	}

	
	@PostMapping(value = "/ui/submitEinvSummaryResp")
	public ResponseEntity<String> submitEinvSummaryReport(
			@RequestBody String jsonString, HttpServletResponse response)
			throws Exception {
		String groupCode = TenantContext.getTenantId();
		Gson gson = GsonUtil.gsonInstanceWithEWB24HRFormat();
		String userName = SecurityContext.getUser() != null
				? (SecurityContext.getUser().getUserPrincipalName() != null
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM")
				: "SYSTEM";
		JsonObject resp = null;
		Long configId = null;
		try {

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			JsonObject reqJson = requestObject.get("req").getAsJsonObject();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request Json {}", reqJson);
			}
			JsonArray selectedGstins = reqJson.get("gstins").getAsJsonArray();
			String returnPeriod = reqJson.get("returnPeriod").getAsString();
			Type listType = new TypeToken<List<String>>() {
			}.getType();

			List<String> listofGstins = gson.fromJson(selectedGstins, listType);

			if (isRequestInValid(listofGstins, returnPeriod)) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Selected GSTIN's are already in InProgress",
							listofGstins);
				}
				resp = new JsonObject();
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.addProperty("resp",
						"One or More Selected GSTIN's are already in InProgress");
				return new ResponseEntity<>(resp.toString(),
						HttpStatus.INTERNAL_SERVER_ERROR);
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request Is Valid, Saving the Data into Tables");
			}

			EinvReconRespConfigEntity einvRecRespEntity = new EinvReconRespConfigEntity();
			einvRecRespEntity.setCreatedBy(userName);
			einvRecRespEntity.setCreatedDate(LocalDateTime.now());
			einvRecRespEntity.setReconRespStatus("Initiated");
			einvReconRespConfigRepo.save(einvRecRespEntity);

			configId = einvRecRespEntity.getReconRespConfigId();

			List<EinvReconRespGSTINEntity> einvRecRespGSTINEntity = createReconRespGSTINData(
					listofGstins, configId, returnPeriod);
			einvReconRespGSTINRepo.saveAll(einvRecRespGSTINEntity);
			LOGGER.debug(
					"Data is Saved Successfully in EinvReconRespGSTINEntity");
			
			List<EinvReconSummRespReportEntity> einvRecSummReptEntity = populateUserResp(
					reqJson, configId);
			einvReconSummRespReptRepo.saveAll(einvRecSummReptEntity);
			LOGGER.debug(
					"Data is Saved Successfully in EinvReconSummRespReportEntity");

			resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.addProperty("resp", "Request has been Initiated Successfully");

			JsonObject jsonParams = new JsonObject();
			jsonParams.addProperty("configId", String.valueOf(configId));
		
			asyncJobsService.createJob(groupCode,
					JobConstants.EINVSUMMARYRESPONSE, jsonParams.toString(),
					userName, 1L, null, null);

			LOGGER.debug(
					"EINVSUMMARYRESPONSE Job is Triggered, Json Params are  {}",
					jsonParams.toString());
			
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			LOGGER.error("Exception while Einvoice Summary Resp ", ex);
			resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree("Request Failed"));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private boolean isRequestInValid(List<String> gstins, String taxPeriod) {

		int validCount = einvReconRespGSTINRepo.gstinIsAvailable(gstins,
				taxPeriod);
		LOGGER.error("Out of {} GSTINs {} are in InProgress", gstins.size(),
				validCount);

		return validCount > 0;
	}

	private List<EinvReconSummRespReportEntity> populateUserResp(
			JsonObject reqJson, Long configId) {
		List<EinvReconSummRespReportEntity> userRespEntities = new ArrayList<>();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Inside populateUserResp Method");
		}
		immutableMap.forEach((k, v) -> {
			if (reqJson.get(k) != null
					&& !reqJson.get(k).getAsString().isEmpty()) {
				userRespEntities.add(createRespEntity(v,
						reqJson.get(k).getAsString(), configId));
			}
		});

		return userRespEntities;
	}

	private EinvReconSummRespReportEntity createRespEntity(Integer reportId,
			String userResp, Long configId) {

		return new EinvReconSummRespReportEntity(configId, reportId, userResp);
	}

	private List<EinvReconRespGSTINEntity> createReconRespGSTINData(
			List<String> listofGstins, Long configId, String returnPeriod) {
		List<EinvReconRespGSTINEntity> gstinRespEntities = new ArrayList<>();

		for (int i = 0; i < listofGstins.size(); i++) {
			gstinRespEntities.add(createReconRespGSTINEntity(configId,
					listofGstins.get(i), returnPeriod));
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Inside createReconRespGSTINData Method");
		}
		return gstinRespEntities;
	}

	private EinvReconRespGSTINEntity createReconRespGSTINEntity(
			Long reconRespConfigId, String gstin, String returnPeriod) {

		return new EinvReconRespGSTINEntity(reconRespConfigId, gstin,
				returnPeriod, "Initiated", true, null);
	}

}
