package com.ey.advisory.app.data.services.gstr9;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.apache.commons.io.FileUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.entities.gstr9.Gstr9TransAdvAdjAmdEntity;
import com.ey.advisory.app.data.entities.gstr9.Gstr9TransAdvRecAmdEntity;
import com.ey.advisory.app.data.entities.gstr9.Gstr9TransB2CSB2CSAEntity;
import com.ey.advisory.app.data.entities.gstr9.Gstr9TransGstr1TransEntity;
import com.ey.advisory.app.data.entities.gstr9.Gstr9TransGstr3bSmryEntity;
import com.ey.advisory.app.data.entities.gstr9.Gstr9TransGstr3bTaxPrdEntity;
import com.ey.advisory.app.data.entities.gstr9.Gstr9TransHsnEntity;
import com.ey.advisory.app.data.entities.gstr9.Gstr9TransNilNonExtEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9TransAdvAdjAmdRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9TransAdvRecAmdRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9TransB2CSB2CSARepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9TransGstr1TransRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9TransGstr3bSmryRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9TransGstr3bTaxPrdRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9TransHsnRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9TransNilNonExtRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AsyncReportDownloadService;
import com.ey.advisory.common.CombineAndZipReportFiles;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.ReportConfig;
import com.ey.advisory.common.ReportConfigFactory;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.ReportTypeConstants;
import com.ey.advisory.core.config.ConfigConstants;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Reddy
 *
 */

@Component("Gstr9DumpAsyncReportDownloadServiceImpl")
@Slf4j
public class Gstr9DumpAsyncReportDownloadServiceImpl
		implements AsyncReportDownloadService {

	private static final List<String> DUMP_REPORTS = ImmutableList.of(
			ReportTypeConstants.GSTR9_TRANSLVL_GSTR1,
			ReportTypeConstants.GSTR9_B2C_B2CSA,
			ReportTypeConstants.GSTR9_ADVREC_AMD,
			ReportTypeConstants.GSTR9_ADVADJ_AMD,
			ReportTypeConstants.GSTR9_NIL_NON_EXT,
			ReportTypeConstants.GSTR9_HSN_SUMMARY,
			ReportTypeConstants.GSTR9_GSTR3B_SUMMARY,
			ReportTypeConstants.GSTR9_GSTR3B_TXPAID);

	private static final DateTimeFormatter dtf = DateTimeFormatter
			.ofPattern("yyyyMMddHHmmss");

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	Gstr9TransAdvAdjAmdRepository gstr9AdvAdjRepo;

	@Autowired
	Gstr9TransAdvRecAmdRepository gstr9AdvRecRepo;

	@Autowired
	Gstr9TransB2CSB2CSARepository gstr9B2CSB2CSARepo;

	@Autowired
	Gstr9TransGstr1TransRepository gstr9Gstr1TransRepo;

	@Autowired
	Gstr9TransHsnRepository gstr9TransHsnRepo;

	@Autowired
	Gstr9TransNilNonExtRepository gstr9NilNonExtRepo;

	@Autowired
	Gstr9TransGstr3bSmryRepository gstr9Gstr3BSmryRepo;

	@Autowired
	Gstr9TransGstr3bTaxPrdRepository gstr9Gstr3BTxPrdRepo;

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
	Gstr9DumpReportCreationHelper reportCreationHelper;

	@Override
	public void generateReports(Long id) {

		Writer writer = null;
		File tempDir = null;
		Pair<String,String> uploadedZipName = null;

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

			String gstin = GenUtil.convertClobtoString(entity.getGstins());
			// String returnPeriod = entity.getTaxPeriod();// 032022
			String reportCateg = entity.getReportCateg();
			String reportType = entity.getReportType();
			String dataType = entity.getDataType();
			String reqPayload = entity.getReqPayload();
			JsonObject requestObject = new JsonParser().parse(reqPayload)
					.getAsJsonObject();

			String fy = requestObject.get("fy").getAsString();

			fy = fy.replace("-", "");

			int emptyRepCount = 0;

			for (String reportName : DUMP_REPORTS) {

				List<Long> chunkIds = getChunkCount(reportName, gstin, fy);

				if (chunkIds.isEmpty()) {
					emptyRepCount++;
					continue;
				}

				String timeMilli = dtf.format(LocalDateTime.now());

				String fullPath = tempDir.getAbsolutePath() + File.separator
						+ reportCateg + "_" + reportName + "_" + timeMilli
						+ ".csv";

				writer = new BufferedWriter(new FileWriter(fullPath), 8192);

				ReportConfig reportConfig = reportConfigFactory
						.getReportConfig(reportName, reportCateg, dataType);

				if (LOGGER.isDebugEnabled()) {
					String msg = "Mapped Reportconvertor based on report type and "
							+ "Report Category";
					LOGGER.debug(msg);
				}

				if (ReportTypeConstants.GSTR9_TRANSLVL_GSTR1
						.equalsIgnoreCase(reportName)) {
					StatefulBeanToCsv<Gstr9TransGstr1TransEntity> beanWriter = getGstr9Trans1Gstr1Reader(
							reportConfig, writer);
					reportCreationHelper.WriteTransGstr1toCsv(reportName,
							beanWriter, reportCateg, gstin, fy, chunkIds);

				} else if (ReportTypeConstants.GSTR9_B2C_B2CSA
						.equalsIgnoreCase(reportName)) {
					StatefulBeanToCsv<Gstr9TransB2CSB2CSAEntity> beanWriter = getGstr9B2CSB2CSAReader(
							reportConfig, writer);

					reportCreationHelper.WriteTransB2CSB2CSAtoCsv(reportName,
							beanWriter, reportCateg, gstin, fy, chunkIds);

				} else if (ReportTypeConstants.GSTR9_ADVREC_AMD
						.equalsIgnoreCase(reportName)) {

					StatefulBeanToCsv<Gstr9TransAdvRecAmdEntity> beanWriter = getGstr9AdvRecAmdReader(
							reportConfig, writer);

					reportCreationHelper.WriteTransAdvRecAmdtoCsv(reportName,
							beanWriter, reportCateg, gstin, fy, chunkIds);

				} else if (ReportTypeConstants.GSTR9_ADVADJ_AMD
						.equalsIgnoreCase(reportName)) {
					StatefulBeanToCsv<Gstr9TransAdvAdjAmdEntity> beanWriter = getGstr9AdvAdjAmdReader(
							reportConfig, writer);

					reportCreationHelper.WriteTransAdvAdjAmdtoCsv(reportName,
							beanWriter, reportCateg, gstin, fy, chunkIds);

				} else if (ReportTypeConstants.GSTR9_NIL_NON_EXT
						.equalsIgnoreCase(reportName)) {
					StatefulBeanToCsv<Gstr9TransNilNonExtEntity> beanWriter = getGstr9NilNonExtReader(
							reportConfig, writer);

					reportCreationHelper.WriteTransNilNonExttoCsv(reportName,
							beanWriter, reportCateg, gstin, fy, chunkIds);

				} else if (ReportTypeConstants.GSTR9_HSN_SUMMARY
						.equalsIgnoreCase(reportName)) {

					StatefulBeanToCsv<Gstr9TransHsnEntity> beanWriter = getGstr9HsnSummReader(
							reportConfig, writer);

					reportCreationHelper.WriteTransHsnSummtoCsv(reportName,
							beanWriter, reportCateg, gstin, fy, chunkIds);

				} else if (ReportTypeConstants.GSTR9_GSTR3B_SUMMARY
						.equalsIgnoreCase(reportName)) {
					StatefulBeanToCsv<Gstr9TransGstr3bSmryEntity> beanWriter = getGstr9Gstr3bSumryReader(
							reportConfig, writer);

					reportCreationHelper.WriteTransGstr3bSmrytoCsv(reportName,
							beanWriter, reportCateg, gstin, fy, chunkIds);

				} else if (ReportTypeConstants.GSTR9_GSTR3B_TXPAID
						.equalsIgnoreCase(reportName)) {

					StatefulBeanToCsv<Gstr9TransGstr3bTaxPrdEntity> beanWriter = getGstr9Gstr3bTaxPrdReader(
							reportConfig, writer);

					reportCreationHelper.WriteTransGstr3bTxPrdtoCsv(reportName,
							beanWriter, reportCateg, gstin, fy, chunkIds);
				}

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

			if (DUMP_REPORTS.size() == emptyRepCount) {
				fileStatusDownloadReportRepo.updateStatus(id,
						ReportStatusConstants.NO_DATA_FOUND, null,
						LocalDateTime.now());
				return;
			}

			String zipFileName = "";
			if (tempDir.list().length > 0) {

				if (LOGGER.isDebugEnabled()) {
					String msg = "Creting Zip folder";
					LOGGER.debug(msg);
				}
				zipFileName = combineAndZipReportFiles.zipfolder(tempDir,
						reportType, null, id);

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format("ZipFile name : %s",
							zipFileName);
					LOGGER.debug(msg);
				}

				File zipFile = new File(tempDir, zipFileName);

				 uploadedZipName = DocumentUtility.uploadFile(zipFile,
						"Gstr9DumpReports");
				
				String uploadedDocName = uploadedZipName.getValue0();
				String docId = uploadedZipName.getValue1();

				if (LOGGER.isDebugEnabled()) {
					String msg = "Sucessfully uploaded zip file and updating the "
							+ "status as 'Report Generated'";
					LOGGER.debug(msg);
				}
				

				fileStatusDownloadReportRepo.updateStatus(id,
						ReportStatusConstants.REPORT_GENERATED, uploadedDocName,
						LocalDateTime.now(),docId);
			} else {

				LOGGER.error("No Data found for report id : %d", id);
				fileStatusDownloadReportRepo.updateStatus(id,
						ReportStatusConstants.NO_DATA_FOUND, null,
						LocalDateTime.now());
			}
		} catch (Exception e) {

			fileStatusDownloadReportRepo.updateStatus(id,
					ReportStatusConstants.REPORT_GENERATION_FAILED, null,
					LocalDateTime.now());
			String msg = "Exception occued while while generating csv file";
			LOGGER.error(msg, e);
			throw new AppException(msg, e);

		} finally {
			deleteTempDir(tempDir);
		}

	}

	private List<Long> getChunkCount(String reportName, String gstin,
			String fy) {

		if (ReportTypeConstants.GSTR9_TRANSLVL_GSTR1
				.equalsIgnoreCase(reportName)) {
			return gstr9Gstr1TransRepo.getDistinctChunkIds(gstin, fy, false);

		} else if (ReportTypeConstants.GSTR9_B2C_B2CSA
				.equalsIgnoreCase(reportName)) {
			return gstr9B2CSB2CSARepo.getDistinctChunkIds(gstin, fy, false);

		} else if (ReportTypeConstants.GSTR9_ADVREC_AMD
				.equalsIgnoreCase(reportName)) {
			return gstr9AdvRecRepo.getDistinctChunkIds(gstin, fy, false);

		} else if (ReportTypeConstants.GSTR9_ADVADJ_AMD
				.equalsIgnoreCase(reportName)) {
			return gstr9AdvAdjRepo.getDistinctChunkIds(gstin, fy, false);

		} else if (ReportTypeConstants.GSTR9_NIL_NON_EXT
				.equalsIgnoreCase(reportName)) {
			return gstr9NilNonExtRepo.getDistinctChunkIds(gstin, fy, false);

		} else if (ReportTypeConstants.GSTR9_HSN_SUMMARY
				.equalsIgnoreCase(reportName)) {
			return gstr9TransHsnRepo.getDistinctChunkIds(gstin, fy, false);

		} else if (ReportTypeConstants.GSTR9_GSTR3B_SUMMARY
				.equalsIgnoreCase(reportName)) {
			return gstr9Gstr3BSmryRepo.getDistinctChunkIds(gstin, fy, false);

		} else if (ReportTypeConstants.GSTR9_GSTR3B_TXPAID
				.equalsIgnoreCase(reportName)) {
			return gstr9Gstr3BTxPrdRepo.getDistinctChunkIds(gstin, fy, false);

		} else {
			return new ArrayList<>();
		}
	}

	private static File createTempDir() throws IOException {
		return Files.createTempDirectory("DownloadReports").toFile();
	}

	private StatefulBeanToCsv<Gstr9TransGstr1TransEntity> getGstr9Trans1Gstr1Reader(
			ReportConfig config, Writer writer) throws Exception {

		if (!env.containsProperty(config.getColumnMappingsKey())
				|| !env.containsProperty(config.getHeadersKey())) {
			String msg = "HeaderKey or ColumnMapping key Not found";
			LOGGER.error(msg);
			throw new AppException(msg);
		}

		ColumnPositionMappingStrategy<Gstr9TransGstr1TransEntity> mappingStrategy = new ColumnPositionMappingStrategy<>();
		mappingStrategy.setType(Gstr9TransGstr1TransEntity.class);
		mappingStrategy.setColumnMapping(
				env.getProperty(config.getColumnMappingsKey()).split(","));

		StatefulBeanToCsvBuilder<Gstr9TransGstr1TransEntity> builder = new StatefulBeanToCsvBuilder<>(
				writer);
		StatefulBeanToCsv<Gstr9TransGstr1TransEntity> beanWriter = builder
				.withMappingStrategy(mappingStrategy)
				.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
				.withLineEnd(CSVWriter.DEFAULT_LINE_END)
				.withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER).build();

		writer.append(env.getProperty(config.getHeadersKey()));
		return beanWriter;
	}

	private StatefulBeanToCsv<Gstr9TransB2CSB2CSAEntity> getGstr9B2CSB2CSAReader(
			ReportConfig config, Writer writer) throws Exception {

		if (!env.containsProperty(config.getColumnMappingsKey())
				|| !env.containsProperty(config.getHeadersKey())) {
			String msg = "HeaderKey or ColumnMapping key Not found";
			LOGGER.error(msg);
			throw new AppException(msg);
		}

		ColumnPositionMappingStrategy<Gstr9TransB2CSB2CSAEntity> mappingStrategy = new ColumnPositionMappingStrategy<>();
		mappingStrategy.setType(Gstr9TransB2CSB2CSAEntity.class);
		mappingStrategy.setColumnMapping(
				env.getProperty(config.getColumnMappingsKey()).split(","));

		StatefulBeanToCsvBuilder<Gstr9TransB2CSB2CSAEntity> builder = new StatefulBeanToCsvBuilder<>(
				writer);
		StatefulBeanToCsv<Gstr9TransB2CSB2CSAEntity> beanWriter = builder
				.withMappingStrategy(mappingStrategy)
				.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
				.withLineEnd(CSVWriter.DEFAULT_LINE_END)
				.withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER).build();

		writer.append(env.getProperty(config.getHeadersKey()));
		return beanWriter;
	}

	private StatefulBeanToCsv<Gstr9TransAdvRecAmdEntity> getGstr9AdvRecAmdReader(
			ReportConfig config, Writer writer) throws Exception {

		if (!env.containsProperty(config.getColumnMappingsKey())
				|| !env.containsProperty(config.getHeadersKey())) {
			String msg = "HeaderKey or ColumnMapping key Not found";
			LOGGER.error(msg);
			throw new AppException(msg);
		}

		ColumnPositionMappingStrategy<Gstr9TransAdvRecAmdEntity> mappingStrategy = new ColumnPositionMappingStrategy<>();
		mappingStrategy.setType(Gstr9TransAdvRecAmdEntity.class);
		mappingStrategy.setColumnMapping(
				env.getProperty(config.getColumnMappingsKey()).split(","));

		StatefulBeanToCsvBuilder<Gstr9TransAdvRecAmdEntity> builder = new StatefulBeanToCsvBuilder<>(
				writer);
		StatefulBeanToCsv<Gstr9TransAdvRecAmdEntity> beanWriter = builder
				.withMappingStrategy(mappingStrategy)
				.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
				.withLineEnd(CSVWriter.DEFAULT_LINE_END)
				.withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER).build();

		writer.append(env.getProperty(config.getHeadersKey()));
		return beanWriter;
	}

	private StatefulBeanToCsv<Gstr9TransAdvAdjAmdEntity> getGstr9AdvAdjAmdReader(
			ReportConfig config, Writer writer) throws Exception {

		if (!env.containsProperty(config.getColumnMappingsKey())
				|| !env.containsProperty(config.getHeadersKey())) {
			String msg = "HeaderKey or ColumnMapping key Not found";
			LOGGER.error(msg);
			throw new AppException(msg);
		}

		ColumnPositionMappingStrategy<Gstr9TransAdvAdjAmdEntity> mappingStrategy = new ColumnPositionMappingStrategy<>();
		mappingStrategy.setType(Gstr9TransAdvAdjAmdEntity.class);
		mappingStrategy.setColumnMapping(
				env.getProperty(config.getColumnMappingsKey()).split(","));

		StatefulBeanToCsvBuilder<Gstr9TransAdvAdjAmdEntity> builder = new StatefulBeanToCsvBuilder<>(
				writer);
		StatefulBeanToCsv<Gstr9TransAdvAdjAmdEntity> beanWriter = builder
				.withMappingStrategy(mappingStrategy)
				.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
				.withLineEnd(CSVWriter.DEFAULT_LINE_END)
				.withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER).build();

		writer.append(env.getProperty(config.getHeadersKey()));
		return beanWriter;
	}

	private StatefulBeanToCsv<Gstr9TransNilNonExtEntity> getGstr9NilNonExtReader(
			ReportConfig config, Writer writer) throws Exception {

		if (!env.containsProperty(config.getColumnMappingsKey())
				|| !env.containsProperty(config.getHeadersKey())) {
			String msg = "HeaderKey or ColumnMapping key Not found";
			LOGGER.error(msg);
			throw new AppException(msg);
		}

		ColumnPositionMappingStrategy<Gstr9TransNilNonExtEntity> mappingStrategy = new ColumnPositionMappingStrategy<>();
		mappingStrategy.setType(Gstr9TransNilNonExtEntity.class);
		mappingStrategy.setColumnMapping(
				env.getProperty(config.getColumnMappingsKey()).split(","));

		StatefulBeanToCsvBuilder<Gstr9TransNilNonExtEntity> builder = new StatefulBeanToCsvBuilder<>(
				writer);
		StatefulBeanToCsv<Gstr9TransNilNonExtEntity> beanWriter = builder
				.withMappingStrategy(mappingStrategy)
				.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
				.withLineEnd(CSVWriter.DEFAULT_LINE_END)
				.withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER).build();

		writer.append(env.getProperty(config.getHeadersKey()));
		return beanWriter;
	}

	private StatefulBeanToCsv<Gstr9TransHsnEntity> getGstr9HsnSummReader(
			ReportConfig config, Writer writer) throws Exception {

		if (!env.containsProperty(config.getColumnMappingsKey())
				|| !env.containsProperty(config.getHeadersKey())) {
			String msg = "HeaderKey or ColumnMapping key Not found";
			LOGGER.error(msg);
			throw new AppException(msg);
		}

		ColumnPositionMappingStrategy<Gstr9TransHsnEntity> mappingStrategy = new ColumnPositionMappingStrategy<>();
		mappingStrategy.setType(Gstr9TransHsnEntity.class);
		mappingStrategy.setColumnMapping(
				env.getProperty(config.getColumnMappingsKey()).split(","));

		StatefulBeanToCsvBuilder<Gstr9TransHsnEntity> builder = new StatefulBeanToCsvBuilder<>(
				writer);
		StatefulBeanToCsv<Gstr9TransHsnEntity> beanWriter = builder
				.withMappingStrategy(mappingStrategy)
				.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
				.withLineEnd(CSVWriter.DEFAULT_LINE_END)
				.withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER).build();

		writer.append(env.getProperty(config.getHeadersKey()));
		return beanWriter;
	}

	private StatefulBeanToCsv<Gstr9TransGstr3bSmryEntity> getGstr9Gstr3bSumryReader(
			ReportConfig config, Writer writer) throws Exception {

		if (!env.containsProperty(config.getColumnMappingsKey())
				|| !env.containsProperty(config.getHeadersKey())) {
			String msg = "HeaderKey or ColumnMapping key Not found";
			LOGGER.error(msg);
			throw new AppException(msg);
		}

		ColumnPositionMappingStrategy<Gstr9TransGstr3bSmryEntity> mappingStrategy = new ColumnPositionMappingStrategy<>();
		mappingStrategy.setType(Gstr9TransGstr3bSmryEntity.class);
		mappingStrategy.setColumnMapping(
				env.getProperty(config.getColumnMappingsKey()).split(","));

		StatefulBeanToCsvBuilder<Gstr9TransGstr3bSmryEntity> builder = new StatefulBeanToCsvBuilder<>(
				writer);
		StatefulBeanToCsv<Gstr9TransGstr3bSmryEntity> beanWriter = builder
				.withMappingStrategy(mappingStrategy)
				.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
				.withLineEnd(CSVWriter.DEFAULT_LINE_END)
				.withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER).build();

		writer.append(env.getProperty(config.getHeadersKey()));
		return beanWriter;
	}

	private StatefulBeanToCsv<Gstr9TransGstr3bTaxPrdEntity> getGstr9Gstr3bTaxPrdReader(
			ReportConfig config, Writer writer) throws Exception {

		if (!env.containsProperty(config.getColumnMappingsKey())
				|| !env.containsProperty(config.getHeadersKey())) {
			String msg = "HeaderKey or ColumnMapping key Not found";
			LOGGER.error(msg);
			throw new AppException(msg);
		}

		ColumnPositionMappingStrategy<Gstr9TransGstr3bTaxPrdEntity> mappingStrategy = new ColumnPositionMappingStrategy<>();
		mappingStrategy.setType(Gstr9TransGstr3bTaxPrdEntity.class);
		mappingStrategy.setColumnMapping(
				env.getProperty(config.getColumnMappingsKey()).split(","));

		StatefulBeanToCsvBuilder<Gstr9TransGstr3bTaxPrdEntity> builder = new StatefulBeanToCsvBuilder<>(
				writer);
		StatefulBeanToCsv<Gstr9TransGstr3bTaxPrdEntity> beanWriter = builder
				.withMappingStrategy(mappingStrategy)
				.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
				.withLineEnd(CSVWriter.DEFAULT_LINE_END)
				.withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER).build();

		writer.append(env.getProperty(config.getHeadersKey()));
		return beanWriter;
	}

	private void deleteTempDir(File tempDir) {

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
