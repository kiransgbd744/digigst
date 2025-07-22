package com.ey.advisory.app.common;

import org.javatuples.Pair;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.ReportConfig;
import com.ey.advisory.common.ReportConfigFactory;
import com.ey.advisory.common.ReportConvertor;
import com.ey.advisory.common.ReportTypeConstants;
import com.ey.advisory.common.StaticContextHolder;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Component("OutwardFileStatusRetReportConfigFactoryImpl")
public class OutwardFileStatusRetReportConfigFactoryImpl
		implements ReportConfigFactory {

	@Override
	public ReportConfig getReportConfig(String reportType, String reportCateg,
			String dataType) {
		ReportConvertor reportConvertor = null;

		Pair<String, String> mapping = CommonUtility
				.getReportHeaderMapping(reportType, reportCateg, dataType);
		String headersKey = mapping.getValue0();
		String columnMappingsKey = mapping.getValue1();

		if (("OUTWARD".equalsIgnoreCase(dataType) || "OUTWARD_1A".equalsIgnoreCase(dataType))
				&& ReportTypeConstants.RET_NOT_APPLICABLE
						.equalsIgnoreCase(reportType)) {
			reportConvertor = (ReportConvertor) StaticContextHolder.getBean(
					"AspProcessedAsUploadedReportConvertor",
					ReportConvertor.class);
		}

		else if (("OUTWARD".equalsIgnoreCase(dataType) || "OUTWARD_1A".equalsIgnoreCase(dataType))
				&& ReportTypeConstants.RET_APPLICABLE
						.equalsIgnoreCase(reportType)) {
			reportConvertor = (ReportConvertor) StaticContextHolder.getBean(
					"AspProcessedAsUploadedReportConvertor",
					ReportConvertor.class);

		}

		else if (("OUTWARD".equalsIgnoreCase(dataType) || "OUTWARD_1A".equalsIgnoreCase(dataType))
				&& ReportTypeConstants.RET_PROCESS
						.equalsIgnoreCase(reportType)) {
			reportConvertor = (ReportConvertor) StaticContextHolder.getBean(
					"AspProcessedAsUploadedReportConvertor",
					ReportConvertor.class);

		}

		else if (("OUTWARD".equalsIgnoreCase(dataType) || "OUTWARD_1A".equalsIgnoreCase(dataType))
				&& ReportTypeConstants.RET_ERROR.equalsIgnoreCase(reportType)) {
			reportConvertor = (ReportConvertor) StaticContextHolder.getBean(
					"AspProcessedAsUploadedReportConvertor",
					ReportConvertor.class);

		}

		else if (("OUTWARD".equalsIgnoreCase(dataType) || "OUTWARD_1A".equalsIgnoreCase(dataType))
				&& ReportTypeConstants.RET_INFO.equalsIgnoreCase(reportType)) {
			reportConvertor = (ReportConvertor) StaticContextHolder.getBean(
					"AspProcessedAsUploadedReportConvertor",
					ReportConvertor.class);

		}
		return new ReportConfig(headersKey, columnMappingsKey, reportConvertor);
	}
}
