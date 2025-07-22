package com.ey.advisory.controller;

import java.lang.reflect.Type;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.data.services.Gstr1A.Gstr1AB2CSUpdateService;
import com.ey.advisory.app.docs.dto.Gstr1HsnSummarySectionDto;
import com.ey.advisory.app.docs.dto.Gstr1VerticalSummaryRespDto;
import com.ey.advisory.app.docs.dto.simplified.Gstr1VerticalRecordDeleteDto;
import com.ey.advisory.app.services.gstr1fileupload.Gstr1B2CSUpdateService;
import com.ey.advisory.app.services.gstr1fileupload.Gstr1HsnUserUpdateService;
import com.ey.advisory.app.services.search.simplified.docsummary.Gstr1PopupRecordDeleteService;
import com.ey.advisory.app.services.search.simplified.docsummary.Gstr1VerticalReqResponse;
import com.ey.advisory.app.util.OnboardingConstant.CONFIG_PARAM_OUTWARD_QUE_KEY_ID;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.common.service.GstnApi;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

/**
 * 
 * @author Balakrishna.S
 *
 */
@RestController
public class Gstr1AspSummaryController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1AspSummaryController.class);

	@Autowired
	@Qualifier("Gstr1VerticalReqResponse")
	Gstr1VerticalReqResponse reqResp;

	@Autowired
	@Qualifier("Gstr1B2CSUpdateService")
	Gstr1B2CSUpdateService b2csService;

	
	@Autowired
	@Qualifier("Gstr1AB2CSUpdateService")
	Gstr1AB2CSUpdateService b2csAService;
	@Autowired
	@Qualifier("Gstr1HsnUserUpdateService")
	Gstr1HsnUserUpdateService hsnUserService;

	@Autowired
	@Qualifier("Gstr1PopupRecordDeleteService")
	Gstr1PopupRecordDeleteService deleteService;
	
	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfPrmRepository;

	@Autowired
	@Qualifier("GstnApi")
	GstnApi gstnApi;
	private static final List<Integer> LENGTH1 = ImmutableList.of(4, 6, 8);
	private static final List<Integer> LENGTH2 = ImmutableList.of(6, 8);

	@PostMapping(value = "/ui/gstr1AspVertical", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> basicAspVerticalSummary(
			@RequestBody String jsonString) {
		String groupCode = TenantContext.getTenantId();
		LOGGER.debug(String.format("Group Code = '%s'", groupCode));
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			Gson gson = GsonUtil.newSAPGsonInstance();

			Annexure1SummaryReqDto ret1SummaryRequest = gson
					.fromJson(reqJson.toString(), Annexure1SummaryReqDto.class);

			JsonElement retReqRespDetails = reqResp
					.gstr1ReqRespDetails(ret1SummaryRequest);
			JsonElement respBody = gson.toJsonTree(retReqRespDetails);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while fecthing Annexure1 "
					+ "Summary Documents " + ex;
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * This method implemented For Doc Issued PopUp screen Summary and Vertica
	 * data getting
	 * 
	 * @param jsonString
	 * @return
	 */

	@PostMapping(value = "/ui/gstr1DocAspVertical", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> docAspVerticalSummary(
			@RequestBody String jsonString) {
		String groupCode = TenantContext.getTenantId();
		LOGGER.debug(String.format("Group Code = '%s'", groupCode));
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			Gson gson = GsonUtil.newSAPGsonInstance();

			Annexure1SummaryReqDto ret1SummaryRequest = gson
					.fromJson(reqJson.toString(), Annexure1SummaryReqDto.class);

			JsonElement retReqRespDetails = reqResp
					.gstr1DocReqRespDetails(ret1SummaryRequest);
			JsonElement respBody = gson.toJsonTree(retReqRespDetails);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while fecthing Annexure1 "
					+ "Summary Documents " + ex;
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * This method implemented For HSN PopUp screen Summary data getting
	 * 
	 * @param jsonString
	 * @return
	 */

	@PostMapping(value = "/ui/gstr1HsnAspVertical", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> hsnAspVerticalSummary(
			@RequestBody String jsonString) {
		String groupCode = TenantContext.getTenantId();
		LOGGER.debug(String.format("Group Code = '%s'", groupCode));
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			Gson gson = GsonUtil.newSAPGsonInstance();

			Annexure1SummaryReqDto ret1SummaryRequest = gson
					.fromJson(reqJson.toString(), Annexure1SummaryReqDto.class);

			JsonElement retReqRespDetails = reqResp
					.gstr1HsnReqRespDetails(ret1SummaryRequest);
			JsonElement respBody = gson.toJsonTree(retReqRespDetails);
			
			
			Boolean rateIncludedInHsn = gstnApi.isRateIncludedInHsn(ret1SummaryRequest.getTaxPeriod());
			JsonElement rateHsn = gson.toJsonTree(rateIncludedInHsn);
		
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			resp.add("rateIncludedInHsn", rateHsn);

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while fecthing Annexure1 "
					+ "Summary Documents " + ex;
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * this method definds for Popup Screen SEction B2CS Edit & Save Vertical
	 * Record
	 */
	@PostMapping(value = "/ui/gstr1VerticalUpdate", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> b2csVerticalUpdateSummary(
			@RequestBody String jsonString) {
		String groupCode = TenantContext.getTenantId();
		LOGGER.debug(String.format("Group Code = '%s'", groupCode));
		try {

			Gson gson = GsonUtil.newSAPGsonInstance();

			JsonArray jsonObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().get("req").getAsJsonArray();
			Type listType = new TypeToken<List<Gstr1VerticalSummaryRespDto>>() {
			}.getType();
			List<Gstr1VerticalSummaryRespDto> updateGstinInfoDetails = gson
					.fromJson(jsonObject, listType);
			
			for(Gstr1VerticalSummaryRespDto dto:updateGstinInfoDetails){
				if(dto.getNewHsnOrSac() != null){
					dto.setNewHsnOrSac(dto.getNewHsnOrSac().trim());
				}
				
				
				if(dto.getOrgHsnOrSac() != null){
					dto.setOrgHsnOrSac(dto.getOrgHsnOrSac().trim());
				}
			}
			JsonObject resp = null;
			if(updateGstinInfoDetails.get(0).getReturnType()!=null && "GSTR1A".equalsIgnoreCase(updateGstinInfoDetails.get(0).getReturnType())){
				resp = b2csAService
						.updateVerticalData(updateGstinInfoDetails);
				
			}
			else
			{
			resp = b2csService
					.updateVerticalData(updateGstinInfoDetails);
			}
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while fecthing Annexure1 "
					+ "Summary Documents " + ex;
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * This Method Using For Saving or Updating User Input Values
	 * 
	 * @param jsonString
	 * @return
	 */
	@PostMapping(value = "/ui/gstr1HsnUserUpdateData", produces = {
			MediaType.APPLICATION_JSON_VALUE })

	public ResponseEntity<String> hsnUserInputDataUpdate(
			@RequestBody String jsonString) {
		String groupCode = TenantContext.getTenantId();
		LOGGER.debug(String.format("Group Code = '%s'", groupCode));
		try {

			Gson gson = GsonUtil.newSAPGsonInstance();

			JsonArray jsonObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().get("req").getAsJsonArray();
			Type listType = new TypeToken<List<Gstr1HsnSummarySectionDto>>() {
			}.getType();
			List<Gstr1HsnSummarySectionDto> updateGstinInfoDetails = gson
					.fromJson(jsonObject, listType);

			JsonObject resp = hsnUserService
					.updateVerticalData(updateGstinInfoDetails);

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while fecthing Annexure1 "
					+ "Summary Documents " + ex;
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	
	/**
	 * This Method Using For Saving or moveDigiGstTo User Input Values
	 * 
	 * @param jsonString
	 * @return
	 */
	@PostMapping(value = "/ui/moveDigiGstToUserEdit", produces = {
			MediaType.APPLICATION_JSON_VALUE })

	public ResponseEntity<String> moveDigiGstToUserEdit(
			@RequestBody String jsonString) {
		String groupCode = TenantContext.getTenantId();
		LOGGER.debug(String.format("Group Code = '%s'", groupCode));
		try {

			Gson gson = GsonUtil.newSAPGsonInstance();

			JsonArray jsonObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().get("req").getAsJsonArray();
			Type listType = new TypeToken<List<Gstr1HsnSummarySectionDto>>() {
			}.getType();
			List<Gstr1HsnSummarySectionDto> updateGstinInfoDetails = gson
					.fromJson(jsonObject, listType);
			String paramKeyId = CONFIG_PARAM_OUTWARD_QUE_KEY_ID.O34.name();
			Ehcachegstin ehcachegstin = StaticContextHolder
					.getBean("Ehcachegstin", Ehcachegstin.class);
			GSTNDetailEntity gstin = ehcachegstin.getGstinInfo(groupCode,
					updateGstinInfoDetails.get(0).getSgstn());
			String answer = entityConfPrmRepository
					.findParamValByGroupCodeAndEntityIdAndIsDeleteFalse(
							groupCode, gstin.getEntityId(), paramKeyId);
			LOGGER.debug("onboarding answer:{} for gstin:{} and entity:{}",
					answer,gstin,gstin.getEntityId());
			for (Gstr1HsnSummarySectionDto dto : updateGstinInfoDetails) {
				if (((Strings.isNullOrEmpty(answer)
						|| GSTConstants.A.equalsIgnoreCase(answer))
						&& !LENGTH1.contains(dto.getHsn().length()))
						|| (GSTConstants.B.equalsIgnoreCase(answer)
								&& !LENGTH2.contains(dto.getHsn().length()))) {
					String msg = "The data cannot be moved as the Length of "
							+ "entered HSN code is not valid as per AATO";
					JsonObject resp = new JsonObject();
					resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

					return new ResponseEntity<>(resp.toString(),
							 HttpStatus.OK);
				}
			}
			 hsnUserService
					.updateVerticalData(updateGstinInfoDetails);
			String msg = "Changes saved successfully";
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("S", msg)));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while fecthing Annexure1 "
					+ "Summary Documents " + ex;
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	
	@PostMapping(value = "/ui/gstr1RecordDelete", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> basicVerticalRecordDelete(
			@RequestBody String jsonString) {
		String groupCode = TenantContext.getTenantId();
		LOGGER.debug(String.format("Group Code = '%s'", groupCode));
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			Gson gson = GsonUtil.newSAPGsonInstance();

			Gstr1VerticalRecordDeleteDto ret1SummaryRequest = gson.fromJson(
					reqJson.toString(), Gstr1VerticalRecordDeleteDto.class);

			int deleteRecord = deleteService.deleteRecord(ret1SummaryRequest);

			// JsonElement respBody = gson.toJsonTree(deleteRecord);
			JsonObject resp = new JsonObject();
			if (deleteRecord != 0) {
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
			} else {
				resp.add("hdr", gson.toJsonTree(APIRespDto.getErrorStatus()));
			}
			// resp.add("resp", respBody);

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while fecthing Annexure1 "
					+ "Summary Documents " + ex;
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/*@PostMapping(value = "/ui/getStateCode", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getStateCodeFromMaster() {
		String groupCode = TenantContext.getTenantId();
		LOGGER.debug(String.format("Group Code = '%s'", groupCode));
		try {

			Gson gson = GsonUtil.newSAPGsonInstance();

			JsonArray fetchStateCode = deleteService.fetchStateCode();
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));

			resp.add("resp", fetchStateCode);

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while fecthing Annexure1 "
					+ "Summary Documents " + ex;
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
*/
	/*@PostMapping(value = "/ui/getUom", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getUnitOfMeasure() {
		String groupCode = TenantContext.getTenantId();
		LOGGER.debug(String.format("Group Code = '%s'", groupCode));
		try {

			Gson gson = GsonUtil.newSAPGsonInstance();

			List<Object> fetchUom = deleteService.fetchUom();
			
			JsonElement respBody = gson.toJsonTree(fetchUom);
			
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));

			resp.add("resp", respBody);

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while fecthing Annexure1 "
					+ "Summary Documents " + ex;
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
*/
	@PostMapping(value = "/ui/getRate", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getRate() {
		String groupCode = TenantContext.getTenantId();
		LOGGER.debug(String.format("Group Code = '%s'", groupCode));
		try {

			Gson gson = GsonUtil.newSAPGsonInstance();

			 JsonArray fetchRate = deleteService.fetchRate();
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));

			resp.add("resp", fetchRate);

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while fecthing Annexure1 "
					+ "Summary Documents " + ex;
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
