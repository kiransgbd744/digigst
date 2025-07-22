package com.ey.advisory.app.common;

import org.javatuples.Pair;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.ReportConfig;
import com.ey.advisory.common.ReportConfigFactory;
import com.ey.advisory.common.ReportConvertor;
import com.ey.advisory.common.StaticContextHolder;

import lombok.extern.slf4j.Slf4j;

@Component("Gstr3BGetReportDownloadImpl")
@Slf4j
public class Gstr3BGetReportDownloadImpl implements ReportConfigFactory {

	ReportConvertor reportConvertor = null;

	@Override
	public ReportConfig getReportConfig(String reportType, String reportCateg,
			String dataType) {

		Pair<String, String> mapping = getReportHeaderMapping(reportType);
		String headersKey = mapping.getValue0();
		String columnMappingsKey = mapping.getValue1();

		if("OUTWARD".equalsIgnoreCase(reportType)){
		reportConvertor = (ReportConvertor) StaticContextHolder.getBean(
				"Gstr3bOutwardReportDownloadConvertor", ReportConvertor.class);
		
		}
		else if("INWARD".equalsIgnoreCase(reportType)){
			reportConvertor = (ReportConvertor) StaticContextHolder.getBean(
					"Gstr3bInwardReportDownloadConvertor", ReportConvertor.class);
		}
		
		return new ReportConfig(headersKey, columnMappingsKey, reportConvertor);
	}

	public static Pair<String, String> getReportHeaderMapping(String reportType) {

		if("OUTWARD".equalsIgnoreCase(reportType)){
			return new Pair<String, String>("gstr3b.outward.api.report.headers",
				"gstr3b.outward.total.report.column.mapping");
		}	else if("INWARD".equalsIgnoreCase(reportType)){
			return new Pair<String, String>("gstr3b.inward.api.report.headers",
					"gstr3b.inward.total.report.column.mapping");
		}
		return null;
	}

}
