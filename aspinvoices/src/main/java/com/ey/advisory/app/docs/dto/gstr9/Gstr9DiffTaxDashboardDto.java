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
public class Gstr9DiffTaxDashboardDto {

	@Expose
	private String subSection;
	
	@Expose
	private BigDecimal userInputTaxPayble = BigDecimal.ZERO;

	@Expose
	private BigDecimal gstinTaxPayble = BigDecimal.ZERO;

	@Expose
	private BigDecimal userInputTaxPaid = BigDecimal.ZERO;

	@Expose
	private BigDecimal gstinTaxPaid = BigDecimal.ZERO;
	
	
	@Expose
	private BigDecimal gstnAutoCalTaxPaid = BigDecimal.ZERO;

	@Expose
	private BigDecimal gstnAutoCalPayble = BigDecimal.ZERO;

	

	public Gstr9DiffTaxDashboardDto(String subSection) {
		super();
		this.subSection = subSection;
	}
}
