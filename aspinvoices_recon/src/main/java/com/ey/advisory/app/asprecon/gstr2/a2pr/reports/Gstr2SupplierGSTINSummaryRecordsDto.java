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
@NoArgsConstructor
@AllArgsConstructor
public class Gstr2SupplierGSTINSummaryRecordsDto {
	
	 private String supplierGSTINPR;
	 private String supplierName2A;
	 private String exactMatchGST2A;
	 private String exactMatchGSTPR;
	 private String additionIn2AGST2A;
	 private String additionInPRGSTPR;
	 private String forceGSTR3BMatchGST2A;
	 private String forceGSTR3BMatchGSTPR;
	 private String matchWithTolerenceGST2A;
	 private String matchWithTolerenceGSTPR;
	 private String docNoMismatchIGST2A;
	 private String docNoMismatchIGSTPR;
	 private String docNoMismatchIIGST2A;
	 private String docNoMismatchIIGSTPR;
	 private String valueMismatchGST2A;
	 private String valueMismatchGSTPR;
	 private String posMismatchGST2A;
	 private String posMismatchGSTPR;
	 private String potentialIGST2A;
	 private String potentialIGSTPR;
	 private String potentialIIGST2A;
	 private String potentialIIGSTPR;
	 private String logicalMatchGST2A;
	 private String logicalMatchGSTPR;
	 private String totalGST2A;
	 private String totalGSTPR;
	 private String differencePR2A;
	 private String docDateMismatchGST2A;
	 private String docDateMismatchGSTPR;
	 private String docTypeMismatchGST2A;
	 private String docTypeMismatchGSTPR;
	 private String multiMismatchGST2A;
	 private String multiMismatchGSTPR;
	 private String docNoDocDateMismatch2A;
	 private String docNoDocDateMismatchPR;

}
