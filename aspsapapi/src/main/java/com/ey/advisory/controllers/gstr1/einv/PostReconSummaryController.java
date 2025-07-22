package com.ey.advisory.controllers.gstr1.einv;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.app.gstr1.einv.PostReconSummaryService;
import com.ey.advisory.app.gstr1.einv.PostReconSummaryTabDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.config.ConfigConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Reddy
 *
 */

@Slf4j
@RestController
public class PostReconSummaryController {

	@Autowired
	@Qualifier("PostReconSummaryServiceImpl")
	private PostReconSummaryService postReconSummaryService;

	@Autowired
	@Qualifier("InternalHttpClient")
	private HttpClient httpClient;

	@PostMapping(value = "/ui/getPostReconSummary", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getAutoReconSummary(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Inside getPostReconSummary with request as " + " : %s",
						jsonString);
				LOGGER.debug(msg);
			}

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			JsonObject reqJson = requestObject.get("req").getAsJsonObject();

			JsonArray gstins = reqJson.getAsJsonArray("recipientGstins");

			String taxPeriod = reqJson.get("taxPeriod").getAsString();

			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();
			ArrayList<String> recipientGstins = gson.fromJson(gstins, listType);

			if (CollectionUtils.isEmpty(recipientGstins))
				throw new AppException("User did not select any gstin");

			String derviedTaxPeriod = getDerviedRetPeriod(taxPeriod);

			PostReconSummaryTabDto reconSummary = postReconSummaryService
					.getReconSummaryDetails(recipientGstins, derviedTaxPeriod);

			JsonObject resp = new JsonObject();

			String jsonReconReqData = gson.toJson(reconSummary);
			JsonElement jsonElement = new JsonParser().parse(jsonReconReqData);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", jsonElement);
			LOGGER.info("response {}", resp);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			LOGGER.error("Message", ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	public static File stream2file(InputStream in, String fileName)
			throws IOException {
		String fileNameWithoutExt = FilenameUtils.getBaseName(fileName);
		final File tempFile = File.createTempFile(fileNameWithoutExt, ".xlsx");

		tempFile.deleteOnExit();
		try (FileOutputStream out = new FileOutputStream(tempFile)) {
			IOUtils.copy(in, out);
		}
		LOGGER.debug("File Name {} and File Path {}", tempFile.getName(),
				tempFile.getAbsolutePath());
		return tempFile;
	}

	@PostMapping(value = "/api/MultiPartEmailPoc", produces = {
			MediaType.APPLICATION_XML_VALUE })
	public String multiPartEmailPoc(@RequestParam("file") MultipartFile[] files,
			@RequestParam("secondaryEmail") String secondaryEmail,
			@RequestParam("primaryEmail") String primaryEmail,
			@RequestParam("fy") String fy, HttpServletRequest request) {

		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		Gson ewbGson = GsonUtil.gsonInstanceWithEWBDateFormat();
		try {
			HttpPost httpPost = new HttpPost(
					"https://uatft.eyasp.in/aspwebemail/sap/sendRetCompEmail");
			MultipartFile file = files[0];
			InputStream stream = file.getInputStream();

			HttpEntity reqEntity = MultipartEntityBuilder.create()
					.addTextBody("primaryEmail", primaryEmail)
					.addTextBody("secondaryEmails", secondaryEmail)
					.addTextBody("entityName", "Test").addTextBody("fy", fy)
					.addBinaryBody("file",
							stream2file(stream, file.getOriginalFilename()),
							ContentType.create("application/octet-stream"),
							file.getOriginalFilename())
					.build();

			LOGGER.debug("File Original Name {} ", file.getOriginalFilename());

			httpPost.setHeader("X-TENANT-ID", TenantContext.getTenantId());
			httpPost.setEntity(reqEntity);
			System.out.println("Requesting : " + httpPost.getRequestLine());
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			String responseBody = httpClient.execute(httpPost, responseHandler);
			System.out.println("responseBody : " + responseBody);

			return responseBody;
		} catch (Exception ex) {
			LOGGER.error("Exception in generating eway bill with request ", ex);
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", ewbGson.toJsonTree(ex.getMessage()));
			return ex.getMessage();
		}
	}

	@PostMapping(value = "/ui/downloadPostReconReport", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> downloadPostReconReport(
			HttpServletResponse response, @RequestBody String jsonString)
			throws Exception {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject jsonObj = new JsonObject();
		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Inside getPostReconSummary with request as " + " : %s",
						jsonString);
				LOGGER.debug(msg);
			}

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			JsonObject reqJson = requestObject.get("req").getAsJsonObject();

			JsonArray gstins = reqJson.getAsJsonArray("recipientGstins");

			String taxPeriod = reqJson.get("taxPeriod").getAsString();

			String entityId = reqJson.get("entityId").getAsString();

			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();
			ArrayList<String> recipientGstins = gson.fromJson(gstins, listType);

			if (CollectionUtils.isEmpty(recipientGstins))
				throw new AppException("User did not select any gstin");

			String derviedTaxPeriod = getDerviedRetPeriod(taxPeriod);

			Workbook workBook = postReconSummaryService.getPostReconReport(
					recipientGstins, derviedTaxPeriod, entityId);

			String fileName = DocumentUtility
					.getUniqueFileName(ConfigConstants.POSTRECONSUMMARY);

			if (workBook != null) {
				response.setContentType("APPLICATION/OCTET-STREAM");
				response.setHeader("Content-Disposition",
						String.format("attachment; filename=" + fileName));
				workBook.save(response.getOutputStream(), SaveFormat.XLSX);
				response.getOutputStream().flush();
				return new ResponseEntity<>(null, HttpStatus.OK);
			} else {
				jsonObj.add("hdr",
						gson.toJsonTree(APIRespDto.creatErrorResp()));
				jsonObj.addProperty("resp", "No Data Available");
				return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
			}

		} catch (Exception ex) {
			jsonObj.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			jsonObj.addProperty("resp", ex.getMessage());
			String msg = "Error occured while generating reconSummary report ";
			LOGGER.error(msg, ex);
			response.flushBuffer();
			return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
		}
	}

	private String getDerviedRetPeriod(String taxPeriod) {
		return taxPeriod.substring(2).concat(taxPeriod.substring(0, 2));
	}

}
