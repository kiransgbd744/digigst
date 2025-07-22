package com.ey.advisory.app.recon.type.tagging.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2ProcessedReconTypeTaggedReportGstinDetailsRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2ProcessedReconTypeTaggedReportProcRepository;
import com.ey.advisory.app.data.views.client.GSTR2ProcessedInwarddto;
import com.ey.advisory.app.services.search.filestatussearch.PRSummarySplitReportCommonService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AsyncReportDownloadService;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.ReconStatusConstants;
import com.ey.advisory.common.ReportConfig;
import com.ey.advisory.common.ReportConfigFactory;
import com.ey.advisory.common.ReportConvertor;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.ZipCsvFilesUtil;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author vishal.verma
 *
 */

@Slf4j
@Component("Gstr2ProcessedReconTypeTaggingReportServiceImpl")
public class Gstr2ProcessedReconTypeTaggingReportServiceImpl
		implements AsyncReportDownloadService {

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	private Environment env;

	@Autowired
	@Qualifier("ProcessedReportConfigFactoryImpl")
	ReportConfigFactory reportConfigFactory;

	@Autowired
	private PRSummarySplitReportCommonService commonService;

	@Autowired
	@Qualifier("Gstr2ProcessedReconTypeTaggedReportProcRepository")
	Gstr2ProcessedReconTypeTaggedReportProcRepository procRepo;

	@Autowired
	ZipCsvFilesUtil zipUtil;

	@Autowired
	Gstr2ProcessedReconTypeTaggedReportGstinDetailsRepository gstinDetailsRepository;

	@Override
	public void generateReports(Long id) {

		Writer writer = null;
		File tempDir = null;

		try {

			if (LOGGER.isDebugEnabled()) {
				String msg = "Inside Gstr2ProcessedReconTypeTaggingReportServiceImpl";
				LOGGER.debug(msg);
			}
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

			String fromTaxPeriod = entity.getTaxPeriodFrom();
			String toTaxPeriod = entity.getTaxPeriodTo();

			String reportType = entity.getReportType();// Processed Records
														// (Recon Tagging)

			String reportCateg = entity.getReportCateg();
			String dataType = entity.getDataType();
			//calling dara removal proc
			callDataRemovalProc();
			
			//calling loading proc
			String loadStatus = callLoadingProc(id);
			

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Invoking USP_PR_RECON_TAG_RPT_INSERT proc and got response as : %s",
						loadStatus);
				LOGGER.debug(msg);
			}
			
			if(!loadStatus.equalsIgnoreCase("SUCCESS")) {
				fileStatusDownloadReportRepo.updateStatus(id,
						ReportStatusConstants.NO_DATA_FOUND, null,
						LocalDateTime.now());
				return;
			}
			
			// calling list of procs for data preparation

			invokeListOfProcs(id, fromTaxPeriod, toTaxPeriod);
			
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Invoked seq of Procs with id  : %d",
						id);
				LOGGER.debug(msg);
			}

			Integer chunkCount = getChunkCount(id);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Invoking ChuckCount StoreProc and got response as : %d",
						chunkCount);
				LOGGER.debug(msg);
			}

			if (chunkCount == 0) {
				fileStatusDownloadReportRepo.updateStatus(id,
						ReportStatusConstants.NO_DATA_FOUND, null,
						LocalDateTime.now());
				return;
			}

			

			// Processed Records (Recon Tagging), ProcessedSummary, Inward
			ReportConfig reportConfig = reportConfigFactory
					.getReportConfig(reportType, reportCateg, dataType);

			ReportConvertor reportConvertor = reportConfig.getReportConvertor();

			if (LOGGER.isDebugEnabled()) {
				String msg = "Mapped Reportconvertor based on report type and "
						+ "Report Category";
				LOGGER.debug(msg);
			}
			String fullPath = tempDir.getAbsolutePath() + File.separator
					+ getUniqueFileName("PR Records_Recon Type Tagging_" + id)
					+ "_" + 1 + ".csv";

			writer = new BufferedWriter(new FileWriter(fullPath), 8192);

			writetoCsv(chunkCount, id, reportConvertor, reportType,
					reportConfig, reportCateg, tempDir, writer);

			if (LOGGER.isDebugEnabled()) {
				String msg = "Sucessfully uploaded zip file and updating the "
						+ "status as 'Report Generated'";
				LOGGER.debug(msg);
			}

			fileStatusDownloadReportRepo.updateStatus(id,
					ReportStatusConstants.REPORT_GENERATED, null,
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));

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
					String msg = String.format("Failed to remove the temp "
							+ "directory created for zip: '%s'. This will "
							+ "lead to clogging of disk space.",
							tempDir.getAbsolutePath());
					LOGGER.error(msg, ex);
				}
			}

		}

	}

	private void invokeListOfProcs(Long id, String fromTaxPeriod,
			String toTaxPeriod) {

		try {

			List<Gstr2ProcessedReconTypeTaggedReportProcEntity> allProcList = procRepo
					.findProcedure();

			if (allProcList.isEmpty()) {

				String msg = String
						.format("TBL_PR_RECON_TAG_PROCEDURE is empty "
								+ ": Hence updating Failed ", id.toString());

				LOGGER.error(msg);

				fileStatusDownloadReportRepo.updateStatus(id,
						ReportStatusConstants.REPORT_GENERATION_FAILED, null,
						LocalDateTime.now());

				throw new AppException();
			}

			Map<Integer, String> map = allProcList.stream()
					.collect(Collectors.toMap(o -> o.getSeqId(),
							o -> o.getProcName(), (o1, o2) -> o2,
							TreeMap::new));

			List<Gstr2ProcessedReconTypeTaggedReportGstinDetailsEntity> list = gstinDetailsRepository
					.findByReportDwndId(id);

			for (Integer k : map.keySet()) {

				String procName = map.get(k);

				for (Gstr2ProcessedReconTypeTaggedReportGstinDetailsEntity gstinEntity : list) {

					StoredProcedureQuery storedProc = procCall(id, procName,
							gstinEntity.getReconType(), gstinEntity.getGstin(),
							gstinEntity.getTaxPeriod());

					String response = (String) storedProc.getSingleResult();
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(" SP Name {}, " + "id {} response {}",
								procName, id, response);
					}

					if (!ReconStatusConstants.SUCCESS
							.equalsIgnoreCase(response)) {

						String msg = String.format(
								" Id is '%s', Response " + "from SP %s did not "
										+ "return success,"
										+ " Hence updating to Failed",
								id.toString(), procName);
						LOGGER.error(msg);

						fileStatusDownloadReportRepo.updateStatus(id,
								ReportStatusConstants.REPORT_GENERATION_FAILED,
								null, LocalDateTime.now());

						throw new AppException(msg);
					}

				}

			}

			// call data order proc
			invokeDataOrderProc(id);

		} catch (Exception e) {
			LOGGER.error("Excetion occured while calling the proc");
			fileStatusDownloadReportRepo.updateStatus(id,
					ReportStatusConstants.REPORT_GENERATION_FAILED, null,
					LocalDateTime.now());

			throw new AppException();
		}

	}

	@SuppressWarnings("unchecked")
	private void writetoCsv(Integer chunkSize, Long id,
			ReportConvertor reportConvertor, String reportType,
			ReportConfig reportConfig, String reportCateg, File tempDir,
			Writer writer) {

		List<Object[]> list = null;
		List<GSTR2ProcessedInwarddto> responseFromDao = null;
		String msg = null;

		String procName = null;

		procName = "gstr2ReconTaggingReportChunkData";

		try {
			int fileIndex = 1;
			int count = 0;
			int maxLimitPerFile = commonService.getChunkingSizes();// 200000

			StatefulBeanToCsv<GSTR2ProcessedInwarddto> beanWriter = getBeanWriter(
					reportConfig, writer);

			for (int noOfChunk = 1; noOfChunk <= chunkSize; noOfChunk++) {

				if (LOGGER.isDebugEnabled()) {
					msg = String.format("Inside for loop with count: %d / %d",
							noOfChunk, chunkSize);
					LOGGER.debug(msg);
				}

				StoredProcedureQuery reportDataProc = entityManager
						.createNamedStoredProcedureQuery(procName);

				reportDataProc.setParameter("P_REPORT_DOWNLOAD_ID", id);

				reportDataProc.setParameter("P_CHUNK_VALUE", noOfChunk);

				list = reportDataProc.getResultList();

				if (LOGGER.isDebugEnabled()) {
					msg = String.format(
							"Executed Stored proc to get ChunckData and "
									+ "got resultset of size: %d",
							list.size());
					LOGGER.debug(msg);
				}

				if (list.size() > 0) {
					responseFromDao = list.stream()
							.map(o -> (GSTR2ProcessedInwarddto) reportConvertor
									.convert(o, reportType))
							.collect(Collectors.toCollection(ArrayList::new));

					if (LOGGER.isDebugEnabled()) {
						msg = String.format(
								"Record count after converting object array to DTO  %d",
								responseFromDao.size());
						LOGGER.debug(msg);
					}

					if (responseFromDao != null && !responseFromDao.isEmpty()) {
						if (count >= maxLimitPerFile) {
							flush(writer);

							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug(
										"Invoking PR Summary commonService to"
												+ " upload a zip file : "
												+ "id {} ",
										id);
							}

							// Zipping

							commonService.chunkZipFiles(tempDir, id, id,
									getUniqueFileName(
											"PR Records_Recon Type Tagging_"
													+ id),
									1);

							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("After Invoking PR Summary "
										+ "commonService to upload "
										+ "a zip file : id "
										+ "{}, deleted file "
										+ "from TempDir, ", id);
							}

							count = 0;
							fileIndex += 1;

							String fullPath = tempDir.getAbsolutePath()
									+ File.separator
									+ getUniqueFileName(
											"PR Records_Recon Type Tagging_"
													+ id)
									+ "_" + fileIndex + ".csv";

							writer = new BufferedWriter(
									new FileWriter(fullPath), 8192);

							beanWriter = getBeanWriter(reportConfig, writer);
						}
						count += responseFromDao.size();
						beanWriter.write(responseFromDao);

						long generationStTime = System.currentTimeMillis();
						long generationEndTime = System.currentTimeMillis();
						long generationTimeDiff = (generationEndTime
								- generationStTime);
						if (LOGGER.isDebugEnabled()) {
							msg = String.format(
									"Total Time taken to"
											+ " Generate the report is '%d' "
											+ "millisecs,"
											+ " Report Name and Data count:"
											+ " '%s' - '%s'",
									generationTimeDiff, reportType,
									responseFromDao.size());
							LOGGER.debug(msg);
						}
						if (chunkSize == 1 || noOfChunk == chunkSize) {
							flush(writer);
							commonService.chunkZipFiles(tempDir, id, id,
									getUniqueFileName(
											"PR Records_Recon Type Tagging_"
													+ id),
									1);
							break;
						}

					}

				}
			}

		} catch (Exception ex) {
			msg = "Exception occured while fetching data from get ChunkData";
			LOGGER.error(msg);
			throw new AppException(msg, ex);
		}

	}

	/**
	 * @param writer
	 */
	private void flush(Writer writer) {
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

	private Integer getChunkCount(Long id) {

		String procName = null;

		procName = "gstr2ReconTaggingReportChunkCount";

		StoredProcedureQuery counterProc = entityManager
				.createNamedStoredProcedureQuery(procName);

		counterProc.setParameter("P_REPORT_DOWNLOAD_ID", id);

		Integer response = (Integer) counterProc.getSingleResult();

		return response;
	}
	
	private String callLoadingProc(Long id) {

		String procName = null;

		procName = "gstr2ReconTaggingReportDataLoad";

		StoredProcedureQuery counterProc = entityManager
				.createNamedStoredProcedureQuery(procName);

		counterProc.setParameter("P_REPORT_DOWNLOAD_ID", id);

		String response = (String) counterProc.getSingleResult();

		return response;
	}

	private void invokeDataOrderProc(Long id) {

		String procName = null;
		try {
			procName = "gstr2ReconTaggingReportChunkOrder";

			StoredProcedureQuery counterProc = entityManager
					.createNamedStoredProcedureQuery(procName);

			counterProc.setParameter("P_REPORT_DOWNLOAD_ID", id);

			String response = (String) counterProc.getSingleResult();

			LOGGER.debug(
					"gstr2ReconTaggingReportChunkOrder {}, response {}" + " :",
					procName, response);

		} catch (Exception e) {

			LOGGER.error(
					"Error cocured while calling gstr2ReconTaggingReportChunkOrder {} : ",
					procName);
		}
	}

	private static File createTempDir() throws IOException {
		return Files.createTempDirectory("DownloadReports").toFile();
	}

	private StatefulBeanToCsv<GSTR2ProcessedInwarddto> getBeanWriter(
			ReportConfig config, Writer writer) throws Exception {

		if (!env.containsProperty(config.getColumnMappingsKey())
				|| !env.containsProperty(config.getHeadersKey())) {
			String msg = "HeaderKey or ColumnMapping key Not found";
			LOGGER.error(msg);
			throw new AppException(msg);
		}

		ColumnPositionMappingStrategy<GSTR2ProcessedInwarddto> mappingStrategy = new ColumnPositionMappingStrategy<>();
		mappingStrategy.setType(GSTR2ProcessedInwarddto.class);
		mappingStrategy.setColumnMapping(
				env.getProperty(config.getColumnMappingsKey()).split(","));

		StatefulBeanToCsvBuilder<GSTR2ProcessedInwarddto> builder = new StatefulBeanToCsvBuilder<>(
				writer);
		StatefulBeanToCsv<GSTR2ProcessedInwarddto> beanWriter = builder
				.withMappingStrategy(mappingStrategy)
				.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
				.withLineEnd(CSVWriter.DEFAULT_LINE_END)
				.withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER).build();

		writer.append(env.getProperty(config.getHeadersKey()));
		return beanWriter;
	}

	private static String getUniqueFileName(String fileName) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside getUniqueFileName() method";
			LOGGER.debug(msg);
		}
		LocalDateTime now = LocalDateTime.now();
		String timeMilli = EYDateUtil.toISTDateTimeFromUTC(now).toString();

		timeMilli = timeMilli.replace(".", "");
		timeMilli = timeMilli.replace("-", "");
		timeMilli = timeMilli.replace(":", "");

		String fileNameWithTimeStamp = fileName + "_" + timeMilli;
		if (LOGGER.isDebugEnabled()) {
			String msg = "UniqueFileName created ";
			LOGGER.debug(msg, fileNameWithTimeStamp);
		}

		return fileNameWithTimeStamp;
	}

	private StoredProcedureQuery procCall(Long id, String procName,
			String reconType, String gstin, String taxPeriod) {

		StoredProcedureQuery storedProc = entityManager
				.createStoredProcedureQuery(procName);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("About to execute Recon SP with id :%s",
					id.toString());
			LOGGER.debug(msg);
		}

		storedProc.registerStoredProcedureParameter("REPORT_DOWNLOAD_ID",
				Long.class, ParameterMode.IN);

		storedProc.setParameter("REPORT_DOWNLOAD_ID", id);

		storedProc.registerStoredProcedureParameter("RECON_TYPE", String.class,
				ParameterMode.IN);
		storedProc.setParameter("RECON_TYPE", reconType);

		storedProc.registerStoredProcedureParameter("GSTIN", String.class,
				ParameterMode.IN);

		storedProc.setParameter("GSTIN", gstin);

		storedProc.registerStoredProcedureParameter("TAX_PERIOD", String.class,
				ParameterMode.IN);

		storedProc.setParameter("TAX_PERIOD", taxPeriod);
		return storedProc;
	}
	
	private void callDataRemovalProc() {

		String procName = null;

		procName = "gstr2ReconTaggingcalldataRemoval";

		StoredProcedureQuery counterProc = entityManager
				.createNamedStoredProcedureQuery(procName);

		  if (LOGGER.isDebugEnabled()) {
		        LOGGER.debug("Calling stored procedure: {}", procName);
		    }
		  
		String response = (String) counterProc.getSingleResult();
		if (LOGGER.isDebugEnabled()) {
	        LOGGER.debug("Stored procedure {} executed successfully. Response: {}", procName, response);
	    }

	}

}