/**
 * 
 */
package com.ey.advisory.controllers.anexure1;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.docs.dto.Anx1FileStatusReportsReqDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.services.reports.Anx1FileStatusCsvReportHandler;
import com.ey.advisory.app.util.Anx1CsvDownloadUtil;
import com.ey.advisory.common.CombineAndZipXlsxFiles;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Laxmi.Salukuti
 *
 */
@RestController
public class Anx1FileStatusCsvDownloadReprotController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1FileStatusCsvDownloadReprotController.class);

	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfigPrmtRepository;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("Anx1FileStatusCsvReportHandler")
	private Anx1FileStatusCsvReportHandler anx1FileStatusCsvReportHandler;

	@Autowired
	CombineAndZipXlsxFiles combineAndZipXlsxFiles;

	@Autowired
	@Qualifier("Anx1CsvDownloadUtil")
	private Anx1CsvDownloadUtil anx1CsvDownloadUtil;

	@RequestMapping(value = "/ui/downloadFileStatusCsvReportsOld", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void downloadFileStatusCsvReport(@RequestBody String jsonString,
			HttpServletResponse response) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		String fileName = null;
		String fullPath = null;
		InputStream inputStream = null;
		String reportType = null;
		try {
			File tempDir = createTempDir();
			Anx1FileStatusReportsReqDto criteria = gson.fromJson(json,
					Anx1FileStatusReportsReqDto.class);
			Long fileId = criteria.getFileId();
			
			if (criteria.getType() != null && criteria.getType()
					.equalsIgnoreCase(DownloadReportsConstant.PROCESSED)) {
				if (criteria.getStatus() != null && criteria.getStatus()
						.equalsIgnoreCase(DownloadReportsConstant.ACTIVE)) {
					reportType = fileId + "_OutwardFileProcessedActiveRecords";
				} else if (criteria.getStatus() != null && criteria.getStatus()
						.equalsIgnoreCase(DownloadReportsConstant.INACTIVE)) {
					reportType = fileId + "_OutwardFileProcessedInactiveRecords";
				}
			}
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
			if (criteria.getType() != null && criteria.getType()
					.equalsIgnoreCase(DownloadReportsConstant.ERRORSV)) {
				reportType = fileId + "_OutwardSVErrorRecords";
			}
			if (criteria.getType() != null && criteria.getType()
					.equalsIgnoreCase(DownloadReportsConstant.ERRORTOTAL)) {
				reportType = fileId + "_OutwardTotalErrorRecords";
			}
			if (criteria.getType() != null && criteria.getType()
					.equalsIgnoreCase(DownloadReportsConstant.TOTALERECORDS)) {
				reportType = fileId + "_OutwardFileTotalRecords";
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
					anx1FileStatusCsvReportHandler
							.generateCsvForFileStatus(criteria, fullPath);
					if (criteria.getType() != null
							&& criteria.getType().equalsIgnoreCase(
									DownloadReportsConstant.PROCESSED)) {
						if (criteria.getStatus() != null
								&& criteria.getStatus().equalsIgnoreCase(
										DownloadReportsConstant.ACTIVE)) {
							fileName = "FileProcessedActiveRecordsReport";
						} else if (criteria.getStatus() != null
								&& criteria.getStatus().equalsIgnoreCase(
										DownloadReportsConstant.INACTIVE)) {
							fileName = "FileProcessedInactiveRecordsReport";
						}
					}
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
					if (criteria.getType() != null
							&& criteria.getType().equalsIgnoreCase(
									DownloadReportsConstant.ERRORSV)) {
						fileName = "FileSVErrorRecordsReport";
					}
					if (criteria.getType() != null
							&& criteria.getType().equalsIgnoreCase(
									DownloadReportsConstant.ERRORTOTAL)) {
						fileName = "FileErrorTotalRecordsReport";
					}
					if (criteria.getType() != null
							&& criteria.getType().equalsIgnoreCase(
									DownloadReportsConstant.TOTALERECORDS)) {
						fileName = "FileTotalRecordsReport";
					}
				}
			} else {
				LOGGER.error("invalid fileId");
				throw new Exception("invalid fileId");
			}

			String zipFileName = "";
			if (tempDir.list().length > 0) {
				Long randomLong = new Random().nextLong();
				zipFileName = combineAndZipXlsxFiles.zipfolder(randomLong,
						tempDir);
				File zipFile = new File(tempDir, zipFileName);
				int read = 0;
				byte[] bytes = new byte[1024];
				inputStream = FileUtils.openInputStream(zipFile);
				response.setHeader("Content-Disposition", String
						.format("attachment; filename=" + fileName + ".zip"));

				OutputStream outputStream = response.getOutputStream();
				while ((read = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}
				response.getOutputStream().flush();
			}
			anx1CsvDownloadUtil.deleteTemporaryDirectory(tempDir);

			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

		} catch (Exception ex) {
			String msg = "Unexpected error while retriving download report ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					// ADD LOGGER TODO
				}
			}
		}
	}

	private static File createTempDir() throws IOException {
		return Files.createTempDirectory("DownloadReports").toFile();
	}
}
