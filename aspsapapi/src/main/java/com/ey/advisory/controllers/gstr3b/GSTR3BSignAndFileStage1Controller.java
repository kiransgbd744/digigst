package com.ey.advisory.controllers.gstr3b;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Clob;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;
import com.google.common.base.Strings;
import org.apache.poi.util.IOUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.app.data.entities.client.SignAndFileEntity;
import com.ey.advisory.app.data.repositories.client.SignAndFileRepository;
import com.ey.advisory.app.docs.dto.gstr1.SignAndFileReqDto;
import com.ey.advisory.app.docs.dto.gstr3B.Gstr3BFilingDetailsDTO;
import com.ey.advisory.app.docs.dto.gstr3B.Gstr3BSignFilePanDetailsDTO;
import com.ey.advisory.app.gstr3b.Gstr3BSignAndFileService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr3bGetInvoicesReqDto;
import com.ey.advisory.gstnapi.domain.client.GstnAPIGstinConfig;
import com.ey.advisory.gstnapi.repositories.client.GstnAPIGstinConfigRepository;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class GSTR3BSignAndFileStage1Controller {

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private GSTNAuthTokenService authTokenService;

	@Autowired
	private GstnAPIGstinConfigRepository gstinConfigRepo;

	@Autowired
	private SignAndFileRepository signAndFileRepo;

	@Autowired
	private Gstr3BSignAndFileService gstr3BSignAndFileService;

	@PostMapping(value = "/ui/GSTR3BSignAndFileStage1", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> execSignAndFileStage1(
			@RequestBody String jsonParam, HttpServletResponse response)
			throws IOException {
		JsonObject errorResp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		Gstr3bGetInvoicesReqDto gstr3BSummaryDto = null;
		SignAndFileReqDto dto = null;
		try {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"GSTR3BSignAndFileStage1 Request received from UI as {} ",
						jsonParam);
			}

			String isNil = "";

			JsonObject requestObject = (new JsonParser()).parse(jsonParam)
					.getAsJsonObject();
			JsonObject reqObject = requestObject.get("req").getAsJsonObject();
			if (reqObject.has("isNil")) {
			    isNil = reqObject.get("isNil").getAsString();
			    
			}
			dto = gson.fromJson(reqObject, SignAndFileReqDto.class);

			gstr3BSummaryDto = new Gstr3bGetInvoicesReqDto();
			gstr3BSummaryDto.setGstin(dto.getGstin());
			gstr3BSummaryDto.setTaxPeriod(dto.getTaxPeriod());
			APIRespDto apiResp = null;
			/*
			 * JsonArray respBody = isAuthTokenActive(gstr3BSummaryDto);
			 * APIRespDto apiResp = null;
			 * 
			 * if (respBody.size() != 0) { String errMsg =
			 * "Auth token is InActive, Please activate and try again";
			 * LOGGER.error(errMsg);
			 * 
			 * saveEntryIntoSignTable(dto, null, null, null,
			 * APIConstants.GSTR3B, errMsg, "Failed");
			 * 
			 * JsonObject resp = new JsonObject(); apiResp =
			 * APIRespDto.creatErrorResp(); resp.add("hdr",
			 * gson.toJsonTree(apiResp)); resp.add("resp",
			 * gson.toJsonTree(respBody)); return new
			 * ResponseEntity<>(resp.toString(), HttpStatus.OK); }
			 */
			Pair<Boolean, String> gstr3BSummaryJsonPair = null;
			if ("Y".equalsIgnoreCase(isNil)) {
				JsonObject jsonObject = new JsonObject();

				jsonObject.addProperty("gstin", dto.getGstin());
				jsonObject.addProperty("ret_period", dto.getTaxPeriod());
				jsonObject.addProperty("isnil", "Y");
				gstr3BSummaryJsonPair = new Pair<Boolean, String>(true,
						jsonObject.toString());
			} else {
				gstr3BSummaryJsonPair = gstr3BSignAndFileService
						.getGstr3BGstnSummary(gstr3BSummaryDto);
			}
			boolean isGet3BSummSuccess = gstr3BSummaryJsonPair.getValue0();
			if (!isGet3BSummSuccess) {

				saveEntryIntoSignTable(dto, null, null, null,
						APIConstants.GSTR3B, gstr3BSummaryJsonPair.getValue1(),
						"Failed");

				JsonObject resp = new JsonObject();
				apiResp = APIRespDto.creatErrorResp();
				resp.add("hdr", gson.toJsonTree(apiResp));
				resp.add("resp",
						gson.toJsonTree(gstr3BSummaryJsonPair.getValue1()));
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			}
			String sha256FinalHash = null;
			try {

				String jsonString = gstr3BSummaryJsonPair.getValue1();
				/*ObjectMapper objectMapper = new ObjectMapper();

				JsonNode jsonNode = objectMapper.readTree(jsonString);
				JsonNode txPyNode = jsonNode.at("/tx_pmt");
				String partB = objectMapper.writeValueAsString(txPyNode);

				ObjectNode jsonObject = (ObjectNode) jsonNode;
				jsonObject.remove("tx_pmt");

				String partA = objectMapper.writeValueAsString(jsonObject);*/
				
				JsonObject obj = JsonParser.parseString(jsonString)
						.getAsJsonObject();
				JsonObject tx_pmt = (JsonObject) obj.get("tx_pmt");
				obj.remove("tx_pmt");
				
				String partB = tx_pmt.toString();
				String partA = obj.toString();
				
				if (LOGGER.isDebugEnabled()) {
					String msg = String
							.format(" Part A -> %s ,  Part B ->  %s", partA, partB);
					LOGGER.debug(msg);
				}

				String sha256hexpartA = Hashing.sha256()
						.hashString(partA,
								StandardCharsets.UTF_8)
						.toString();
				
				String sha256hexpartB = Hashing.sha256()
						.hashString(partB,
								StandardCharsets.UTF_8)
						.toString();
				
				sha256FinalHash = Hashing.sha256()
						.hashString( sha256hexpartA+ sha256hexpartB,
								StandardCharsets.UTF_8)
						.toString();
				

				if (LOGGER.isDebugEnabled()) {
				
					LOGGER.debug("Part A hash ->  {} ,  Part B hash ->  {} , Final Hash  ->  {}"
						,sha256hexpartA, sha256hexpartB,sha256FinalHash);
				}

			} catch (Exception ex) {
				String msg = "Exception while encrypting the GET3B summary data";
				LOGGER.error(msg, ex);
				throw new AppException(msg, ex);
			}

			/*
			 * String sha256hex = Hashing.sha256() .hashString( new
			 * String(inputJsonBytes, StandardCharsets.UTF_8),
			 * StandardCharsets.UTF_8) .toString();
			 */
			GstnAPIGstinConfig gstinConfig = gstinConfigRepo
					.findByGstin(dto.getGstin());
			String gstinUserName = gstinConfig.getGstinUserName();

			SignAndFileEntity entity = saveEntryIntoSignTable(dto,
					gstr3BSummaryJsonPair.getValue1(), sha256FinalHash,
					gstinUserName, APIConstants.GSTR3B, null, "Initiated");

			Properties prop = readPropertiesFile("signandfile.properties");
			String url = prop.getProperty("url");
			String fileName = "SignAndFile".concat(dto.getGstin()).concat("_")
					.concat(dto.getTaxPeriod());
			String jnlpString = genJnlpString();
			String groupCode = TenantContext.getTenantId();
			byte[] bytes = String
					.format(jnlpString, url, url, url,
							"signreturn/GSTR3BSignAndFileStage2.do", groupCode,
							entity.getId(), sha256FinalHash)
					.getBytes(StandardCharsets.UTF_8);
			InputStream inputStream = new ByteArrayInputStream(bytes);
			response.setContentType(
					"application/x-java-jnlp-file;charset=UTF-8");

			response.setHeader("Content-Disposition", String
					.format("attachment; filename =%s ", fileName + ".jnlp"));
			IOUtils.copy(inputStream, response.getOutputStream());
			response.flushBuffer();
			return new ResponseEntity<>(null, HttpStatus.OK);
		} catch (Exception e) {
			String errorMsg = "Application Error";
			saveEntryIntoSignTable(dto, null, null, null, APIConstants.GSTR3B,
					errorMsg, "Failed");

			JsonArray respBody = new JsonArray();
			JsonObject json = new JsonObject();
			json.addProperty("msg", e.getMessage());
			respBody.add(json);
			LOGGER.error("Message", e);
			errorResp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			errorResp.add("resp", gson.toJsonTree(respBody));
			return new ResponseEntity<>(errorResp.toString(), HttpStatus.OK);
			// throw new InvalidAPIResponseException(e.getMessage());
		}
	}

	private SignAndFileEntity saveEntryIntoSignTable(SignAndFileReqDto dto,
			String gstr3BSummaryJson, String sha256hex, String gstnUserName,
			String returnType, String errorMsg, String status) {

		String userName = (SecurityContext.getUser() != null
				&& SecurityContext.getUser().getUserPrincipalName() != null)
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM";
		SignAndFileEntity entity = new SignAndFileEntity();
		entity.setGstin(dto.getGstin());
		entity.setTaxPeriod(dto.getTaxPeriod());
		entity.setPan(dto.getPan());
		if(LOGGER.isDebugEnabled())
		{
			LOGGER.debug("Final Hash in entity --> {}",sha256hex);
		}
		entity.setHashPayload(sha256hex);
		entity.setGstinUserName(gstnUserName);
		entity.setReturnType(returnType);
		entity.setProcessingKey(
				dto.getGstin().concat("|").concat(dto.getTaxPeriod()));
		entity.setErrorMsg(errorMsg);
		entity.setStatus(status);
		entity.setCreatedBy(userName);
		entity.setCreatedOn(LocalDateTime.now());
		entity.setModifiedOn(LocalDateTime.now());

		if (!Strings.isNullOrEmpty(gstr3BSummaryJson)) {
			Clob responseClob = GenUtil.convertStringToClob(gstr3BSummaryJson);
			entity.setPayload(responseClob);
			JsonObject requestObject = JsonParser.parseString(gstr3BSummaryJson)
					.getAsJsonObject();

			if (requestObject.has(APIIdentifiers.ISNIL)
					&& APIConstants.Y.equalsIgnoreCase(requestObject
							.get(APIIdentifiers.ISNIL).getAsString())) {
				entity.setFilingType("Nil Filing");
			}

		}
		entity.setFilingMode("DSC");
		return signAndFileRepo.save(entity);

	}

	private String genJnlpString() {

		return "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
				+ "\t<jnlp spec=\"1.0+\" codebase=\"%ssignreturn\" >\n"
				+ "\t<information>\n" + "\t\t<title>Digital Signer</title>\n"
				+ "\t\t<vendor>EY</vendor>\n" + "\t\t<homepage href=\"%s\" />\n"
				+ "\t\t<description>Digital Signing Tool</description>\n"
				+ "\t</information>\n" + "\t<security>\n"
				+ "\t\t<all-permissions/>\n" + "\t</security>\n"
				+ "\t<resources>\n" + "\t\t<j2se version=\"1.7+\" />\n"
				+ "\t\t<jar href=\"bulkSignJnlp.jar\" />\n" + "\t</resources>\n"
				+ "\t<application-desc main-class=\"com.ey.sign.BulkDigiSigner\">\n"
				+ "\t\t<argument>%s---%s---%s---%s---%s</argument>\n"
				+ "\t</application-desc>\n"
				+ "\t<update check=\"background\"/>\n" + "</jnlp>\n";

	}

	private JsonArray isAuthTokenActive(Gstr3bGetInvoicesReqDto dto) {

		JsonArray respBody = new JsonArray();
		String gstin = dto.getGstin();
		String authStatus = authTokenService.getAuthTokenStatusForGstin(gstin);
		if (!"A".equalsIgnoreCase(authStatus)) {
			String msg = "Auth Token is Inactive, Please Activate";
			JsonObject json = new JsonObject();
			json.addProperty("gstin", gstin);
			json.addProperty("msg", msg);
			respBody.add(json);
		}

		return respBody;
	}

	private Properties readPropertiesFile(String fileName) throws IOException {
	    Properties prop = new Properties();
	    try (InputStream stream = this.getClass().getClassLoader().getResourceAsStream(fileName)) {
	        if (stream == null) {
	            throw new FileNotFoundException("Property file '" + fileName + "' not found in the classpath.");
	        }
	        prop.load(stream);
	    } catch (FileNotFoundException fnfe) {
	        String msg = "File not found: " + fileName;
	        LOGGER.error(msg, fnfe);
	        throw fnfe;
	    } catch (IOException ioe) {
	        String msg = "Error reading properties file: " + fileName;
	        LOGGER.error(msg, ioe);
	        throw ioe;
	    }
	    return prop;}

	@PostMapping(value = "/ui/getFilingStatus", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getFilingDetails(
			@RequestBody String jsonReq) {

		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonReq)
					.getAsJsonObject();
			Gson gson = GsonUtil.newSAPGsonInstance();

			JsonObject json = requestObject.get("req").getAsJsonObject();

			String gstin = json.get("gstin").getAsString();
			String taxPeriod = json.get("taxPeriod").getAsString();

			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("Begin get Filing status in GSTR3b with gstin : %s , taxPeriod"
								+ ": %s", gstin, taxPeriod);
				LOGGER.debug(msg);
			}

			List<Gstr3BFilingDetailsDTO> fileStatusList = gstr3BSignAndFileService
					.getGstr3bFilingDetails(gstin, taxPeriod);

			JsonObject statusResp = new JsonObject();
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(fileStatusList);
			statusResp.add("details", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", statusResp);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String msg = " Unexpected error occured ";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/ui/get3BSignAndFilePanDetails", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> get3BSignAndFilePanDetails(
			@RequestBody String jsonReq) {

		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonReq)
					.getAsJsonObject();
			Gson gson = GsonUtil.newSAPGsonInstance();

			JsonObject json = requestObject.get("req").getAsJsonObject();

			String gstin = json.get("gstin").getAsString();

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Begin get panDetails  in GSTR3b sign & file with gstin : %s ",
						gstin);
				LOGGER.debug(msg);
			}

			List<Gstr3BSignFilePanDetailsDTO> panDetailList = gstr3BSignAndFileService
					.getPanDetails(gstin);

			JsonObject statusResp = new JsonObject();
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(panDetailList);
			statusResp.add("panDetails", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", statusResp);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String msg = " Unexpected error occured while getting pan details ";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@GetMapping(value = "/ui/downloadGstr3BErrors", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void downloadSaveStatusRecords(HttpServletRequest request,
			HttpServletResponse response) {
		JsonObject resp = new JsonObject();

		try {
			String fileId = request.getParameter("id");

			if (fileId == null || fileId.isEmpty()) {
				resp.add("hdr", new Gson()
						.toJsonTree(new APIRespDto("E", "Id is Mandatory")));
				response.getWriter().println(resp.toString());
				return;
			}

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Begin download Gstr3b filing Errors with id : %s ",
						fileId);
				LOGGER.debug(msg);
			}
			gstr3BSignAndFileService.downloadGstr3bErrorResp(fileId, response);
		} catch (Exception ex) {
			String msg = " Exception while Downloading the Gstr3b filing Errors  ";
			LOGGER.error(msg, ex);
		}
	}

	/*public static void main(String[] args)
			throws JsonMappingException, JsonProcessingException, UnsupportedEncodingException {
		String jsonString ="{\"gstin\":\"33GSPTN0481G1ZA\",\"ret_period\":\"122017\",\"sup_details\":{\"osup_det\":{\"txval\":10000.0,\"iamt\":100.0,\"camt\":300.0,\"samt\":300.0,\"csamt\":0.0},\"osup_zero\":{\"txval\":67500.0,\"iamt\":500.0,\"camt\":0.0,\"samt\":0.0,\"csamt\":0.0},\"osup_nil_exmp\":{\"txval\":43000.0,\"iamt\":0.0,\"camt\":0.0,\"samt\":0.0,\"csamt\":0.0},\"isup_rev\":{\"txval\":0.0,\"iamt\":0.0,\"camt\":0.0,\"samt\":0.0,\"csamt\":0.0},\"osup_nongst\":{\"txval\":24500.0,\"iamt\":0.0,\"camt\":0.0,\"samt\":0.0,\"csamt\":0.0}},\"inter_sup\":{\"unreg_details\":[{\"pos\":\"01\",\"txval\":100.0,\"iamt\":30.0}],\"comp_details\":[{\"pos\":\"27\",\"txval\":2500.0,\"iamt\":10.0}],\"uin_details\":[{\"pos\":\"27\",\"txval\":2600.0,\"iamt\":10.0}]},\"itc_elg\":{\"itc_avl\":[{\"ty\":\"IMPG\",\"iamt\":0.0,\"camt\":0.0,\"samt\":0.0,\"csamt\":0.0},{\"ty\":\"IMPS\",\"iamt\":0.0,\"camt\":0.0,\"samt\":0.0,\"csamt\":0.0},{\"ty\":\"ISRC\",\"iamt\":0.0,\"camt\":0.0,\"samt\":0.0,\"csamt\":0.0},{\"ty\":\"ISD\",\"iamt\":0.0,\"camt\":0.0,\"samt\":0.0,\"csamt\":0.0},{\"ty\":\"OTH\",\"iamt\":0.0,\"camt\":0.0,\"samt\":0.0,\"csamt\":0.0}],\"itc_rev\":[{\"ty\":\"RUL\",\"iamt\":0.0,\"camt\":0.0,\"samt\":0.0,\"csamt\":0.0},{\"ty\":\"OTH\",\"iamt\":0.0,\"camt\":0.0,\"samt\":0.0,\"csamt\":0.0}],\"itc_net\":{\"iamt\":0.0,\"camt\":0.0,\"samt\":0.0,\"csamt\":0.0},\"itc_inelg\":[{\"ty\":\"RUL\",\"iamt\":0.0,\"camt\":0.0,\"samt\":0.0,\"csamt\":0.0},{\"ty\":\"OTH\",\"iamt\":0.0,\"camt\":0.0,\"samt\":0.0,\"csamt\":0.0}]},\"inward_sup\":{\"isup_details\":[{\"ty\":\"GST\",\"inter\":0.0,\"intra\":0.0},{\"ty\":\"NONGST\",\"inter\":0.0,\"intra\":0.0}]},\"intr_ltfee\":{\"intr_details\":{\"iamt\":0.0,\"camt\":0.0,\"samt\":0.0,\"csamt\":0.0},\"ltfee_details\":{\"iamt\":0.0,\"camt\":0.0,\"samt\":0.0,\"csamt\":0.0}},\"tx_pmt\":{\"tx_py\":[{\"tran_desc\":\"Other than Reverse Charge\",\"liab_ldg_id\":0,\"trans_typ\":30002,\"igst\":{\"tx\":600.0,\"intr\":0.0,\"fee\":0.0},\"cgst\":{\"tx\":300.0,\"intr\":0.0,\"fee\":0.0},\"sgst\":{\"tx\":300.0,\"intr\":0.0,\"fee\":0.0},\"cess\":{\"tx\":0.0,\"intr\":0.0,\"fee\":0.0}},{\"tran_desc\":\"Reverse Charge\",\"liab_ldg_id\":0,\"trans_typ\":30003,\"igst\":{\"tx\":0.0,\"intr\":0.0,\"fee\":0.0},\"cgst\":{\"tx\":0.0,\"intr\":0.0,\"fee\":0.0},\"sgst\":{\"tx\":0.0,\"intr\":0.0,\"fee\":0.0},\"cess\":{\"tx\":0.0,\"intr\":0.0,\"fee\":0.0}}]}}";
				
				
				//apper instance
		//String jsonString = gstr3BSummaryJsonPair.getValue1();
		ObjectMapper objectMapper = new ObjectMapper();

		JsonNode jsonNode = objectMapper.readTree(jsonString);
		JsonNode txPyNode = jsonNode.at("/tx_pmt");
		String partB = objectMapper.writeValueAsString(txPyNode);

		ObjectNode jsonObject = (ObjectNode) jsonNode;
		jsonObject.remove("tx_pmt");

		String partA = objectMapper.writeValueAsString(jsonObject);
		

		System.out.println("PartA"+ partA);
		
		System.out.println("PartB"+ partB);

		String sha256hexpartA = Hashing.sha256()
				.hashString(
						new String(
								Base64.getEncoder().encode(
										partA.getBytes("UTF-8")),
								StandardCharsets.UTF_8),
						StandardCharsets.UTF_8)
				.toString();
		System.out.println("PartA hash  ->" + sha256hexpartA);

		String sha256hexpartB = Hashing.sha256()
				.hashString(
						new String(
								Base64.getEncoder().encode(
										partB.getBytes("UTF-8")),
								StandardCharsets.UTF_8),
						StandardCharsets.UTF_8)
				.toString();
		
		System.out.println("PartB hash -->" + sha256hexpartB);

		String sha256FinalHash = Hashing.sha256()
				.hashString(
						new String(
								Base64.getEncoder()
										.encode((sha256hexpartA
												+ sha256hexpartB)
														.getBytes(
																"UTF-8")),
								StandardCharsets.UTF_8),
						StandardCharsets.UTF_8)
				.toString();
		
		System.out.println(sha256FinalHash);
		
		LOGGER.debug(" Part A hash ->  {},  Part B hash ->  {}, Final Hash  ->  {}"
						,sha256hexpartA, sha256hexpartB,sha256FinalHash);
		
	}
*/
}
