package com.ey.advisory.controller.drc;
/**
 * 
 */

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.master.DRCReasonMasterEntity;
import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.caches.DefaultDrcReasonCache;
import com.ey.advisory.app.data.entities.drc.TblDrcChallanDetails;
import com.ey.advisory.app.data.services.drc.DrcGetRespDto;
import com.ey.advisory.app.data.services.drc.DrcGstnService;
import com.ey.advisory.app.data.services.drc.DrcService;
import com.ey.advisory.app.data.services.drc.Reason;
import com.ey.advisory.app.data.services.drc01c.Drc01cGstnService;
import com.ey.advisory.app.util.DataSecurityAttributeUtil;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Reddy
 *
 * 
 */
@Slf4j
@RestController
public class DRCScreenController {

	@Autowired
	private GSTNDetailRepository gstinDetailRepo;

	@Autowired
	private DrcService drcService;

	@Autowired
	private DrcGstnService drcGstnService;

	@Autowired
	DefaultDrcReasonCache defaultReasonCache;
	
	@Autowired
	@Qualifier("Drc01cGstnServiceImpl")
	private Drc01cGstnService drc01cGstnService;

	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfigPemtRepo;

	private static final List<String> REG_TYPES = ImmutableList.of("REGULAR",
			"SEZ", "SEZU", "SEZD");

	@PostMapping(value = "/ui/getDrcDetails", consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getDrcDetails(@RequestBody String jsonString,
			HttpServletRequest req) {
		JsonObject jsonObj = new JsonObject();
		JsonObject requestObject = JsonParser.parseString(jsonString)
				.getAsJsonObject().getAsJsonObject("req");
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		DrcGetRespDto getRespDetails = new DrcGetRespDto();
		try {
			JsonArray gstins = new JsonArray();
			List<String> gstnsList = null;
			String taxPeriod = requestObject.get("taxPeriod").getAsString();
			List<Long> entityIds = new ArrayList<>();
			Long entityId = requestObject.get("entityId").getAsLong();
			entityIds.add(entityId);
			if ((requestObject.has("gstins"))
					&& (requestObject.getAsJsonArray("gstins").size() > 0)) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"As gstins are provided in request, not Invoking"
									+ "service to get gstins for entity");
				}
				gstins = requestObject.getAsJsonArray("gstins");
				Type listType = new TypeToken<ArrayList<String>>() {
				}.getType();
				gstnsList = gson.fromJson(gstins, listType);
			} else {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Invoking service to get gstins for entity");
				}
				Map<String, String> outwardSecurityAttributeMap = DataSecurityAttributeUtil
						.getOutwardSecurityAttributeMap();
				Map<String, List<String>> dataSecAttrs = DataSecurityAttributeUtil
						.dataSecurityAttrMapForQuery(entityIds,
								outwardSecurityAttributeMap);
				List<String> dataSecList = dataSecAttrs
						.get(OnboardingConstant.GSTIN);

				gstnsList = gstinDetailRepo
						.filterGstinBasedByRegType(dataSecList, REG_TYPES);
			}
			if (gstnsList == null || gstnsList.isEmpty()) {
				JsonObject resp = new JsonObject();
				String msg = "Gstins cannot be empty";
				getRespDetails.setErrMsg(msg);
				JsonElement respBody = gson.toJsonTree(getRespDetails);
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", gson.toJsonTree(respBody));
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			}
			List<String> uniqueGstins = gstnsList.stream().distinct()
					.collect(Collectors.toList());
			String refId = null;
			if (requestObject.has("refId")) {
				refId = requestObject.get("refId").getAsString();
			}

			getRespDetails = drcService.getDRCGetDetails(uniqueGstins,
					taxPeriod, entityId, refId);
			JsonElement respBody = gson.toJsonTree(getRespDetails);
			jsonObj.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			jsonObj.add("resp", respBody);
			return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			LOGGER.error("Unable to Fetch the DRC Details. ", ex);
			JsonObject resp = new JsonObject();
			getRespDetails.setErrMsg(ex.getMessage());
			JsonElement respBody = gson.toJsonTree(getRespDetails);
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(respBody));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	@PostMapping(value = "/ui/saveUserReasons", consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveUserReasons(
			@RequestBody String jsonString, HttpServletRequest req) {
		JsonObject jsonObj = new JsonObject();
		JsonObject requestObject = JsonParser.parseString(jsonString)
				.getAsJsonObject().getAsJsonObject("req");
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		try {
			if ((requestObject.has("reasons"))
					&& (requestObject.getAsJsonArray("reasons").size() > 0)) {
				List<Reason> reasonList = gson.fromJson(
						requestObject.getAsJsonArray("reasons"),
						new TypeToken<List<Reason>>() {
						}.getType());

				String response = drcService.saveReasonDetails(reasonList);
				JsonElement respBody = gson.toJsonTree(response);
				jsonObj.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				jsonObj.add("resp", respBody);
				return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
			} else {
				JsonObject resp = new JsonObject();
				String errMsg = "Reasons cannot be empty";
				JsonElement respBody = gson.toJsonTree(errMsg);
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", gson.toJsonTree(respBody));
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			}
		} catch (Exception ex) {
			LOGGER.error("Exception while Saving DRC Details ", ex);
			String errMsg = "Unable to Save Reason Details.";
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(errMsg);
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(respBody));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	@PostMapping(value = "/ui/saveDifferentialDtls", consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveDifferentialDtls(
			@RequestBody String jsonString, HttpServletRequest req) {
		JsonObject jsonObj = new JsonObject();
		JsonObject requestObject = JsonParser.parseString(jsonString)
				.getAsJsonObject().getAsJsonObject("req");
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		try {
			String gstin = requestObject.get("gstin").getAsString();
			String taxPeriod = requestObject.get("taxPeriod").getAsString();
			String refId = requestObject.get("refid").getAsString();
			if ((requestObject.has("differentialDetails")) && (requestObject
					.getAsJsonArray("differentialDetails").size() > 0)) {
				List<TblDrcChallanDetails> challanDtls = gson.fromJson(
						requestObject.getAsJsonArray("differentialDetails"),
						new TypeToken<List<TblDrcChallanDetails>>() {
						}.getType());

				String response = drcService.saveDifferentialDetails(gstin,
						taxPeriod, refId, challanDtls);
				JsonElement respBody = gson.toJsonTree(response);
				jsonObj.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				jsonObj.add("resp", respBody);
				return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
			} else {
				JsonObject resp = new JsonObject();
				String errMsg = "Reasons cannot be empty";
				JsonElement respBody = gson.toJsonTree(errMsg);
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", gson.toJsonTree(respBody));
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			}
		} catch (Exception ex) {
			LOGGER.error("Exception while Saving DRC Details ", ex);
			String errMsg = "Unable to Save Reasons.";
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(errMsg);
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(respBody));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/ui/fetchDrcDetails", consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> fetchDrcDetails(
			@RequestBody String jsonString, HttpServletRequest req) {
		JsonObject requestObject = JsonParser.parseString(jsonString)
				.getAsJsonObject().getAsJsonObject("req");
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		try {
			JsonArray gstinArray = requestObject.get(APIConstants.GSTIN_LIST)
					.getAsJsonArray();
			String retPeriod = requestObject.get(APIConstants.RETPERIOD)
					.getAsString();
			String frmType = requestObject.get("frmtyp").getAsString();
			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();
			JsonArray respBody=null;
			List<String> gstnsList = gson.fromJson(gstinArray, listType);
			if(frmType.equalsIgnoreCase("DRC01B")){
			respBody = drcGstnService.getDrcRetComList(gstnsList,
					retPeriod, false, null);
			}else{
			respBody = drc01cGstnService.getDrcRetComList(gstnsList,
						retPeriod, false, null);
			}
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(respBody));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String msg = "Unable to Fetching DRC Details from GSTN.";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@GetMapping(value = "/ui/getAllDrcReasons", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getAllState() {
		try {
			Gson gson = GsonUtil.gsonInstanceWithExpose();
			List<DRCReasonMasterEntity> reasonsDetails = defaultReasonCache
					.getReasonsList();
			JsonObject gstinResp = new JsonObject();
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(reasonsDetails);
			gstinResp.add("reasons", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gstinResp);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String msg = "Unable to fetch the DRC Reasons. ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

}
