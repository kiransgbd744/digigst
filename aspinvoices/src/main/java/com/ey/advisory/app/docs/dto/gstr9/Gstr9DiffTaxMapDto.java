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
public class Gstr9DiffTaxMapDto {
	
	@Expose
	private String subSection;

	@Expose
	private BigDecimal taxPayable = BigDecimal.ZERO;

	@Expose
	private BigDecimal taxPaid = BigDecimal.ZERO;

}
