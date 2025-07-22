package com.ey.advisory.controllers.vendorcommunication;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.javatuples.Quartet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.entities.client.ClientFilingStatusEntity;
import com.ey.advisory.app.data.repositories.client.ClientFilingStatusRepositoty;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.data.returns.compliance.service.CompienceHistoryHelperService;
import com.ey.advisory.app.data.returns.compliance.service.ComplienceRespDto;
import com.ey.advisory.app.data.returns.compliance.service.ComplienceSummeryRespDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.BasicGstr6SecCommonParam;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.ProcessedRecordsCommonSecParam;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr1ProcessedRecordsReqDto;
import com.ey.advisory.core.dto.Gstr2aProcessedDataRecordsReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Siva.Nandam
 *
 */
@RestController
@Slf4j
public class ClientMasterController {

	private static final String FINANCIAL_YEAR = "financialYear";
	private static final String FAILED = "Failed";
	private static final String SUBMITTED = "SUBMITTED";
	private static final String IN_PROGRESS = "InProgress";
	// private static final String COMPLETED = "Completed";
	private static final DateTimeFormatter format = DateTimeFormatter
			.ofPattern("dd-MM-yyyy HH:mm:ss");
	@Autowired
	@Qualifier("ClientFilingStatusRepositoty")
	private ClientFilingStatusRepositoty returnDataStorageStatusRepo;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinInfoRepository;

	@Autowired
	@Qualifier("entityInfoRepository")
	private EntityInfoRepository entityInfoRepository;

	@Autowired
	@Qualifier("CompienceHistoryHelperService")
	private CompienceHistoryHelperService complienceSummery;

	@Autowired
	@Qualifier("ProcessedRecordsCommonSecParam")
	private ProcessedRecordsCommonSecParam processedRecordsCommonSecParam;

	@Autowired
	@Qualifier("BasicGstr6SecCommonParam")
	BasicGstr6SecCommonParam basicGstr6SecCommonParam;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@PostMapping("/ui/complienceHistory")
	public ResponseEntity<String> getGstr2AProcessedRecords(
			@RequestBody String jsonString) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("gstr1,gstr7,gstr6,gstr3b complienceHistory request{}",
					json);
		}

		try {

			Gstr2aProcessedDataRecordsReqDto gstr2AProcessedDataRecordsReqDto = gson
					.fromJson(json, Gstr2aProcessedDataRecordsReqDto.class);
			List<ComplienceSummeryRespDto> respDtos = complienceSummery
					.findcomplienceSummeryRecords(
							gstr2AProcessedDataRecordsReqDto);
			JsonObject resp = new JsonObject();
			if ((respDtos != null) && (!respDtos.isEmpty())) {
				String initiatestatus = respDtos.get(0).getInitiatestatus();
				String initiateTime = respDtos.get(0).getInitiateTime();
				ComplienceRespDto respd = new ComplienceRespDto();

				respd.setInitiateTime(initiateTime);

				respd.setInitiatestatus(initiatestatus);

				respd.setComplienceSummeryRespDto(respDtos);

				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", gson.toJsonTree(respd));
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Response data for given criteria for processed data records is{}",
							resp);
				}
			} else {
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				String msg = String.format("No GSTIN is Onboarded for this Return Type");
				resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
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
			String msg = "Unexpected error while retrieving Compliance processed data records";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@PostMapping(value = "/ui/initateGeClientFilingStatus", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getVendorsReturnFileStatus(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonObject request = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");
			Gstr2aProcessedDataRecordsReqDto reqDto = gson.fromJson(request,
					Gstr2aProcessedDataRecordsReqDto.class);

			String financialYear = reqDto.getFinancialYear();
			String returnType = reqDto.getReturnType();
			Map<String, List<String>> dataSecAttrs = reqDto.getDataSecAttrs();
			List<String> gstinList = reqDto.getDataSecAttrs()
					.get(OnboardingConstant.GSTIN);
			gstinList = getGstins(gstinList, Long.parseLong(reqDto.getEntity()),
					reqDto.getReturnType(), dataSecAttrs);

			if (LOGGER.isDebugEnabled())
				LOGGER.debug("insithe the method getClientssReturnFileStatus "
						+ "of controller ClientMasterController ");
			String userName = SecurityContext.getUser().getUserPrincipalName();

			Quartet<List<Long>, List<String>, String, LocalDateTime> checkstatus = checkstatus(
					gstinList, financialYear, returnType, userName);
			JsonObject resps = new JsonObject();
			if (!checkstatus.getValue1().isEmpty()) {

				JsonObject jsonParams = new JsonObject();
				jsonParams.add("requestIds",
						gson.toJsonTree(checkstatus.getValue0()));
				jsonParams.addProperty(FINANCIAL_YEAR, financialYear);
				jsonParams.add("gstins",
						gson.toJsonTree(checkstatus.getValue1()));

				asyncJobsService.createJob(TenantContext.getTenantId(),
						JobConstants.InitiateGetclientFilingStatus,
						jsonParams.toString(), userName, 5L, 0L, 0L);

				resps.addProperty("status", "Initiate Call Successfully done");
			} else {
				resps.addProperty("status",
						"Get call is in progress for the selected GSTINs");
			}

			JsonElement respBody = gson.toJsonTree(resps);
			resps = new JsonObject();
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
		} catch (Exception e) {
			APIRespDto dto = new APIRespDto(FAILED, e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			resp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", e.getMessage())));
			resp.add("resp", respBody);
			LOGGER.error("exception while intiating client filing status: ", e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	private List<String> getGstins(List<String> gstinList, Long entityId,
			String returnType, Map<String, List<String>> dataSecAttrs) {
		if (!gstinList.isEmpty())
			return gstinList;
		if ("GSTR6".equalsIgnoreCase(returnType)) {
			Gstr1ProcessedRecordsReqDto dto = new Gstr1ProcessedRecordsReqDto();
			dto.setDataSecAttrs(dataSecAttrs);
			dto.setEntityId(Arrays.asList(entityId));
			Gstr1ProcessedRecordsReqDto reqDto = processedRecordsCommonSecParam
					.setGstr6DataSecuritySearchParams(dto);
			return reqDto.getDataSecAttrs().get(OnboardingConstant.GSTIN);
		}
		if ("GSTR7".equalsIgnoreCase(returnType)) {
			Gstr1ProcessedRecordsReqDto dto = new Gstr1ProcessedRecordsReqDto();
			dto.setDataSecAttrs(dataSecAttrs);
			dto.setEntityId(Arrays.asList(entityId));
			Gstr1ProcessedRecordsReqDto reqDto = processedRecordsCommonSecParam
					.setGstr7DataSecuritySearchParams(dto);
			return reqDto.getDataSecAttrs().get(OnboardingConstant.GSTIN);
		}
		// gstr1,gstr3b,gstr9,itc04
		Gstr1ProcessedRecordsReqDto dto = new Gstr1ProcessedRecordsReqDto();
		dto.setDataSecAttrs(dataSecAttrs);
		dto.setEntityId(Arrays.asList(entityId));
		Gstr1ProcessedRecordsReqDto reqDto = processedRecordsCommonSecParam
				.setGstr1DataSecuritySearchParams(dto);

		return reqDto.getDataSecAttrs().get(OnboardingConstant.GSTIN);

	}

	private Quartet<List<Long>, List<String>, String, LocalDateTime> checkstatus(
			List<String> gstinList, String financialYear, String returnType,
			String userName) {
		String initiationStatus = SUBMITTED;
		LocalDateTime initiationTime = null;
		List<String> reqGstins = new ArrayList<>();
		List<Long> ids = new ArrayList<>();
		for (String gstin : gstinList) {
			ClientFilingStatusEntity returnDataSttusEntity = returnDataStorageStatusRepo
					.findByFinancialYearAndGstinAndReturnType(financialYear,
							gstin, returnType);
			if (returnDataSttusEntity == null) {
				returnDataSttusEntity = new ClientFilingStatusEntity(returnType,
						gstin, financialYear, SUBMITTED, userName, userName);
				returnDataStorageStatusRepo.save(returnDataSttusEntity);
				ids.add(returnDataSttusEntity.getId());
				reqGstins.add(returnDataSttusEntity.getGstin());

			} else if (returnDataSttusEntity.getStatus()
					.equalsIgnoreCase(SUBMITTED)
					|| returnDataSttusEntity.getStatus()
							.equalsIgnoreCase(IN_PROGRESS)
			/*
			 * || returnDataSttusEntity.getStatus() .equalsIgnoreCase(COMPLETED)
			 */) {
				// errorList.add(gstin);
			} else {
				initiationStatus = returnDataSttusEntity.getStatus();
				initiationTime = returnDataSttusEntity.getModifiedOn();
				ids.add(returnDataSttusEntity.getId());
				reqGstins.add(returnDataSttusEntity.getGstin());
			}
		}
		return new Quartet<>(ids, reqGstins, initiationStatus, initiationTime);
	}
}