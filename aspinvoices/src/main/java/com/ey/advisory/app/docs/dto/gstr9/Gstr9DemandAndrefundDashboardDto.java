/**
 * 
 */
package com.ey.advisory.app.docs.dto.gstr9;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */
@Data
public class Gstr9DemandAndrefundDashboardDto {

	@Expose
	private String subSection;
	
	@Expose
	private BigDecimal igstUserInput = BigDecimal.ZERO;

	@Expose
	private BigDecimal cgstUserInput = BigDecimal.ZERO;

	@Expose
	private BigDecimal sgstUserInput = BigDecimal.ZERO;

	@Expose
	private BigDecimal cessUserInput = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal interestUserInput = BigDecimal.ZERO;

	@Expose
	private BigDecimal penaltyUserInput = BigDecimal.ZERO;

	@Expose
	private BigDecimal lateFeeUserInput = BigDecimal.ZERO;
	
	
	@Expose
	private BigDecimal igstGstn = BigDecimal.ZERO;

	@Expose
	private BigDecimal cgstGstn = BigDecimal.ZERO;

	@Expose
	private BigDecimal sgstGstn = BigDecimal.ZERO;

	@Expose
	private BigDecimal cessGstn = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal interestGstn = BigDecimal.ZERO;

	@Expose
	private BigDecimal penaltyGstn = BigDecimal.ZERO;

	@Expose
	private BigDecimal lateFeeGstn = BigDecimal.ZERO;
	


	@Expose
	private BigDecimal igstGstnCompute = BigDecimal.ZERO;

	@Expose
	private BigDecimal cgstGstnCompute = BigDecimal.ZERO;

	@Expose
	private BigDecimal sgstGstnCompute = BigDecimal.ZERO;

	@Expose
	private BigDecimal cessGstnCompute = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal interestGstnCompute = BigDecimal.ZERO;

	@Expose
	private BigDecimal penaltyGstnCompute = BigDecimal.ZERO;

	@Expose
	private BigDecimal lateFeeGstnCompute = BigDecimal.ZERO;

	

	public Gstr9DemandAndrefundDashboardDto(String subSection) {
		super();
		this.subSection = subSection;
	}
}
