package com.ey.advisory.controller.gstr9;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.dashboard.apiCall.GetFyCallDto;
import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.entities.gstr9.Gstr9AutoCalculateEntity;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9AutoCalculateRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9GetSummaryRepository;
import com.ey.advisory.app.data.services.common.GstnCommonServiceUtil;
import com.ey.advisory.app.data.services.gstr9.Gstr9AutoPopulateTblDataUtil;
import com.ey.advisory.app.data.services.gstr9.Gstr9EntityLevelService;
import com.ey.advisory.app.data.services.gstr9.Gstr9InitiateGetDataUtil;
import com.ey.advisory.app.data.services.gstr9.Gstr9OutwardUtil;
import com.ey.advisory.app.data.services.gstr9.Gstr9PopulateTblDataUtil;
import com.ey.advisory.app.data.services.gstr9.Gstr9Util;
import com.ey.advisory.app.docs.dto.gstr9.GetDetailsForGstr9ReqDto;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GSTR9Constants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Siva.Reddy
 *
 */
@Slf4j
@RestController
@RequestMapping(value = "/ui/")
public class Gstr9EntityLevelController {

	@Autowired
	@Qualifier("Gstr9EntityLevelServiceImpl")
	private Gstr9EntityLevelService gstr9EntityLevelService;

	@Autowired
	@Qualifier("Gstr9GetSummaryRepository")
	private Gstr9GetSummaryRepository gstr9GetSummaryRepo;

	@Autowired
	@Qualifier("Gstr9AutoCalculateRepository")
	private Gstr9AutoCalculateRepository gstr9AutoCalcRepo;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	GSTNAuthTokenService authTokenService;

	@Autowired
	Gstr9OutwardUtil gstr9OutwardUtil;

	@Autowired
	Gstr9AutoPopulateTblDataUtil gstr9AutoPopTblDataUtil;

	@Autowired
	Gstr9PopulateTblDataUtil gstr9PopTblDataUtil;

	@Autowired
	GstnCommonServiceUtil gstnCommonUtil;

	@Autowired
	@Qualifier("Gstr9InitiateGetDataUtil")
	Gstr9InitiateGetDataUtil gstr9InitiateGetDataUtil;

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	@PostMapping(value = "getUpdateGstnData", consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getUpdateGstnData(
			@RequestBody String jsonString, HttpServletRequest req) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject obj = JsonParser.parseString(jsonString).getAsJsonObject();
		JsonObject reqObj = obj.get("req").getAsJsonObject();
		String gstin = reqObj.get("gstin").getAsString();
		String isGstinActive = authTokenService
				.getAuthTokenStatusForGstin(gstin);

		if (!"A".equalsIgnoreCase(isGstinActive)) {
			JsonObject jsonObj = new JsonObject();
			String errMsg = "Auth Token is Inactive, Please Activate";
			jsonObj.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			jsonObj.addProperty("resp", errMsg);
			return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
		}
		String fyOld = reqObj.get("fy").getAsString();
		String formattedFy = GenUtil.getFormattedFy(fyOld);
		String taxPeriod = GenUtil.getFinancialPeriodFromFY(formattedFy);
		return getUpdateGstnData(gstin, formattedFy, taxPeriod);
	}

	@PostMapping(value = "initiateGstr9GetCall", consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> initiateGstr9GetCall(
			@RequestBody String jsonString, HttpServletRequest req) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonObject obj = JsonParser.parseString(jsonString)
					.getAsJsonObject();
			JsonObject reqObj = obj.get("req").getAsJsonObject();
			JsonArray respBody = new JsonArray();
			GetFyCallDto dto = gson.fromJson(reqObj, GetFyCallDto.class);
			List<String> gstinList = dto.getGstins();
			String fyOld = dto.getFy();
			String formattedFy = GenUtil.getFormattedFy(fyOld);
			String taxPeriod = GenUtil.getFinancialPeriodFromFY(formattedFy);
			gstinList.forEach(o -> {
				JsonObject json = new JsonObject();
				json.addProperty("gstin", o);
				String authStatus = authTokenService
						.getAuthTokenStatusForGstin(o);
				if ("A".equalsIgnoreCase(authStatus)) {
					json.addProperty("msg",
							"Get Gstr9 Initiated Successfully for "
									+ formattedFy);
					getUpdateGstnData(o, formattedFy, taxPeriod);
					respBody.add(json);
				} else {
					json.addProperty("msg",
							"Auth Token is Inactive, Please Activate");
					respBody.add(json);
				}
			});
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(respBody));
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
		} catch (Exception ex) {
			String msg = "Unexpected error while making api get call ";
			LOGGER.error(msg, ex);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private ResponseEntity<String> getUpdateGstnData(String gstin, String fy,
			String taxPeriod) {
		JsonObject jsonObj = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		String userName = SecurityContext.getUser() != null
				? (SecurityContext.getUser().getUserPrincipalName() != null
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM")
				: "SYSTEM";
		try {
			Long batchId = createBatchAndSave(gstin, taxPeriod, "RETSUM",
					userName);

			APIResponse apiResponse = gstr9InitiateGetDataUtil
					.getGstnCall(gstin, fy, taxPeriod);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("After invoking GetUpdated Gstin"
						+ ".getGstr9Details() method " + "json response : "
						+ apiResponse);
			}
			if (!apiResponse.isSuccess()) {
				String errMsg = String.format(
						"GSTN has Returned Error Response %s",
						apiResponse.getError().getErrorDesc());
				LOGGER.error(errMsg);
				jsonObj.add("hdr",
						gson.toJsonTree(new APIRespDto("E", errMsg)));
				jsonObj.addProperty("resp", errMsg);
				batchUtil.updateById(batchId, APIConstants.FAILED,
						apiResponse.getError().getErrorCode(),
						apiResponse.getError().getErrorDesc(), false);
			} else {
				batchUtil.updateById(batchId, APIConstants.SUCCESS, null, null,
						false);
				jsonObj.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				jsonObj.addProperty("resp", "Data Updated Successfully");
			}
			return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);
			jsonObj.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			jsonObj.addProperty("resp", ex.getMessage());
			return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "getAutoCalcGstnData", consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getAutoCalcGstnData(
			@RequestBody String jsonString, HttpServletRequest req) {
		return getAutoCalcGstnData(jsonString);
	}

	private ResponseEntity<String> getAutoCalcGstnData(String jsonString) {
		JsonObject jsonObj = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		String userName = SecurityContext.getUser() != null
				? (SecurityContext.getUser().getUserPrincipalName() != null
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM")
				: "SYSTEM";
		try {
			JsonObject obj = JsonParser.parseString(jsonString)
					.getAsJsonObject();
			JsonObject req = obj.get("req").getAsJsonObject();
			String gstin = req.get("gstin").getAsString();
			String fyOld = req.get("fy").getAsString();
			String formattedFy = GenUtil.getFormattedFy(fyOld);
			String taxPeriod = GenUtil.getFinancialPeriodFromFY(formattedFy);
			Integer derviedTaxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriod);
			String isGstinActive = authTokenService
					.getAuthTokenStatusForGstin(gstin);

			if (!"A".equalsIgnoreCase(isGstinActive)) {
				String errMsg = "Auth Token is Inactive, Please Activate";
				throw new AppException(errMsg);
			}
			APIResponse apiResponse = gstr9EntityLevelService
					.getAutoCalcDetails(gstin, taxPeriod);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("After invoking getAutoCalcGstnData"
						+ ".getGstr9Details() method " + "json response : "
						+ apiResponse);
			}
			Long batchId = createBatchAndSave(gstin, taxPeriod,
					GSTR9Constants.GSTR9_AUTOCALC, userName);

			if (!apiResponse.isSuccess()) {
				String errMsg = String.format(
						"GSTN has Returned Error Response %s",
						apiResponse.getError().getErrorDesc());
				LOGGER.error(errMsg);
				batchUtil.updateById(batchId, APIConstants.FAILED,
						apiResponse.getError().getErrorCode(),
						apiResponse.getError().getErrorDesc(), false);
				jsonObj.add("hdr",
						gson.toJsonTree(new APIRespDto("E", errMsg)));
				jsonObj.addProperty("resp", errMsg);
			} else {
				String getGstnData = apiResponse.getResponse();

				gstnCommonUtil.saveOrUpdateGstnUserRequest(gstin, taxPeriod,
						getGstnData, GSTR9Constants.GSTR9_AUTOCALC);
				List<Gstr9AutoCalculateEntity> listOfAutoCalcEntities = new ArrayList<>();
				GetDetailsForGstr9ReqDto gstr9GetReqDto = gson.fromJson(
						apiResponse.getResponse(),
						GetDetailsForGstr9ReqDto.class);
				batchUtil.updateById(batchId, APIConstants.SUCCESS, null, null,
						false);
				// Below Method will Populate the Data from Gstr9 Get Auto Cacl
				// API and
				// Create a List of Entities
				gstr9AutoPopTblDataUtil.populateAutoTbl4Data(gstr9GetReqDto,
						gstin, formattedFy, taxPeriod, derviedTaxPeriod,
						listOfAutoCalcEntities, userName);
				gstr9AutoPopTblDataUtil.populateAutoTbl5Data(gstr9GetReqDto,
						gstin, formattedFy, taxPeriod, derviedTaxPeriod,
						listOfAutoCalcEntities, userName);
				gstr9AutoPopTblDataUtil.populateAutoTbl6Data(gstr9GetReqDto,
						gstin, formattedFy, taxPeriod, derviedTaxPeriod,
						listOfAutoCalcEntities, userName);
				gstr9AutoPopTblDataUtil.populateAutoTbl8Data(gstr9GetReqDto,
						gstin, formattedFy, taxPeriod, derviedTaxPeriod,
						listOfAutoCalcEntities, userName);
				gstr9AutoPopTblDataUtil.populateAutoTbl9Data(gstr9GetReqDto,
						gstin, formattedFy, taxPeriod, derviedTaxPeriod,
						listOfAutoCalcEntities, userName);
				int updatedRecords = gstr9AutoCalcRepo
						.updateActiveExistingRecords(gstin, taxPeriod,
								userName);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							" Updated Existing Active Records {} for"
									+ " Gstin  {}  and Fy {} in Gstr9AutoCalculateEntity",
							updatedRecords, gstin, formattedFy);
				}
				gstr9AutoCalcRepo.saveAll(listOfAutoCalcEntities);
				jsonObj.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				jsonObj.addProperty("resp", "Data Updated Successfully");
			}
			return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);
			jsonObj.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			jsonObj.addProperty("resp", ex.getMessage());
			return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "clearTabData", consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> clearTabData(@RequestBody String jsonString,
			HttpServletRequest req) {
		return clearTabDataWise(jsonString);
	}

	private ResponseEntity<String> clearTabDataWise(String jsonString) {
		JsonObject jsonObj = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		int deleteCount = 0;
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("INSIDE clearTabDataWise API");
			}
			JsonObject obj = JsonParser.parseString(jsonString)
					.getAsJsonObject();
			JsonObject req = obj.get("req").getAsJsonObject();
			String gstin = req.get("gstin").getAsString();
			String fyOld = req.get("fy").getAsString();
			String formattedFy = GenUtil.getFormattedFy(fyOld);
			String taxPeriod = GenUtil.getFinancialPeriodFromFY(formattedFy);
			String tabName = req.get("tabName").getAsString();
			if (Strings.isNullOrEmpty(gstin)
					|| Strings.isNullOrEmpty(taxPeriod)) {
				String msg = "Gstin and taxPeriod is mandatory to clear the Data";
				LOGGER.error(msg);
				throw new AppException(msg);
			}
			String createdBy = (SecurityContext.getUser() != null
					&& SecurityContext.getUser().getUserPrincipalName() != null)
							? SecurityContext.getUser().getUserPrincipalName()
							: "SYSTEM";
			List<String> sectionList = new ArrayList<String>();
			sectionList = Gstr9Util.getSectionListbyTabName(tabName);
			if (sectionList.isEmpty()) {
				String errMsg = "TabName does not match with the Map";
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			}
			if (tabName.equalsIgnoreCase("hsnoutward")
					|| tabName.equalsIgnoreCase("hsninward")) {
				deleteCount = gstr9OutwardUtil.hsnsoftDeleteData(gstin,
						taxPeriod, sectionList, createdBy);
			} else {
				deleteCount = gstr9OutwardUtil.softDeleteData(gstin,
						formattedFy, sectionList, createdBy);
			}
			JsonObject resp = new JsonObject();
			if (deleteCount > 0) {
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.addProperty("resp", "Data Cleared Successfully");
			} else {
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.addProperty("resp", "No Data Available to Clear");
				LOGGER.debug("No Data Available to Delete");
			}
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String msg = "Not Able to Clear the Data";
			LOGGER.error(msg, ex);
			jsonObj.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			jsonObj.addProperty("resp", msg);
			return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "gstr9CopyCompData", consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> gstr9CopyData(@RequestBody String jsonString,
			HttpServletRequest req) {
		return copyComputeGstr9Data(jsonString, req);
	}

	public ResponseEntity<String> copyComputeGstr9Data(String jsonString,HttpServletRequest request) {
		JsonObject jsonObj = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("INSIDE copyComputeGstr9Data");
			}
			JsonObject obj = JsonParser.parseString(jsonString)
					.getAsJsonObject();
			JsonObject req = obj.get("req").getAsJsonObject();
			String gstin = req.get("gstin").getAsString();
			String fyOld = req.get("fy").getAsString();
			String formattedFy = GenUtil.getFormattedFy(fyOld);
			String taxPeriod = GenUtil.getFinancialPeriodFromFY(formattedFy);
			String saveStatus = gstr9EntityLevelService
					.copyGstr9ComputeData(gstin, taxPeriod, formattedFy);
			if ("Success".equalsIgnoreCase(saveStatus)) {
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.addProperty("resp", "Data Copied Successfully");
			}
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			String msg = "Error while copy the data from Compute Table.";
			LOGGER.error(msg, ex);
			jsonObj.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			jsonObj.addProperty("resp", ex.getMessage());
			return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "gstr9CopyAutoCompData", consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> copyAutoComputeGstr9Data(
			@RequestBody String jsonString) {
		JsonObject jsonObj = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("INSIDE copyAutoComputeGstr9Data");
			}
			JsonObject obj = JsonParser.parseString(jsonString)
					.getAsJsonObject();
			JsonObject req = obj.get("req").getAsJsonObject();
			String gstin = req.get("gstin").getAsString();
			String fyOld = req.get("fy").getAsString();
			String formattedFy = GenUtil.getFormattedFy(fyOld);
			String taxPeriod = GenUtil.getFinancialPeriodFromFY(formattedFy);
			String saveStatus = gstr9EntityLevelService
					.copyGstr9AutoComputeData(gstin, taxPeriod, formattedFy);
			if ("Success".equalsIgnoreCase(saveStatus)) {
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.addProperty("resp", "Data Copied Successfully");
			}
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			String msg = "Error while copy the data from Auto Compute Table.";
			LOGGER.error(msg, ex);
			jsonObj.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			jsonObj.addProperty("resp", ex.getMessage());
			return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
		}
	}

	private Long createBatchAndSave(String gstin, String retPeriod,
			String returnType, String userName) {
		batchRepo.softlyDelete(returnType, GSTR9Constants.GSTR9.toUpperCase(),
				gstin, retPeriod);
		GetAnx1BatchEntity batch = batchUtil.makeBatch(gstin, retPeriod,
				returnType, GSTR9Constants.GSTR9.toUpperCase(), userName);
		batch = batchRepo.save(batch);
		return batch.getId();
	}
}
