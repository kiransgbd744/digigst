package com.ey.advisory.controller.gstr2;

import java.time.LocalDateTime;
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

import com.ey.advisory.app.docs.dto.gstr2.Gstr2TaxCrVendorDto;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

@RestController
public class VenCommunicationController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(VenCommunicationController.class);
	
	@RequestMapping(value = "/ui/getGstr2Vendor", method = 
			RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })

	public ResponseEntity<String> getVendorDetails(
			@RequestBody String jsonString) {
		try {
			Gson gson = GsonUtil.newSAPGsonInstance();
			
			List<Gstr2TaxCrVendorDto> respList = new ArrayList<>();
			Gstr2TaxCrVendorDto dto = new Gstr2TaxCrVendorDto();
			dto.setRequestId(123L);
			dto.setCategory("SummaryLevel");
			dto.setDate(LocalDateTime.now());
			dto.setStatus("Success");
			respList.add(dto);
			
			Gstr2TaxCrVendorDto dto1 = new Gstr2TaxCrVendorDto();
			dto1.setRequestId(124L);
			dto1.setCategory("SummaryLevel");
			dto1.setDate(LocalDateTime.now());
			dto1.setStatus("Saved");
			respList.add(dto1);
			
			Gstr2TaxCrVendorDto dto2 = new Gstr2TaxCrVendorDto();
			dto2.setRequestId(125L);
			dto2.setCategory("SummaryLevel");
			dto2.setDate(LocalDateTime.now());
			dto2.setStatus("Saved");
			respList.add(dto2);
			
			Gstr2TaxCrVendorDto dto3 = new Gstr2TaxCrVendorDto();
			dto3.setRequestId(221L);
			dto3.setCategory("SummaryLevel");
			dto3.setDate(LocalDateTime.now());
			dto3.setStatus("Success");
			respList.add(dto3);
			
			Gstr2TaxCrVendorDto dto4 = new Gstr2TaxCrVendorDto();
			dto4.setRequestId(211L);
			dto4.setCategory("SummaryLevel");
			dto4.setDate(LocalDateTime.now());
			dto4.setStatus("Success");
			respList.add(dto4);
			
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(respList));

			return new ResponseEntity<>(resp.toString(),HttpStatus.OK);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while getting "
					+ "Vendor Communication  for Gstr2";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}




}
