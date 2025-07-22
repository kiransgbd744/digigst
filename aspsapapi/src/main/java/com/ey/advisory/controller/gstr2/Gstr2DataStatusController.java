/*package com.ey.advisory.controller.gstr2;

import java.math.BigDecimal;
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

import com.ey.advisory.app.docs.dto.gstr2.GSTR2APRMatchingDto;
import com.ey.advisory.app.docs.dto.gstr2.Gstr2DataStatusRespDto;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

*//**
 * 
 * @author Balakrishna.S
 *
 *//*

@RestController
public class Gstr2DataStatusController {
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr2DataStatusController.class);

	
	@RequestMapping(value = "/getGstr2DataStatus", method = 
			RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })

	public ResponseEntity<String> getGstr2DataStatussss(
			@RequestBody String jsonString) {
		try {
			Gson gson = GsonUtil.newSAPGsonInstance();
			
			List<Gstr2DataStatusRespDto> respList=new ArrayList<>();
			Gstr2DataStatusRespDto respDto =new Gstr2DataStatusRespDto();
			respDto.setDate(LocalDate.now());
			respDto.setReviewstatus("Saved");
			respDto.setSapTotal(100);
			respDto.setDiffCount(30);
			respDto.setHciTotal(60);
			respDto.setHciProcess(40);
			respDto.setHciError(20);
			respDto.setAspTotal(70);
			respDto.setAspProcess(45);
			respDto.setAspError(25);
			respDto.setAspInfo(10);
			respDto.setAspRect(20);
			respDto.setAspStatus("Saved");
			respDto.setGstinProcess(60);
			respDto.setGstinError(26);
			respList.add(respDto);
			
			Gstr2DataStatusRespDto respDto1 =new Gstr2DataStatusRespDto();
			respDto1.setDate(LocalDate.now());
			respDto1.setReviewstatus("Saved");
			respDto1.setSapTotal(120);
			respDto1.setDiffCount(50);
			respDto1.setHciTotal(80);
			respDto1.setHciProcess(50);
			respDto1.setHciError(30);
			respDto1.setAspTotal(70);
			respDto1.setAspProcess(35);
			respDto1.setAspError(35);
			respDto1.setAspInfo(20);
			respDto1.setAspRect(30);
			respDto1.setAspStatus("Saved");
			respDto1.setGstinProcess(50);
			respDto1.setGstinError(36);
			respList.add(respDto1);
			
			Gstr2DataStatusRespDto respDto2 =new Gstr2DataStatusRespDto();
			respDto2.setDate(LocalDate.now());
			respDto2.setReviewstatus("Saved");
			respDto2.setSapTotal(120);
			respDto2.setDiffCount(50);
			respDto2.setHciTotal(80);
			respDto2.setHciProcess(50);
			respDto2.setHciError(30);
			respDto2.setAspTotal(70);
			respDto2.setAspProcess(35);
			respDto2.setAspError(35);
			respDto2.setAspInfo(20);
			respDto2.setAspRect(30);
			respDto2.setAspStatus("Saved");
			respDto2.setGstinProcess(50);
			respDto2.setGstinError(36);
			respList.add(respDto2);
			
			Gstr2DataStatusRespDto respDto3 =new Gstr2DataStatusRespDto();
			respDto3.setDate(LocalDate.now());
			respDto3.setReviewstatus("Saved To GSTN");
			respDto3.setSapTotal(120);
			respDto3.setDiffCount(50);
			respDto3.setHciTotal(80);
			respDto3.setHciProcess(50);
			respDto3.setHciError(30);
			respDto3.setAspTotal(70);
			respDto3.setAspProcess(35);
			respDto3.setAspError(35);
			respDto3.setAspInfo(20);
			respDto3.setAspRect(30);
			respDto3.setAspStatus("Saved");
			respDto3.setGstinProcess(50);
			respDto3.setGstinError(36);
			respList.add(respDto3);
			
			Gstr2DataStatusRespDto respDto4 =new Gstr2DataStatusRespDto();
			respDto4.setDate(LocalDate.now());
			respDto4.setReviewstatus("Saved");
			respDto4.setSapTotal(120);
			respDto4.setDiffCount(50);
			respDto4.setHciTotal(80);
			respDto4.setHciProcess(50);
			respDto4.setHciError(30);
			respDto4.setAspTotal(70);
			respDto4.setAspProcess(35);
			respDto4.setAspError(35);
			respDto4.setAspInfo(20);
			respDto4.setAspRect(30);
			respDto4.setAspStatus("Saved");
			respDto4.setGstinProcess(50);
			respDto4.setGstinError(36);
			respList.add(respDto4);
			
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
	@RequestMapping(value = "/getGstr2APRData", method = 
			RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })

	public ResponseEntity<String> getGstr2APRMatching(
			@RequestBody String jsonString) {
		try {
			Gson gson = GsonUtil.newSAPGsonInstance();
						
			List<GSTR2APRMatchingDto> respList = 
					new ArrayList<GSTR2APRMatchingDto>();
			GSTR2APRMatchingDto dto =new GSTR2APRMatchingDto();
			dto.setSection("B2B");
			dto.setDiffCount(40);
			dto.setCount2A(20);
			dto.setTaxableValue2A(new BigDecimal(100.0));
			dto.setIgst2A(new BigDecimal(50.0));
			dto.setCgst2A(new BigDecimal(20.0));
			dto.setSgst2A(new BigDecimal(30.0));
			dto.setCess2A(new BigDecimal(40.0));
			dto.setPrCount(40);
			dto.setPrTaxableValue(new BigDecimal(20.0));
			dto.setPrIgst(new BigDecimal(40.0));
			dto.setPrCgst(new BigDecimal(30.0));
			dto.setPrSgst(new BigDecimal(35.0));
			dto.setPrCess(new BigDecimal(50.0));
			respList.add(dto);
			
			GSTR2APRMatchingDto dto1 =new GSTR2APRMatchingDto();
			dto1.setSection("B2B");
			dto1.setDiffCount(60);
			dto1.setCount2A(40);
			dto1.setTaxableValue2A(new BigDecimal(90.0));
			dto1.setIgst2A(new BigDecimal(60.0));
			dto1.setCgst2A(new BigDecimal(20.0));
			dto1.setSgst2A(new BigDecimal(40.0));
			dto1.setCess2A(new BigDecimal(30.0));
			dto1.setPrCount(30);
			dto1.setPrTaxableValue(new BigDecimal(30.0));
			dto1.setPrIgst(new BigDecimal(50.0));
			dto1.setPrCgst(new BigDecimal(30.0));
			dto1.setPrSgst(new BigDecimal(35.0));
			dto1.setPrCess(new BigDecimal(50.0));
			respList.add(dto1);

			
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
*/