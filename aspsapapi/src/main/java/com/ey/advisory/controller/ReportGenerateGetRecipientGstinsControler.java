/**
 * 
 */
package com.ey.advisory.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.common.GstinReqTypDto;
import com.ey.advisory.app.util.DataSecurityAttributeUtil;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi.jain
 *
 */
@RestController
@Slf4j
public class ReportGenerateGetRecipientGstinsControler {

	@Autowired
	private GSTNDetailRepository gstinDetailRepo;

	@PostMapping(value = "/ui/getRecipientGstins", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getRecipientGstins(
			@RequestBody String reqJson) {

		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithExpose();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Start of ReportGenerateGetGstinsControler.getRecipientGstins,"
							+ "request provided {}",
					reqJson);
		}
		try {
			JsonObject reqObj = (new JsonParser()).parse(reqJson)
					.getAsJsonObject().getAsJsonObject();

			JsonObject reqObject = reqObj.get("req").getAsJsonObject();
			Long entityId = reqObject.get("entityId").getAsLong();
			String reconType = reqObject.has("reconType")
					? reqObject.get("reconType").getAsString() : null;

			Map<String, List<String>> dataSecurityAttrMap = new HashMap<>();
			Map<String, String> inwardSecurityAttributeMap = DataSecurityAttributeUtil
					.getInwardSecurityAttributeMap();
			dataSecurityAttrMap = DataSecurityAttributeUtil
					.dataSecurityAttrMapForQuery(Arrays.asList(entityId),
							inwardSecurityAttributeMap);
			List<String> dataSecGstinList = dataSecurityAttrMap.get("GSTIN");
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"ReportGenerateGetGstinsControler - dataSecurityAttrMap {} ",
						dataSecurityAttrMap.toString());
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"ReportGenerateGetGstinsControler - dataSecList {} ",
						dataSecGstinList);
			}

			if (CollectionUtils.isEmpty(dataSecGstinList))
				throw new AppException("User Does not have any gstin");

			List<GstinReqTypDto> respList = new ArrayList<>();
			List<GSTNDetailEntity> gstinEntity = gstinDetailRepo
					.findRegTypeByGstinList(dataSecGstinList);
			Collections.sort(gstinEntity,
					(a, b) -> a.getGstin().compareToIgnoreCase(b.getGstin()));
			for (GSTNDetailEntity entity : gstinEntity) {
				GstinReqTypDto dto = null;

				if (Strings.isNullOrEmpty(reconType)) {
					if (entity.getRegistrationType().equalsIgnoreCase("ISD"))
						dto = new GstinReqTypDto(entity.getGstin(), "ISD");
					else
						dto = new GstinReqTypDto(entity.getGstin(), null);

					respList.add(dto);
				} else if (!Strings.isNullOrEmpty(reconType)
						&& "2A_PR".equalsIgnoreCase(reconType)) {
					if (entity.getRegistrationType().equalsIgnoreCase("ISD"))
						dto = new GstinReqTypDto(entity.getGstin(), "ISD");
					else
						dto = new GstinReqTypDto(entity.getGstin(), null);

					respList.add(dto);

				}else
				{
					if (entity.getRegistrationType().equalsIgnoreCase("ISD"))
						continue;
					else
						dto = new GstinReqTypDto(entity.getGstin(), null);

					respList.add(dto);

				}
			}

			JsonObject gstinResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(respList);
			gstinResp.add("gstinInfo", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gstinResp);
			if (LOGGER.isDebugEnabled()) {
				String msg = "End ReportGenerateGetRecipientGstinsControler"
						+ ".getRecipientGstins, before returning response";
				LOGGER.debug(msg);
			}

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error(
					"Exception on ReportGenerateGetRecipientGstinsControler ",
					e);
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			JsonObject gstinResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(e.getMessage());
			gstinResp.add("errMsg", respBody);
			resp.add("resp", gstinResp);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}
}
