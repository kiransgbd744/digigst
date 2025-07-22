package com.ey.advisory.app.services.search.filestatussearch;

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
import com.ey.advisory.app.data.views.client.ReversalInwardDto;
import com.ey.advisory.app.search.reports.BasicCommonSecParamRSReports;
import com.ey.advisory.app.services.reports.ItcReversalInwardDao;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AsyncReportDownloadService;
import com.ey.advisory.common.CombineAndZipReportFiles;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.ReportConfig;
import com.ey.advisory.common.ReportConfigFactory;
import com.ey.advisory.common.ReportConvertor;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.core.config.ConfigConstants;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.google.gson.Gson;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Reddy
 *
 */
@Component("AsyncCommonCreditDownloadInwardServiceImpl")
@Slf4j
public class AsyncCommonCreditDownloadInwardServiceImpl
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
	@Qualifier("ReportConfigFactoryImpl")
	ReportConfigFactory reportConfigFactory;

	@Autowired
	CombineAndZipReportFiles combineAndZipReportFiles;

	@Autowired
	@Qualifier("BasicCommonSecParamRSReports")
	BasicCommonSecParamRSReports basicCommonSecParamRSReports;

	@Autowired
	@Qualifier("ItcInwardReportDaoImpl")
	private ItcReversalInwardDao itcReversalInwardDao;

	@Autowired
	@Qualifier("entityInfoRepository")
	EntityInfoRepository repo;

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
			String reportCateg = entity.getReportCateg();
			String dataType = entity.getDataType();
			String status = entity.getStatus();
			String reqPayload = optEntity.get().getReqPayload();

			Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
			Gstr1ReviwSummReportsReqDto criteria = gson.fromJson(reqPayload,
					Gstr1ReviwSummReportsReqDto.class);
			Gstr1ReviwSummReportsReqDto request = basicCommonSecParamRSReports
					.setDataSecuritySearchParams(criteria);

			setDataSecurityAttribute(request, entity);

			// List<Object> responseFromcompAmt = new ArrayList<>();
			// responseFromcompAmt =
			// itcReversalInwardDao.getItcReports(request);

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
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
				return;
			}

			String entityName = repo
					.findEntityNameByEntityId(request.getEntityId().get(0));

			LocalDateTime timeOfGeneration = LocalDateTime.now();
			LocalDateTime convertISDDate = EYDateUtil
					.toISTDateTimeFromUTC(timeOfGeneration);
			DateTimeFormatter format = DateTimeFormatter
					.ofPattern("yyyyMMddHHmmss");

			String fullPath = tempDir.getAbsolutePath() + File.separator
					+ "Transactional_Inward_ITC_Reversal_Rule_42_Report_"
					+ entityName + format.format(convertISDDate) + ".csv";

			writer = new BufferedWriter(new FileWriter(fullPath), 8192);

			ReportConfig reportConfig = reportConfigFactory
					.getReportConfig(reportType, reportCateg, dataType);

			ReportConvertor reportConvertor = reportConfig.getReportConvertor();

			if (LOGGER.isDebugEnabled()) {
				String msg = "Mapped Reportconvertor based on report type and "
						+ "Report Category";
				LOGGER.debug(msg);
			}

			StatefulBeanToCsv<ReversalInwardDto> beanWriter = getBeanWriterInwardRecords(
					reportConfig, writer);
			WritetoCsvInwardrecords(chunkCount, id, reportConvertor, reportType,
					beanWriter, reportCateg);

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

			String zipFileName = "";
			if (tempDir.list().length > 0) {

				if (LOGGER.isDebugEnabled()) {
					String msg = "Creting Zip folder";
					LOGGER.debug(msg);
				}
				zipFileName = combineAndZipReportFiles.zipfolder(tempDir,
						reportType + "_" + dataType, status, id);

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format("ZipFile name : %s",
							zipFileName);
					LOGGER.debug(msg);
				}

				File zipFile = new File(tempDir, zipFileName);

			/*	String uploadedDocName = DocumentUtility.uploadZipFile(zipFile,
						ConfigConstants.COMMONCREDITDOWNLOAD);*/
				
				Pair<String, String> uploadedZipName = DocumentUtility
						.uploadFile(zipFile, ConfigConstants.COMMONCREDITDOWNLOAD);
				String uploadedDocName = uploadedZipName.getValue0();
				String docId = uploadedZipName.getValue1();

				if (LOGGER.isDebugEnabled()) {
					String msg = "Sucessfully uploaded zip file and updating the "
							+ "status as 'Report Generated'";
					LOGGER.debug(msg);
				}

				fileStatusDownloadReportRepo.updateStatus(id,
						ReportStatusConstants.REPORT_GENERATED, uploadedDocName,
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()),docId);
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
			String msg = "Exception occued while while generating Inward csv file";
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

	private void WritetoCsvInwardrecords(Integer chunkNo, Long id,
			ReportConvertor reportConvertor, String reportType,
			StatefulBeanToCsv<ReversalInwardDto> beanWriter,
			String reportCateg) {

		List<Object[]> list = null;
		List<ReversalInwardDto> responseFromDao = null;

		try {
			for (int i = 1; i <= chunkNo; i++) {

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format("Inside for loop with count: %d",
							i);
					LOGGER.debug(msg);
				}

				StoredProcedureQuery reportDataProc = entityManager
						.createStoredProcedureQuery(
								"USP_DISP_INWARD_CREDIT_REVERSAL_RPT");

				reportDataProc.registerStoredProcedureParameter(
						"P_REPORT_DOWNLOAD_ID", Long.class, ParameterMode.IN);
				reportDataProc.setParameter("P_REPORT_DOWNLOAD_ID", id);

				reportDataProc.registerStoredProcedureParameter("P_CHUNK_NUM",
						Integer.class, ParameterMode.IN);

				reportDataProc.setParameter("P_CHUNK_NUM", chunkNo);

				list = reportDataProc.getResultList();

				if (LOGGER.isDebugEnabled()) {
					String msg = String
							.format("Executed Stored proc to get ChunckData and "
									+ "got resultset of size: %d", list.size());
					LOGGER.debug(msg);
				}

				if (list.size() > 0) {

					responseFromDao = list.stream()
							.map(o -> (ReversalInwardDto) reportConvertor
									.convert(o, reportType))
							.collect(Collectors.toCollection(ArrayList::new));

					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"Record count after converting object array to DTO ",
								responseFromDao.size());
						LOGGER.debug(msg);
					}

					if (responseFromDao != null && !responseFromDao.isEmpty()) {
						beanWriter.write(responseFromDao);

						if (LOGGER.isDebugEnabled()) {
							String msg = String.format(
									"Successfully writing into csv for chunk count: %d ",
									i);
							LOGGER.debug(msg);
						}
					}

				}

			}
		} catch (Exception ex) {
			String msg = "Exception occured while fetching data from get ChunkData";
			LOGGER.error(msg);
			throw new AppException(msg, ex);
		}

	}

	private Integer getChunkCount(Long id) {
		StoredProcedureQuery storedProc = entityManager
				.createStoredProcedureQuery(
						"USP_INS_INWARD_CHUNK_CREDIT_REVERSAL_RPT");

		storedProc.registerStoredProcedureParameter("P_REPORT_DOWNLOAD_ID",
				Long.class, ParameterMode.IN);
		storedProc.setParameter("P_REPORT_DOWNLOAD_ID", id);

		storedProc.registerStoredProcedureParameter("P_CHUNK_SIZE",
				Integer.class, ParameterMode.IN);

		storedProc.setParameter("P_CHUNK_SIZE", 10000);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Executing chunking proc"
							+ " USP_INS_INWARD_CHUNK_CREDIT_REVERSAL_RPT: '%s'",
					id.toString());
			LOGGER.debug(msg);
		}

		@SuppressWarnings("unchecked")
		Integer chunks = (Integer) storedProc.getSingleResult();

		return chunks;
	}

	private static File createTempDir() throws IOException {
		return Files.createTempDirectory("DownloadReports").toFile();
	}

	private StatefulBeanToCsv<ReversalInwardDto> getBeanWriterInwardRecords(
			ReportConfig config, Writer writer) throws Exception {

		if (!env.containsProperty(config.getColumnMappingsKey())
				|| !env.containsProperty(config.getHeadersKey())) {
			String msg = "HeaderKey or ColumnMapping key Not found";
			LOGGER.error(msg);
			throw new AppException(msg);
		}

		ColumnPositionMappingStrategy<ReversalInwardDto> mappingStrategy = new ColumnPositionMappingStrategy<>();
		mappingStrategy.setType(ReversalInwardDto.class);
		mappingStrategy.setColumnMapping(
				env.getProperty(config.getColumnMappingsKey()).split(","));

		StatefulBeanToCsvBuilder<ReversalInwardDto> builder = new StatefulBeanToCsvBuilder<>(
				writer);
		StatefulBeanToCsv<ReversalInwardDto> beanWriter = builder
				.withMappingStrategy(mappingStrategy)
				.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
				.withLineEnd(CSVWriter.DEFAULT_LINE_END)
				.withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER).build();

		writer.append(env.getProperty(config.getHeadersKey()));
		return beanWriter;
	}

	private void setDataSecurityAttribute(Gstr1ReviwSummReportsReqDto request,
			FileStatusDownloadReportEntity entity) {

		Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();

		String ProfitCenter = null;
		String plant = null;
		String purchase = null;
		String division = null;
		String location = null;
		String ud1 = null;
		String ud2 = null;
		String ud3 = null;
		String ud4 = null;
		String ud5 = null;
		String ud6 = null;
		String GSTIN = null;

		List<String> pcList = null;
		List<String> plantList = null;
		List<String> divisionList = null;
		List<String> locationList = null;
		List<String> purchaseList = null;
		List<String> ud1List = null;
		List<String> ud2List = null;
		List<String> ud3List = null;
		List<String> ud4List = null;
		List<String> ud5List = null;
		List<String> ud6List = null;

		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {
				if (key.equalsIgnoreCase(OnboardingConstant.PC)) {
					ProfitCenter = key;
					if (!dataSecAttrs.get(OnboardingConstant.PC).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.PC)
									.size() > 0) {
						pcList = dataSecAttrs.get(OnboardingConstant.PC);
					}
				}

				if (key.equalsIgnoreCase(OnboardingConstant.PLANT)) {

					plant = key;
					if (!dataSecAttrs.get(OnboardingConstant.PLANT).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.PLANT)
									.size() > 0) {
						plantList = dataSecAttrs.get(OnboardingConstant.PLANT);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.DIVISION)) {
					division = key;
					if (!dataSecAttrs.get(OnboardingConstant.DIVISION).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.DIVISION)
									.size() > 0) {
						divisionList = dataSecAttrs
								.get(OnboardingConstant.DIVISION);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.LOCATION)) {
					location = key;
					if (!dataSecAttrs.get(OnboardingConstant.LOCATION).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.LOCATION)
									.size() > 0) {
						locationList = dataSecAttrs
								.get(OnboardingConstant.LOCATION);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.PO)) {
					purchase = key;
					if (!dataSecAttrs.get(OnboardingConstant.PO).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.PO)
									.size() > 0) {
						purchaseList = dataSecAttrs.get(OnboardingConstant.PO);
					}
				}

				if (key.equalsIgnoreCase(OnboardingConstant.UD1)) {
					ud1 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD1) != null
							&& dataSecAttrs.get(OnboardingConstant.UD1)
									.size() > 0) {
						ud1List = dataSecAttrs.get(OnboardingConstant.UD1);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD2)) {
					ud2 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD2) != null
							&& dataSecAttrs.get(OnboardingConstant.UD2)
									.size() > 0) {
						ud2List = dataSecAttrs.get(OnboardingConstant.UD2);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD3)) {
					ud3 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD3) != null
							&& dataSecAttrs.get(OnboardingConstant.UD3)
									.size() > 0) {
						ud3List = dataSecAttrs.get(OnboardingConstant.UD3);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD4)) {
					ud4 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD4) != null
							&& dataSecAttrs.get(OnboardingConstant.UD4)
									.size() > 0) {
						ud4List = dataSecAttrs.get(OnboardingConstant.UD4);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD5)) {
					ud5 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD5) != null
							&& dataSecAttrs.get(OnboardingConstant.UD5)
									.size() > 0) {
						ud5List = dataSecAttrs.get(OnboardingConstant.UD5);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD6)) {
					ud6 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD6) != null
							&& dataSecAttrs.get(OnboardingConstant.UD6)
									.size() > 0) {
						ud6List = dataSecAttrs.get(OnboardingConstant.UD6);
					}
				}
			}
		}

		if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
			if (pcList != null && pcList.size() > 0) {
				entity.setProfitCenter(String.join(",", pcList));
			}
		}
		if (plant != null && !plant.isEmpty()) {
			if (plantList != null && plantList.size() > 0) {
				entity.setPlantCode(String.join(",", plantList));
			}
		}
		if (purchase != null && !purchase.isEmpty()) {
			if (purchaseList != null && purchaseList.size() > 0) {
				entity.setPurchaseOrg(String.join(",", purchaseList));
			}
		}
		if (division != null && !division.isEmpty()) {
			if (divisionList != null && divisionList.size() > 0) {
				entity.setDivision(String.join(",", divisionList));
			}
		}
		if (location != null && !location.isEmpty()) {
			if (locationList != null && locationList.size() > 0) {
				entity.setLocation(String.join(",", locationList));
			}
		}
		if (ud1 != null && !ud1.isEmpty()) {
			if (ud1List != null && ud1List.size() > 0) {
				entity.setUsrAcs1(String.join(",", ud1List));
			}
		}
		if (ud2 != null && !ud2.isEmpty()) {
			if (ud2List != null && ud2List.size() > 0) {
				entity.setUsrAcs2(String.join(",", ud2List));
			}
		}
		if (ud3 != null && !ud3.isEmpty()) {
			if (ud3List != null && ud3List.size() > 0) {
				entity.setUsrAcs3(String.join(",", ud3List));
			}
		}
		if (ud4 != null && !ud4.isEmpty()) {
			if (ud4List != null && ud4List.size() > 0) {
				entity.setUsrAcs4(String.join(",", ud4List));
			}
		}
		if (ud5 != null && !ud5.isEmpty()) {
			if (ud5List != null && ud5List.size() > 0) {
				entity.setUsrAcs5(String.join(",", ud5List));
			}
		}
		if (ud6 != null && !ud6.isEmpty()) {
			if (ud6List != null && ud6List.size() > 0) {
				entity.setUsrAcs6(String.join(",", ud6List));
			}
		}
		fileStatusDownloadReportRepo.save(entity);
	}
}
