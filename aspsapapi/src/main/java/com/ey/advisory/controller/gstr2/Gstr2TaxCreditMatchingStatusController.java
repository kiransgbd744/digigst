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

import com.ey.advisory.app.docs.dto.gstr2.TaxCreditMatchingStatusDto;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

@RestController
public class Gstr2TaxCreditMatchingStatusController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr2TaxCreditMatchingStatusController.class);

	
	@RequestMapping(value = "/ui/Gstr2TaxCreditMatchingStatus", method = 
			RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })

	public ResponseEntity<String> getGstr2Get2AData(
			@RequestBody String jsonString) {
		try {
			Gson gson = GsonUtil.newSAPGsonInstance();
			
			List<TaxCreditMatchingStatusDto> respList = 
					new ArrayList<TaxCreditMatchingStatusDto>();
			
			TaxCreditMatchingStatusDto dto = new TaxCreditMatchingStatusDto();
			
			
			dto.setAprilStatus("success");
			dto.setApriltimeStamp(LocalDateTime.now());
			
			dto.setAugestStatus("Success");
			dto.setAugesttimeStamp(LocalDateTime.now());
			
			dto.setDecemberStatus("Failed");
			dto.setDecembertimeStamp(LocalDateTime.now());
			
			dto.setFebStatus("Success");
			dto.setFebtimeStamp(LocalDateTime.now());
			dto.setGstin("33GSPTN0481G1ZA");
			dto.setId(1L);
			
			dto.setJanStatus("Failed");
			dto.setJantimeStamp(LocalDateTime.now());
			
			dto.setJulyStatus("Success");
			dto.setJulytimeStamp(LocalDateTime.now());
			
			dto.setJuneStatus("Success");
			dto.setJunetimeStamp(LocalDateTime.now());
			
			dto.setMarchStatus("Success");
			dto.setMarchtimeStamp(LocalDateTime.now());
			dto.setMayStatus("Failed");
			
			dto.setMaytimeStamp(LocalDateTime.now());
			
			dto.setNovemberStatus("Success");
			dto.setNovembertimeStamp(LocalDateTime.now());
		
			dto.setOctoberStatus("Failed");
			dto.setOctobertimeStamp(LocalDateTime.now());
			
			dto.setSeptemberStatus("failed");
			dto.setSeptembertimeStamp(LocalDateTime.now());
			respList.add(dto);
			
			TaxCreditMatchingStatusDto dto2 = new TaxCreditMatchingStatusDto();
			
			dto2.setAprilStatus("success");
			dto2.setApriltimeStamp(LocalDateTime.now());
			
			dto2.setAugestStatus("Success");
			dto2.setAugesttimeStamp(LocalDateTime.now());
			
			dto2.setDecemberStatus("Failed");
			dto2.setDecembertimeStamp(LocalDateTime.now());
			
			dto2.setFebStatus("Success");
			dto2.setFebtimeStamp(LocalDateTime.now());
			dto2.setGstin("33GSPTN0481G1ZA");
			dto2.setId(2L);
			
			dto2.setJanStatus("Failed");
			dto2.setJantimeStamp(LocalDateTime.now());
			
			dto2.setJulyStatus("Success");
			dto2.setJulytimeStamp(LocalDateTime.now());
			
			dto2.setJuneStatus("Success");
			dto2.setJunetimeStamp(LocalDateTime.now());
			
			dto2.setMarchStatus("Success");
			dto2.setMarchtimeStamp(LocalDateTime.now());
			dto2.setMayStatus("Failed");
			
			dto2.setMaytimeStamp(LocalDateTime.now());
			
			dto2.setNovemberStatus("Success");
			dto2.setNovembertimeStamp(LocalDateTime.now());
			
			dto2.setOctoberStatus("Failed");
			dto2.setOctobertimeStamp(LocalDateTime.now());
			
			dto2.setSeptemberStatus("failed");
			dto2.setSeptembertimeStamp(LocalDateTime.now());
			respList.add(dto2);
			
			TaxCreditMatchingStatusDto dto3 = new TaxCreditMatchingStatusDto();
			
			dto3.setAprilStatus("success");
			dto3.setApriltimeStamp(LocalDateTime.now());
			
			dto3.setAugestStatus("Success");
			dto3.setAugesttimeStamp(LocalDateTime.now());
			
			dto3.setDecemberStatus("Failed");
			dto3.setDecembertimeStamp(LocalDateTime.now());
			
			dto3.setFebStatus("Success");
			dto3.setFebtimeStamp(LocalDateTime.now());
			dto3.setGstin("33GSPTN0481G1ZA");
			dto3.setId(3L);
			
			dto3.setJanStatus("Failed");
			dto3.setJantimeStamp(LocalDateTime.now());
			
			dto3.setJulyStatus("Success");
			dto3.setJulytimeStamp(LocalDateTime.now());
			
			dto3.setJuneStatus("Success");
			dto3.setJunetimeStamp(LocalDateTime.now());
			
			dto3.setMarchStatus("Success");
			dto3.setMarchtimeStamp(LocalDateTime.now());
			dto3.setMayStatus("Failed");
			
			dto3.setMaytimeStamp(LocalDateTime.now());
			
			dto3.setNovemberStatus("Success");
			dto3.setNovembertimeStamp(LocalDateTime.now());
			
			dto3.setOctoberStatus("Failed");
			dto3.setOctobertimeStamp(LocalDateTime.now());
			
			dto3.setSeptemberStatus("failed");
			dto3.setSeptembertimeStamp(LocalDateTime.now());
			respList.add(dto3);
			
			TaxCreditMatchingStatusDto dto4 = new TaxCreditMatchingStatusDto();
			
			dto4.setAprilStatus("success");
			dto4.setApriltimeStamp(LocalDateTime.now());
			
			dto4.setAugestStatus("Success");
			dto4.setAugesttimeStamp(LocalDateTime.now());
		
			dto4.setDecemberStatus("Failed");
			dto4.setDecembertimeStamp(LocalDateTime.now());
			
			dto4.setFebStatus("Success");
			dto4.setFebtimeStamp(LocalDateTime.now());
			dto4.setGstin("33GSPTN0481G1ZA");
			dto4.setId(4L);
			
			dto4.setJanStatus("Failed");
			dto4.setJantimeStamp(LocalDateTime.now());
			
			dto4.setJulyStatus("Success");
			dto4.setJulytimeStamp(LocalDateTime.now());
			
			dto4.setJuneStatus("Success");
			dto4.setJunetimeStamp(LocalDateTime.now());
			
			dto4.setMarchStatus("Success");
			dto4.setMarchtimeStamp(LocalDateTime.now());
			dto4.setMayStatus("Failed");
			
			dto4.setMaytimeStamp(LocalDateTime.now());
		
			dto4.setNovemberStatus("Success");
			dto4.setNovembertimeStamp(LocalDateTime.now());
			
			dto4.setOctoberStatus("Failed");
			dto4.setOctobertimeStamp(LocalDateTime.now());
			
			dto4.setSeptemberStatus("failed");
			dto4.setSeptembertimeStamp(LocalDateTime.now());
			respList.add(dto4);
			
			TaxCreditMatchingStatusDto dto5 = new TaxCreditMatchingStatusDto();
		
			dto5.setAprilStatus("success");
			dto5.setApriltimeStamp(LocalDateTime.now());
			
			dto5.setAugestStatus("Success");
			dto5.setAugesttimeStamp(LocalDateTime.now());
			
			dto5.setDecemberStatus("Failed");
			dto5.setDecembertimeStamp(LocalDateTime.now());
			
			dto5.setFebStatus("Success");
			dto5.setFebtimeStamp(LocalDateTime.now());
			dto5.setGstin("33GSPTN0481G1ZA");
			dto5.setId(5L);
			
			dto5.setJanStatus("Failed");
			dto5.setJantimeStamp(LocalDateTime.now());
			
			dto5.setJulyStatus("Success");
			dto5.setJulytimeStamp(LocalDateTime.now());
			
			dto5.setJuneStatus("Success");
			dto5.setJunetimeStamp(LocalDateTime.now());
			
			dto5.setMarchStatus("Success");
			dto5.setMarchtimeStamp(LocalDateTime.now());
			dto5.setMayStatus("Failed");
			
			dto5.setMaytimeStamp(LocalDateTime.now());
			
			dto5.setNovemberStatus("Success");
			dto5.setNovembertimeStamp(LocalDateTime.now());
			
			dto5.setOctoberStatus("Failed");
			dto5.setOctobertimeStamp(LocalDateTime.now());
			
			dto5.setSeptemberStatus("failed");
			dto5.setSeptembertimeStamp(LocalDateTime.now());
			respList.add(dto5);
			
			
			
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
			String msg = "Unexpected error while getting DataStatus for Gstr2";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
