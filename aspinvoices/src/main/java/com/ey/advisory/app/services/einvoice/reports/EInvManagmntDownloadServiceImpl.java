/**
 * 
 */
package com.ey.advisory.app.services.einvoice.reports;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.einvoice.EinvoiceMangementResponseDto;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.dto.EInvoiceDocSearchReqDto;
import com.ey.advisory.core.search.SearchCriteria;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Laxmi.Salukuti
 *
 */
@Service("EInvManagmntDownloadServiceImpl")
@Slf4j
public class EInvManagmntDownloadServiceImpl
		implements EInvManagmntDownloadService {

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("EInvManagmntDownloadDaoImpl")
	private EInvManagmntDownloadDao eInvManagmntDownloadDao;

	@Override
	public void findEInvMngmtdwnld(SearchCriteria criteria, String fullPath) {

		EInvoiceDocSearchReqDto request = (EInvoiceDocSearchReqDto) criteria;
		List<EinvoiceMangementResponseDto> responseFromView = new ArrayList<>();

		responseFromView = eInvManagmntDownloadDao
				.getEInvMngmtScreendwnld(request);

		if (responseFromView != null && !responseFromView.isEmpty()) {
			String[] columnMappings = null;
			String invoiceHeadersTemplate = null;
			try (Writer writer = new BufferedWriter(new FileWriter(fullPath),
					8192)) {

				invoiceHeadersTemplate = commonUtility
						.getProp("gsrt1.invmangment.headers");
				writer.append(invoiceHeadersTemplate);
				columnMappings = commonUtility
						.getProp("gstr1.invmangment.mapping").split(",");

				ColumnPositionMappingStrategy<EinvoiceMangementResponseDto> mappingStrategy = new ColumnPositionMappingStrategy<>();
				mappingStrategy.setType(EinvoiceMangementResponseDto.class);
				mappingStrategy.setColumnMapping(columnMappings);
				StatefulBeanToCsvBuilder<EinvoiceMangementResponseDto> builder = new StatefulBeanToCsvBuilder<>(
						writer);
				StatefulBeanToCsv<EinvoiceMangementResponseDto> beanWriter = builder
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
