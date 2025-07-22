/**
 * 
 */
package com.ey.advisory.app.services.reports;

/**
 * @author Laxmi.Salukuti
 *
 */
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Writer;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.Anx1ReportSearchReqDto;
import com.ey.advisory.app.docs.dto.einvoice.DataStatusEinvoiceDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

@Service("Anx1CsvReportsServiceImpl")
public class Anx1CsvReportsServiceImpl implements Anx1CsvReportsService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1CsvReportsServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Anx1CsvReportsDaoImpl")
	private Anx1CsvReportsDao anx1CsvReportsDao;

	@Override
	public void generateCsvForCriteira(SearchCriteria criteria,
			PageRequest pageReq, String fullPath) {
		Anx1ReportSearchReqDto request = (Anx1ReportSearchReqDto) criteria;
		String serviceOption = request.getServiceOption();

		List<DataStatusEinvoiceDto> responseFromView = anx1CsvReportsDao
				.getCsvReports(request);

		if (responseFromView != null && !responseFromView.isEmpty()) {
			String[] columnMappings = null;
			String invoiceHeadersTemplate = null;
			try (Writer writer = new BufferedWriter(new FileWriter(fullPath),
					8192)) {
				if ((serviceOption != null)
						&& (DownloadReportsConstant.ServiceOptions.ASYN_COMP
								.getServiceOption()
								.equalsIgnoreCase(serviceOption))) {

					invoiceHeadersTemplate = commonUtility
							.getProp("anx1.api.async.csv.report.headers");
					writer.append(invoiceHeadersTemplate);
					columnMappings = commonUtility
							.getProp(
									"anx1.api.async.csv.report.headers.mapping")
							.split(",");
				}
				if ((DownloadReportsConstant.ServiceOptions.SYNCEWB_ASYN_COMP
						.getServiceOption().equalsIgnoreCase(serviceOption))
						|| (DownloadReportsConstant.ServiceOptions.ASYNCEWB_ASYNC_COMP
								.getServiceOption()
								.equalsIgnoreCase(serviceOption))
						|| (DownloadReportsConstant.ServiceOptions.ASYNCEWB_COMP_EINV
								.getServiceOption()
								.equalsIgnoreCase(serviceOption))
								|| (DownloadReportsConstant.ServiceOptions.SYNCEWB_SYNEINV_ASYN_COMP
										.getServiceOption()
										.equalsIgnoreCase(serviceOption))
						|| (serviceOption == null)) {

					invoiceHeadersTemplate = commonUtility
							.getProp("anx1.api.sync.csv.report.headers");
					writer.append(invoiceHeadersTemplate);
					columnMappings = commonUtility
							.getProp("anx1.api.new.report.headers").split(",");

				}

				ColumnPositionMappingStrategy<DataStatusEinvoiceDto> mappingStrategy = new ColumnPositionMappingStrategy<>();
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
				beanWriter.write(responseFromView);
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
