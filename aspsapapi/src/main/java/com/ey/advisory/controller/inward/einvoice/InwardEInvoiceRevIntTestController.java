package com.ey.advisory.controller.inward.einvoice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.vendor.service.VendorValidatorApiRevIntgHandler;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.PayloadDocsRevIntegrationReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author vishal.verma
 *
 */

@Slf4j
@RestController
public class InwardEInvoiceRevIntTestController {

	/*@Autowired
	@Qualifier("InwardEInvoiceRevIntgServiceImpl")
	private InwardEInvoiceRevIntgService service;*/
	
	@Autowired
	@Qualifier("VendorValidatorApiRevIntgHandler")
	private VendorValidatorApiRevIntgHandler service;
	/*
	@Autowired
	@Qualifier("Gstr2ProcessedReconTypeTaggingReportServiceImpl")
	private Gstr2ProcessedReconTypeTaggingReportServiceImpl service;
	*/
	/*
	 * @Autowired
	 * 
	 * @Qualifier("EinvoiceDetailedLineItemAndNestedReportSFTPService") private
	 * EinvoiceDetailedLineItemAndNestedReportSFTPService service;
	 * 
	 * 
	 * 
	 * @Autowired
	 * 
	 * @Qualifier("PaymentReference180DaysPayloadMetadataRevIntHandler") private
	 * PaymentReference180DaysPayloadMetadataRevIntHandler service;
	 * 
	 */

	@RequestMapping(value = "/ui/inwardEinvoiceRevIntTest", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> ewb3WaySummaryInitiateRecon(
			@RequestBody String jsonString) {

		JsonObject requestObject = JsonParser.parseString(jsonString)
				.getAsJsonObject();

		JsonObject jsonObj = new JsonObject();

		JsonObject reqJson = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();

		try {

			/*InwardEInvoiceRevIntgReqDto criteria = gson.fromJson(reqJson,
					InwardEInvoiceRevIntgReqDto.class);
*/
			
			  PayloadDocsRevIntegrationReqDto criteria = gson.fromJson(reqJson,
			  PayloadDocsRevIntegrationReqDto.class);//
			 

			// Invoking RevInt Service for Testing.

			// Integer inwardEInvoiceErpPush =
			// service.getPayloadMetadataDetails(criteria);
			// getInwardEInvoiceErpPush(criteria);

			/*Integer inwardEInvoiceErpPush = service.getDataForErpPush(criteria);
					//.getInwardEInvoiceErpPush(criteria);
					 * 
*/
			  	service.getDataForErpPush(criteria);		  
			 
			JsonElement respBody = gson.toJsonTree("Done");
			jsonObj.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			jsonObj.add("resp", respBody);

			return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "InwardEInvoiceGstinLevelController- "
					+ "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "InwardEInvoiceGstinLevelController "
					+ "Unexpected error while retriving Data Status ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

}
