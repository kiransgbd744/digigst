/**
 * 
 */
package com.ey.advisory.app.data.services.ewb;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Writer;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.einvoice.CEWBDownloadReportResponse;
import com.ey.advisory.common.CommonUtility;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Laxmi.Salukuti
 *
 */
@Service("CEWBDownloadReportServiceImpl")
@Slf4j
public class CEWBDownloadReportServiceImpl
		implements CEWBDownloadReportService {

	@Autowired
	CommonUtility commonUtility;

	@Override
	public void generateCsvForProcessedCEWB(String fullPath,
			List<CEWBDownloadReportResponse> processedResponse) {

		if (processedResponse != null && !processedResponse.isEmpty()) {
			String[] columnMappings = null;
			String invoiceHeadersTemplate = null;
			try (Writer writer = new BufferedWriter(new FileWriter(fullPath),
					8192)) {

				invoiceHeadersTemplate = commonUtility
						.getProp("cewb.processed.csv.report.headers");
				writer.append(invoiceHeadersTemplate);
				columnMappings = commonUtility
						.getProp("cewb.processed.csv.report.headers.mapping")
						.split(",");

				ColumnPositionMappingStrategy<CEWBDownloadReportResponse> mappingStrategy = new ColumnPositionMappingStrategy<>();
				mappingStrategy.setType(CEWBDownloadReportResponse.class);
				mappingStrategy.setColumnMapping(columnMappings);
				StatefulBeanToCsvBuilder<CEWBDownloadReportResponse> builder = new StatefulBeanToCsvBuilder<>(
						writer);
				StatefulBeanToCsv<CEWBDownloadReportResponse> beanWriter = builder
						.withMappingStrategy(mappingStrategy)
						.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
						.withLineEnd(CSVWriter.DEFAULT_LINE_END)
						.withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER)
						.build();
				long generationStTime = System.currentTimeMillis();
				beanWriter.write(processedResponse);
				long generationEndTime = System.currentTimeMillis();
				long generationTimeDiff = (generationEndTime
						- generationStTime);
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format("Total Time taken to"
							+ " Generate the report is '%d' millisecs,"
							+ " Report Name and Data count:" + " '%s' - '%s'",
							generationTimeDiff);
					LOGGER.debug(msg);
				}
			} catch (Exception ex) {

				String msg = String.format(" Exception while creating csv ");
				LOGGER.error(msg);
			}
		}
	}

	@Override
	public void generateCsvForErrorCEWB(String fullPath,
			List<CEWBDownloadReportResponse> errorResponse) {

		if (errorResponse != null && !errorResponse.isEmpty()) {
			String[] columnMappings = null;
			String invoiceHeadersTemplate = null;
			try (Writer writer = new BufferedWriter(new FileWriter(fullPath),
					8192)) {

				invoiceHeadersTemplate = commonUtility
						.getProp("cewb.error.csv.report.headers");
				writer.append(invoiceHeadersTemplate);
				columnMappings = commonUtility
						.getProp("cewb.error.csv.report.headers.mapping")
						.split(",");

				ColumnPositionMappingStrategy<CEWBDownloadReportResponse> mappingStrategy = new ColumnPositionMappingStrategy<>();
				mappingStrategy.setType(CEWBDownloadReportResponse.class);
				mappingStrategy.setColumnMapping(columnMappings);
				StatefulBeanToCsvBuilder<CEWBDownloadReportResponse> builder = new StatefulBeanToCsvBuilder<>(
						writer);
				StatefulBeanToCsv<CEWBDownloadReportResponse> beanWriter = builder
						.withMappingStrategy(mappingStrategy)
						.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
						.withLineEnd(CSVWriter.DEFAULT_LINE_END)
						.withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER)
						.build();
				long generationStTime = System.currentTimeMillis();
				beanWriter.write(errorResponse);
				long generationEndTime = System.currentTimeMillis();
				long generationTimeDiff = (generationEndTime
						- generationStTime);
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format("Total Time taken to"
							+ " Generate the report is '%d' millisecs,"
							+ " Report Name and Data count:" + " '%s' - '%s'",
							generationTimeDiff);
					LOGGER.debug(msg);
				}
			} catch (Exception ex) {

				String msg = String.format(" Exception while creating csv ");
				LOGGER.error(msg);
			}
		}
	}
}
