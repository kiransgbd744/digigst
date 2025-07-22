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
public class Gstr9CompositionDeemedSupplyDto {

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
	private BigDecimal taxableValueUserInput = BigDecimal.ZERO;

	
	
	@Expose
	private BigDecimal igstGstn = BigDecimal.ZERO;

	@Expose
	private BigDecimal cgstGstn = BigDecimal.ZERO;

	@Expose
	private BigDecimal sgstGstn = BigDecimal.ZERO;

	@Expose
	private BigDecimal cessGstn = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal taxableValueGstn = BigDecimal.ZERO;
	
	
	
	@Expose
	private BigDecimal igstGstnCompute = BigDecimal.ZERO;

	@Expose
	private BigDecimal cgstGstnCompute = BigDecimal.ZERO;

	@Expose
	private BigDecimal sgstGstnCompute = BigDecimal.ZERO;

	@Expose
	private BigDecimal cessGstnCompute = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal taxableValueGstnCompute = BigDecimal.ZERO;


	public Gstr9CompositionDeemedSupplyDto(String subSection) {
		super();
		this.subSection = subSection;
	}
}
