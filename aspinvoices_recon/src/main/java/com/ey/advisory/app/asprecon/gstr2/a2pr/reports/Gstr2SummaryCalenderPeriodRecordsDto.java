package com.ey.advisory.app.asprecon.gstr2.a2pr.reports;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author vishal.verma
 *
 */

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Gstr2SummaryCalenderPeriodRecordsDto {

	 private String calendarPeriod;
	 private String reportType;
	 private String recordsCount2A;
	 private String taxable2A;
	 private String iGST2A;
	 private String cGST2A;
	 private String sGST2A;
	 private String cESS2A;
	 private String totalTax2A;
	 private String recordsCountPR;
	 private String taxablePR;
	 private String iGSTPR;
	 private String cGSTPR;
	 private String sGSTPR;
	 private String cESSPR;
	 private String totalTaxPR;

}
