package com.ey.advisory.app.get.notices.handlers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.sql.Clob;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.asprecon.gstr2.initiaterecon.Gstr2InitiateReconReportDto;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AsyncReportDownloadService;
import com.ey.advisory.common.CombineAndZipCsvFiles;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.ReportConfigFactory;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.core.async.domain.master.GstnNoticeAlertCodeEntity;
import com.ey.advisory.core.async.domain.master.GstnNoticeModuleCodeEntity;
import com.ey.advisory.core.async.repositories.master.GstnNoticeAlertCodeRepo;
import com.ey.advisory.core.async.repositories.master.GstnNoticeModuleCodeRepo;
import com.ey.advisory.core.config.ConfigConstants;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author ashutosh.kar
 *
 */
@Component("GstnNoticeDetailedReportDwldServiceImpl")
@Slf4j
public class GstnNoticeDetailedReportDwldServiceImpl
		implements AsyncReportDownloadService {

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("ReportConfigFactoryImpl")
	ReportConfigFactory reportConfigFactory;

	@Autowired
	@Qualifier("GstnNoticeModuleCodeRepo")
	GstnNoticeModuleCodeRepo moduleRepo;

	@Autowired
	@Qualifier("GstnNoticeAlertCodeRepo")
	GstnNoticeAlertCodeRepo alterRepo;

	@Autowired
	CombineAndZipCsvFiles combineAndZipCsvFiles;

	private static int CSV_BUFFER_SIZE = 8192;

	@Override
	public void generateReports(Long id) {

		Writer writer = null;
		File tempDir = null;

		try {

			tempDir = createTempDir();

			if (LOGGER.isDebugEnabled()) {
				String msg = "Created temporary directory";
				LOGGER.debug(msg);
			}

			Optional<FileStatusDownloadReportEntity> optEntity = fileStatusDownloadReportRepo
					.findById(id);

			FileStatusDownloadReportEntity entity = optEntity.get();

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Fetched file entity based on report ID : %s",
						entity.toString());
				LOGGER.debug(msg);
			}

			String reportType = entity.getReportType();

			DateTimeFormatter dtf = DateTimeFormatter
					.ofPattern("yyyyMMddHHmmss");
			String timeMilli = dtf.format(LocalDateTime.now());
			String fullPath = null;

			fullPath = tempDir.getAbsolutePath() + File.separator
					+ "GSTNNotices" + "_" + "DetailedDownloadReport" + "_"
					+ timeMilli + ".csv";

			writer = new BufferedWriter(new FileWriter(fullPath),
					CSV_BUFFER_SIZE);
			String invoiceHeadersTemplate = commonUtility
					.getProp("gstn.notice.detailed.report.headers");
			writer.append(invoiceHeadersTemplate);

			String[] columnMappings = commonUtility
					.getProp("gstn.notice.detailed.report.mappings").split(",");

			ColumnPositionMappingStrategy<GstnNoticeDetailReportRespDto> mappingStrategy = new ColumnPositionMappingStrategy<>();
			mappingStrategy.setType(GstnNoticeDetailReportRespDto.class);
			mappingStrategy.setColumnMapping(columnMappings);

			StatefulBeanToCsvBuilder<GstnNoticeDetailReportRespDto> builder = new StatefulBeanToCsvBuilder<>(
					writer);
			StatefulBeanToCsv<GstnNoticeDetailReportRespDto> beanWriter = builder
					.withMappingStrategy(mappingStrategy)
					.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
					.withLineEnd(CSVWriter.DEFAULT_LINE_END)
					.withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER).build();
			long generationStTime = System.currentTimeMillis();

			@SuppressWarnings("unchecked")
			List<Object[]> records = getGstnDetailedReportDataFromQuery(entity);
			if (records != null && !records.isEmpty()) {
				Map<String, String> moduleAlertMap = new HashMap<>();
				List<GstnNoticeModuleCodeEntity> moduleList = moduleRepo
						.findByIsActiveTrue();
				List<GstnNoticeAlertCodeEntity> alertList = alterRepo
						.findByIsActiveTrue();

				moduleAlertMap.putAll(moduleList.stream()
						.collect(Collectors.toMap(
								m -> m.getModuleCode().toString(),
								m -> m.getDesc().toString())));

				moduleAlertMap.putAll(alertList.stream()
						.collect(Collectors.toMap(
								m -> m.getAlertCode().toString(),
								m -> m.getDesc().toString())));
				
				LOGGER.debug(" moduleAlertMap {} ", moduleAlertMap);
				
				List<GstnNoticeDetailReportRespDto> reconDataList = records
						.stream().map(o -> convert(o, moduleAlertMap))
						.collect(Collectors.toCollection(ArrayList::new));
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Report Name and row count: '%s' - '%s'",
							reportType, reconDataList.size());
					LOGGER.debug(msg);
				}
				beanWriter.write(reconDataList);

			} else {
				LOGGER.error("No Data found for report id : %d", id);
				fileStatusDownloadReportRepo.updateStatus(id,
						ReportStatusConstants.NO_DATA_FOUND, null,
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
				return;
			}
			flushWriter(writer);

			// String zipFileName = "";
			if (tempDir.list().length > 0) {

				String zipFileName = combineAndZipCsvFiles.zipfolder(id,
						tempDir, reportType);

				File zipFile = new File(tempDir, zipFileName);
				Pair<String, String> uploadedZipName = DocumentUtility
						.uploadFile(zipFile,
								ConfigConstants.GSTN_NOTICE_DETAIL_REPORT);
				String uploadedDocName = uploadedZipName.getValue0();
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
			}
		} catch (Exception e) {

			fileStatusDownloadReportRepo.updateStatus(id,
					ReportStatusConstants.REPORT_GENERATION_FAILED, null,
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
			String msg = "Exception occued while while generating csv file";
			LOGGER.error(msg, e);
			throw new AppException(msg, e);

		} finally {

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
			}

		}

	}

	private static File createTempDir() throws IOException {
		return Files.createTempDirectory("DownloadReports").toFile();
	}

	public List<Object[]> getGstnDetailedReportDataFromQuery(
			FileStatusDownloadReportEntity request) {

		StringBuilder s = new StringBuilder();
		try {

			List<String> gstinList = clobToList(request.getGstins());

			if (!gstinList.isEmpty()) {
				LOGGER.debug(" ");
				s.append(" AND N.GSTIN IN (:gstin) ");
			}

			if ("TaxPeriod wise".equalsIgnoreCase(request.getCriteria()))

			{
				s.append(
						" AND N.DERIVED_FROM_TAX_PERIOD >=:derivedFromTaxPeriod ");
				s.append(" AND N.DERIVED_TO_TAX_PERIOD <=:derivedToTaxPeriod");
			} else {
				s.append(
						" AND N.DATE_OF_ISSUE BETWEEN (:dateFrom) AND (:dateTo) ");

			}

			String querySummStr = createGstnNoticeDetailReportQuery(
					s.toString());
			Query q = entityManager.createNativeQuery(querySummStr);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Reading query inwardeinvoicescreen" + querySummStr);
			}
			if (gstinList != null && !gstinList.isEmpty()) {
				LOGGER.debug("gstinList {} ", gstinList);
				q.setParameter("gstin", gstinList);
			}

			if (request.getEntityId() != null) {
				LOGGER.debug("request.getEntityId() {} ",
						request.getEntityId());
				q.setParameter("entityId", request.getEntityId());
			}

			if (request.getCriteria() != null) {
				if ("TaxPeriod wise".equalsIgnoreCase(request.getCriteria())) {

					if (request.getDerivedRetPeriodFrom() != null) {
						q.setParameter("derivedFromTaxPeriod",
								request.getDerivedRetPeriodFrom());
					}
					if (request.getDerivedRetPeriodFromTo() != null) {
						q.setParameter("derivedToTaxPeriod",
								request.getDerivedRetPeriodFromTo());
					}

					LOGGER.debug(
							"derivedFromTaxPeriod {}, derivedToTaxPeriod{} ",
							request.getDerivedRetPeriodFrom(),
							request.getDerivedRetPeriodFromTo());

				} else {
					if (request.getDocDateFrom() != null) {
						q.setParameter("dateFrom", request.getDocDateFrom());
					}
					if (request.getDocDateTo() != null) {
						q.setParameter("dateTo", request.getDocDateTo());
					}

					LOGGER.debug("dateFrom {}, dateTo{} ",
							request.getDocDateFrom(), request.getDocDateTo());

				}
			}
			List<Object[]> list = q.getResultList();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Reading Resultset for einvoiceinward {} " + list);
			}

			return list;
		} catch (Exception ex) {
			LOGGER.error("exception in json download dao impl {} ", ex);
			throw new AppException(ex);
		}

	}

	private String createGstnNoticeDetailReportQuery(String buildSummQuery) {

		return " SELECT (SELECT ENTITY_NAME FROM ENTITY_INFO WHERE ID =:entityId  ) "
				+ " AS ENTITY_NAME, N.GSTIN, N.REFERENCE_ID AS REFERENCE_ID, N.ARN AS ACKNOWLEDGEMENT_NUMBER, "
				+ " N.DATE_OF_ISSUE AS DATE_OF_ISSUE, N.DUE_DATE_OF_REPLY AS DUE_DATE_OF_REPLY, N.DATE_OF_RESPONSE AS DATE_OF_RESPONSE, "
				+ " CASE "
				+ "     WHEN N.DATE_OF_RESPONSE IS NOT NULL THEN 'Responded'"
				+ "     WHEN N.DUE_DATE_OF_REPLY >= CURRENT_DATE THEN 'Pending'"
				+ "     WHEN N.DUE_DATE_OF_REPLY < CURRENT_DATE THEN 'Overdue'"
				+ " END AS NOTICE_STATUS," + " CASE "
				+ "     WHEN N.DATE_OF_RESPONSE IS NOT NULL THEN ''"
				+ "     WHEN TO_DATE(N.DUE_DATE_OF_REPLY, 'YYYY-MM-DD') >= CURRENT_DATE THEN "
				+ "         'Due in '||ABS(DAYS_BETWEEN(TO_DATE(N.DUE_DATE_OF_REPLY, 'YYYY-MM-DD'), CURRENT_DATE ))||' No. of Days'  "
				+ "     WHEN TO_DATE(N.DUE_DATE_OF_REPLY, 'YYYY-MM-DD') < CURRENT_DATE THEN "
				+ "         'Over Due by '||ABS(DAYS_BETWEEN(TO_DATE(N.DUE_DATE_OF_REPLY, 'YYYY-MM-DD'), CURRENT_DATE))||' No. of Days' "
				+ " END AS TIME_LEFT_FOR_RESPONSE,"
				+ " N.DERIVED_FROM_TAX_PERIOD AS FROM_TAX_PERIOD, N.DERIVED_TO_TAX_PERIOD AS TO_TAX_PERIOD, N.NOTICE_TYPE, "
				+ " NULL AS NOTICE_TYPE_DESCRIPTION, "
				+ " NULL AS TAXABLE_VALUE, " + " NULL AS TOTAL_TAX, "
				+ " N.MODULE_CODE AS UNIQUE_MODULE_CODE, NULL AS UNIQUE_MODULE_DESCRIPTION, N.ALERT_CODE AS UNIQUE_ALERT_CODE, NULL AS UNIQUE_ALERT_DESCRIPTION, "
				+ " ND.DOC_TYPE AS MAIN_DOCUMENT_TYPE, ND.DOC_ID AS MAIN_DOCUMENT_ID, ND.HASH AS MAIN_DOCUMENT_HASH_CODE, "
				+ " NULL AS SUPPORTING_DOCUMENT_TYPE, "
				+ " NULL AS SUPPORTING_DOCUMENT_ID, "
				+ " NULL AS SUPPORTING_DOCUMENT_HASH_CODE"
				+ " FROM TBL_NOTICE_DETAILS N "
				+ " INNER JOIN TBL_NOTICE_DOCUMENT_DETAILS ND ON ND.ND_ID = N.ID"
				+ " WHERE ND.IS_MAINDOC = True " + buildSummQuery;

	}

	private StatefulBeanToCsv<Gstr2InitiateReconReportDto> getBeanWriter(
			String[] columnMappings, Writer writer) {
		ColumnPositionMappingStrategy<Gstr2InitiateReconReportDto> mappingStrategy = new ColumnPositionMappingStrategy<>();
		mappingStrategy.setType(Gstr2InitiateReconReportDto.class);
		mappingStrategy.setColumnMapping(columnMappings);
		StatefulBeanToCsvBuilder<Gstr2InitiateReconReportDto> builder = new StatefulBeanToCsvBuilder<>(
				writer);
		StatefulBeanToCsv<Gstr2InitiateReconReportDto> beanWriter = builder
				.withMappingStrategy(mappingStrategy)
				.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
				.withLineEnd(CSVWriter.DEFAULT_LINE_END)
				.withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER).build();
		return beanWriter;
	}

	private void flushWriter(Writer writer) {
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
	}

	private GstnNoticeDetailReportRespDto convert(Object[] arr,
			Map<String, String> moduleAlertMap) {

		GstnNoticeDetailReportRespDto obj = new GstnNoticeDetailReportRespDto();
		obj.setEntityName((arr[0] != null) ? arr[0].toString() : null);
		obj.setGstin((arr[1] != null) ? arr[1].toString() : null);
		obj.setReferenceId((arr[2] != null) ? arr[2].toString() : null);
		obj.setAckNo((arr[3] != null) ? arr[3].toString() : null);
		obj.setDateOfIssue((arr[4] != null) ? arr[4].toString() : null);
		obj.setDueDate((arr[5] != null) ? arr[5].toString() : null);
		obj.setDateOfResponse((arr[6] != null) ? arr[6].toString() : null);
		obj.setNoticeStatus((arr[7] != null) ? arr[7].toString() : null);
		obj.setTimeLeftForResponse((arr[8] != null) ? arr[8].toString() : null);
		Pair<String, String> fromToPair = formatTaxPeriodRange(
				Integer.valueOf(arr[9].toString()),
				Integer.valueOf(arr[10].toString()));

		obj.setFromTaxPeriod(fromToPair.getValue0());
		obj.setToTaxPeriod(fromToPair.getValue1());
		obj.setNoticeType((arr[11] != null) ? arr[11].toString() : null);
		obj.setNoticeTypeDescription(
				moduleAlertMap.containsKey(obj.getNoticeType())
						? moduleAlertMap.get(obj.getNoticeType()) : null);

		obj.setTaxableValue((arr[13] != null) ? arr[13].toString() : null);
		obj.setTotalTax((arr[14] != null) ? arr[14].toString() : null);
		obj.setUniqueModuleCode((arr[15] != null) ? arr[15].toString() : null);
		obj.setUniqueModuleDesc(
				moduleAlertMap.containsKey(obj.getUniqueModuleCode())
						? moduleAlertMap.get(obj.getUniqueModuleCode()) : null);
		obj.setUniqueAlertCode((arr[17] != null) ? arr[17].toString() : null);
		obj.setUniqueAlertDesc(
				moduleAlertMap.containsKey(obj.getUniqueAlertCode())
						? moduleAlertMap.get(obj.getUniqueAlertCode()) : null);
		obj.setMainDocType((arr[19] != null) ? arr[19].toString() : null);
		obj.setMainDocId((arr[20] != null) ? "'" + arr[20].toString() : null);
		obj.setMainDocHashCode((arr[21] != null) ? arr[21].toString() : null);
		/*
		 * obj.setSuppDocType(suppDocType); obj.setSuppDocId(suppDocId);
		 * obj.setSuppDocHashCode(suppDocHashCode);
		 */
		return obj;
	}

	public static List<String> clobToList(Clob clob) {
		List<String> result = new ArrayList<>();
		if (clob == null)
			return result;

		LOGGER.debug("clobToList {} ", clob);
		try (Reader reader = clob.getCharacterStream()) {
			StringBuilder sb = new StringBuilder();
			char[] buffer = new char[2048];
			int bytesRead;
			while ((bytesRead = reader.read(buffer)) != -1) {
				sb.append(buffer, 0, bytesRead);
			}

			// CLOB string content, e.g., "'27GSPMH0481G1ZN','29ABCDE1234P2Z1'"
			String clobString = sb.toString();
			LOGGER.debug("clobString  {} ", clobString);

			// Remove extra whitespace and split by comma
			String[] parts = clobString.split(",");

			for (String part : parts) {
				String cleaned = part.trim();

				// Remove single quotes around each GSTIN (if present)
				if (cleaned.startsWith("'") && cleaned.endsWith("'")
						&& cleaned.length() > 2) {
					cleaned = cleaned.substring(1, cleaned.length() - 1);
				}

				if (!cleaned.isEmpty()) {
					result.add(cleaned);
				}
			}

		} catch (Exception e) {
			throw new AppException("Error converting Clob to List<String>", e);
		}

		LOGGER.debug("result  {} ", result);
		return result;
	}

	private Pair<String, String> formatTaxPeriodRange(
			Integer derivedFromTaxPeriod, Integer derivedToTaxPeriod) {
		if (derivedFromTaxPeriod == null || derivedToTaxPeriod == null) {
			return null;
		}

		// Convert from YYYYMM to Year and Month
		int fromYear = derivedFromTaxPeriod / 100;
		int fromMonth = derivedFromTaxPeriod % 100;

		int toYear = derivedToTaxPeriod / 100;
		int toMonth = derivedToTaxPeriod % 100;

		// Convert numeric month to string (e.g. 1 => Jan)
		String[] monthNames = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul",
				"Aug", "Sep", "Oct", "Nov", "Dec" };

		String from = (fromMonth >= 1 && fromMonth <= 12)
				? monthNames[fromMonth - 1] + " " + fromYear
				: derivedFromTaxPeriod.toString();

		String to = (toMonth >= 1 && toMonth <= 12)
				? monthNames[toMonth - 1] + " " + toYear
				: derivedToTaxPeriod.toString();

		return new Pair<>(from, to);
	}
}
