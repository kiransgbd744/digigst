package com.ey.advisory.app.common;

import org.javatuples.Pair;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.ReportConfig;
import com.ey.advisory.common.ReportConfigFactory;
import com.ey.advisory.common.ReportConvertor;
import com.ey.advisory.common.StaticContextHolder;

@Component("Asyncgstr2aB2BReportConfigFactoryImpl")
public class Asyncgstr2aB2BReportConfigFactoryImpl implements ReportConfigFactory {
	@Override
	public ReportConfig getReportConfig(String reportType, String reportCateg,
			String dataType) {
		ReportConvertor reportConvertor = null;

		Pair<String, String> mapping = CommonUtility
				.getReportHeaderMapping(reportType, reportCateg, dataType);
		String headersKey = mapping.getValue0();
		String columnMappingsKey = mapping.getValue1();
		
			reportConvertor = (ReportConvertor) StaticContextHolder.getBean(
					"Asynvgstr2aB2BReportConvertor",
					ReportConvertor.class);
		
		return new ReportConfig(headersKey, columnMappingsKey, reportConvertor);
	}

}
