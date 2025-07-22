package com.ey.advisory.app.data.services.pdfreader;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.pdfreader.PDFReaderFileDetailsEntity;
import com.ey.advisory.app.data.entities.pdfreader.PDFResponseLineItemEntity;
import com.ey.advisory.app.data.entities.pdfreader.PDFResponseSummaryEntity;
import com.ey.advisory.app.data.entities.pdfreader.PDFUploadFileStatusEntity;
import com.ey.advisory.app.data.repositories.client.pdfreader.PDFReaderFileDetailsRepo;
import com.ey.advisory.app.data.repositories.client.pdfreader.PDFUploadFileStatusRepo;
import com.ey.advisory.app.data.repositories.client.qrcodevalidator.QRCodeFileDetailsRepo;
import com.ey.advisory.app.data.services.pdfreader.dto.PDFCountDto;
import com.ey.advisory.app.data.services.qrcodevalidator.QRCommonUtility;
import com.ey.advisory.app.util.MergeFilesUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.PerfamanceEventConstants;
import com.ey.advisory.common.QRCodeValidatorConstants;
import com.ey.advisory.core.config.ConfigManager;

import lombok.extern.slf4j.Slf4j;

@Component("PdfFileUploadExtractor")
@Slf4j
public class PdfFileUploadExtractor implements FileUploadExtractor {

	@Autowired
	PDFCommonUtility commonUtility;

	@Autowired
	PDFUploadFileStatusRepo fileStatusRepo;

	@Autowired
	QRCodeFileDetailsRepo qrFileDtlsRepo;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Autowired
	PDFReaderFileDetailsRepo pdfFileDtlsRepo;

	@Override
	public void extractAndPopulatePDFData(String fileType,
			List<PDFResponseSummaryEntity> listofSumm,
			List<PDFResponseLineItemEntity> pdflistItems, Long id,
			PDFCountDto countDto, String pdfAccesToken) {

		PerfUtil.logEventToFile(PerfamanceEventConstants.QRCODEVALIDATOR,
				"QRCODEAPI_EXTRACTPDF_START", "QRPdfFileUploadExtractor",
				"extractAndPopulateQRData", String.valueOf(id));
		File downloadDir = null;
		File tempDir = null;
		File destFile = null;
		Document document = null;
		try {
			Optional<PDFUploadFileStatusEntity> fileDetails = fileStatusRepo
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

			String pdfResponseBody = commonUtility.getPDFResponse(destFile,
					pdfAccesToken);

			if (!pdfResponseBody
					.equalsIgnoreCase(QRCommonUtility.EXCEP_ERROR)) {

				commonUtility.parseAndPopulatePDFResponse(listofSumm,
						pdflistItems, pdfResponseBody, id, countDto,
						fileDetails.get().getFileName());
			} else {
				LOGGER.debug("PDF Error Response File {}",
						destFile.getAbsoluteFile(), destFile.getName());
				PDFReaderFileDetailsEntity fileEntity = new PDFReaderFileDetailsEntity();
				fileEntity.setFileName(destFile.getName());
				fileEntity.setFileId(id);
				fileEntity.setCreatedBy("SYSTEM");
				fileEntity.setCreatedOn(LocalDateTime.now());
				fileEntity.setErrMsg(pdfResponseBody);

				pdfFileDtlsRepo.save(fileEntity);

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
