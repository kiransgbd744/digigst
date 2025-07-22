package com.ey.advisory.app.services.search.filestatussearch;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.data.views.client.ComptReversalAmtDto;
import com.ey.advisory.app.data.views.client.ReversalComputeDto;
import com.ey.advisory.app.search.reports.BasicCommonSecParamRSReports;
import com.ey.advisory.app.services.reports.ItcReversalInwardDao;
import com.ey.advisory.app.services.reports.ReversalAmtItcUtil;
import com.ey.advisory.app.services.reports.ReversalComputeItcUtil;
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
@Component("AsyncCommonCreditDownloadSummaryServiceImpl")
@Slf4j
public class AsyncCommonCreditDownloadSummaryServiceImpl
		implements AsyncReportDownloadService {
	
	@Autowired
	private FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("ComputationofRevAmtDaoImpl")
	private ItcReversalInwardDao itcComptDao;

	@Autowired
	@Qualifier("ComptTurnoverReportDaoImpl")
	private ItcReversalInwardDao itcReversalComptDao;

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
					+ "Summary_ITC_Reversal_Rule_42_Report_" + entityName
					+ format.format(convertISDDate) + ".xlsx";

			Workbook workbook = commonUtility.createWorkbookWithExcelTemplate(
					"ReportTemplates", "CommonCredit_Summary.xlsx");

			List<Object> responseFromcompAmt = itcComptDao
					.getItcReports(request);
			List<Object> responseFromcomp = itcReversalComptDao
					.getItcReports(request);

			if (responseFromcomp == null && responseFromcompAmt ==null) {
				fileStatusDownloadReportRepo.updateStatus(id,
						ReportStatusConstants.NO_DATA_FOUND, null,
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()),null);
				return;
			}

			String taxPeriod = getTaxPeriod(request.getTaxperiod());

			processComptReversalAmt(workbook, responseFromcompAmt, entityName,
					taxPeriod);
			processReversalCompute(workbook, responseFromcomp, entityName,
					taxPeriod);

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

	private String getTaxPeriod(String fromTaxPeriod) {
		LocalDate startDate = LocalDate.of(
				Integer.parseInt(fromTaxPeriod.substring(2)),
				Integer.parseInt(fromTaxPeriod.substring(0, 2)), 01);
		return startDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.US)
				+ "-" + startDate.getYear();
	}

	private void processComptReversalAmt(Workbook workbook,
			List<Object> responseFromcompAmt, String entityName,
			String taxPeriod) {
		if (responseFromcompAmt != null && !responseFromcompAmt.isEmpty()) {
			List<ComptReversalAmtDto> computeDtoList = responseFromcompAmt
					.stream().map(dto -> (ComptReversalAmtDto) dto)
					.collect(Collectors.toList());
			ReversalAmtItcUtil.prepareDataForComputeAmtItcReport(workbook,
					computeDtoList, entityName, taxPeriod);
		}
	}

	private void processReversalCompute(Workbook workbook,
			List<Object> responseFromcomp, String entityName,
			String taxPeriod) {
		if (responseFromcomp != null && !responseFromcomp.isEmpty()) {
			List<ReversalComputeDto> computeDtoList = responseFromcomp.stream()
					.map(dto -> (ReversalComputeDto) dto)
					.collect(Collectors.toList());

			ReversalComputeItcUtil.prepareDataForComputeItcReport(workbook,
					computeDtoList, entityName, taxPeriod);

		}
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

		String fileName = "Summary_ITC_Reversal_Rule_42_Report_" + id;
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
