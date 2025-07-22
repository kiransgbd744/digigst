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

import com.ey.advisory.app.docs.dto.gstr2.TaxMatchResponseDto;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

@RestController
public class MatchingRespController {
	

	private static final Logger LOGGER = LoggerFactory
			.getLogger(MatchingRespController.class);
	
	@RequestMapping(value = "/ui/getMatchingResponse", method = 
			RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })

	public ResponseEntity<String> getVendorDetails(
			@RequestBody String jsonString) {
		try {
			Gson gson = GsonUtil.newSAPGsonInstance();
			
			List<TaxMatchResponseDto> respList = 
					new ArrayList<TaxMatchResponseDto>();
			TaxMatchResponseDto dto = new TaxMatchResponseDto();
			dto.setsNo(1L);
			dto.setEntityName("SAP_POC_Sandbox");
			dto.setSearchDocNo("JHGKR1");
			dto.setDocDate(LocalDate.now());
			dto.setSupplier("SAP");
			dto.setInvoiceNum("1234");
			respList.add(dto);
			
			TaxMatchResponseDto dto1 = new TaxMatchResponseDto();
			dto1.setsNo(2L);
			dto1.setEntityName("SAP_POC");
			dto1.setSearchDocNo("ABC001");
			dto1.setDocDate(LocalDate.now());
			dto1.setSupplier("SAP");
			dto1.setInvoiceNum("1289");
			respList.add(dto1);
			
			TaxMatchResponseDto dto2 = new TaxMatchResponseDto();
			dto2.setsNo(3L);
			dto2.setEntityName("SAP_SandBox");
			dto2.setSearchDocNo("ABC002");
			dto2.setDocDate(LocalDate.now());
			dto2.setSupplier("Hema");
			dto2.setInvoiceNum("1256");
			respList.add(dto2);
			
			TaxMatchResponseDto dto3 = new TaxMatchResponseDto();
			dto3.setsNo(4L);
			dto3.setEntityName("SAP_123");
			dto3.setSearchDocNo("ABC002");
			dto3.setDocDate(LocalDate.now());
			dto3.setSupplier("Siva");
			dto3.setInvoiceNum("2561");
			respList.add(dto3);
			
			TaxMatchResponseDto dto4 = new TaxMatchResponseDto();
			dto4.setsNo(5L);
			dto4.setEntityName("SAP_112");
			dto4.setSearchDocNo("ABC003");
			dto4.setDocDate(LocalDate.now());
			dto4.setSupplier("Mahesh");
			dto4.setInvoiceNum("2564");
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
			String msg = "Unexpected error while getting Tax "
					+ "Matching Response  for Gstr2";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}



}
