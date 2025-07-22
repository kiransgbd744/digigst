package com.ey.advisory.app.services.vendorcomm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import org.apache.commons.io.FileUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.aspose.cells.Cell;
import com.aspose.cells.Cells;
import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.anx2.vendorsummary.AsyncVendorCommSummaryReportHelperServiceImpl;
import com.ey.advisory.app.anx2.vendorsummary.VendorCommSummaryReportDto;
import com.ey.advisory.app.data.entities.client.asprecon.VendorJsonCommRequestEntity;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorCommRequestRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorJsonCommRequestRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorMasterConfigEntityRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorMasterUploadEntityRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorReqVendorGstinRepository;
import com.ey.advisory.app.docs.dto.VendorCommReportGenDto;
import com.ey.advisory.app.report.convertor.VendorCommReportConverter;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CombineAndZipReportFiles;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.CompressAndZipXlsxFiles;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.ReconStatusConstants;
import com.ey.advisory.common.ReportConfigFactory;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.ReportTypeConstants;
import com.ey.advisory.common.zip.DirAndFilesCompressor;
import com.ey.advisory.core.config.ConfigConstants;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("AsyncVendorCommReportUploadServiceImpl")
public class AsyncVendorCommReportUploadServiceImpl
		implements AsyncVendorCommReportUploadService {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	private Environment env;

	@Autowired
	@Qualifier("VendorCommRequestRepository")
	private VendorCommRequestRepository vendorCommRequestRepository;

	@Autowired
	@Qualifier("VendorReqVendorGstinRepository")
	private VendorReqVendorGstinRepository vendorReqVendorGstinRepository;

	@Autowired
	@Qualifier("ReportConfigFactoryImpl")
	ReportConfigFactory reportConfigFactory;

	@Autowired
	CombineAndZipReportFiles combineAndZipReportFiles;

	@Autowired
	@Qualifier("VendorCommunicationReportConverterImpl")
	VendorCommReportConverter vendorCommReportConverter;

	@Autowired
	@Qualifier("VendorMasterConfigEntityRepository")
	private VendorMasterConfigEntityRepository vendorConfigGstinDetailsRepository;

	@Autowired
	VendorMasterUploadEntityRepository vendorMasterUploadEntityRepository;

	@Autowired
	@Qualifier("VendorCommDAOImpl")
	private VendorCommDAO vendorCommDAO;

	@Autowired
	CompressAndZipXlsxFiles compressAndZipXlsxFiles;

	@Autowired
	AsyncVendorCommSummaryReportHelperServiceImpl summaryReportService;

	@Autowired
	@Qualifier("entityInfoRepository")
	EntityInfoRepository entityInfoRepo;

	@Autowired
	private GSTNDetailRepository gSTNDetailRepository;

	@Autowired
	private VendorJsonCommRequestRepository vendorJsonCommRequestRepository;

	@Autowired
	@Qualifier("DirAndFilesCompressorImpl")
	private DirAndFilesCompressor compressor;

	@Autowired
	private Vendor2wayserviceImpl reqIdService;

	private static int maxLimitJsonFile = 20000;

	@Override
	public void generateVendorCommReports(Long reqId, boolean isAPOpted) {
		Writer writer = null;
		File tempDir = null;
		try {
			tempDir = createTempDir("DownloadVenorCommReports");

			if (LOGGER.isDebugEnabled()) {
				String msg = "Created temporary directory";
				LOGGER.debug(msg);
			}

		/*	Map<String, String> vGstinNameGstr2AMap = new HashMap<>();
			Map<String, String> vGstinNamePRMap = new HashMap<>();
		*/	Map<String, String> vGstinNameVendMasterMap = new HashMap<>();
			Map<String, String> vGstinNameVendMasterUploadTableMap = new HashMap<>();

		/*	List<Object[]> vendorGstinNameGstr2AList = new ArrayList<>();
		*/	List<Object[]> vendorGstinNameVendMasterList = new ArrayList<>();
		/*	List<Object[]> vendorGstinNamePRList = new ArrayList<>();
*/
			List<Object[]> vGstinNameVendMasterUploadTableList = new ArrayList<>();
			List<String> vendorGstinList = vendorReqVendorGstinRepository
					.getVendorGstin(reqId);

			List<List<String>> chunks = Lists.partition(vendorGstinList, 2000);
			for (List<String> chunk : chunks) {
				LOGGER.debug("Inside Chunk Method");

/*				vendorGstinNameGstr2AList.addAll(vendorGstinDetailsRepository
						.getVendorGstinAndVendorTradeName(chunk));
*/				vendorGstinNameVendMasterList
						.addAll(vendorConfigGstinDetailsRepository
								.getLegalNameByVendorGstin(chunk));

				vGstinNameVendMasterUploadTableList
					.addAll(vendorMasterUploadEntityRepository
								.getVendorGstinAndVendorName(chunk));

			/*	vendorGstinNamePRList
						.addAll(vendorCommDAO.getVendorNamePR(chunk));
*/
			}

		
			vGstinNameVendMasterMap = convert(vendorGstinNameVendMasterList);
			vGstinNameVendMasterUploadTableMap = convert(vGstinNameVendMasterUploadTableList);
		
			for (String vGstin : vendorGstinList) {
				try {
					String vendorName = vGstinNameVendMasterMap.containsKey(vGstin)?vGstinNameVendMasterMap.get(vGstin):"";
					
				/*	if (Strings.isNullOrEmpty(vendorName)) {

						vendorName = vGstinNameVendMasterUploadTableMap.get(vGstin);
					}
					*/
					if (!Strings.isNullOrEmpty(vendorName)) {
						vendorName = vendorName.replaceAll("[./]", "_");
					}

					Pair<List<VendorCommSummaryReportDto>, String> pair = summaryReportService
							.getSummaryDataFromSPandConvertToDto(reqId, vGstin,
									isAPOpted);
					String excelPath = null;
					if (pair.getValue0() != null) {
						excelPath = writeToExcel(pair.getValue0(), reqId,
								vGstin, vendorName, pair.getValue1(), tempDir);
					}
					writetoCsv(reqId, vGstin, writer, tempDir, vendorName,
							isAPOpted, excelPath);

				} catch (Exception e) {

					vendorReqVendorGstinRepository.updateReportStatus(reqId,
							ReportStatusConstants.REPORT_GENERATION_FAILED,
							null, EYDateUtil.toUTCDateTimeFromLocal(
									LocalDateTime.now()),
							vGstin);
					String msg = "Exception occued while generating csv file for:"
							+ vGstin;
					LOGGER.error(msg, e);
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
						ReportTypeConstants.VENDOR_COMM, null, reqId);

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format("ZipFile name : %s",
							zipFileName);
					LOGGER.debug(msg);
				}

				File zipFile = new File(tempDir, zipFileName);

				// String uploadedDocName =
				// DocumentUtility.uploadZipFile(zipFile,
				// "VendorCommReport");

				Pair<String, String> uploadedDocNameWithDocId = DocumentUtility
						.uploadFile(zipFile, "VendorCommReport");
				String uploadedFileName = uploadedDocNameWithDocId.getValue0();
				String docId = uploadedDocNameWithDocId.getValue1();

				if (LOGGER.isDebugEnabled()) {
					String msg = "Sucessfully uploaded zip file and updating the "
							+ "status as 'Report Generated'";
					LOGGER.debug(msg);
				}

				vendorCommRequestRepository.updateStatus(reqId,
						ReportStatusConstants.REPORT_GENERATED,
						uploadedFileName, LocalDateTime.now(), docId);

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"calling azure api to accept the reqId for {} : %s",
							reqId);
					LOGGER.debug(msg);
				}

				String azureResponse = reqIdService.vendorReportReqId(reqId);

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"azure response we receiving : %s", azureResponse);
					LOGGER.debug(msg);
				}

				if (!azureResponse.isEmpty()) {
					JsonObject respObject = (new JsonParser())
							.parse(azureResponse).getAsJsonObject();

					String status = respObject.get("status_cd").getAsString();
					if ("1".equals(status)) {

						vendorCommRequestRepository.updatePrepStatus(reqId,
								ReconStatusConstants.VENDR_REQID_COMPLETED);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"Azure call with reqId is successfull for {}",
									reqId);
						}
					} else {
						vendorCommRequestRepository.updatePrepStatus(reqId,
								ReconStatusConstants.VENDR_REQID_FAILED);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"Azure call with reqId is not successfull for {} with response{}",
									reqId, azureResponse);
						}
					}

				}
			} else {

				LOGGER.error("No Data found for report id : %d", reqId);
				vendorCommRequestRepository.updateStatus(reqId,
						ReportStatusConstants.NO_DATA_FOUND, null,
						LocalDateTime.now());
			}

		} catch (Exception e) {

			vendorCommRequestRepository.updateStatus(reqId,
					ReportStatusConstants.REPORT_GENERATION_FAILED, null,
					LocalDateTime.now());

			String msg = "Exception occued while generating csv file for reqID"
					+ reqId;
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

	private static File createTempDir(String uploadFolder) throws IOException {
		return Files.createTempDirectory(uploadFolder).toFile();
	}

	@SuppressWarnings("unchecked")
	private void writetoCsv(Long reqId, String vendorGstin, Writer writer,
			File tempDir, String vName, boolean isAPOpted, String excelPath) {

		List<Object[]> list = null;
		List<VendorCommReportGenDto> responseFromDao = null;
		String procName = null;

		try {

			if (isAPOpted) {
				procName = "vendorCommReportDataAPOpted";
			} else {
				procName = "vendorCommReportDataNonAP";
			}

			StoredProcedureQuery reportDataProc = entityManager
					.createNamedStoredProcedureQuery(procName);

			reportDataProc.setParameter("P_REQUEST_ID", reqId);

			reportDataProc.setParameter("P_VENDOR_GSTIN", vendorGstin);

			list = reportDataProc.getResultList();

			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("Executed Stored proc to get Data and "
								+ "got resultset of size: %d", list.size());
				LOGGER.debug(msg);
			}

			if (!list.isEmpty()) {

				String csvPath = tempDir.getAbsolutePath() + File.separator
						+ vendorGstin + "_" + vName + ".csv";

				String zipFileName = vendorGstin + "_" + vName + ".zip";

				writer = new BufferedWriter(new FileWriter(csvPath), 8192);

				responseFromDao = list.stream()
						.map(o -> (VendorCommReportGenDto) vendorCommReportConverter
								.convert(o, false))
						.collect(Collectors.toCollection(ArrayList::new));

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Record count after converting object array to DTO %d ",
							responseFromDao.size());
					LOGGER.debug(msg);
				}

				StatefulBeanToCsv<VendorCommReportGenDto> beanWriter = getBeanWriter(
						writer);
				beanWriter.write(responseFromDao);

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

				Pair<String, String> uploadedDocNameDocId = zipAndUploadVendorCSV(
						csvPath, excelPath, tempDir, zipFileName);
				String uploadedDocName = uploadedDocNameDocId.getValue0();
				String docId = uploadedDocNameDocId.getValue1();
				vendorReqVendorGstinRepository.updateReportStatus(reqId,
						ReportStatusConstants.REPORT_GENERATED, uploadedDocName,
						LocalDateTime.now(), vendorGstin, docId);

				int noOfJsonfiles = dtoToJsonFile(list, reqId, vendorGstin);
				vendorReqVendorGstinRepository.updateCounts(noOfJsonfiles,
						responseFromDao.size(), reqId, vendorGstin);

			} else {
				vendorReqVendorGstinRepository.updateReportStatus(reqId,
						ReportStatusConstants.NO_DATA_FOUND, null,
						LocalDateTime.now(), vendorGstin);
				vendorReqVendorGstinRepository.updateCounts(0, 0, reqId,
						vendorGstin);
			}
		} catch (Exception ex) {
			String msg = "Exception occured while fetching data from get ChunkData";
			LOGGER.error(msg);
			throw new AppException(msg, ex);
		}

	}

	private String writeToExcel(List<VendorCommSummaryReportDto> reportDtos,
			Long reqId, String vendorGstin, String vName, String recipientGstin,
			File tempDir) {
		Workbook workbook = null;
		int startRow = 0;
		int startcolumn = 0;
		boolean isHeaderRequired = false;
		try {

			String[] invoiceHeaders = commonUtility
					.getProp("vendor.comm.summary.report.data").split(",");

			workbook = commonUtility.createWorkbookWithExcelTemplate(
					"ReportTemplates", "VendorCommSummaryReportTemplate.xlsx");

			if (LOGGER.isDebugEnabled()) {
				String msg = "AsyncVendorCommReportUploadServiceImpl."
						+ "writeToExcel workbook created writing data "
						+ "to the workbook";
				LOGGER.debug(msg);
			}
			Map<String, Pair<Integer, Integer>> map = getVendorCommSummaryReportMap();
			Worksheet sheet = workbook.getWorksheets().get(1);
			Cells reportCells = sheet.getCells();
			populateEntityDetails(sheet, vendorGstin, vName, recipientGstin);
			for (VendorCommSummaryReportDto dto : reportDtos) {
				if (map.containsKey(dto.getReportType())) {
					Pair<Integer, Integer> pair = map.get(dto.getReportType());
					startRow = pair.getValue0();
					startcolumn = pair.getValue1();
					if (!"TOTAL".equalsIgnoreCase(dto.getReportType())) {
						reportCells.deleteRows(startRow, 1);
					}
					if (dto.getReportType().equalsIgnoreCase(
							"Doc Number & Doc Date Mismatch")) {
						dto.setReportType("Doc No & Doc Date Mismatch");
					}
					reportCells.importCustomObjects(Arrays.asList(dto),
							invoiceHeaders, isHeaderRequired, startRow,
							startcolumn, reportDtos.size(), true, "yyyy-mm-dd",
							false);
				}
			}
			if (LOGGER.isDebugEnabled()) {
				String msg = "AsyncVendorCommReportUploadServiceImpl.writeToExcel "
						+ "saving workbook";
				LOGGER.debug(msg);
			}
			String excelPath = tempDir.getAbsolutePath() + File.separator
					+ vendorGstin + "_" + vName + "_" + "Read me" + "_"
					+ ConfigConstants.VENDORCOMM_SUMMARY_REPORT + ".xlsx";
			workbook.save(excelPath, SaveFormat.XLSX);
			return excelPath;
		} catch (Exception ex) {
			String msg = "Exception occured while saving to workbook";
			LOGGER.error(msg);
			throw new AppException(msg, ex);
		}

	}

	private void populateEntityDetails(Worksheet sheet, String vendorGstin,
			String vendorName, String recipientGstin) {
		String entityName = null;
		if (recipientGstin != null) {
			String rGstin = recipientGstin.substring(0, 15);
			Long entityId = gSTNDetailRepository.findEntityIdByGstin(rGstin);
			Optional<EntityInfoEntity> optional = entityInfoRepo
					.findById(entityId);
			EntityInfoEntity entity = optional.get();
			entityName = entity.getEntityName();
		}
		Cell cellc4 = sheet.getCells().get("C4");
		cellc4.setValue(entityName);
		Cell cellc5 = sheet.getCells().get("C5");
		cellc5.setValue(recipientGstin);
		Cell cellf4 = sheet.getCells().get("F4");
		cellf4.setValue(vendorName);
		Cell cellf5 = sheet.getCells().get("F5");
		cellf5.setValue(vendorGstin);

	}

	private StatefulBeanToCsv<VendorCommReportGenDto> getBeanWriter(
			Writer writer) throws Exception {

		if (!env.containsProperty("vendorComm.report.column")
				|| !env.containsProperty("vendorComm.report.header")) {
			String msg = "HeaderKey or ColumnMapping key Not found";
			LOGGER.error(msg);
			throw new AppException(msg);
		}

		ColumnPositionMappingStrategy<VendorCommReportGenDto> mappingStrategy = new ColumnPositionMappingStrategy<>();
		mappingStrategy.setType(VendorCommReportGenDto.class);
		mappingStrategy.setColumnMapping(
				env.getProperty("vendorComm.report.column").split(","));

		StatefulBeanToCsvBuilder<VendorCommReportGenDto> builder = new StatefulBeanToCsvBuilder<>(
				writer);
		StatefulBeanToCsv<VendorCommReportGenDto> beanWriter = builder
				.withMappingStrategy(mappingStrategy)
				.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
				.withLineEnd(CSVWriter.DEFAULT_LINE_END)
				.withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER).build();

		writer.append(env.getProperty("vendorComm.report.header"));
		return beanWriter;
	}

	private Map<String, String> convert(List<Object[]> vendorGstinList) {

		Map<String, String> vGstinMap = new HashMap<>();
		vendorGstinList.forEach(o -> {
			vGstinMap.put((String) o[0], (String) o[1]);
		});

		return vGstinMap;
	}

	private Pair<String, String> zipAndUploadVendorCSV(String csvPath,
			String excelPath, File tempDir, String compressedFileName) {

		List<String> filesToZip = new ArrayList<>();
		filesToZip.add(csvPath);
		if (excelPath != null) {
			filesToZip.add(excelPath);
		}
		compressor.compressFiles(tempDir.getAbsolutePath(), compressedFileName,
				filesToZip);

		String zipPath = tempDir.getAbsolutePath() + File.separator
				+ compressedFileName;
		File zipFile = new File(zipPath);

		// String uploadedDocName = DocumentUtility.uploadZipFile(zipFile,
		// "VendorCommReport");

		Pair<String, String> uploadedDocNameDocId = DocumentUtility
				.uploadFile(zipFile, "VendorCommReport");

		if (zipFile != null && zipFile.exists()) {
			try {
				FileUtils.forceDelete(zipFile);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(String.format(
							"Deleted the Temp directory/Folder '%s'",
							zipFile.getAbsolutePath()));
				}
			} catch (Exception ex) {
				String msg = String.format(
						"Failed to remove the temp "
								+ "directory created for zip: '%s'. This will "
								+ "lead to clogging of disk space.",
						zipFile.getAbsolutePath());
				LOGGER.error(msg, ex);
			}
		}

		return uploadedDocNameDocId;
	}

	public static Map<String, Pair<Integer, Integer>> getVendorCommSummaryReportMap() {
		Map<String, Pair<Integer, Integer>> map = new LinkedHashMap<>();
		map.put("Exact Match", new Pair<>(10, 1));
		map.put("Match With Tolerance", new Pair<>(11, 1));
		map.put("Value Mismatch", new Pair<>(12, 1));
		map.put("POS Mismatch", new Pair<>(13, 1));
		map.put("Doc Date Mismatch", new Pair<>(14, 1));
		map.put("Doc Type Mismatch", new Pair<>(15, 1));
		map.put("Doc No Mismatch I", new Pair<>(16, 1));
		map.put("Doc No Mismatch II", new Pair<>(17, 1));
		map.put("Doc No & Doc Date Mismatch", new Pair<>(18, 1));
		map.put("Multi-Mismatch", new Pair<>(19, 1));
		map.put("Potential-I", new Pair<>(20, 1));
		map.put("Potential-II", new Pair<>(21, 1));
		map.put("Logical Match", new Pair<>(22, 1));
		map.put("Addition in PR", new Pair<>(23, 1));
		map.put("Addition in 2A", new Pair<>(24, 1));
		map.put("TOTAL", new Pair<>(25, 1));
		return map;
	}

	private int dtoToJsonFile(List<Object[]> list, Long reqId,
			String vendorGstin) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("inside json file creation");
			LOGGER.debug(msg);
		}
		List<VendorCommReportGenDto> responseFromDao = new ArrayList<>();

		responseFromDao = list.stream()
				.map(o -> (VendorCommReportGenDto) vendorCommReportConverter
						.convert(o, true))
				.collect(Collectors.toCollection(ArrayList::new));

		File tempDir = null;
		try {
			tempDir = createTempDir("DownloadVenorCommJsonReports");
			int fileIndex = 0;
			Gson gson = new GsonBuilder().setPrettyPrinting()
					.disableHtmlEscaping().create();
			List<List<VendorCommReportGenDto>> chunks = Lists
					.partition(responseFromDao, maxLimitJsonFile);
			List<VendorJsonCommRequestEntity> jsonEntity = new ArrayList<>();

			for (List<VendorCommReportGenDto> chunk : chunks) {
				VendorJsonCommRequestEntity entity = new VendorJsonCommRequestEntity();

				fileIndex++;
				String jsonPath = tempDir.getAbsolutePath() + File.separator
						+ vendorGstin + "_" + reqId + "_" + fileIndex + ".json";

				BufferedWriter writer = Files
						.newBufferedWriter(Paths.get(jsonPath));

				String jsonString = gson.toJson(chunk);
				writer.write(jsonString);

				writer.flush();
				writer.close();

				Pair<String, String> uploadedFileName = uploadFile(jsonPath,
						vendorGstin, reqId);
				entity.setRequestId(reqId);
				entity.setVendorGstin(vendorGstin);
				entity.setFilePath(uploadedFileName.getValue0());
				entity.setDocId(uploadedFileName.getValue1());
				jsonEntity.add(entity);
			}

			vendorJsonCommRequestRepository.saveAll(jsonEntity);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("json file created for reqId {}",
						reqId);
				LOGGER.debug(msg);
			}
			return chunks.size();

		} catch (Exception ex) {
			String msg = "Exception occured while saving json file";
			LOGGER.error(msg);
			throw new AppException();
		} finally {
			GenUtil.deleteTempDir(tempDir);
		}
	}

	private Pair<String, String> uploadFile(String jsonPath, String vendorGstin,
			Long reqId) {
		File tempFile = null;
		try {
			tempFile = new File(jsonPath);

			Pair<String, String> uploadedDocName = DocumentUtility
					.uploadFile(tempFile, "VendorCommJsonReport");
			/*
			 * String uploadedDocName = DocumentUtility.uploadCsvFile(tempFile,
			 * "VendorCommJsonReport");
			 */
			return uploadedDocName;
		} catch (Exception e) {
			String msg = String.format(
					"Error While Uploading the json File to DocRepo %s",
					tempFile.getAbsoluteFile());
			LOGGER.error(msg);
			throw new AppException(msg);
		}
	}

}
