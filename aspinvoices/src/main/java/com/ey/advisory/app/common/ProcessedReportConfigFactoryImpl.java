package com.ey.advisory.app.common;

import org.javatuples.Pair;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.ReportConfig;
import com.ey.advisory.common.ReportConfigFactory;
import com.ey.advisory.common.ReportConvertor;
import com.ey.advisory.common.ReportTypeConstants;
import com.ey.advisory.common.StaticContextHolder;

import lombok.extern.slf4j.Slf4j;

@Component("ProcessedReportConfigFactoryImpl")
@Slf4j
public class ProcessedReportConfigFactoryImpl implements ReportConfigFactory {

	/*
	 * final static List<String> processedSummaryReportTypeList =
	 * ImmutableList.of( ReportTypeConstants.AS_UPLOADED);
	 */

	@Override
	public ReportConfig getReportConfig(String reportType, String reportCateg,
			String dataType) {
		ReportConvertor reportConvertor = null;
		LOGGER.debug("reportType {} reportCateg {} dataType {} ",reportType,reportCateg,dataType);
		Pair<String, String> mapping = CommonUtility
				.getReportHeaderMapping(reportType, reportCateg, dataType);
		String headersKey = mapping.getValue0();
		String columnMappingsKey = mapping.getValue1();

		if ((ReportTypeConstants.PROCESSED_SUMMARY.equalsIgnoreCase(reportCateg)
				|| ReportTypeConstants.REVIEW_SUMMARY
						.equalsIgnoreCase(reportCateg))
				&& ("OUTWARD".equalsIgnoreCase(dataType) 
						|| "OUTWARD_1A".equalsIgnoreCase(dataType))
				&& (ReportTypeConstants.AS_UPLOADED
						.equalsIgnoreCase(reportType)
						|| ReportTypeConstants.GSTR1A_AS_UPLOADED
						.equalsIgnoreCase(reportType))) {
			reportConvertor = (ReportConvertor) StaticContextHolder.getBean(
					"AspProcessedAsUploadedReportConvertor",
					ReportConvertor.class);
		}

		else if ((ReportTypeConstants.PROCESSED_SUMMARY
				.equalsIgnoreCase(reportCateg)
				|| ReportTypeConstants.REVIEW_SUMMARY
						.equalsIgnoreCase(reportCateg))
				&& ("OUTWARD".equalsIgnoreCase(dataType) 
						|| "OUTWARD_1A".equalsIgnoreCase(dataType))
				&& (ReportTypeConstants.ASPERROR.equalsIgnoreCase(reportType)
						|| ReportTypeConstants.GSTR1A_ASPERROR
								.equalsIgnoreCase(reportType))) {
			reportConvertor = (ReportConvertor) StaticContextHolder
					.getBean("AspErrorReportConvertor", ReportConvertor.class);

		}

		else if ((ReportTypeConstants.PROCESSED_SUMMARY
				.equalsIgnoreCase(reportCateg)
				|| ReportTypeConstants.REVIEW_SUMMARY
						.equalsIgnoreCase(reportCateg))
				&& ("OUTWARD".equalsIgnoreCase(dataType) 
						|| "OUTWARD_1A".equalsIgnoreCase(dataType))
				&& (ReportTypeConstants.GSTNERROR.equalsIgnoreCase(reportType)
						|| ReportTypeConstants.GSTR1A_GSTNERROR
								.equalsIgnoreCase(reportType))) {
			reportConvertor = (ReportConvertor) StaticContextHolder
					.getBean("AspErrorReportConvertor", ReportConvertor.class);

		} else if ((ReportTypeConstants.GSTR1ENTITYLEVEL.equalsIgnoreCase(
				reportType) ||ReportTypeConstants.GSTR1AENTITYLEVEL.equalsIgnoreCase(
						reportType) ) && ("OUTWARD".equalsIgnoreCase(dataType) 
						|| "Outward_1A".equalsIgnoreCase(dataType))) {
			reportConvertor = (ReportConvertor) StaticContextHolder.getBean(
					"Gstr1EntityLevelReportConvertor", ReportConvertor.class);

		}

		else if ((ReportTypeConstants.PROCESSED_SUMMARY
				.equalsIgnoreCase(reportCateg)
				|| ReportTypeConstants.REVIEW_SUMMARY
						.equalsIgnoreCase(reportCateg))
				&& "INWARD".equalsIgnoreCase(dataType)
				&& ReportTypeConstants.GSTR2PROCESS
						.equalsIgnoreCase(reportType)) {
			reportConvertor = (ReportConvertor) StaticContextHolder.getBean(
					"Gstr2ProcessedReportConvertor", ReportConvertor.class);

		}

		else if ((ReportTypeConstants.PROCESSED_SUMMARY
				.equalsIgnoreCase(reportCateg)
				|| ReportTypeConstants.REVIEW_SUMMARY
						.equalsIgnoreCase(reportCateg))
				&& "INWARD".equalsIgnoreCase(dataType)
				&& ReportTypeConstants.GSTR6PROCESS
						.equalsIgnoreCase(reportType)) {
			reportConvertor = (ReportConvertor) StaticContextHolder.getBean(
					"Gstr6ProcessedReportConvertor", ReportConvertor.class);

		}

		else if ((ReportTypeConstants.GET_GSTR1_EINVOICE
				.equalsIgnoreCase(reportCateg))
				&& "OUTWARD".equalsIgnoreCase(dataType)
				&& ReportTypeConstants.GSTR1ENVPROCESS
						.equalsIgnoreCase(reportType)) {
			reportConvertor = (ReportConvertor) StaticContextHolder
					.getBean("Gstr1EInvReportConvertor", ReportConvertor.class);

			// DigiGST error Report
		} else if ((ReportTypeConstants.PROCESSED_SUMMARY
				.equalsIgnoreCase(reportCateg) || ReportTypeConstants.REVIEW_SUMMARY
				.equalsIgnoreCase(reportCateg))
				&& "INWARD".equalsIgnoreCase(dataType)
				&& "Consolidated DigiGST Error".contains(reportType)) {

			reportConvertor = (ReportConvertor) StaticContextHolder.getBean(
					"InwardErrorsFileStatusConvertor", ReportConvertor.class);
		}

		// Recon Tagging report
		else if ((ReportTypeConstants.PROCESSED_SUMMARY
				.equalsIgnoreCase(reportCateg) || ReportTypeConstants.REVIEW_SUMMARY
				.equalsIgnoreCase(reportCateg))
				&& "INWARD".equalsIgnoreCase(dataType)
				&& "Processed Records (Recon Tagging)".contains(reportType)) {

			reportConvertor = (ReportConvertor) StaticContextHolder.getBean(
					"Gstr2ProcessedReconTaggingReportConvertor",
					ReportConvertor.class);
		}

		return new ReportConfig(headersKey, columnMappingsKey, reportConvertor);
	}

}
