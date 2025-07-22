package com.ey.advisory.app.common;

import org.javatuples.Pair;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.ReportConfig;
import com.ey.advisory.common.ReportConfigFactory;
import com.ey.advisory.common.ReportConvertor;
import com.ey.advisory.common.StaticContextHolder;

import lombok.extern.slf4j.Slf4j;

@Component("Gstr3BTable4TransactionalReportDownloadImpl")
@Slf4j
public class Gstr3BTable4TransactionalReportDownloadImpl implements ReportConfigFactory {

	ReportConvertor reportConvertor = null;

	@Override
	public ReportConfig getReportConfig(String reportType, String reportCateg,
			String dataType) {

		Pair<String, String> mapping = getReportHeaderMapping(reportType);
		String headersKey = mapping.getValue0();
		String columnMappingsKey = mapping.getValue1();

		reportConvertor = (ReportConvertor) StaticContextHolder.getBean(
				"Gstr3BTable4TransactionalReportConvertor", ReportConvertor.class);
		
		return new ReportConfig(headersKey, columnMappingsKey, reportConvertor);
	}

	public static Pair<String, String> getReportHeaderMapping(String reportType) {
			return new Pair<String, String>("gstr3b.table4.transaction.report.header.mapping",
					"gstr3b.table4.transaction.report.column.mapping");

	}

}
