package com.ey.advisory.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.sql.Clob;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ContentDisposition;
import org.springframework.http.ContentDisposition.Builder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.entities.client.SignAndFileEntity;
import com.ey.advisory.app.data.repositories.client.SignAndFileRepository;
import com.ey.advisory.app.docs.dto.anx2.Anx2GetInvoicesReqDto;
import com.ey.advisory.app.docs.dto.gstr1.SignAndFileReqDto;
import com.ey.advisory.app.services.jobs.anx2.Gstr7SummaryAtGstnImpl;
import com.ey.advisory.common.APICryptoException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.gstnapi.domain.client.GstnAPIGstinConfig;
import com.ey.advisory.gstnapi.repositories.client.GstnAPIGstinConfigRepository;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author SriBhavya
 *
 */
@Slf4j
@RestController
public class Gstr7SignAndFileStage1Controller {

	@Autowired
	@Qualifier("Gstr7SummaryAtGstnImpl")
	private Gstr7SummaryAtGstnImpl gstnGetSummary;

	@Autowired
	private SignAndFileRepository signAndFileRepo;

	@Autowired
	private GstnAPIGstinConfigRepository gstinConfigRepo;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private GSTNAuthTokenService authTokenService;

	@PostMapping(value = "/ui/gstr7ExecSignAndFileStage1", produces = {
			"application/x-java-jnlp-file;charset=UTF-8" })
	public HttpEntity<byte[]> execSignAndFileStage1(
			@RequestBody String jsonParam, HttpServletResponse response)
			throws IOException {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"execSignAndFileStage1 Request received from UI as {} ",
					jsonParam);
		}
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject requestObject = (new JsonParser()).parse(jsonParam)
				.getAsJsonObject();
		JsonObject reqObject = requestObject.get("req").getAsJsonObject();
		SignAndFileReqDto dto = gson.fromJson(reqObject,
				SignAndFileReqDto.class);

		String groupCode = TenantContext.getTenantId();
		TenantContext.setTenantId(groupCode);

		Anx2GetInvoicesReqDto summaryDto = new Anx2GetInvoicesReqDto();
		summaryDto.setGstin(dto.getGstin());
		summaryDto.setReturnPeriod(dto.getTaxPeriod());
		summaryDto.setGroupcode(groupCode);

		JsonArray respBody = isAuthTokenActive(summaryDto);
		APIRespDto apiResp = null;

		if (respBody != null && !respBody.equals(new JsonArray())) {
			LOGGER.error("Sign and File - Auth token got expired.");
			JsonObject resp = new JsonObject();
			apiResp = APIRespDto.creatErrorResp();
			resp.add("hdr", gson.toJsonTree(apiResp));
			resp.add("resp", gson.toJsonTree(respBody));
			byte[] bytes = resp.toString().getBytes();
			return new HttpEntity<byte[]>(bytes);
		}

		String gstr7SummaryJson = gstnGetSummary
				.getGstr7Summary(summaryDto, groupCode).toString();

		// Hard coded payload for testing.
		// String gstr1SummaryJson =
		// "{'gstin':'33GSPTN0481G1ZA','ret_period':'062018','chksum':'AflJufPlFStqKBZ','summ_typ':'L','sec_sum':[{'sec_nm':'b2b','chksum':'AflJufPlFStqKBZ','ttl_rec':10,'ttl_val':12345,'ttl_igst':124.99,'ttl_cgst':3423,'ttl_sgst':5589.87,'ttl_cess':3423,'ttl_tax':1234,'cpty_sum':[{'ctin':'20GRRHF2562D3A3','chksum':'AflJufPlFStqKBZ','ttl_rec':10,'ttl_val':12345,'ttl_igst':124.99,'ttl_cgst':3423,'ttl_sgst':5589.87,'ttl_cess':3423,'ttl_tax':1234}]}]}";

		String jnlpString = genJnlpString();

		byte[] inputJsonBytes;
		try {
			inputJsonBytes = Base64.getEncoder()
					.encode(gstr7SummaryJson.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException ex) {
			String msg = "Exception while encrypting the GET summary data";
			LOGGER.error(msg, ex);
			throw new APICryptoException(msg, ex);
		}

		String sha256hex = Hashing.sha256()
				.hashString(new String(inputJsonBytes, StandardCharsets.UTF_8),
						StandardCharsets.UTF_8)
				.toString();
		GstnAPIGstinConfig gstinConfig = gstinConfigRepo
				.findByGstin(dto.getGstin());
		String gstinUserName = gstinConfig.getGstinUserName();

		if (gstinUserName == null) {
			LOGGER.error("Sign and File - NULL username found.");
			byte[] bytes = String
					.format("No valid UserName foud for the GSTIN %s",
							dto.getGstin())
					.getBytes();
			return new HttpEntity<byte[]>(bytes);
		}

		SignAndFileEntity entity = saveEntryIntoSignTable(summaryDto,
				gstr7SummaryJson, sha256hex, gstinUserName, APIConstants.GSTR7);

		Properties prop = readPropertiesFile("signandfile.properties");
		String url = prop.getProperty("url");

		byte[] bytes = String
				.format(jnlpString, url, url, url,
						"signreturn/gstr7ExecSignAndFileStage2.do",
						gstinUserName, entity.getId(), sha256hex)
				.getBytes(StandardCharsets.UTF_8);

		HttpHeaders header = new HttpHeaders();
		header.setContentType(new MediaType("application", "x-java-jnlp-file",
				StandardCharsets.UTF_8));
		Builder builder = ContentDisposition.builder("attachment");
		builder.filename("SignAndFile".concat(dto.getGstin()).concat("_")
				.concat(dto.getTaxPeriod()).concat(".jnlp"));
		header.setContentDisposition(builder.build());
		header.setContentLength(bytes.length);

		return new HttpEntity<byte[]>(bytes, header);

	}

	private SignAndFileEntity saveEntryIntoSignTable(Anx2GetInvoicesReqDto dto,
			String gstr1SummaryJson, String sha256hex, String gstnUserName,
			String returnType) {
		String userName = SecurityContext.getUser().getUserPrincipalName();
		SignAndFileEntity entity = new SignAndFileEntity();
		entity.setGstin(dto.getGstin());
		entity.setTaxPeriod(dto.getReturnPeriod());
		entity.setHashPayload(sha256hex);
		entity.setGstinUserName(gstnUserName);
		entity.setReturnType(returnType);
		entity.setProcessingKey(
				dto.getGstin().concat("|").concat(dto.getReturnPeriod()));

		entity.setCreatedBy(userName);
		entity.setCreatedOn(
				EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));

		Clob responseClob = null;
		try {
			responseClob = new javax.sql.rowset.serial.SerialClob(
					gstr1SummaryJson.toCharArray());

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("Response clob %s ",
						 (responseClob != null && responseClob.length() > 0) ? responseClob.toString()
								: "Null Gsnt GET Summary data");
				LOGGER.debug(msg);
			}

			entity.setPayload(responseClob);

		} catch (SQLException e) {

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("Exception occured %s", e);
				LOGGER.error(msg);
			}
		}
		return signAndFileRepo.save(entity);

	}

	private String genJnlpString() {

		return "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
				+ "\t<jnlp spec=\"1.0+\" codebase=\"%s/signreturn\" >\n"
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

	private Properties readPropertiesFile(String fileName) throws IOException {
		Properties prop = new Properties();

		try (InputStream stream = this.getClass().getClassLoader()
				.getResourceAsStream(fileName)) {
			if (stream == null) {
				throw new FileNotFoundException("Property file '" + fileName
						+ "' not found in the classpath");
			}
			prop.load(stream);
		} catch (FileNotFoundException fnfe) {
			LOGGER.error("File not found: {}", fileName, fnfe);
			throw fnfe; // Rethrow the exception to ensure it propagates
		} catch (IOException ioe) {
			LOGGER.error("Error reading the property file: {}", fileName, ioe);
			throw ioe; // Rethrow the exception to ensure it propagates
		}

		return prop;
	}

	private JsonArray isAuthTokenActive(Anx2GetInvoicesReqDto dto) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Getting all the active GSTN for GSTR1 Sign and File");
		}
		String msg = "";
		JsonArray respBody = new JsonArray();
		if (dto != null) {
			String gstin = dto.getGstin();
			JsonObject json = new JsonObject();
			String authStatus = authTokenService
					.getAuthTokenStatusForGstin(gstin);
			if (!"A".equalsIgnoreCase(authStatus)) {
				msg = "Auth Token is Inactive, Please Activate";
				json.addProperty("gstin", gstin);
				json.addProperty("msg", msg);
				respBody.add(json);
			}
		}
		return respBody;
	}

}