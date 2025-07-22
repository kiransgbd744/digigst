//package com.ey.advisory.controller.gstr2b;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.ey.advisory.common.GsonUtil;
//import com.ey.advisory.core.dto.APIRespDto;
//import com.ey.advisory.gstr2.ap.recon.Gstr2NonApManualGenerateReportService;
//import com.google.gson.Gson;
//import com.google.gson.JsonElement;
//import com.google.gson.JsonObject;
//import com.google.gson.JsonParseException;
//import com.google.gson.JsonParser;
//
//import lombok.extern.slf4j.Slf4j;
//
///**
// * @author vishal.verma
// *
// */
//
//
//	@RestController
//	@Slf4j
//	public class TestController {
//
//		@Autowired
//		@Qualifier("Gstr2NonApManualGenerateReportServiceImpl")
//		Gstr2NonApManualGenerateReportService service;
//		
//
//		@RequestMapping(value = "/ui/getTest", method = RequestMethod.POST, 
//				produces = {MediaType.APPLICATION_JSON_VALUE })
//
//		public ResponseEntity<String> createGstr2BGstnGetJob(
//				@RequestBody String request) {
//
//			try {
//				
//				Gson gson = GsonUtil.newSAPGsonInstance();
//				
//				/*JsonParser parser = new JsonParser();
//				
//				Object jsonResponse = parser.parse(
//				new FileReader("C:/Users/QD194RK/Desktop/sampleJson.json"));
//				//new FileReader("C:/Users/QD194RK/Desktop/prod2BGetJson.json"));
//				
//				
//				JsonObject requestObject = (new JsonParser()
//						.parse(jsonResponse.toString())).getAsJsonObject();
//
//				Gstr2BGETDataDto reqDto = gson.fromJson(requestObject,
//						Gstr2BGETDataDto.class);*/
//				
//				JsonObject requestObject = (new JsonParser()).parse(request)
//						.getAsJsonObject();
//				JsonObject json = requestObject.get("req").getAsJsonObject();
//
//				Long configId = json.get("configId").getAsLong();
//				
//				int	count = 0;
//				// upload data for 2b 
//				//int	count = service.pasrseResp(requestObject, reqDto);
//				
//				service.generateReport(configId);
//				
//				
//				JsonObject resp = new JsonObject();
//				JsonObject reportTypeResp = new JsonObject();
//				JsonElement respBody = gson.toJsonTree(count);
//				reportTypeResp.add("reportTypeList", respBody);
//				resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
//				resp.add("resp", reportTypeResp);
//
//				if (LOGGER.isDebugEnabled()) {
//					String msg = "End Gstr2ReconSummaryConfigIdController"
//							+ ".getConfigId, before returning response";
//					LOGGER.debug(msg);
//				}
//				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
//			} catch (
//
//			JsonParseException ex) {
//				String msg = "Error while parsing the input Json";
//				LOGGER.error(msg, ex);
//
//				JsonObject resp = new JsonObject();
//				resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
//
//				return new ResponseEntity<>(resp.toString(),
//						HttpStatus.INTERNAL_SERVER_ERROR);
//
//			} catch (Exception ex) {
//				String msg = " Unexpected error occured";
//				LOGGER.error(msg, ex);
//
//				JsonObject resp = new JsonObject();
//				resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
//
//				return new ResponseEntity<>(resp.toString(),
//						HttpStatus.INTERNAL_SERVER_ERROR);
//			}
//		}
//
//	}
