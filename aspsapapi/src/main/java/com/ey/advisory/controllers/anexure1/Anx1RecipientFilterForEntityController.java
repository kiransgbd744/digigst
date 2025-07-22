package com.ey.advisory.controllers.anexure1;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.anx1.recipientsummary.RecipientService;
import com.ey.advisory.app.anx1.recipientsummary.RecipientSummaryFilterDto;
import com.ey.advisory.app.util.DataSecurityAttributeUtil;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.gstr2.userdetails.EntityService;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun KA
 *
 */

@Slf4j
@RestController
public class Anx1RecipientFilterForEntityController { 
	
	@Autowired
	@Qualifier("RecipientServiceImpl")
	RecipientService recipientService;
	
	@Autowired
	@Qualifier("EntityServiceImpl")
	EntityService entityService;
	
	@PostMapping(value = "/ui/getRecipientFilterForEntity", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getRecipientFilterForEntity(
			@RequestBody String jsonString) {

		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = "Begin RecipientFilterForEntityController"
						+ ".getRecipientFilterForEntity ,Parsing Input request";
				LOGGER.debug(msg);
			}
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			Gson gson = GsonUtil.newSAPGsonInstance();
			Long entityId = requestObject.get("entityId").getAsLong();
			String taxPeriod = requestObject.get("taxPeriod").getAsString();
			
			/*List<String> sgstins = 
					entityService.getGSTINsForEntity(entityId);*/
			
			 Map<String, List<String>> dataSecurityAttrMap = new HashMap<>();
			  Map<String, String> outwardSecurityAttributeMap =
			  DataSecurityAttributeUtil .getOutwardSecurityAttributeMap();
			  dataSecurityAttrMap = DataSecurityAttributeUtil
			  .dataSecurityAttrMapForQuery( Arrays.asList(entityId),
			  outwardSecurityAttributeMap);
			  
			  List<String> sgstins = 
					  dataSecurityAttrMap.get(OnboardingConstant.GSTIN) ;
			
			/*List<GstinDto> gstinDtoList = sgstins.stream()
					.map(GstinDto::new).collect(Collectors.toList());*/
			if(sgstins.isEmpty() || sgstins == null)
				throw new AppException("No SGSTINS available for the "
						+ " provided entity");
			
			List<RecipientSummaryFilterDto> respList = 
					recipientService.getCgstinsForGstins(sgstins, taxPeriod);
			
			/*List<GstinDto> cgstinDtoList = cgstins.stream()
					.map(GstinDto::new).collect(Collectors.toList());
			
			List<String> cPans = cgstins.stream()
					.map(o -> o.substring(2, 12))
					.distinct()
					.collect(Collectors.toList());
			
			List<PanDto> panDtoList = cPans.stream()
					.map(PanDto::new).collect(Collectors.toList());
			
			RecipientFilterForEntityDto rfed = 
					new RecipientFilterForEntityDto(gstinDtoList, panDtoList ,
							cgstinDtoList);*/

			JsonObject resp = new JsonObject();
			JsonObject detResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(respList);
			detResp.add("det", respBody);

			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", detResp);
			if (LOGGER.isDebugEnabled()) {
				String msg = "End getRecipientResponseDetails"
					+ ".getRecipientResponseDetails, before returning response";
				LOGGER.debug(msg);
			}

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {

			return InputValidationUtil.createJsonErrResponse(ex);
		}

	}
}