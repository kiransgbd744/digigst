package com.ey.advisory.controllers.vendorcommunication;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;

import org.apache.chemistry.opencmis.client.api.Document;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ey.advisory.app.data.entities.client.asprecon.VendorReqVendorGstinEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorCommRequestRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorReqVendorGstinRepository;
import com.ey.advisory.app.services.vendorcomm.Vendor2wayCommunicationUtility;
import com.ey.advisory.app.services.vendorcomm.Vendor2wayserviceImpl;
import com.ey.advisory.app.vendorcomm.dto.VendorReportDownloadDto;
import com.ey.advisory.app.vendorcomm.dto.VendorResponseDataDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.ReconStatusConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class Vendor2wayController {

	@Autowired
	Vendor2wayCommunicationUtility vendorUtility;

	@Autowired
	Vendor2wayserviceImpl vendr2wayservice;

	@Autowired
	@Qualifier("VendorReqVendorGstinRepository")
	VendorReqVendorGstinRepository vendrReqGstinRepo;

	@Autowired
	@Qualifier("VendorCommRequestRepository")
	VendorCommRequestRepository vendrCommRepo;

	@GetMapping(value = "/vendorApi/downloadVendorJsonFiles")
	// @PostMapping(value = "/api/downloadVendorJsonFiles")
	public byte[] downloadVendorJsonFiles(HttpServletRequest request,
			HttpServletResponse response) {
		Long requestId = null;
		try {

			requestId = Long.valueOf(request.getHeader("requestId"));
			String groupCode = request.getHeader("groupCode");

			vendrCommRepo.updatePrepStatus(requestId,
					ReconStatusConstants.VENDR_JSON_INITIATED);

			InputStream inputStream = vendorUtility
					.vendorReqIdZipFile(requestId);

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = inputStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}
			vendrCommRepo.updatePrepStatus(requestId,
					ReconStatusConstants.VENDR_JSON_COMPLETED);
			return outputStream.toByteArray();

		} catch (Exception e) {
			vendrCommRepo.updatePrepStatus(requestId,
					ReconStatusConstants.VENDR_JSON_FAILED);
			String msg = "Exception while calling downloadVendorJsonFiles ";
			LOGGER.error(msg, e);

			throw new AppException(e, msg);
		}
	}

	@PostMapping(value = "/ui/vendorResponseRequestData", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> vendorResponseRequestData(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();

		JsonObject resp = new JsonObject();
		try {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"vendorResponseRequestData Request received from UI as {} ",
						jsonString);
			}

			JsonObject reqObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			JsonObject hdrObject = reqObject.getAsJsonObject("hdr");
			int pageNum = hdrObject.get("pageNum").getAsInt();
			int pageSize = hdrObject.get("pageSize").getAsInt();

			JsonObject reqJson = reqObject.get("req").getAsJsonObject();
			VendorReportDownloadDto vendorRespDto = gson.fromJson(reqJson,
					VendorReportDownloadDto.class);

			Pair<List<VendorResponseDataDto>, Integer> vendrResp = vendr2wayservice
					.getVendorRespData(vendorRespDto, pageNum, pageSize);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"vendor response fetched from db : %s", vendrResp);
				LOGGER.debug(msg);
			}

			if (vendrResp == null || vendrResp.getValue0().isEmpty()) {
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", gson.toJsonTree("No data"));
			} else {

				resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp(
						vendrResp.getValue1(), pageNum, pageSize, "S",
						"Successfully fetched Vendor Master Processed records")));
				resp.add("resp", gson.toJsonTree(vendrResp.getValue0()));
			}
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			String msg = "Exception on Vendor Report Request" + ex;
			LOGGER.error(msg, ex);
			JsonObject respMsg = new JsonObject();
			respMsg.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			respMsg.add("resp", gson.toJsonTree(msg));
			return new ResponseEntity<>(respMsg.toString(), HttpStatus.OK);
		}

	}

	@PostMapping(value = "/ui/vendr2wayRespFileDownload", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void fileDownloads(@RequestBody String jsonString,
			HttpServletResponse response) throws Exception {

		LOGGER.debug(
				"inside fileDownloads method for vendr2wayRespFileDownload");

		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			JsonObject json = requestObject.get("req").getAsJsonObject();
			String fileName = json.get("filePath").getAsString();

			String identifier = json.get("identifier").getAsString();
			Long reqId = json.get("reqId").getAsLong();

			if (reqId.toString() == null) {
				reqId = vendrCommRepo.getLastReqId();
			}

			String vendrGstin = json.get("vendrGstin").getAsString();

			if (fileName == null) {
				throw new AppException("FileName is null");
			}

			String newFileName = null;
			String fileFolder = "VendorCommJsonReport";

			Document document = null;
			InputStream inputStream = null;

			if (identifier.equalsIgnoreCase("Resp")) {
				vendrReqGstinRepo.updateIsRead(reqId, vendrGstin);
				Optional<VendorReqVendorGstinEntity> entity = vendrReqGstinRepo.findByRequestIdVendorGstin(reqId, vendrGstin);
				if(entity.isPresent()){
					if (LOGGER.isDebugEnabled()) {
				        LOGGER.debug("Entity found: {}", entity.get());
				    }
					 String docId = entity.get().getDocIdRespondedFilePath();
					 if(docId != null){
						 document = DocumentUtility.downloadDocumentByDocId(entity.get().getDocIdRespondedFilePath());
					 } else {
						 document = DocumentUtility.downloadDocument(fileName,
									fileFolder);
					 }
				} 
				if (fileName.contains("All_Response")) {
					newFileName = fileName.replace("All_Response", "Response");
				} else {
					newFileName = fileName;
				}
			} else {
				Optional<VendorReqVendorGstinEntity> entity = vendrReqGstinRepo.findByRequestIdVendorGstin(reqId, vendrGstin);
				if(entity.isPresent()){
					if (LOGGER.isDebugEnabled()) {
				        LOGGER.debug("Entity found ALL: {}", entity.get());
				    }
					 String docId = entity.get().getDocIdTotalFilePath();
					 if(docId != null){
						 document = DocumentUtility.downloadDocumentByDocId(entity.get().getDocIdTotalFilePath());
					 } else {
						 document = DocumentUtility.downloadDocument(fileName,
									fileFolder);
					 }
				}
				newFileName = fileName;
			}
            if (document!=null){
		    inputStream = document.getContentStream().getStream();
            }
			int read = 0;
			byte[] bytes = new byte[1024];

			if (document != null) {
				response.setHeader("Content-Disposition",
						String.format("attachment; filename =" + newFileName));
				OutputStream outputStream = response.getOutputStream();
				while ((read = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}
			}

		} catch (Exception ex) {
			String msg = "Exception while downloading files ";
			LOGGER.error(msg, ex);
		}
	}

	@PostMapping(value = "/vendorApi/saveVendorRespCsv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = {
			MediaType.APPLICATION_JSON_UTF8_VALUE })
	public ResponseEntity<String> saveVendorRespCsv(
			@RequestParam("respFile") MultipartFile file,
			@RequestParam("totRespCounts") String totRespCount,
			@RequestParam("totRecords") String totRecords,
			@RequestHeader("requestId") String reqId,
			@RequestParam("vendorGstin") String vendrGstin,
			@RequestHeader("groupCode") String grpCode) {

		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();

		Long requestId = Long.valueOf(reqId);
		Long totalRespCount = Long.valueOf(totRespCount);
		Long totalRecords = Long.valueOf(totRecords);

		vendrCommRepo.updatePrepStatus(requestId, "VENDOR_RESP_SAVE_INITIATED");

		File tempDir = null;
		File destDir = null;
		try {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"The param received through azure - reqId- {}, "
								+ " totalRespCounts- {}, totRecords- {}, vendorGstin- {},groupCode- {}",
						requestId, totalRespCount, totalRecords, vendrGstin,
						grpCode);
			}

			tempDir = Files.createTempDir();
			destDir = Files.createTempDir();
			if (totalRecords == totalRespCount) {
				vendorUtility.vendorFileUpload(file, requestId, "Match",
						vendrGstin, tempDir, destDir);

			} else if (totalRespCount == 0)
				vendorUtility.vendorFileUpload(file, requestId, "Empty",
						vendrGstin, tempDir, destDir);
			else
				vendorUtility.vendorFileUpload(file, requestId, "Difference",
						vendrGstin, tempDir, destDir);

			vendrReqGstinRepo.updateIsRespAndRespCnt(requestId, vendrGstin,
					totalRespCount.intValue());

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Updated csv files are saved in doc repo");
			}

			vendrCommRepo.updatePrepStatus(requestId,
					"VENDOR_RESP_SAVE_COMPLETED");

			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String msg = "Exception while saving downloadVendorJsonFiles ";
			LOGGER.error(msg, ex);
			vendrCommRepo.updatePrepStatus(requestId,
					"VENDOR_RESP_SAVE_FAILED");
			JsonObject respMsg = new JsonObject();
			respMsg.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			respMsg.add("resp", gson.toJsonTree(ex));
			return new ResponseEntity<>(respMsg.toString(), HttpStatus.OK);
		} finally {
			GenUtil.deleteTempDir(tempDir);
			GenUtil.deleteTempDir(destDir);
		}
	}

	@GetMapping(value = "/vendorApi/getVendorSummaryData")
	public byte[] getVendorSummaryData(HttpServletRequest request,
			HttpServletResponse response) {
		Long requestId = null;
		try {

			requestId = Long.valueOf(request.getHeader("requestId"));
			String groupCode = request.getHeader("groupCode");
			String vendorGstin = request.getHeader("vendorGstin");

			InputStream inputStream = vendorUtility.vendorSummaryFile(requestId,
					vendorGstin);

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = inputStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}
			return outputStream.toByteArray();

		} catch (Exception e) {
			String msg = "Exception while sending getVendorSummaryData ";
			LOGGER.error(msg, e);

			throw new AppException(e, msg);
		}
	}

}
