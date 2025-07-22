package com.ey.advisory.app.data.services.qrcodevalidator;

import static com.ey.advisory.common.GSTConstants.WEB_UPLOAD_KEY;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.ey.advisory.admin.data.repositories.client.GroupConfigPrmtRepository;
import com.ey.advisory.app.data.entities.qrcodevalidator.DummyQRResponseSummaryReqDto;
import com.ey.advisory.app.data.entities.qrcodevalidator.QRPDFJSONResponseSummaryEntity;
import com.ey.advisory.app.data.entities.qrcodevalidator.QRPDFResponseSummaryEntity;
import com.ey.advisory.app.data.entities.qrcodevalidator.QRResponseLogEntity;
import com.ey.advisory.app.data.entities.qrcodevalidator.QRResponseSummaryEntity;
import com.ey.advisory.app.data.repositories.client.qrcodevalidator.QRInwardEinvoiceTaggingRepository;
import com.ey.advisory.app.data.repositories.client.qrcodevalidator.QRResponseLogRepo;
import com.ey.advisory.app.data.repositories.client.qrcodevalidator.QRUploadFileStatusRepo;
import com.ey.advisory.app.data.services.qrvspdf.PdfValidatorRespDto;
import com.ey.advisory.app.data.services.qrvspdf.QRValidatorRespDto;
import com.ey.advisory.app.data.services.qrvspdf.QRvsPdfCommonUtility;
import com.ey.advisory.app.data.services.qrvspdf.QrvsPdfValidatorFinalRespDto;
import com.ey.advisory.app.inward.einvoice.JsonPdfValidatorFinalRespDto;
import com.ey.advisory.app.inward.einvoice.ReconJsonVsPdfUtility;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.PerfamanceEventConstants;
import com.ey.advisory.common.QRCodeValidatorConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.repositories.master.EinvMasterGstinRepository;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Reddy
 *
 */

@Component
@Slf4j
public class QRCommonUtility {

	@Autowired
	@Qualifier("GSTNHttpClient")
	private HttpClient httpClient;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Autowired
	@Qualifier("InternalHttpClient")
	private HttpClient httpClientnew;

	@Autowired
	QRvsPdfCommonUtility qrvsPdfCommonUtility;

	@Autowired
	@Qualifier("GroupConfigPrmtRepository")
	private GroupConfigPrmtRepository groupConfigPemtRepo;

	@Autowired
	private QRResponseLogRepo respLogRep;

	@Autowired
	@Qualifier("QRCodeValidatorServiceImpl")
	private QRCodeValidatorService qrCodeService;

	@Autowired
	@Qualifier("QRUploadFileStatusRepo")
	QRUploadFileStatusRepo qrUploadFileStatusRepo;

	// autowire the QRInwardEinvoiceTaggingRepository
	@Autowired
	private QRInwardEinvoiceTaggingRepository qrInwardEinvoiceTaggingRepository;

	public final static String EXCEP_ERROR = "Excep_Error";

	private final static String Available = "Available";

	private final static String Valid = "Valid";

	public final static String ERROR = "Error";

	@Autowired
	private EinvMasterGstinRepository einvMasterGstinRepo;

	@Autowired
	ReconJsonVsPdfUtility rconJsonPdfUtility;

	public static File createDownloadDir(File tempDir) throws IOException {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Creating downloaded directory to download generated csv file for merging");
		}

		File downloadDir = new File(
				tempDir.getAbsolutePath() + File.separator + "DownloadDir");
		downloadDir.mkdirs();

		return downloadDir;
	}

	public String getAccessToken(String userName, String password,
			String apiAccessKey) {
		try {
			Map<String, Config> configMap = configManager
					.getConfigs("QRVALIDATOR", "qr.details", "DEFAULT");

			String authUrl = configMap.get("qr.details.authtoken") == null ? ""
					: configMap.get("qr.details.authtoken").getValue();

			HttpPost httpPost = new HttpPost(authUrl);
			httpPost.setHeader("username", userName);
			httpPost.setHeader("password", password);
			httpPost.setHeader("apiaccesskey", apiAccessKey);

			LOGGER.debug("Requesting {} ", httpPost.getRequestLine());
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			String responseBody = httpClient.execute(httpPost, responseHandler);
			LOGGER.debug("responseBody {} ", responseBody);

			return responseBody;
		} catch (Exception ex) {
			LOGGER.error("Exception in QR Code Validator {}", ex);
			return ex.getMessage();
		}

	}

	public String getPdfAccessToken(String userName, String password,
			String apiAccessKey) {
		try {
			Map<String, Config> configMap = configManager
					.getConfigs("QRVALIDATOR", "pdf.details", "DEFAULT");

			String authUrl = configMap.get("pdf.details.authtoken") == null ? ""
					: configMap.get("pdf.details.authtoken").getValue();

			HttpPost httpPost = new HttpPost(authUrl);
			httpPost.setHeader("username", userName);
			httpPost.setHeader("password", password);
			httpPost.setHeader("apiaccesskey", apiAccessKey);

			LOGGER.debug("Requesting pdf {} ", httpPost.getRequestLine());
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			String responseBody = httpClient.execute(httpPost, responseHandler);
			LOGGER.debug("responseBody pdf {} ", responseBody);

			return responseBody;
		} catch (Exception ex) {
			LOGGER.error("Exception in PDF Code Validator {}", ex);
			return ex.getMessage();
		}

	}

	public String callOfflineTool(File destFile, String accessToken,
			String apiAccessKey, String qrValidUrl) {
		try {
			PerfUtil.logEventToFile(PerfamanceEventConstants.QRCODEVALIDATOR,
					"QRCODEAPI_PopulateQRValidatorStart", "QRCommonUtility",
					"callOfflineTool", destFile.getName());
			HttpPost httpPost = new HttpPost(qrValidUrl);

			HttpEntity reqEntity = MultipartEntityBuilder.create()
					.addBinaryBody("files[]", destFile,
							ContentType.create("application/octet-stream"),
							destFile.getName())
					.addTextBody("hsn", hsnOptedVal()).build();
			httpPost.setHeader("Accesstoken", accessToken);
			httpPost.setHeader("apiaccesskey", apiAccessKey);
			httpPost.setEntity(reqEntity);
			LOGGER.debug("Requesting {} ", httpPost.getRequestLine());
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			String responseBody = httpClient.execute(httpPost, responseHandler);
			LOGGER.debug("responseBody {} ", responseBody);
			PerfUtil.logEventToFile(PerfamanceEventConstants.QRCODEVALIDATOR,
					"QRCODEAPI_PopulateQRValidatorEnd", "QRCommonUtility",
					"callOfflineTool", destFile.getName());
			JsonObject respObject = JsonParser.parseString(responseBody)
					.getAsJsonObject();
			int status = respObject.get("status").getAsInt();
			if (status == 0 || status == 1)
				return responseBody;
			else
				return EXCEP_ERROR;
		} catch (Exception e) {
			String msg = String.format(
					"Exception occured while calling Offline Tool for File %s",
					destFile);
			LOGGER.error(msg, e);
			return EXCEP_ERROR;
		}

	}

	public String callPdfTool(File destFile, String apiAccessKey,
			String apiSecret, String pdfReaderUrl) {
		try {
			PerfUtil.logEventToFile(PerfamanceEventConstants.QRCODEVALIDATOR,
					"QRCODEAPI_PopulateQRValidatorStart", "QRCommonUtility",
					"callPdfTool", destFile.getName());

			LOGGER.debug("callPdfTool DestiFile {}", destFile.getName());

			HttpPost httpPost = new HttpPost(pdfReaderUrl);
			HttpEntity reqEntity = MultipartEntityBuilder.create()
					.addBinaryBody("input_file", destFile,
							ContentType.create("application/pdf"),
							destFile.getName())
					.addTextBody("pages", "1-3")
					.addTextBody("gpt-version", "3.5-turbo").build();
			httpPost.setHeader("apiaccesskey", apiSecret);
			httpPost.setHeader("accesstoken", apiAccessKey);
			httpPost.setHeader("groupcode", TenantContext.getTenantId());
			httpPost.setEntity(reqEntity);
			LOGGER.debug("Requesting {} ", httpPost.getRequestLine());
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			String responseBody = httpClient.execute(httpPost, responseHandler);
			LOGGER.debug("Response Body {} ", responseBody);
			PerfUtil.logEventToFile(PerfamanceEventConstants.QRCODEVALIDATOR,
					"QRCODEAPI_PopulateQRValidatorEnd", "QRCommonUtility",
					"callPdfTool", destFile.getName());
			JsonObject respObject = JsonParser.parseString(responseBody)
					.getAsJsonObject();
			LOGGER.debug("Json Object {} ", respObject);
			if (respObject.has("status")) {
				String status = respObject.get("status").getAsString();
				return respObject.toString();
			}else
				return EXCEP_ERROR;
			// TODO
			// JsonObject data = respObject.get("data").getAsJsonObject();
		
		} catch (Exception e) {
			String msg = String.format(
					"Exception occured while calling PDF Tool for File %s",
					destFile);
			LOGGER.error(msg, e);
			return EXCEP_ERROR;
		}

	}

	public void parseAndPopulateResponse(
			List<QRResponseSummaryEntity> listofSumm, String responseBody,
			Long fileId, QRCountDto countDto, String fileName,
			String uploadType, String entityId) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Received Response is {}", responseBody);
		}
		try {
			JsonObject respObject = JsonParser.parseString(responseBody)
					.getAsJsonObject();

			int status = respObject.get("status").getAsInt();
			if (status == 1) {
				String resultObj = respObject.get("result").toString();
				JsonArray respArray = JsonParser.parseString(resultObj)
						.getAsJsonArray();
				for (JsonElement inv : respArray) {
					String invoiceDetails = inv.toString();
					QRResponseSummaryEntity qrSumm = convertDtoToEntity(
							invoiceDetails);
					if (!Strings.isNullOrEmpty(qrSumm.getBuyerGstin())) {
						qrSumm.setBuyerPan(
								qrSumm.getBuyerGstin().substring(2, 12));
					}
					if (!Strings.isNullOrEmpty(qrSumm.getSellerGstin())) {
						qrSumm.setSellerPan(
								qrSumm.getSellerGstin().substring(2, 12));
					}
					LOGGER.debug("File Name {}", qrSumm.getFileName());
					qrSumm.setFileId(fileId);
					qrSumm.setCreatedBy("SYSTEM");
					qrSumm.setCreatedOn(LocalDateTime.now());
					qrSumm.setValidatedDate(LocalDate.now());
					qrSumm.setFileName(fileName);
					qrSumm.setFullMatch(QRCommonUtility.isFullMatch(qrSumm));
					qrSumm.setProcessedQR(
							!Strings.isNullOrEmpty(qrSumm.getIrn()));
					qrSumm.setSigMisMat(
							Strings.isNullOrEmpty(qrSumm.getSignature())
									|| "InValid".equalsIgnoreCase(
											qrSumm.getSignature()));
					LOGGER.debug("UploadType from EntityID {} ", uploadType,
							entityId);
					if (uploadType.equalsIgnoreCase("webUpload")
							&& entityId != null) {
						qrSumm.setEntityId(Long.parseLong(entityId));
					} else {
						qrSumm.setEntityId(qrCodeService.setEntityId(qrSumm,
								qrSumm.getBuyerGstin(), qrSumm.getFileId()));
					}
					listofSumm.add(qrSumm);
					// countDto.incrementTotalCnt();
					if (!Strings.isNullOrEmpty(qrSumm.getIrn())) {
						countDto.incrementQrCnt();
					}
					if (!Valid.equalsIgnoreCase(qrSumm.getSignature())) {
						countDto.incrementSigMisCnt();
					}
					if (isFullMatch(qrSumm)) {
						countDto.incrementFullMatCnt();
					}
				}

			} else {
				QRResponseSummaryEntity qrSumm = new QRResponseSummaryEntity();

				LOGGER.debug("File Name {}", qrSumm.getFileName());
				qrSumm.setFileId(fileId);
				qrSumm.setCreatedBy("SYSTEM");
				qrSumm.setCreatedOn(LocalDateTime.now());
				qrSumm.setValidatedDate(LocalDate.now());
				qrSumm.setFileName(fileName);
				qrSumm.setFullMatch(false);
				qrSumm.setProcessedQR(false);
				qrSumm.setSigMisMat(false);
				LOGGER.debug("UploadType from EntityID {} ", uploadType,
						entityId);
				if (uploadType.equalsIgnoreCase("webUpload")
						&& entityId != null) {
					qrSumm.setEntityId(Long.parseLong(entityId));
				} else {
					qrSumm.setEntityId(qrCodeService.setEntityId(qrSumm,
							qrSumm.getBuyerGstin(), qrSumm.getFileId()));
				}
				listofSumm.add(qrSumm);
				// countDto.incrementTotalCnt();
				if (!Strings.isNullOrEmpty(qrSumm.getIrn())) {
					countDto.incrementQrCnt();
				}
				if (!Valid.equalsIgnoreCase(qrSumm.getSignature())) {
					countDto.incrementSigMisCnt();
				}
				if (isFullMatch(qrSumm)) {
					countDto.incrementFullMatCnt();
				}
			}
		} catch (Exception e) {
			String msg = String.format(
					"Exception while Parsing with the Received Json and Response is %s",
					responseBody);
			LOGGER.error(msg, e);
		}
	}

	public QrvsPdfValidatorFinalRespDto parseAndPopulateQRPDFResponse(
			List<QRPDFResponseSummaryEntity> listofSumm, String qrResponseBody,
			String pdfResponseBody, Long fileId, QRCountDto countDto,
			String fileName, String uploadType, String entityId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("QR Received Response is {} and PDF Response {} ",
					qrResponseBody, pdfResponseBody);
		}
/*		
		  qrResponseBody=
		  "{\"status\":0,\"success\":false,\"message\":\"It seems like QR is absent or unreadable\"}"; 
		  
		  pdfResponseBody=
		  "{\"status\":\"success\",\"data\":{\"File Name\":\"MAT 332H-Fall 2010.pdf\",\"Customer Name\":null,\"Customer GSTIN\":null,\"Customer Address\":null,\"Customer State\":null,\"Customer State Code\":null,\"Customer Pincode\":null,\"Billing Address\":null,\"Shipping Address\":null,\"Document Number\":null,\"Document Date\":null,\"Document Type\":\"Syllabus\",\"IRN Number\":null,\"IRN Date\":null,\"Supplier Name\":null,\"Supplier GSTIN\":null,\"Supplier Address\":null,\"Supplier State\":null,\"Supplier State Code\":null,\"Supplier Pincode\":null,\"Place of Supply\":null,\"Sub Total\":null,\"Total Tax\":null,\"Invoice Total\":null,\"Purchase Order Number\":null,\"CGST Rate\":null,\"CGST Amount\":null,\"SGST Rate\":null,\"SGST Amount\":null,\"IGST Rate\":null,\"IGST Amount\":null,\"UTGST Rate\":null,\"UTGST Amount\":null,\"CESS Rate\":null,\"CESS Amount\":null,\"Reverse Charge / RCM Flag\":null,\"Line-items\":[]},\"file_path\":\"MAT 332H-Fall 2010.pdf\"}";
*/		 Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		try {
			
			
			QRValidatorRespDto qrDetails = parseQRResponse(qrResponseBody);
			PdfValidatorRespDto pdfDetails = parsePDFResponse(pdfResponseBody);
			QrvsPdfValidatorFinalRespDto finalResp = qrvsPdfCommonUtility
					.reconPdfvsQRResp(qrDetails, pdfDetails);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("final Resp Option B {} ", finalResp);
			}
			finalResp.setFileId(fileId);
			incrementCounts(finalResp.getSignature(),
					finalResp.getReportCategory(), countDto);

			if (Strings.isNullOrEmpty(qrDetails.getMessage())
					&& !Strings.isNullOrEmpty(pdfDetails.getMessage())) {
				finalResp.setMismatchReasons(pdfDetails.getMessage());
				// finalResp.setValidatedDate(null);

			} else if (!Strings.isNullOrEmpty(qrDetails.getMessage())
					&& Strings.isNullOrEmpty(pdfDetails.getMessage())) {
				finalResp.setMismatchReasons(qrDetails.getMessage());
				// finalResp.setValidatedDate(null);

			}

			createQRResponseLog(fileId, fileName, qrResponseBody,
					pdfResponseBody, gson.toJson(finalResp), null, null);
			boolean isError = false;

			if (!Strings.isNullOrEmpty(pdfDetails.getMessage())
					|| !Strings.isNullOrEmpty(qrDetails.getMessage())) {
				isError = true;
			}

			if (!Strings.isNullOrEmpty(pdfDetails.getMessage())
					&& !Strings.isNullOrEmpty(qrDetails.getMessage())) {
				throw new AppException();

			}

			QRPDFResponseSummaryEntity summaryEntity = convertFromQrvsPdfValidatorFinalRespDto(
					finalResp, fileId, isError);
			LOGGER.debug("UploadType from EntityID {} ", uploadType, entityId);
			if (uploadType.equalsIgnoreCase("webUpload") && entityId != null) {
				summaryEntity.setEntityId(Long.parseLong(entityId));
			} else {
				Long setEntityId = qrCodeService.setEntityId(summaryEntity,
						summaryEntity.getBuyerGstinQR(),
						summaryEntity.getFileId());
				finalResp.setEntityId(setEntityId);
			}
			if (Strings.isNullOrEmpty(qrDetails.getMessage())
					&& !Strings.isNullOrEmpty(pdfDetails.getMessage())) {
				finalResp.setMismatchReasons(pdfDetails.getMessage());
				summaryEntity.setMisMatchReasons(pdfDetails.getMessage());
				summaryEntity.setIsProcessedQR(false);
				summaryEntity.setMismatchAttributes(null);
				/*
				 * summaryEntity.setValidatedDate(null);
				 * summaryEntity.setValidatedDateStr(null);
				 */
			} else if (!Strings.isNullOrEmpty(qrDetails.getMessage())
					&& Strings.isNullOrEmpty(pdfDetails.getMessage())) {
				finalResp.setMismatchReasons(qrDetails.getMessage());
				summaryEntity.setMisMatchReasons(qrDetails.getMessage());
				summaryEntity.setIsProcessedQR(false);
				summaryEntity.setMismatchAttributes(null);
				/*
				 * summaryEntity.setValidatedDate(null);
				 * summaryEntity.setValidatedDateStr(null);
				 */
			}
			listofSumm.add(summaryEntity);
			return finalResp;
		} catch (Exception e) {
			String msg = String.format(
					"Exception while doing the Recon for File ID %s", fileId);
			JsonObject hdr = new JsonObject();
			hdr.addProperty("status", "E");
			JsonObject resp = new JsonObject();
			resp.addProperty("errMsg", msg);
			JsonObject finalResponse = new JsonObject();
			finalResponse.add("hdr", hdr);
			finalResponse.add("resp", resp);

			createQRResponseLog(fileId, fileName, qrResponseBody,
					pdfResponseBody, gson.toJson(finalResponse), null, null);
			LOGGER.error(msg, e);
			throw new AppException(msg);

		}
	}

	public JsonPdfValidatorFinalRespDto parseAndPopulateQRPDFJSONResponse(
			List<QRPDFJSONResponseSummaryEntity> listofSumm,
			String qrResponseBody, String pdfResponseBody, Long fileId,
			QRCountDto countDto, String fileName, String uploadType,
			String entityId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("QR Received Response is {} and PDF Response {} ",
					qrResponseBody, pdfResponseBody);
		}

		/*
		 * qrResponseBody=
		 * "{\"status\":0,\"success\":false,\"message\":\"It seems like QR is absent or unreadable\"}"
		 * ; pdfResponseBody=
		 * "{\"status\":\"success\",\"data\":{\"File Name\":\"MAT 332H-Fall 2010.pdf\",\"Customer Name\":null,\"Customer GSTIN\":null,\"Customer Address\":null,\"Customer State\":null,\"Customer State Code\":null,\"Customer Pincode\":null,\"Billing Address\":null,\"Shipping Address\":null,\"Document Number\":null,\"Document Date\":null,\"Document Type\":\"Syllabus\",\"IRN Number\":null,\"IRN Date\":null,\"Supplier Name\":null,\"Supplier GSTIN\":null,\"Supplier Address\":null,\"Supplier State\":null,\"Supplier State Code\":null,\"Supplier Pincode\":null,\"Place of Supply\":null,\"Sub Total\":null,\"Total Tax\":null,\"Invoice Total\":null,\"Purchase Order Number\":null,\"CGST Rate\":null,\"CGST Amount\":null,\"SGST Rate\":null,\"SGST Amount\":null,\"IGST Rate\":null,\"IGST Amount\":null,\"UTGST Rate\":null,\"UTGST Amount\":null,\"CESS Rate\":null,\"CESS Amount\":null,\"Reverse Charge / RCM Flag\":null,\"Line-items\":[]},\"file_path\":\"MAT 332H-Fall 2010.pdf\"}"
		 * ;
		 */
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		JsonPdfValidatorFinalRespDto finalResp = null;
		try {
			QRValidatorRespDto qrDetails = parseQRResponse(qrResponseBody);
			PdfValidatorRespDto pdfDetails = parsePDFResponse(pdfResponseBody);
			finalResp = rconJsonPdfUtility.reconPdfvsJsonResp(qrDetails,
					pdfDetails);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("final Resp Option C {} ", finalResp);
			}
			finalResp.setFileId(fileId);
			incrementCounts(finalResp.getSignature(),
					finalResp.getReportCategory(), countDto);

			if (Strings.isNullOrEmpty(qrDetails.getMessage())
					&& !Strings.isNullOrEmpty(pdfDetails.getMessage())) {
				finalResp.setMismatchReasons(pdfDetails.getMessage());
				// finalResp.setValidatedDate(null);

			} else if (!Strings.isNullOrEmpty(qrDetails.getMessage())
					&& Strings.isNullOrEmpty(pdfDetails.getMessage())) {
				finalResp.setMismatchReasons(qrDetails.getMessage());
				// finalResp.setValidatedDate(null);

			}

			createQRResponseLog(fileId, fileName, qrResponseBody,
					pdfResponseBody, null, gson.toJson(finalResp), finalResp);

			boolean isError = false;
			if (!Strings.isNullOrEmpty(pdfDetails.getMessage())
					|| !Strings.isNullOrEmpty(qrDetails.getMessage())) {
				isError = true;
			}

			if (!Strings.isNullOrEmpty(pdfDetails.getMessage())
					&& !Strings.isNullOrEmpty(qrDetails.getMessage())) {
				throw new AppException();
			}

			QRPDFJSONResponseSummaryEntity summaryEntity = convertFromJsonPdfValidatorFinalRespDto(
					finalResp, fileId, qrDetails.getIrn(), isError);
			LOGGER.debug("UploadType from EntityID {} ", uploadType, entityId);
			if (uploadType.equalsIgnoreCase("webUpload") && entityId != null) {
				summaryEntity.setEntityId(Long.parseLong(entityId));
			} else {
				qrCodeService.setEntityId(summaryEntity,
						summaryEntity.getBuyerGstinJson(),
						summaryEntity.getFileId());
			}

			if (Strings.isNullOrEmpty(qrDetails.getMessage())
					&& !Strings.isNullOrEmpty(pdfDetails.getMessage())) {
				LOGGER.debug(" inside block 1");
				finalResp.setMismatchReasons(pdfDetails.getMessage());
				summaryEntity.setMisMatchReasons(pdfDetails.getMessage());
				summaryEntity.setIsProcessedQR(false);
				summaryEntity.setMismatchAttributes(null);
				/*
				 * summaryEntity.setValidatedDate(null);
				 * summaryEntity.setValidatedDateStr(null);
				 */ } else if (!Strings.isNullOrEmpty(qrDetails.getMessage())
						&& Strings.isNullOrEmpty(pdfDetails.getMessage())) {
					 
					 LOGGER.debug(" inside block 2");
				finalResp.setMismatchReasons(qrDetails.getMessage());
				summaryEntity.setMisMatchReasons(qrDetails.getMessage());
				summaryEntity.setIsProcessedQR(false);
				summaryEntity.setMismatchAttributes(null);
				/*
				 * summaryEntity.setValidatedDate(null);
				 * summaryEntity.setValidatedDateStr(null);
				 */
			}
			listofSumm.add(summaryEntity);

			return finalResp;
		} catch (Exception e) {
			String msg = String.format(
					"Exception while doing the Recon for File ID %s", fileId);
			JsonObject hdr = new JsonObject();
			hdr.addProperty("status", "E");
			JsonObject resp = new JsonObject();
			resp.addProperty("errMsg", msg);
			JsonObject finalResponse = new JsonObject();
			finalResponse.add("hdr", hdr);
			finalResponse.add("resp", resp);
			createQRResponseLog(fileId, fileName, qrResponseBody,
					pdfResponseBody, null, gson.toJson(finalResponse),
					finalResp);
			LOGGER.error(msg, e);
			throw new AppException(msg);
		}
	}

	private QRValidatorRespDto parseQRResponse(String qrResponseBody) {
		if (!qrResponseBody.equalsIgnoreCase(QRCommonUtility.EXCEP_ERROR)) {
			JsonObject respObject = JsonParser.parseString(qrResponseBody)
					.getAsJsonObject();
			int status = respObject.get("status").getAsInt();
			if (status == 1) {
				String resultObjStr = respObject.get("result").toString();
				JsonObject resultObject = JsonParser.parseString(resultObjStr)
						.getAsJsonArray().get(0).getAsJsonObject();
				return GsonUtil.gsonInstanceWithEWBDateFormat()
						.fromJson(resultObject, QRValidatorRespDto.class);
			} else if (status == 0) {
				String message = respObject.has("message")
						? respObject.get("message").getAsString()
						: "QR Code Decryption Failed";
				QRValidatorRespDto dto = new QRValidatorRespDto();
				dto.setMessage(message);
				return dto;
			} else {

				String errMsg = "Received Error response while processing the QR.";
				LOGGER.error(errMsg);

				QRValidatorRespDto dto = new QRValidatorRespDto();
				dto.setMessage("QR Code Decryption Failed");
				return dto;
			}
		} else {
			QRValidatorRespDto dto = new QRValidatorRespDto();
			dto.setMessage("QR Code Decryption Failed");
			return dto;

		}
	}

	private PdfValidatorRespDto parsePDFResponse(String pdfResponseBody) {
		if (!pdfResponseBody.equalsIgnoreCase(QRCommonUtility.EXCEP_ERROR)) {
			JsonObject respObject = JsonParser.parseString(pdfResponseBody)
					.getAsJsonObject();
			String status = respObject.get("status").getAsString();
			if (status.equalsIgnoreCase("success")
					|| status.equalsIgnoreCase("1")) {
				String resultObjStr = respObject.get("data").toString();
				/*
				 * JsonObject resultObject =
				 * JsonParser.parseString(resultObjStr)
				 * .getAsJsonArray().get(0).getAsJsonObject();
				 */ PdfValidatorRespDto pdfDetails = GsonUtil
						.gsonInstanceWithEWBDateFormat()
						.fromJson(resultObjStr, PdfValidatorRespDto.class);
				return pdfDetails;
			} else if (status.equalsIgnoreCase("0")) {
				String message = respObject.has("message")
						? respObject.get("message").getAsString()
						: "PDF Reader Failed";
				PdfValidatorRespDto dto = new PdfValidatorRespDto();
				dto.setMessage(message);
				return dto;
			} else {
				PdfValidatorRespDto dto = new PdfValidatorRespDto();
				dto.setMessage("PDF Reader Failed");
				return dto;
			}
		} else {
			PdfValidatorRespDto dto = new PdfValidatorRespDto();
			dto.setMessage("PDF Reader Failed");
			return dto;

		}
	}

	private void incrementCounts(String signature, String reportCategory,
			QRCountDto countDto) {
		countDto.incrementQrCnt();
		if (!Valid.equalsIgnoreCase(signature)) {
			countDto.incrementSigMisCnt();
		}
		if (!Strings.isNullOrEmpty(reportCategory)
				&& "Match".equalsIgnoreCase(reportCategory)) {
			countDto.incrementFullMatCnt();
		}
	}

	private QRResponseLogEntity createQRResponseLog(Long fileId,
			String fileName, String qrResponseBody, String pdfResponseBody,
			String qrPdfReconResponse, String jsonPdfReconResponse,
			JsonPdfValidatorFinalRespDto jsonPdfresp) {

		LOGGER.debug(" inside log 1");
		QRResponseLogEntity responseLog = new QRResponseLogEntity();
		responseLog.setFileId(fileId);
		responseLog.setFileName(fileName);
		responseLog.setQrResponse(qrResponseBody);
		responseLog.setPdfResponse(pdfResponseBody);
		responseLog.setCreatedBy("SYSTEM");
		responseLog.setCreatedOn(LocalDateTime.now());
		responseLog.setQrPdfReconResponse(qrPdfReconResponse);
		responseLog.setJsonPdfReconResponse(jsonPdfReconResponse);
		if (jsonPdfresp != null) {
			responseLog.setIrnJsonResponse(jsonPdfresp.getJsonResponse());
		}
		respLogRep.save(responseLog);
		return responseLog;
	}

	public QRPDFResponseSummaryEntity convertFromQrvsPdfValidatorFinalRespDto(
			QrvsPdfValidatorFinalRespDto finalResp, Long fileId,
			boolean isError) {
		QRPDFResponseSummaryEntity entity = new QRPDFResponseSummaryEntity();
		entity.setFileId(fileId);
		entity.setBuyerGstinQR(finalResp.getRecipientGstinQR());
		if (!Strings.isNullOrEmpty(finalResp.getRecipientGstinQR())) {
			entity.setBuyerPanQR(
					finalResp.getRecipientGstinQR().substring(2, 12));
		}
		entity.setBuyerGstinPDF(finalResp.getRecipientGstinPDF());
		if (!Strings.isNullOrEmpty(finalResp.getRecipientGstinPDF())) {
			entity.setBuyerPanPDF(
					finalResp.getRecipientGstinPDF().substring(2, 12));
		}
		entity.setSellerGstinQR(finalResp.getSupplierGstinQR());
		if (!Strings.isNullOrEmpty(finalResp.getSupplierGstinQR())) {
			entity.setSellerPanQR(
					finalResp.getSupplierGstinQR().substring(2, 12));
		}
		entity.setSellerGstinPDF(finalResp.getSupplierGstinPDF());
		if (!Strings.isNullOrEmpty(finalResp.getSupplierGstinPDF())) {
			entity.setSellerPanPDF(
					finalResp.getSupplierGstinPDF().substring(2, 12));
		}
		entity.setDocNoQR(finalResp.getDocNoQR());
		entity.setDocNoPDF(finalResp.getDocNoPDF());
		entity.setDocDtQR(EYDateUtil.fmtDateOnly(finalResp.getDocDtQR(),
				DateTimeFormatter.ofPattern("dd/MM/yyyy")));
		entity.setDocDtPDF(EYDateUtil.fmtDateOnly(finalResp.getDocDtPDF(),
				DateTimeFormatter.ofPattern("dd/MM/yyyy")));
		entity.setDocTypeQR(finalResp.getDocTypQR());
		entity.setDocTypePDF(finalResp.getDocTypPDF());
		if (finalResp.getTotalInvoiceValueQR() != null) {
			entity.setTotInvValQR(
					String.valueOf(finalResp.getTotalInvoiceValueQR()));
		}
		if (finalResp.getTotalInvoiceValuePDF() != null) {
			entity.setTotInvValPDF(
					String.valueOf(finalResp.getTotalInvoiceValuePDF()));
		}
		entity.setItemCntQR(finalResp.getItemCountQR());
		entity.setMainHsnCodeQR(finalResp.getMainHsnCodeQR());
		entity.setMainHsnCodePDF(finalResp.getMainHsnCodePDF());
		entity.setIrnQR(finalResp.getIrnQR());
		entity.setIrnPDF(finalResp.getIrnPDF());
		entity.setIrnDateQR(EYDateUtil.fmtDate4(finalResp.getIrnDateQR()));
		entity.setIrnDatePDF(EYDateUtil.fmtDateOnly(finalResp.getIrnDatePDF(),
				DateTimeFormatter.ofPattern("dd/MM/yyyy")));
		entity.setSignatureQR(finalResp.getSignature());
		entity.setFileName(finalResp.getFileName());
		entity.setValidatedDate(finalResp.getValidatedDate());
		entity.setDeclaration(finalResp.getDeclarationRule48());
		entity.setReportCategory(finalResp.getReportCategory());
		entity.setMatchReasons(finalResp.getMatchReasons());
		entity.setMisMatchReasons(finalResp.getMismatchReasons());
		entity.setMismatchAttributes(finalResp.getMismatchReasons());

		// save
		entity.setMatchCount(finalResp.getMatchCount());
		entity.setMismatchCount(finalResp.getMismatchCount());
		entity.setDocKeyQR(QRCommonUtility.generateQRCodeKey(
				finalResp.getSupplierGstinQR(),
				EYDateUtil.fmtDateOnly(finalResp.getDocDtQR(),
						DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				finalResp.getDocTypQR(), finalResp.getDocNoQR(),
				finalResp.getRecipientGstinQR()));
		entity.setDocKeyQR(QRCommonUtility.generateQRCodeKey(
				finalResp.getSupplierGstinPDF(),
				EYDateUtil.fmtDateOnly(finalResp.getDocDtPDF(),
						DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				finalResp.getDocTypPDF(), finalResp.getDocNoPDF(),
				finalResp.getRecipientGstinPDF()));

		entity.setIsFullMatch(!Strings.isNullOrEmpty(finalResp.getReportCategory())?finalResp.getReportCategory().equalsIgnoreCase("Match") ? true
			: false:false);
			
		// If the code reaches here, it means the PDF API has returned a Valid
		// Response.
		entity.setIsProcessedQR(true);
		entity.setIsSigMisMat(Strings.isNullOrEmpty(finalResp.getSignature())
				|| "InValid".equalsIgnoreCase(finalResp.getSignature()));
		entity.setFileId(fileId);
		entity.setCreatedBy((SecurityContext.getUser() != null
				&& SecurityContext.getUser().getUserPrincipalName() != null)
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM");
		entity.setCreatedOn(LocalDateTime.now());

		if (Strings.isNullOrEmpty(finalResp.getIrnQR())) {
			entity.setEinvAppli(
					einvMasterGstinRepo.findByPan(entity.getSellerPanPDF())
							.isPresent() ? "Yes" : "No");
		} else {
			entity.setEinvAppli("Yes");
		}

		if (!isError) {
			matchAttributes(
					new ArrayList<String>(Arrays
							.asList(finalResp.getMatchReasons().split(", "))),
					entity, null);
		}
		return entity;
	}

	public QRPDFJSONResponseSummaryEntity convertFromJsonPdfValidatorFinalRespDto(
			JsonPdfValidatorFinalRespDto finalResp, Long fileId, String irnQR,
			boolean isError) {
		QRPDFJSONResponseSummaryEntity entity = new QRPDFJSONResponseSummaryEntity();
		entity.setFileId(fileId);

		entity.setBuyerGstinJson(finalResp.getRecipientGstinJSON());
		if (!Strings.isNullOrEmpty(finalResp.getRecipientGstinJSON())) {
			entity.setBuyerPanJson(
					finalResp.getRecipientGstinJSON().substring(2, 12));
		}

		entity.setBuyerGstinPDF(finalResp.getRecipientGstinPDF());
		if (!Strings.isNullOrEmpty(finalResp.getRecipientGstinPDF())) {
			entity.setBuyerPanPDF(
					finalResp.getRecipientGstinPDF().substring(2, 12));
		}

		entity.setSellerGstinJson(finalResp.getSupplierGstinJSON());
		if (!Strings.isNullOrEmpty(finalResp.getSupplierGstinJSON())) {
			entity.setSellerPanJson(
					finalResp.getSupplierGstinJSON().substring(2, 12));
		}
		entity.setSellerGstinPDF(finalResp.getSupplierGstinPDF());
		if (!Strings.isNullOrEmpty(finalResp.getSupplierGstinPDF())) {
			entity.setSellerPanPDF(
					finalResp.getSupplierGstinPDF().substring(2, 12));
		}
		entity.setDocNoJson(finalResp.getDocNoJSON());
		entity.setDocNoPDF(finalResp.getDocNoPDF());
		entity.setDocDtJson(EYDateUtil.fmtDateOnly(finalResp.getDocDtJSON(),
				DateTimeFormatter.ofPattern("dd/MM/yyyy")));
		entity.setDocDtPDF(EYDateUtil.fmtDateOnly(finalResp.getDocDtPDF(),
				DateTimeFormatter.ofPattern("dd/MM/yyyy")));
		entity.setDocTypeJson(finalResp.getDocTypJSON());
		entity.setDocTypePDF(finalResp.getDocTypPDF());

		if (finalResp.getTotalInvoiceValueJSON() != null) {
			entity.setTotInvValJson(
					String.valueOf(finalResp.getTotalInvoiceValueJSON()));
		}
		if (finalResp.getTotalInvoiceValuePDF() != null) {
			entity.setTotInvValPDF(
					String.valueOf(finalResp.getTotalInvoiceValuePDF()));
		}
		entity.setItemCntJson(finalResp.getItemCountJSON());
		entity.setItemCntPDF(finalResp.getItemCountPDF());
		entity.setMainHsnCodeJson(finalResp.getMainHsnCodeJSON());
		entity.setMainHsnCodePDF(finalResp.getMainHsnCodePDF());
		entity.setSignatureQR(finalResp.getSignature());
		entity.setIrnJson(finalResp.getIrnJSON());
		entity.setIrnPDF(finalResp.getIrnPDF());
		entity.setIrnDateJson(EYDateUtil.fmtDate4(finalResp.getIrnDateJSON()));
		entity.setIrnDatePDF(EYDateUtil.fmtDateOnly(finalResp.getIrnDatePDF(),
				DateTimeFormatter.ofPattern("dd/MM/yyyy")));
		entity.setIrnStatusJson(finalResp.getIrnStatusJSON());

		entity.setIrnCancellationDateJson(
				EYDateUtil.fmtDate2(finalResp.getIrnCancelDtJSON()));
		entity.setPosJson(finalResp.getPosJSON());
		entity.setPosPdf(finalResp.getPosPdf());
		entity.setTaxableValueJson(finalResp.getTaxableJSON());
		entity.setTaxableValuePdf(finalResp.getTaxablePdf());
		entity.setIgstJson(finalResp.getIgstJSON());
		entity.setIgstPdf(finalResp.getIgstPdf());
		entity.setCgstJson(finalResp.getCgstJSON());
		entity.setCgstPdf(finalResp.getCgstPdf());
		entity.setSgstUtgstJson(finalResp.getSgstUgstJSON());
		entity.setSgstUtgstPdf(finalResp.getSgstUgstPdf());
		entity.setCessJson(finalResp.getCessJSON());
		entity.setCessPdf(finalResp.getCessPdf());
		entity.setTotalTaxJson(finalResp.getTotalJSON());
		entity.setTotalTaxPdf(finalResp.getTotalPdf());
		entity.setReverseChargeJson(finalResp.getRcmJSON());
		entity.setReverseChargePdf(finalResp.getRcmPDF());
		entity.setPurchaseOrderNoJson(finalResp.getPurchaseOrderJson());
		entity.setPurchaseOrderNoPdf(finalResp.getPurchaseOrderPdf());
		entity.setQuantityJson(finalResp.getQuantityJSON());
		entity.setQuantityPdf(finalResp.getQuantityPDF());
		entity.setUnitPriceJson(finalResp.getUnitPriceJSON());
		entity.setUnitPricePdf(finalResp.getUnitPricePDF());
		entity.setLineItemAmountJson(finalResp.getLineItemJSON());
		entity.setLineItemAmountPdf(finalResp.getLineItemPDF());
		entity.setFileName(finalResp.getFileName());
		entity.setValidatedDate(finalResp.getValidatedDate());
		entity.setDeclaration(finalResp.getDeclarationRule48());

		// save
		entity.setReportCategory(finalResp.getReportCategory());
		entity.setMatchReasons(finalResp.getMatchReasons());
		entity.setMisMatchReasons(finalResp.getMismatchReasons());
		entity.setMismatchAttributes(finalResp.getMismatchReasons());
		entity.setMatchCount(finalResp.getMatchCount());
		entity.setMismatchCount(finalResp.getMismatchCount());
		entity.setDocKeyJson(QRCommonUtility.generateQRCodeKey(
				finalResp.getSupplierGstinJSON(),
				EYDateUtil.fmtDateOnly(finalResp.getDocDtJSON(),
						DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				finalResp.getDocTypJSON(), finalResp.getDocNoJSON(),
				finalResp.getRecipientGstinJSON()));
		entity.setDocKeyPDF(QRCommonUtility.generateQRCodeKey(
				finalResp.getSupplierGstinPDF(),
				EYDateUtil.fmtDateOnly(finalResp.getDocDtPDF(),
						DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				finalResp.getDocTypPDF(), finalResp.getDocNoPDF(),
				finalResp.getRecipientGstinPDF()));

		entity.setIsFullMatch(
				!Strings.isNullOrEmpty(finalResp.getReportCategory())
						&& finalResp.getReportCategory()
								.equalsIgnoreCase("Match") ? true : false);

		// If the code reaches here, it means the PDF API has returned a Valid
		// Response.
		entity.setIsProcessedQR(true);
		entity.setIsSigMisMat(Strings.isNullOrEmpty(finalResp.getSignature())
				|| "InValid".equalsIgnoreCase(finalResp.getSignature()));

		entity.setCreatedBy((SecurityContext.getUser() != null
				&& SecurityContext.getUser().getUserPrincipalName() != null)
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM");
		entity.setCreatedOn(LocalDateTime.now());
		entity.setJsonResponse(finalResp.getJsonResponse());

		if (Strings.isNullOrEmpty(irnQR)) {
			entity.setEinvAppli(
					einvMasterGstinRepo.findByPan(entity.getSellerPanPDF())
							.isPresent() ? "Yes" : "No");
		} else {
			entity.setEinvAppli("Yes");
		}

		if (!isError) {
			matchAttributes(
					new ArrayList<String>(Arrays
							.asList(finalResp.getMatchReasons().split(", "))),
					null, entity);
		}
		return entity;
	}

	private void matchAttributes(List<String> matchReasons,
			QRPDFResponseSummaryEntity entity,
			QRPDFJSONResponseSummaryEntity qrPdfJsonEntity) {
		if (entity != null) {
			entity.setSellerGstinMatch(matchReasons.contains("SGSTIN")
					? QRCodeValidatorHelper.Available
					: QRCodeValidatorHelper.UnAvailable);
			entity.setBuyerGstinMatch(matchReasons.contains("RGSTIN")
					? QRCodeValidatorHelper.Available
					: QRCodeValidatorHelper.UnAvailable);
			entity.setDocNoMatch(matchReasons.contains("Doc No(SC)")
					|| matchReasons.contains("Doc No")
							? QRCodeValidatorHelper.Available
							: QRCodeValidatorHelper.UnAvailable);
			entity.setDocDtMatch(matchReasons.contains("Doc Date")
					? QRCodeValidatorHelper.Available
					: QRCodeValidatorHelper.UnAvailable);
			entity.setDocTypeMatch(matchReasons.contains("Doc Type")
					? QRCodeValidatorHelper.Available
					: QRCodeValidatorHelper.UnAvailable);
			entity.setTotInvValMatch(
					matchReasons.contains("Total Invoice Value")
							? QRCodeValidatorHelper.Available
							: QRCodeValidatorHelper.UnAvailable);
			entity.setMainHsnCodeMatch(matchReasons.contains("HSN")
					? QRCodeValidatorHelper.Available
					: QRCodeValidatorHelper.UnAvailable);
			entity.setIrnMatch(matchReasons.contains("IRN")
					? QRCodeValidatorHelper.Available
					: QRCodeValidatorHelper.UnAvailable);
			entity.setIrnDateMatch(matchReasons.contains("IRN Date")
					? QRCodeValidatorHelper.Available
					: QRCodeValidatorHelper.UnAvailable);
			entity.setSignatureMatch(matchReasons.contains("Signature")
					? QRCodeValidatorHelper.Available
					: QRCodeValidatorHelper.UnAvailable);
		} else if (qrPdfJsonEntity != null) {
			qrPdfJsonEntity.setSellerGstinMatch(matchReasons.contains("SGSTIN")
					? QRCodeValidatorHelper.Available
					: QRCodeValidatorHelper.UnAvailable);
			qrPdfJsonEntity.setBuyerGstinMatch(matchReasons.contains("RGSTIN")
					? QRCodeValidatorHelper.Available
					: QRCodeValidatorHelper.UnAvailable);
			qrPdfJsonEntity.setDocNoMatch(matchReasons.contains("Doc No(SC)")
					|| matchReasons.contains("Doc No")
							? QRCodeValidatorHelper.Available
							: QRCodeValidatorHelper.UnAvailable);
			qrPdfJsonEntity.setDocDtMatch(matchReasons.contains("Doc Date")
					? QRCodeValidatorHelper.Available
					: QRCodeValidatorHelper.UnAvailable);
			qrPdfJsonEntity.setDocTypeMatch(matchReasons.contains("Doc Type")
					? QRCodeValidatorHelper.Available
					: QRCodeValidatorHelper.UnAvailable);
			qrPdfJsonEntity.setTotInvValMatch(
					matchReasons.contains("Total Invoice Value")
							? QRCodeValidatorHelper.Available
							: QRCodeValidatorHelper.UnAvailable);
			qrPdfJsonEntity.setMainHsnCodeMatch(matchReasons.contains("HSN")
					? QRCodeValidatorHelper.Available
					: QRCodeValidatorHelper.UnAvailable);
			qrPdfJsonEntity.setIrnMatch(matchReasons.contains("IRN")
					? QRCodeValidatorHelper.Available
					: QRCodeValidatorHelper.UnAvailable);
			qrPdfJsonEntity.setIrnDateMatch(matchReasons.contains("IRN Date")
					? QRCodeValidatorHelper.Available
					: QRCodeValidatorHelper.UnAvailable);
			qrPdfJsonEntity.setSignatureMatch(matchReasons.contains("Signature")
					? QRCodeValidatorHelper.Available
					: QRCodeValidatorHelper.UnAvailable);

			qrPdfJsonEntity
					.setTaxableValueMatch(matchReasons.contains("Taxable Value")
							? QRCodeValidatorHelper.Available
							: QRCodeValidatorHelper.UnAvailable);

			qrPdfJsonEntity.setIgstMatch(matchReasons.contains("IGST")
					? QRCodeValidatorHelper.Available
					: QRCodeValidatorHelper.UnAvailable);

			qrPdfJsonEntity.setCgstMatch(matchReasons.contains("CGST")
					? QRCodeValidatorHelper.Available
					: QRCodeValidatorHelper.UnAvailable);

			qrPdfJsonEntity.setSgstMatch(matchReasons.contains("SGST/UTGST")
					? QRCodeValidatorHelper.Available
					: QRCodeValidatorHelper.UnAvailable);

			qrPdfJsonEntity.setCessMatch(matchReasons.contains("CESS")
					? QRCodeValidatorHelper.Available
					: QRCodeValidatorHelper.UnAvailable);

			qrPdfJsonEntity.setTotalTaxMatch(matchReasons.contains("Total Tax")
					? QRCodeValidatorHelper.Available
					: QRCodeValidatorHelper.UnAvailable);

		} else {

		}
	}

	public static boolean isFullMatch(QRResponseSummaryEntity qrSumm) {

		return Available.equalsIgnoreCase(qrSumm.getBuyerGstinMatch())
				&& Available.equalsIgnoreCase(qrSumm.getSellerGstinMatch())
				&& Available.equalsIgnoreCase(qrSumm.getIrnMatch())
				&& Available.equalsIgnoreCase(qrSumm.getMainHsnCodeMatch())
				&& Available.equalsIgnoreCase(qrSumm.getDocDtMatch())
				&& Available.equalsIgnoreCase(qrSumm.getDocNoMatch())
				&& Available.equalsIgnoreCase(qrSumm.getDocTypeMatch())
				&& Available.equalsIgnoreCase(qrSumm.getTotInvValMatch())
				&& Valid.equalsIgnoreCase(qrSumm.getSignature());
	}

	private static QRResponseSummaryEntity convertDtoToEntity(
			String invoiceDetails) {

		QRResponseSummaryEntity respEntity = new QRResponseSummaryEntity();
		DummyQRResponseSummaryReqDto reqDto = GsonUtil.newSAPGsonInstance()
				.fromJson(invoiceDetails, DummyQRResponseSummaryReqDto.class);
		respEntity.setSellerGstin(reqDto.getSellerGstin());
		respEntity.setBuyerGstin(reqDto.getBuyerGstin());
		respEntity.setDocNo(reqDto.getDocNo());
		respEntity.setDocDt(reqDto.getDocDt());

		if (!Strings.isNullOrEmpty(reqDto.getDocType())) {
			String docType = null;
			if ("CRN".equalsIgnoreCase(reqDto.getDocType())) {
				docType = "CR";
			} else if ("DBN".equalsIgnoreCase(reqDto.getDocType())) {
				docType = "DR";
			} else {
				docType = reqDto.getDocType();
			}
			respEntity.setDocType(docType);
		}
		respEntity.setTotInvVal(reqDto.getTotInvVal());
		respEntity.setItemCnt(reqDto.getItemCnt());
		respEntity.setFileName(reqDto.getFileName());
		respEntity.setMainHsnCode(reqDto.getMainHsnCode());
		respEntity.setIrn(reqDto.getIrn());
		respEntity.setIrnDate(reqDto.getIrnDate());
		respEntity.setSignature(reqDto.getSignature());
		respEntity.setSellerGstinMatch(reqDto.getSellerGstinMatch());
		respEntity.setBuyerGstinMatch(reqDto.getBuyerGstinMatch());
		respEntity.setDocNoMatch(reqDto.getDocNoMatch());
		respEntity.setDocTypeMatch(reqDto.getDocTypeMatch());
		respEntity.setDocDtMatch(reqDto.getDocDtMatch());
		respEntity.setTotInvValMatch(reqDto.getTotInvValMatch());
		respEntity.setMainHsnCodeMatch(reqDto.getMainHsnCodeMatch());
		respEntity.setIrnMatch(reqDto.getIrnMatch());
		if (!Strings.isNullOrEmpty(reqDto.getDeclaration())) {
			respEntity.setDeclaration(reqDto.getDeclaration());
		}
		return respEntity;

	}

	public static Pair<String, String> uploadFile(MultipartFile inputfile,
			File tempDir, String extension) {
		try {
			String tempFileName = tempDir.getAbsolutePath() + File.separator
					+ inputfile.getOriginalFilename();

			File tempFile = new File(tempFileName);
			inputfile.transferTo(tempFile);

			LOGGER.debug("Transferred Successfully");
			String folderName = extension
					.equalsIgnoreCase(QRCodeValidatorConstants.PDF)
							? QRCodeValidatorConstants.PDF_FOLDER
							: QRCodeValidatorConstants.ZIP_FOLDER;
			Pair<String, String> uploadedDocDetails = DocumentUtility
					.uploadFile(tempFile, folderName);
			return uploadedDocDetails;
		} catch (Exception e) {
			String msg = String.format(
					"Error While Upload the File to DocRepo %s",
					inputfile.getOriginalFilename());
			LOGGER.error(msg);
			throw new AppException(msg);
		}
	}

	public static String generateQRCodeKey(String sGstin, String docDate,
			String docType, String docNum, String bGstin) {
		try {
			LocalDate fyLoc = DateUtil.parseObjToDate(docDate);
			String fy = GenUtil.getFinYear(fyLoc);
			sGstin = (sGstin != null) ? sGstin.trim() : "";
			fy = (fy != null) ? fy.trim() : "";
			docType = (docType != null) ? docType.trim() : "";
			docNum = (docNum != null) ? docNum.trim() : "";
			bGstin = (bGstin != null) ? bGstin.trim() : "";
			return new StringJoiner(WEB_UPLOAD_KEY).add(fy).add(sGstin)
					.add(docType).add(docNum).add(bGstin).toString();
		} catch (Exception e) {
			String msg = "Unable to generate the DocKey";
			LOGGER.error(msg);
			return null;
		}
	}

	private String hsnOptedVal() {
		Map<String, Config> configMap = configManager.getConfigs("QRVALIDATOR",
				"qr.hsn", TenantContext.getTenantId());

		String optAns = configMap.get("qr.hsn.value") == null ? "8"
				: configMap.get("qr.hsn.value").getValue();

		return optAns;
	}

	public String testIRPAPI() {
		try {
			File destFile = new File(
					"C://Users//LC657ZC//Downloads//000000147568-2023_INV.pdf");
			String authUrl = "https://devapi.eyasp.in/asppdfapi/v1.0/pdfreader";
			HttpPost httpPost = new HttpPost(authUrl);
			HttpEntity reqEntity = MultipartEntityBuilder.create()
					.addBinaryBody("input_file", destFile,
							ContentType.create("application/octet-stream"),
							destFile.getName())
					.addTextBody("pages", "1-3").build();
			httpPost.setHeader("apiaccesskey",
					"l7xx7bd905f7783d4df1a8594778a6826d75");
			httpPost.setHeader("apisecret", "a0cfa7f207694a759a0ca0a692baa8e9");
			httpPost.setEntity(reqEntity);
			LOGGER.debug("Requesting {} ", httpPost.getRequestLine());
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			String responseBody = httpClient.execute(httpPost, responseHandler);
			JsonObject respObject = JsonParser.parseString(responseBody)
					.getAsJsonObject();
			String status = respObject.get("Status").getAsString();
			if (status.equalsIgnoreCase("Error")) {
				return EXCEP_ERROR;
			}

			LOGGER.debug("responseBody {} ", responseBody);

			return responseBody;
		} catch (Exception ex) {
			LOGGER.error("Exception in QR Code Validator {}", ex);
			ex.printStackTrace();
			return ex.getMessage();
		}

	}

	public Pair<String, String> getQRandPDFResponse(File destFile,
			String qrAccessToken, String qrApiAccessKey, String qrApiUrl,
			String accessTokenPdf) {
		ExecutorService executor = Executors.newFixedThreadPool(2);
		try {
			CompletableFuture<String> offlineToolFuture = CompletableFuture
					.supplyAsync(() -> callOfflineTool(destFile, qrAccessToken,
							qrApiAccessKey, qrApiUrl), executor);

			Map<String, Config> configPDFMap = configManager
					.getConfigs("QRVALIDATOR", "pdf.details", "DEFAULT");

			/*
			 * String pdfApiSecret = configPDFMap .get("pdf.details.apisecret")
			 * == null ? "" : configPDFMap.get("pdf.details.apisecret")
			 * .getValue();
			 */
			String pdfApiAccesskey = configPDFMap
					.get("pdf.details.apiAccessKey") == null ? ""
							: configPDFMap.get("pdf.details.apiAccessKey")
									.getValue();

			String pdfReaderUrl = configPDFMap
					.get("pdf.details.validatorUrl") == null ? ""
							: configPDFMap.get("pdf.details.validatorUrl")
									.getValue();

			CompletableFuture<String> pdfToolFuture = CompletableFuture
					.supplyAsync(() -> callPdfTool(destFile, accessTokenPdf,
							pdfApiAccesskey, pdfReaderUrl), executor);

			CompletableFuture<Void> combinedFuture = CompletableFuture
					.allOf(offlineToolFuture, pdfToolFuture);

			// Wait for both tasks to complete
			combinedFuture.get();

			return new Pair<String, String>(offlineToolFuture.get(),
					pdfToolFuture.get());
		} catch (Exception e) {
			String msg = e.getMessage();
			LOGGER.error(msg, e);
			throw new AppException(msg, e.getMessage());
		} finally {
			PerfUtil.logEventToFile(PerfamanceEventConstants.QRCODEVALIDATOR,
					"QRCODEAPI_PopulateFileDtls_END",
					"QRCodeValidatorServiceImpl", "saveAndPersistQRReports",
					destFile.getName());
			executor.shutdown();
		}
	}

	public String optedOption(Long entityId) {
		String optAns = groupConfigPemtRepo.findQROptedOption();
		return Strings.isNullOrEmpty(optAns) ? "A" : optAns;
	}

	public static Map<String, String> optionDescMap() {
		Map<String, String> descMap = new HashMap<>();
		descMap.put("A", "Basic QR Code Validation");
		descMap.put("B", "PDF vs QR Code");
		descMap.put("C", "PDF vs Inward E-invoice JSON");
		return descMap;
	}

	public static String formattedDateOfUpload(
			LocalDateTime istDateTimeFromUTC) {
		LocalDateTime dateTimeInIst = EYDateUtil
				.toISTDateTimeFromUTC(istDateTimeFromUTC);
		DateTimeFormatter formatter = DateTimeFormatter
				.ofPattern("yyyy-MM-dd HH:mm:ss");
		return dateTimeInIst.format(formatter);
	}

	public String optedAns(String groupCode) {
		String answerDesc = "A";
		if (!Strings.isNullOrEmpty(groupCode)) {
			answerDesc = groupConfigPemtRepo
					.findAnswerForQRCodeValidator(groupCode);
		}
		return answerDesc;
	}

}
