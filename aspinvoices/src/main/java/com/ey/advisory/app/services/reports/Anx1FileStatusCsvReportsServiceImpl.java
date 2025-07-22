/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Writer;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.Anx1FileStatusReportsReqDto;
import com.ey.advisory.app.docs.dto.einvoice.DataStatusEinvoiceDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

/**
 * @author Laxmi.Salukuti
 *
 */
@Service("Anx1FileStatusCsvReportsServiceImpl")
public class Anx1FileStatusCsvReportsServiceImpl
		implements Anx1FileStatusCsvReportsService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1FileStatusCsvReportsServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Anx1FileStatusCsvReportsDaoImpl")
	private Anx1FileStatusCsvReportsDao anx1FileStatusCsvReportsDao;

	@Override
	public void generateFileStatusCsv(SearchCriteria criteria,
			PageRequest pageReq, String fullPath) {
		Anx1FileStatusReportsReqDto request = (Anx1FileStatusReportsReqDto) criteria;
		String fileType = request.getFileType();

		List<DataStatusEinvoiceDto> responseFromDao = anx1FileStatusCsvReportsDao
				.getFileStatusCsvReports(request);

		if (responseFromDao != null && !responseFromDao.isEmpty()) {
			String[] columnMappings = null;
			String invoiceHeadersTemplate = null;
			try (Writer writer = new BufferedWriter(new FileWriter(fullPath),
					8192)) {
				if (fileType != null) {
					if (DownloadReportsConstant.FileTypes.RAW.getFileType()
							.equalsIgnoreCase(fileType)) {

						invoiceHeadersTemplate = commonUtility
								.getProp("anx1.api.async.csv.report.headers");
						writer.append(invoiceHeadersTemplate);
						columnMappings = commonUtility
								.getProp(
										"anx1.api.async.csv.report.headers.mapping")
								.split(",");
					}
					if (DownloadReportsConstant.FileTypes.COMPREHENSIVE_RAW
							.getFileType().equalsIgnoreCase(fileType)) {

						invoiceHeadersTemplate = commonUtility
								.getProp("anx1.api.sync.csv.report.headers");
						writer.append(invoiceHeadersTemplate);
						columnMappings = commonUtility
								.getProp("anx1.api.new.report.headers")
								.split(",");
					}
				}
				ColumnPositionMappingStrategy<DataStatusEinvoiceDto> mappingStrategy = 
						new ColumnPositionMappingStrategy<>();
				mappingStrategy.setType(DataStatusEinvoiceDto.class);
				mappingStrategy.setColumnMapping(columnMappings);
				StatefulBeanToCsvBuilder<DataStatusEinvoiceDto> builder = new StatefulBeanToCsvBuilder<>(
						writer);
				StatefulBeanToCsv<DataStatusEinvoiceDto> beanWriter = builder
						.withMappingStrategy(mappingStrategy)
						.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
						.withLineEnd(CSVWriter.DEFAULT_LINE_END)
						.withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER)
						.build();
				long generationStTime = System.currentTimeMillis();
				beanWriter.write(responseFromDao);
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
