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
public class GSTR9TaxPaidReportDownloadDto {

	private String gstin;
	
	private String subSection;
	
	private String natureOfSupply;
	
	
	private BigDecimal taxPayableFiledAutoComp = BigDecimal.ZERO;
	
	 private BigDecimal pdCashFiledAutoComp = BigDecimal.ZERO;
	 
	 private BigDecimal pdIgstFiledAutoComp = BigDecimal.ZERO;
	 
	 private BigDecimal pdCgstFiledAutoComp = BigDecimal.ZERO;
	 
	 private BigDecimal pdSgstFiledAutoComp = BigDecimal.ZERO;
	 
	 private BigDecimal pdCessFiledAutoComp = BigDecimal.ZERO;
	 
	 
	 private BigDecimal taxPayableAutoCal = BigDecimal.ZERO;

	 private BigDecimal pdCashAutoCal = BigDecimal.ZERO;
	 
	 private BigDecimal pdIgstAutoCal = BigDecimal.ZERO;
	 
	 private BigDecimal pdCgstAutoCal = BigDecimal.ZERO;
	 
	 private BigDecimal pdSgstAutoCal = BigDecimal.ZERO;
	 
	 private BigDecimal pdCessAutoCal = BigDecimal.ZERO;
	 
	 
	 private BigDecimal taxPayableUserInput = BigDecimal.ZERO;

	 private BigDecimal pdCashUserInput = BigDecimal.ZERO;
	 
	 private BigDecimal pdIgstUserInput = BigDecimal.ZERO;
	 
	 private BigDecimal pdCgstUserInput = BigDecimal.ZERO;
	 
	 private BigDecimal pdSgstUserInput = BigDecimal.ZERO;
	 
	 private BigDecimal pdCessUserInput = BigDecimal.ZERO;
	 
	 
	 private BigDecimal taxPayableGstn = BigDecimal.ZERO;

	 private BigDecimal pdCashGstn = BigDecimal.ZERO;
	 
	 private BigDecimal pdIgstGstn = BigDecimal.ZERO;
	 
	 private BigDecimal pdCgstGstn = BigDecimal.ZERO;
	 
	 private BigDecimal pdSgstGstn = BigDecimal.ZERO;
	 
	 private BigDecimal pdCessGstn = BigDecimal.ZERO;

	 
	 private BigDecimal taxPayableDiff = BigDecimal.ZERO;

	 private BigDecimal pdCashDiff = BigDecimal.ZERO;
	 
	 private BigDecimal pdIgstDiff = BigDecimal.ZERO;
	 
	 private BigDecimal pdCgstDiff = BigDecimal.ZERO;
	 
	 private BigDecimal pdSgstDiff = BigDecimal.ZERO;
	 
	 private BigDecimal pdCessDiff = BigDecimal.ZERO;
	
	
	
	public GSTR9TaxPaidReportDownloadDto(String gstin, String subSection,
			String natureOfSuply) {
		super();
		this.gstin = gstin;
		this.subSection = subSection;
		this.natureOfSupply = natureOfSuply;

	}
}
