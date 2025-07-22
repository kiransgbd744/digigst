package com.ey.advisory.app.data.erp.vertical.services;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.NilNonExmptHeaderReqDto;
import com.ey.advisory.app.docs.dto.NilNonExmptItemReqDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Nandam
 *
 */
@Slf4j
@Component("NilNonExtConvertDtoTocsv")
public class NilNonExtConvertDtoTocsv {

	@Autowired
	private CommonUtility commonUtility;

	public boolean convert(NilNonExmptHeaderReqDto dto, String fullPath,
			String path) {
		// convert the list of dto's to CSV
		Writer writer = null;
		try {
			List<NilNonExmptItemReqDto> responseFromView = dto.getNilInvoices();
			// csv created
			writer = new BufferedWriter(new FileWriter(fullPath), 8192);

			String invoiceHeadersTemplate = null;
			String[] columnMappings = null;
			if ("/api/saveNilNonExt.do".equals(path)) {
				invoiceHeadersTemplate = commonUtility
						.getProp("nil.api.async.csv.report.headers");
				writer.append(invoiceHeadersTemplate);
				columnMappings = commonUtility
						.getProp("nil.api.async.csv.report.headers.mapping")
						.split(",");
			} else {
				invoiceHeadersTemplate = commonUtility
						.getProp("nil.api.async.csv.new.report.headers");
				writer.append(invoiceHeadersTemplate);
				columnMappings = commonUtility
						.getProp("nil.api.async.csv.new.report.headers.mapping")
						.split(",");
			}
			// writing the data into the csv
			if (LOGGER.isDebugEnabled()) {
				String errMsg = String.format("writhing the data into the csv");
				LOGGER.debug(errMsg);

			}

			ColumnPositionMappingStrategy<NilNonExmptItemReqDto> mappingStrategy = new ColumnPositionMappingStrategy<>();
			mappingStrategy.setType(NilNonExmptItemReqDto.class);
			mappingStrategy.setColumnMapping(columnMappings);
			StatefulBeanToCsvBuilder<NilNonExmptItemReqDto> builder = new StatefulBeanToCsvBuilder<>(
					writer);
			StatefulBeanToCsv<NilNonExmptItemReqDto> beanWriter = builder
					.withMappingStrategy(mappingStrategy)
					.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
					.withLineEnd(CSVWriter.DEFAULT_LINE_END)
					.withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER).build();
			beanWriter.write(responseFromView);

		} catch (Exception ex) {
			String msg = "Unexpected error while retriving download report ";
			LOGGER.error(msg, ex);
			return false;
		} finally {
			if (writer != null) {
				try {
					writer.flush();
					writer.close();
					if (LOGGER.isDebugEnabled()) {
						String msg = "Flushed writer successfully";
						LOGGER.debug(msg);
					}
				} catch (IOException e) {
					String msg = "Exception while closing the file writer";
					LOGGER.error(msg);
					throw new AppException(msg, e);
				}
			}
		}
		return true;
	}

}
