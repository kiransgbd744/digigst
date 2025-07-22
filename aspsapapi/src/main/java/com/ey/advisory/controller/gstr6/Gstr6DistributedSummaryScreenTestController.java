package com.ey.advisory.controller.gstr6;

import java.lang.reflect.Type;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.repositories.client.Gstr6DistributionRepository;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6DistributedSummaryScreenRequestDto;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6DistributedSummaryScreenResponseDto;
import com.ey.advisory.app.docs.service.gstr6.Gstr6DistributedSummaryService;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

/**
 * 
 * @author Dibyakanta.Sahoo
 *
 */
@RestController
public class Gstr6DistributedSummaryScreenTestController {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr6DistributedSummaryScreenTestController.class);

	@Autowired
	@Qualifier("Gstr6DistributionRepository")
	private Gstr6DistributionRepository gstr6DistributionRepository;

	@Autowired
	@Qualifier("Gstr6DistributedSummaryServiceImpl")
	private Gstr6DistributedSummaryService gstr6DistributedSummaryScreenService;

	@Autowired
	@Qualifier("Gstr6DistributionRepository")
	private Gstr6DistributionRepository gstr6DistributionSummaryRepository;

	@PostMapping(value = "/ui/gstr6distributed/summary1", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstr6DistributedSummaryData1(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			JsonObject requestObject = JsonParser.parseString(jsonString)
					.getAsJsonObject();
			JsonObject reqObj = requestObject.get("req").getAsJsonObject();

			Annexure1SummaryReqDto reqDto = gson.fromJson(reqObj,
					Annexure1SummaryReqDto.class);
			JsonObject hdrObject = requestObject.getAsJsonObject("hdr");
			int pageNum = hdrObject.get("pageNum").getAsInt();
			int pageSize = hdrObject.get("pageSize").getAsInt();
			int totalCount = 0;
			Pageable pageReq = PageRequest.of(pageNum, pageSize, Direction.DESC,
					"id");

			List<Gstr6DistributedSummaryScreenResponseDto> responseData = gstr6DistributedSummaryScreenService
					.getGstr6DistributedEliSummaryListPagination(reqDto,
							pageReq);

			totalCount = gstr6DistributedSummaryScreenService
					.getGstr6DistributedEliSummaryListCount(reqDto);
			
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(responseData);
			resps.add("hdr",
					gson.toJsonTree(APIRespDto.createSuccessResp(totalCount,
							pageNum, pageSize, "S", "Successfully fetched gstr6 Request records")));
			resps.add("resp", respBody);
			return new ResponseEntity<String>(resps.toString(), HttpStatus.OK);
		} catch (JsonParseException ex) {
			LOGGER.error("Error while parsing the input Json:{}", ex);
			resp.add("hdr", new Gson()
					.toJsonTree(new APIRespDto("E", ex.getMessage())));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			LOGGER.error("Unexpected error while retriving Data Status:{} ",
					ex);
			resp.add("hdr", new Gson()
					.toJsonTree(new APIRespDto("E", ex.getMessage())));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(value = "/ui/gstr6distributed/summary2", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstr6DistributedSummaryData2(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			JsonObject requestObject = JsonParser.parseString(jsonString)
					.getAsJsonObject();
			JsonObject reqObj = requestObject.get("req").getAsJsonObject();

			Annexure1SummaryReqDto reqDto = gson.fromJson(reqObj,
					Annexure1SummaryReqDto.class);
			JsonObject hdrObject = requestObject.getAsJsonObject("hdr");
			int pageNum = hdrObject.get("pageNum").getAsInt();
			int pageSize = hdrObject.get("pageSize").getAsInt();
			int totalCount = 0;
			Pageable pageReq = PageRequest.of(pageNum, pageSize, Direction.DESC,
					"id");
	
			List<Gstr6DistributedSummaryScreenResponseDto> responseData2 = gstr6DistributedSummaryScreenService
					.getGstr6DistributedInEliSummaryListPagination(reqDto,
							pageReq);

			totalCount = gstr6DistributedSummaryScreenService
					.getGstr6DistributedInEliSummaryListCount(reqDto);
			
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(responseData2);
			resps.add("hdr",
					gson.toJsonTree(APIRespDto.createSuccessResp(totalCount,
							pageNum, pageSize, "S", "Successfully fetched gstr6 Request records")));
			resps.add("resp", respBody);
			return new ResponseEntity<String>(resps.toString(), HttpStatus.OK);
		} catch (JsonParseException ex) {
			LOGGER.error("Error while parsing the input Json:{}", ex);
			resp.add("hdr", new Gson()
					.toJsonTree(new APIRespDto("E", ex.getMessage())));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			LOGGER.error("Unexpected error while retriving Data Status:{} ",
					ex);
			resp.add("hdr", new Gson()
					.toJsonTree(new APIRespDto("E", ex.getMessage())));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}

	@PostMapping(value = "/ui/gstr6distributed/summary3", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstr6DistributedSummaryData3(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			JsonObject requestObject = JsonParser.parseString(jsonString)
					.getAsJsonObject();
			JsonObject reqObj = requestObject.get("req").getAsJsonObject();

			Annexure1SummaryReqDto reqDto = gson.fromJson(reqObj,
					Annexure1SummaryReqDto.class);
			JsonObject hdrObject = requestObject.getAsJsonObject("hdr");
			int pageNum = hdrObject.get("pageNum").getAsInt();
			int pageSize = hdrObject.get("pageSize").getAsInt();
			int totalCount = 0;
			Pageable pageReq = PageRequest.of(pageNum, pageSize, Direction.DESC,
					"id");
		
			List<Gstr6DistributedSummaryScreenResponseDto> responseData3 = gstr6DistributedSummaryScreenService
					.getGstr6ReDistributedSummaryListPagination(reqDto,
							pageReq);
			totalCount = gstr6DistributedSummaryScreenService
					.getGstr6ReDistributedSummaryListCount(reqDto);
			
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(responseData3);
			resps.add("hdr",
					gson.toJsonTree(APIRespDto.createSuccessResp(totalCount,
							pageNum, pageSize, "S", "Successfully fetched gstr6 Request records")));
			resps.add("resp", respBody);
			return new ResponseEntity<String>(resps.toString(), HttpStatus.OK);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Data Status ";
			LOGGER.error(msg, ex);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}

	@PostMapping(value = "/ui/gstr6distributed/summary4", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstr6DistributedSummaryData4(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			JsonObject requestObject = JsonParser.parseString(jsonString)
					.getAsJsonObject();
			JsonObject reqObj = requestObject.get("req").getAsJsonObject();

			Annexure1SummaryReqDto reqDto = gson.fromJson(reqObj,
					Annexure1SummaryReqDto.class);
			JsonObject hdrObject = requestObject.getAsJsonObject("hdr");
			int pageNum = hdrObject.get("pageNum").getAsInt();
			int pageSize = hdrObject.get("pageSize").getAsInt();
			int totalCount = 0;
			Pageable pageReq = PageRequest.of(pageNum, pageSize, Direction.DESC,
					"id");
			
			List<Gstr6DistributedSummaryScreenResponseDto> responseData4 = gstr6DistributedSummaryScreenService
					.getGstr6ReDistributedInEligibleSummaryListPagination(
							reqDto, pageReq);
			totalCount = gstr6DistributedSummaryScreenService
					.getGstr6ReDistributedInEligibleSummaryListCount(reqDto);
			
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(responseData4);
			resps.add("hdr",
					gson.toJsonTree(APIRespDto.createSuccessResp(totalCount,
							pageNum, pageSize, "S", "Successfully fetched gstr6 Request records")));
			resps.add("resp", respBody);
			return new ResponseEntity<String>(resps.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Data Status ";
			LOGGER.error(msg, ex);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}

	@PostMapping(value = "/ui/deleteSummary/id", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getDeleteSummaryData(

			@RequestBody String jsonString) {

		JsonArray jsonObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject().get("req").getAsJsonArray();
		Gson gson = GsonUtil.newSAPGsonInstance();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Request data begin");
		}
		JsonObject resps = new JsonObject();
		try {
			Type listType = new TypeToken<List<Gstr6DistributedSummaryScreenRequestDto>>() {
			}.getType();
			List<Gstr6DistributedSummaryScreenRequestDto> entityId = gson
					.fromJson(jsonObject, listType);
			gstr6DistributedSummaryScreenService.deleteEntity(entityId);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			return new ResponseEntity<String>(resps.toString(), HttpStatus.OK);

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

	@PostMapping(value = "/ui/saveGstr6DistributedSummary", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveGstr6SummaryData1(
			@RequestBody String jsonString) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonArray jsonObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().get("req").getAsJsonArray();
			Type listType = new TypeToken<List<Gstr6DistributedSummaryScreenRequestDto>>() {
			}.getType();
			List<Gstr6DistributedSummaryScreenRequestDto> saveData = gson
					.fromJson(jsonObject, listType);
			gstr6DistributedSummaryScreenService
					.saveGstr6DistributedSummaryData(saveData);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			LOGGER.error(
					"Unexpected error while saving Gstr6 Summary Documents ",
					ex);
			resp.add("hdr", new Gson()
					.toJsonTree(new APIRespDto("E", ex.getMessage())));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}