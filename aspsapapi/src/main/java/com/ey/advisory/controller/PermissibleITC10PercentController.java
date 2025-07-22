package com.ey.advisory.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.docs.dto.PermissibleITC10PerGstinDetailsDto;
import com.ey.advisory.app.docs.dto.PermissibleITC10PercentDetailReqDto;
import com.ey.advisory.app.docs.dto.PermissibleITC10PercentDto;
import com.ey.advisory.app.docs.dto.PermissibleITC10PercentReqDto;
import com.ey.advisory.app.docs.dto.PermissibleITCGstinDetailsDto;
import com.ey.advisory.app.services.docs.PermissibleITC10PercentService;
import com.ey.advisory.app.util.DataSecurityAttributeUtil;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Rajesh NK
 *
 */
@RestController
@Slf4j
public class PermissibleITC10PercentController {

	@Autowired
	private GSTNDetailRepository gstinDetailRepo;

	@Autowired
	private EntityConfigPrmtRepository qstnRepo;

	@Autowired
	@Qualifier("PermisibleITC10PercentServiceImpl")
	private PermissibleITC10PercentService permisibleITC10PercentService;

	@PostMapping(value = "/ui/getPermissibleITC10PercentSummary", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getPermissibleItcTenPercentSummary(
			@RequestBody String jsonString) {

		List<String> gstnsList = new ArrayList<>();

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstanceWithDateFmt();
		LOGGER.debug("The selected criteria for Permissible ITC 10%  is:->"
				+ requestObject.get("req"));
		// Execute the service method and get the result.
		try {

			PermissibleITC10PercentReqDto itcReqDto = gson.fromJson(json,
					PermissibleITC10PercentReqDto.class);

			Long entityId = itcReqDto.getEntityId();

			String toTaxPeriod = itcReqDto.getToTaxPeriod();

			String fromTaxPeriod = itcReqDto.getFromTaxPeriod();

			String reconType = itcReqDto.getReconType();

			List<String> docType = itcReqDto.getDocType();

			List<Long> entityIds = new ArrayList<>();
			entityIds.add(Long.valueOf(entityId));

			Map<String, String> outwardSecurityAttributeMap = DataSecurityAttributeUtil
					.getOutwardSecurityAttributeMap();
			Map<String, List<String>> dataSecAttrs = DataSecurityAttributeUtil
					.dataSecurityAttrMapForQuery(entityIds,
							outwardSecurityAttributeMap);
			List<String> dataSecList = dataSecAttrs
					.get(OnboardingConstant.GSTIN);

			if (itcReqDto.getGstinList() != null
					&& !itcReqDto.getGstinList().isEmpty()) {
				gstnsList = itcReqDto.getGstinList();
			} else {
				gstnsList = gstinDetailRepo.getRegularandSezGstins(dataSecList);
			}

			if (itcReqDto.getEntityId() == null
					|| itcReqDto.getFromTaxPeriod() == null
					|| itcReqDto.getToTaxPeriod() == null
					|| reconType == null) {

				String msg = "Entity Id, To taxperiod, From taxperiod , ReconType"
						+ " are mandatory";
				JsonObject resp = new JsonObject();
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", gson.toJsonTree(msg));
				return new ResponseEntity<String>(resp.toString(),
						HttpStatus.OK);
			}

			List<PermissibleITC10PercentDto> sGstinTaxperiodDetails = permisibleITC10PercentService
					.getPermissibleRecord(gstnsList, toTaxPeriod, fromTaxPeriod,
							docType, reconType);

			JsonObject resp = new JsonObject();
			JsonObject gstinResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(sGstinTaxperiodDetails);
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
		}

		catch (Exception ex) {
			String msg = " Unexpected error occured";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/ui/getPermissibleITC10PercentDetails", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getPermissibleItcTenPercentDetails(
			@RequestBody String jsonString) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstanceWithDateFmt();
		LOGGER.debug("The selected criteria for Permissible ITC 10%  is: ->"
				+ requestObject.get("req"));
		List<String> gstnsList = new ArrayList<>();

		// Execute the service method and get the result.
		try {

			PermissibleITC10PercentDetailReqDto itcReqDto = gson.fromJson(json,
					PermissibleITC10PercentDetailReqDto.class);

			Long entityId = itcReqDto.getEntityId();

			String toTaxPeriod = itcReqDto.getToTaxPeriod();

			String fromTaxPeriod = itcReqDto.getFromTaxPeriod();

			List<String> docType = itcReqDto.getDocType();

			String reconType = itcReqDto.getReconType();

			List<Long> entityIds = new ArrayList<>();
			entityIds.add(Long.valueOf(entityId));

			Map<String, String> outwardSecurityAttributeMap = DataSecurityAttributeUtil
					.getOutwardSecurityAttributeMap();
			Map<String, List<String>> dataSecAttrs = DataSecurityAttributeUtil
					.dataSecurityAttrMapForQuery(entityIds,
							outwardSecurityAttributeMap);
			List<String> dataSecList = dataSecAttrs
					.get(OnboardingConstant.GSTIN);

			if (itcReqDto.getGstinList() != null
					&& !itcReqDto.getGstinList().isEmpty()) {
				gstnsList = itcReqDto.getGstinList();
			} else {
				gstnsList = gstinDetailRepo.getRegularandSezGstins(dataSecList);
			}

			if (gstnsList == null || itcReqDto.getFromTaxPeriod() == null
					|| itcReqDto.getToTaxPeriod() == null) {

				String msg = "Gstin, To taxperiod, From taxperiod , ReconType are mandatory";
				JsonObject resp = new JsonObject();
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", gson.toJsonTree(msg));
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			}
			List<PermissibleITC10PerGstinDetailsDto> permissibleDetails = permisibleITC10PercentService
					.getPermissibleGstinDetails(gstnsList, toTaxPeriod,
							fromTaxPeriod, docType, reconType);

			PermissibleITCGstinDetailsDto itcTenPercGstinDetails = new PermissibleITCGstinDetailsDto();
			itcTenPercGstinDetails
					.setItcTenPercGstinDetails(permissibleDetails);

			JsonObject resp = new JsonObject();
			JsonObject gstinResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(itcTenPercGstinDetails);
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
		}

		catch (Exception ex) {
			String msg = " Unexpected error occured";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@GetMapping(value = "/ui/getEligibleCreditPercentage", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getEligibleCreditPercentage(
			@RequestParam Long entityId) {
		Gson gson = GsonUtil.newSAPGsonInstanceWithDateFmt();
		String itcPerc = null;
		try {

			EntityConfigPrmtEntity e = qstnRepo
					.findByGroupCodeAndEntityIdAndparamkryIdAndIsDeleteFalse(
							TenantContext.getTenantId(), entityId, "I23");
			if (e == null) {
				itcPerc = "5";
			} else {
				itcPerc = getValidPercentage(e.getParamValue());
			}
			JsonObject resp = new JsonObject();
			JsonObject percResp = new JsonObject();
			percResp.addProperty("egCrPerc", itcPerc);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", percResp);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception e) {

			String msg = " Unexpected error occured";
			LOGGER.error(msg, e);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		}
	}

	private String getValidPercentage(String percentage) {
		try {
			double value = Double.parseDouble(percentage);
			if (value < 0)
				return "5";
			else
				return percentage;
		} catch (NumberFormatException e) {
			return "5";
		}
	}
}
