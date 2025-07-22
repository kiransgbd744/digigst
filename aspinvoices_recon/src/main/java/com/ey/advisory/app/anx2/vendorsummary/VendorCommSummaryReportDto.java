package com.ey.advisory.app.anx2.vendorsummary;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class VendorCommSummaryReportDto {

	private String reportType;
	private String prCountOfTrans;
	private String prTaxableValue;
	private String prTotalTax;
	private String gstr2ACountOfTrans;
	private String gstr2ATaxableValue;
	private String gstr2ATotalTax;
	private String diffCountOfTrans;
	private String diffTaxableValue;
	private String diffTotalTax;
	
}
