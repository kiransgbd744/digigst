package com.ey.advisory.app.sftp.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.erp.GSTR2aAutoReconSFTPRevIntgItemDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Jithendra.B
 *
 */

@Service("GSTR2ASftpResponseHandler")
@Slf4j
public class GSTR2ASftpResponseHandler {

	@Autowired
	private CommonUtility commonUtility;

	@Autowired
	@Qualifier("SFTPFileTransferServiceImpl")
	private SFTPFileTransferService sftpService;

	private static int CSV_BUFFER_SIZE = 8192;

	private static final String GSTR2A_SFTP_RESPONSE_DESTINATION = "ey.internal.gstr2a.sftp.response.destination";

	@Autowired
	@Qualifier("ConfigManagerImpl")
	private ConfigManager configManager;

	public Integer uploadToSftpServer(String filename,
			List<GSTR2aAutoReconSFTPRevIntgItemDto> reconDataList) {

		File tempDir = null;

		try {
			Map<String, Config> configMap = configManager.getConfigs("SFTP",
					"ey.internal.gstr2a.sftp.response");

			tempDir = createTempDir();
			String fullPath = tempDir.getAbsolutePath() + File.separator
					+ filename;

			try (Writer writer = new BufferedWriter(new FileWriter(fullPath),
					CSV_BUFFER_SIZE)) {

				String invoiceHeadersTemplate = commonUtility
						.getProp("gstr2.auto.recon.sftp.report.header");
				writer.append(invoiceHeadersTemplate);
				String[] columnMappings = commonUtility
						.getProp("getr2.auto.recon.sftp.report.column")
						.split(",");

				if (reconDataList != null && !reconDataList.isEmpty()) {

					ColumnPositionMappingStrategy<GSTR2aAutoReconSFTPRevIntgItemDto> mappingStrategy = new ColumnPositionMappingStrategy<>();
					mappingStrategy
							.setType(GSTR2aAutoReconSFTPRevIntgItemDto.class);
					mappingStrategy.setColumnMapping(columnMappings);
					StatefulBeanToCsvBuilder<GSTR2aAutoReconSFTPRevIntgItemDto> builder = new StatefulBeanToCsvBuilder<>(
							writer);
					StatefulBeanToCsv<GSTR2aAutoReconSFTPRevIntgItemDto> beanWriter = builder
							.withMappingStrategy(mappingStrategy)
							.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
							.withLineEnd(CSVWriter.DEFAULT_LINE_END)
							.withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER)
							.build();
					long generationStTime = System.currentTimeMillis();
					beanWriter.write(reconDataList);
					long generationEndTime = System.currentTimeMillis();
					long generationTimeDiff = (generationEndTime
							- generationStTime);
					if (LOGGER.isDebugEnabled()) {
						String msg = String.format("Total Time taken to"
								+ " Generate the report is '%d'" + "millisecs,"
								+ " Report Name and Data count:" + " '%d'",
								generationTimeDiff, reconDataList.size());
						LOGGER.debug(msg);
					}

				}
				flushWriter(writer);

				boolean isReverseInt = sftpService.uploadFiles(
						Arrays.asList(fullPath),
						configMap.get(GSTR2A_SFTP_RESPONSE_DESTINATION)
								.getValue());
				if (isReverseInt) {
					return 200;
				} else {
					return 0;
				}
			} catch (Exception e) {
				String msg = "Exception while writing to csv";
				LOGGER.error(msg, e);
				throw new AppException(msg, e);

			}
		} catch (Exception e) {
			String msg = "Exception while doing gstr2a sftp";
			LOGGER.error(msg, e);
			throw new AppException(msg, e);

		} finally {
			deleteTemporaryDirectory(tempDir);
		}
	}

	private void flushWriter(Writer writer) {
		if (writer != null) {
			try {
				writer.flush();
				writer.close();
				if (LOGGER.isDebugEnabled()) {
					String msg = "Flushed writer " + "successfully";
					LOGGER.debug(msg);
				}
			} catch (IOException e) {
				String msg = "Exception while " + "closing the file writer";
				LOGGER.error(msg);
				throw new AppException(msg, e);
			}
		}
	}

	private void deleteTemporaryDirectory(File tempFile) {

		if (tempFile != null && tempFile.exists()) {
			try {
				FileUtils.deleteDirectory(tempFile);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(String.format(
							"Deleted the Temp directory/Folder '%s'",
							tempFile.getAbsolutePath()));
				}
			} catch (Exception ex) {
				String msg = String.format(
						"Failed to remove the temp "
								+ "directory created for zip: '%s'. This will "
								+ "lead to clogging of disk space.",
						tempFile.getAbsolutePath());
				LOGGER.error(msg, ex);
			}
		}

	}

	private static File createTempDir() throws IOException {

		return Files.createTempDirectory("GSTR2aSftpPush").toFile();
	}

}