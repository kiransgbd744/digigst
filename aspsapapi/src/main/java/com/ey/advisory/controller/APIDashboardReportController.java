package com.ey.advisory.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.GstnGetStatusEntity;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.entities.client.ZipGenStatusEntity;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.dashboard.apiCall.APICAllDtls;
import com.ey.advisory.app.dashboard.apiCall.APIFyCallDtls;
import com.ey.advisory.app.dashboard.apiCall.ApiCallDashboardReqDto;
import com.ey.advisory.app.dashboard.apiCall.ApiFyGstinDetailsDto;
import com.ey.advisory.app.dashboard.apiCall.ApiGstinDetailsDto;
import com.ey.advisory.app.dashboard.apiCall.DownloadGetCallDto;
import com.ey.advisory.app.dashboard.apiCall.GetFyCallDto;
import com.ey.advisory.app.dashboard.apiCall.GstinApiCallService;
import com.ey.advisory.app.dashboard.apiCall.TaxPeriodDetailsDto;
import com.ey.advisory.app.dashboard.mergefiles.APICallDashboardFileNameCreator;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.data.repositories.client.GstinApiCallRepository;
import com.ey.advisory.app.data.repositories.client.GstinGetStatusRepository;
import com.ey.advisory.app.data.repositories.client.ZipGenStatusRepository;
import com.ey.advisory.app.data.services.Gstr1A.Gstr1AGetCallHandler;
import com.ey.advisory.app.data.services.gstr8A.Gstr8AIntiateGetCallServiceImpl;
import com.ey.advisory.app.data.services.gstr9.Gstr9InitiateGetDataUtil;
import com.ey.advisory.app.docs.dto.anx2.Anx2GetInvoicesReqDto;
import com.ey.advisory.app.gstr3b.Gstr3bUpdateGstnService;
import com.ey.advisory.app.services.gstr1.Gstr1GetCallHandler;
import com.ey.advisory.app.services.gstr8.Gstr8ApiGetCallHandler;
import com.ey.advisory.app.services.jobs.anx2.Gstr7SummaryAtGstnImpl;
import com.ey.advisory.app.services.jobs.gstr2a.Gstr2aGetCallHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DashboardCommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.common.zip.DirAndFilesCompressor;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.GetCallDto;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.core.dto.Gstr6GetInvoicesReqDto;
import com.ey.advisory.core.dto.Gstr7GetInvoicesReqDto;
import com.ey.advisory.core.dto.InitiateGetCallDto;
import com.ey.advisory.core.dto.Itc04GetInvoicesReqDto;
import com.ey.advisory.gstnapi.PublicApiConstants;
import com.ey.advisory.gstnapi.PublicApiContext;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.ey.advisory.gstr2.userdetails.EntityService;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Rajesh N K
 *
 */
@Slf4j
@RestController
public class APIDashboardReportController {

	@Autowired
	@Qualifier("EntityServiceImpl")
	private EntityService entityService;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	GSTNDetailRepository gSTNDetailRepository;

	@Autowired
	@Qualifier("GstinApiCallServiceImpl")
	private GstinApiCallService gstinApiCallService;

	@Autowired
	@Qualifier("ZipGenStatusRepository")
	ZipGenStatusRepository zipGenStatusRepository;

	@Autowired
	Gstr1GetApiJobInsertionController gstr1GetApi;

	@Autowired
	Gstr2aGetApiJobInsertionController gstr2aGetApi;

	@Autowired
	@Qualifier("Gstr3bUpdateGstnServiceImpl")
	Gstr3bUpdateGstnService gstr3bGetApi;

	@Autowired
	@Qualifier("DirAndFilesCompressorImpl")
	private DirAndFilesCompressor compressor;

	@Autowired
	@Qualifier("GstinGetStatusRepository")
	GstinGetStatusRepository gstinGetStatusRepo;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	GSTNAuthTokenService authTokenService;

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	Gstr8AIntiateGetCallServiceImpl gstr8AIntiateGetCallServiceImpl;

	@Autowired
	@Qualifier("Gstr9InitiateGetDataUtil")
	Gstr9InitiateGetDataUtil gstr9InitiateGetDataUtil;

	@Autowired
	@Qualifier("GstinApiCallRepository")
	GstinApiCallRepository gstinApiCallRepository;

	@Autowired
	Gstr2XGetApiJobInsertionController gstr2XGetApi;

	@Autowired
	Itc04GetApiJobInsertionController itc04GetApi;

	@Autowired
	Gstr7GetApiJobInsertionController gstr7GetApi;

	@Autowired
	private Gstr6GetApiJobInsertionController gstr6GetApi;

	@Autowired
	Gstr7SummaryAtGstnImpl gstr7SummaryGetApi;

	@Autowired
	APICallDashboardFileNameCreator fileNameCreator;

	@Autowired
	Gstr1GetCallHandler gstr1GetCallHandler;

	@Autowired
	Gstr8ApiGetCallHandler gstr8ApiGetCallJobHandler;
	
	@Autowired
	Gstr2aGetCallHandler gstr2aGetCallHandler;
	
	@Autowired
	Gstr1AGetCallHandler gstr1AGetCallHandler;
	
/*	@Autowired
	DownloadGetCallReportsImplTest downloadGetCallReportsImplTest;*/
	
	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@PostMapping(value = "/ui/getApiDashboardStatus", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGSTINsandStatus(
			@RequestBody String jsonString) {

		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Entered API Call Dashboard with input %s", jsonString);
				LOGGER.debug(msg);
			}

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
			String reqJson = requestObject.get("req").getAsJsonObject()
					.toString();
			ApiCallDashboardReqDto reqDashBoardDto = gson.fromJson(reqJson,
					ApiCallDashboardReqDto.class);

			if (reqDashBoardDto.getEntityId() == null
					|| reqDashBoardDto.getFy() == null
					|| reqDashBoardDto.getReturnType() == null) {

				String msg = "Entity Id, Financial year,"
						+ " Return type should be mandatory";
				JsonObject resp = new JsonObject();
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", gson.toJsonTree(msg));
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			}

			Long entityId = reqDashBoardDto.getEntityId();

			String financialPeriod = reqDashBoardDto.getFy();

			String returnType = reqDashBoardDto.getReturnType();

			List<Object[]> gstnObject = gSTNDetailRepository
					.getGstinBasedOnRegTypeforACD(entityId,
							GenUtil.getRegTypesBasedOnTypeForACD(returnType));

			if (gstnObject == null || gstnObject.isEmpty()) {
				String msg = String.format(
						"No Active Gstn's For Selected Entity Id %s and Return Type %s",
						entityId, returnType);
				LOGGER.error(msg);
				throw new AppException(msg);
			}

			Map<String, String> gstnRegMap = gstnObject.stream()
					.collect(Collectors.toMap(obj -> (String) obj[0],
							obj -> (String) obj[1]));

			List<String> gstnsList = gstnRegMap.keySet().stream()
					.collect(Collectors.toList());

			Map<String, String> stateNames = entityService
					.getStateNames(gstnsList);

			Collections.sort(gstnsList);

			if (LOGGER.isDebugEnabled()) {
				String msg = "EntityServiceImpl"
						+ ".getGSTINsForEntity Preparing Response Object";
				LOGGER.debug(msg);
			}
			String jobStatus = "ZIP_COMPLETED";
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

			List<Object[]> listZipstatus = zipGenStatusRepository
					.getGstinZipGenStatus(gstnsList, jobStatus,
							derivedStartPeriod, derivedEndPeriod, returnType);

			List<GstnGetStatusEntity> getBatchEntityDetails = gstinApiCallService
					.fetchGetStatus(gstnsList, derivedStartPeriod,
							derivedEndPeriod, returnType);

			List<String> taxPeriods;
			if ("ITC04".equalsIgnoreCase(returnType)) {
				taxPeriods = GenUtil.extractTaxPeriodsFromFY(financialPeriod,
						returnType);
			} else {
				taxPeriods = GenUtil.extractTaxPeriodsFromFY(financialPeriod,
						"");
			}
			List<ApiGstinDetailsDto> sGstinTaxperiodDetails = null;

			if (!getBatchEntityDetails.isEmpty()) {
				sGstinTaxperiodDetails = gstinApiCallService
						.getTaxPeriodDetails(getBatchEntityDetails,
								listZipstatus, returnType);

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
				o.setStateName(stateNames.get(o.getGstin()));
			});

			Collections.sort(sGstinTaxperiodDetails,
					Comparator.comparing(ApiGstinDetailsDto::getGstin));

			APICAllDtls apiDtls = new APICAllDtls();
			apiDtls.setApiGstinDetails(sGstinTaxperiodDetails);

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
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String msg = " Unexpected error occured";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/ui/initiateGstr8AGetCall", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> initiateGstr8AGetCall(
			@RequestBody String jsonString) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			JsonObject reqObj = obj.get("req").getAsJsonObject();
			JsonArray respBody = new JsonArray();
			GetFyCallDto dto = gson.fromJson(reqObj, GetFyCallDto.class);
			String fy = dto.getFy();
			List<String> gstinList = dto.getGstins();
			String fyOld = dto.getFy();
			String formattedFy = GenUtil.getFormattedFy(fyOld);
			String[] fyArr = fy.split("-");
			String taxPeriod = "0320" + fyArr[1];

			gstinList.forEach(o -> {
				JsonObject json = new JsonObject();
				json.addProperty("gstin", o);
				String authStatus = authTokenService
						.getAuthTokenStatusForGstin(o);
				if ("A".equalsIgnoreCase(authStatus)) {
					json.addProperty("msg",
							"Get Table-8A Initiated Successfully for "
									+ formattedFy);
					gstr8AIntiateGetCallServiceImpl.getGstnCall(o, fy,
							taxPeriod);
					respBody.add(json);
				} else {
					json.addProperty("msg",
							"Auth Token is Inactive, Please Activate");
					respBody.add(json);
				}
			});
			/*
			 * gstinList.forEach(o -> { JsonObject json = new JsonObject();
			 * json.addProperty("gstin", o);
			 * 
			 * json.addProperty("msg", "Get GSTR8A Initiated Successfully for "
			 * + fy); gstr8AIntiateGetCallServiceImpl.getGstnCall(o, fy,
			 * taxPeriod); respBody.add(json);
			 * 
			 * });
			 */
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(respBody));
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
		} catch (Exception ex) {
			String msg = "Unexpected error while making api get call ";
			LOGGER.error(msg, ex);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/ui/getACDStatusOnFy", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstr9ApiDashboardStatus(
			@RequestBody String jsonString) {
		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Entered API Call Dashboard with input %s", jsonString);
				LOGGER.debug(msg);
			}

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
			String reqJson = requestObject.get("req").getAsJsonObject()
					.toString();
			ApiCallDashboardReqDto reqDashBoardDto = gson.fromJson(reqJson,
					ApiCallDashboardReqDto.class);

			if (reqDashBoardDto.getEntityId() == null
					|| reqDashBoardDto.getFy() == null
					|| reqDashBoardDto.getReturnType() == null) {

				String msg = "Entity Id, Financial year,"
						+ " Return type should be mandatory";
				JsonObject resp = new JsonObject();
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", gson.toJsonTree(msg));
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			}

			Long entityId = reqDashBoardDto.getEntityId();

			String financialPeriod = reqDashBoardDto.getFy();

			String returnType = reqDashBoardDto.getReturnType();

			String formattedFy = GenUtil.getFormattedFy(financialPeriod);
			String taxPeriod = GenUtil.getFinancialPeriodFromFY(formattedFy);

			List<Object[]> gstnObject = gSTNDetailRepository
					.getGstinBasedOnRegTypeforACD(entityId,
							GenUtil.getRegTypesBasedOnTypeForACD(returnType));

			if (gstnObject == null || gstnObject.isEmpty()) {
				String msg = String.format(
						"No Active Gstn's For Selected Entity Id %s and Return Type %s",
						entityId, returnType);
				LOGGER.error(msg);
				throw new AppException(msg);
			}

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

			List<GstnGetStatusEntity> getBatchEntityDetails = gstinApiCallRepository
					.getDataStatusbyTaxPeriod(gstnsList, taxPeriod, returnType);

			List<ApiFyGstinDetailsDto> sGstinTaxperiodDetails = null;

			if (!getBatchEntityDetails.isEmpty()) {

				sGstinTaxperiodDetails = gstinApiCallService
						.getFyPeriodDetails(getBatchEntityDetails, gstnRegMap);

				List<String> gstinsWithGetCall = sGstinTaxperiodDetails.stream()
						.map(o -> o.getGstin()).collect(Collectors.toList());

				gstnsList.removeAll(gstinsWithGetCall);

				List<ApiFyGstinDetailsDto> notInitDto = populateFyDefaultValues(
						gstnsList, gstnRegMap);

				sGstinTaxperiodDetails.addAll(notInitDto);

			} else {
				sGstinTaxperiodDetails = populateFyDefaultValues(gstnsList,
						gstnRegMap);
			}

			sGstinTaxperiodDetails.forEach(o -> o.setAuthStatus(
					authTokenService.getAuthTokenStatusForGstin(o.getGstin())));
			Collections.sort(sGstinTaxperiodDetails,
					Comparator.comparing(ApiFyGstinDetailsDto::getGstin));

			APIFyCallDtls apiDtls = new APIFyCallDtls();
			apiDtls.setApiGstinDetails(sGstinTaxperiodDetails);
			apiDtls.setFy(financialPeriod);

			JsonObject resp = new JsonObject();
			JsonObject gstinResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(apiDtls);
			gstinResp.add("gstins", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			String msg = " Unexpected error occured";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
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

	private List<ApiFyGstinDetailsDto> populateFyDefaultValues(
			List<String> gstnsList, Map<String, String> gstnRegMap) {

		List<ApiFyGstinDetailsDto> list = new ArrayList<>();
		gstnsList.stream().forEach(o -> {
			ApiFyGstinDetailsDto dto = new ApiFyGstinDetailsDto();
			dto.setGstin(o);
			dto.setApiStatus("NOT_INITIATED");
			dto.setRegistrationType(gstnRegMap.get(o));
			list.add(dto);
		});
		return list;
	}

	@GetMapping(value = "/ui/downloadMonthlyReport")
	public void downloadMonthlyReport(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String gstin = request.getParameter("gstin");
		String taxPeriod = request.getParameter("taxPeriod");
		String returnType = request.getParameter("returnType");
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Inside Download Monthly zip Controller,"
							+ " for GSTIN - %s, taxperiod - %s, returnType - %s",
					gstin, taxPeriod, returnType);
			LOGGER.debug(msg);
		}
		ZipGenStatusEntity zipGenStatusEntity = zipGenStatusRepository
				.findByGstinAndTaxPeriodAndReturnType(gstin, taxPeriod,
						returnType);

		String fileName = zipGenStatusEntity.getZipFilePath();
		String folderName = "Anx1FileStatusReport";

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Downloading Document with fileName : %s and Folder Name: %s",
					fileName, folderName);
			LOGGER.debug(msg);
		}

		Document document = DocumentUtility.downloadDocument(fileName,
				folderName);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Document to download : %s", document);
			LOGGER.debug(msg);
		}

		if (document == null) {
			return;
		}

		InputStream inputStream = document.getContentStream().getStream();
		int read = 0;
		byte[] bytes = new byte[1024];
		response.setHeader("Content-Disposition",
				String.format("attachment; filename = %s",
						fileNameCreator.createFileName(returnType, gstin,
								taxPeriod, fileName)));
		OutputStream outputStream = response.getOutputStream();
		while ((read = inputStream.read(bytes)) != -1) {
			outputStream.write(bytes, 0, read);
		}

	}

	@PostMapping(value = "/ui/initiateGetCall", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getCallApi(@RequestBody String jsonString) {

		JsonObject resp = new JsonObject();
		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = "Begin EntityServiceImpl"
						+ ".getGSTINsForEntity ,Parsing Input request";
				LOGGER.debug(msg);
			}

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
			JsonArray respBody = new JsonArray();

			GetCallDto dto = gson.fromJson(requestObject, GetCallDto.class);

			List<InitiateGetCallDto> gstinTaxPeiordList = dto
					.getGstinTaxPeiordList();

			List<InitiateGetCallDto> activeGstinTaxPeiordList = new ArrayList<>();

			String returnType = dto.getReturnType();

			gstinTaxPeiordList.forEach(o -> {
				JsonObject json = new JsonObject();
				json.addProperty("gstin", o.getGstin());
				String authStatus = authTokenService
						.getAuthTokenStatusForGstin(o.getGstin());
				if ("A".equalsIgnoreCase(authStatus)) {

					activeGstinTaxPeiordList.add(o);
					json.addProperty("msg", String.format(
							"Get %s Initiated Successfully", returnType));
					respBody.add(json);

				} else {
					json.addProperty("msg",
							"Auth Token is Inactive, Please Activate");
					respBody.add(json);
				}
			});

			List<List<Pair<String, String>>> gstinReturnPeriodList = activeGstinTaxPeiordList
					.stream()
					.map(o -> createGstinPair(o, dto.getFy(), returnType))
					.collect(Collectors.toList());

			List<Pair<String, String>> list = gstinReturnPeriodList.stream()
					.collect(ArrayList::new, List::addAll, List::addAll);

			List<AsyncExecJob> jobList = new ArrayList<>();

			for (int i = 0; i < list.size(); i++) {

				String taxPeriod = list.get(i).getValue1();

				boolean isNoFutureTaxPeriod = false;
				if (returnType.equalsIgnoreCase("ITC04")) {
					isNoFutureTaxPeriod = GenUtil
							.isValidQuarterForCurrentFy(taxPeriod);
				} else {
					isNoFutureTaxPeriod = GenUtil
							.isValidTaxPeriodForCurrentFy(taxPeriod);
				}
				if (!isNoFutureTaxPeriod) {
					LOGGER.error(
							"Requested TaxPeriod {} is Future TaxPeriod, Hence skipping {} ",
							taxPeriod, returnType);
					continue;
				}

				String gstin = list.get(i).getValue0();
				List<Gstr1GetInvoicesReqDto> dtos = new ArrayList<>();
				Gstr1GetInvoicesReqDto reqDto = new Gstr1GetInvoicesReqDto();

				reqDto.setGstin(gstin);
				reqDto.setReturnPeriod(taxPeriod);

				// GSTR1 get call
				if ("GSTR1".equalsIgnoreCase(returnType)) {

					reqDto.setIsFailed(false);
					reqDto.setGstr1Sections(new ArrayList<>(
							getRelevantInvoiceTypes(returnType)));
					dtos.add(reqDto);
					PublicApiContext.setContextMap(PublicApiConstants.SOURCE,
							PublicApiConstants.API_CALL_DASH_RET);

					gstr1GetCallHandler.createGstr1Gstn(dtos, jobList);

				}
				// GSTR2A get call
				else if ("GSTR2A".equalsIgnoreCase(returnType)) {
					reqDto.setGstr2aSections(new ArrayList<>(
							getRelevantInvoiceTypes(returnType)));
					dtos.add(reqDto);

					gstr2aGetCallHandler.getCallGSTR2A(dtos, jobList);

				}
				// GSTR3B get call
				else if ("GSTR3B".equalsIgnoreCase(returnType)) {
					gstr3bGetApi.getGstnCall(gstin, taxPeriod);
				}
				// GSTR8A get call
				else if ("GSTR8A".equalsIgnoreCase(returnType)) {
					String finYear = GenUtil
							.getFinancialYearByTaxperiod(taxPeriod);
					gstr8AIntiateGetCallServiceImpl.getGstnCall(gstin, finYear,
							taxPeriod);
				} else if ("ITC04".equalsIgnoreCase(returnType)) {
					List<Itc04GetInvoicesReqDto> itc04Dtos = new ArrayList<>();
					Itc04GetInvoicesReqDto itc04Req = new Itc04GetInvoicesReqDto();
					itc04Req.setGstin(gstin);
					itc04Req.setReturnPeriod(taxPeriod);
					itc04Req.setItc04Sections(new ArrayList<>(
							getRelevantInvoiceTypes(returnType)));
					itc04Dtos.add(itc04Req);
					itc04GetApi.createItc04Gstn(itc04Dtos);
				} else if ("GSTR2X".equalsIgnoreCase(returnType)) {
					reqDto.setIsFailed(false);
					reqDto.setGstr2XSections(new ArrayList<>(
							getRelevantInvoiceTypes(returnType)));
					dtos.add(reqDto);
					gstr2XGetApi.gstr2XGetCallservice(dtos);
				}
				// GSTR7 Get Call.
				else if ("GSTR7".equalsIgnoreCase(returnType)) {
					
					Map<String, Config> confiMap = configManager.getConfigs(
							"GSTR7", "gstr7.transactional.cutOff", "DEFAULT");

					String cutOffRetPeriod = confiMap
							.get("gstr7.transactional.cutOff") == null
									? "202506"
									: confiMap.get("gstr7.transactional.cutOff")
											.getValue();
					
					Integer cutOffRetPeriodInt = GenUtil
							.convertTaxPeriodToInt(cutOffRetPeriod);
					LOGGER.error("ReturnPeriod {} ", cutOffRetPeriod);
					
					Integer currentRetPeriodInt = GenUtil
							.convertTaxPeriodToInt(taxPeriod);
					
					List<Gstr7GetInvoicesReqDto> gstr7Dtos = new ArrayList<>();
					Gstr7GetInvoicesReqDto gstr7ReqDto = new Gstr7GetInvoicesReqDto();
					gstr7ReqDto.setGstin(gstin);
					gstr7ReqDto.setReturnPeriod(taxPeriod);
					
					if (currentRetPeriodInt < cutOffRetPeriodInt) {
						gstr7ReqDto.setGstr7Sections(new ArrayList<>(
								getRelevantInvoiceTypes(returnType)));
					} else {
						gstr7ReqDto.setGstr7Sections(
								Arrays.asList(APIConstants.GSTR7_TRANSACTIONAL));
					}
					
					gstr7Dtos.add(gstr7ReqDto);
					// Get Tds Details
					gstr7GetApi.createGstr7Gstn(gstr7Dtos,currentRetPeriodInt < cutOffRetPeriodInt ? true : false);

					Anx2GetInvoicesReqDto gstr7SummaryDto = new Anx2GetInvoicesReqDto();
					gstr7SummaryDto.setGstin(gstin);
					gstr7SummaryDto.setReturnPeriod(taxPeriod);
					gstr7SummaryGetApi.getGstr7Summary(gstr7SummaryDto,
							TenantContext.getTenantId());
				} else if ("GSTR6".equalsIgnoreCase(returnType)) {

					List<Gstr6GetInvoicesReqDto> gstr6DtoList = new ArrayList<>();
					Gstr6GetInvoicesReqDto gstr6Dto = new Gstr6GetInvoicesReqDto();
					gstr6Dto.setGstin(gstin);
					gstr6Dto.setReturnPeriod(taxPeriod);
					gstr6Dto.setIsFailed(false);
					gstr6Dto.setGstr6Sections(new ArrayList<>(
							getRelevantInvoiceTypes(returnType)));
					gstr6DtoList.add(gstr6Dto);

					gstr6GetApi.createGstr6Gstn(gstr6DtoList);
				} else if ("GSTR8".equalsIgnoreCase(returnType)) {

					reqDto.setIsFailed(false);
					reqDto.setGstr8Sections(new ArrayList<>(
							getRelevantInvoiceTypes(returnType)));
					dtos.add(reqDto);
					PublicApiContext.setContextMap(PublicApiConstants.SOURCE,
							PublicApiConstants.API_CALL_DASH_RET);
					gstr8ApiGetCallJobHandler.createGstr8Gstn(dtos, jobList);
				}
				// GSTR1A get call
				else if (APIConstants.GSTR1A.equalsIgnoreCase(returnType)) {

					reqDto.setIsFailed(false);
					reqDto.setGstr1Sections(new ArrayList<>(
							getRelevantInvoiceTypes(returnType)));
					dtos.add(reqDto);
					PublicApiContext.setContextMap(PublicApiConstants.SOURCE,
							PublicApiConstants.API_CALL_DASH_RET);

					gstr1AGetCallHandler.createGstr1Gstn(dtos, jobList);

				}

			}

			if (!jobList.isEmpty())
				asyncJobsService.createJobs(jobList);

			/*
			 * String msg = String.format("Get %s Initiated Successfully",
			 * returnType);
			 */
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(respBody));
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
		} catch (Exception ex) {
			String msg = "Unexpected error while making api get call ";
			LOGGER.error(msg, ex);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/ui/downloadGetCallReport", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> downloadGetCallReport(
			@RequestBody String jsonString, HttpServletResponse response) {

		JsonObject resp = new JsonObject();
		File tempDir = null;
		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = "Begin EntityServiceImpl"
						+ ".getGSTINsForEntity ,Parsing Input request";
				LOGGER.debug(msg);
			}

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();

			String reqJson = requestObject.get("req").getAsJsonObject()
					.toString();

			DownloadGetCallDto dto = gson.fromJson(reqJson,
					DownloadGetCallDto.class);

			String returnType = dto.getReturnType();

			String jobStatus = "ZIP_COMPLETED";
			String startMonth = "04";
			String endMonth = "03";
			String appendMonthYear = null;
			String appendMonthYear1 = null;
			String financialPeriod = dto.getFy();
			if (financialPeriod != null && !financialPeriod.isEmpty()) {
				String[] arrOfStr = financialPeriod.split("-", 2);
				appendMonthYear = arrOfStr[0] + startMonth;
				appendMonthYear1 = "20" + arrOfStr[1] + endMonth;
			}
			int derivedStartPeriod = Integer.parseInt(appendMonthYear);

			int derivedEndPeriod = Integer.parseInt(appendMonthYear1);

			List<Object[]> listZipstatus = zipGenStatusRepository
					.getGstinZipGenStatus(dto.getGstinList(), jobStatus,
							derivedStartPeriod, derivedEndPeriod, returnType);

			tempDir = createTempDir(returnType);

			String folderName = DashboardCommonUtility.getDashboardFolderName(
					returnType, JobStatusConstants.CSV_FILE);

			List<Object[]> csvEligible = gstinGetStatusRepo
					.getCsvFilesEligibleForZip(true, dto.getGstinList(),
							derivedStartPeriod, derivedEndPeriod, returnType,
							"SUCCESS");

			if (csvEligible.isEmpty()) {
				String msg = "No data found";
				LOGGER.error("No records found to download csv files");
				resp.add("hdr",
						new Gson().toJsonTree(new APIRespDto("E", msg)));
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			}

			List<String> filesToDownload = new ArrayList<>();
			Set<String> distinctGstins = new HashSet<>();

			for (int i = 0; i < csvEligible.size(); i++) {

				String gstin = String.valueOf((csvEligible.get(i))[0]);
				String taxPeriod = String.valueOf((csvEligible.get(i))[1]);
				String section = String.valueOf((csvEligible.get(i))[2]);

				distinctGstins.add(gstin);

				if ("GSTR3B".equalsIgnoreCase(returnType)) {
					List<String> invTypes = getRelevantInvoiceTypes(returnType);

					invTypes.forEach(
							sec -> filesToDownload.add(createDocumentName(
									returnType, sec, taxPeriod, gstin)));
				} else {
					filesToDownload.add(createDocumentName(returnType, section,
							taxPeriod, gstin));
				}
			}

			for (String fileName : filesToDownload) {

				downloadCsvFileToTmpFileFromDocRepo(tempDir, folderName,
						fileName);

			}

			File[] files = tempDir.listFiles();

			List<String> filesToZip = Arrays.stream(files)
					.map(f -> f.getAbsolutePath())
					.collect(Collectors.toCollection(ArrayList::new));

			LocalDateTime now = EYDateUtil
					.toISTDateTimeFromUTC(LocalDateTime.now());
			DateTimeFormatter formatter = DateTimeFormatter
					.ofPattern("yyyyMMddHHmmss");
			String suffixStr = now.format(formatter);

			String compressedFileName = null;
			if (distinctGstins.size() == 1) {

				String gstin = distinctGstins.toArray(new String[1])[0];
				compressedFileName = returnType + "_ " + gstin + "_"
						+ suffixStr;
			} else {
				compressedFileName = returnType + "_" + suffixStr;
			}
			String zipFileName = compressedFileName + ".zip";

			compressor.compressFiles(tempDir.getAbsolutePath(), zipFileName,
					filesToZip);

			// downloading the zip
			File file = new File(
					tempDir.getAbsolutePath() + File.separator + zipFileName);
			InputStream inputStream = FileUtils.openInputStream(file);
			response.setContentType("application/zip");
			response.setHeader("Content-Disposition",
					String.format("attachment; filename =%s ", zipFileName));
			IOUtils.copy(inputStream, response.getOutputStream());
			response.flushBuffer();

			return new ResponseEntity<>("Download Success", HttpStatus.OK);
		} catch (Exception ex) {
			String msg = "Unexpected error while downloading zip file ";
			LOGGER.error(msg, ex);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} finally {
			if (tempDir != null && tempDir.exists()) {
				try {
					FileUtils.deleteDirectory(tempDir);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Deleted the Temp directory/Folder");
					}
				} catch (Exception ex) {
					// Log the error and proceed.
					String logMsg = String.format(
							"Failed to remove the temp "
									+ "directory created for zip: '%s'. This will "
									+ "lead to clogging of disk space.",
							tempDir.getAbsolutePath());
					LOGGER.error(logMsg, ex);
				}
			}
		}
	}

	@PostMapping(value = "/ui/downloadSelectedReports", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> downloadSelectedReports(
			@RequestBody String jsonString) {

		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = "Begin EntityServiceImpl"
						+ ".getGSTINsForEntity ,Parsing Input request";
				LOGGER.debug(msg);
			}

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			String fy = requestObject.get("fy").getAsString();

			GetCallDto dto = gson.fromJson(requestObject.toString(),
					GetCallDto.class);
			List<InitiateGetCallDto> gstinTaxPeiordList = dto
					.getGstinTaxPeiordList();

			String returnType = dto.getReturnType();

			String[] year = fy.split("-");
			String stYear = year[0];
			String endYear = "20" + year[1];

			gstinTaxPeiordList.forEach(o -> {
				List<String> taxPeriods = new ArrayList<>();
				o.getTaxPeriodList().forEach(tp -> {
					String taxPeriod = "";
					if ("ITC04".equalsIgnoreCase(returnType)) {
						taxPeriod = tp + stYear;
					} else {
						taxPeriod = (Integer.valueOf(tp) >= 4
								&& Integer.valueOf(tp) <= 12) ? tp + stYear
										: tp + endYear;
					}
					taxPeriods.add(taxPeriod);
				});

				o.setTaxPeriodList(taxPeriods);

			});

			String obj = gson.toJson(dto);

			requestObject = (new JsonParser()).parse(obj).getAsJsonObject();
			User user = SecurityContext.getUser();
			String userName = user.getUserPrincipalName();

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Api Call Dashboard download request for return Type - %s : %s",
						returnType, requestObject.toString());
				LOGGER.debug(msg);
			}

			if (returnType.equalsIgnoreCase("Gstr9")
					/*|| returnType.equalsIgnoreCase("Table-8A")*/) {
				String fyOld = requestObject.get("fy").getAsString();
				String formattedFy = GenUtil.getFormattedFy(fyOld);
				requestObject.addProperty("fy", formattedFy);
			}
			FileStatusDownloadReportEntity entity = new FileStatusDownloadReportEntity();

			entity.setReqPayload(requestObject.toString());
			entity.setCreatedBy(userName);
			entity.setCreatedDate(
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
			entity.setReportStatus(ReportStatusConstants.INITIATED);
			entity.setReportCateg("API Call Dashboard");
			entity.setDataType("Get Call");
			entity.setReportType(returnType + " API Call");

			entity = fileStatusDownloadReportRepo.save(entity);

			Long id = entity.getId();
			//need to remove this code
					/*downloadGetCallReportsImplTest.generateReport(id);*/

			String groupCode = TenantContext.getTenantId();

			JsonObject jobParams = new JsonObject();
			jobParams.addProperty("id", id);

			asyncJobsService.createJob(groupCode,
					JobConstants.DOWNLOAD_API_GET_CALL, jobParams.toString(),
					userName, 1L, null, null);

			jobParams.addProperty("reportType", returnType + " Reports");
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(jobParams);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);

			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed",
					"Unexpected occured in APi call dashboard download"
							+ e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Error Occured in Async Report Controller";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		}
	}

	private List<Pair<String, String>> createGstinPair(InitiateGetCallDto o,
			String fy, String returnType) {

		String[] year = fy.split("-");
		String stYear = year[0];
		String endYear = "20" + year[1];
		if ("ITC04".equalsIgnoreCase(returnType)) {
			return o.getTaxPeriodList().stream().map(
					x -> new Pair<String, String>(o.getGstin(), x + stYear))
					.collect(Collectors.toList());
		} else {
			return o.getTaxPeriodList().stream()
					.map(x -> new Pair<String, String>(o.getGstin(),
							(Integer.valueOf(x) >= 4
									&& Integer.valueOf(x) <= 12) ? x + stYear
											: x + endYear))
					.collect(Collectors.toList());
		}
	}

	private File createTempDir(String returnType) throws IOException {
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter
				.ofPattern("yyyyMMddHHmmss");
		String suffixStr = now.format(formatter);
		String tempFolder = returnType + "_" + suffixStr;
		return Files.createTempDirectory(tempFolder).toFile();
	}

	private String createDocumentName(String returnType, String section,
			String taxPeriod, String gstin) {
		String fileName = new StringJoiner("_").add(returnType).add(section)
				.add(taxPeriod).add(gstin).toString();
		return fileName + ".csv";
	}

	private List<String> getRelevantInvoiceTypes(String returnType) {
		final List<String> gstr1Types = ImmutableList.of("B2B", "B2CL", "B2CS",
				"B2BA", "NIL", "CDNR", "CDNRA", "TXP", "AT", "EXP", "CDNUR",
				"B2CSA", "B2CLA", "EXPA", "ATA", "TXPA", "CDNURA", "HSN",
				"DOC_ISSUE", APIConstants.SUPECO, APIConstants.SUPECOAMD,
				APIConstants.ECOM, APIConstants.ECOMAMD);


		final List<String> gstr8Types = ImmutableList.of("TCS", "URD");
		// For GSTR 3b there'll be only one json. But we cretae 2 separate
		// csv files out out of it. When we do this we can use 3b1 and 3b2 as
		// invoice types, so that the zipping process can happen correctly.
		final List<String> gstr3bTypes = ImmutableList.of("taxPayable",
				"summary");

		final List<String> gstr2aTypes = ImmutableList.of("B2B", "B2BA", "CDN",
				"CDNA", "ISD");

		final List<String> itc04Types = ImmutableList.of("GET");

		final List<String> gstr2xTypes = ImmutableList.of("TCSANDTDS");

		final List<String> gstr7Types = ImmutableList.of(
				APIConstants.GSTR7_TDS_DETAILS,
				APIConstants.GSTR7_SUMMARY_DETAILS);

		final List<String> gstr6Types = ImmutableList.of("b2b", "b2ba", "cdn",
				"cdna", "isd", "isda");

		switch (returnType.toUpperCase()) {
		case "GSTR1":
			return gstr1Types;
		case "GSTR3B":
			return gstr3bTypes;
		case "GSTR2A":
			return gstr2aTypes;
		case "GSTR2X":
			return gstr2xTypes;
		case "ITC04":
			return itc04Types;
		case "GSTR7":
			return gstr7Types;
		case "GSTR6":
			return gstr6Types;
		case "GSTR8":
			return gstr8Types;
		case "GSTR1A":
			return gstr1Types;
			
		default: {
			String msg = String.format(
					"Invalid GSTR Invoice Type encountered: " + "'%s'",
					returnType);
			LOGGER.error(msg);
			throw new AppException(msg);
		}
		}
	}

	private void downloadCsvFileToTmpFileFromDocRepo(File tmpDir,
			String folderName, String fileName) {

		if (LOGGER.isDebugEnabled()) {
			String logMsg = String.format(
					"Before downloading the existing"
							+ " csv file : '%s' from container : %s",
					fileName, folderName);
			LOGGER.debug(logMsg);
		}

		File tmpCsvFile = new File(tmpDir.toPath() + File.separator + fileName);

		try {
			Document document = DocumentUtility.downloadDocument(fileName,
					folderName);

			if (document == null) {
				LOGGER.error(
						"Document doesn't exist, doc Name {} , folderName {}",
						fileName, folderName);
				return;
			}

			FileUtils.copyInputStreamToFile(
					document.getContentStream().getStream(), tmpCsvFile);

			if (LOGGER.isDebugEnabled()) {
				String logMsg = String.format(
						"Downloaded the document : '%s'"
								+ " Successfully from the folder : '%s'",
						fileName, folderName);
				LOGGER.debug(logMsg);
			}
		} catch (Exception e) {
			String msg = "Exception ocured while downloading csv";
			LOGGER.error(msg, e);
			throw new AppException(msg, e);
		}
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

}
