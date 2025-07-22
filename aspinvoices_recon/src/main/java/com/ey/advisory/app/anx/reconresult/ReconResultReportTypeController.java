/**
 * 
 */
package com.ey.advisory.app.anx.reconresult;

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
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.APIRespDto;
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
public class ReconResultReportTypeController {
	
	@Autowired
	@Qualifier("ReconResultReportTypeServiceImpl")
	ReconResultReportTypeService reconResultReportTypeService;

	@PostMapping(value = "/ui/getReconResultReportType", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getReconResultReportForGstin(
			@RequestBody String jsonString) {
		
		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = "Begin ReconResultReport FilterForGstinController"
						+ "getReconResultReportForGstin ,Parsing Input request";
				LOGGER.debug(msg);
			}
			int taxPeriod = 0;
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			JsonObject json = requestObject.get("req").getAsJsonObject();
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonElement sgstinsEle= json.get("gstins");
			Type listType = new TypeToken<List<String>>() {}.getType();
			List<String> gstins = new Gson().fromJson(sgstinsEle,listType);
			String returnPeriod = json.get("taxPeriod")
										.getAsString();
			if (returnPeriod == null ||
					returnPeriod.length() != 6) {
				String msg = "EntityId or TaxPeriod Cannot be Empty";
				throw new AppException(msg);
			}
			else
			{
				taxPeriod = GenUtil
						.convertTaxPeriodToInt(returnPeriod);
			}
			
			if(CollectionUtils.isEmpty(gstins)) {
				throw new AppException("Gstins cannot be Empty :");
			}
			
			if(LOGGER.isDebugEnabled()) {
				String msg = String.format(" The Request for ReconResult FileType or "
						+ " Bucket Type for TaxPeriod : %d and Gstins %s ",
						taxPeriod,gstins.toString());
				LOGGER.debug(msg);
			}
			
			List<String> reportType = reconResultReportTypeService
					.getReconResultReportNames(taxPeriod, gstins);
			
			List<ReconResponseReportTypeDto> sgstinDtoList = reportType.stream()
					.map(ReconResponseReportTypeDto::new).
					collect(Collectors.toList());
			
			
		    JsonObject resp = new JsonObject();
			JsonObject detResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(sgstinDtoList);
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

