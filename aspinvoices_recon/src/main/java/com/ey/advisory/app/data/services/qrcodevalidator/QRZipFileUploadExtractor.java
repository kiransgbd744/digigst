package com.ey.advisory.app.data.services.qrcodevalidator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.commons.io.FileUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.qrcodevalidator.QRCodeFileDetailsEntity;
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
import com.ey.advisory.common.QRCodeValidatorConstants;

import lombok.extern.slf4j.Slf4j;

@Component("QRZipFileUploadExtractor")
@Slf4j
public class QRZipFileUploadExtractor implements QRFileUploadExtractor {

	@Autowired
	QRCommonUtility commonUtility;

	@Autowired
	QRCodeFileDetailsRepo qrFileDtlsRepo;

	@Autowired
	QRUploadFileStatusRepo fileStatusRepo;

	@Override
	public void extractAndPopulateQRData(String fileName, String fileType,
			List<QRResponseSummaryEntity> listofSumm, String accessToken,
			String apiAccessKey, Long id, QRCountDto countDto, String docId,
			String apiUrl , String uploadType , String entityId) {

		File downloadDir = null;
		File tempDir = null;
		File tempQRDir = null;
		File destFile = null;
		Document document = null;
		String responseBody = null;
		List<QRCodeFileDetailsEntity> fileEntityList = new ArrayList<QRCodeFileDetailsEntity>();
		try {
			tempDir = MergeFilesUtil.createTempDir();
			tempQRDir = MergeFilesUtil.createTempDir();
			LOGGER.debug("FileName {}", fileName);
			downloadDir = QRCommonUtility.createDownloadDir(tempDir);
			destFile = new File(
					downloadDir.getAbsolutePath() + File.separator + fileName);
			LOGGER.debug("DestiFile {}", destFile);

			document = DocumentUtility.downloadDocumentByDocId(docId);
			if (document == null) {
				String msg = String.format(
						"Not Able to Download the Document for FileName %s in Folder %s with DocId %s",
						fileName, QRCodeValidatorConstants.ZIP_FOLDER, docId);
				LOGGER.error(msg);
				throw new AppException(msg);
			}
			FileUtils.copyInputStreamToFile(
					document.getContentStream().getStream(), destFile);

			boolean isUnZipped = unzip(destFile.getAbsolutePath(), tempQRDir);

			if (isUnZipped) {
				File directoryPath = new File(tempQRDir.getAbsolutePath());
				File filesList[] = directoryPath.listFiles();
				for (File fileDetails : filesList) {
					LOGGER.debug("FileDetails {}, {} ",
							fileDetails.getAbsoluteFile(),
							fileDetails.getName());

					countDto.incrementTotalCnt();
					responseBody = commonUtility.callOfflineTool(fileDetails,
							accessToken, apiAccessKey, apiUrl);
					LOGGER.debug("Response from Offline Tool is {} ",
							responseBody);
					if (!QRCommonUtility.EXCEP_ERROR
							.equalsIgnoreCase(responseBody)) {
						commonUtility.parseAndPopulateResponse(listofSumm,
								responseBody, id, countDto,
								fileDetails.getName() , uploadType , entityId);
					} else {
						LOGGER.debug("Error Response File {}",
								fileDetails.getAbsoluteFile(),
								fileDetails.getName());
						countDto.incrementErrCnt();
						QRCodeFileDetailsEntity fileEntity = new QRCodeFileDetailsEntity();
						fileEntity.setFileName(fileDetails.getName());
						fileEntity.setFileId(id);
						fileEntity.setCreatedBy("SYSTEM");
						fileEntity.setCreatedOn(LocalDateTime.now());
						fileEntity.setErrMsg(responseBody);
						fileEntityList.add(fileEntity);
					}
				}
				if (!fileEntityList.isEmpty()) {
					qrFileDtlsRepo.saveAll(fileEntityList);
				}
			}

		} catch (Exception e) {
			String msg = String.format(
					"Error in Populating the Zip for File %s", fileName);
			LOGGER.error(msg, e);
			throw new AppException(msg);
		}

		finally {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Deleting temporary directories");
			}
			GenUtil.deleteTempDir(tempDir);
			GenUtil.deleteTempDir(tempQRDir);
			GenUtil.deleteTempDir(downloadDir);
		}
	}

	private static boolean unzip(String zipFilePath, File destDir) {
		FileInputStream fis;
		// buffer for read and write data to file
		byte[] buffer = new byte[1024];
		try {
			fis = new FileInputStream(zipFilePath);
			ZipInputStream zis = new ZipInputStream(fis);
			ZipEntry ze = zis.getNextEntry();
			while (ze != null) {
				String fileName = ze.getName();
				File newFile = new File(destDir + File.separator + fileName);
				new File(newFile.getParent()).mkdirs();
				FileOutputStream fos = new FileOutputStream(newFile);
				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}
				fos.close();
				zis.closeEntry();
				ze = zis.getNextEntry();
			}
			zis.closeEntry();
			zis.close();
			fis.close();
			return true;
		} catch (Exception e) {
			LOGGER.error("Error while unzipping and copying the file", e);
			return false;
		}

	}

	@Override
	public void extractAndPopulateQRPDFData(String fileType,
			List<QRPDFResponseSummaryEntity> listofSumm, String qrAccessToken,
			String qrApiAccessKey, Long id, QRCountDto countDto,
			String qrApiUrl, String pdfAccesToken , String uploadType , String entityId) {
		File downloadDir = null;
		File tempDir = null;
		File tempQRDir = null;
		File destFile = null;
		Document document = null;
		String responseBody = null;
		List<QRCodeFileDetailsEntity> fileEntityList = new ArrayList<QRCodeFileDetailsEntity>();
		try {
			Optional<QRUploadFileStatusEntity> fileIdDetails = fileStatusRepo
					.findById(id);
			String docId = fileIdDetails.get().getDocId();
			String filePath = fileIdDetails.get().getFilePath();

			tempDir = MergeFilesUtil.createTempDir();
			tempQRDir = MergeFilesUtil.createTempDir();
			LOGGER.debug("FileName {}", filePath);
			downloadDir = QRCommonUtility.createDownloadDir(tempDir);
			destFile = new File(
					downloadDir.getAbsolutePath() + File.separator + filePath);
			LOGGER.debug("DestiFile {}", destFile);

			document = DocumentUtility.downloadDocumentByDocId(docId);
			if (document == null) {
				String msg = String.format(
						"Not Able to Download the Document for FileName %s in Folder %s with DocId %s",
						filePath, QRCodeValidatorConstants.ZIP_FOLDER, docId);
				LOGGER.error(msg);
				throw new AppException(msg);
			}
			FileUtils.copyInputStreamToFile(
					document.getContentStream().getStream(), destFile);

			boolean isUnZipped = unzip(destFile.getAbsolutePath(), tempQRDir);

			if (isUnZipped) {
				File directoryPath = new File(tempQRDir.getAbsolutePath());
				File filesList[] = directoryPath.listFiles();
				for (File fileDetails : filesList) {
					LOGGER.debug("FileDetails {}, {} ",
							fileDetails.getAbsoluteFile(),
							fileDetails.getName());
					countDto.incrementTotalCnt();
					Pair<String, String> qrPdfReconCall = commonUtility
							.getQRandPDFResponse(fileDetails, qrAccessToken,
									qrApiAccessKey, qrApiUrl, pdfAccesToken);
					LOGGER.debug("Response from Offline Tool is {} ",
							responseBody);
					if (!(qrPdfReconCall.getValue0()
							.equalsIgnoreCase(QRCommonUtility.EXCEP_ERROR)
							|| qrPdfReconCall.getValue1().equalsIgnoreCase(
									QRCommonUtility.EXCEP_ERROR))) {
						commonUtility.parseAndPopulateQRPDFResponse(listofSumm,
								qrPdfReconCall.getValue0(),
								qrPdfReconCall.getValue1(), id, countDto,
								fileDetails.getName() , uploadType , entityId);
					} else {
						LOGGER.debug("Error Response File {}",
								fileDetails.getAbsoluteFile(),
								fileDetails.getName());
						countDto.incrementErrCnt();
						QRCodeFileDetailsEntity fileEntity = new QRCodeFileDetailsEntity();
						fileEntity.setFileName(fileDetails.getName());
						fileEntity.setFileId(id);
						fileEntity.setCreatedBy("SYSTEM");
						fileEntity.setCreatedOn(LocalDateTime.now());
						fileEntity.setErrMsg(responseBody);
						fileEntityList.add(fileEntity);
					}
				}
				if (!fileEntityList.isEmpty()) {
					qrFileDtlsRepo.saveAll(fileEntityList);
				}
			}

		} catch (Exception e) {
			String msg = String
					.format("Error in Populating the Zip for File %s", id);
			LOGGER.error(msg, e);
			throw new AppException(msg);
		} finally {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Deleting temporary directories");
			}
			GenUtil.deleteTempDir(tempDir);
			GenUtil.deleteTempDir(tempQRDir);
			GenUtil.deleteTempDir(downloadDir);
		}
	}

	@Override
	public void extractAndPopulateQRPDFJsonData(String fileType,
			List<QRPDFJSONResponseSummaryEntity> listofSumm, String accessToken,
			String apiAccessKey, Long id, QRCountDto countDto, String apiUrl, String pdfAccessToken ,
			String uploadType , String entityId) {
		File downloadDir = null;
		File tempDir = null;
		File tempQRDir = null;
		File destFile = null;
		Document document = null;
		String responseBody = null;
		List<QRCodeFileDetailsEntity> fileEntityList = new ArrayList<QRCodeFileDetailsEntity>();
		try {
			Optional<QRUploadFileStatusEntity> fileIdDetails = fileStatusRepo
					.findById(id);
			String docId = fileIdDetails.get().getDocId();
			String filePath = fileIdDetails.get().getFilePath();

			tempDir = MergeFilesUtil.createTempDir();
			tempQRDir = MergeFilesUtil.createTempDir();
			LOGGER.debug("FileName {}", filePath);
			downloadDir = QRCommonUtility.createDownloadDir(tempDir);
			destFile = new File(
					downloadDir.getAbsolutePath() + File.separator + filePath);
			LOGGER.debug("DestiFile {}", destFile);

			document = DocumentUtility.downloadDocumentByDocId(docId);
			if (document == null) {
				String msg = String.format(
						"Not Able to Download the Document for FileName %s in Folder %s with DocId %s",
						filePath, QRCodeValidatorConstants.ZIP_FOLDER, docId);
				LOGGER.error(msg);
				throw new AppException(msg);
			}
			FileUtils.copyInputStreamToFile(
					document.getContentStream().getStream(), destFile);

			boolean isUnZipped = unzip(destFile.getAbsolutePath(), tempQRDir);

			if (isUnZipped) {
				File directoryPath = new File(tempQRDir.getAbsolutePath());
				File filesList[] = directoryPath.listFiles();
				for (File fileDetails : filesList) {
					LOGGER.debug("FileDetails {}, {} ",
							fileDetails.getAbsoluteFile(),
							fileDetails.getName());
					countDto.incrementTotalCnt();
					Pair<String, String> qrPdfReconCall = commonUtility
							.getQRandPDFResponse(fileDetails, accessToken,
									apiAccessKey, apiUrl,pdfAccessToken);
					LOGGER.debug("Response from Offline Tool is {} ",
							responseBody);
					if (!(qrPdfReconCall.getValue0()
							.equalsIgnoreCase(QRCommonUtility.EXCEP_ERROR)
							|| qrPdfReconCall.getValue1().equalsIgnoreCase(
									QRCommonUtility.EXCEP_ERROR))) {
						commonUtility.parseAndPopulateQRPDFJSONResponse(
								listofSumm, qrPdfReconCall.getValue0(),
								qrPdfReconCall.getValue1(), id, countDto,
								fileDetails.getName() , uploadType , entityId);
					} else {
						LOGGER.debug("Error Response File {}",
								fileDetails.getAbsoluteFile(),
								fileDetails.getName());
						countDto.incrementErrCnt();
						QRCodeFileDetailsEntity fileEntity = new QRCodeFileDetailsEntity();
						fileEntity.setFileName(fileDetails.getName());
						fileEntity.setFileId(id);
						fileEntity.setCreatedBy("SYSTEM");
						fileEntity.setCreatedOn(LocalDateTime.now());
						fileEntity.setErrMsg(responseBody);
						fileEntityList.add(fileEntity);
					}
				}
				if (!fileEntityList.isEmpty()) {
					qrFileDtlsRepo.saveAll(fileEntityList);
				}
			}

		} catch (Exception e) {
			String msg = String
					.format("Error in Populating the Zip for File %s", id);
			LOGGER.error(msg, e);
			throw new AppException(msg);
		} finally {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Deleting temporary directories");
			}
			GenUtil.deleteTempDir(tempDir);
			GenUtil.deleteTempDir(tempQRDir);
			GenUtil.deleteTempDir(downloadDir);
		}
	}
}
