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

import com.ey.advisory.app.data.views.client.Gstr3bdownloadProcessedDto;
import com.ey.advisory.app.data.views.client.Gstr3bdownloadTotalDto;
import com.ey.advisory.app.docs.dto.Anx1FileStatusReportsReqDto;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

@Service("Gstr3BTotalReportsServiceImpl")
public class Gstr3BTotalReportsServiceImpl implements GSTR3BTotalReportService {

	private static final Logger LOGGER = LoggerFactory.getLogger(Gstr3BTotalReportsServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Gstr3BTotalReportDaoImpl")
	private Gstr3BTotalDao gstr3BTotalDao;

	@Override
	public void generateTotalCsv(SearchCriteria criteria, PageRequest pageReq, String fullPath) {
		Anx1FileStatusReportsReqDto request = (Anx1FileStatusReportsReqDto) criteria;

		List<Gstr3bdownloadProcessedDto> responseFromDao = gstr3BTotalDao.generateTotalCsv(request);

		if (responseFromDao != null && !responseFromDao.isEmpty()) {
			String[] columnMappings = null;
			String invoiceHeadersTemplate = null;
			try (Writer writer = new BufferedWriter(new FileWriter(fullPath), 8192)) {

				invoiceHeadersTemplate = commonUtility.getProp("gstr3b.total.csv.report.headers");
				writer.append(invoiceHeadersTemplate);
				columnMappings = commonUtility.getProp("gstr3b.total.csv.report.headers.mapping").split(",");

				ColumnPositionMappingStrategy<Gstr3bdownloadProcessedDto> mappingStrategy = new ColumnPositionMappingStrategy<>();
				mappingStrategy.setType(Gstr3bdownloadProcessedDto.class);
				mappingStrategy.setColumnMapping(columnMappings);
				StatefulBeanToCsvBuilder<Gstr3bdownloadProcessedDto> builder = new StatefulBeanToCsvBuilder<>(writer);
				StatefulBeanToCsv<Gstr3bdownloadProcessedDto> beanWriter = builder.withMappingStrategy(mappingStrategy)
						.withSeparator(CSVWriter.DEFAULT_SEPARATOR).withLineEnd(CSVWriter.DEFAULT_LINE_END)
						.withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER).build();
				long generationStTime = System.currentTimeMillis();
				beanWriter.write(responseFromDao);
				long generationEndTime = System.currentTimeMillis();
				long generationTimeDiff = (generationEndTime - generationStTime);
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format("Total Time taken to" + " Generate the report is '%d' millisecs,"
							+ " Report Name and Data count:" + " '%s' - '%s'", generationTimeDiff);
					LOGGER.debug(msg);
				}
			} catch (Exception ex) {

				String msg = String.format(" Exception while creating csv ");
				LOGGER.error(msg);
			}
		}
	}
}
