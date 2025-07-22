package com.ey.advisory.controller.recon.response;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.app.asprecon.gstr2.recon.result.Gstr2ReconResultReqDto;
import com.ey.advisory.app.asprecon.gstr2.recon.result.Gstr2ReconResultValidations;
import com.ey.advisory.app.asprecon.reconresponse.Gstr2ReconResponseDashboardDto;
import com.ey.advisory.app.asprecon.reconresponse.Gstr2ReconResponsePosDto;
import com.ey.advisory.app.asprecon.reconresponse.Gstr2ReconResponseService;
import com.ey.advisory.app.data.entities.client.StateCodeInfoEntityClient;
import com.ey.advisory.app.data.repositories.client.StatecodeRepository;
import com.ey.advisory.app.util.DataSecurityAttributeUtil;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author sakshi.jain
 *
 */

@Slf4j
@RestController
public class Gstr2ReconResponseDashboardController {

	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfigPemtRepo;

	@Autowired
	@Qualifier("Gstr2ReconResponseServiceImpl")
	private Gstr2ReconResponseService serviceDashboard;

	@Autowired
	private StatecodeRepository stateRepo;
	
	@Autowired
	private Gstr2ReconResultValidations validations;

	
	@RequestMapping(value = "/ui/gstr2getReconResponse", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getReconResponseData(
			@RequestBody String jsonString) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		JsonObject hdrObject = requestObject.getAsJsonObject("hdr");
		int pageNum = hdrObject.get("pageNum").getAsInt();
		int pageSize = hdrObject.get("pageSize").getAsInt();
		Integer totalCount = 0;
		Gson gson = GsonUtil.newSAPGsonInstance();

		List<String> gstnsList = null;
		List<Gstr2ReconResponseDashboardDto> recResponse = new ArrayList<>();
		JsonObject json = requestObject.get("req").getAsJsonObject();

		String msg = String
				.format("Inside Gstr2ReconResponseDashboardController.getReconResponseData() "
						+ "method : %s {} ", jsonString);
		LOGGER.debug(msg);
		try {

			Gstr2ReconResultReqDto reqDto = gson.fromJson(json,
					Gstr2ReconResultReqDto.class);
			Long entityId = Long.valueOf(reqDto.getEntityId());

			if (reqDto.getGstins().isEmpty()) {

				if (LOGGER.isDebugEnabled()) {
					msg = String.format(
							"Invoking service to get gstins for entity %s ",
							reqDto);
					LOGGER.debug(msg);
				}

				try {
					List<Long> entityIds = new ArrayList<>();
					entityIds.add(entityId);
					Map<String, String> outwardSecurityAttributeMap = DataSecurityAttributeUtil
							.getOutwardSecurityAttributeMap();
					Map<String, List<String>> dataSecAttrs = DataSecurityAttributeUtil
							.dataSecurityAttrMapForQuery(entityIds,
									outwardSecurityAttributeMap);
					gstnsList = dataSecAttrs.get(OnboardingConstant.GSTIN);

					reqDto.setGstins(gstnsList);
				} catch (Exception ex) {
					msg = String.format(
							"Error while fetching Gstins from entityId %s", ex);
					LOGGER.error(msg);
					throw new AppException(msg);
				}

			}
			
		
			JsonObject resps = new JsonObject();
			Pair<List<Gstr2ReconResponseDashboardDto>, Integer> resp = null;
			
			Optional<List<String>> optionalVendorGstins = Optional
					.ofNullable(reqDto.getVendorGstins());
			if (optionalVendorGstins.isPresent()) {

				List<String> vendorGstins = optionalVendorGstins.get();
				String vendorGstinString = String.join(",", vendorGstins);
				if (vendorGstinString.length() > 4000) {
					APIRespDto respdto = new APIRespDto("Failed",
							"Vendor GSTIN has exceeded the limit of 3000 characters");
					JsonObject response = new JsonObject();
					JsonElement respBody = gson.toJsonTree(respdto);
					 msg = "Vendor GSTIN has exceeded the limit of 3000 characters";
					response.add("hdr",
							new Gson().toJsonTree(new APIRespDto("E", msg)));
					response.add("resp", respBody);
					// LOGGER.error(msg, e);
					return new ResponseEntity<String>(response.toString(),
							HttpStatus.OK);
				}

			}
			
			Optional<List<String>> optionalDocNum = Optional
					.ofNullable(reqDto.getDocNumber());
			if (optionalDocNum.isPresent()) {

				List<String> docNumber = optionalDocNum.get();
				String docNumberString = String.join(",", docNumber);
				if (docNumberString.length() > 4000) {
					APIRespDto respdto = new APIRespDto("Failed",
							"Document Numbers have exceeded the limit of 3000 characters");
					JsonObject response = new JsonObject();
					JsonElement respBody = gson.toJsonTree(respdto);
					 msg = "Document Numbers have exceeded the limit of 3000 characters";
					response.add("hdr",
							new Gson().toJsonTree(new APIRespDto("E", msg)));
					response.add("resp", respBody);
					// LOGGER.error(msg, e);
					return new ResponseEntity<String>(response.toString(),
							HttpStatus.OK);
				}

			}
			
			List<String> rptTyp = reqDto.getReportType();

			if (!reqDto.getReportType().isEmpty() && reqDto.getReportType()
					.contains("Doc Number & Doc Date Mismatch")) {
				for (String rptType : reqDto.getReportType()) {
					if ("Doc Number & Doc Date Mismatch"
							.equalsIgnoreCase(rptType))
						continue;
					else
						rptTyp.add(rptType);
				}
				rptTyp.add("Doc No & Doc Date Mismatch");
				reqDto.setReportType(rptTyp);
			}
			
			//ADDED BASIS US - User Story 143832: SCL | Recon Response | Change filter 'Vendor PAN/GSTIN' from dropdown to search box
			
			
			try{
			validations.validations(reqDto);
			}catch(Exception ex)
			{
				JsonObject resp1 = new JsonObject();
				resp1.add("hdr", new Gson().toJsonTree(new APIRespDto("E", ex.getMessage())));
				return new ResponseEntity<>(resp1.toString(),
						HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
			
			if(!reqDto.getVendorPans().isEmpty() && !reqDto.getVendorGstins().isEmpty())
			{
				reqDto.setVendorPans(new ArrayList<>());
			}
				
			
			resp = serviceDashboard.getReconResponseDashboardData(reqDto,
					pageNum, pageSize);

			if (resp == null) {
				resps.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp(totalCount,
								pageNum, pageSize, "S", "No Records Found")));
				resps.add("resp", gson.toJsonTree("No Records Found"));
			}

			else {
				recResponse = resp.getValue0();
				totalCount = resp.getValue1();
				JsonElement respBody = gson.toJsonTree(recResponse);
				resps.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp(totalCount,
								pageNum, pageSize, "S", "Successfully fetched Recon Results")));
				resps.add("resp", respBody);
			}

			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			msg = "Unexpected error while retriving Data Status ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", ex.getMessage())));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@RequestMapping(value = "/ui/gstr2getLastReconTimeStamp", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> gstr2getLastReconTimeStamp(
			@RequestBody String jsonString) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		Gson gson = GsonUtil.newSAPGsonInstance();

		JsonObject json = requestObject.get("req").getAsJsonObject();

		String msg = String
				.format("Inside Gstr2ReconResponseDashboardController.gstr2getLastReconTimeStamp() "
						+ "method : %s {} ", jsonString);
		LOGGER.debug(msg);
		try {
			String reconLastTime = LocalDateTime.now().toString();

			Gstr2ReconResultReqDto reqDto = gson.fromJson(json,
					Gstr2ReconResultReqDto.class);
			Long entityId = Long.valueOf(reqDto.getEntityId());
			String reconType = reqDto.getReconType();

			JsonObject resps = new JsonObject();
			Gstr2ReconResultReqDto resp = new Gstr2ReconResultReqDto();
			reconLastTime = serviceDashboard.getLastReconTimeStamp(entityId,
					reconType);
			resp.setTimeStamp(reconLastTime);

			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", gson.toJsonTree(reconLastTime));

			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			msg = "Unexpected error while retriving Data Status ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@RequestMapping(value = "/ui/gstr2GetPos", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> gstr2GetPos(@RequestBody String jsonString) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		Gson gson = GsonUtil.newSAPGsonInstance();

		List<String> gstnsList = null;
		JsonObject json = requestObject.get("req").getAsJsonObject();

		String msg = String
				.format("Inside Gstr2ReconResponseDashboardController.gstr2getLastReconTimeStamp() "
						+ "method : %s {} ", jsonString);
		LOGGER.debug(msg);
		try {

			Gstr2ReconResultReqDto reqDto = gson.fromJson(json,
					Gstr2ReconResultReqDto.class);

			Long entityId = Long.valueOf(reqDto.getEntityId());

			if (reqDto.getGstins().isEmpty()) {

				if (LOGGER.isDebugEnabled()) {
					msg = String.format(
							"Invoking service to get gstins for entity %s ",
							reqDto);
					LOGGER.debug(msg);
				}

				try {
					List<Long> entityIds = new ArrayList<>();
					entityIds.add(entityId);
					Map<String, String> outwardSecurityAttributeMap = DataSecurityAttributeUtil
							.getOutwardSecurityAttributeMap();
					Map<String, List<String>> dataSecAttrs = DataSecurityAttributeUtil
							.dataSecurityAttrMapForQuery(entityIds,
									outwardSecurityAttributeMap);
					gstnsList = dataSecAttrs.get(OnboardingConstant.GSTIN);

					reqDto.setGstins(gstnsList);
				} catch (Exception ex) {
					msg = String.format(
							"Error while fetching Gstins from entityId %s", ex);
					LOGGER.error(msg);
					throw new AppException(msg);
				}

			}

			List<StateCodeInfoEntityClient> stateEntity = stateRepo.findAll();
			List<Gstr2ReconResponsePosDto> posList = new ArrayList<>();
			if (!stateEntity.isEmpty()) {
				posList = stateEntity.stream().map(
						o -> new Gstr2ReconResponsePosDto(o.getStateCode()))
						.collect(Collectors.toCollection(ArrayList::new));
			}

			if (LOGGER.isDebugEnabled()) {
				msg = String.format("posList %s ", posList.toString());
				LOGGER.debug(msg);
			}

			posList.sort(
					Comparator.comparing(Gstr2ReconResponsePosDto::getPos));

			JsonObject resps = new JsonObject();
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", gson.toJsonTree(posList));

			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			msg = "Unexpected error while retriving Data Status ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
	
}
