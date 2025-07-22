package com.ey.advisory.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.services.gstr1.EinvoiceStatusPopUpServiceImpl;
import com.ey.advisory.app.services.gstr1.Gstr1PopScreenRecordsFinalResponseDto;
import com.ey.advisory.app.services.gstr1.Gstr1PopScreenRecordsRequestDto;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Slf4j
@RestController
public class EInvoiceStatusPopController {

	@Autowired
	@Qualifier("EinvoiceStatusPopUpServiceImpl")
	private EinvoiceStatusPopUpServiceImpl gstr1PopUpRecordsService;

	@PostMapping(value = "/ui/getEinvoiceStatusPopupData", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> fetchProcessedRecords(
			@RequestBody String jsonString) throws Exception {
		try {

			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject reqObj = new JsonParser().parse(jsonString)
					.getAsJsonObject().get("req").getAsJsonObject();
			Gstr1PopScreenRecordsRequestDto dto = gson.fromJson(reqObj,
					Gstr1PopScreenRecordsRequestDto.class);
			Gstr1PopScreenRecordsFinalResponseDto finalResponseDtos = gstr1PopUpRecordsService
					.fetchRec(dto);

			JsonElement respBody = gson.toJsonTree(finalResponseDtos);
			JsonObject resp = new JsonObject();
			resp.add("resp", respBody);

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String msg = "Unexpected error while getting the gstr-1 e-invoices records";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
