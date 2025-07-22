package com.ey.advisory.controller.drc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.DRC01BReminderFrequencyEntity;
import com.ey.advisory.admin.data.entities.client.DRC01CReminderFrequencyEntity;
import com.ey.advisory.app.data.repositories.client.drc01c.TblDrc01bReminderFrequencyRepo;
import com.ey.advisory.app.data.repositories.client.drc01c.TblDrc01cReminderFrequencyRepo;
import com.ey.advisory.app.data.services.drc.DrcGetReminderFrequencyRespDto;
import com.ey.advisory.app.data.services.drc01c.Drc01cService;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.jsonwebtoken.lang.Collections;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi.jain
 *
 */

@Slf4j
@RestController
public class DRC01ReminderFrequencyScreenController {

	@Autowired
	@Qualifier("Drc01cServiceImpl")
	private Drc01cService drcService;

	@Autowired
	@Qualifier("TblDrc01bReminderFrequencyRepo")
	private TblDrc01bReminderFrequencyRepo tblDrc01bReminderFrequencyRepo;

	@Autowired
	@Qualifier("TblDrc01cReminderFrequencyRepo")
	private TblDrc01cReminderFrequencyRepo tblDrc01cReminderFrequencyRepo;

	@PostMapping(value = "/ui/saveDrc01ReminderFrequency", consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveDrcReminderDetails(
			@RequestBody String jsonString, HttpServletRequest req) {
		JsonObject jsonObj = new JsonObject();
		JsonObject requestObject = JsonParser.parseString(jsonString)
				.getAsJsonObject().getAsJsonObject("req");

		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();

		DrcGetReminderFrequencyRespDto getRespDetails = gson
				.fromJson(requestObject, DrcGetReminderFrequencyRespDto.class);

		try {
			Pair<List<DRC01BReminderFrequencyEntity>, List<DRC01CReminderFrequencyEntity>> entity = convertToReminderEntity(
					getRespDetails);
			tblDrc01bReminderFrequencyRepo
					.inActivateRecords(getRespDetails.getEntityId());
			tblDrc01cReminderFrequencyRepo
					.inActivateRecords(getRespDetails.getEntityId());

			tblDrc01bReminderFrequencyRepo.saveAll(entity.getValue0());
			tblDrc01cReminderFrequencyRepo.saveAll(entity.getValue1());

			JsonElement respBody = gson.toJsonTree("Updated Successfully");
			jsonObj.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			jsonObj.add("resp", respBody);
			return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			LOGGER.error("Unable to save the reminder details. ", ex);
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(getRespDetails);
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(respBody));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	@PostMapping(value = "/ui/getDrc01ReminderFrequency", consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getDrcReminderDetails(
			@RequestBody String jsonString, HttpServletRequest req) {
		JsonObject jsonObj = new JsonObject();
		JsonObject requestObject = JsonParser.parseString(jsonString)
				.getAsJsonObject().getAsJsonObject("req");
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();

		DrcGetReminderFrequencyRespDto getRespDetails = gson
				.fromJson(requestObject, DrcGetReminderFrequencyRespDto.class);

		try {
			List<String> drc01bReminderDates = tblDrc01bReminderFrequencyRepo
					.getReminderdates(getRespDetails.getEntityId());

			List<String> drc01cReminderDates = tblDrc01cReminderFrequencyRepo
					.getReminderdates(getRespDetails.getEntityId());

			if (drc01bReminderDates == null) {
				getRespDetails.setDrc01bFreq(Arrays.asList("0", "0", "0"));
			} else {
				getRespDetails.setDrc01bFreq(drc01bReminderDates);

			}

			if (drc01cReminderDates == null) {
				getRespDetails.setDrc01cFreq(Arrays.asList("0", "0", "0"));
			} else {
				getRespDetails.setDrc01cFreq(drc01cReminderDates);

			}

			JsonElement respBody = gson.toJsonTree(getRespDetails);
			jsonObj.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			jsonObj.add("resp", respBody);
			return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			LOGGER.error("Unable to get the reminder details. ", ex);
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(getRespDetails);
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(respBody));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	private Pair<List<DRC01BReminderFrequencyEntity>, List<DRC01CReminderFrequencyEntity>> convertToReminderEntity(
			DrcGetReminderFrequencyRespDto getRespDetails) {
		List<DRC01BReminderFrequencyEntity> drc01bEntity = new ArrayList<>();
		List<DRC01CReminderFrequencyEntity> drc01cEntity = new ArrayList<>();

		for (int i = 0; i < 3; i++) {

			if (!Collections.isEmpty(getRespDetails.getDrc01bFreq())
					&& i < getRespDetails.getDrc01bFreq().size()) {

				if (Integer
						.valueOf(getRespDetails.getDrc01bFreq().get(i)) != 0) {
					drc01bEntity.add(convertToDrc01bEntity(
							getRespDetails.getDrc01bFreq().get(i),
							getRespDetails.getEntityId(), i + 1));
				}
				/*
				 * drc01bEntity = getRespDetails.getDrc01bFreq().stream().map( o
				 * -> convertToDrc01bEntity(o, getRespDetails.getEntityId()))
				 * .collect(Collectors.toList());
				 */
			}
			if (!Collections.isEmpty(getRespDetails.getDrc01cFreq())
					&& i < getRespDetails.getDrc01cFreq().size()) {

				if (Integer
						.valueOf(getRespDetails.getDrc01cFreq().get(i)) != 0) {

					drc01cEntity.add(convertToDrc01cEntity(
							getRespDetails.getDrc01cFreq().get(i),
							getRespDetails.getEntityId(), i + 1));
				}
				/*
				 * drc01cEntity = getRespDetails.getDrc01cFreq().stream().map( o
				 * -> convertToDrc01cEntity(o, getRespDetails.getEntityId()))
				 * .collect(Collectors.toList());
				 */ }
		}
		return new Pair<List<DRC01BReminderFrequencyEntity>, List<DRC01CReminderFrequencyEntity>>(
				drc01bEntity, drc01cEntity);
	}

	private DRC01BReminderFrequencyEntity convertToDrc01bEntity(String o,
			Long entityId, int i) {
		DRC01BReminderFrequencyEntity dto = new DRC01BReminderFrequencyEntity();
		dto.setEntityId(entityId);
		dto.setDrc01bReminderDate(o.toString());
		dto.setDelete(false);
		dto.setReminderNumber(String.valueOf(i++));
		return dto;
	}

	private DRC01CReminderFrequencyEntity convertToDrc01cEntity(String o,
			Long entityId, int i) {
		DRC01CReminderFrequencyEntity dto = new DRC01CReminderFrequencyEntity();
		dto.setEntityId(entityId);
		dto.setDrc01cReminderDate(o.toString());
		dto.setDelete(false);
		dto.setReminderNumber(String.valueOf(i++));

		return dto;
	}
}
