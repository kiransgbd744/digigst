package com.ey.advisory.app.data.services.compliancerating;

import java.math.BigDecimal;

import lombok.Data;

/**
 * @author Saif.S
 *
 */
@Data
public class VendorComplianceSummaryAsyncReportDto {

	private String gstin;
	private String businessName;
	private String gstinStatus;
	private String regDate;
	private String canDate;
	private String taxPayerType;
	private String lastPeriodOfFilingGstr1;
	private String lastPeriodOfFilingGstr3b;
	private String filingType;
	private BigDecimal averageRanking;
	private BigDecimal avgRatingWithPrevFy;
	private BigDecimal perFiledWithInTime;
	private String totalReturnsFiled;
	private String filedWithInTime;
	private String toalLateFiled;
	private String notFiled;
}
