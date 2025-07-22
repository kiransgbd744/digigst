package com.ey.advisory.app.services.search.filestatussearch;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Cells;
import com.aspose.cells.Workbook;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.search.reports.BasicCommonSecParamRSReports;
import com.ey.advisory.app.services.reports.ItcNilNonDaoImpl;
import com.ey.advisory.app.services.reports.ItcReversalInwardDao;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AsyncReportDownloadService;
import com.ey.advisory.common.CombineAndZipXlsxFiles;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.core.config.ConfigConstants;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Reddy
 *
 */
@Component("AsyncCommonCreditDownloadVerticalServiceImpl")
@Slf4j
public class AsyncCommonCreditDownloadVerticalServiceImpl
		implements AsyncReportDownloadService {

	@Autowired
	private FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("ItcB2csReportDaoImpl")
	private ItcReversalInwardDao itcReversalb2CSDao;

	@Autowired
	@Qualifier("ItcNilNonDaoImpl")
	private ItcNilNonDaoImpl itcReversalnilDao;

	@Autowired
	@Qualifier("BasicCommonSecParamRSReports")
	BasicCommonSecParamRSReports basicCommonSecParamRSReports;

	@Autowired
	@Qualifier("entityInfoRepository")
	EntityInfoRepository repo;

	private static final String UPLOADED_FILENAME_MSG = "Uploaded file Name :'%s'";

	@Override
	public void generateReports(Long id) {
		File tempDir = null;

		int startRow = 2;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		try {
			tempDir = createTempDir();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Created temporary directory");
			}

			Optional<FileStatusDownloadReportEntity> optEntity = fileStatusDownloadReportRepo
					.findById(id);
			String reqPayload = optEntity.get().getReqPayload();

			Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
			Gstr1ReviwSummReportsReqDto criteria = gson.fromJson(reqPayload,
					Gstr1ReviwSummReportsReqDto.class);
			Gstr1ReviwSummReportsReqDto request = basicCommonSecParamRSReports
					.setDataSecuritySearchParams(criteria);

			String entityName = repo
					.findEntityNameByEntityId(request.getEntityId().get(0));

			LocalDateTime timeOfGeneration = LocalDateTime.now();
			LocalDateTime convertISDDate = EYDateUtil
					.toISTDateTimeFromUTC(timeOfGeneration);
			DateTimeFormatter format = DateTimeFormatter
					.ofPattern("yyyyMMddHHmmss");
			String fileName = tempDir.getAbsolutePath() + File.separator
					+ "Vertical_ITC_Reversal_Rule_42_Report_" + entityName
					+ format.format(convertISDDate) + ".xlsx";

			Workbook workbook = commonUtility.createWorkbookWithExcelTemplate(
					"ReportTemplates", "CommonCredit_Vertical.xlsx");

			List<Object> responseFromB2CS = new ArrayList<>();
			responseFromB2CS = itcReversalb2CSDao.getItcReports(request);

			List<Object> responseFromNil = new ArrayList<>();
			responseFromNil = itcReversalnilDao.getItcReports(request);
			
			if(responseFromB2CS.isEmpty() && responseFromNil.isEmpty()){
				fileStatusDownloadReportRepo.updateStatus(id,
						ReportStatusConstants.NO_DATA_FOUND, null,
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()),null);
				return;
			}

			if (!responseFromB2CS.isEmpty()) {
				String[] invoiceHeaders = commonUtility
						.getProp("reversal.B2CS.api.report.columns").split(",");

				Cells errorDumpCells = workbook.getWorksheets().get(0)
						.getCells();
				errorDumpCells.importCustomObjects(responseFromB2CS,
						invoiceHeaders, isHeaderRequired, startRow, startcolumn,
						responseFromB2CS.size(), true, "yyyy-mm-dd", false);
			}

			if (!responseFromNil.isEmpty()) {
				String[] invoiceHeaders = commonUtility
						.getProp("gstr1.Asp.Nil.itc.report.headers").split(",");

				Cells errorDumpCells = workbook.getWorksheets().get(1)
						.getCells();
				errorDumpCells.importCustomObjects(responseFromNil,
						invoiceHeaders, isHeaderRequired, startRow, startcolumn,
						responseFromNil.size(), true, "yyyy-mm-dd", false);
			}

			workbook.save(fileName);

			String zipFileName = zipEinvoicePdfFiles(tempDir, entityName, id);
			File zipFile = new File(tempDir, zipFileName);

			/*String uploadedFileName = DocumentUtility.uploadZipFile(zipFile,
					ConfigConstants.COMMONCREDITDOWNLOAD);*/
			Pair<String, String> uploadedDocName = DocumentUtility
					.uploadFile(zipFile, ConfigConstants.COMMONCREDITDOWNLOAD);
			String uploadedFileName = uploadedDocName.getValue0();
			String docId = uploadedDocName.getValue1();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						String.format(UPLOADED_FILENAME_MSG, uploadedFileName));
			}

			fileStatusDownloadReportRepo.updateStatus(id,
					ReportStatusConstants.REPORT_GENERATED, uploadedFileName,
					LocalDateTime.now(),docId);
		} catch (Exception e) {
			handleReportGenerationFailure(id, e);
		} finally {
			GenUtil.deleteTempDir(tempDir);
		}
	}

	private static File createTempDir() throws IOException {
		return Files.createTempDirectory(ConfigConstants.COMMONCREDITDOWNLOAD)
				.toFile();
	}

	private void handleReportGenerationFailure(Long id, Exception e) {
		fileStatusDownloadReportRepo.updateStatus(id,
				ReportStatusConstants.REPORT_GENERATION_FAILED, null,
				EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()),null);
		String msg = "Exception occurred while generating PDF file";
		LOGGER.error(msg, e);
		throw new AppException(msg, e);
	}

	private String zipEinvoicePdfFiles(File tempDir, String entityName,
			Long id) {

		if (LOGGER.isDebugEnabled()) {
			String msg = " getting files to be zipped from temp directory";
			LOGGER.debug(msg);
		}

		List<String> filesToZip = getAllFilesToBeZipped(tempDir);

		String fileName = "Vertical_ITC_Reversal_Rule_42_Report_" + id;
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

	private static List<String> getAllFilesToBeZipped(File tmpDir) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("Temporary directory containing files to "
							+ "be zipped: %s", tmpDir.getAbsolutePath());
			LOGGER.debug(msg);
		}
		FilenameFilter pdfFilter = new FilenameFilter() {
			public boolean accept(File tmpDir, String name) {
				return name.toLowerCase().endsWith(".xlsx");
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
}
