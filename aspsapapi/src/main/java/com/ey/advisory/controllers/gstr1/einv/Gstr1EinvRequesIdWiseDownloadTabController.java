package com.ey.advisory.controllers.gstr1.einv;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.chemistry.opencmis.client.api.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.repositories.client.Gstr1EInvReconConfigRepository;
import com.ey.advisory.app.gstr1.einv.Gstr1EinvInitiateReconService;
import com.ey.advisory.app.gstr1.einv.Gstr1EinvRequesIdWiseDownloadTabDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Rajesh N K
 *
 */
@RestController
@Slf4j
public class Gstr1EinvRequesIdWiseDownloadTabController {

	@Autowired
	@Qualifier("Gstr1EinvInitiateReconServiceImpl")
	private Gstr1EinvInitiateReconService gstinEinvService;

	@Autowired
	@Qualifier("Gstr1EInvReconConfigRepository")
	Gstr1EInvReconConfigRepository gstr1InvReconrepo;

	@RequestMapping(value = "/ui/gstr1EinvDownloadIdWise", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getdownloadIdWiseforRecon(
			@RequestBody String jsonString) {

		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			JsonObject json = requestObject.get("req").getAsJsonObject();
			Gson gson = GsonUtil.newSAPGsonInstance();

			String configId = json.get("configId").getAsString();

			LOGGER.debug(
					"Inside Gstr1EinvRequesIdWiseDownloadTabController"
							+ ".getdownloadIdWiseforRecon() method and config id : %s ",
					configId);

			List<Gstr1EinvRequesIdWiseDownloadTabDto> resp = gstinEinvService
					.getDownloadData(Long.valueOf(configId));
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(resp);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Data Status ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
	
	
	@RequestMapping(value = "/ui/gstr1PrVsSubmDownloadIdWise", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getPrVsSubmdownloadIdWiseforRecon(
			@RequestBody String jsonString) {

		try {
			/*JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();*/
			
			JsonObject requestObject = JsonParser.parseString(jsonString)
					.getAsJsonObject();

			JsonObject json = requestObject.get("req").getAsJsonObject();
			Gson gson = GsonUtil.newSAPGsonInstance();

			String configId = json.get("configId").getAsString();

			LOGGER.debug(
					"Inside Gstr1EinvRequesIdWiseDownloadTabController"
							+ ".getPrVsSubmdownloadIdWiseforRecon() method and config id : %s ",
					configId);

			List<Gstr1EinvRequesIdWiseDownloadTabDto> resp = gstinEinvService
					.getPrVsSubmDownloadData(Long.valueOf(configId));
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(resp);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Data Status ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@PostMapping(value = "/ui/gstr1EinvfiledownloadDoc")
	public void gstr1fileDownloads(@RequestBody String jsonString,
			HttpServletResponse response) throws Exception {

		JsonObject errorResp = new JsonObject();

		LOGGER.debug("inside fileDownloads method and file type is {} ",
				"Gstr1ReconReports");
		Document document=null;
		try {
			String tenantCode = TenantContext.getTenantId();
			LOGGER.debug("Tenant Id Is {}", tenantCode);
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			JsonObject json = requestObject.get("req").getAsJsonObject();

			String fileName = json.get("filePath").getAsString();
			String docId = json.has("docId") ? json.get("docId").getAsString() : null;
			String fileFolder = "Gstr1ReconReports";

			/*Document document = DocumentUtility.downloadDocument(fileName,
					fileFolder);*/
			
			if (docId != null && !docId.isEmpty()) {

				String msg = String.format(
						"Doc Id is available for File Name %s and Doc Id %s",
						fileName, docId);
				LOGGER.debug(msg);
				document = DocumentUtility
						.downloadDocumentByDocId(docId);

			} else {

				document = DocumentUtility.downloadDocument(fileName,
						fileFolder);
				String msg = String.format(
						"Doc Id is not available for File Name  %s",
						fileName);
				LOGGER.debug(msg);
				if (fileName == null ) {
					throw new AppException("FileName is null");

				}
			}

			if (document == null) {
				return;
			}

			InputStream inputStream = document.getContentStream().getStream();
			int read = 0;
			byte[] bytes = new byte[1024];

			if (document != null) {
				response.setHeader("Content-Disposition",
						String.format("attachment; filename = " + fileName));
				OutputStream outputStream = response.getOutputStream();
				while ((read = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}
			}
		} catch (Exception ex) {
			String msg = "Error occured while generating Gstr1vsEinv report ";
			LOGGER.error(msg, ex);
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			response.flushBuffer();
		}

	}
	
	@PostMapping(value = "/ui/gstr1PrVsSubmfiledownloadDoc")
	public void gstr1PrVsSubmfileDownloads(@RequestBody String jsonString,
			HttpServletResponse response) throws Exception {

		JsonObject errorResp = new JsonObject();

		LOGGER.debug("inside fileDownloads method and file type is {} ",
				"Gstr1ReconReports");

		try {
			String tenantCode = TenantContext.getTenantId();
			LOGGER.debug("Tenant Id Is {}", tenantCode);
			/*JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
*/
			JsonObject requestObject = JsonParser.parseString(jsonString)
					.getAsJsonObject();
			JsonObject json = requestObject.get("req").getAsJsonObject();

			String fileName = json.get("filePath").getAsString();
			String fileFolder = "PrVsSubmittedReport";
			
			String[] spliedName = fileName.split("_");

			Document document = DocumentUtility.downloadDocument(fileName,
					fileFolder);

			if (document == null) {
				return;
			}

			InputStream inputStream = document.getContentStream().getStream();
			int read = 0;
			byte[] bytes = new byte[1024];
			
			String fname =  "Processed DigiGST vs Submitted GSTN"
					+ "_" + spliedName[1] + "_" + spliedName[2] + "_"
					+ spliedName[3];

			if (document != null) {
				response.setHeader("Content-Disposition",
						String.format("attachment; filename =" + fname));
				OutputStream outputStream = response.getOutputStream();
				while ((read = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}
			}
		} catch (Exception ex) {
			String msg = "Error occured while downloading Gstr1PR vs Submitted report ";
			LOGGER.error(msg, ex);
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			response.flushBuffer();
		}

	}


}
