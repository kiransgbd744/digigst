package com.ey.advisory.controller.inward.einvoice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.inward.einvoice.EinvoiceNestedDetailedReportRequestDto;
import com.ey.advisory.app.inward.einvoice.InwardEinvoiceInvMngtPRTaggingService;
import com.ey.advisory.app.inward.einvoice.InwardEinvoiceInvMngtReqDto;
import com.ey.advisory.app.inward.einvoice.InwardEinvoiceInvMngtRespDto;
import com.ey.advisory.app.inward.einvoice.InwardEinvoiceInvMngtService;
import com.ey.advisory.app.inward.einvoice.InwardEinvoiceInvMngtTabLayoutRespDto;
import com.ey.advisory.app.util.DataSecurityAttributeUtil;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Sakshi.jain
 *
 */

@Slf4j
@RestController
public class InwardEInvoiceInvMngtController {

	@Autowired
	@Qualifier("InwardEinvoiceInvMngtServiceImpl")
	private InwardEinvoiceInvMngtService invService;

	@Autowired
	@Qualifier("InwardEinvoiceInvMngtPRTaggingServiceImpl")
	private InwardEinvoiceInvMngtPRTaggingService invPrService;

	@RequestMapping(value = "/ui/getInwardIrnInvMngtTableData", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> inwardIrnInvMngtTableData(
			@RequestBody String jsonString) {

		JsonObject requestObject = JsonParser.parseString(jsonString)
				.getAsJsonObject();

		JsonObject hdrJson = requestObject.get("hdr").getAsJsonObject();
		int pageNum = hdrJson.get("pageNum").getAsInt();
		int pageSize = hdrJson.get("pageSize").getAsInt();

		JsonObject reqJson = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();

		try {

			InwardEinvoiceInvMngtReqDto criteria = gson.fromJson(reqJson,
					InwardEinvoiceInvMngtReqDto.class);

			if (CollectionUtils.isEmpty(criteria.getGstins())) {
				List<String> gstnsList = new ArrayList<>();

				List<Long> entityIds = new ArrayList<>();
				entityIds.add(Long.valueOf(criteria.getEntityId().get(0)));
				Map<String, String> outwardSecurityAttributeMap = DataSecurityAttributeUtil
						.getOutwardSecurityAttributeMap();
				Map<String, List<String>> dataSecAttrs = DataSecurityAttributeUtil
						.dataSecurityAttrMapForQuery(entityIds,
								outwardSecurityAttributeMap);
				gstnsList = dataSecAttrs.get(OnboardingConstant.GSTIN);

				criteria.setGstins(gstnsList);
			}
			// Executing the service method and getting the result.
			Pair<List<InwardEinvoiceInvMngtRespDto>, Integer> recResponse = invService
					.findTableData(criteria, pageNum, pageSize);

			JsonObject resps = new JsonObject();
			resps.add("hdr",
					gson.toJsonTree(APIRespDto.createSuccessResp(
							recResponse.getValue1(), pageNum, pageSize, null,
							null)));
			resps.add("resp", gson.toJsonTree(recResponse.getValue0()));
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

	@RequestMapping(value = "/ui/getInwardIrnInvMngtTabLayoutData", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> inwardIrnInvMngtTablayoutData(
			@RequestBody String jsonString) {

		JsonObject requestObject = JsonParser.parseString(jsonString)
				.getAsJsonObject();

		JsonObject reqJson = requestObject.get("req").getAsJsonObject();

		String irn = reqJson.get("irnNum").getAsString();

		Gson gson = GsonUtil.newSAPGsonInstance();

		try {

			// Executing the service method and getting the result.
			InwardEinvoiceInvMngtTabLayoutRespDto recResponse = invService
					.findTabLayoutData(irn);

			JsonObject resps = new JsonObject();
			JsonObject resp = new JsonObject();

			resp.add("AttribDtls",
					gson.toJsonTree(recResponse.getAttribDtls()));

			resp.add("PrecDocDtls",
					gson.toJsonTree(recResponse.getPrecDocDtls()));

			resp.add("ContrDtls", gson.toJsonTree(recResponse.getContrDtls()));

			resp.add("AddlDocDtls",
					gson.toJsonTree(recResponse.getAddlDocDtls()));

			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", resp);
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

	// new api to take the listing ids as long
	@RequestMapping(value = "/ui/updatePRTagging", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> updatePRTagging(
			@RequestBody String jsonString) {

		JsonObject requestObject = JsonParser.parseString(jsonString)
				.getAsJsonObject();

		JsonObject reqJson = requestObject.get("req").getAsJsonObject();

		Gson gson = new Gson();
		EinvoiceNestedDetailedReportRequestDto reqDto = gson.fromJson(reqJson,
				EinvoiceNestedDetailedReportRequestDto.class);

		try {

			String response = invPrService.updatePRTagging(reqDto);
			JsonObject resp = new JsonObject();

			if ("SUCCESS".equalsIgnoreCase(response)) {

				resp.add("hdr", new Gson().toJsonTree(
						new APIRespDto("S", "Updated Successfully")));
			} else {
				resp.add("hdr",
						new Gson().toJsonTree(new APIRespDto("E", "Failed")));
				
			}
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			// print the error log
			LOGGER.error("Exception in updatePRTagging");
			JsonObject resp = new JsonObject();
			resp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", "Failed")));
			resp.add("resp", new Gson().toJsonTree(ex.getMessage()));

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		}

	}
}