package com.ey.advisory.app.gstr1.einv;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.sql.Clob;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.data.entities.client.Gstr6ComputeCredDistDataEntity;
import com.ey.advisory.app.data.repositories.client.EinvReconReportDownloadRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1EInvReconConfigRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1PRvsSubReconGstinDetailsRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1SubmittedReconConfigRepository;
import com.ey.advisory.app.data.repositories.client.Gstr6ComputeCredDistDataRepository;
import com.ey.advisory.app.docs.dto.Anx1ReportSearchReqDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.search.reports.BasicCommonSecParamReports;
import com.ey.advisory.app.services.reports.AspProcessVsSubmitReportServiceImpl;
import com.ey.advisory.app.services.reports.Gstr6DeterminationReportHandler;
import com.ey.advisory.app.util.Anx1CsvDownloadUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CombineAndZipCsvFiles;
import com.ey.advisory.common.CombineAndZipXlsxFiles;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.ReconStatusConstants;
import com.ey.advisory.core.config.ConfigConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * @author kiran s
 *
 */
@Slf4j
@Component("Gstr6CompCredDistDataReportDetailsImpl")
public class Gstr6CompCredDistDataReportDetailsImpl {

	@Autowired
	@Qualifier("EinvReconReportDownloadRepository")
	EinvReconReportDownloadRepository einvchunkrepo;

	@Autowired
	@Qualifier("Gstr1EInvReconConfigRepository")
	Gstr1EInvReconConfigRepository configRepo;

	@Autowired
	@Qualifier("Gstr1EInvReconConfigRepository")
	Gstr1EInvReconConfigRepository gstr1InvReconConfigRepo;

	@Autowired
	@Qualifier("Gstr1SubmittedReconConfigRepository")
	Gstr1SubmittedReconConfigRepository gstr1PrSubmReconConfigRepo;

	@Autowired
	@Qualifier("Gstr1PRvsSubReconGstinDetailsRepository")
	Gstr1PRvsSubReconGstinDetailsRepository Gstr1GstinDetailRepo;

	@Autowired
	@Qualifier("AspProcessVsSubmitReportServiceImpl")
	AspProcessVsSubmitReportServiceImpl aspProcessVsSubmitReport;

	@Autowired
	CombineAndZipCsvFiles combineAndZipCsvFiles;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	Gstr6ComputeCredDistDataRepository gstr6ComputeCredDistDataRepository;

	@Autowired
	@Qualifier("BasicCommonSecParamReports")
	private BasicCommonSecParamReports basicCommonSecParamReports;

	@Autowired
	@Qualifier("Gstr6DeterminationReportHandler")
	private Gstr6DeterminationReportHandler gstr6DeterminationReportHandler;

	@Autowired
	private Gstr6ComputeCredDistDataRepository gstr6CompCredDistDataRepository;
	
	@Autowired
	@Qualifier("Anx1CsvDownloadUtil")
	private Anx1CsvDownloadUtil anx1CsvDownloadUtil;

	public void getGstr6CredDistReportData(Long requestId) throws IOException {
		File tempDir = null;
		try {
			String fileName = null;
			gstr6CompCredDistDataRepository.updateGstr6CredDistDataComp(
					ReconStatusConstants.REPORT_GENERATION_INPROGRESS, null,
					LocalDateTime.now(), requestId, null);
			Workbook workBook = null;
			Anx1ReportSearchReqDto criteria = new Anx1ReportSearchReqDto();
			Optional<Gstr6ComputeCredDistDataEntity> gstr6comData = gstr6ComputeCredDistDataRepository
					.findById(requestId);
			criteria.setEntityId(
					Arrays.asList(gstr6comData.get().getEntityId()));
			/*
			 * criteria.setReturnPeriod(GenUtil.
			 * convertDerivedTaxPeriodToTaxPeriod(Integer.parseInt(gstr6comData.
			 * get().getTaxPeriod())));
			 */
			criteria.setReturnPeriod(gstr6comData.get().getTaxPeriod());
			Clob gstins = gstr6comData.get().getGstins();
			String gstinsString = clobToString(gstins);
			String[] gstinArray = gstinsString.split(",");
			String taxPeriod = gstr6comData.get().getTaxPeriod();

			Map<String, List<String>> dataSecAttrs = new HashMap<>();
			dataSecAttrs.put("dataSecAttrs", new ArrayList<>());
			for (String gstin : gstinArray) {
				dataSecAttrs.get("dataSecAttrs").add(gstin);
			}
			criteria.setDataSecAttrs(dataSecAttrs);
			criteria.setDataType(GSTConstants.INWARD);
			Anx1ReportSearchReqDto dataSecSearcParam = basicCommonSecParamReports
					.setDataSecuritySearchParams(criteria);
			/**
			 * End - Set Data Security Attributes
			 */
			criteria.setType(DownloadReportsConstant.GSTR6_DETERMINATION);

			if (criteria.getType() != null
					&& criteria.getType().equalsIgnoreCase(
							DownloadReportsConstant.GSTR6_DETERMINATION)) {
				workBook = gstr6DeterminationReportHandler
						.findGstr6DeterminationReports(dataSecSearcParam);

			}

			String date = null;
			String time = null;
			String dateAndTime = null;
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			LocalDateTime istDateTimeFromUTC = EYDateUtil
					.toISTDateTimeFromUTC(now);
			DateTimeFormatter FOMATTER = DateTimeFormatter
					.ofPattern("dd-MM-yyyy");
			DateTimeFormatter FOMATTER1 = DateTimeFormatter
					.ofPattern("HH:mm:ss");
			DateTimeFormatter FOMATTER2 = DateTimeFormatter
					.ofPattern("dd-MM-yyyy HH:mm:ss");
			date = FOMATTER.format(istDateTimeFromUTC);
			time = FOMATTER1.format(istDateTimeFromUTC);
			dateAndTime = FOMATTER2.format(istDateTimeFromUTC);
			//File tempDir = null;
			tempDir = createTempDir();
			LocalDateTime utcDateTime = LocalDateTime.now();
			ZoneId istZone = ZoneId.of("Asia/Kolkata");
			LocalDateTime istDateTime = utcDateTime.atZone(ZoneId.of("UTC"))
					.withZoneSameInstant(istZone).toLocalDateTime();

			DateTimeFormatter dtf = DateTimeFormatter
					.ofPattern("yyyyMMddHHmmss");
			String timeMilli = dtf.format(istDateTime);
			fileName = tempDir.getAbsolutePath() + File.separator
					+ "Credit_Distribution_Data_RecordS"
					+ "_" + taxPeriod + "_" + timeMilli + ".xlsx";
			/*
			 * fileName = tempDir.getAbsolutePath() + File.separator +
			 * "Credit_Distribution_Data_RecordS" + "_" + taxPeriod +".xlsx";
			 */

			workBook.save(fileName);
			String zipFileName = zipEinvoicePdfFiles(tempDir, taxPeriod,
					timeMilli);
			if (LOGGER.isDebugEnabled()) {
				String str = String.format(
						"zipFile name before file upload is : %s",
						zipFileName);
				LOGGER.debug(str);
			}
			File zipFile = new File(tempDir, zipFileName);

			Pair<String, String> uploadedDocName = DocumentUtility
					.uploadFile(zipFile,
							ConfigConstants.GSTR6_CRED_REPORT_DOWNLOAD);
			String uploadedFileName = uploadedDocName.getValue0();
			String docId = uploadedDocName.getValue1();
			LOGGER.debug("Uploaded FileName: {}, DocId: {}", uploadedFileName,
					docId);

			if (docId != null) {

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Gstr6 report downloaded with docid and uploadedFileName ",
							docId, uploadedFileName);
				}

				gstr6CompCredDistDataRepository.updateGstr6CredDistDataComp(
						ReconStatusConstants.REPORT_GENERATED, null,
						LocalDateTime.now(), requestId, docId);
			}

		} catch (Exception ex) {
			String message = String.format(
					"Exception while generating report in getGstr6CredDistReportData ");
			LOGGER.error(message, ex);
			gstr6CompCredDistDataRepository.updateGstr6CredDistDataComp(
					ReconStatusConstants.REPORT_GENERATION_FAILED, null,
					LocalDateTime.now(), requestId, null);
			throw new AppException();
		}
		finally 
		{
			anx1CsvDownloadUtil.deleteTemporaryDirectory(tempDir);

		}
	}

	private String clobToString(Clob clob) throws SQLException {
		StringBuilder sb = new StringBuilder();
		int bufferSize = 1024;
		char[] buffer = new char[bufferSize];
		try (Reader reader = clob.getCharacterStream()) {
			int charsRead;
			while ((charsRead = reader.read(buffer, 0, bufferSize)) != -1) {
				sb.append(buffer, 0, charsRead);
			}
		} catch (IOException e) {
			// Handle IOException
			e.printStackTrace();
		}
		return sb.toString();
	}

	private static File createTempDir() throws IOException {
		return Files
				.createTempDirectory(ConfigConstants.GSTR6_CRED_REPORT_DOWNLOAD)
				.toFile();
	}

	private String zipEinvoicePdfFiles(File tempDir, String taxPeriod,
			String dateAndTime) {

		if (LOGGER.isDebugEnabled()) {
			String msg = " getting files to be zipped from temp directory";
			LOGGER.debug(msg);
		}
		String fileName = null;
		List<String> filesToZip = getAllFilesToBeZipped(tempDir);
		DateTimeFormatter format = DateTimeFormatter
				.ofPattern("yyyyMMddHHmmss");

		fileName = "Credit_Distribution_Data_RecordS"
				+ "_" + taxPeriod;

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

		if (LOGGER.isDebugEnabled()) {
			String msg = " zipping is successful";
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
