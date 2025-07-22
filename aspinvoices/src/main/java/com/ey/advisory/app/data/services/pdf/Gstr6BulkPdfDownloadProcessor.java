package com.ey.advisory.app.data.services.pdf;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.services.search.filestatussearch.AsyncReportDownloadServiceGstr3b;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.CombineAndZipXlsxFiles;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Gstr2AProcessedRecordsReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

/**
 * @author Ravindra V S
 *
 */
@Slf4j
@Component("Gstr6BulkPdfDownloadProcessor")
public class Gstr6BulkPdfDownloadProcessor implements TaskProcessor {


	@Autowired
	FileStatusDownloadReportRepository downloadRepository;
	
	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	@Qualifier("AsyncGSTR3bPDFDownloadServiceImpl")
	AsyncReportDownloadServiceGstr3b asyncReportDownloadServiceGstr3b;
	
	@Autowired
	@Qualifier("Gstr6MultiplePdfZipReportService")
	Gstr6MultiplePdfZipReportService zipReportService;
	
	@Autowired
	@Qualifier("Gstr6AspSummaryPDFGenerationReportImpl")
	private Gstr6AspSummaryPDFGenerationReportImpl gstr6ReportImpl;
	
	@Override
	public void execute(Message message, AppExecContext context) {

		String jsonString = message.getParamsJson();
		JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
		Long id = json.get("id").getAsLong();
		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Inside Async Report Download processor with Report id : %d",
						id);
				LOGGER.debug(msg);
			}

			fileStatusDownloadReportRepo.updateStatus(id,
					ReportStatusConstants.REPORT_GENERATION_INPROGRESS, null,
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));

			try {

				if (LOGGER.isDebugEnabled()) {
					String msg = "Created temporary directory";
					LOGGER.debug(msg);
				}
				Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
				Optional<FileStatusDownloadReportEntity> optEntity = fileStatusDownloadReportRepo
						.findById(id);

				String reqPayload = optEntity.get().getReqPayload();
				
				if (LOGGER.isDebugEnabled()) {
					String msg = "Request Payload";
					LOGGER.debug(msg, reqPayload);
				}

				JsonObject obj = JsonParser.parseString(reqPayload)
						.getAsJsonObject();

				boolean isDigigst = obj.get("isDigigst").getAsBoolean();
				
				String taxPeriod = obj.get(APIConstants.TAXPERIOD).getAsString();
				
				Gstr2AProcessedRecordsReqDto reqDto = gson.fromJson(obj,
						Gstr2AProcessedRecordsReqDto.class);

				Gstr2AProcessedRecordsReqDto request = zipReportService
						.setDataSecuritySearchParams(reqDto);
				
				if (LOGGER.isDebugEnabled()) {
					String msg = "Request ";
					LOGGER.debug(msg, request);
				}
				
				List<String> gstinList = null;

				Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();

				gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);

				File folderToZip = null;
				File zipPath = null;
				try {
					
					folderToZip = createTempDir();
					for (String gstn : gstinList) {

						String fileName = "";
						if (isDigigst) {
							fileName = String.format("GSTR6_%s_%s_DigiGST", gstn,
									taxPeriod);
						} else {
							fileName = String.format("GSTR6_%s_%s_GSTN", gstn, taxPeriod);
						}

						String fullPath = folderToZip.getAbsolutePath()
								+ File.separator + fileName + ".pdf";
						BufferedOutputStream outStream = new BufferedOutputStream(
								new FileOutputStream(fullPath), 8192);
						
						if (LOGGER.isDebugEnabled()) {
							String msg = String.format(
									"inside service impl before calling to Gstr6BulkPdfDownloadProcessor this method    id : %d",
									id);
							LOGGER.debug(msg);
						}
						
						JasperPrint jprint = gstr6ReportImpl
								.generatePdfGstr6Report(request, gstn);
						if (LOGGER.isDebugEnabled()) {
							String msg = String.format(
									"inside service impl before  after calling to Gstr6BulkPdfDownloadProcessor this method ",
									id);
							LOGGER.debug(msg);
						}
						
						
						JasperExportManager.exportReportToPdfStream(jprint,
								outStream);
						outStream.flush();
						outStream.close();

					}

					String zipFileName = zipEinvoicePdfFiles(folderToZip, id);
					File zipFile = new File(folderToZip, zipFileName);
					
					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"inside Gstr6BulkPdfDownloadProcessor  before uploading file to the temp folder file path ");
						LOGGER.debug(msg);
					}

					/*String uploadedFileName = DocumentUtility.uploadZipFile(zipFile,
							"PdfGstr3bReports");*/
					
					Pair<String, String> uploadedFileName = DocumentUtility
							.uploadFile(zipFile, "PdfGstr3bReports");
					String uploadedDocName = uploadedFileName.getValue0();
					String docId = uploadedFileName.getValue1();
					
					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"inside Gstr6BulkPdfDownloadProcessor  after uploading file to the temp folder file path ");
						LOGGER.debug(msg);
					}

					downloadRepository.updateStatus(id, "REPORT_GENERATED",
							uploadedDocName, LocalDateTime.now(),docId);

				} catch (Exception ex) {
					String msg = "Error while creating Gstr6 Summary bulk report";
					LOGGER.error(msg, ex);
					throw new AppException(msg);

				} finally {
					if (folderToZip != null && folderToZip.exists()) {
						try {
							FileUtils.deleteDirectory(folderToZip);
							if (zipPath != null && zipPath.exists()) {
								FileUtils.forceDelete(zipPath);
							}
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug(String.format(
										"Deleted the Temp directory/Folder '%s'",
										folderToZip.getAbsolutePath()));
							}
						} catch (Exception ex) {
							String msg = String.format(
									"Failed to remove the temp "
											+ "directory created for zip: '%s'. This will "
											+ "lead to clogging of disk space.",
									folderToZip.getAbsolutePath());
							LOGGER.error(msg, ex);
						}
					}

				}

			} catch (Exception e) {
				fileStatusDownloadReportRepo.updateStatus(id,
						ReportStatusConstants.REPORT_GENERATION_FAILED, null,
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
				String msg = "Exception occued while while generating PDF file";
				LOGGER.error(msg, e);
				throw new AppException(msg, e);

			}
		} catch (Exception ex) {
			String msg = String.format(
					"Exception occured during report generation for gstr6");
			LOGGER.error(msg, ex);
			throw new AppException(ex,
					ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);

		}
	}
	
	private String zipEinvoicePdfFiles(File tempDir, Long id) throws Exception {

		List<String> filesToZip = getAllFilesToBeZipped(tempDir);
		String fileName = "Gstr6EntityLevelSummaryPDFReport_" + id;
		String compressedFileName = fileName;

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("compressedFileName : %s",
					compressedFileName);
			LOGGER.debug(msg);
		}

		CombineAndZipXlsxFiles.compressFiles(tempDir.getAbsolutePath(),
				compressedFileName + ".zip", filesToZip);
		String zipFileName = compressedFileName + ".zip";

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("zipFileName : %s", zipFileName);
			LOGGER.debug(msg);
		}
		return zipFileName;

	}

	private static List<String> getAllFilesToBeZipped(File tmpDir)
			throws Exception {

		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("Temporary directory containing files to "
							+ "be zipped: %s", tmpDir.getAbsolutePath());
			LOGGER.debug(msg);
		}
		FilenameFilter pdfFilter = new FilenameFilter() {
			public boolean accept(File tmpDir, String name) {
				return name.toLowerCase().endsWith(".pdf");
			}
		};
		File[] files = tmpDir.listFiles(pdfFilter);
		List<String> retFileNames = Arrays.stream(files)
				.map(f -> f.getAbsolutePath())
				.collect(Collectors.toCollection(ArrayList::new));

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("List of files to be zipped %s",
					retFileNames);
			LOGGER.debug(msg);
		}
		return retFileNames;
	}

	private static File createTempDir() throws IOException {
		return Files.createTempDirectory("PdfGstr3bReports").toFile();
	}

	
}
