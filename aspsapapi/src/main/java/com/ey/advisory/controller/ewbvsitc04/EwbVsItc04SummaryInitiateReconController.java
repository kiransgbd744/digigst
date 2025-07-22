package com.ey.advisory.controller.ewbvsitc04;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.reconewbvsitc04.EwbVsItc04SummaryInitiateReconDto;
import com.ey.advisory.app.reconewbvsitc04.EwbVsItc04SummaryInitiateReconLineItemDto;
import com.ey.advisory.app.reconewbvsitc04.EwbvsItc04SummaryInitiateReconService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr2ReconSummaryStatusDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Ravindra V S
 *
 */

@Slf4j
@RestController
public class EwbVsItc04SummaryInitiateReconController {
	
	@Autowired
	@Qualifier("GSTNDetailRepository")
	GSTNDetailRepository entityRepository;

	@Autowired
	@Qualifier("EwbvsItc04SummaryInitiateReconService")
	private EwbvsItc04SummaryInitiateReconService reconService;

	@RequestMapping(value = "/ui/ewbVsItc04SummaryInitiateRecon", method = 
			RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> ewb3WaySummaryInitiateRecon(
			@RequestBody String jsonString) {

		JsonObject requestObject = JsonParser.parseString(jsonString)
				.getAsJsonObject();

		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		
		try {

			EwbVsItc04SummaryInitiateReconDto criteria = gson.fromJson(json, 
					EwbVsItc04SummaryInitiateReconDto.class);
			
			if (CollectionUtils.isEmpty(criteria.getSgstins()))
				throw new AppException("User has not selected any gstin");
			
			List<EwbVsItc04SummaryInitiateReconLineItemDto> recResponse = reconService
					.find(criteria);
			
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(recResponse);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

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
	
	@RequestMapping(value = "/ui/ewbVsItc04SummaryReconReport", method = 
			RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> ewb3WaySummaryReconReport(
			@RequestBody String jsonString,  HttpServletResponse response) {

		JsonObject requestObject = JsonParser.parseString(jsonString)
				.getAsJsonObject();
		
		String fileName = null;
		Workbook workbook = null;
		JsonObject dataObj = new JsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		
		try {

			EwbVsItc04SummaryInitiateReconDto criteria = gson.fromJson(json, 
					EwbVsItc04SummaryInitiateReconDto.class);
			
			if (CollectionUtils.isEmpty(criteria.getSgstins()))
				throw new AppException("User has not selected any gstin");
			
			workbook = reconService
					.getReport(criteria);
			
			String date = null;
			String time = null;
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			LocalDateTime istDateTimeFromUTC = EYDateUtil
					.toISTDateTimeFromUTC(now);
			DateTimeFormatter FOMATTER = DateTimeFormatter
					.ofPattern("yyyyMMdd");

			DateTimeFormatter FOMATTER1 = DateTimeFormatter
					.ofPattern("HHmmssms");
			date = FOMATTER.format(istDateTimeFromUTC);
			time = FOMATTER1.format(istDateTimeFromUTC).substring(0, 9);

			fileName = "EWBVsITC04Summary" + "_" + date + "T" + time;

			if (workbook != null) {
				response.setContentType("APPLICATION/OCTET-STREAM");
				response.setHeader("Content-Disposition", String
						.format("attachment; filename=" + fileName + ".xlsx"));
				workbook.save(response.getOutputStream(), SaveFormat.XLSX);
				response.getOutputStream().flush();
				return new ResponseEntity<>(null, HttpStatus.OK);
			} else {
				dataObj.add("hdr",
						gson.toJsonTree(APIRespDto.creatErrorResp()));
				dataObj.addProperty("resp", "No Data Available");
				return new ResponseEntity<>(dataObj.toString(), HttpStatus.OK);
			}

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

	@PostMapping(value = "/ui/getDataForReconEwbVsItc04Summary", produces = {
			MediaType.APPLICATION_JSON_VALUE }, consumes = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getDataForRecon3WaySummary(
			@RequestBody String reqJson) {

		JsonObject requestObject = JsonParser.parseString(reqJson)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		
		try
		{
			EwbVsItc04SummaryInitiateReconDto criteria = gson.fromJson(json, 
					EwbVsItc04SummaryInitiateReconDto.class);

			List<Gstr2ReconSummaryStatusDto> respObj = reconService
					.getReconEwbVsItc04DetailSummaryStatus(criteria);

			//sorting of gstins
			if(!respObj.isEmpty() && respObj.size() > 0){
				respObj.sort(Comparator.comparing(Gstr2ReconSummaryStatusDto::getGstin));
			}
			
			
			JsonObject resp = new JsonObject();
			JsonObject detResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(respObj);
			detResp.add("det", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", detResp);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("EwbVsItc04SummaryInitiateReconController", e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error in EwbVsItc04SummaryInitiateReconController";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

}
