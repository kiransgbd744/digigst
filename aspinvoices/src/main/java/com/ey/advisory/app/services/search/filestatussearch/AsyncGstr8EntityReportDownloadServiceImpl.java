package com.ey.advisory.app.services.search.filestatussearch;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
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
import com.ey.advisory.app.data.views.client.Gstr8EntityLevelSummaryDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AsyncReportDownloadService;
import com.ey.advisory.common.CombinAndZipReportFiles;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.ReportStatusConstants;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("AsyncGstr8EntityReportDownloadServiceImpl")
public class AsyncGstr8EntityReportDownloadServiceImpl
		implements AsyncReportDownloadService {

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	CombinAndZipReportFiles combinAndZipReportFiles;

	private static final String UPLOADED_FILENAME_MSG = "Uploaded file Name :'%s'";

	@SuppressWarnings("unchecked")
	@Override
	public void generateReports(Long id) {

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

		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("Get Entity Level Report with batchId:'%s'", id);
			LOGGER.debug(msg);
		}
		try {
			Optional<FileStatusDownloadReportEntity> optEntity = fileStatusDownloadReportRepo
					.findById(id);

			FileStatusDownloadReportEntity entity = optEntity.get();

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Fetched file entity based on report ID : %s",
						entity.toString());
				LOGGER.debug(msg);
			}

			String taxPeriod = entity.getTaxPeriod();
			String gstins = GenUtil.convertClobtoString(entity.getGstins());

			DateTimeFormatter dtf = DateTimeFormatter
					.ofPattern("yyyyMMddHHmmss");
			String timeMilli = dtf.format(LocalDateTime.now());

			fullPath = tempDir.getAbsolutePath() + File.separator
					+ "GSTR8_Entity Level Report" + "_" + timeMilli + ".csv";

			writer = new BufferedWriter(new FileWriter(fullPath), 8192);
			String invoiceHeadersTemplate = commonUtility
					.getProp("gstr8.entity.report.headers");
			String[] columnMappings = commonUtility
					.getProp("gstr8.entity.report.mapping").split(",");

			StatefulBeanToCsv<Gstr8EntityLevelSummaryDto> beanWriter = getBeanWriter(
					columnMappings, writer);
			writer.append(invoiceHeadersTemplate);

			List<Object[]> list = null;

			StoredProcedureQuery storedProc = entityManager
					.createStoredProcedureQuery("USP_GSTR8_ENTITY_SMRY_RPT");

			storedProc.registerStoredProcedureParameter("GSTIN", String.class,
					ParameterMode.IN);
			storedProc.setParameter("GSTIN", gstins);

			storedProc.registerStoredProcedureParameter("RET_PERIOD",
					String.class, ParameterMode.IN);
			storedProc.setParameter("RET_PERIOD", taxPeriod);

			list = storedProc.getResultList();

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Record count after converting object array to DTO ",
						list.size());
				LOGGER.debug(msg);
			}
			List<Gstr8EntityLevelSummaryDto> entityDataList = list.stream()
					.map(o -> convertRowsToDto(o, taxPeriod))
					.collect(Collectors.toCollection(ArrayList::new));
			beanWriter.write(entityDataList);
			flushWriter(writer);
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
			fileStatusDownloadReportRepo.updateStatus(id,
					ReportStatusConstants.REPORT_GENERATION_FAILED, null,
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
			String msg = "Exception occued while while generating csv file";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);

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

		String tempFolderPrefix = "Gstr8EntityLevel" + "_" + batchId;
		return Files.createTempDirectory(tempFolderPrefix).toFile();
	}

	private StatefulBeanToCsv<Gstr8EntityLevelSummaryDto> getBeanWriter(
			String[] columnMappings, Writer writer) {
		ColumnPositionMappingStrategy<Gstr8EntityLevelSummaryDto> mappingStrategy = new ColumnPositionMappingStrategy<>();
		mappingStrategy.setType(Gstr8EntityLevelSummaryDto.class);
		mappingStrategy.setColumnMapping(columnMappings);
		StatefulBeanToCsvBuilder<Gstr8EntityLevelSummaryDto> builder = new StatefulBeanToCsvBuilder<>(
				writer);
		StatefulBeanToCsv<Gstr8EntityLevelSummaryDto> beanWriter = builder
				.withMappingStrategy(mappingStrategy)
				.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
				.withLineEnd(CSVWriter.DEFAULT_LINE_END)
				.withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER).build();
		return beanWriter;
	}

	private Gstr8EntityLevelSummaryDto convertRowsToDto(Object[] arr,
			String taxPeriod) {

		Gstr8EntityLevelSummaryDto obj = new Gstr8EntityLevelSummaryDto();
		obj.setGstin(arr[0] != null ? arr[0].toString() : null);
		obj.setSection(arr[1] != null ? arr[1].toString() : null);
		obj.setSubSection(arr[2] != null ? arr[2].toString() : null);
		obj.setTaxPeriod(
				DownloadReportsConstant.CSVCHARACTER.concat(taxPeriod));
		obj.setGrossSuppliesMadeDigi(
				arr[3] != null ? (BigDecimal) arr[3] : BigDecimal.ZERO);
		obj.setGrossSuppliesReturnedDigi(
				arr[4] != null ? (BigDecimal) arr[4] : BigDecimal.ZERO);
		obj.setNetSuppliesDigi(
				arr[5] != null ? (BigDecimal) arr[5] : BigDecimal.ZERO);
		obj.setIgstDigi(arr[6] != null ? (BigDecimal) arr[6] : BigDecimal.ZERO);
		obj.setCgstDigi(arr[7] != null ? (BigDecimal) arr[7] : BigDecimal.ZERO);
		obj.setSgstDigi(arr[8] != null ? (BigDecimal) arr[8] : BigDecimal.ZERO);
		obj.setGrossSuppliesMadeGstn(
				arr[9] != null ? (BigDecimal) arr[9] : BigDecimal.ZERO);
		obj.setGrossSuppliesReturnedGstn(
				arr[10] != null ? (BigDecimal) arr[10] : BigDecimal.ZERO);
		obj.setNetSuppliesGstn(
				arr[11] != null ? (BigDecimal) arr[11] : BigDecimal.ZERO);
		obj.setIgstGstn(
				arr[12] != null ? (BigDecimal) arr[12] : BigDecimal.ZERO);
		obj.setCgstGstn(
				arr[13] != null ? (BigDecimal) arr[13] : BigDecimal.ZERO);
		obj.setSgstGstn(
				arr[14] != null ? (BigDecimal) arr[14] : BigDecimal.ZERO);
		obj.setGrossSuppliesMadeDifference(
				arr[15] != null ? (BigDecimal) arr[15] : BigDecimal.ZERO);
		obj.setGrossSuppliesReturnedDifference(
				arr[16] != null ? (BigDecimal) arr[16] : BigDecimal.ZERO);
		obj.setNetSuppliesDifference(
				arr[17] != null ? (BigDecimal) arr[17] : BigDecimal.ZERO);
		obj.setIgstDifference(
				arr[18] != null ? (BigDecimal) arr[18] : BigDecimal.ZERO);
		obj.setCgstDifference(
				arr[19] != null ? (BigDecimal) arr[19] : BigDecimal.ZERO);
		obj.setSgstDifference(
				arr[20] != null ? (BigDecimal) arr[20] : BigDecimal.ZERO);

		return obj;

	}

}
