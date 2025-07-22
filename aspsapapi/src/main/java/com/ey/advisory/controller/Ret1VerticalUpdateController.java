package com.ey.advisory.controller;

import java.lang.reflect.Type;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.docs.dto.simplified.Ret1AspDetailRespDto;
import com.ey.advisory.app.docs.dto.simplified.Ret1LateFeeDetailSummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Ret1PaymentTaxDetailSummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Ret1RefundDetailSummaryDto;
import com.ey.advisory.app.services.search.simplified.docsummary.Ret1InterestLateFeeUpdateService;
import com.ey.advisory.app.services.search.simplified.docsummary.Ret1PaymentUpdateService;
import com.ey.advisory.app.services.search.simplified.docsummary.Ret1RefundUpdateService;
import com.ey.advisory.app.services.search.simplified.docsummary.Ret1VerticalUpdateService;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

/**
 * 
 * @author Balakrishna.S
 *
 */
@RestController
public class Ret1VerticalUpdateController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Ret1VerticalUpdateController.class);

	@Autowired
	@Qualifier("Ret1VerticalUpdateService")
	Ret1VerticalUpdateService service;
	
	@Autowired
	@Qualifier("Ret1InterestLateFeeUpdateService")
	Ret1InterestLateFeeUpdateService lateFeeService;
	
	@Autowired
	@Qualifier("Ret1PaymentUpdateService")
	Ret1PaymentUpdateService paymentService;
	
	@Autowired
	@Qualifier("Ret1RefundUpdateService")
	Ret1RefundUpdateService refundService;

	@PostMapping(value = "/ui/ret1VerticalUpdate", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> eyAnnexure1Summary(
			@RequestBody String jsonString) {
		String groupCode = TenantContext.getTenantId();
		LOGGER.debug(String.format("Group Code = '%s'", groupCode));
		try {
			/*JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().;
		*/	Gson gson = GsonUtil.newSAPGsonInstance();

			
			JsonArray jsonObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().get("req").getAsJsonArray();
			Type listType = new TypeToken<List<Ret1AspDetailRespDto>>() {
			}.getType();
			List<Ret1AspDetailRespDto> updateGstinInfoDetails = gson
					.fromJson(jsonObject, listType);

			
			/*List<Ret1AspDetailRespDto> ret1SummaryRequest = gson
					.fromJson(updateGstinInfoDetails, listType);*/

			service.updateVerticalData(updateGstinInfoDetails);
			
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			
			return new ResponseEntity<>(resp.toString(),HttpStatus.OK);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while fecthing Annexure1 "
					+ "Summary Documents " + ex;
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	// Interest & Late Fee Update & Save 
	@PostMapping(value = "/ui/ret1IntLateFeeUpdate", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> eyLateFeeUpdate(
			@RequestBody String jsonString) {
		String groupCode = TenantContext.getTenantId();
		LOGGER.debug(String.format("Group Code = '%s'", groupCode));
		try {
			/*JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().;
		*/	Gson gson = GsonUtil.newSAPGsonInstance();

			
			JsonArray jsonObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().get("req").getAsJsonArray();
			Type listType = new TypeToken<List<Ret1LateFeeDetailSummaryDto>>() {
			}.getType();
			List<Ret1LateFeeDetailSummaryDto> updateLateFee = gson
					.fromJson(jsonObject, listType);

			
			/*List<Ret1AspDetailRespDto> ret1SummaryRequest = gson
					.fromJson(updateGstinInfoDetails, listType);*/

			lateFeeService.updateVerticalData(updateLateFee);
		//	service.updateVerticalData(updateGstinInfoDetails);
			
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			
			return new ResponseEntity<>(resp.toString(),HttpStatus.OK);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while fecthing Annexure1 "
					+ "Summary Documents " + ex;
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	// Paymentof Tax For RET1 Update & Save 
		@PostMapping(value = "/ui/ret1PaymentUpdate", produces = {
				MediaType.APPLICATION_JSON_VALUE })
		public ResponseEntity<String> eyPaymentUpdate(
				@RequestBody String jsonString) {
			String groupCode = TenantContext.getTenantId();
			LOGGER.debug(String.format("Group Code = '%s'", groupCode));
			try {

				Gson gson = GsonUtil.newSAPGsonInstance();
				JsonArray jsonObject = (new JsonParser()).parse(jsonString)
						.getAsJsonObject().get("req").getAsJsonArray();
				Type listType = new TypeToken<List<Ret1PaymentTaxDetailSummaryDto>>() {
				}.getType();
				List<Ret1PaymentTaxDetailSummaryDto> updatePayment = gson
						.fromJson(jsonObject, listType);

				paymentService.updatePaymentData(updatePayment);
				
				JsonObject resp = new JsonObject();
				resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
				
				return new ResponseEntity<>(resp.toString(),HttpStatus.OK);
			} catch (JsonParseException ex) {
				String msg = "Error while parsing the input Json";
				LOGGER.error(msg, ex);

				JsonObject resp = new JsonObject();
				resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

				return new ResponseEntity<>(resp.toString(),
						HttpStatus.INTERNAL_SERVER_ERROR);
			} catch (Exception ex) {
				String msg = "Unexpected error while fecthing Annexure1 "
						+ "Summary Documents " + ex;
				LOGGER.error(msg, ex);

				JsonObject resp = new JsonObject();
				resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

				return new ResponseEntity<>(resp.toString(),
						HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		
		//  Refund For RET1 Update & Save 
				@PostMapping(value = "/ui/ret1RefundUpdate", produces = {
						MediaType.APPLICATION_JSON_VALUE })
				public ResponseEntity<String> eyRefundUpdate(
						@RequestBody String jsonString) {
					String groupCode = TenantContext.getTenantId();
					LOGGER.debug(String.format("Group Code = '%s'", groupCode));
					try {
						/*JsonObject obj = new JsonParser().parse(jsonString)
								.getAsJsonObject();
						String reqJson = obj.get("req").getAsJsonObject().;
					*/	Gson gson = GsonUtil.newSAPGsonInstance();

						
						JsonArray jsonObject = (new JsonParser()).parse(jsonString)
								.getAsJsonObject().get("req").getAsJsonArray();
						Type listType = new TypeToken<List<Ret1RefundDetailSummaryDto>>() {
						}.getType();
						List<Ret1RefundDetailSummaryDto> updateRefund = gson
								.fromJson(jsonObject, listType);

						
						/*List<Ret1AspDetailRespDto> ret1SummaryRequest = gson
								.fromJson(updateGstinInfoDetails, listType);*/

						refundService.updateRefundData(updateRefund);
						
						JsonObject resp = new JsonObject();
						resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
						
						return new ResponseEntity<>(resp.toString(),HttpStatus.OK);
					} catch (JsonParseException ex) {
						String msg = "Error while parsing the input Json";
						LOGGER.error(msg, ex);

						JsonObject resp = new JsonObject();
						resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

						return new ResponseEntity<>(resp.toString(),
								HttpStatus.INTERNAL_SERVER_ERROR);
					} catch (Exception ex) {
						String msg = "Unexpected error while fecthing Annexure1 "
								+ "Summary Documents " + ex;
						LOGGER.error(msg, ex);

						JsonObject resp = new JsonObject();
						resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

						return new ResponseEntity<>(resp.toString(),
								HttpStatus.INTERNAL_SERVER_ERROR);
					}
				}

}
