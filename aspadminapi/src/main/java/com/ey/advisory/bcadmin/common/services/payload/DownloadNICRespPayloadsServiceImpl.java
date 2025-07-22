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
import org.springframework.stereotype.Component;

import com.ey.advisory.bcadmin.common.dao.GetNICRespPayloadDAO;
import com.ey.advisory.bcadmin.common.dto.ERPRequestLogEntitydto;
import com.ey.advisory.bcadmin.common.utility.BusinessAdminConstants;
import com.ey.advisory.common.AppException;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("DownloadNICRespPayloadsServiceImpl")
public class DownloadNICRespPayloadsServiceImpl
		implements DownloadNICRespPayloadsService {

	@Autowired
	GetNICRespPayloadDAO getNICRespPayloadDAO;

	@Override
	public void downloadRespPayloads(String docNo, String sgstin, String irn,
			String apiType, HttpServletResponse response) {

		String fileName = null;
		try {

			List<ERPRequestLogEntitydto> erpRequestLogList = null;

			if (apiType.equals(BusinessAdminConstants.GEN_EWB_BY_IRN_API)) {
				fileName = irn;
				erpRequestLogList = getNICRespPayloadDAO.getRespPayloads(irn,
						" ", BusinessAdminConstants.GEN_EWB_BY_IRN_API);
			} else if (apiType.equals(BusinessAdminConstants.GEN_EINV_API)) {
				fileName = sgstin + "_" + docNo;
				erpRequestLogList = getNICRespPayloadDAO.getRespPayloads(docNo,
						sgstin, BusinessAdminConstants.GEN_EINV_API);

			} else if (apiType.equals(BusinessAdminConstants.GEN_EWB_API)) {
				fileName = sgstin + "_" + docNo;
				erpRequestLogList = getNICRespPayloadDAO.getRespPayloads(docNo,
						sgstin, BusinessAdminConstants.GEN_EWB_API);

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
		
		File tempDir = null;
		try {
		    tempDir = createTempDir();
		    String fullPath = tempDir.getAbsolutePath() + File.separator + fileName + ".txt";

		    // Use try-with-resources for BufferedWriter
		    try (Writer writer = new BufferedWriter(new FileWriter(fullPath), 8192)) {
		        for (ERPRequestLogEntitydto erpRequestLogdto : erpRequestLogdtoList) {
		            writer.write(erpRequestLogdto.getNicResPayload());
		        }
		        writer.flush(); // Ensure data is flushed to the file
		        if (LOGGER.isDebugEnabled()) {
		            LOGGER.debug("Flushed writer successfully");
		        }
		    } catch (IOException e) {
		        String msg = "Exception while writing to the file";
		        LOGGER.error(msg, e);
		        throw new AppException(msg, e);
		    }

		    File fileToDownload = new File(fullPath);

		    // Use try-with-resources for FileInputStream
		    try (InputStream inputStream = new FileInputStream(fileToDownload)) {
		        response.setContentType("text/plain");
		        response.setHeader(
		                "Content-Disposition",
		                String.format("attachment; filename=%s", fileName + ".txt")
		        );
		        IOUtils.copy(inputStream, response.getOutputStream());
		        response.flushBuffer();
		    } catch (IOException e) {
		        String msg = "Exception occurred while generating txt file";
		        LOGGER.error(msg, e);
		        throw new AppException(msg, e);
		    }
		} catch (Exception e) {
		    String msg = "Exception occurred while processing the request";
		    LOGGER.error(msg, e);
		    throw new AppException(msg, e);
		} finally {
		    // Clean up temporary directory
		    if (tempDir != null && tempDir.exists()) {
		        try {
		            FileUtils.deleteDirectory(tempDir);
		            if (LOGGER.isDebugEnabled()) {
		                LOGGER.debug(String.format(
		                        "Deleted the Temp directory/Folder '%s'",
		                        tempDir.getAbsolutePath()
		                ));
		            }
		        } catch (Exception ex) {
		            String msg = String.format(
		                    "Failed to remove the temp directory '%s'. This may lead to disk space issues.",
		                    tempDir.getAbsolutePath()
		            );
		            LOGGER.error(msg, ex);
		        }
		    }
		}
		
		/*

		File tempDir = null;
		Writer writer = null;
		try {
			tempDir = createTempDir();

			String fullPath = tempDir.getAbsolutePath() + File.separator
					+ fileName + ".txt";

			writer = new BufferedWriter(new FileWriter(fullPath), 8192);

			for (ERPRequestLogEntitydto erpRequestLogdto : erpRequestLogdtoList) {
				writer.write(erpRequestLogdto.getNicResPayload());
			}

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

			response.setContentType("text/plain");
			response.setHeader("Content-Disposition", String
					.format("attachment; filename =%s ", fileName + ".txt"));
			IOUtils.copy(inputStream, response.getOutputStream());
			response.flushBuffer();

		} catch (IOException e) {

			String msg = "Exception occured while generating txt file";
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

		}

	*/}

	public static File createTempDir() throws IOException {
		return Files.createTempDirectory("DownloadReports").toFile();
	}

}
