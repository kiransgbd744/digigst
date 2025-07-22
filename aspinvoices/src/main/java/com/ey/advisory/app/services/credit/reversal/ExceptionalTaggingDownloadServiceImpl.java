package com.ey.advisory.app.services.credit.reversal;

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
import com.ey.advisory.core.config.ConfigConstants;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("ExceptionalTaggingDownloadServiceImpl")
public class ExceptionalTaggingDownloadServiceImpl
		implements ExceptionalTaggingDownloadService {

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
	public void getData(Long batchId, String reportType) {
		String fullPath = null;
		Pair<String,String> uploadedZipName = null;
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
			String reqReportType=null;
			if(reportType.equalsIgnoreCase("processed")){
				reqReportType="Processed Records";
			}else if(reportType.equalsIgnoreCase("error")){
				reqReportType="Error Records";
			}else if(reportType.equalsIgnoreCase("totalrecords")){
				reqReportType="Total Records";
			}
			String fileName = "Common Credit Reversal_Exceptional Tagging_" + reqReportType + " Report";
			fullPath = tempDir.getAbsolutePath() + File.separator + fileName
					+ ".csv";
			
			Integer noOfChunk = getChunkNo("USP_COM_CRE_REV_INS_CHUNK",
					batchId, chunkSize);

			noOfChunk = noOfChunk != null ? noOfChunk.intValue() : 0;

			if (noOfChunk == 0) {

				String msg = String.format(
						"no.of " + "chunk count is zero batchId:'%s'", batchId);
				LOGGER.error(msg);

				fileStatusDownloadReportRepo.updateStatus(batchId,
						"NO_DATA_FOUND", null, LocalDateTime.now());

			
			}
			int j = 0;
			writer = new BufferedWriter(new FileWriter(fullPath),
					CSV_BUFFER_SIZE);

			String invoiceHeadersTemplate;
			String[] columnMappings;

			if (reportType.equalsIgnoreCase("processed")) {
				invoiceHeadersTemplate = commonUtility.getProp(
						"exceptional.tagging.processed.header.mapping");

				columnMappings = commonUtility
						.getProp(
								"exceptional.tagging.processed.column.mapping")
						.split(",");
			} else {
				invoiceHeadersTemplate = commonUtility
						.getProp("exceptional.tagging.error.header.mapping");

				columnMappings = commonUtility
						.getProp("exceptional.tagging.error.column.mapping")
						.split(",");

			}

			StatefulBeanToCsv<ExceptionalTaggingDetailsDto> beanWriter = getBeanWriter(
					columnMappings, writer);

			while (j < noOfChunk) {
				j++;

				List<Object[]> records = getProcData("USP_COM_CRE_REV_DISP_CHUNK",
						batchId, j);

				if (records != null && !records.isEmpty()) {

					List<ExceptionalTaggingDetailsDto> dataList = records
							.stream().map(o -> convertRowsToDto(o, reportType))
							.collect(Collectors.toCollection(ArrayList::new));

					writer.append(invoiceHeadersTemplate);
					beanWriter = getBeanWriter(columnMappings, writer);
					beanWriter.write(dataList);
					flushWriter(writer);
				}
			}
			File fPathFile = new File(fullPath);

			uploadedZipName = DocumentUtility.uploadFile(fPathFile,
					ConfigConstants.EXCEPTIONAL_TAGGING_REPORT);
			
			String uploadedDocName = uploadedZipName.getValue0();
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
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(UPLOADED_FILENAME_MSG,
						uploadedDocName);
				LOGGER.debug(msg);
			}
			deleteTemporaryDirectory(tempDir);

		} catch (Exception ex) {
			String errMsg = String
					.format("Error while creating Error report %s", batchId);

			LOGGER.error(errMsg, ex);
			deleteTemporaryDirectory(tempDir);
			throw new AppException(ex);

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

		String tempFolderPrefix = "ExceptionalTaggingUpload" + "_" + batchId;
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

	private ExceptionalTaggingDetailsDto convertRowsToDto(Object[] arr,
			String reportType) {

		ExceptionalTaggingDetailsDto obj = new ExceptionalTaggingDetailsDto();

		obj.setCustomerGSTIN((arr[0] != null) ? arr[0].toString() : null);
		obj.setDocumentType((arr[1] != null) ? arr[1].toString() : null);
		obj.setDocumentNumber((arr[2] != null) ? arr[2].toString() : null);
		obj.setDocumentDate((arr[3] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[3].toString())
				: null);
		obj.setSupplierGSTIN((arr[4] != null) ? arr[4].toString() : null);
		obj.setItemSerialNumber((arr[5] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[5].toString())
				: null);
		obj.setCommonSupplyIndicator((arr[6] != null) ? arr[6].toString() : null);
		obj.setReturnPeriod((arr[7] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[7].toString())
				: null);
		obj.setFileID((arr[8] != null) ? arr[8].toString() : null);
		obj.setFileName((arr[9] != null) ? arr[9].toString() : null);
		if (!reportType.equalsIgnoreCase("processed")) {
			obj.setErrorCode((arr[10] != null) ? arr[10].toString() : null);
			obj.setErrorDescription(
					(arr[11] != null) ? arr[11].toString() : null);
		}

		return obj;

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

		/*storedProc.registerStoredProcedureParameter("P_REPORT_TYPE",
				String.class, ParameterMode.IN);

		storedProc.setParameter("P_REPORT_TYPE", reportType);*/

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

		/*storedProc.registerStoredProcedureParameter("P_REPORT_TYPE",
				String.class, ParameterMode.IN);

		storedProc.setParameter("P_REPORT_TYPE", reportType);*/

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

	private StatefulBeanToCsv<ExceptionalTaggingDetailsDto> getBeanWriter(
			String[] columnMappings, Writer writer) {
		ColumnPositionMappingStrategy<ExceptionalTaggingDetailsDto> mappingStrategy = new ColumnPositionMappingStrategy<>();
		mappingStrategy.setType(ExceptionalTaggingDetailsDto.class);
		mappingStrategy.setColumnMapping(columnMappings);
		StatefulBeanToCsvBuilder<ExceptionalTaggingDetailsDto> builder = new StatefulBeanToCsvBuilder<>(
				writer);
		StatefulBeanToCsv<ExceptionalTaggingDetailsDto> beanWriter = builder
				.withMappingStrategy(mappingStrategy)
				.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
				.withLineEnd(CSVWriter.DEFAULT_LINE_END)
				.withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER).build();
		return beanWriter;
	}

}
