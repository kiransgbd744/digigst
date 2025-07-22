/**
 * 
 */
package com.ey.advisory.app.docs.dto.gstr9;

import java.math.BigDecimal;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */
@Data
public class GSTR9ReportDownloadDto {

	private String gstin;
	
	private String subSection;
	
	private String natureOfSupply;
	
	
	private BigDecimal digiIgstProAutoComp = BigDecimal.ZERO;

	private BigDecimal digiCgstProAutoComp = BigDecimal.ZERO;

	private BigDecimal digiSgstProAutoComp = BigDecimal.ZERO;

	private BigDecimal digiCessProAutoComp = BigDecimal.ZERO;
	
	private BigDecimal digiTaxableValueProAutoComp = BigDecimal.ZERO;
	
	private BigDecimal digiInterestProAutoComp = BigDecimal.ZERO;
	
	private BigDecimal digiLateFeeProAutoComp = BigDecimal.ZERO;
	
	private BigDecimal digiPenaltyProAutoComp = BigDecimal.ZERO;
	
	private BigDecimal digiOtherProAutoComp = BigDecimal.ZERO;
	
	
	private BigDecimal igstProAutoComp = BigDecimal.ZERO;

	private BigDecimal cgstProAutoComp = BigDecimal.ZERO;

	private BigDecimal sgstProAutoComp = BigDecimal.ZERO;

	private BigDecimal cessProAutoComp = BigDecimal.ZERO;
	
	private BigDecimal taxableValueProAutoComp = BigDecimal.ZERO;
	
	private BigDecimal interestProAutoComp = BigDecimal.ZERO;
	
	private BigDecimal lateFeeProAutoComp = BigDecimal.ZERO;
	
	private BigDecimal penaltyProAutoComp = BigDecimal.ZERO;
	
	private BigDecimal otherProAutoComp = BigDecimal.ZERO;
	
	
	private BigDecimal igstFiledAutoComp = BigDecimal.ZERO;

	private BigDecimal cgstFiledAutoComp = BigDecimal.ZERO;

	private BigDecimal sgstFiledAutoComp = BigDecimal.ZERO;

	private BigDecimal cessFiledAutoComp = BigDecimal.ZERO;
	
	private BigDecimal taxableValueFiledAutoComp = BigDecimal.ZERO;
	
	private BigDecimal interestFiledAutoComp = BigDecimal.ZERO;
	
	private BigDecimal lateFeeFiledAutoComp = BigDecimal.ZERO;
	
	private BigDecimal penaltyFiledAutoComp = BigDecimal.ZERO;
	
	private BigDecimal otherFiledAutoComp = BigDecimal.ZERO;
	
	
	private BigDecimal igstAutoCal = BigDecimal.ZERO;

	private BigDecimal cgstAutoCal = BigDecimal.ZERO;

	private BigDecimal sgstAutoCal = BigDecimal.ZERO;

	private BigDecimal cessAutoCal = BigDecimal.ZERO;
	
	private BigDecimal taxableValueAutoCal = BigDecimal.ZERO;
	
	private BigDecimal interestAutoCal = BigDecimal.ZERO;
	
	private BigDecimal lateFeeAutoCal = BigDecimal.ZERO;
	
	private BigDecimal penaltyAutoCal = BigDecimal.ZERO;
	
	private BigDecimal otherAutoCal = BigDecimal.ZERO;
	
	
	private BigDecimal igstUserInput = BigDecimal.ZERO;

	private BigDecimal cgstUserInput = BigDecimal.ZERO;

	private BigDecimal sgstUserInput = BigDecimal.ZERO;

	private BigDecimal cessUserInput = BigDecimal.ZERO;
	
	private BigDecimal taxableValueUserInput = BigDecimal.ZERO;
	
	private BigDecimal interestUserInput = BigDecimal.ZERO;
	
	private BigDecimal lateFeeUserInput = BigDecimal.ZERO;
	
	private BigDecimal penaltyUserInput = BigDecimal.ZERO;
	
	private BigDecimal otherUserInput = BigDecimal.ZERO;
	
	
	private BigDecimal igstGstn = BigDecimal.ZERO;

	private BigDecimal cgstGstn = BigDecimal.ZERO;

	private BigDecimal sgstGstn = BigDecimal.ZERO;

	private BigDecimal cessGstn = BigDecimal.ZERO;
	
	private BigDecimal taxableValueGstn = BigDecimal.ZERO;
	
	private BigDecimal interestGstn = BigDecimal.ZERO;
	
	private BigDecimal lateFeeGstn = BigDecimal.ZERO;
	
	private BigDecimal penaltyGstn = BigDecimal.ZERO;
	
	private BigDecimal otherGstn = BigDecimal.ZERO;
	
	
	private BigDecimal igstDiff = BigDecimal.ZERO;

	private BigDecimal cgstDiff = BigDecimal.ZERO;

	private BigDecimal sgstDiff = BigDecimal.ZERO;

	private BigDecimal cessDiff = BigDecimal.ZERO;
	
	private BigDecimal taxableValueDiff = BigDecimal.ZERO;
	
	private BigDecimal interestDiff = BigDecimal.ZERO;
	
	private BigDecimal lateFeeDiff = BigDecimal.ZERO;
	
	private BigDecimal penaltyDiff = BigDecimal.ZERO;
	
	private BigDecimal otherDiff = BigDecimal.ZERO;
	
	
	public GSTR9ReportDownloadDto(String gstin, String subSection,
			String natureOfSuply) {
		super();
		this.gstin = gstin;
		this.subSection = subSection;
		this.natureOfSupply = natureOfSuply;

	}
	
}
