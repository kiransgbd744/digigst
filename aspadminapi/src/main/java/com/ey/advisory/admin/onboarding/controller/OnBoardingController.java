package com.ey.advisory.admin.onboarding.controller;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.ELExtractEntity;
import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.entities.client.UserCreationEntity;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.admin.services.onboarding.ELExtractService;
import com.ey.advisory.admin.services.onboarding.GSTINDetailService;
import com.ey.advisory.admin.services.onboarding.UserCreationService;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.ELExtractDto;
import com.ey.advisory.core.dto.GSTINDetailDto;
import com.ey.advisory.core.dto.UserCreationDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;


@RestController
public class OnBoardingController {
	
	@Autowired
	@Qualifier("ELExtractService")
	ELExtractService elExtractService;
	
	@Autowired
	@Qualifier("GSTINDetailService")
	GSTINDetailService gstinDetailService;
	
	@Autowired
	@Qualifier("UserCreationService")
	UserCreationService userCreationService;

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	EntityInfoDetailsRepository entityInfoDetailsRepository;
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(OnBoardingController.class);

	/**
	 * Save Api
	 * 
	 * @param jsonString
	 * @return
	 */
	@RequestMapping(value = "/saveGstinDetail", method = 
			RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })

	public ResponseEntity<String> saveGstnDetail(
			@RequestBody String jsonString) {
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			Type listType = new TypeToken<List<GSTINDetailDto>>() {
			}.getType();

			JsonArray json = requestObject.get("req").getAsJsonArray();
			Gson gson = GsonUtil.newSAPGsonInstance();
			
			List<GSTINDetailDto> saveGstinDetailsDto = 
					gson.fromJson(json, listType);
			List<GSTNDetailEntity> saveGstinDetails = 
					new ArrayList<GSTNDetailEntity>();
			for(GSTINDetailDto dto: saveGstinDetailsDto) {
				GSTNDetailEntity ent = new GSTNDetailEntity();
				ent.setId(dto.getId());
				ent.setEntityId(dto.getEntityId());
				ent.setGroupCode(dto.getGroupCode());
				ent.setGstin(dto.getSupplierGstin());
				ent.setRegistrationType(dto.getRegistrationType());
				ent.setGstnUsername(dto.getGstnUsername());
				ent.setRegDate(dto.getEffectiveDate());
				ent.setRegisteredEmail(dto.getRegisteredEmail());
				ent.setRegisteredMobileNo(dto.getRegisteredMobileNo());
				ent.setPrimaryAuthEmail(dto.getPrimaryAuthEmail());
				ent.setSecondaryAuthEmail(dto.getSecondaryAuthEmail());
				ent.setPrimaryContactEmail(dto.getPrimaryContactEmail());
				ent.setSecondaryContactEmail(dto.getSecondaryContactEmail());
				ent.setBankAccNum(dto.getBankAccNo());
				ent.setTurnover(dto.getTurnover());
				ent.setIsDelete(false);
				saveGstinDetails.add(ent);
			}
			
			// TO-DO - Persist gstnDetails
			List<GSTNDetailEntity> response=
					gstinDetailService.saveOrUpdate(saveGstinDetails);
			
			List<Long> ids = new ArrayList<Long>();
			for(GSTNDetailEntity each : response) {
				ids.add(each.getId());
			}
			
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(ids));

			return new ResponseEntity<>(resp.toString(),HttpStatus.CREATED);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while saving documents";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Get API GSTIN Detail
	 * 
	 * @param jsonString
	 * @return
	 */
	@RequestMapping(value = "/getGstinDetail", method = 
			RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })

	public ResponseEntity<String> getGstinDetail() {
		try {
			Gson gson = GsonUtil.newSAPGsonInstance();
			List<GSTNDetailEntity> getGstinDetails =
					gstinDetailService.findGstinDetails();
			
			Type listType = new TypeToken<List<GSTINDetailDto>>(){}.getType();
			ModelMapper modelmapper = new ModelMapper();		
			List<GSTINDetailDto> postDtoList = modelmapper
					.map(getGstinDetails, listType);
			// TO-DO - Persist gstnDetails
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(postDtoList));

			return new ResponseEntity<>(resp.toString(),HttpStatus.CREATED);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while saving documents";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * SaveUser API for User Creation
	 * @param jsonString
	 * @return
	 */
	
	@RequestMapping(value = "/saveUserInfo", method = 
			RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })

	public ResponseEntity<String> getUserCreation(
			@RequestBody String jsonString) {
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			Type listType = new TypeToken<List<UserCreationDto>>() {
			}.getType();

			JsonArray json = requestObject.get("req").getAsJsonArray();
			Gson gson = GsonUtil.newSAPGsonInstance();
			List<UserCreationDto> saveUserDetailDto = gson.fromJson(json,
					listType);
			List<UserCreationEntity> saveUserDetails = 
					new ArrayList<UserCreationEntity>();
			for(UserCreationDto dto: saveUserDetailDto) {
				UserCreationEntity ent = new UserCreationEntity();
				ent.setGroupCode(dto.getGroupCode());

				ent.setUserName(dto.getUserName());
				ent.setFirstName(dto.getFirstName());
				ent.setLastName(dto.getLastName());
				ent.setEmail(dto.getEmail());
				ent.setMobile(dto.getMobile());
				ent.setUserRole(dto.getUserRole());
				ent.setIsFlag(dto.getIsFlag());
				ent.setCreatedBy(dto.getCreatedBy());
				ent.setModifiedBy(dto.getModifiedBy());
				
				/*EntityInfoEntity infoEntity = new EntityInfoEntity();
				infoEntity.setId(dto.getEntityId());
				ent.setEntityInfoEntity(infoEntity);*/
				
				saveUserDetails.add(ent);
			}
			// TO-DO - Persist gstnDetails
			//userCreationRepository.saveAll(saveUserDetails);
			List<UserCreationEntity> response=
					userCreationService.saveOrUpdate(saveUserDetails);
			List<Long> ids = new ArrayList<Long>();
			for(UserCreationEntity each : response) {
				ids.add(each.getId());
			}
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(ids));
			return new ResponseEntity<>(resp.toString(),HttpStatus.CREATED);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while saving documents";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	
	
	/**
	 *  ELExtract API For Saving Data
	 * @param jsonString
	 * @return
	 */
	
	@RequestMapping(value = "/saveELExtract", method = 
			RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })

	public ResponseEntity<String> saveELExtract(
			@RequestBody String jsonString) {
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			
			Type listType = new TypeToken<List<ELExtractDto>>() {
			}.getType();

					
			JsonArray json = requestObject.get("req").getAsJsonArray();
			Gson gson = GsonUtil.newSAPGsonInstance();
			
			List<ELExtractDto> extractDto = gson.fromJson(json, listType);
			List<ELExtractEntity> saveExtractDetails = 
					new ArrayList<ELExtractEntity>();
			for(ELExtractDto dto: extractDto) {
				ELExtractEntity ent = new ELExtractEntity();
				ent.setContractEndPeriod(dto.getContractEndPeriod());
				ent.setContractStartPeriod(dto.getContractStartPeriod());
				ent.setEntityId(dto.getEntityId());
				ent.setGroupCode(dto.getGroupCode());
				ent.setGstin(dto.getGstin());
				ent.setRenewal(dto.getRenewal());
				ent.setElId(dto.getElId());
				ent.setFunctionality(dto.getFunctionality());
				ent.setIsFlag(false);
				saveExtractDetails.add(ent);
			}
		
			List<ELExtractEntity> response=
					elExtractService.saveELExtractDetail(saveExtractDetails);
			
			List<Long> ids = new ArrayList<Long>();
			for(ELExtractEntity each : response) {
				ids.add(each.getElId());
			}
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(ids));

			return new ResponseEntity<>(resp.toString(),HttpStatus.CREATED);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while saving documents";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * ELExtract API For getting Data
	 * @return
	 */
	
	@RequestMapping(value = "/getELExtractInfo", method = 
			RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })

	public ResponseEntity<String> getELExtractInfo() {
		try {
			Gson gson = GsonUtil.newSAPGsonInstance();
			List<ELExtractEntity> getELExtractDetails=
					elExtractService.getELExtractDetail();
			
			Type listType = new TypeToken<List<ELExtractDto>>(){}.getType();
			ModelMapper modelmapper = new ModelMapper();		
			List<ELExtractDto> elExtractDetails = modelmapper
					.map(getELExtractDetails, listType);
			
			// TO-DO - Persist gstnDetails
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(elExtractDetails));

			return new ResponseEntity<>(resp.toString(),HttpStatus.CREATED);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while getting ELExtracting ";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	@RequestMapping(value = "/deleteGstin", method = 
			RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })

	public ResponseEntity<String> deleteGstnDetail(
			@RequestBody String jsonString) {
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			Type listType = new TypeToken<List<GSTINDetailDto>>() {
			}.getType();

			JsonArray json = requestObject.get("req").getAsJsonArray();
			Gson gson = GsonUtil.newSAPGsonInstance();
			
			List<GSTINDetailDto> saveGstinDetailsDto = 
					gson.fromJson(json, listType);
			List<GSTNDetailEntity> saveGstinDetails = 
					new ArrayList<GSTNDetailEntity>();
						
			for(GSTINDetailDto dto: saveGstinDetailsDto) {
				GSTNDetailEntity ent = new GSTNDetailEntity();
				ent.setId(dto.getId());
				ent.setEntityId(dto.getEntityId());
				ent.setGroupCode(dto.getGroupCode());
				ent.setGstin(dto.getSupplierGstin());
				ent.setRegistrationType(dto.getRegistrationType());
				ent.setGstnUsername(dto.getGstnUsername());
				ent.setRegDate(dto.getEffectiveDate());
				ent.setRegisteredEmail(dto.getRegisteredEmail());
				ent.setRegisteredMobileNo(dto.getRegisteredMobileNo());
				ent.setPrimaryAuthEmail(dto.getPrimaryAuthEmail());
				ent.setSecondaryAuthEmail(dto.getSecondaryAuthEmail());
				ent.setPrimaryContactEmail(dto.getPrimaryContactEmail());
				ent.setSecondaryContactEmail(dto.getSecondaryContactEmail());
				ent.setBankAccNum(dto.getBankAccNo());
				ent.setTurnover(dto.getTurnover());
				
				saveGstinDetails.add(ent);
			}
			
			// TO-DO - Persist gstnDetails
			gstinDetailService.deleteRecord(saveGstinDetails);
						
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			

			return new ResponseEntity<>(resp.toString(),HttpStatus.CREATED);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while saving documents";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * delete API For User
	 * @param jsonString
	 * @return
	 */
	
	@RequestMapping(value = "/deleteUserInfo", method = 
			RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })

	public ResponseEntity<String> deleteUser(
			@RequestBody String jsonString) {
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			Type listType = new TypeToken<List<UserCreationDto>>() {
			}.getType();

			JsonArray json = requestObject.get("req").getAsJsonArray();
			Gson gson = GsonUtil.newSAPGsonInstance();
			List<UserCreationDto> saveUserDetailDto = gson.fromJson(json,
					listType);
			List<UserCreationEntity> saveUserDetails = 
					new ArrayList<UserCreationEntity>();
			for(UserCreationDto dto: saveUserDetailDto) {
				UserCreationEntity ent = new UserCreationEntity();
				ent.setGroupCode(dto.getGroupCode());
				ent.setUserName(dto.getUserName());
				ent.setFirstName(dto.getFirstName());
				ent.setLastName(dto.getLastName());
				ent.setEmail(dto.getEmail());
				ent.setMobile(dto.getMobile());
				ent.setUserRole(dto.getUserRole());
				ent.setIsFlag(dto.getIsFlag());
				ent.setCreatedBy(dto.getCreatedBy());
				ent.setModifiedBy(dto.getModifiedBy());
				/*EntityInfoEntity entityInfoEntity = new EntityInfoEntity();
				entityInfoEntity.setId(dto.getId());
				ent.setEntityInfoEntity(entityInfoEntity);*/
				saveUserDetails.add(ent);
			}
			// TO-DO - Persist gstnDetails
			//userCreationRepository.saveAll(saveUserDetails);
			
			userCreationService.deleteRecord(saveUserDetails);
			
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			
			return new ResponseEntity<>(resp.toString(),HttpStatus.CREATED);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while saving documents";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * Delete API For ELExtract
	 * @param jsonString
	 * @return
	 */
	@RequestMapping(value = "/deleteELExtract", method = 
			RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })

	public ResponseEntity<String> deleteELExtract(
			@RequestBody String jsonString) {
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			
			Type listType = new TypeToken<List<ELExtractDto>>() {
			}.getType();

					
			JsonArray json = requestObject.get("req").getAsJsonArray();
			Gson gson = GsonUtil.newSAPGsonInstance();
			
			List<ELExtractDto> extractDto = gson.fromJson(json, listType);
			List<ELExtractEntity> saveExtractDetails = 
					new ArrayList<ELExtractEntity>();
			for(ELExtractDto dto: extractDto) {
				ELExtractEntity ent = new ELExtractEntity();
				ent.setContractEndPeriod(dto.getContractEndPeriod());
				ent.setContractStartPeriod(dto.getContractStartPeriod());
				ent.setEntityId(dto.getEntityId());
				ent.setGroupCode(dto.getGroupCode());
				ent.setGstin(dto.getGstin());
				ent.setRenewal(dto.getRenewal());
				ent.setElId(dto.getElId());
				ent.setFunctionality(dto.getFunctionality());
				saveExtractDetails.add(ent);
			}
		
			elExtractService.deleteRecord(saveExtractDetails);
			
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			

			return new ResponseEntity<>(resp.toString(),HttpStatus.CREATED);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while saving documents";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}



	
}
