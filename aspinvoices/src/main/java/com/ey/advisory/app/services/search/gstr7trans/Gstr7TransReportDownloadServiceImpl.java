package com.ey.advisory.app.services.search.gstr7trans;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;



import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.services.reports.gstr7trans.Gstr7TransDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.ReportConvertor;
import com.ey.advisory.common.ReportStatusConstants;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("Gstr7TransReportDownloadServiceImpl")
public class Gstr7TransReportDownloadServiceImpl
		implements Gstr7TransReportDownloadService {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;
	
	@Autowired
	@Qualifier("Gstr7TransRSReportConvertor")
	ReportConvertor reportConvertor;

	private static final String OLDFARMATTER = "yyyy-MM-dd";
	private static final String NEWFARMATTER = "dd-MM-yyyy";
	private static final int CSV_BUFFER_SIZE = 8192;
	private static Integer chunkSize = 500;
	private static final String UPLOADED_FILENAME_MSG = "Uploaded file Name :'%s'";

	@Override
	public void generateReports(Long id, String reportType) {
		String fullPath = null;
		String uploadedDocName = null;
		Writer writer = null;

		File tempDir = null;
		try {
			tempDir = GenUtil.createTempDir();
		} catch (IOException e) {
			LOGGER.error("error while creating Temp Dir");
			throw new AppException();
		}

		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Get Error Report Details with batchId:'%s'", id);
				LOGGER.debug(msg);
			}
			String fileName = "";
			if (reportType
					.equalsIgnoreCase(DownloadReportsConstant.TOTALERECORDS)) {
				fileName = "GSTR7_Trans_Total_Records_Report";
			} else if (reportType
					.equalsIgnoreCase(DownloadReportsConstant.PROCESSED)) {
				fileName = "GSTR7_Trans_Processed_Records_Report";
			} else if (reportType
					.equalsIgnoreCase(DownloadReportsConstant.ERROR)) {
				fileName = "GSTR7_Trans_Error_Records_Report";
			} else {

			}

			fullPath = tempDir.getAbsolutePath() + File.separator + fileName
					+ ".csv";

			Integer noOfChunk = getChunkNo("USP_INS_CHUNK_GSTR7_DATA_STATUS",
					id, chunkSize);

			noOfChunk = noOfChunk != null ? noOfChunk.intValue() : 0;

			if (noOfChunk == 0) {

				String msg = String.format(
						"no.of " + "chunk count is zero batchId:'%s'", id);
				LOGGER.error(msg);

				fileStatusDownloadReportRepo.updateStatus(id,
						ReportStatusConstants.NO_DATA_FOUND, null,
						LocalDateTime.now());
				return;
			}
			writer = new BufferedWriter(new FileWriter(fullPath),
					CSV_BUFFER_SIZE);

			String invoiceHeadersTemplate = null;
			String[] columnMappings = null;

			if (reportType
					.equalsIgnoreCase(DownloadReportsConstant.TOTALERECORDS)) {
				invoiceHeadersTemplate = commonUtility
						.getProp("gstr7.trans.asp.total.report.headers");

				columnMappings = commonUtility
						.getProp("gstr7.trans.asp.total.report.columns")
						.split(",");
			}

			if (reportType
					.equalsIgnoreCase(DownloadReportsConstant.PROCESSED)) {
				invoiceHeadersTemplate = commonUtility
						.getProp("gstr7.trans.asp.process.report.headers");

				columnMappings = commonUtility
						.getProp("gstr7.trans.asp.process.report.columns")
						.split(",");
			}

			if (reportType.equalsIgnoreCase(DownloadReportsConstant.ERROR)) {
				invoiceHeadersTemplate = commonUtility
						.getProp("gstr7.trans.asp.error.report.headers");

				columnMappings = commonUtility
						.getProp("gstr7.trans.asp.error.report.columns")
						.split(",");
			}

			StatefulBeanToCsv<Gstr7TransDto> beanWriter = getBeanWriter(
					columnMappings, writer);

			writer.append(invoiceHeadersTemplate);

			WritetoCsv(noOfChunk, id, columnMappings, reportType, beanWriter,
					writer);

			if (writer != null) {
				try {
					writer.flush();
					writer.close();
					if (LOGGER.isDebugEnabled()) {
						String msg = "Flushed writer successfully";
						LOGGER.debug(msg);
					}
				} catch (IOException e) {
					String msg = "Exception while closing the file writer";
					LOGGER.error(msg);
					throw new AppException(msg, e);
				}
			}
			File fPathFile = new File(fullPath);

			Pair<String, String> uploadedZipName = DocumentUtility
					.uploadFile(fPathFile, "Anx1FileStatusReport");
			String docId = uploadedZipName.getValue1();
			uploadedDocName = uploadedZipName.getValue0();
			
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(UPLOADED_FILENAME_MSG,
						uploadedDocName);
				LOGGER.debug(msg);
			}
			
			if (LOGGER.isDebugEnabled()) {
				String msg = "Sucessfully uploaded zip file and updating the "
						+ "status as 'Report Generated'";
				LOGGER.debug(msg);
			}

			fileStatusDownloadReportRepo.updateStatus(id,
					ReportStatusConstants.REPORT_GENERATED, uploadedDocName,
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()),
					docId);
		} catch (Exception ex) {
			String errMsg = String
					.format("Error while creating Error report %s", id);
			fileStatusDownloadReportRepo.updateStatus(id,
					ReportStatusConstants.REPORT_GENERATION_FAILED, null,
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));

			LOGGER.error(errMsg, ex);
			throw new AppException(ex);

		} finally {
			GenUtil.deleteTempDir(tempDir);
		}
	}

	

	private Integer getChunkNo(String procName, Long downloadId,
			Integer chunkSize) {

		StoredProcedureQuery storedProc = entityManager
				.createStoredProcedureQuery(procName);

		storedProc.registerStoredProcedureParameter("P_REPORT_DOWNLOAD_ID",
				Long.class, ParameterMode.IN);

		storedProc.setParameter("P_REPORT_DOWNLOAD_ID", downloadId);

		storedProc.registerStoredProcedureParameter("P_CHUNK_SPILIT_VAL",
				Integer.class, ParameterMode.IN);

		storedProc.setParameter("P_CHUNK_SPILIT_VAL", chunkSize);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Executing chunking proc '%s' , downloadId '%d' ", procName,
					downloadId);
			LOGGER.debug(msg);
		}

		Integer chunkNum = (Integer) storedProc.getSingleResult();

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Executed chunking proc '%s', downloadId '%d', "
							+ "chunkCount '%d' ",
					procName, downloadId, chunkNum);
			LOGGER.debug(msg);
		}

		return chunkNum;
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

	private void WritetoCsv(Integer chunkNo, Long id, String[] columnMappings,
			String reportType, StatefulBeanToCsv<Gstr7TransDto> beanWriter,
			Writer writer) {

		try {
			for (int i = 1; i <= chunkNo; i++) {
				List<Object[]> records = getProcData(
						"USP_DISP_CHUNK_GSTR7_DATA_STATUS", id, i);

				if (records != null && !records.isEmpty()) {

					List<Gstr7TransDto> reconDataList = records.stream()
							.map(o -> (Gstr7TransDto) reportConvertor.convert(o, reportType))
							.collect(Collectors.toCollection(ArrayList::new));
					
					beanWriter = getBeanWriter(columnMappings, writer);
					beanWriter.write(reconDataList);
				}
			}
		} catch (Exception ex) {
			String msg = "Exception occured while fetching data from get ChunkData";
			LOGGER.error(msg);
			throw new AppException(msg, ex);
		}

	}
}