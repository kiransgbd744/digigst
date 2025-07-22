package com.ey.advisory.service.gstr1.sales.register;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("GSTR1SalesRegisterReportDownloadServiceImpl")
public class GSTR1SalesRegisterReportDownloadServiceImpl
		implements GSTR1SalesRegisterReportDownloadService {

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

			String fileName = "Sales_Register_" + reportType + "_Report";

			fullPath = tempDir.getAbsolutePath() + File.separator + fileName
					+ ".csv";

			Integer noOfChunk = getChunkNo("USP_SRFILEUPLOAD_INS_CHUNK",
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

			if (reportType.equalsIgnoreCase("Processed")) {
				invoiceHeadersTemplate = commonUtility.getProp(
						"gstr1.sales.register.processed.header.mapping");

				columnMappings = commonUtility
						.getProp(
								"gstr1.sales.register.processed.column.mapping")
						.split(",");
			} else {
				invoiceHeadersTemplate = commonUtility
						.getProp("gstr1.sales.register.error.header.mapping");

				columnMappings = commonUtility
						.getProp("gstr1.sales.register.error.column.mapping")
						.split(",");

			}

			StatefulBeanToCsv<Gstr1StagingRegisterDownloadDto> beanWriter = getBeanWriter(
					columnMappings, writer);

			while (j < noOfChunk) {
				j++;

				List<Object[]> records = getProcData("USP_SRFILE_DISP_CHUNK",
						batchId, j, reportType);

				if (records != null && !records.isEmpty()) {

					List<Gstr1StagingRegisterDownloadDto> reconDataList = records
							.stream().map(o -> convertRowsToDto(o, reportType))
							.collect(Collectors.toCollection(ArrayList::new));

					writer.append(invoiceHeadersTemplate);
					beanWriter = getBeanWriter(columnMappings, writer);
					beanWriter.write(reconDataList);
					flushWriter(writer);
				}
			}

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(UPLOADED_FILENAME_MSG,
						uploadedDocName);
				LOGGER.debug(msg);
			}
			File fPathFile = new File(fullPath);
			Pair<String, String> uploadedZipName = DocumentUtility
					.uploadFile(fPathFile, "Anx1FileStatusReport");
			uploadedDocName = uploadedZipName.getValue0();
			String docId = uploadedZipName.getValue1();
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

	private Gstr1StagingRegisterDownloadDto convertRowsToDto(Object[] arr,
			String reportType) {

		Gstr1StagingRegisterDownloadDto obj = new Gstr1StagingRegisterDownloadDto();

		obj.setReturnPeriod((arr[0] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[0].toString())
				: null);
		obj.setBusinessPlace((arr[1] != null) ? arr[1].toString() : null);
		obj.setCustomerGstin((arr[2] != null) ? arr[2].toString() : null);
		obj.setDocumentType((arr[3] != null) ? arr[3].toString() : null);
		obj.setSupplyType((arr[4] != null) ? arr[4].toString() : null);
		obj.setDocumentNumber((arr[5] != null) ? arr[5].toString() : null);
		obj.setDocumentDate((arr[6] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[6].toString())
				: null);
		obj.setItemSerialNumber((arr[7] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[7].toString())
				: null);
		obj.setHsnSac((arr[8] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[8].toString())
				: null);
		obj.setTaxRate((arr[9] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[9].toString())
				: null);
		obj.setItemAssessableValue(
				(arr[10] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[10].toString()) : null);
		obj.setIgst((arr[11] != null) ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[11].toString()) : null);
		obj.setCgst((arr[12] != null) ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[12].toString()) : null);
		obj.setSgst((arr[13] != null) ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[13].toString()) : null);
		obj.setAdvaloremCess(
				(arr[14] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[14].toString()) : null);
		obj.setSpecificCess(
				(arr[15] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[15].toString()) : null);
		obj.setInvoiceValue(
				(arr[16] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[16].toString()) : null);
		obj.setPos((arr[17] != null) ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[17].toString()) : null);
		obj.setTransactionType((arr[18] != null) ? arr[18].toString() : null);
		obj.setFileName((arr[19] != null) ? arr[19].toString() : null);
		obj.setDateTime((arr[20] != null) ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[20].toString().substring(0, 19)) : null);
		if (!reportType.equalsIgnoreCase("Processed")) {
			obj.setErrorDescription(
					(arr[21] != null) ? arr[21].toString() : null);
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

		storedProc.registerStoredProcedureParameter("P_CHUNK_NUM",
				Integer.class, ParameterMode.IN);

		storedProc.setParameter("P_CHUNK_NUM", chunkValue);

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

	private StatefulBeanToCsv<Gstr1StagingRegisterDownloadDto> getBeanWriter(
			String[] columnMappings, Writer writer) {
		ColumnPositionMappingStrategy<Gstr1StagingRegisterDownloadDto> mappingStrategy = new ColumnPositionMappingStrategy<>();
		mappingStrategy.setType(Gstr1StagingRegisterDownloadDto.class);
		mappingStrategy.setColumnMapping(columnMappings);
		StatefulBeanToCsvBuilder<Gstr1StagingRegisterDownloadDto> builder = new StatefulBeanToCsvBuilder<>(
				writer);
		StatefulBeanToCsv<Gstr1StagingRegisterDownloadDto> beanWriter = builder
				.withMappingStrategy(mappingStrategy)
				.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
				.withLineEnd(CSVWriter.DEFAULT_LINE_END)
				.withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER).build();
		return beanWriter;
	}

}
