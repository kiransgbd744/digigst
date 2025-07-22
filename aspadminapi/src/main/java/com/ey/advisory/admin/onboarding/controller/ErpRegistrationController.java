package com.ey.advisory.admin.onboarding.controller;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.ErpInfoEntity;
import com.ey.advisory.admin.data.erp.ERPRegistrationService;
import com.ey.advisory.admin.data.repositories.client.ErpInfoEntityRepository;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.ErpEntityInfoDto;
import com.ey.advisory.core.dto.ErpEntityInfoSaveDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

/**
 * @author Laxmi.Salukuti
 *
 */
@RestController
public class ErpRegistrationController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ErpRegistrationController.class);
	@Autowired
	@Qualifier("ErpInfoEntityRepository")
	private ErpInfoEntityRepository erpInfoEntityRepository;

	@Autowired
	@Qualifier("ERPRegistrationServiceImpl")
	private ERPRegistrationService erpRegistrationService;

	private static final String UPDATE_SUCCESSFULLY = "Updated Successfully";
	private static final String DELETED_SUCCESSFULLY = "Deleted Succefully";
	private static final String RESP = "resp";

	@RequestMapping(value = "/geterpregistration", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getEntities() {
		try {

			List<ErpEntityInfoDto> lineList = new ArrayList<>();
			List<ErpInfoEntity> info = erpInfoEntityRepository.findAllVal();

			for (ErpInfoEntity erpInfodata : info) {
				ErpEntityInfoDto erp = new ErpEntityInfoDto();

				erp.setId(erpInfodata.getId());
				erp.setSystemId(erpInfodata.getSystemId());
				erp.setHostName(erpInfodata.getHostName());
				erp.setPort(erpInfodata.getPort());
				erp.setStatus(erpInfodata.isDelete());
				erp.setUserName(erpInfodata.getUser());
				erp.setPassword(erpInfodata.getPass());
				erp.setPortocal(erpInfodata.getProtocal());
				erp.setSourceType(erpInfodata.getSourceType());
				lineList.add(erp);
			}

			Gson gson = new Gson();
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(lineList);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			String msg = "Unexpected error while searching entities";
			LOGGER.error(msg, e);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/saveErpInformation", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })

	public ResponseEntity<String> getErpsave(@RequestBody String jsonString) {
		JsonObject resp = new JsonObject();
		try {

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			Type listType = new TypeToken<List<ErpEntityInfoSaveDto>>() {
			}.getType();

			JsonArray json = requestObject.get("req").getAsJsonArray();
			Gson gson = GsonUtil.newSAPGsonInstance();
			List<ErpEntityInfoSaveDto> saveErpRegDetailDto = gson.fromJson(json,
					listType);
			List<ErpInfoEntity> saveErpRegEntity = new ArrayList<>();
			for (ErpEntityInfoSaveDto dto : saveErpRegDetailDto) {
				ErpInfoEntity ent = new ErpInfoEntity();
				LocalDateTime convertNow = EYDateUtil
						.toUTCDateTimeFromLocal(LocalDateTime.now());
				erpInfoEntityRepository
						.inActiveErpBasedOnSystem(dto.getSystemId());
				ent.setId(dto.getId());
				ent.setSystemId(dto.getSystemId());
				ent.setHostName(dto.getHostName());
				ent.setPort(dto.getPort());
				ent.setDelete(dto.isStatus());
				ent.setUser(dto.getUserName());
				ent.setPass(dto.getPassword());
				ent.setCreatedBy("SYSTEM");
				ent.setCreatedOn(convertNow);
				ent.setModifiedBy("SYSTEM");
				ent.setModifiedOn(convertNow);
				ent.setProtocal(dto.getPortocal());
				ent.setSourceType(dto.getSourceType());
				saveErpRegEntity.add(ent);
			}
			erpInfoEntityRepository.saveAll(saveErpRegEntity);

			resp.add(RESP, gson.toJsonTree(new APIRespDto(
					APIRespDto.getSuccessStatus(), UPDATE_SUCCESSFULLY)));
		} catch (Exception e) {
			String msg = "Unexpected error while saving Erp details";
			LOGGER.error(msg, e);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	@PostMapping(value = "/deleteErpRegistration.do", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> deleteErpDetalsInfo(
			@RequestBody String jsonReq) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonArray requestObject = (new JsonParser()).parse(jsonReq)
					.getAsJsonObject().get("req").getAsJsonArray();
			Type listType = new TypeToken<List<ErpEntityInfoDto>>() {
			}.getType();
			List<ErpEntityInfoDto> erpRegistrationReqDtos = gson
					.fromJson(requestObject, listType);
			erpRegistrationService.deleteErpInfoDetails(erpRegistrationReqDtos);
			resp.add(RESP, gson.toJsonTree(new APIRespDto(
					APIRespDto.getSuccessStatus(), DELETED_SUCCESSFULLY)));
		} catch (Exception e) {
			LOGGER.error("Exception Occure:", e);
			resp.add("hrd",
					gson.toJsonTree(new APIRespDto(APIRespDto.getErrorStatus(),
							e.getMessage())));
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
	}

}