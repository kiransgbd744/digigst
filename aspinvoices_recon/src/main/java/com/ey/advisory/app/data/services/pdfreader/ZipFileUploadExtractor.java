package com.ey.advisory.app.data.services.pdfreader;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.pdfreader.PDFReaderFileDetailsEntity;
import com.ey.advisory.app.data.entities.pdfreader.PDFResponseLineItemEntity;
import com.ey.advisory.app.data.entities.pdfreader.PDFResponseSummaryEntity;
import com.ey.advisory.app.data.entities.pdfreader.PDFUploadFileStatusEntity;
import com.ey.advisory.app.data.repositories.client.pdfreader.PDFReaderFileDetailsRepo;
import com.ey.advisory.app.data.repositories.client.pdfreader.PDFUploadFileStatusRepo;
import com.ey.advisory.app.data.services.pdfreader.dto.PDFCountDto;
import com.ey.advisory.app.data.services.qrcodevalidator.QRCommonUtility;
import com.ey.advisory.app.util.MergeFilesUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.QRCodeValidatorConstants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("ZipFileUploadExtractor")
public class ZipFileUploadExtractor implements FileUploadExtractor {

	@Autowired
	PDFCommonUtility commonUtility;

	@Autowired
	PDFReaderFileDetailsRepo pdfFileDtlsRepo;

	@Autowired
	PDFUploadFileStatusRepo fileStatusRepo;

	@Override
	public void extractAndPopulatePDFData(String fileType,
			List<PDFResponseSummaryEntity> listofSumm,
			List<PDFResponseLineItemEntity> pdflistItems, Long id,
			PDFCountDto countDto, String pdfAccesToken) {

		File downloadDir = null;
		File tempDir = null;
		File tempQRDir = null;
		File destFile = null;
		Document document = null;
		String responseBody = null;
		List<PDFReaderFileDetailsEntity> fileEntityList = new ArrayList<PDFReaderFileDetailsEntity>();
		try {
			Optional<PDFUploadFileStatusEntity> fileIdDetails = fileStatusRepo
					.findById(id);
			String docId = fileIdDetails.get().getDocId();
			String filePath = fileIdDetails.get().getFilePath();

			tempDir = MergeFilesUtil.createTempDir();
			tempQRDir = MergeFilesUtil.createTempDir();
			LOGGER.debug("ZipFileUploadExtractor FileName {}", filePath);
			downloadDir = QRCommonUtility.createDownloadDir(tempDir);
			destFile = new File(
					downloadDir.getAbsolutePath() + File.separator + filePath);
			LOGGER.debug("ZipFileUploadExtractor DestiFile {}", destFile);

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
			
			LOGGER.debug("destFile.getAbsolutePath()  {}", destFile.getAbsolutePath());
			


			boolean isUnZipped = unzip(destFile.getAbsolutePath(), tempQRDir);
			
			LOGGER.debug("isUnZipped  {}", isUnZipped);

			if (isUnZipped) {

				LOGGER.debug("Temp Directory  {}", tempQRDir);
				File directoryPath = new File(tempQRDir.getAbsolutePath());
				//File directoryPath = new File(tempQRDir.getAbsolutePath()+"/New folder");
				LOGGER.debug("directoryPath  {}", directoryPath);

				File filesList[] = directoryPath.listFiles();
				
				LOGGER.debug("filesList  {}", filesList.length);
				
				for (File fileDetails : filesList) {
					LOGGER.debug("ZipFileUploadExtractor FileDetails {}, {} ",
							fileDetails.getAbsoluteFile(),
							fileDetails.getName());
					
					LOGGER.debug("fileDetails file name  {}", fileDetails.getName());
					LOGGER.debug("fileDetails AbsolutePath {}", fileDetails.getAbsolutePath());
					
					countDto.incrementTotalCnt();

					String pdfResponseBody = commonUtility
							.getPDFResponse(fileDetails, pdfAccesToken);

					LOGGER.debug("Response from Offline Tool is {} ",
							responseBody);
					if (!(pdfResponseBody
							.equalsIgnoreCase(QRCommonUtility.EXCEP_ERROR))) {
						commonUtility.parseAndPopulatePDFResponse(listofSumm,
								pdflistItems, pdfResponseBody, id,countDto,
								fileDetails.getName());

					} else {
						LOGGER.debug("Zip Error Response File {}",
								fileDetails.getAbsoluteFile(),
								fileDetails.getName());
						PDFReaderFileDetailsEntity fileEntity = new PDFReaderFileDetailsEntity();
						fileEntity.setFileName(fileDetails.getName());
						fileEntity.setFileId(id);
						fileEntity.setCreatedBy("SYSTEM");
						fileEntity.setCreatedOn(LocalDateTime.now());
						fileEntity.setErrMsg(responseBody);
						fileEntityList.add(fileEntity);
					}
				}
				if (!fileEntityList.isEmpty()) {
					pdfFileDtlsRepo.saveAll(fileEntityList);
				}
				
				LOGGER.debug("All zip files are proccessed");
			}
			LOGGER.debug("isUnZipped  {}", "finished");

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
				LOGGER.debug("unzip fileName {}", fileName);
				File newFile = new File(destDir + File.separator + fileName);
				LOGGER.debug("newFile.getAbsolutePath() {}", newFile.getAbsolutePath());

				LOGGER.debug("unzip newFile {}", newFile.getName());
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

}
