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
public class Gstr9CompositionDeemedSupplyMapDto {
	
	@Expose
	private String subSection;
	
	@Expose
	private BigDecimal igst = BigDecimal.ZERO;

	@Expose
	private BigDecimal cgst = BigDecimal.ZERO;

	@Expose
	private BigDecimal sgst = BigDecimal.ZERO;

	@Expose
	private BigDecimal cess = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal taxableValue = BigDecimal.ZERO;

}
