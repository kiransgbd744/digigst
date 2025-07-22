package com.ey.advisory.bcadmin.common.services.payload;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.file.Files;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.ey.advisory.bcadmin.common.dao.GetNicPayloadsDAO;
import com.ey.advisory.bcadmin.common.dto.ERPRequestLogEntitydto;
import com.ey.advisory.bcadmin.common.utility.BusinessAdminConstants;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.einv.app.api.APIIdentifiers;
import com.ey.advisory.repositories.client.LoggerAdviceRepository;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("DownloadNICPayloadsServiceImpl")
public class DownloadNICPayloadsServiceImpl
		implements DownloadNICPayloadsService {

	@Autowired
	LoggerAdviceRepository loggerAdviceRepository;

	@Autowired
	GetNicPayloadsDAO gettNicPayloadsDAO;

	@Autowired
	private Environment env;

	@Override
	public void downloadPayloads(String docNo, String sgstin, String irn,
			String apiType, HttpServletResponse response) {
		String fileName = null;
		try {

			List<ERPRequestLogEntitydto> erpRequestLogList = null;

			if (apiType.equals(BusinessAdminConstants.GEN_EWB_BY_IRN_API)) {
				fileName = irn;
				erpRequestLogList = gettNicPayloadsDAO.getPayloads(irn, " ",
						BusinessAdminConstants.GEN_EWB_BY_IRN_API, "generateEWBByIrn" );
			} else if (apiType.equals(BusinessAdminConstants.GEN_EINV_API)) {
				fileName = sgstin + "_" + docNo;
				erpRequestLogList = gettNicPayloadsDAO.getPayloads(docNo,
						sgstin, APIIdentifiers.GENERATE_EINV,
						JobConstants.EINVOICE_ASYNC);

			} else if (apiType.equals(BusinessAdminConstants.GEN_EWB_API)) {
				fileName = sgstin + "_" + docNo;
				erpRequestLogList = gettNicPayloadsDAO.getPayloads(docNo,
						sgstin,
						com.ey.advisory.ewb.app.api.APIIdentifiers.GENERATE_EWB,
						JobConstants.GENERATE_EWAYBILL);
			}

			if (erpRequestLogList == null || erpRequestLogList.isEmpty()) {
				String errMsg = "No Records Found";
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			}

			generateReports(erpRequestLogList, response, fileName);
		} catch (Exception e) {
			String msg = "Exception occured while downloading payloads";
			LOGGER.error(msg, e);

			throw new AppException(e.getMessage());
		}

	}

	private void generateReports(
			List<ERPRequestLogEntitydto> erpRequestLogdtoList,
			HttpServletResponse response, String fileName) {

		//Writer writer = null;
		File tempDir = null;
		
		try {
		    tempDir = createTempDir();

		    String fullPath = tempDir.getAbsolutePath() + File.separator + fileName + ".csv";

		    try (BufferedWriter writer = new BufferedWriter(new FileWriter(fullPath), 8192)) {
		        StatefulBeanToCsv<ERPRequestLogEntitydto> beanWriter = getBeanWriter(writer);
		        WritetoCsv(beanWriter, erpRequestLogdtoList);
		    }

		    File fileToDownload = new File(fullPath);

		    try (InputStream inputStream = new FileInputStream(fileToDownload)) {
		        response.setContentType("text/csv");
		        response.setHeader("Content-Disposition", String.format("attachment; filename=%s", fileName + ".csv"));
		        IOUtils.copy(inputStream, response.getOutputStream());
		        response.flushBuffer();
		    }

		} catch (Exception e) {
		    String msg = "Exception occurred while generating CSV file";
		    LOGGER.error(msg, e);
		    throw new AppException(msg, e);
		} finally {
		    if (tempDir != null && tempDir.exists()) {
		        try {
		            FileUtils.deleteDirectory(tempDir);
		            if (LOGGER.isDebugEnabled()) {
		                LOGGER.debug(String.format("Deleted the Temp directory/Folder '%s'", tempDir.getAbsolutePath()));
		            }
		        } catch (Exception ex) {
		            String msg = String.format(
		                    "Failed to remove the temp directory created for zip: '%s'. This will lead to clogging of disk space.",
		                    tempDir.getAbsolutePath());
		            LOGGER.error(msg, ex);
		        }
		    }
		}
/*		try {
			tempDir = createTempDir();

			String fullPath = tempDir.getAbsolutePath() + File.separator
					+ fileName + ".csv";

			writer = new BufferedWriter(new FileWriter(fullPath), 8192);

			StatefulBeanToCsv<ERPRequestLogEntitydto> beanWriter = getBeanWriter(
					writer);

			WritetoCsv(beanWriter, erpRequestLogdtoList);

			if (writer != null) {
				try {
					writer.flush();
					writer.close();
					if (LOGGER.isDebugEnabled()) {
						String msg = "Flushed writer successfully";
						LOGGER.debug(msg);
					}
				} catch (IOException e) {
					String msg = "Exception while closing the file writer";
					LOGGER.error(msg);
					throw new AppException(msg, e);
				}
			}

			File fileToDownload = new File(fullPath);
			InputStream inputStream = new FileInputStream(fileToDownload);

			response.setContentType("text/csv");
			response.setHeader("Content-Disposition", String
					.format("attachment; filename =%s ", fileName + ".csv"));
			IOUtils.copy(inputStream, response.getOutputStream());
			response.flushBuffer();

		} catch (Exception e) {

			String msg = "Exception occured while generating csv file";
			LOGGER.error(msg, e);
			throw new AppException(msg, e);

		} finally {

			if (tempDir != null && tempDir.exists()) {
				try {
					FileUtils.deleteDirectory(tempDir);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(String.format(
								"Deleted the Temp directory/Folder '%s'",
								tempDir.getAbsolutePath()));
					}
				} catch (Exception ex) {
					String msg = String.format(
							"Failed to remove the temp "
									+ "directory created for zip: '%s'. This will "
									+ "lead to clogging of disk space.",
							tempDir.getAbsolutePath());
					LOGGER.error(msg, ex);
				}
			}

		}*/

	}

	private void WritetoCsv(
			StatefulBeanToCsv<ERPRequestLogEntitydto> beanWriter,
			List<ERPRequestLogEntitydto> erpRequestLogdtoList) {

		try {

			if (erpRequestLogdtoList.size() > 0) {

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format("Record of DTO ",
							erpRequestLogdtoList.size());
					LOGGER.debug(msg);
				}

				if (erpRequestLogdtoList != null
						&& !erpRequestLogdtoList.isEmpty()) {
					beanWriter.write(erpRequestLogdtoList);

				}

			}

		} catch (Exception ex) {
			String msg = "Exception occured while Writing Data into CSV";
			LOGGER.error(msg);
			throw new AppException(msg, ex);
		}

	}

	private StatefulBeanToCsv<ERPRequestLogEntitydto> getBeanWriter(
			Writer writer) throws Exception {

		if (!env.containsProperty("nic.report.column.mapping")
				|| !env.containsProperty("nic.report.headers")) {
			String msg = "HeaderKey or ColumnMapping key Not found";
			LOGGER.error(msg);
			throw new AppException(msg);
		}

		ColumnPositionMappingStrategy<ERPRequestLogEntitydto> mappingStrategy = new ColumnPositionMappingStrategy<>();
		mappingStrategy.setType(ERPRequestLogEntitydto.class);
		mappingStrategy.setColumnMapping(
				env.getProperty("nic.report.column.mapping").split(","));

		StatefulBeanToCsvBuilder<ERPRequestLogEntitydto> builder = new StatefulBeanToCsvBuilder<>(
				writer);
		StatefulBeanToCsv<ERPRequestLogEntitydto> beanWriter = builder
				.withMappingStrategy(mappingStrategy)
				.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
				.withLineEnd(CSVWriter.DEFAULT_LINE_END)
				.withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER).build();

		writer.append(env.getProperty("nic.report.headers"));
		return beanWriter;
	}

	public static File createTempDir() throws IOException {
		return Files.createTempDirectory("DownloadReports").toFile();
	}
}
