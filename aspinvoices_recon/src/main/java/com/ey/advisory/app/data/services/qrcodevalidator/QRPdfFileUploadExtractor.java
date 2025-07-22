package com.ey.advisory.app.data.services.qrcodevalidator;

import java.io.File;
import java.util.List;
import java.util.Optional;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.commons.io.FileUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.qrcodevalidator.QRPDFJSONResponseSummaryEntity;
import com.ey.advisory.app.data.entities.qrcodevalidator.QRPDFResponseSummaryEntity;
import com.ey.advisory.app.data.entities.qrcodevalidator.QRResponseSummaryEntity;
import com.ey.advisory.app.data.entities.qrcodevalidator.QRUploadFileStatusEntity;
import com.ey.advisory.app.data.repositories.client.qrcodevalidator.QRCodeFileDetailsRepo;
import com.ey.advisory.app.data.repositories.client.qrcodevalidator.QRUploadFileStatusRepo;
import com.ey.advisory.app.util.MergeFilesUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.PerfamanceEventConstants;
import com.ey.advisory.common.QRCodeValidatorConstants;
import com.ey.advisory.core.config.ConfigManager;

import lombok.extern.slf4j.Slf4j;

@Component("QRPdfFileUploadExtractor")
@Slf4j
public class QRPdfFileUploadExtractor implements QRFileUploadExtractor {

	@Autowired
	QRCommonUtility commonUtility;

	@Autowired
	QRUploadFileStatusRepo fileStatusRepo;

	@Autowired
	QRCodeFileDetailsRepo qrFileDtlsRepo;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Override
	public void extractAndPopulateQRData(String filePath, String fileType,
			List<QRResponseSummaryEntity> listofSumm, String accessToken,
			String apiAccessKey, Long id, QRCountDto countDto, String docId,
			String apiUrl , String uploadType , String entityId) {

		PerfUtil.logEventToFile(PerfamanceEventConstants.QRCODEVALIDATOR,
				"QRCODEAPI_EXTRACTPDF_START", "QRPdfFileUploadExtractor",
				"extractAndPopulateQRData", String.valueOf(id));

		File downloadDir = null;
		File tempDir = null;
		File destFile = null;
		Document document = null;
		String responseBody = null;
		try {
			tempDir = MergeFilesUtil.createTempDir();
			LOGGER.debug("FileName {}", filePath);
			downloadDir = QRCommonUtility.createDownloadDir(tempDir);
			destFile = new File(
					downloadDir.getAbsolutePath() + File.separator + filePath);
			LOGGER.debug("DestiFile {}", destFile);
			document = DocumentUtility.downloadDocumentByDocId(docId);
			if (document == null) {
				String msg = String.format(
						"Not Able to Download the Document for FileName %s in Folder %s with DocId %s",
						filePath, QRCodeValidatorConstants.PDF_FOLDER, docId);
				LOGGER.error(msg);
				throw new AppException(msg);
			}
			FileUtils.copyInputStreamToFile(
					document.getContentStream().getStream(), destFile);
			Optional<QRUploadFileStatusEntity> fileDetails = fileStatusRepo
					.findById(id);
			countDto.incrementTotalCnt();
			PerfUtil.logEventToFile(PerfamanceEventConstants.QRCODEVALIDATOR,
					"QRCODEAPI_OFFLINEAPI_START", "QRPdfFileUploadExtractor",
					"extractAndPopulateQRData", String.valueOf(id));
			responseBody = commonUtility.callOfflineTool(destFile, accessToken,
					apiAccessKey, apiUrl);
			PerfUtil.logEventToFile(PerfamanceEventConstants.QRCODEVALIDATOR,
					"QRCODEAPI_OFFLINEAPI_END", "QRPdfFileUploadExtractor",
					"extractAndPopulateQRData", String.valueOf(id));
			LOGGER.debug("Response from Offline Tool is {} ", responseBody);
			if (!QRCommonUtility.EXCEP_ERROR.equalsIgnoreCase(responseBody)) {
				commonUtility.parseAndPopulateResponse(listofSumm,
						responseBody, id, countDto,
						fileDetails.get().getFileName() , uploadType , entityId);
			} else {
				LOGGER.debug("Error Response File {}", filePath);
				countDto.incrementErrCnt();
			}
		} catch (Exception e) {
			String msg = String.format(
					"Error in Populating the PDF for File %s", filePath);
			LOGGER.error(msg, e);
			throw new AppException(msg);

		} finally {
			PerfUtil.logEventToFile(PerfamanceEventConstants.QRCODEVALIDATOR,
					"QRCODEAPI_EXTRACTPDF_END", "QRPdfFileUploadExtractor",
					"extractAndPopulateQRData", String.valueOf(id));
			GenUtil.deleteTempDir(tempDir);
			GenUtil.deleteTempDir(downloadDir);
		}
	}

	@Override
	public void extractAndPopulateQRPDFData(String fileType,
			List<QRPDFResponseSummaryEntity> listofSumm, String qrAccessToken,
			String qrApiAccessKey, Long id, QRCountDto countDto,
			String qrApiUrl, String accessTokenPdf ,String uploadType , String entityId) {
		PerfUtil.logEventToFile(PerfamanceEventConstants.QRCODEVALIDATOR,
				"QRCODEAPI_EXTRACTPDF_START", "QRPdfFileUploadExtractor",
				"extractAndPopulateQRData", String.valueOf(id));
		File downloadDir = null;
		File tempDir = null;
		File destFile = null;
		Document document = null;
		try {
			Optional<QRUploadFileStatusEntity> fileDetails = fileStatusRepo
					.findById(id);
			String docId = fileDetails.get().getDocId();
			String filePath = fileDetails.get().getFilePath();
			tempDir = MergeFilesUtil.createTempDir();
			LOGGER.debug("FileName {}", filePath);
			downloadDir = QRCommonUtility.createDownloadDir(tempDir);
			destFile = new File(
					downloadDir.getAbsolutePath() + File.separator + filePath);
			LOGGER.debug("DestiFile {}", destFile);
			document = DocumentUtility
					.downloadDocumentByDocId(fileDetails.get().getDocId());
			if (document == null) {
				String msg = String.format(
						"Not Able to Download the Document for FileName %s in Folder %s with DocId %s",
						filePath, QRCodeValidatorConstants.PDF_FOLDER, docId);
				LOGGER.error(msg);
				throw new AppException(msg);
			}
			FileUtils.copyInputStreamToFile(
					document.getContentStream().getStream(), destFile);
			countDto.incrementTotalCnt();

			Pair<String, String> qrPdfReconCall = commonUtility
					.getQRandPDFResponse(destFile, qrAccessToken,
							qrApiAccessKey, qrApiUrl,accessTokenPdf);

			if (qrPdfReconCall.getValue1()
					.equalsIgnoreCase(QRCommonUtility.EXCEP_ERROR)) {
				countDto.incrementErrCnt();

			} else {
				commonUtility.parseAndPopulateQRPDFResponse(listofSumm,
						qrPdfReconCall.getValue0(), qrPdfReconCall.getValue1(),
						id, countDto, fileDetails.get().getFileName() , uploadType , entityId);
			}
		} catch (Exception e) {
			String msg = String
					.format("Error in Populating the PDF for File %s", id);
			LOGGER.error(msg, e);
			throw new AppException(msg);

		} finally {
			PerfUtil.logEventToFile(PerfamanceEventConstants.QRCODEVALIDATOR,
					"QRCODEAPI_EXTRACTPDF_END", "QRPdfFileUploadExtractor",
					"extractAndPopulateQRData", String.valueOf(id));
			GenUtil.deleteTempDir(tempDir);
			GenUtil.deleteTempDir(downloadDir);
		}
	}

	@Override
	public void extractAndPopulateQRPDFJsonData(String fileType,
			List<QRPDFJSONResponseSummaryEntity> listofSumm,
			String qrAccessToken, String qrApiAccessKey, Long id,
			QRCountDto countDto, String qrApiUrl, String accessTokenpdf , String uploadType , String entityId) {
		PerfUtil.logEventToFile(PerfamanceEventConstants.QRCODEVALIDATOR,
				"QRCODEAPI_EXTRACTPDF_START", "QRPdfFileUploadExtractor",
				"extractAndPopulateQRData", String.valueOf(id));
		File downloadDir = null;
		File tempDir = null;
		File destFile = null;
		Document document = null;
		try {
			Optional<QRUploadFileStatusEntity> fileDetails = fileStatusRepo
					.findById(id);
			String docId = fileDetails.get().getDocId();
			String filePath = fileDetails.get().getFilePath();
			tempDir = MergeFilesUtil.createTempDir();
			LOGGER.debug("FileName {}", filePath);
			downloadDir = QRCommonUtility.createDownloadDir(tempDir);
			destFile = new File(
					downloadDir.getAbsolutePath() + File.separator + filePath);
			LOGGER.debug("DestiFile {}", destFile);
			document = DocumentUtility
					.downloadDocumentByDocId(fileDetails.get().getDocId());
			if (document == null) {
				String msg = String.format(
						"Not Able to Download the Document for FileName %s in Folder %s with DocId %s",
						filePath, QRCodeValidatorConstants.PDF_FOLDER, docId);
				LOGGER.error(msg);
				throw new AppException(msg);
			}
			FileUtils.copyInputStreamToFile(
					document.getContentStream().getStream(), destFile);
			countDto.incrementTotalCnt();

			Pair<String, String> qrPdfReconCall = commonUtility
					.getQRandPDFResponse(destFile, qrAccessToken,
							qrApiAccessKey, qrApiUrl,accessTokenpdf);

			if (qrPdfReconCall.getValue1()
					.equalsIgnoreCase(QRCommonUtility.EXCEP_ERROR)) {
				countDto.incrementErrCnt();

			} else {
				commonUtility.parseAndPopulateQRPDFJSONResponse(listofSumm,
						qrPdfReconCall.getValue0(), qrPdfReconCall.getValue1(),
						id, countDto, fileDetails.get().getFileName() , uploadType , entityId);
			}
		} catch (Exception e) {
			String msg = String
					.format("Error in Populating the PDF for File %s", id);
			LOGGER.error(msg, e);
			throw new AppException(msg);

		} finally {
			PerfUtil.logEventToFile(PerfamanceEventConstants.QRCODEVALIDATOR,
					"QRCODEAPI_EXTRACTPDF_END", "QRPdfFileUploadExtractor",
					"extractAndPopulateQRData", String.valueOf(id));
			GenUtil.deleteTempDir(tempDir);
			GenUtil.deleteTempDir(downloadDir);
		}
	}

}
