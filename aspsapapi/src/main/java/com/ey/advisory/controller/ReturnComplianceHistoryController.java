/**
 * 
 */
package com.ey.advisory.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicResponseHandler;
import org.javatuples.Pair;
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

import com.ey.advisory.app.data.entities.client.asprecon.ReturnComplianceRequestEntity;
import com.ey.advisory.app.data.returns.compliance.service.ReturnComplianceSummaryService;
import com.ey.advisory.app.services.vendorcomm.ReturnComplianceEmailCommDto;
import com.ey.advisory.app.services.vendorcomm.ReturnComplianceRequestDto;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Laxmi.Salukuti
 *
 */
@RestController
@Slf4j
public class ReturnComplianceHistoryController {

	@Autowired
	@Qualifier("ReturnComplianceSummaryServiceImpl")
	private ReturnComplianceSummaryService returnComplianceSummaryService;

	@Autowired
	@Qualifier("InternalHttpClient")
	private HttpClient httpClient;

	@PostMapping(value = "/ui/getReturnComplianceReqDetails", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getComplianceReturnReqDetails() {
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			String userName = (SecurityContext.getUser() != null
					&& SecurityContext.getUser().getUserPrincipalName() != null)
							? SecurityContext.getUser().getUserPrincipalName()
							: "SYSTEM";
			List<ReturnComplianceRequestEntity> complianceComReqList = returnComplianceSummaryService
					.getComplianceDataByUserName(userName);
			List<ReturnComplianceRequestDto> complianceCommDataList = returnComplianceSummaryService
					.getComplianceCommResponse(complianceComReqList);
			Collections.sort(complianceCommDataList,
					new Comparator<ReturnComplianceRequestDto>() {
						public int compare(ReturnComplianceRequestDto o1,
								ReturnComplianceRequestDto o2) {
							return (int) (o2.getRequestId()
									- o1.getRequestId());
						}
					});
			String jsonComplianceData = gson.toJson(complianceCommDataList);
			JsonElement clientJsonElement = new JsonParser()
					.parse(jsonComplianceData);
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("ComplianceReturnClientData", clientJsonElement);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", jsonObject);
			LOGGER.info("response {}", resp);
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
		} catch (Exception ex) {
			LOGGER.error("Message", ex.getMessage());
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp",
					gson.toJsonTree(
							"Exception while Processing the getReturnComplianceReqDetails request "
									+ ex.getMessage()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/ui/getComplianceReturnEmailCommData", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getComplianceReturnEmailCommData(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"getComplianceReturnEmailCommData Request received from UI as {} ",
						jsonString);
			}

			JsonObject reqObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			JsonObject hdrObject = reqObject.getAsJsonObject("hdr");
			int pageNum = hdrObject.get("pageNum").getAsInt();
			int pageSize = hdrObject.get("pageSize").getAsInt();

			JsonObject reqJson = reqObject.get("req").getAsJsonObject();
			Long requestID = reqJson.get("requestID").getAsLong();
			Long entityId = reqJson.get("entityId").getAsLong();

			Pair<List<ReturnComplianceEmailCommDto>, Integer> clientEmailCommDtoListPair = returnComplianceSummaryService
					.getComReturnEmailCommunicationDetails(requestID, entityId,
							pageSize, pageNum);
			List<ReturnComplianceEmailCommDto> clientEmailCommDtoList = clientEmailCommDtoListPair
					.getValue0();

			Collections.sort(clientEmailCommDtoList,
					new Comparator<ReturnComplianceEmailCommDto>() {
						public int compare(ReturnComplianceEmailCommDto o1,
								ReturnComplianceEmailCommDto o2) {
							return (o1.getClientGstin()
									.compareTo(o2.getClientGstin()));
						}
					});

			String jsonEINV = gson.toJson(clientEmailCommDtoList);
			JsonElement einvJsonElement = new JsonParser().parse(jsonEINV);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp(
					clientEmailCommDtoListPair.getValue1(), pageNum, pageSize,
					"S",
					"Successfully fetched Return Compliance email Communication details")));
			resp.add("resp", einvJsonElement);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			LOGGER.error("Message", ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp",
					gson.toJsonTree(
							"Exception on Return Compliance email Communication details Request "
									+ ex.getMessage()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	@PostMapping(value = "/ui/postComplianceReturnEmailCommData", produces = {
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
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("File Original Name {} ",
						file.getOriginalFilename());
			}
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
}
