package com.ey.advisory.controller.gstr2;

import java.math.BigDecimal;
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

import com.ey.advisory.app.docs.dto.gstr2.TaxCrMatchSummaryDto;
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
public class TaxMatchSummaryController {
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(TaxMatchSummaryController.class);

	
	@RequestMapping(value = "/ui/taxMatchingSumary", method = 
	RequestMethod.POST, produces = {
	MediaType.APPLICATION_JSON_VALUE })

public ResponseEntity<String> getGstr2APRMatching(
	@RequestBody String jsonString) {
try {
	Gson gson = GsonUtil.newSAPGsonInstance();
	
	List<TaxCrMatchSummaryDto> respList = new ArrayList<TaxCrMatchSummaryDto>();
	
	TaxCrMatchSummaryDto dto = new TaxCrMatchSummaryDto();
	dto.setPerticulars("Perticular1");
	dto.setPurchaseCountOfTransn(30L);
	dto.setPurchaseRegPercentage(30L);
	dto.setPurTaxableValue(BigDecimal.valueOf(23415.0));
	dto.setPurTotalTax(BigDecimal.valueOf(35.0));
	dto.setGstr2ACountOfTransn(35L);
	dto.setGstr2APercentage(30L);
	dto.setGstr2AtaxableValue(BigDecimal.valueOf(34521.0));
	dto.setGstr2ATotalTax(BigDecimal.valueOf(12134.0));
	respList.add(dto);
	
	TaxCrMatchSummaryDto dto1 = new TaxCrMatchSummaryDto();
	dto1.setPerticulars("Perticular2");
	dto1.setPurchaseCountOfTransn(20L);
	dto1.setPurchaseRegPercentage(30L);
	dto1.setPurTaxableValue(BigDecimal.valueOf(23432.0));
	dto1.setPurTotalTax(BigDecimal.valueOf(35.0));
	dto1.setGstr2ACountOfTransn(35L);
	dto1.setGstr2APercentage(30L);
	dto1.setGstr2AtaxableValue(BigDecimal.valueOf(12321.0));
	dto1.setGstr2ATotalTax(BigDecimal.valueOf(45134.0));
	respList.add(dto1);
	
	TaxCrMatchSummaryDto dto2 = new TaxCrMatchSummaryDto();
	dto2.setPerticulars("Perticular3");
	dto2.setPurchaseCountOfTransn(30L);
	dto2.setPurchaseRegPercentage(30L);
	dto2.setPurTaxableValue(BigDecimal.valueOf(234432.0));
	dto2.setPurTotalTax(BigDecimal.valueOf(55.0));
	dto2.setGstr2ACountOfTransn(35L);
	dto2.setGstr2APercentage(30L);
	dto2.setGstr2AtaxableValue(BigDecimal.valueOf(12321.0));
	dto2.setGstr2ATotalTax(BigDecimal.valueOf(45134.0));
	respList.add(dto2);

	TaxCrMatchSummaryDto dto3 = new TaxCrMatchSummaryDto();
	dto3.setPerticulars("Perticular4");
	dto3.setPurchaseCountOfTransn(60L);
	dto3.setPurchaseRegPercentage(70L);
	dto3.setPurTaxableValue(BigDecimal.valueOf(23432.0));
	dto3.setPurTotalTax(BigDecimal.valueOf(35.0));
	dto3.setGstr2ACountOfTransn(35L);
	dto3.setGstr2APercentage(30L);
	dto3.setGstr2AtaxableValue(BigDecimal.valueOf(12321.0));
	dto3.setGstr2ATotalTax(BigDecimal.valueOf(45134.0));
	respList.add(dto3);

	
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
