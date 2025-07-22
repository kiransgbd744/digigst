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

	@Getter
	@Setter
	@ToString
	@AllArgsConstructor
	@NoArgsConstructor
	public class Gstr2VendorPANPeriodWiseRecordsDto {
		
		 private String SupplierPan;
		 private String SupplierNamePR;
		 private String PeriodPR;
		 private String DocType;
		 private String TotalCount;
		 private String TotalTaxable;
		 private String TotalIGST;
		 private String TotalCGST;
		 private String TotalSGST;
		 private String TotalCESS;
		 private String exactMatchCount;
		 private String exactMatchTaxable;
		 private String exactMatchIGST;
		 private String exactMatchCGST;
		 private String exactMatchSGST;
		 private String exactMatchCESS;
		 private String matchWithTolerenceCount;
		 private String matchWithTolerenceTaxable;
		 private String matchWithTolerenceIGST;
		 private String matchWithTolerenceCGST;
		 private String matchWithTolerenceSGST;
		 private String matchWithTolerenceCESS;
		 private String valueMismatchCount;
		 private String valueMismatchTaxable;
		 private String valueMismatchIGST;
		 private String valueMismatchCGST;
		 private String valueMismatchSGST;
		 private String valueMismatchCESS;
		 private String multiMismatchCount;
		 private String multiMismatchTaxable;
		 private String multiMismatchIGST;
		 private String multiMismatchCGST;
		 private String multiMismatchSGST;
		 private String multiMismatchCESS;
		 private String additionCount2A;
		 private String additionTaxable2A;
		 private String additionIGST2A;
		 private String additionCGST2A;
		 private String additionSGST2A;
		 private String additionCESS2A;
		 private String additionCountPR;
		 private String additionTaxablePR;
		 private String additionIGSTPR;
		 private String additionCGSTPR;
		 private String additionSGSTPR;
		 private String additionCESSPR;
		 private String potentialMatch1Count;
		 private String potentialMatch1Taxable;
		 private String potentialMatch1IGST;
		 private String potentialMatch1CGST;
		 private String potentialMatch1SGST;
		 private String potentialMatch1CESS;
		 private String potentialMatch2Count;
		 private String potentialMatch2Taxable;
		 private String potentialMatch2IGST;
		 private String potentialMatch2CGST;
		 private String potentialMatch2SGST;
		 private String potentialMatch2CESS;
		 private String documentNumberMishmatch1Count;
		 private String documentNumberMishmatch1Taxable;
		 private String documentNumberMishmatch1IGST;
		 private String documentNumberMishmatch1CGST;
		 private String documentNumberMishmatch1SGST;
		 private String documentNumberMishmatch1CESS;
		 private String documentNumberMishmatch2Count;
		 private String documentNumberMishmatch2Taxable;
		 private String documentNumberMishmatch2IGST;
		 private String documentNumberMishmatch2CGST;
		 private String documentNumberMishmatch2SGST;
		 private String documentNumberMishmatch2CESS;
		 private String documentDateMishmatchCount;
		 private String documentDateMishmatchTaxable;
		 private String documentDateMishmatchIGST;
		 private String documentDateMishmatchCGST;
		 private String documentDateMishmatchSGST;
		 private String documentDateMishmatchCESS;
		 private String documentTypeMishmatchCount;
		 private String documentTypeMishmatchTaxable;
		 private String documentTypeMishmatchIGST;
		 private String documentTypeMishmatchCGST;
		 private String documentTypeMishmatchSGST;
		 private String documentTypeMishmatchCESS;
		 private String logicalMatchCount;
		 private String logicalMatchTaxable;
		 private String logicalMatchIGST;
		 private String logicalMatchCGST;
		 private String logicalMatchSGST;
		 private String logicalMatchCESS;
		 private String posMismatchCount;
		 private String posMismatchTaxable;
		 private String posMismatchIGST;
		 private String posMismatchCGST;
		 private String posMismatchSGST;
		 private String posMismatchCESS;
		 private String forceGSTR3BMatchCount;
		 private String forceGSTR3BMatchTaxable;
		 private String forceGSTR3BMatchIGST;
		 private String forceGSTR3BMatchCGST;
		 private String forceGSTR3BMatchSGST;
		 private String forceGSTR3BMatchCESS;
		 private String docNoDocDateMisMatchCount;
		 private String docNoDocDateMisMatchTaxable;
		 private String docNoDocDateMisMatchIGST;
		 private String docNoDocDateMisMatchCGST;
		 private String docNoDocDateMisMatchSGST;
		 private String docNoDocDateMisMatchCESS;
	}
