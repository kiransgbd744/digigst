/**
 * 
 */
package com.ey.advisory.app.services.einvoice.reports;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.einvoice.EInvMangmntScreenDownloadReqDto;
import com.ey.advisory.app.docs.dto.einvoice.EInvMangmntScreenDownloadResponseDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.search.SearchCriteria;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

/**
 * @author Laxmi.Salukuti
 *
 */
@Service("EInvManagmntScreenDownloadServiceImpl")
public class EInvManagmntScreenDownloadServiceImpl
		implements EInvManagmntScreenDownloadService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(EInvManagmntScreenDownloadServiceImpl.class);

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("EInvManagmntScreenDownloadDaoImpl")
	private EInvManagmntScreenDownloadDao einvManagmntScreenDownloadDao;

	@Autowired
	@Qualifier("EInvManagmtInwardScreenDownloadDaoImpl")
	private EInvManagmntScreenDownloadDao einvManagmtInwardScreenDownloadDao;

	@Override
	public void findEInvMngmtScreendwnld(SearchCriteria criteria,
			String fullPath) {
		EInvMangmntScreenDownloadReqDto request = (EInvMangmntScreenDownloadReqDto) criteria;
		List<EInvMangmntScreenDownloadResponseDto> responseFromView = new ArrayList<>();

		if ((request.getDataType() != null && request.getDataType()
				.equalsIgnoreCase(DownloadReportsConstant.OUTWARD))) {
			responseFromView = einvManagmntScreenDownloadDao
					.getEInvMngmtScreendwnld(request);
		} else {
			responseFromView = einvManagmtInwardScreenDownloadDao
					.getEInvMngmtScreendwnld(request);
		}
		if (responseFromView != null && !responseFromView.isEmpty()) {
			String[] columnMappings = null;
			String invoiceHeadersTemplate = null;
			try (Writer writer = new BufferedWriter(new FileWriter(fullPath),
					8192)) {
				if ((request.getDataType() != null && request.getDataType()
						.equalsIgnoreCase(DownloadReportsConstant.OUTWARD))) {
					invoiceHeadersTemplate = commonUtility.getProp(
							"einvoice.mangmt.outward.csv.report.headers");
					writer.append(invoiceHeadersTemplate);
					columnMappings = commonUtility
							.getProp(
									"einvoice.mangmt.outward.csv.mapping.headers")
							.split(",");
 				} else {
					invoiceHeadersTemplate = commonUtility.getProp(
							"einvoice.mangmt.inward.csv.report.headers");
					writer.append(invoiceHeadersTemplate);
					columnMappings = commonUtility
							.getProp(
									"einvoice.mangmt.inward.csv.mapping.headers")
							.split(",");
				}

				ColumnPositionMappingStrategy<EInvMangmntScreenDownloadResponseDto> mappingStrategy = new ColumnPositionMappingStrategy<>();
				mappingStrategy
						.setType(EInvMangmntScreenDownloadResponseDto.class);
				mappingStrategy.setColumnMapping(columnMappings);
				StatefulBeanToCsvBuilder<EInvMangmntScreenDownloadResponseDto> builder = new StatefulBeanToCsvBuilder<>(
						writer);
				StatefulBeanToCsv<EInvMangmntScreenDownloadResponseDto> beanWriter = builder
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
