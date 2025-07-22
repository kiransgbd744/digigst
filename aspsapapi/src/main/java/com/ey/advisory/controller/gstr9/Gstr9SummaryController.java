package com.ey.advisory.controller.gstr9;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.services.gstr9.Gstr9SummaryService;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9HsnSummaryDetailsDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9SummaryDto;
import com.ey.advisory.app.util.DataSecurityAttributeUtil;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
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
 * @author Rajesh NK
 *
 */
@Slf4j
@RestController
public class Gstr9SummaryController {
	
	@Autowired
	private GSTNDetailRepository gstinDetailRepo;
	
	@Autowired
	private Gstr9SummaryService gstr9SummService;
	
	private static final String REGULAR = "REGULAR";
	private static final String SEZ = "SEZ";
	private static final String TDS = "TDS";
	private static final String ISD = "ISD";
	private static final String SEZU = "SEZU";
	private static final String SEZD = "SEZD";
	
	@PostMapping(value = "/ui/gstr9Summary", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> gstr9SummaryData(
			@RequestBody String jsonString) {

		JsonObject errorResp = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		List<String> gstnsLists = null;

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		LOGGER.debug("The selected criteria for Gstr9 Summary data is:->"
				+ requestObject.get("req"));
		try {

			String reqJson = requestObject.get("req").getAsJsonObject()
					.toString();
			Gstr9HsnSummaryDetailsDto req = gson.fromJson(reqJson.toString(),
					Gstr9HsnSummaryDetailsDto.class);

			String fyOld = req.getFy();
			String formattedFy = GenUtil.getFormattedFy(fyOld);
			
			List<String> gstinList = req.getListGstins();
			Long entityId = req.getEntityId();

			List<Long> entityIds = new ArrayList<>();
			entityIds.add(entityId);

			if (gstinList != null && !gstinList.isEmpty()) {

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"As gstins are provided in request, not Invoking"
									+ "service to get gstins for entity");
				}
				gstnsLists = gstinList;
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
					List<String> regTypeList = Arrays.asList(REGULAR, SEZ, SEZU,
					SEZD);
					gstnsLists = gstinDetailRepo
					.filterGstinBasedByRegType(dataSecList, regTypeList);

			}
			if (gstnsLists == null || gstnsLists.isEmpty()) {
				String msg = "Gstins cannot be empty";
				errorResp.add("hdr",
						new Gson().toJsonTree(new APIRespDto("E", msg)));
				LOGGER.error(msg);
				return new ResponseEntity<>(errorResp.toString(),
						HttpStatus.OK);
			}

			List<Gstr9SummaryDto> resp1 = gstr9SummService
					.listGstr9ummary(gstnsLists, formattedFy);

			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(resp1);	
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json payload";
			LOGGER.error(msg, ex);
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(errorResp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);

		} catch (AppException ex) {
			String msg = ex.getMessage();
			LOGGER.error(ex.getMessage(), ex);
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(errorResp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Data Status ";
			LOGGER.error(msg, ex);
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(errorResp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}

}
