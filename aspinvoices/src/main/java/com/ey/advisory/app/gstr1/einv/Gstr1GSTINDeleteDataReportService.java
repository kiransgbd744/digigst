package com.ey.advisory.app.gstr1.einv;

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
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.Gstr1GSTINDeleteDataEntity;
import com.ey.advisory.app.data.repositories.client.Gstr1GSTINDeleteDataRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CombineAndZipXlsxFiles;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hema Gonur
 *
 */
@Slf4j
@Component("Gstr1GSTINDeleteDataReportService")
public class Gstr1GSTINDeleteDataReportService {
	private static int CSV_BUFFER_SIZE = 8192;

	@Autowired
	@Qualifier("Gstr1GSTINDeleteDataRepository")
	private Gstr1GSTINDeleteDataRepository repo;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	CombineAndZipXlsxFiles combineAndZipXlsxFiles;

	private static final DateTimeFormatter dtf = DateTimeFormatter
			.ofPattern("yyyyMMddHHmmss");

	public Pair<String, File> generateErrorReport(Integer fileId)
			throws IOException {

		String fullPath = null;
		File tempDir = null;

		LocalDateTime now = LocalDateTime.now();
		LocalDateTime istTimeStamp = EYDateUtil.toISTDateTimeFromUTC(now);
		String timeMilli = dtf.format(istTimeStamp);

		String errFileName = "Delete_Response_Error_Report" + timeMilli;
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Get Error Report Details with fileId:'%d'", fileId);
			LOGGER.debug(msg);
		}

		try {
			tempDir = createTempDir(fileId);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"fileId  is '%d',"
								+ " Created temporary directory to generate "
								+ "zip file: %s",
						fileId.toString(), tempDir.getAbsolutePath());
				LOGGER.debug(msg);
			}
		} catch (Exception ex) {
			LOGGER.error(
					"Exception while creating temp Directory for config id {}",
					fileId, ex);
		}

		List<Gstr1GSTINDeleteDataEntity> entityList = repo
				.findByFileIdAndIsProcessedFalse(fileId);

		List<Gstr1GSTINDeleteDataReportDto> records = entityList.stream()
				.map(o -> convert(o)).collect(Collectors.toList());

		if (records != null && !records.isEmpty()) {

			fullPath = tempDir.getAbsolutePath() + File.separator + errFileName
					+ ".csv";

			try (Writer writer = new BufferedWriter(new FileWriter(fullPath),
					CSV_BUFFER_SIZE)) {

				String invoiceHeadersTemplate = commonUtility
						.getProp("gstr1.einvoice.gstin.delete.report.header");
				writer.append(invoiceHeadersTemplate);
				String[] columnMappings = commonUtility
						.getProp("gstr1.einvoice.gstin.delete.report.column")
						.split(",");

				ColumnPositionMappingStrategy<Gstr1GSTINDeleteDataReportDto> mappingStrategy = new ColumnPositionMappingStrategy<>();
				mappingStrategy.setType(Gstr1GSTINDeleteDataReportDto.class);
				mappingStrategy.setColumnMapping(columnMappings);
				StatefulBeanToCsvBuilder<Gstr1GSTINDeleteDataReportDto> builder = new StatefulBeanToCsvBuilder<>(
						writer);
				StatefulBeanToCsv<Gstr1GSTINDeleteDataReportDto> beanWriter = builder
						.withMappingStrategy(mappingStrategy)
						.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
						.withLineEnd(CSVWriter.DEFAULT_LINE_END)
						.withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER)
						.build();
				long generationStTime = System.currentTimeMillis();
				beanWriter.write(records);
				long generationEndTime = System.currentTimeMillis();
				long generationTimeDiff = (generationEndTime
						- generationStTime);
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Total Time taken to "
									+ "Generate the report is '%d' millisecs, "
									+ "Data count: , '%s'",
							generationTimeDiff, records.size());
					LOGGER.debug(msg);
				}
				if (writer != null) {
					try {
						writer.flush();
						writer.close();
						if (LOGGER.isDebugEnabled()) {
							String msg = "Flushed writer " + "successfully";
							LOGGER.debug(msg);
						}
					} catch (IOException e) {
						String msg = "Exception while "
								+ "closing the file writer";
						LOGGER.error(msg);
						throw new AppException(msg, e);
					}
				}

			} catch (Exception ex) {
				LOGGER.error("Exception while executing the query for ", ex);

			}

		}
		File fPathFile = new File(fullPath);
		return new Pair<>(errFileName, fPathFile);
	}

	private static File createTempDir(Integer fileId) throws IOException {

		String tempFolderPrefix = "ReconDeleteResponse" + "_" + fileId;
		return Files.createTempDirectory(tempFolderPrefix).toFile();
	}

	private Gstr1GSTINDeleteDataReportDto convert(
			Gstr1GSTINDeleteDataEntity entity) {

		Gstr1GSTINDeleteDataReportDto dto = new Gstr1GSTINDeleteDataReportDto();

		dto.setDocDate(entity.getDocumentDate() != null
				? entity.getDocumentDate().toString() : null);
		dto.setDocNum(entity.getDocumentNumber() != null
				&& !entity.getDocumentNumber().isEmpty()
						? "'".concat(entity.getDocumentNumber()) : null);
		dto.setDocType(entity.getDocumentType());
		dto.setErrorCode(entity.getErrorCode());
		dto.setErrorDesc(entity.getErrorDesc());
		dto.setSGstin(entity.getSgstin());
		dto.setCGstin(entity.getCgstin());
		dto.setReturnPeriod(entity.getReturnPeriod() != null
				&& !entity.getReturnPeriod().isEmpty()
						? "'".concat(entity.getReturnPeriod()) : null);
		dto.setPos(entity.getPos() != null && !entity.getPos().isEmpty()
				? "'".concat(entity.getPos()) : null);
		dto.setTableType(entity.getTableType());

		return dto;

	}

}
