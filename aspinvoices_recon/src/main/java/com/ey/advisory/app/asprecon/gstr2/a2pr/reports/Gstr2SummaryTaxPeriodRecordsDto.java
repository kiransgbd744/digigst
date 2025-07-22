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
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Gstr2SummaryTaxPeriodRecordsDto {
	
	 private String recipientsGSTIN;
	 private String description;
	 private String docType;
	 private String recordsCount2A;
	 private String recordsPer2A;
	 private String taxableValue2A;
	 private String taxablePer2A;
	 private String iGST2A;
	 private String cGST2A;
	 private String sGST2A;
	 private String cESS2A;
	 private String totalTax2A;
	 private String totalTaxPer2A;
	 private String totalTaxCfsY2A;
	 private String totalTaxCfsN2A;
	 private String recordsCountPR;
	 private String recordPerPR;
	 private String taxableValuePR;
	 private String taxablePerPR;
	 private String iGSTPR;
	 private String cGSTPR;
	 private String sGSTPR;
	 private String cESSPR;
	 private String totalTaxPR;
	 private String totalTaxPerPR;


}
