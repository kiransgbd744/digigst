package com.ey.advisory.processors.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.docs.dto.Anx1FileStatusReportsReqDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.services.reports.Anx1AsyncFileStatusCsvReportHandler;
import com.ey.advisory.app.util.Anx1CsvDownloadUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.CombineAndZipXlsxFiles;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun K.A
 *
 */
@Slf4j
@Component("Anx1AsyncReportDownloadProcessor")
public class Anx1AsyncReportDownloadProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfigPrmtRepository;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("Anx1AsyncFileStatusCsvReportHandler")
	private Anx1AsyncFileStatusCsvReportHandler anx1AsyncFileStatusCsvReportHandler;

	@Autowired
	CombineAndZipXlsxFiles combineAndZipXlsxFiles;

	@Autowired
	@Qualifier("Anx1CsvDownloadUtil")
	private Anx1CsvDownloadUtil anx1CsvDownloadUtil;

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Override
	public void execute(Message message, AppExecContext context) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		String fileName = null;
		String fullPath = null;
		InputStream inputStream = null;
		String reportType = null;
		FileStatusDownloadReportEntity entity = null;
		String jsonString = message.getParamsJson();
		JsonParser parser = new JsonParser();
		JsonObject json = (JsonObject) parser.parse(jsonString);

		Anx1FileStatusReportsReqDto criteria = gson.fromJson(json,
				Anx1FileStatusReportsReqDto.class);
		File tempDir = null;
		try {
			tempDir = createTempDir();
			Long fileId = criteria.getFileId();

			if (criteria.getType() != null && criteria.getType()
					.equalsIgnoreCase(DownloadReportsConstant.ERRORBV)) {
				if (criteria.getStatus() != null && criteria.getStatus()
						.equalsIgnoreCase(DownloadReportsConstant.ACTIVE)) {
					reportType = fileId + "_OutwardBVErrorActiveRecords";
				} else if (criteria.getStatus() != null && criteria.getStatus()
						.equalsIgnoreCase(DownloadReportsConstant.INACTIVE)) {
					reportType = fileId + "_OutwardBVErrorInactiveRecords";
				}
			}
			fullPath = tempDir.getAbsolutePath() + File.separator + reportType
					+ ".csv";

			if (null != criteria.getFileId()) {
				if ((criteria.getFileType() == null
						|| criteria.getFileType().isEmpty())
						&& (criteria.getDataType() == null
								|| criteria.getDataType().isEmpty())) {
					List<Object[]> objects = gstr1FileStatusRepository
							.getTypes(criteria.getFileId());

					objects.forEach(object -> {

						String filetype = (String) object[0];
						String datatype = (String) object[1];

						criteria.setFileType(filetype);
						criteria.setDataType(datatype);
					});
				}
				List<Object> answers = entityConfigPrmtRepository
						.findByQtnCode();
				if (answers != null && !answers.isEmpty()) {
					Integer answer = Integer
							.parseInt(String.valueOf(answers.get(0)));
					criteria.setAnswer(answer);
				}
			}
			if (criteria.getDataType() != null
					&& !criteria.getDataType().isEmpty()) {
				if ((criteria.getDataType() != null && criteria.getDataType()
						.equalsIgnoreCase(DownloadReportsConstant.OUTWARD))
						|| (criteria.getDataType() != null
								&& criteria.getDataType().equalsIgnoreCase(
										DownloadReportsConstant.GSTR1))) {
					entity = new FileStatusDownloadReportEntity();
					entity.setFileId(fileId);
					entity = fileStatusDownloadReportRepo.save(entity);
					anx1AsyncFileStatusCsvReportHandler
							.generateCsvForFileStatus(criteria,
									fullPath, entity.getId());
					if (criteria.getType() != null
							&& criteria.getType().equalsIgnoreCase(
									DownloadReportsConstant.ERRORBV)) {
						if (criteria.getStatus() != null
								&& criteria.getStatus().equalsIgnoreCase(
										DownloadReportsConstant.ACTIVE)) {
							fileName = "FileBVErrorActiveRecordsReport";
						} else if (criteria.getStatus() != null
								&& criteria.getStatus().equalsIgnoreCase(
										DownloadReportsConstant.INACTIVE)) {
							fileName = "FileBVErrorInactiveRecordsReport";
						}
					}
				}
			} else {
				LOGGER.error("invalid fileId");
				throw new AppException("invalid fileId");
			}

			String zipFileName = "";
			if (tempDir.list().length > 0 && entity != null) {
				Long randomLong = new Random().nextLong();
				zipFileName = combineAndZipXlsxFiles.zipfolder(randomLong,
						tempDir);
				File zipFile = new File(tempDir, zipFileName);

				String uploadedDocName = DocumentUtility.uploadZipFile(zipFile,
						"Anx1FileStatusReport");
				entity.setFilePath(uploadedDocName);
				fileStatusDownloadReportRepo.save(entity);
			}

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving download report ";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
					anx1CsvDownloadUtil.deleteTemporaryDirectory(tempDir);
				} catch (IOException e) {
					LOGGER.error("Excpetion while closing input stream", e);
				}
			}
		}
	}

	private static File createTempDir() throws IOException {
		return Files.createTempDirectory("DownloadReports").toFile();
	}

}
