package com.ey.advisory.common;

import java.util.List;

import org.javatuples.Pair;
import org.springframework.stereotype.Component;

import com.ey.advisory.core.api.APIConstants;
import com.google.common.collect.ImmutableList;

@Component("ReportConfigFactoryImpl")
public class ReportConfigFactoryImpl implements ReportConfigFactory {

	private static final List<String> fileStatusReportTypeList = ImmutableList
			.of(ReportTypeConstants.ERROR_BV, ReportTypeConstants.ERROR_SV,
					ReportTypeConstants.PROCESSED_RECORDS,
					ReportTypeConstants.TOTAL_ERRORS,
					ReportTypeConstants.TOTAL_RECORDS);

	private static final List<String> dataStatusReportTypeList = ImmutableList
			.of(ReportTypeConstants.ERROR,
					ReportTypeConstants.PROCESSED_RECORDS,
					ReportTypeConstants.TOTAL_RECORDS);

	private static final List<String> fileStatusEinvReportTypeList = ImmutableList
			.of(ReportTypeConstants.EINV_NOT_APPLICABLE,
					ReportTypeConstants.EINV_APPLICABLE,
					ReportTypeConstants.EINV_IRN_GENERATE,
					ReportTypeConstants.EINV_IRN_CANCELED,
					ReportTypeConstants.EINV_ERROR,
					ReportTypeConstants.EINV_ERROR_FROM_IRP);

	private static final String OUTWARD = "OUTWARD";
	private static final String INWARD = "INWARD";
	private static final String ISD = "ISD";
	private static final String GSTR6A = "GSTR6A";

	@Override
	public ReportConfig getReportConfig(String reportType, String reportCateg,
			String dataType) {
		ReportConvertor reportConvertor = null;

		Pair<String, String> mapping = CommonUtility
				.getReportHeaderMapping(reportType, reportCateg, dataType);
		String headersKey = mapping.getValue0();
		String columnMappingsKey = mapping.getValue1();

		if ((ReportTypeConstants.FILE_STATUS.equalsIgnoreCase(reportCateg)
				|| ReportTypeConstants.SFTP.equalsIgnoreCase(reportCateg))
				&& (OUTWARD.equalsIgnoreCase(dataType) || "OUTWARD_1A".equalsIgnoreCase(dataType))
				&& (fileStatusReportTypeList.contains(reportType)
						|| fileStatusEinvReportTypeList.contains(reportType))) {
			reportConvertor = StaticContextHolder.getBean(
					"BusinessValidationReportConvertor", ReportConvertor.class);
		} else if (ReportTypeConstants.DATA_STATUS.equalsIgnoreCase(reportCateg)
				&& OUTWARD.equalsIgnoreCase(dataType)
				&& dataStatusReportTypeList.contains(reportType)) {
			reportConvertor = StaticContextHolder.getBean(
					"DataStatusReportConvertor", ReportConvertor.class);

		} else if ((ReportTypeConstants.FILE_STATUS
				.equalsIgnoreCase(reportCateg)
				|| ReportTypeConstants.SFTP.equalsIgnoreCase(reportCateg))
				&& INWARD.equalsIgnoreCase(dataType)
				&& ReportTypeConstants.TOTAL_RECORDS
						.equalsIgnoreCase(reportType)) {
			reportConvertor = StaticContextHolder.getBean(
					"InwardTotalRecordsFileStatusConvertor",
					ReportConvertor.class);

		} else if ((ReportTypeConstants.FILE_STATUS
				.equalsIgnoreCase(reportCateg)
				|| ReportTypeConstants.SFTP.equalsIgnoreCase(reportCateg))
				&& INWARD.equalsIgnoreCase(dataType)
				&& ReportTypeConstants.PROCESSED_RECORDS
						.equalsIgnoreCase(reportType)) {
			reportConvertor = StaticContextHolder.getBean(
					"InwardProcessedFileStatusConvertor",
					ReportConvertor.class);

		} else if ((ReportTypeConstants.FILE_STATUS
				.equalsIgnoreCase(reportCateg)
				|| ReportTypeConstants.SFTP.equalsIgnoreCase(reportCateg))
				&& INWARD.equalsIgnoreCase(dataType)
				&& (ReportTypeConstants.ERROR_BV.equalsIgnoreCase(reportType)
						|| ReportTypeConstants.ERROR_SV
								.equalsIgnoreCase(reportType)
						|| ReportTypeConstants.TOTAL_ERRORS
								.equalsIgnoreCase(reportType))) {
			reportConvertor = StaticContextHolder.getBean(
					"InwardErrorsFileStatusConvertor", ReportConvertor.class);
		} else if (ReportTypeConstants.DATA_STATUS.equalsIgnoreCase(reportCateg)
				&& INWARD.equalsIgnoreCase(dataType)
				&& ReportTypeConstants.TOTAL_RECORDS
						.equalsIgnoreCase(reportType)) {
			reportConvertor = StaticContextHolder.getBean(
					"InwardTotalRecordsDataStatusConvertor",
					ReportConvertor.class);

		} else if (ReportTypeConstants.DATA_STATUS.equalsIgnoreCase(reportCateg)
				&& INWARD.equalsIgnoreCase(dataType)
				&& ReportTypeConstants.PROCESSED_RECORDS
						.equalsIgnoreCase(reportType)) {
			reportConvertor = StaticContextHolder.getBean(
					"InwardProcessedDataStatusConvertor",
					ReportConvertor.class);

		} else if (ReportTypeConstants.DATA_STATUS.equalsIgnoreCase(reportCateg)
				&& INWARD.equalsIgnoreCase(dataType)
				&& ReportTypeConstants.ERROR.equalsIgnoreCase(reportType)) {
			reportConvertor = StaticContextHolder.getBean(
					"InwardErrorsDataStatusConvertor", ReportConvertor.class);
		} else if ((ReportTypeConstants.EINVOICE_RECON
				.equalsIgnoreCase(reportCateg))
				&& OUTWARD.equalsIgnoreCase(dataType)
				&& (ReportTypeConstants.EINVPROCESS.equalsIgnoreCase(reportType)
						|| ReportTypeConstants.ERROR
								.equalsIgnoreCase(reportType))) {
			reportConvertor = StaticContextHolder.getBean(
					"EinvoiceReconReportConverter", ReportConvertor.class);
		} else if ((ReportTypeConstants.ISD_RECON.equalsIgnoreCase(reportCateg))
				&& ISD.equalsIgnoreCase(dataType)
				&& (ReportTypeConstants.EINVPROCESS.equalsIgnoreCase(reportType)
						|| ReportTypeConstants.ERROR
								.equalsIgnoreCase(reportType))) {
			reportConvertor = StaticContextHolder.getBean(
					"EinvoiceReconReportConverter", ReportConvertor.class);
		} else if (ReportTypeConstants.EINVSUMMARY.equalsIgnoreCase(reportCateg)
				&& OUTWARD.equalsIgnoreCase(dataType)
				&& ReportTypeConstants.EINVCONSOLIDATED
						.equalsIgnoreCase(reportType)) {
			reportConvertor = StaticContextHolder.getBean(
					"EinvoiceReconReportConverter", ReportConvertor.class);
		} else if ("GSTR3B".equalsIgnoreCase(reportCateg)
				&& "GSTR3B".equalsIgnoreCase(dataType)
				&& ReportTypeConstants.GSTR3B_180DAYS_REV_RESP
						.equalsIgnoreCase(reportType)) {
			reportConvertor = StaticContextHolder.getBean(
					"Gstr3B180daysReversalRespReportConvertor",
					ReportConvertor.class);
		} else if ((ReportTypeConstants.FILE_STATUS
				.equalsIgnoreCase(reportCateg) && ISD.equalsIgnoreCase(dataType)
				|| ReportTypeConstants.SFTP.equalsIgnoreCase(reportCateg)
						&& ISD.equalsIgnoreCase(dataType))
				|| "SFTPSend".equalsIgnoreCase(reportCateg)
						&& ISD.equalsIgnoreCase(dataType)) {
			reportConvertor = StaticContextHolder
					.getBean("IsdReconReportConverter", ReportConvertor.class);
		} else if (GSTR6A.equalsIgnoreCase(reportCateg)
				&& GSTR6A.equalsIgnoreCase(dataType)) {
			reportConvertor = StaticContextHolder.getBean(
					"Gstr6aReconReportConverter", ReportConvertor.class);
		} else if (ReportTypeConstants.ITC_REVERSAL_RULE_42
				.equalsIgnoreCase(reportType)) {
			if ("Inward".equalsIgnoreCase(dataType)) {
				reportConvertor = StaticContextHolder.getBean(
						"CommonCreditInwardReportConverter",
						ReportConvertor.class);
			} else {
				reportConvertor = StaticContextHolder.getBean(
						"CommonCreditOutwardReportConverter",
						ReportConvertor.class);
			}
		} else if (ReportTypeConstants.STOCK_TRACKING_REPORT
				.equalsIgnoreCase(reportType)) {
			reportConvertor = StaticContextHolder.getBean(
					"Itc04StockTrackingReportConvertor", ReportConvertor.class);
		} else if (ReportTypeConstants.GSTR8_PROCESSED_REPORT
				.equalsIgnoreCase(reportType)
				&& APIConstants.GSTR8.toUpperCase()
						.equalsIgnoreCase(dataType)) {
			reportConvertor = StaticContextHolder.getBean(
					"Gstr8ProcessedReportConvertor", ReportConvertor.class);
		}else if (("IMS".equalsIgnoreCase(reportCateg))
				&& INWARD.equalsIgnoreCase(dataType)
				&& (ReportTypeConstants.PROCESSED_RECORDS.equalsIgnoreCase(reportType)
						|| ReportTypeConstants.ERROR.equalsIgnoreCase(reportType)
						|| ReportTypeConstants.TOTAL_RECORDS.equalsIgnoreCase(reportType))) {
			reportConvertor = StaticContextHolder.getBean(
					"ImsFileStatusReportConverter", ReportConvertor.class);
		} else if ("GL_DUMP".equalsIgnoreCase(reportCateg)) {
			reportConvertor = StaticContextHolder.getBean(
					"GLDumpFileStatusReportConverter", ReportConvertor.class);
		}
		else if ("GL Recon".equalsIgnoreCase(reportCateg)) {
			reportConvertor = StaticContextHolder.getBean(
					"GlConsolidatedSummaryReportConvertor", ReportConvertor.class);
		}
		return new ReportConfig(headersKey, columnMappingsKey, reportConvertor);
	} 

}
