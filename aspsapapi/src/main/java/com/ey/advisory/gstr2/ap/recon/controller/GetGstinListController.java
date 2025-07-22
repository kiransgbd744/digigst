/**
 * 
 */
package com.ey.advisory.gstr2.ap.recon.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.util.DataSecurityAttributeUtil;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr2ReconSummaryStatusDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@RestController
public class GetGstinListController {
	
	@Autowired
	private GSTNDetailRepository gstinDetailRepo;
	
	private static final String REGULAR = "REGULAR";
	private static final String SEZ = "SEZ";
	private static final String SEZU = "SEZU";
	private static final String SEZD = "SEZD";
	private static final String ISD = "ISD";
	
	@RequestMapping(value = "/ui/getGstinList", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstins(@RequestBody String jsonString) {

		LOGGER.debug("Inside GetGstinListController"
				+ ".getGstins() method and file type is {} ");

		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			JsonObject json = requestObject.get("req").getAsJsonObject();
			Gson gson = GsonUtil.newSAPGsonInstance();

			Long entityId = json.get("entityId").getAsLong();
			String src = json.has("src") ? json.get("src").getAsString() : null;
			List<String> gstins = new ArrayList<>();

			Map<String, List<String>> dataSecurityAttrMap = new HashMap<>();
			Map<String, String> inwardSecurityAttributeMap = DataSecurityAttributeUtil
					.getInwardSecurityAttributeMap();
			dataSecurityAttrMap = DataSecurityAttributeUtil
					.dataSecurityAttrMapForQuery(Arrays.asList(entityId),
							inwardSecurityAttributeMap);
			gstins = dataSecurityAttrMap.get("GSTIN");
			if (src != null && "GST_NOTICE".equalsIgnoreCase(src)) {
				List<Object[]> gstnObject = getFetchNotGstinList(entityId);
				Map<String, String> gstnRegMap = gstnObject.stream()
						.collect(Collectors.toMap(obj -> (String) obj[0],
								obj -> (String) obj[1]));
				gstins = gstnRegMap.keySet().stream()
						.collect(Collectors.toList());
			}
			
			List<Gstr2ReconSummaryStatusDto> list = gstins.stream()
					.map(o -> convert(o)).collect(Collectors.toList());
			
			list.sort(Comparator.comparing(Gstr2ReconSummaryStatusDto::getGstin));
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(list);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json : "
					+ "CheckForAPReconOptedController";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Data Status :"
					+ " GetGstinListController ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	private Gstr2ReconSummaryStatusDto convert(String o) {

		Gstr2ReconSummaryStatusDto dto = new Gstr2ReconSummaryStatusDto();
		dto.setGstin(o);

		return dto;
	}
	
	@RequestMapping(value = "/ui/getGstinListIrn", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getIrnGstins(@RequestBody String jsonString) {

		LOGGER.debug("Inside GetGstinListController"
				+ ".getGstins() method and file type is {} ");

		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			JsonObject json = requestObject.get("req").getAsJsonObject();
			Gson gson = GsonUtil.newSAPGsonInstance();

			Long entityId = json.get("entityId").getAsLong();

			List<String> regTypeList = Arrays.asList(REGULAR, SEZ, SEZU, SEZD, 
					ISD);
			List<String> gstnsList = new ArrayList<>();

			List<Long> entityIds = new ArrayList<>();
			entityIds.add(Long.valueOf(entityId));
			Map<String, String> inwardSecurityAttributeMap = 
						DataSecurityAttributeUtil
						.getInwardSecurityAttributeMap();
			Map<String, List<String>> dataSecAttrs = DataSecurityAttributeUtil
						.dataSecurityAttrMapForQuery(entityIds,
								inwardSecurityAttributeMap);
			List<String> dataSecList = dataSecAttrs
						.get(OnboardingConstant.GSTIN);
				
			gstnsList = gstinDetailRepo
						.filterGstinBasedByRegType(dataSecList, regTypeList);
			

			List<Gstr2ReconSummaryStatusDto> list = gstnsList.stream()
					.map(o -> convert(o)).collect(Collectors.toList());
			
			list.sort(Comparator.comparing(Gstr2ReconSummaryStatusDto::getGstin));
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(list);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json : "
					+ "CheckForAPReconOptedController";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Data Status :"
					+ " GetGstinListController ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
	
	public List<Object[]> getFetchNotGstinList(Long entityId) {

		List<Object[]> gstnObject = gstinDetailRepo
				.getGstinBasedOnRegTypeforACD(entityId, Arrays.asList("REGULAR",
						"SEZ", "SEZU", "SEZD", "TDS", "ISD", "TCS", "ECOM"));

		return gstnObject;
	}

}