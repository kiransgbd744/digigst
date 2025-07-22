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

import com.ey.advisory.app.docs.dto.gstr2.TaxCreditMatchingReportsDto;
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
public class TaxCreditMatchReportController {
	

	private static final Logger LOGGER = LoggerFactory
			.getLogger(TaxCreditMatchReportController.class);

	
	@RequestMapping(value = "/ui/taxMatchingReport", method = 
	RequestMethod.POST, produces = {
	MediaType.APPLICATION_JSON_VALUE })

public ResponseEntity<String> getGstr2APRMatching(
	@RequestBody String jsonString) {
try {
	Gson gson = GsonUtil.newSAPGsonInstance();
		
			List<TaxCreditMatchingReportsDto> respList = 
					new ArrayList<TaxCreditMatchingReportsDto>();
	TaxCreditMatchingReportsDto dto =new TaxCreditMatchingReportsDto();
	dto.setSno(12L);
	dto.setRequestId("1245");
	dto.setNoOfGstins(20);
	dto.setFromTaxPeriod("102018");
	dto.setToTaxPeriod("132019");
	dto.setInitiation(LocalDateTime.now());
	dto.setInitiatedBy("Siva");
	dto.setCompletion(LocalDateTime.now());
	respList.add(dto);
	
	TaxCreditMatchingReportsDto dto1 =new TaxCreditMatchingReportsDto();
	dto1.setSno(12L);
	dto1.setRequestId("1245");
	dto1.setNoOfGstins(20);
	dto1.setFromTaxPeriod("102018");
	dto1.setToTaxPeriod("032019");
	dto1.setInitiation(LocalDateTime.now());
	dto1.setInitiatedBy("Siva");
	dto1.setCompletion(LocalDateTime.now());
	respList.add(dto1);
	
	TaxCreditMatchingReportsDto dto2 =new TaxCreditMatchingReportsDto();
	dto2.setSno(32L);
	dto2.setRequestId("1345");
	dto2.setNoOfGstins(20);
	dto2.setFromTaxPeriod("102018");
	dto2.setToTaxPeriod("032019");
	dto2.setInitiation(LocalDateTime.now());
	dto2.setInitiatedBy("Hema");
	dto2.setCompletion(LocalDateTime.now());
	respList.add(dto2);
	
	TaxCreditMatchingReportsDto dto3 =new TaxCreditMatchingReportsDto();
	dto3.setSno(22L);
	dto3.setRequestId("2345");
	dto3.setNoOfGstins(20);
	dto3.setFromTaxPeriod("102018");
	dto3.setToTaxPeriod("022019");
	dto3.setInitiation(LocalDateTime.now());
	dto3.setInitiatedBy("Hema");
	dto3.setCompletion(LocalDateTime.now());
	respList.add(dto3);

	TaxCreditMatchingReportsDto dto4 =new TaxCreditMatchingReportsDto();
	dto4.setSno(14L);
	dto4.setRequestId("2345");
	dto4.setNoOfGstins(20);
	dto4.setFromTaxPeriod("102018");
	dto4.setToTaxPeriod("022019");
	dto4.setInitiation(LocalDateTime.now());
	dto4.setInitiatedBy("Mahesh");
	dto4.setCompletion(LocalDateTime.now());
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
	String msg = "Unexpected error while getting DataStatus for Gstr2";
	LOGGER.error(msg, ex);

	JsonObject resp = new JsonObject();
	resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

	return new ResponseEntity<>(resp.toString(),
			HttpStatus.INTERNAL_SERVER_ERROR);
}
}


}
