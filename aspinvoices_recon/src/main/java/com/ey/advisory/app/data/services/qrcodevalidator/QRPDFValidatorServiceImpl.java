package com.ey.advisory.app.data.services.qrcodevalidator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.app.data.entities.qrcodevalidator.QRPDFResponseSummaryEntity;
import com.ey.advisory.app.data.entities.qrcodevalidator.QRUploadFileStatusEntity;
import com.ey.advisory.app.data.repositories.client.qrcodevalidator.QRPDFResponseSummaryRepo;
import com.ey.advisory.app.data.repositories.client.qrcodevalidator.QRUploadFileStatusRepo;
import com.ey.advisory.app.sftp.service.SFTPFileTransferService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigConstants;

import lombok.extern.slf4j.Slf4j;

@Component("QRPDFValidatorServiceImpl")
@Slf4j
public class QRPDFValidatorServiceImpl implements QRPDFValidatorService{
	
	@Autowired
	QRUploadFileStatusRepo qrUploadFileStatusRepo;

	@Autowired
	QRPDFResponseSummaryRepo qrpdfRespSummRepo;

	@Autowired
	@Qualifier("QRCodeValidatorServiceImpl")
	QRCodeValidatorService qrCodeService;

	@Autowired
	@Qualifier("SFTPFileTransferServiceImpl")
	private SFTPFileTransferService sftpService;

	@Override
	public void revIntegrateQRPDFData(List<Long> activeIds,
			Map<String, Config> configMap, File tempDir) {

		try {
			List<QRPDFResponseSummaryEntity> retList = qrpdfRespSummRepo
					.findByFileIdIn(activeIds);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("Option B retList size : {}", retList.size());

			Map<Long, List<QRPDFResponseSummaryEntity>> groupedMap = retList
					.stream().collect(Collectors
							.groupingBy(QRPDFResponseSummaryEntity::getFileId));

			List<String> filePaths = new ArrayList<>();
			for (Entry<Long, List<QRPDFResponseSummaryEntity>> pair : groupedMap
					.entrySet()) {
				try {
					Optional<QRUploadFileStatusEntity> fileRecord = qrUploadFileStatusRepo
							.findById(pair.getKey());
					Workbook workBook = qrCodeService.generateQrReport(null,
							pair.getValue(), null);
					String uploadedFileName = FilenameUtils
							.removeExtension(fileRecord.get().getFileName());
					// String fileName = DocumentUtility
					// .getUniqueFileName(uploadedFileName);
					String excelFilePath = tempDir.getAbsolutePath()
							+ File.separator + uploadedFileName + ".xlsx";
					workBook.save(excelFilePath, SaveFormat.XLSX);
					filePaths.add(excelFilePath);
				} catch (Exception ee) {
					LOGGER.error(
							"Error while generating QRValidator summary "
									+ "report for option B fileId: {}",
							pair.getKey(), ee);
				}
			}

			if (LOGGER.isDebugEnabled())
				LOGGER.debug("Option B Excel File Paths are : {}",
						filePaths.toString());

			boolean isReverseInt = sftpService.uploadFiles(filePaths, configMap
					.get(ConfigConstants.QR_VALIDATOR_SFTP_RESPONSE_DESTINATION).getValue());
			if (isReverseInt) {
				// UPDATE isReverseIntg flag to true
				qrUploadFileStatusRepo
						.updateReverseIntegrated(groupedMap.keySet());
			} else {
				LOGGER.error(
						"Reverse Integration failed for option B "
								+ "QR Validator with fileIds : {} ",
						groupedMap.keySet());
			}

		} catch (Exception ee) {
			String msg = "Exception occured while reverse integrating option B reports"
					+ " to SFTP Destination from QR Validator";
			LOGGER.error(msg, ee);
			throw new AppException(msg, ee);
		}

	}

}
