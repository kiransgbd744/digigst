package com.ey.advisory.controller;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.sql.Clob;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Properties;

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

import com.ey.advisory.app.data.entities.client.SignAndFileEntity;
import com.ey.advisory.app.data.repositories.client.SignAndFileRepository;
import com.ey.advisory.app.docs.dto.gstr1.SignAndFileReqDto;
import com.ey.advisory.app.docs.dto.gstr3B.Gstr3BFilingDetailsDTO;
import com.ey.advisory.app.gstr3b.Gstr3BSignAndFileService;
import com.ey.advisory.app.services.jobs.anx2.Gstr6SummaryAtGstnImpl;
import com.ey.advisory.app.signinfile.Gstr6SignAndFileServiceImpl;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr6GetInvoicesReqDto;
import com.ey.advisory.gstnapi.domain.client.GstnAPIGstinConfig;
import com.ey.advisory.gstnapi.repositories.client.GstnAPIGstinConfigRepository;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
/**
 * 
 * @author SriBhavya
 *
 */
@Slf4j
@RestController
public class Gstr6SignAndFileStage1Controller {
	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private GSTNAuthTokenService authTokenService;

	@Autowired
	private GstnAPIGstinConfigRepository gstinConfigRepo;

	@Autowired
	private SignAndFileRepository signAndFileRepo;
	
	@Autowired
	@Qualifier("Gstr6SummaryAtGstnImpl")
	private Gstr6SummaryAtGstnImpl gstnGetSummary;
	
	@Autowired
	private Gstr6SignAndFileServiceImpl gstr6SignAndFileService;
	
	@Autowired
	private Gstr3BSignAndFileService gstr3BSignAndFileService;
	
	@PostMapping(value = "/ui/Gstr6SignAndFileStage1", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> execSignAndFileStage1(
			@RequestBody String jsonParam, HttpServletResponse response)
			throws IOException {
		JsonObject errorResp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		Gstr6GetInvoicesReqDto SummaryDto = null;
		SignAndFileReqDto dto = null;
		try {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr6SignAndFileStage1 Request received from UI as {} ",
						jsonParam);
			}

			JsonObject requestObject = (new JsonParser()).parse(jsonParam)
					.getAsJsonObject();
			JsonObject reqObject = requestObject.get("req").getAsJsonObject();
			dto = gson.fromJson(reqObject, SignAndFileReqDto.class);

			SummaryDto = new Gstr6GetInvoicesReqDto();
			SummaryDto.setGstin(dto.getGstin());
			SummaryDto.setReturnPeriod(dto.getTaxPeriod());
			
			JsonArray respBody = isAuthTokenActive(SummaryDto);
			APIRespDto apiResp = null;

			if (respBody.size() != 0) {
				String errMsg = "Auth token is InActive, Please activate and try again";
				LOGGER.error(errMsg);

				saveEntryIntoSignTable(dto, null, null, null,
						APIConstants.GSTR6.toUpperCase(), errMsg, "Failed");

				JsonObject resp = new JsonObject();
				apiResp = APIRespDto.creatErrorResp();
				resp.add("hdr", gson.toJsonTree(apiResp));
				resp.add("resp", gson.toJsonTree(respBody));
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			}

			Pair<Boolean, String> gstr6SummaryJsonPair = gstr6SignAndFileService.getGstr6GstnSummary(SummaryDto);
			boolean isGet6SummSuccess = gstr6SummaryJsonPair.getValue0();
			if (!isGet6SummSuccess) {

				saveEntryIntoSignTable(dto, null, null, null,
						APIConstants.GSTR6.toUpperCase(), gstr6SummaryJsonPair.getValue1(),
						"Failed");

				JsonObject resp = new JsonObject();
				apiResp = APIRespDto.creatErrorResp();
				resp.add("hdr", gson.toJsonTree(apiResp));
				resp.add("resp",
						gson.toJsonTree(gstr6SummaryJsonPair.getValue1()));
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			}
			byte[] inputJsonBytes = null;
			String modifiedSummaryJsonPair = "";
			try {
				modifiedSummaryJsonPair = setOffsetValue(gstr6SummaryJsonPair.getValue1());
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("GSTR6 modified Value is {} ", modifiedSummaryJsonPair);
				}
				inputJsonBytes = Base64.getEncoder().encode(
						modifiedSummaryJsonPair.getBytes("UTF-8"));
			} catch (UnsupportedEncodingException ex) {
				String msg = "Exception while encrypting the GETGSTR6 summary data";
				LOGGER.error(msg, ex);
				throw new AppException(msg, ex);
			}

			String sha256hex = Hashing.sha256()
					.hashString(
							new String(inputJsonBytes, StandardCharsets.UTF_8),
							StandardCharsets.UTF_8)
					.toString();
			GstnAPIGstinConfig gstinConfig = gstinConfigRepo
					.findByGstin(dto.getGstin());
			String gstinUserName = gstinConfig.getGstinUserName();

			SignAndFileEntity entity = saveEntryIntoSignTable(dto,
					modifiedSummaryJsonPair, sha256hex, gstinUserName,
					APIConstants.GSTR6.toUpperCase(), null, "Initiated");

			Properties prop = readPropertiesFile("signandfile.properties");
			String url = prop.getProperty("url");
			String fileName = "SignAndFile".concat(dto.getGstin()).concat("_")
					.concat(dto.getTaxPeriod());
			String jnlpString = genJnlpString();
			String groupCode = TenantContext.getTenantId();
			byte[] bytes = String
					.format(jnlpString, url, url, url,
							"signreturn/Gstr6SignAndFileStage2.do", groupCode,
							entity.getId(), sha256hex)
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
			if(dto!=null){
			saveEntryIntoSignTable(dto, null, null, null, APIConstants.GSTR6.toUpperCase(),
					errorMsg, "Failed");
			}
			JsonArray respBody = new JsonArray();
			JsonObject json = new JsonObject();
			json.addProperty("msg", e.getMessage());
			respBody.add(json);
			LOGGER.error("Message", e);
			errorResp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			errorResp.add("resp", gson.toJsonTree(respBody));
			return new ResponseEntity<>(errorResp.toString(), HttpStatus.OK);
		}
	}
	
	private String setOffsetValue(String value) {
		JsonObject jsonObject = JsonParser.parseString(value).getAsJsonObject();

		// Skip processing if "offset" exists
		if (jsonObject.has("offset")) {
			return jsonObject.toString();
		}

		if (jsonObject.has("lateFeemain")) {
			JsonObject lateFeemain = jsonObject.getAsJsonObject("lateFeemain");
			if (lateFeemain.has("latefee")) {
				JsonObject lateFee = lateFeemain.getAsJsonObject("latefee");

				JsonElement cLamt = lateFee.has("cLamt") ? lateFee.get("cLamt") : JsonNull.INSTANCE;
				JsonElement sLamt = lateFee.has("sLamt") ? lateFee.get("sLamt") : JsonNull.INSTANCE;

				JsonObject offset = new JsonObject();
				offset.add("cLamt", cLamt);
				offset.add("sLamt", sLamt);
				
				jsonObject.add("offset", offset);
			}
		}

		return jsonObject.toString();
	}

	private SignAndFileEntity saveEntryIntoSignTable(SignAndFileReqDto dto,
			String gstr6SummaryJson, String sha256hex, String gstnUserName,
			String returnType, String errorMsg, String status) {

		String userName = (SecurityContext.getUser() != null
				&& SecurityContext.getUser().getUserPrincipalName() != null)
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM";
		SignAndFileEntity entity = new SignAndFileEntity();
		entity.setGstin(dto.getGstin());
		entity.setTaxPeriod(dto.getTaxPeriod());
		entity.setPan(dto.getPan());
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

		Clob responseClob = GenUtil.convertStringToClob(gstr6SummaryJson);
		entity.setPayload(responseClob);
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

	private JsonArray isAuthTokenActive(Gstr6GetInvoicesReqDto dto) {

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
            throw new FileNotFoundException("Property file '" + fileName + "' not found in the classpath");
        }
        prop.load(stream);
    } catch (FileNotFoundException fnfe) {
        LOGGER.error("File not found: {}", fileName, fnfe);
        throw fnfe;
    } catch (IOException ioe) {
        LOGGER.error("Error reading the property file: {}", fileName, ioe);
        throw ioe;
    }
    return prop;
}

	@PostMapping(value = "/ui/getGstr6FilingStatus", produces = {
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

			List<Gstr3BFilingDetailsDTO> fileStatusList = gstr6SignAndFileService
					.getGstr6FilingDetails(gstin, taxPeriod);

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

	@GetMapping(value = "/ui/downloadGstr6Errors", produces = {
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
						"Begin download Gstr6 filing Errors with id : %s ",
						fileId);
				LOGGER.debug(msg);
			}
			gstr6SignAndFileService.downloadGstr6ErrorResp(fileId, response);
		} catch (Exception ex) {
			String msg = " Exception while Downloading the Gstr6 filing Errors  ";
			LOGGER.error(msg, ex);
		}
	}
}
