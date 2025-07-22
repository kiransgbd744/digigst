/**
 * 
 */
package com.ey.advisory.controller;

import java.lang.reflect.Type;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.entities.client.GstrReturnStatusEntity;
import com.ey.advisory.app.docs.dto.Anx1ReportSearchReqDto;
import com.ey.advisory.app.docs.dto.Gstr6CrossItcRequestDto;
import com.ey.advisory.app.docs.service.gstr6.GstrCrossItcSceenService;
import com.ey.advisory.app.search.reports.BasicCommonSecParamReports;
import com.ey.advisory.app.services.gstr1fileupload.Gstr6CrossItcUpdateService;
import com.ey.advisory.app.util.EhcacheGstinTaxperiod;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.search.SearchResult;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Balakrishna.S
 *
 */
@RestController
@Slf4j
public class Gstr6CrossItcUpdateController {

	@Autowired
	@Qualifier("Gstr6CrossItcUpdateService")
	Gstr6CrossItcUpdateService userService;
	
	@Autowired
	@Qualifier("BasicCommonSecParamReports")
	private BasicCommonSecParamReports basicCommonSecParamReports;
	
	@Autowired
	@Qualifier("Gstr6CrossItcScreenServiceImpl")
	private GstrCrossItcSceenService gstrCrossItcSceenService;
	
	/**
	 * This Method Using For Saving or Updating User Input Values
	 * 
	 * @param jsonString
	 * @return
	 */
	@PostMapping(value = "/ui/gstr6CrossItcSaveData", produces = {
			MediaType.APPLICATION_JSON_VALUE })

	public ResponseEntity<String> hsnUserInputDataUpdate(
			@RequestBody String jsonString) {
		String groupCode = TenantContext.getTenantId();
		LOGGER.debug(String.format("Group Code = '%s'", groupCode));
		try {

			Gson gson = GsonUtil.newSAPGsonInstance();

			JsonArray jsonObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().get("req").getAsJsonArray();
			Type listType = new TypeToken<List<Gstr6CrossItcRequestDto>>() {
			}.getType();
			List<Gstr6CrossItcRequestDto> updateGstinInfoDetails = gson
					.fromJson(jsonObject, listType);
			
			String msg = null;
			
			if(updateGstinInfoDetails.size() == 1){
				msg = "Changes made in User Data cannot be saved as the Return "
						+ "for the selected period is already filed";
			} else {
				msg = "Changes made in Edit User Data cannot be "
						+ "saved as Return for 1 or more selected GSTIN’s "
						+ "is already filed. Please check the Return status "
						+ "to save changes in Edit User Data";
			}
			
			for(Gstr6CrossItcRequestDto gstr6CrossItcRequestDto:  updateGstinInfoDetails){
				String gstin = gstr6CrossItcRequestDto.getIsdGstin();
				String taxPeriod = gstr6CrossItcRequestDto.getTaxPeriod();
				
				EhcacheGstinTaxperiod ehcachegstinTaxPeriod = StaticContextHolder.getBean("EhcacheGstinTaxperiod",
						EhcacheGstinTaxperiod.class);
				GstrReturnStatusEntity entity = ehcachegstinTaxPeriod.isGstinFiled(gstin,
						taxPeriod, "GSTR6", "FILED", TenantContext.getTenantId());
				if(entity != null){

					JsonObject resp = new JsonObject();
					resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

					return new ResponseEntity<>(resp.toString(),
								HttpStatus.OK);
				}
			}
			JsonObject resp = userService.updateVerticalData(updateGstinInfoDetails);
			
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while Saving Data "
					+ "Summary Documents " + ex;
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	@PostMapping(value = "/ui/gstr6CrossItcScreen", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> gstr6CrossItcScreen(@RequestBody String jsonInput) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject json = new JsonParser().parse(jsonInput).getAsJsonObject()
				.get("req").getAsJsonObject();
		JsonObject resp = new JsonObject();
		try {
			Anx1ReportSearchReqDto criteria = gson.fromJson(json,
					Anx1ReportSearchReqDto.class);
			if(LOGGER.isDebugEnabled())
			LOGGER.debug("Gstr6CrossItcScreen Adapter Filters Setting to Request BEGIN");
			
			Anx1ReportSearchReqDto dataSecSearcParam = basicCommonSecParamReports
					.setDataSecuritySearchParams(criteria);
			SearchResult<Gstr6CrossItcRequestDto> gstr6CrossItcScreen = gstrCrossItcSceenService 
					.getGstr6CrossItcvalues(dataSecSearcParam); 
			
			if(LOGGER.isDebugEnabled())
			LOGGER.debug("Gstr6CrossItcScreen Adapter Filters Setting to Request END");
			
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(gstr6CrossItcScreen.getResult()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Gstr6 Determination ";
			LOGGER.error(msg, ex);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}


	
	
	
}
