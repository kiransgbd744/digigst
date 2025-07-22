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

import com.ey.advisory.app.docs.dto.einvoice.EwbEinvDownloadRequestDto;
import com.ey.advisory.app.services.search.docsearch.EinvReportConvertor;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.dto.EinvEwbDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
import com.opencsv.CSVWriter;
import com.opencsv.ICSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

/**
 * @author Sujith.Nanga
 *
 * 
 */

@Service("EwbEinvReportsServiceImpl")
public class EwbEinvReportsServiceImpl implements Anx1CsvReportsService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(EwbEinvReportsServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("EinvEwbReportConvertor")
	private EinvReportConvertor einvReportConvertor;

	@Override
	public void generateCsvForCriteira(SearchCriteria criteria,
			PageRequest pageReq, String fullPath) {
		EwbEinvDownloadRequestDto request = (EwbEinvDownloadRequestDto) criteria;

		List<EinvEwbDto> responseFromDao = einvReportConvertor
				.getEInvMngmtListing(request);

		if (responseFromDao != null && !responseFromDao.isEmpty()) {
			String[] columnMappings = null;
			String invoiceHeadersTemplate = null;
			try (Writer writer = new BufferedWriter(new FileWriter(fullPath),
					8192);
					ICSVWriter csvWriter = new CSVWriter(writer, '|',
							CSVWriter.NO_QUOTE_CHARACTER)) {

				invoiceHeadersTemplate = commonUtility
						.getProp("invoice.ewb.report.headers");
				writer.append(invoiceHeadersTemplate);
				columnMappings = commonUtility
						.getProp("invoice.ewb.report.headers.mapping")
						.split("\\|");

				ColumnPositionMappingStrategy<EinvEwbDto> mappingStrategy = new ColumnPositionMappingStrategy<>();
				mappingStrategy.setType(EinvEwbDto.class);
				mappingStrategy.setColumnMapping(columnMappings);
				StatefulBeanToCsvBuilder<EinvEwbDto> builder = new StatefulBeanToCsvBuilder<>(
						csvWriter);
				StatefulBeanToCsv<EinvEwbDto> beanWriter = builder
						.withMappingStrategy(mappingStrategy)
						.withSeparator('|')
						.withLineEnd(CSVWriter.DEFAULT_LINE_END)
						// .withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER)
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
