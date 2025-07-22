/**
 * 
 */
package com.ey.advisory.app.anx2.vendorsummary;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.gstr2.userdetails.EntityService;
import com.ey.advisory.gstr2.userdetails.GstinDto;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Nikhil.Duseja
 *
 */

@Slf4j
@RestController
public class Anx2VendoerFilterForGstinController {
	
	@Autowired
	@Qualifier("VendorSgstinServiceImpl")
	VendoeSgstinService vendorSgstinService;
	
	@Autowired
	@Qualifier("EntityServiceImpl")
	EntityService entityService;
	
	@PostMapping(value = "/ui/getSupplierFilterForEntity", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getVendortFilterForGstin(
			@RequestBody String jsonString) {
		
		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = "Begin VendorFilterForGstinController"
						+ "getVendortFilterForGstin ,Parsing Input request";
				LOGGER.debug(msg);
			}
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			Gson gson = GsonUtil.newSAPGsonInstance();
			
			Long entityId = requestObject.get("entityId").getAsLong();
			
			JsonElement sgstinsEle= requestObject.get("gstins");
			Type listType = new TypeToken<List<String>>() {}.getType();
			
			List<String> gstins = new Gson().fromJson(sgstinsEle,listType);
			if(CollectionUtils.isEmpty(gstins)) {
				gstins = 
						entityService.getGSTINsForEntity(entityId);
			}
			String taxPeriod = requestObject.get("taxPeriod").getAsString();
			
			List<String> sgstins = 
					vendorSgstinService.getSgstinsforCgstins(gstins, taxPeriod);
			
			List<GstinDto> sgstinDtoList = sgstins.stream()
					.map(GstinDto::new).collect(Collectors.toList());
			
			List<String> sPans = sgstins.stream()
					.filter(o -> o.length() == 15)
					.map(o -> o.substring(2, 12))
					.distinct()
					.collect(Collectors.toList());
			
			List<PanDto> panDtoList = sPans.stream()
					.map(PanDto::new).collect(Collectors.toList());
			
			RecipientFilterForEntityDto rfed = 
					new RecipientFilterForEntityDto(sgstinDtoList, panDtoList ,
							null);

			JsonObject resp = new JsonObject();
			JsonObject detResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(rfed);
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
			String exMsg = ex.getMessage();
			String msg = null;
			
			if (ex instanceof AppException) {
				msg = exMsg;
			} else if (ex instanceof JsonParseException) {
				msg = String.format(
						"Error while parsing the request - [%s]", exMsg);
			} else {
				msg = String.format("Unexpected error occured - [%s]", exMsg);
			}
			
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson()
					.toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

}
