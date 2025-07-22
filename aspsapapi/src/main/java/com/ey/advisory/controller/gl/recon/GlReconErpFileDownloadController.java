package com.ey.advisory.controller.gl.recon;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.chemistry.opencmis.client.api.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.GlReconFileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.GlReconFileStatusRepository;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class GlReconErpFileDownloadController {
	
	@Autowired
	@Qualifier("GlReconFileStatusRepository")
	private GlReconFileStatusRepository glReconFileStatusRepository;

	
	
	
	@RequestMapping(value = {
	"/ui/glReconErpFileDownload"}, method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE})
        public void downloadGlReconErpFile(@RequestBody String jsonString,
                                             HttpServletResponse response) {

		try {
			 JsonObject requestObject = JsonParser.parseString(jsonString).getAsJsonObject();
			    JsonObject reqObj = requestObject.getAsJsonObject("req");

			    if (reqObj == null || !reqObj.has("payloadId")) {
			        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			        response.getWriter().write("Missing 'payloadId' in request.");
			        return;
			    }

			    String payloadId = reqObj.get("payloadId").getAsString();

			Optional<GlReconFileStatusEntity> fileStatusOpt = glReconFileStatusRepository
					.findByPayloadId(payloadId);
			String erpDmsGlDumpDocId = fileStatusOpt.get()
					.getErpDmsGlDumpDocId();
			//String payloadId = fileStatusOpt.get().getPayloadId();

			Document document = null;
			if (erpDmsGlDumpDocId != null
					&& !erpDmsGlDumpDocId.trim().isEmpty()) {
				document = DocumentUtility
						.downloadDocumentByDocId(erpDmsGlDumpDocId);
			}

			if (document == null) {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				response.getWriter().write("Document not found.");
				return;
			}

			String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
					.format(new Date());
			String baseFileName = String.format("Rawfile_GLDump_%s_%s",
					payloadId, timestamp);

			String extension = ".bin";
			if (baseFileName != null && baseFileName.contains(".")) {
				extension = baseFileName
						.substring(baseFileName.lastIndexOf('.')); // .xlsx/.csv/.txt
			}

			String zipFileName = baseFileName + ".zip";
			String zippedFileName = baseFileName + extension;

			// Set response headers
			response.setContentType("application/zip");
			response.setHeader("Content-Disposition",
					"attachment; filename=" + zipFileName);

			try (ZipOutputStream zipOut = new ZipOutputStream(
					response.getOutputStream());
					InputStream inputStream = document.getContentStream()
							.getStream()) {

				zipOut.putNextEntry(new ZipEntry(zippedFileName));
				byte[] buffer = new byte[1024];
				int length;
				while ((length = inputStream.read(buffer)) >= 0) {
					zipOut.write(buffer, 0, length);
				}
				zipOut.closeEntry();
				zipOut.finish();
			}

			response.flushBuffer();

		} catch (Exception e) {
			LOGGER.error("Error generating zip file ", e);
			sendErrorJsonResponse(response,
					"Download failed: " + e.getMessage());
		}
	}

    private static final Gson gson = new Gson();

    private ResponseEntity<String> sendErrorJsonResponse(HttpServletResponse response, String errorMessage) {
        String msg = "Exception occuerd during file download";
		LOGGER.error(msg);

		JsonObject hdrMsg = new JsonObject();
		hdrMsg.addProperty("status", "E");

		JsonObject respMsg = new JsonObject();
		respMsg.addProperty("errMsg", msg);

		JsonObject resp = new JsonObject();
		resp.add("hdr", hdrMsg);
		resp.add("resp", respMsg);

		return new ResponseEntity<>(response.toString(), HttpStatus.OK);
    }

        }


