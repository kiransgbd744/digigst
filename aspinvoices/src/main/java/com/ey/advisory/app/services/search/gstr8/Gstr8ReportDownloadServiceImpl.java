package com.ey.advisory.app.services.search.gstr8;

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

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import org.apache.commons.io.FileUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.services.strcutvalidation.gstr8.Gstr8Dto;
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
@Component("Gstr8ReportDownloadServiceImpl")
public class Gstr8ReportDownloadServiceImpl
		implements Gstr8ReportDownloadService {

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
	public void generateReports(Long id, String reportType) {
		String fullPath = null;
		String uploadedDocName = null;
		Writer writer = null;

		File tempDir = null;
		try {
			tempDir = createTempDir(id);
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
			if (reportType.equalsIgnoreCase("Processed Records")) {
				fileName = "GSTR8_Processed Records Report";
			} else if (reportType.equalsIgnoreCase("Error Records")) {
				fileName = "GSTR8_Error Records Report";
			} else {
				fileName = "GSTR8_Total Records Report";
			}

			fullPath = tempDir.getAbsolutePath() + File.separator + fileName
					+ ".csv";

			Integer noOfChunk = getChunkNo(
					"USP_GSTR8_INS_CHUNK_DOWNLOAD_REPORT", id, chunkSize);

			noOfChunk = noOfChunk != null ? noOfChunk.intValue() : 0;

			if (noOfChunk == 0) {

				String msg = String.format(
						"no.of " + "chunk count is zero batchId:'%s'", id);
				LOGGER.error(msg);

				fileStatusDownloadReportRepo.updateStatus(id, "NO_DATA_FOUND",
						null, LocalDateTime.now());
			}
			int j = 0;
			writer = new BufferedWriter(new FileWriter(fullPath),
					CSV_BUFFER_SIZE);

			String invoiceHeadersTemplate;
			String[] columnMappings;

			if (reportType.equalsIgnoreCase("Processed Records")) {
				invoiceHeadersTemplate = commonUtility
						.getProp("gstr8.processed.header.mapping");

				columnMappings = commonUtility
						.getProp("gstr8.processed.column.mapping").split(",");
			} else {
				invoiceHeadersTemplate = commonUtility
						.getProp("gstr8.error.header.mapping");

				columnMappings = commonUtility
						.getProp("gstr8.error.column.mapping").split(",");

			}

			StatefulBeanToCsv<Gstr8Dto> beanWriter = getBeanWriter(
					columnMappings, writer);
			
			writer.append(invoiceHeadersTemplate);
			while (j < noOfChunk) {
				j++;
				List<Object[]> records = getProcData(
						"USP_GSTR8_DISP_CHUNK_DOWNLOAD_REPORT", id, j);

				if (records != null && !records.isEmpty()) {

					List<Gstr8Dto> reconDataList = records.stream()
							.map(o -> convertRowsToDto(o, reportType))
							.collect(Collectors.toCollection(ArrayList::new));

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

			fileStatusDownloadReportRepo.updateStatus(id,
					ReportStatusConstants.REPORT_GENERATED, uploadedDocName,
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()),
					docId);

		} catch (Exception ex) {
			String errMsg = String
					.format("Error while creating Error report %s", id);

			LOGGER.error(errMsg, ex);
			throw new AppException(ex);

		} finally {
			deleteTemporaryDirectory(tempDir);
		}

		// return uploadedDocName;
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

		String tempFolderPrefix = "Gstr8Upload" + "_" + batchId;
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

	private Gstr8Dto convertRowsToDto(Object[] arr, String reportType) {

		Gstr8Dto obj = new Gstr8Dto();
		obj.setEcomGstin(arr[0] != null ? arr[0].toString() : null);
		obj.setReturnPeriod(arr[1] != null
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[1].toString())
				: null);
		obj.setIdentifier(arr[2] != null
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[2].toString())
				: null);
		obj.setOriginalReturnPeriod(arr[3] != null
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[3].toString())
				: null);
		obj.setOriginalNetSupplies(arr[4] != null
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[4].toString())
				: null);
		obj.setDocumentType(arr[5] != null ? arr[5].toString() : null);
		obj.setSupplyType(arr[6] != null ? arr[6].toString() : null);
		obj.setSupplierGSTINorEnrolmentID(
				arr[7] != null ? arr[7].toString() : null);
		obj.setOriginalSupplierGSTINorEnrolmentID(
				arr[8] != null ? arr[8].toString() : null);
		obj.setSuppliesToRegistered(arr[9] != null
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[9].toString())
				: null);
		obj.setReturnsFromRegistered(
				arr[10] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[10].toString()) : null);
		obj.setSuppliesToUnRegistered(
				arr[11] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[11].toString()) : null);
		obj.setReturnsFromUnRegistered(
				arr[12] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[12].toString()) : null);
		obj.setNetSupplies(
				arr[13] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[13].toString()) : null);
		obj.setIntegratedTaxAmount(
				arr[14] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[14].toString()) : null);
		obj.setCentralTaxAmount(
				arr[15] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[15].toString()) : null);
		obj.setStateUTTaxAmount(
				arr[16] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[16].toString()) : null);
		obj.setUserDefinedField1(arr[17] != null ? arr[17].toString() : null);
		obj.setUserDefinedField2(arr[18] != null ? arr[18].toString() : null);
		obj.setUserDefinedField3(arr[19] != null ? arr[19].toString() : null);
		obj.setUserDefinedField4(arr[20] != null ? arr[20].toString() : null);
		obj.setUserDefinedField5(arr[21] != null ? arr[21].toString() : null);
		obj.setUserDefinedField6(arr[22] != null ? arr[22].toString() : null);
		obj.setUserDefinedField7(arr[23] != null ? arr[23].toString() : null);
		obj.setUserDefinedField8(arr[24] != null ? arr[24].toString() : null);
		obj.setUserDefinedField9(arr[25] != null ? arr[25].toString() : null);
		obj.setUserDefinedField10(arr[26] != null ? arr[26].toString() : null);
		obj.setUserDefinedField11(arr[27] != null ? arr[27].toString() : null);
		obj.setUserDefinedField12(arr[28] != null ? arr[28].toString() : null);
		obj.setUserDefinedField13(arr[29] != null ? arr[29].toString() : null);
		obj.setUserDefinedField14(arr[30] != null ? arr[30].toString() : null);
		obj.setUserDefinedField15(arr[31] != null ? arr[31].toString() : null);

		if (!reportType.equalsIgnoreCase("Processed Records")) {
			obj.setErrorCode((arr[32] != null) ? arr[32].toString() : null);
			obj.setErrorDesc((arr[33] != null) ? arr[33].toString() : null);
			obj.setPos(arr[34] != null ? arr[34].toString() : null);
			obj.setOriginalPos(arr[35] != null ? arr[35].toString() : null);
		} else {
			obj.setPos(arr[32] != null ? arr[32].toString() : null);
			obj.setOriginalPos(arr[33] != null ? arr[33].toString() : null);
		}
		

		return obj;

	}

	private Integer getChunkNo(String procName, Long downloadId,
			Integer chunkSize) {

		StoredProcedureQuery storedProc = entityManager
				.createStoredProcedureQuery(procName);

		storedProc.registerStoredProcedureParameter("P_REQUEST_ID", Long.class,
				ParameterMode.IN);

		storedProc.setParameter("P_REQUEST_ID", downloadId);

		storedProc.registerStoredProcedureParameter("P_CHUNK_SPLIT_VAL",
				Integer.class, ParameterMode.IN);

		storedProc.setParameter("P_CHUNK_SPLIT_VAL", chunkSize);

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

		storedProc.registerStoredProcedureParameter("P_REQUEST_ID", Long.class,
				ParameterMode.IN);

		storedProc.setParameter("P_REQUEST_ID", downloadId);

		storedProc.registerStoredProcedureParameter("P_CHUNK_NUM",
				Integer.class, ParameterMode.IN);
		storedProc.setParameter("P_CHUNK_NUM", chunkValue);

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

	private StatefulBeanToCsv<Gstr8Dto> getBeanWriter(String[] columnMappings,
			Writer writer) {
		ColumnPositionMappingStrategy<Gstr8Dto> mappingStrategy = new ColumnPositionMappingStrategy<>();
		mappingStrategy.setType(Gstr8Dto.class);
		mappingStrategy.setColumnMapping(columnMappings);
		StatefulBeanToCsvBuilder<Gstr8Dto> builder = new StatefulBeanToCsvBuilder<>(
				writer);
		StatefulBeanToCsv<Gstr8Dto> beanWriter = builder
				.withMappingStrategy(mappingStrategy)
				.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
				.withLineEnd(CSVWriter.DEFAULT_LINE_END)
				.withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER).build();
		return beanWriter;
	}
}
