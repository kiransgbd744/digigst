package com.ey.advisory.app.services.search.filestatussearch;

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
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.data.services.pdf.Gstr1SummaryMultiPDFService;
import com.ey.advisory.app.data.services.pdf.Gstr3BSummaryPDFGenerationReportImpl;
import com.ey.advisory.app.services.search.simplified.docsummary.Gstr1SimpleDocGstnSummarySearchService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CombineAndZipXlsxFiles;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.ProcessedRecordsCommonSecParam;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.core.api.APIConstants;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

/**
 * @author kiran s
 *
 */
@Component("AsyncGSTR3bPDFDownloadServiceImpl")
@Slf4j
public class AsyncGSTR3bPDFDownloadServiceImpl
		implements AsyncReportDownloadServiceGstr3b {

	@Autowired
	private FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	@Qualifier("ProcessedRecordsCommonSecParam")
	private ProcessedRecordsCommonSecParam processedRecordsCommonSecParam;

	@Autowired
	@Qualifier("Gstr1SimpleDocGstnSummarySearchService")
	Gstr1SimpleDocGstnSummarySearchService tableSearchService;

	@Autowired
	@Qualifier("Gstr1SummaryMultiPDFService")
	Gstr1SummaryMultiPDFService gstr1SummaryMultiPDFService;


	/*@Autowired
	@Qualifier("Gstr3BSummaryPDFGenerationAsyncReportImpl")
	Gstr3BSummaryPDFGenerationAsyncReportImpl gstr3BSummaryPDFGenerationReport;
	*/
	@Autowired
	@Qualifier("Gstr3BSummaryPDFGenerationReportImpl")
	Gstr3BSummaryPDFGenerationReportImpl gstr3BSummaryPDFGenerationReport;

	@Autowired
	FileStatusDownloadReportRepository downloadRepository;

	@Override
	public String generateReports(Long id) {

		try {

			if (LOGGER.isDebugEnabled()) {
				String msg = "Created temporary directory";
				LOGGER.debug(msg);
			}

			Optional<FileStatusDownloadReportEntity> optEntity = fileStatusDownloadReportRepo
					.findById(id);

			String reqPayload = optEntity.get().getReqPayload();

			JsonObject obj = JsonParser.parseString(reqPayload)
					.getAsJsonObject();

			boolean isDigigst = obj.get("isDigigst").getAsBoolean();
			String isVerified = obj.get("isVerified").getAsString();
			JsonArray gstinArray = obj.get(APIConstants.GSTIN_LIST)
					.getAsJsonArray();
			String taxPeriod = obj.get(APIConstants.TAXPERIOD).getAsString();

			File folderToZip = null;
			File zipPath = null;
			try {
				
				folderToZip = createTempDir();
				for (JsonElement gstin : gstinArray) {

					String gstn = gstin.getAsString();
					String fileName = "Gstr3BEntityLevelSummaryPDFReport" + "_"
							+ gstn;

					String fullPath = folderToZip.getAbsolutePath()
							+ File.separator + fileName + ".pdf";
					BufferedOutputStream outStream = new BufferedOutputStream(
							new FileOutputStream(fullPath), 8192);
					
					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"inside service impl before calling to generateGstr3BSummaryPdfReport this method    id : %d",
								id);
						LOGGER.debug(msg);
					}
					

					JasperPrint jprint = gstr3BSummaryPDFGenerationReport
							.generateGstr3BSummaryPdfReport(gstn, taxPeriod,
									isDigigst, isVerified);
					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"inside service impl before  after calling to generateGstr3BSummaryPdfReport this method ",
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
							"inside AsyncGSTR3bPDFDownloadServiceImpl  before uploading file to the temp folder file path ");
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
							"inside AsyncGSTR3bPDFDownloadServiceImpl  after uploading file to the temp folder file path ");
					LOGGER.debug(msg);
				}

				downloadRepository.updateStatus(id, "REPORT_GENERATED",
						uploadedDocName, LocalDateTime.now(),docId);

				return "SUCCESS";
			} catch (Exception ex) {
				String msg = "Error while creating Gstr3B Summary bulk report";
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
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()),null);
			String msg = "Exception occued while while generating PDF file";
			LOGGER.error(msg, e);
			throw new AppException(msg, e);

		}
	}

	private String zipEinvoicePdfFiles(File tempDir, Long id) throws Exception {

		List<String> filesToZip = getAllFilesToBeZipped(tempDir);
		String fileName = "Gstr3BEntityLevelSummaryPDFReport_" + id;
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

	/*
	 * public static void main(String[] args) {
	 * 
	 * JasperPrint jprint =
	 * gstr3BSummaryPDFGenerationReport.generateGstr3BSummaryPdfReport(
	 * "29BSOPM2999H1ZT", "012022",false); System.out.println(jprint);
	 * 
	 * }
	 */
}
