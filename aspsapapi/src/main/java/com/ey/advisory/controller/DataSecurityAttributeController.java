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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.docs.dto.FileStatusReportDto;
import com.ey.advisory.app.search.reports.BasicCommonSecParamAsyncReports;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/ui")
public class DataSecurityAttributeController {

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstnDetailRepository;

	@Autowired
	@Qualifier("BasicCommonSecParamAsyncReports")
	BasicCommonSecParamAsyncReports basicCommonSecParamAsyncReports;

	@GetMapping(value = "/getGstnbyEntityId", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstnbyEntityId(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Gson gson = GsonUtil.gsonInstanceWithEWB24HRFormat();
		JsonObject resp = null;
		try {
			String entityId = request.getParameter("entityId");
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Entity Id ", entityId);
			}

			FileStatusReportDto fileStatusDto = new FileStatusReportDto();
			List<Long> entityIds = new ArrayList<>();
			entityIds.add(Long.valueOf(entityId));
			fileStatusDto.setEntityId(entityIds);
			fileStatusDto.setDataType("outward");
			FileStatusReportDto statusReportDto = basicCommonSecParamAsyncReports
					.setDataSecuritySearchParams(fileStatusDto);
			Map<String, List<String>> dataSecReqMap = statusReportDto
					.getDataSecAttrs();
			List<String> gstins = dataSecReqMap.get(OnboardingConstant.GSTIN);

			if (gstins.isEmpty()) {

				resp = new JsonObject();
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", gson.toJsonTree(
						"No GSTIN's Available for the Selected Entity"));
				return new ResponseEntity<>(resp.toString(),
						HttpStatus.INTERNAL_SERVER_ERROR);

			}
			resp = new JsonObject();
			JsonArray gstinArray = new JsonArray();
			JsonObject finalObj = new JsonObject();
			for (int i = 0; i < gstins.size(); i++) {
				JsonObject gstinObj = new JsonObject();
				gstinObj.addProperty("gstin", gstins.get(i));
				gstinArray.add(gstinObj);
			}
			finalObj.add("gstindata", gstinArray);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", finalObj);
			LOGGER.debug("response {}", resp);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			LOGGER.error("Exception while getGstnbyEntityId ", ex);
			resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(value = "/getplantCodebyEntityId", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getplantCode(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Gson gson = GsonUtil.gsonInstanceWithEWB24HRFormat();
		JsonObject resp = null;
		try {

			String entityId = request.getParameter("entityId");
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Entity Id ", entityId);
			}
			FileStatusReportDto fileStatusDto = new FileStatusReportDto();
			List<Long> entityIds = new ArrayList<>();
			entityIds.add(Long.valueOf(entityId));
			fileStatusDto.setEntityId(entityIds);
			fileStatusDto.setDataType("outward");
			FileStatusReportDto statusReportDto = basicCommonSecParamAsyncReports
					.setDataSecuritySearchParams(fileStatusDto);
			Map<String, List<String>> dataSecReqMap = statusReportDto
					.getDataSecAttrs();
			List<String> plantList = dataSecReqMap
					.get(OnboardingConstant.PLANT);

			if (plantList == null) {

				resp = new JsonObject();
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", gson.toJsonTree(
						"No PlantCode Available for the Selected Entity"));
				return new ResponseEntity<>(resp.toString(),
						HttpStatus.INTERNAL_SERVER_ERROR);

			}
			resp = new JsonObject();
			JsonArray gstinArray = new JsonArray();
			JsonObject finalObj = new JsonObject();
			for (int i = 0; i < plantList.size(); i++) {
				JsonObject gstinObj = new JsonObject();
				gstinObj.addProperty("plantcode", plantList.get(i));
				gstinArray.add(gstinObj);
			}
			finalObj.add("plantdata", gstinArray);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", finalObj);
			LOGGER.debug("response {}", resp);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			LOGGER.error("Exception while saveB2COnboardingParams ", ex);
			resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(value = "/getprofitCenterbyEntityId", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getprofitCenterbyEntityId(
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Gson gson = GsonUtil.gsonInstanceWithEWB24HRFormat();
		JsonObject resp = null;
		try {

			String entityId = request.getParameter("entityId");
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Entity Id ", entityId);
			}
			FileStatusReportDto fileStatusDto = new FileStatusReportDto();
			List<Long> entityIds = new ArrayList<>();
			entityIds.add(Long.valueOf(entityId));
			fileStatusDto.setEntityId(entityIds);
			fileStatusDto.setDataType("outward");
			FileStatusReportDto statusReportDto = basicCommonSecParamAsyncReports
					.setDataSecuritySearchParams(fileStatusDto);
			Map<String, List<String>> dataSecReqMap = statusReportDto
					.getDataSecAttrs();
			List<String> profitCenterList = dataSecReqMap
					.get(OnboardingConstant.PC);

			if (profitCenterList == null) {
				resp = new JsonObject();
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", gson.toJsonTree(
						"No Profit Center Available for the Selected Entity"));
				return new ResponseEntity<>(resp.toString(),
						HttpStatus.INTERNAL_SERVER_ERROR);

			}
			resp = new JsonObject();
			JsonArray gstinArray = new JsonArray();
			JsonObject finalObj = new JsonObject();
			for (int i = 0; i < profitCenterList.size(); i++) {
				JsonObject gstinObj = new JsonObject();
				gstinObj.addProperty("profitCenter", profitCenterList.get(i));
				gstinArray.add(gstinObj);
			}
			finalObj.add("profitCenterData", gstinArray);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", finalObj);
			LOGGER.debug("response {}", resp);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			LOGGER.error("Exception while getprofitCenterbyEntityId ", ex);
			resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
