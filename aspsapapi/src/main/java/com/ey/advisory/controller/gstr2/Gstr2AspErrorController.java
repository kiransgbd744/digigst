package com.ey.advisory.controller.gstr2;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.docs.dto.gstr2.Gstr2AspErrorDto;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * 
 * @author Balakrishna.S
 *
 */

@RestController
public class Gstr2AspErrorController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr2AspErrorController.class);

	@RequestMapping(value = "/ui/gstr2AspError", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })

	public ResponseEntity<String> getGstr2Asperror(
			@RequestBody String jsonString) {
		try {
			Gson gson = GsonUtil.newSAPGsonInstance();

			List<Gstr2AspErrorDto> respList = new ArrayList<Gstr2AspErrorDto>();
			Gstr2AspErrorDto dto = new Gstr2AspErrorDto();

			dto.setGstin("33GSPTN0481G1ZA");
			dto.setDocType("CR");
			dto.setDocNum("97432");
			dto.setDocDate(LocalDate.now());
			dto.setReturnPeriod("072018");
			dto.setErrorCode("ER051");
			dto.setRecipientGStin("33GSPTN0481G1ZA");
			respList.add(dto);

			Gstr2AspErrorDto dto2 = new Gstr2AspErrorDto();

			dto2.setGstin("33ABOPS9546G1Z3");
			dto2.setDocType("INV");
			dto2.setDocNum("MC9712");
			dto2.setDocDate(LocalDate.now());
			dto2.setReturnPeriod("092018");
			dto2.setErrorCode("ER064");
			dto2.setRecipientGStin("33GSPTN0481G1ZA");
			respList.add(dto2);
			
			Gstr2AspErrorDto dto3 = new Gstr2AspErrorDto();

			dto3.setGstin("33ABOPS9546G1Z3");
			dto3.setDocType("INV");
			dto3.setDocNum("MC9712");
			dto3.setDocDate(LocalDate.now());
			dto3.setReturnPeriod("092018");
			dto3.setErrorCode("ER064");
			dto3.setRecipientGStin("33GSPTN0481G1ZA");
			respList.add(dto3);
			
			Gstr2AspErrorDto dto4 = new Gstr2AspErrorDto();

			dto4.setGstin("33ABOPS9546G1Z3");
			dto4.setDocType("INV");
			dto4.setDocNum("MC9712");
			dto4.setDocDate(LocalDate.now());
			dto4.setReturnPeriod("092018");
			dto4.setErrorCode("ER064");
			dto4.setRecipientGStin("33GSPTN0481G1ZA");
			respList.add(dto4);

			Gstr2AspErrorDto dto5 = new Gstr2AspErrorDto();

			dto5.setGstin("33ABOPS9546G1Z3");
			dto5.setDocType("INV");
			dto5.setDocNum("MC9712");
			dto5.setDocDate(LocalDate.now());
			dto5.setReturnPeriod("092018");
			dto5.setErrorCode("ER064");
			dto5.setRecipientGStin("33GSPTN0481G1ZA");
			respList.add(dto5);

			


			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(respList));

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while getting PR Data  for Gstr2";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
