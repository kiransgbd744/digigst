package com.ey.advisory.app.services.search.gstr7trans;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import org.apache.commons.io.FileUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.docs.dto.gstr8.Gstr8DownloadDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.search.reports.BasicCommonSecParamRSReports;
import com.ey.advisory.app.services.reports.gstr7trans.Gstr7TransDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CombineAndZipReportFiles;
import com.ey.advisory.common.CombineAndZipXlsxFiles;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.ProcessedRecordsCommonSecParam;
import com.ey.advisory.common.ReportConfig;
import com.ey.advisory.common.ReportConfigFactory;
import com.ey.advisory.common.ReportConvertor;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.core.config.ConfigConstants;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.dto.Gstr7TransReviwSummReportsReqDto;
import com.google.gson.Gson;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("Gstr7TransRSGstnErrorRptDwnldServiceImpl")
public class Gstr7TransRSGstnErrorRptDwnldServiceImpl
		implements Gstr7TransReportDownloadService {

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	private Environment env;

	@Autowired
	@Qualifier("ReportConfigFactoryImpl")
	ReportConfigFactory reportConfigFactory;

	@Autowired
	CombineAndZipReportFiles combineAndZipReportFiles;

	@Autowired
	@Qualifier("BasicCommonSecParamRSReports")
	BasicCommonSecParamRSReports basicCommonSecParamRSReports;

	@Autowired
	@Qualifier("ProcessedRecordsCommonSecParam")
	ProcessedRecordsCommonSecParam processedRecordsCommonSecParam;

	@Autowired
	@Qualifier("entityInfoRepository")
	EntityInfoRepository repo;
	
	@Autowired
	@Qualifier("Gstr7TransRSReportConvertor")
	ReportConvertor reportConvertor;

	private static final String CHUNK_PROC = "USP_INS_CHUNK_OD_PS_GSTR7_GSTN_ERROR_CONSOLIDATED";
	private static final String DISP_PROC = "USP_CHUNK_DISP_OD_PS_GSTR7_GSTN_ERROR_CONSOLIDATED";

	private static final int CSV_BUFFER_SIZE = 8192;
	private static Integer chunkSize = 10000;

	private static final String UPLOADED_FILENAME_MSG = "Uploaded file Name :'%s'";

	@Override
	public void generateReports(Long id, String orgReportType) {

		Writer writer = null;
		File tempDir = null;

		String fullPath = null;
		String uploadedDocName = null;
		String fileName = null;
		String invoiceHeadersTemplate = null;
		String[] columnMappings = null;
		List<String> gstins = null;

		try {
			tempDir = createTempDir(id);
			if (LOGGER.isDebugEnabled()) {
				String msg = "Created temporary directory";
				LOGGER.debug(msg);
			}
			Optional<FileStatusDownloadReportEntity> optEntity = fileStatusDownloadReportRepo
					.findById(id);
			FileStatusDownloadReportEntity entity = optEntity.get();
			
            String gstin = GenUtil.convertClobtoString(entity.getGstins());
			
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("gstin : %s",gstin);
				LOGGER.debug(msg);
			}
			gstins = Arrays.asList(gstin.split(","));
			String finalGstinString = String.join(",", gstins);
			
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("finalGstinString : %s",finalGstinString);
				LOGGER.debug(msg);
			}
			
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Fetched file entity based on report ID : %s",
						entity.toString());
				LOGGER.debug(msg);
			}
			String reportType = entity.getReportType();
			String reportCateg = entity.getReportCateg();
			String dataType = entity.getDataType();
			String status = entity.getStatus();
			String reqPayload = optEntity.get().getReqPayload();

			Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
			Gstr7TransReviwSummReportsReqDto criteria = gson.fromJson(
					reqPayload, Gstr7TransReviwSummReportsReqDto.class);

			Gstr7TransReviwSummReportsReqDto request = processedRecordsCommonSecParam
					.setGstr7TransReportDataSecuritySearchParams(criteria);

			processedRecordsCommonSecParam
					.setGstr7TransDataSecurityAttribute(request, entity);
			
			fileStatusDownloadReportRepo.save(entity);

			Integer chunkCount = getChunkCount(id);

			chunkCount = chunkCount != null ? chunkCount.intValue() : 0;

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Invoking ChuckCount StoreProc and got response as : %d",
						chunkCount);
				LOGGER.debug(msg);
			}

			if (chunkCount == 0) {
				fileStatusDownloadReportRepo.updateStatus(id,
						ReportStatusConstants.NO_DATA_FOUND, null,
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
				return;
			}

			String entityName = repo
					.findEntityNameByEntityId(criteria.getEntityId().get(0));

			LocalDateTime timeOfGeneration = LocalDateTime.now();
			LocalDateTime convertISDDate = EYDateUtil
					.toISTDateTimeFromUTC(timeOfGeneration);
			DateTimeFormatter format = DateTimeFormatter
					.ofPattern("yyyyMMddHHmmss");

			fileName = "GSTR7_" + finalGstinString
					+ "_ConsolidatedErrorRecords";
			invoiceHeadersTemplate = commonUtility
					.getProp("gstr7.trans.con.gstn.error.report.headers");

			columnMappings = commonUtility
					.getProp("gstr7.trans.con.gstn.error.report.columns")
					.split(",");

			fullPath = tempDir.getAbsolutePath() + File.separator + fileName
					+ entityName + format.format(convertISDDate) + ".csv";

			int j = 0;
			writer = new BufferedWriter(new FileWriter(fullPath),
					CSV_BUFFER_SIZE);

						StatefulBeanToCsv<Gstr7TransDto> beanWriter = getBeanWriter(
					columnMappings, writer);

			writer.append(invoiceHeadersTemplate);

			while (j < chunkCount) {
				j++;
				List<Object[]> records = getProcData(
						DISP_PROC, id, j);

				if (records != null && !records.isEmpty()) {

					List<Gstr7TransDto> reconDataList = records.stream()
							.map(o -> (Gstr7TransDto) reportConvertor.convert(o,
									reportType))
							.collect(Collectors.toCollection(ArrayList::new));

					beanWriter = getBeanWriter(columnMappings, writer);
					beanWriter.write(reconDataList);
					flushWriter(writer);
				}
			}

			String zipFileName = "";
			if (tempDir.list().length > 0) {

				if (LOGGER.isDebugEnabled()) {
					String msg = "Creting Zip folder";
					LOGGER.debug(msg);
				}
				zipFileName = combineAndZipReportFiles.zipfolder(tempDir,
						dataType + "_" + reportType, status, id);

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format("ZipFile name : %s",
							zipFileName);
					LOGGER.debug(msg);
				}

				File zipFile = new File(tempDir, zipFileName);

				/*
				 * String uploadedDocName =
				 * DocumentUtility.uploadZipFile(zipFile,
				 * ConfigConstants.COMMONCREDITDOWNLOAD);
				 */

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(UPLOADED_FILENAME_MSG,
							uploadedDocName);
					LOGGER.debug(msg);
				}

				Pair<String, String> uploadedZipName = DocumentUtility
						.uploadFile(zipFile,
								ConfigConstants.GSTR7_TRANS_RS_DOWNLOAD_REPORT);
				uploadedDocName = uploadedZipName.getValue0();
				String docId = uploadedZipName.getValue1();

				if (LOGGER.isDebugEnabled()) {
					String msg = "Sucessfully uploaded zip file and updating the "
							+ "status as 'Report Generated'";
					LOGGER.debug(msg);
				}

				fileStatusDownloadReportRepo.updateStatus(id,
						ReportStatusConstants.REPORT_GENERATED, uploadedDocName,
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()),
						docId);

			} else {

				LOGGER.error("No Data found for report id : %d", id);
				fileStatusDownloadReportRepo.updateStatus(id,
						ReportStatusConstants.NO_DATA_FOUND, null,
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
			}
		} catch (Exception e) {

			fileStatusDownloadReportRepo.updateStatus(id,
					ReportStatusConstants.REPORT_GENERATION_FAILED, null,
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
			String msg = "Exception occued while generating GSTR7 Processed File";
			LOGGER.error(msg, e);
			throw new AppException(msg, e);

		} finally {
			deleteTemporaryDirectory(tempDir);
		}
	}

	private void flushWriter(Writer writer) {
		if (writer != null) {
			try {
				writer.flush();
				if (LOGGER.isDebugEnabled()) {
					String msg = "Flushed writer " + "successfully";
					LOGGER.debug(msg);
				}
			} catch (IOException e) {
				String msg = "Exception while " + "closing the file writer";
				LOGGER.error(msg);
				throw new AppException(msg, e);
			}
		}

	}

	private static File createTempDir(Long batchId) throws IOException {

		String tempFolderPrefix = "Gstr7TransRSUpload" + "_" + batchId;
		return Files.createTempDirectory(tempFolderPrefix).toFile();
	}

	private void deleteTemporaryDirectory(File tempDir) {
		
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
	}}

	

	private StatefulBeanToCsv<Gstr7TransDto> getBeanWriter(
			String[] columnMappings, Writer writer) {
		ColumnPositionMappingStrategy<Gstr7TransDto> mappingStrategy = new ColumnPositionMappingStrategy<>();
		mappingStrategy.setType(Gstr7TransDto.class);
		mappingStrategy.setColumnMapping(columnMappings);
		StatefulBeanToCsvBuilder<Gstr7TransDto> builder = new StatefulBeanToCsvBuilder<>(
				writer);
		StatefulBeanToCsv<Gstr7TransDto> beanWriter = builder
				.withMappingStrategy(mappingStrategy)
				.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
				.withLineEnd(CSVWriter.DEFAULT_LINE_END)
				.withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER).build();
		return beanWriter;
	}

	private Integer getChunkCount(Long id) {
		StoredProcedureQuery storedProc = entityManager
				.createStoredProcedureQuery(CHUNK_PROC);

		storedProc.registerStoredProcedureParameter("P_REPORT_DOWNLOAD_ID",
				Long.class, ParameterMode.IN);
		storedProc.setParameter("P_REPORT_DOWNLOAD_ID", id);

		storedProc.registerStoredProcedureParameter("P_CHUNK_SPILIT_VAL",
				Integer.class, ParameterMode.IN);

		storedProc.setParameter("P_CHUNK_SPILIT_VAL", chunkSize);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Executing chunking proc"
							+ CHUNK_PROC+ " : '%s'",
					id.toString());
			LOGGER.debug(msg);
		}

		Integer chunks = (Integer) storedProc.getSingleResult();

		return chunks;
	}

	@SuppressWarnings("unchecked")
	private List<Object[]> getProcData(String procName, Long downloadId,
			Integer chunkValue) {

		StoredProcedureQuery storedProc = entityManager
				.createStoredProcedureQuery(procName);

		storedProc.registerStoredProcedureParameter("P_REPORT_DOWNLOAD_ID",
				Long.class, ParameterMode.IN);

		storedProc.setParameter("P_REPORT_DOWNLOAD_ID", downloadId);

		storedProc.registerStoredProcedureParameter("P_CHUNK_VALUE",
				Integer.class, ParameterMode.IN);
		storedProc.setParameter("P_CHUNK_VALUE", chunkValue);

		List<Object[]> resultList = storedProc.getResultList();

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Executing dispaly proc '%s' , downloadId '%d', "
							+ "Records count '%d' ",
					procName, downloadId, resultList.size());
			LOGGER.debug(msg);
		}

		return resultList;
	}
	
}