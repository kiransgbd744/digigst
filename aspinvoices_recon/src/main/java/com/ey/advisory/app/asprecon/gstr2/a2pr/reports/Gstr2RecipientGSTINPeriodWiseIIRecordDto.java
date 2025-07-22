package com.ey.advisory.app.asprecon.gstr2.a2pr.reports;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Ravindra V S
 *
 */

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Gstr2RecipientGSTINPeriodWiseIIRecordDto {
	
	 	private String recipientsGstin2B;
	    private String reportType;
	    private String totalTaxComparison;
	    private String taxPeriod2B;
	    private String reverseChargeFlag2B;
	    private String docType2B;
	    private String recordsCount2B;
	    private String taxableValue2B;
	    private String igst2B;
	    private String cgst2B;
	    private String sgst2B;
	    private String cess2B;
	    private String totalTax2B;

	    // Variables for PR
	    private String recordsCountPR;
	    private String taxableValuePR;
	    private String igstPR;
	    private String cgstPR;
	    private String sgstPR;
	    private String cessPR;
	    private String totalTaxPR;
	    private String availableIgstPR;
	    private String availableCgstPR;
	    private String availableSgstPR;
	    private String availableCessPR;
	    private String totalAvailableTaxPR;
	    private String ineligibleIgstPR;
	    private String ineligibleCgstPR;
	    private String ineligibleSgstPR;
	    private String ineligibleCessPR;
	    private String totalIneligibleTaxPR;
	 
}
