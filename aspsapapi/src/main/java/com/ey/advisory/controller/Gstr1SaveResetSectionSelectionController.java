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

import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Slf4j
@RestController
public class Gstr1SaveResetSectionSelectionController {

	@Autowired
	@Qualifier("batchSaveStatusRepository")
	private Gstr1BatchRepository batchSaveStatusRepo;

	@PostMapping(value = "/ui/gstr1SaveToGstnSectionSelection", produces = {
			MediaType.APPLICATION_JSON_VALUE })

	public ResponseEntity<String> gstr1SaveSectionSelection(
			@RequestBody String request) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"gstr1SaveSectionSelection Request received from UI as {} ",
					request);
		}
		JsonObject resp = new JsonObject();

		Gson gson = GsonUtil.newSAPGsonInstance();
		try {

			JsonObject requestObject = (new JsonParser()).parse(request)
					.getAsJsonObject();
			JsonArray asJsonArray = requestObject.get("req").getAsJsonArray();
			Type listType = new TypeToken<List<Gstr1GetInvoicesReqDto>>() {
			}.getType();
			List<Gstr1GetInvoicesReqDto> dtos = gson.fromJson(asJsonArray,
					listType);

			String groupCode = TenantContext.getTenantId();
			TenantContext.setTenantId(groupCode);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("groupCode {} is set", groupCode);
			}
			JsonArray respBody = new JsonArray();
			for (Gstr1GetInvoicesReqDto dto : dtos) {

				List<String> savedSections = batchSaveStatusRepo
						.findRerurnSavedSections(
								APIConstants.GSTR1.toUpperCase(),
								dto.getGstin(), dto.getReturnPeriod());

				String sections = savedSections.toString()
						.replaceAll(", ", "', '").replace("[", "['")
						.replace("]", "']");

				JsonObject json = new JsonObject();
				json.addProperty("gstin", dto.getGstin());
				json.addProperty("sections", sections);
				respBody.add(json);
			}
			APIRespDto apiResp = APIRespDto.createSuccessResp();

			resp.add("hdr", gson.toJsonTree(apiResp));
			resp.add("resp", gson.toJsonTree(respBody));
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
		} catch (Exception e) {
			String msg = "Unexpected error while fetching  Gstr1 Save Reset sections";
			LOGGER.error(msg, e.getMessage());
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
