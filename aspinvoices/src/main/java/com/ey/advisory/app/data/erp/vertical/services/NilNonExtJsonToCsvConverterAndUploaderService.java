package com.ey.advisory.app.data.erp.vertical.services;

import static com.ey.advisory.common.GSTConstants.FOLDER_NAME;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.docs.dto.NilNonExmptHeaderReqDto;
import com.ey.advisory.app.services.gstr1fileupload.Gstr1FileUploadJobInsertion;
import com.ey.advisory.app.services.gstr1fileupload.Gstr1FileUploadService;
import com.ey.advisory.app.util.Anx1CsvDownloadUtil;
import com.ey.advisory.app.util.Gstr1FileUploadUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CombineAndZipXlsxFiles;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.async.repositories.master.AsyncExecJobRepository;
import com.google.gson.JsonParseException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Nandam
 *
 */
@Slf4j
@Component("NilNonExtJsonToCsvConverterAndUploaderService")
public class NilNonExtJsonToCsvConverterAndUploaderService {

	@Autowired
	CombineAndZipXlsxFiles combineAndZipXlsxFiles;
	@Autowired
	@Qualifier("Anx1CsvDownloadUtil")
	private Anx1CsvDownloadUtil anx1CsvDownloadUtil;

	@Autowired
	@Qualifier("DefaultGstr1FileUploadService")
	Gstr1FileUploadService gstr1FileUploadService;

	@Autowired
	@Qualifier("Gstr1FileUploadUtil")
	private Gstr1FileUploadUtil gstr1FileUploadUtil;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("asyncExecJobRepository")
	private AsyncExecJobRepository asyncExecJobRepository;

	@Autowired
	@Qualifier("Gstr1FileUploadJobInsertion")
	private Gstr1FileUploadJobInsertion gstr1FileUploadJobInsertion;
	@Autowired
	@Qualifier("NilNonExtConvertDtoTocsv")
	private NilNonExtConvertDtoTocsv convertDtoTocsv;

	public String convertAndUpload(NilNonExmptHeaderReqDto dto,
			String cntrlPayloadId, String path) {
		File tempDir = null;
		String filename = null;
		try {
			String gstin = dto.getSgstin();
			String taxPeriod = dto.getReturnPeriod();

			filename = gstin + "_" + taxPeriod + "_" + cntrlPayloadId + ".csv";
			// create directory
			tempDir = createTempDir();
			String fullPath = tempDir.getAbsolutePath() + File.separator
					+ filename;
			// converting to dto
			boolean isFileCreated = convertDtoTocsv.convert(dto, fullPath,
					path);

			if (!isFileCreated) {
				String errMsg = String.format(
						"Failed to upload the csv file to folder %s", filename);
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			}
			User user = SecurityContext.getUser();
			String userName = user != null ? user.getUserPrincipalName()
					: "SYSTEM";
			String folderName = FOLDER_NAME;

			File csvDir = new File(fullPath);
			// Upload the CSV file to Doc Repo
			filename = DocumentUtility.uploadCsvFile(csvDir, folderName);

			if (filename == null) {
				String errMsg = String.format(
						"Failed to upload the csv file to folder %s",
						folderName);
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			}
			// Delete the temporary Directory

			String jsonParams = createJsonParams(filename, folderName);

			// file status entry
			if (LOGGER.isDebugEnabled()) {
				String errMsg = String.format("FileStatus entry starting",
						jsonParams);
				LOGGER.debug(errMsg);
			}
			Gstr1FileStatusEntity fileStatus = gstr1FileUploadUtil
					.getErpFileStatus(filename, userName, null,
							GSTConstants.NILNONEXMPT, GSTConstants.GSTR1, null);
			gstr1FileStatusRepository.save(fileStatus);
			if (LOGGER.isDebugEnabled()) {
				String errMsg = String.format("FileStatus entry ending",
						fileStatus);
				LOGGER.debug(errMsg);
			}

			if (LOGGER.isDebugEnabled()) {
				String errMsg = String.format("JobStatus entry starting");
				LOGGER.debug(errMsg);
			}
			// Job Status entry
			if ("/api/saveNilNonExt.do".equals(path)) {
				gstr1FileUploadJobInsertion.fileUploadJob(jsonParams,
						JobConstants.GSTR1NILOLDFILEUPLOAD, userName);
			} else {
				gstr1FileUploadJobInsertion.fileUploadJob(jsonParams,
						JobConstants.GSTR1NILFILEUPLOAD, userName);
			}
			if (LOGGER.isDebugEnabled()) {
				String errMsg = String.format("JobStatus entry ending");
				LOGGER.debug(errMsg);
			}

		} catch (JsonParseException ex) {
			String msg = "Failed to parse the json payload";
			LOGGER.error(msg, ex);
			throw new AppException(msg);

		} catch (Exception ex) {
			String msg = "Unexpected error while retriving download report ";
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		} finally {
			if (tempDir != null) {
				anx1CsvDownloadUtil.deleteTemporaryDirectory(tempDir);
			}
		}
		return filename;
	}

	private String createJsonParams(String fileName, String folderName) {

		String paramJson = "{\"filePath\":\"" + folderName + "\","
				+ "\"fileName\":\"" + fileName + "\"}";

		return paramJson;
	}

	private static File createTempDir() throws IOException {
		return Files.createTempDirectory("NIlNonExtFile").toFile();
	}

}
