package com.ey.advisory.controller.inward.einvoice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.inward.einvoice.InwardEinvoiceDetailedInfoResponseDto;
import com.ey.advisory.app.inward.einvoice.InwardEinvoiceEntitySummaryReqDto;
import com.ey.advisory.app.inward.einvoice.InwardEinvoiceEntitySummaryResponseDto;
import com.ey.advisory.app.inward.einvoice.InwardEinvoiceEntitySummaryService;
import com.ey.advisory.app.inward.einvoice.InwardEinvoiceStatusScreenResponseDto;
import com.ey.advisory.app.util.DataSecurityAttributeUtil;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author vishal.verma
 *
 */

@Slf4j
@RestController
public class InwardEInvoiceEntitySummaryController {

	@Autowired
	@Qualifier("InwardEinvoiceEntitySummaryServiceImpl")
	private InwardEinvoiceEntitySummaryService service;
	
	@Autowired
	private GSTNDetailRepository gstinDetailRepo;
	
	private static final String REGULAR = "REGULAR";
	private static final String SEZ = "SEZ";
	private static final String SEZU = "SEZU";
	private static final String SEZD = "SEZD";
	private static final String ISD = "ISD";

	@RequestMapping(value = "/ui/inwardEinvoiceEntitySummary", method 
			= RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> summaryInitiateRecon(
			@RequestBody String jsonString) {

		JsonObject requestObject = JsonParser.parseString(jsonString)
				.getAsJsonObject();

		JsonObject jsonObj = new JsonObject();

		JsonObject reqJson = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();

		try {

			InwardEinvoiceEntitySummaryReqDto criteria = gson.fromJson(reqJson,
					InwardEinvoiceEntitySummaryReqDto.class);
			
			List<String> regTypeList = Arrays.asList(REGULAR, SEZ, SEZU, SEZD, 
					ISD);

			if (CollectionUtils.isEmpty(criteria.getGstins())) {
				List<String> gstnsList = new ArrayList<>();

				List<Long> entityIds = new ArrayList<>();
				entityIds.add(Long.valueOf(criteria.getEntityId()));
				Map<String, String> outwardSecurityAttributeMap = 
						DataSecurityAttributeUtil
						.getOutwardSecurityAttributeMap();
				Map<String, List<String>> dataSecAttrs = DataSecurityAttributeUtil
						.dataSecurityAttrMapForQuery(entityIds,
								outwardSecurityAttributeMap);
				List<String> dataSecList = dataSecAttrs
						.get(OnboardingConstant.GSTIN);
				
				gstnsList = gstinDetailRepo
						.filterGstinBasedByRegType(dataSecList, regTypeList);

				criteria.setGstins(gstnsList);
			}
			// Executing the service method and getting the result.

			if (criteria.getType().equalsIgnoreCase("EntityLevel")) {
				List<InwardEinvoiceEntitySummaryResponseDto> recResponse = 
						service.findTableData(criteria);

				JsonElement respBody = gson.toJsonTree(recResponse);
				jsonObj.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				jsonObj.add("resp", respBody);
				
			} else if (criteria.getType().equalsIgnoreCase("DetailedLevel")) {
				List<InwardEinvoiceDetailedInfoResponseDto> recResponse =
						service.findTableDetailedData(criteria);

				JsonElement respBody = gson.toJsonTree(recResponse);
				jsonObj.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				jsonObj.add("resp", respBody);
				
			} else if (criteria.getType().equalsIgnoreCase("SectionLevel")) {
				InwardEinvoiceStatusScreenResponseDto recResponse =
						service.findStatusData(criteria);

				JsonElement respBody = gson.toJsonTree(recResponse);
				jsonObj.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				jsonObj.add("resp", respBody);
				
			}
			return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
			
		} catch (JsonParseException ex) {
			String msg = "InwardEInvoiceEntitySummaryController- "
					+ "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "InwardEInvoiceEntitySummaryController "
					+ "Unexpected error while retriving Data Status ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

}
