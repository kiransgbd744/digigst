package com.ey.advisory.service.gstr1.hsn;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import org.apache.commons.io.FileUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CombineAndZipXlsxFiles;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.ReportStatusConstants;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("HsnReportDownloadServiceImpl")
public class HsnReportDownloadServiceImpl implements HsnReportDownloadService {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	CombineAndZipXlsxFiles combineAndZipXlsxFiles;

	private static final int CSV_BUFFER_SIZE = 8192;

	private static Integer chunkSize = 10000;

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;
	private static final String UPLOADED_FILENAME_MSG = "Uploaded file Name :'%s'";

	@Override
	public String getData(Long batchId, String reportType) {
		String fullPath = null;
		String uploadedDocName = null;
		Writer writer = null;

		File tempDir = null;
		try {
			tempDir = createTempDir(batchId);
		} catch (IOException e) {
			LOGGER.error("error while creating Temp Dir");
			throw new AppException();
		}

		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Get Error Report Details with batchId:'%s'", batchId);
				LOGGER.debug(msg);
			}
			
			Optional<FileStatusDownloadReportEntity> entity = fileStatusDownloadReportRepo
					.findById(batchId);
			String reportCateg = entity.get().getReportCateg();
			String fileName = "";
			if (reportType.equalsIgnoreCase("HSN_VERTICAL_TOTAL")) {
				fileName = reportCateg.equalsIgnoreCase(DownloadReportsConstant.GSTR1)
				           ? "GSTR1VerticleHSNUploadTotalRecords"
				           : "GSTR1AVerticleHSNUploadTotalRecords";

			} else if (reportType.equalsIgnoreCase("HSN_VERTICAL_PROCESSED")) {
				fileName = reportCateg.equalsIgnoreCase(DownloadReportsConstant.GSTR1)
				           ? "GSTR1VerticleHSNUploadProcessedRecords"
				           : "GSTR1AVerticleHSNUploadProcessedRecords";
			} else if (reportType.equalsIgnoreCase("HSN_VERTICAL_ERROR")) {
				fileName = reportCateg.equalsIgnoreCase(DownloadReportsConstant.GSTR1)
				           ? "GSTR1VerticleHSNUploadErrorRecords"
				           : "GSTR1AVerticleHSNUploadErrorRecords";
				
			}
			

			fullPath = tempDir.getAbsolutePath() + File.separator + fileName
					+ ".csv";

			// gstr1a code
			Integer noOfChunk = null;
			if (reportCateg.equalsIgnoreCase(DownloadReportsConstant.GSTR1))
				noOfChunk = getChunkNo("USP_INS_HSNVERTICAL_FILEUPLD",
						batchId, chunkSize, reportType);
			else
				noOfChunk = getGstr1aChunkNo("USP_INS_HSNVERTICAL_FILEUPLD_1A",
						batchId, chunkSize, reportType);

			noOfChunk = noOfChunk != null ? noOfChunk.intValue() : 0;

			if (noOfChunk == 0) {

				String msg = String.format(
						"no.of " + "chunk count is zero batchId:'%s'", batchId);
				LOGGER.error(msg);

				fileStatusDownloadReportRepo.updateStatus(batchId,
						"NO_DATA_FOUND", null, LocalDateTime.now());

				return null;
			}
			int j = 0;
			writer = new BufferedWriter(new FileWriter(fullPath),
					CSV_BUFFER_SIZE);

			String invoiceHeadersTemplate;
			String[] columnMappings;

			if (reportType.equalsIgnoreCase("HSN_VERTICAL_PROCESSED")) {
				invoiceHeadersTemplate = commonUtility
						.getProp("gstr1.hsn.vertical.processed.header.mapping");

				columnMappings = commonUtility
						.getProp("gstr1.hsn.vertical.processed.column.mapping")
						.split(",");
			} else {
				invoiceHeadersTemplate = commonUtility
						.getProp("gstr1.hsn.vertical.error.header.mapping");

				columnMappings = commonUtility
						.getProp("gstr1.hsn.vertical.error.column.mapping")
						.split(",");

			}

			StatefulBeanToCsv<HsnDownloadDto> beanWriter = getBeanWriter(
					columnMappings, writer);

			while (j < noOfChunk) {
				j++;
				List<Object[]> records=null;
				if (reportCateg.equalsIgnoreCase(DownloadReportsConstant.GSTR1))
				 records = getProcData(
						"USP_DISP_HSNVERTICAL_FILEUPLD", batchId, j,
						reportType);
				else
					 records = getProcData(
								"USP_DISP_HSNVERTICAL_FILEUPLD_1A", batchId, j,
								reportType);
				if (records != null && !records.isEmpty()) {

					List<HsnDownloadDto> reconDataList = records.stream()
							.map(o -> convertRowsToDto(o, reportType))
							.collect(Collectors.toCollection(ArrayList::new));

					writer.append(invoiceHeadersTemplate);
					beanWriter = getBeanWriter(columnMappings, writer);
					beanWriter.write(reconDataList);
					flushWriter(writer);
				}
			}
			File fPathFile = new File(fullPath);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(UPLOADED_FILENAME_MSG,
						uploadedDocName);
				LOGGER.debug(msg);
			}

			Pair<String, String> uploadedZipName = DocumentUtility
					.uploadFile(fPathFile, "Anx1FileStatusReport");
			String docId = uploadedZipName.getValue1();
			uploadedDocName = uploadedZipName.getValue0();
			if (LOGGER.isDebugEnabled()) {
				String msg = "Sucessfully uploaded zip file and updating the "
						+ "status as 'Report Generated'";
				LOGGER.debug(msg);
			}

			fileStatusDownloadReportRepo.updateStatus(batchId,
					ReportStatusConstants.REPORT_GENERATED, uploadedDocName,
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()),
					docId);

		} catch (Exception ex) {
			String errMsg = String
					.format("Error while creating Error report %s", batchId);

			LOGGER.error(errMsg, ex);
			throw new AppException(ex);

		} finally {
			deleteTemporaryDirectory(tempDir);
		}

		return uploadedDocName;
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

		String tempFolderPrefix = "SalesRegisterUpload" + "_" + batchId;
		return Files.createTempDirectory(tempFolderPrefix).toFile();
	}

	private void deleteTemporaryDirectory(File tempFile) {

		if (tempFile != null && tempFile.exists()) {
			try {
				FileUtils.deleteDirectory(tempFile);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(String.format(
							"Deleted the Temp directory/Folder '%s'",
							tempFile.getAbsolutePath()));
				}
			} catch (Exception ex) {
				String msg = String.format(
						"Failed to remove the temp "
								+ "directory created for zip: '%s'. This will "
								+ "lead to clogging of disk space.",
						tempFile.getAbsolutePath());
				LOGGER.error(msg, ex);
			}
		}

	}

	private HsnDownloadDto convertRowsToDto(Object[] arr, String reportType) {

		HsnDownloadDto obj = new HsnDownloadDto();

		obj.setSerialNo((arr[0] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[0].toString())
				: null);
		obj.setSupplierGstin((arr[1] != null) ? arr[1].toString() : null);
		obj.setReturnPeriod((arr[2] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[2].toString())
				: null);
		if (arr[3] != null) {
			obj.setHsnSac((!arr[3].toString().isEmpty())
					? DownloadReportsConstant.CSVCHARACTER
							.concat(arr[3].toString())
					: null);
		} else {
			obj.setHsnSac(null);
		}
		obj.setProductDescription((arr[4] != null) ? arr[4].toString() : null);
		obj.setUnitOfMeasurement((arr[5] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[5].toString())
				: null);
		obj.setQuantity((arr[6] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[6].toString())
				: null);
		obj.setRate((arr[7] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[7].toString())
				: null);
		obj.setTaxableValue((arr[8] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[8].toString())
				: null);
		obj.setIntegratedTaxAmount((arr[9] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[9].toString())
				: null);
		obj.setCentralTaxAmount(
				(arr[10] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[10].toString()) : null);
		obj.setStateUTTaxAmount(
				(arr[11] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[11].toString()) : null);
		obj.setCessAmount(
				(arr[12] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[12].toString()) : null);
		obj.setTotalValue(
				(arr[13] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[13].toString()) : null);
		obj.setFileName((arr[14] != null) ? arr[14].toString() : null);
		obj.setUploadedBy((arr[15] != null) ? arr[15].toString() : null);

		if (arr[16] != null) {
			obj.setUploadedDateAndTime(DownloadReportsConstant.CSVCHARACTER
					.concat(arr[16].toString()));
		} else {
			obj.setUploadedDateAndTime(null);
		}

		if (!reportType.equalsIgnoreCase("HSN_VERTICAL_PROCESSED")) {
			obj.setErrorCode((arr[17] != null) ? arr[17].toString() : null);
			obj.setErrorDescription(
					(arr[18] != null) ? arr[18].toString() : null);
			obj.setRecordType((arr[19] != null) ? arr[19].toString() : null);

		} else {
			obj.setRecordType((arr[17] != null) ? arr[17].toString() : null);
		}

				return obj;

	}

	private Integer getChunkNo(String procName, Long downloadId,
			Integer chunkSize, String reportType) {

		StoredProcedureQuery storedProc = entityManager
				.createStoredProcedureQuery(procName);

		storedProc.registerStoredProcedureParameter("P_REPORT_DOWNLOAD_ID",
				Long.class, ParameterMode.IN);

		storedProc.setParameter("P_REPORT_DOWNLOAD_ID", downloadId);

		storedProc.registerStoredProcedureParameter("P_CHUNK_SIZE",
				Integer.class, ParameterMode.IN);

		storedProc.setParameter("P_CHUNK_SIZE", chunkSize);

		storedProc.registerStoredProcedureParameter("P_REPORT_TYPE",
				String.class, ParameterMode.IN);

		storedProc.setParameter("P_REPORT_TYPE", reportType);

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
			Integer chunkValue, String reportType) {

		StoredProcedureQuery storedProc = entityManager
				.createStoredProcedureQuery(procName);

		storedProc.registerStoredProcedureParameter("P_REPORT_DOWNLOAD_ID",
				Long.class, ParameterMode.IN);

		storedProc.setParameter("P_REPORT_DOWNLOAD_ID", downloadId);

		storedProc.registerStoredProcedureParameter("P_CHUNK_VALUE",
				Integer.class, ParameterMode.IN);

		storedProc.setParameter("P_CHUNK_VALUE", chunkValue);

		storedProc.registerStoredProcedureParameter("P_REPORT_TYPE",
				String.class, ParameterMode.IN);

		storedProc.setParameter("P_REPORT_TYPE", reportType);

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

	private StatefulBeanToCsv<HsnDownloadDto> getBeanWriter(
			String[] columnMappings, Writer writer) {
		ColumnPositionMappingStrategy<HsnDownloadDto> mappingStrategy = new ColumnPositionMappingStrategy<>();
		mappingStrategy.setType(HsnDownloadDto.class);
		mappingStrategy.setColumnMapping(columnMappings);
		StatefulBeanToCsvBuilder<HsnDownloadDto> builder = new StatefulBeanToCsvBuilder<>(
				writer);
		StatefulBeanToCsv<HsnDownloadDto> beanWriter = builder
				.withMappingStrategy(mappingStrategy)
				.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
				.withLineEnd(CSVWriter.DEFAULT_LINE_END)
				.withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER).build();
		return beanWriter;
	}
	
	private Integer getGstr1aChunkNo(String procName, Long downloadId,
			Integer chunkSize, String reportType) {

		StoredProcedureQuery storedProc = entityManager
				.createStoredProcedureQuery(procName);

		storedProc.registerStoredProcedureParameter("P_REPORT_DOWNLOAD_ID",
				Long.class, ParameterMode.IN);

		storedProc.setParameter("P_REPORT_DOWNLOAD_ID", downloadId);

		storedProc.registerStoredProcedureParameter("P_CHUNK_SIZE",
				Integer.class, ParameterMode.IN);

		storedProc.setParameter("P_CHUNK_SIZE", chunkSize);

		storedProc.registerStoredProcedureParameter("P_REPORT_TYPE",
				String.class, ParameterMode.IN);

		storedProc.setParameter("P_REPORT_TYPE", reportType);

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
}
