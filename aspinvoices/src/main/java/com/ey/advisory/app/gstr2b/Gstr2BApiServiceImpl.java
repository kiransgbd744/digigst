package com.ey.advisory.app.gstr2b;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Clob;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.sql.rowset.serial.SerialClob;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.GetGstr2BManualApiStatusRepo;
import com.ey.advisory.app.util.CommonDocumentUtility;
import com.ey.advisory.app.util.MergeFilesUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.QRCodeValidatorConstants;
import com.ey.advisory.gstnapi.GstnApiWrapperConstants;
import com.ey.advisory.gstnapi.domain.client.APIResponseEntity;
import com.ey.advisory.gstnapi.repositories.client.APIResponseRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Reddy.
 *
 */

@Slf4j
@Component("Gstr2BApiServiceImpl")
public class Gstr2BApiServiceImpl {

	@Autowired
	@Qualifier("GSTR2BSingleResponseProcessor")
	private GSTR2BSingleResponseProcessor singleResponseProcessor;

	@Autowired
	@Qualifier("APIResponseRepository")
	private APIResponseRepository respRepo;
	
	@Autowired
	private GetGstr2BManualApiStatusRepo apiStatusRepo;

	public void parseandpersisResp(Long invocationId) {
		try {
			
			 Optional<Gstr2BManualApiStatus> manualApiStatus = apiStatusRepo.findByInvocationId(invocationId);
			String fileName = manualApiStatus.get().getFileName();
			String docId = manualApiStatus.get().getDocId();
			String gstin = manualApiStatus.get().getGstin();
			String taxPeriod = manualApiStatus.get().getTaxPeriod();
			File tempDir = MergeFilesUtil.createTempDir();
			File tempJsonDir = MergeFilesUtil.createTempDir();
			File downloadDir = CommonDocumentUtility.createDownloadDir(tempDir);

			File destFile = new File(
					downloadDir.getAbsolutePath() + File.separator + fileName);

			Document document = DocumentUtility.downloadDocumentByDocId(docId);
			if (document == null) {
				String msg = String.format(
						"Not Able to Download the Document for FileName %s in Folder %s with DocId %s",
						fileName, QRCodeValidatorConstants.ZIP_FOLDER, docId);
				LOGGER.error(msg);
				throw new AppException(msg);
			}
			FileUtils.copyInputStreamToFile(
					document.getContentStream().getStream(), destFile);

			List<APIResponseEntity> apiResponseList = new ArrayList<>();
			boolean isUnZipped = unzip(destFile.getAbsolutePath(), tempJsonDir);
			Map<String, APIResponseEntity> processedFilesData = new HashMap<>();

			if (isUnZipped) {
				File directoryPath = new File(tempJsonDir.getAbsolutePath());
				File filesList[] = directoryPath.listFiles();
				for (File file : filesList) {
					if (file.getName().endsWith(".json") && !processedFilesData
							.containsKey(file.getName())) {
						LOGGER.debug("Processing file: {}", file.getName());
						// Read and process the JSON file
						try {
							APIResponseEntity responseEntity = processJsonFile(
									file, invocationId);
							// Save the entity to the database
							apiResponseList.add(responseEntity);
							// Cache the processed data (to avoid re-reading the
							// file)
							processedFilesData.put(file.getName(),
									responseEntity);
						} catch (Exception e) {
							LOGGER.error("Error processing file: {}",
									file.getName(), e);
						}
					}
				}
			}
			if (!apiResponseList.isEmpty()) {
				respRepo.saveAll(apiResponseList);
			}

			List<Long> resultIds = respRepo
					.getResultIdByInvocationId(invocationId);

			singleResponseProcessor.processJsonResponse(gstin, taxPeriod,
					invocationId, resultIds, false);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	private APIResponseEntity processJsonFile(File file, Long invocationId)
			throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		// Assuming the structure of the JSON file maps to your
		// APIResponseEntity
		Map<String, Object> jsonMap = objectMapper.readValue(file, Map.class);

		// Prepare Clob (you may need to adjust this based on actual JSON
		// content)
		String jsonString = objectMapper.writeValueAsString(jsonMap);
		Clob responseClob = new SerialClob(jsonString.toCharArray());

		// Create and populate the APIResponseEntity object
		return new APIResponseEntity(invocationId, responseClob,
				GstnApiWrapperConstants.SUCCESS, "MANUAL_API",
				LocalDateTime.now(), "MANUAL_API", LocalDateTime.now());
	}

}