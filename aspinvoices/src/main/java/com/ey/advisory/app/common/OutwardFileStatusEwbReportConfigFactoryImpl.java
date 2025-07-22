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
@Component("OutwardFileStatusEwbReportConfigFactoryImpl")
public class OutwardFileStatusEwbReportConfigFactoryImpl
		implements ReportConfigFactory {

	@Override
	public ReportConfig getReportConfig(String reportType, String reportCateg,
			String dataType) {
		ReportConvertor reportConvertor = null;

		Pair<String, String> mapping = CommonUtility
				.getReportHeaderMapping(reportType, reportCateg, dataType);
		String headersKey = mapping.getValue0();
		String columnMappingsKey = mapping.getValue1();

		if ("OUTWARD".equalsIgnoreCase(dataType)
				&& ReportTypeConstants.EWB_NOT_APPLICABLE
						.equalsIgnoreCase(reportType) || "OUTWARD_1A".equalsIgnoreCase(dataType)
				&& ReportTypeConstants.EWB_NOT_APPLICABLE
						.equalsIgnoreCase(reportType)) {
			reportConvertor = (ReportConvertor) StaticContextHolder.getBean(
					"AspProcessedAsUploadedReportConvertor",
					ReportConvertor.class);
		}

		else if ("OUTWARD".equalsIgnoreCase(dataType)
				&& ReportTypeConstants.EWB_APPLICABLE
						.equalsIgnoreCase(reportType) || "OUTWARD_1A".equalsIgnoreCase(dataType)
				&& ReportTypeConstants.EWB_APPLICABLE
						.equalsIgnoreCase(reportType)) {
			reportConvertor = (ReportConvertor) StaticContextHolder.getBean(
					"AspProcessedAsUploadedReportConvertor",
					ReportConvertor.class);

		}

		else if ("OUTWARD".equalsIgnoreCase(dataType)
				&& ReportTypeConstants.EWB_GENERATED
						.equalsIgnoreCase(reportType) || "OUTWARD_1A".equalsIgnoreCase(dataType)
				&& ReportTypeConstants.EWB_GENERATED
						.equalsIgnoreCase(reportType)) {
			reportConvertor = (ReportConvertor) StaticContextHolder.getBean(
					"AspProcessedAsUploadedReportConvertor",
					ReportConvertor.class);

		}

		else if ("OUTWARD".equalsIgnoreCase(dataType)
				&& ReportTypeConstants.EWB_ERROR.equalsIgnoreCase(reportType) || "OUTWARD_1A".equalsIgnoreCase(dataType)
				&& ReportTypeConstants.EWB_ERROR.equalsIgnoreCase(reportType)) {
			reportConvertor = (ReportConvertor) StaticContextHolder.getBean(
					"AspProcessedAsUploadedReportConvertor",
					ReportConvertor.class);

		}

		else if ("OUTWARD".equalsIgnoreCase(dataType)
				&& ReportTypeConstants.EWB_CANCEL
						.equalsIgnoreCase(reportType) || "OUTWARD_1A".equalsIgnoreCase(dataType)
				&& ReportTypeConstants.EWB_CANCEL
						.equalsIgnoreCase(reportType)) {
			reportConvertor = (ReportConvertor) StaticContextHolder.getBean(
					"AspProcessedAsUploadedReportConvertor",
					ReportConvertor.class);

		} else if ("OUTWARD".equalsIgnoreCase(dataType)
				&& ReportTypeConstants.EWB_ERROR_FROM_NIC
						.equalsIgnoreCase(reportType) || "OUTWARD_1A".equalsIgnoreCase(dataType)
				&& ReportTypeConstants.EWB_ERROR_FROM_NIC
						.equalsIgnoreCase(reportType)) {
			reportConvertor = (ReportConvertor) StaticContextHolder.getBean(
					"AspProcessedAsUploadedReportConvertor",
					ReportConvertor.class);

		}
		return new ReportConfig(headersKey, columnMappingsKey, reportConvertor);
	}
}
