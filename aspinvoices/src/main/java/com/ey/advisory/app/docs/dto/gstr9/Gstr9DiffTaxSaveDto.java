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
public class Gstr9DiffTaxSaveDto {
	
	private String subSection;
	private BigDecimal igst = BigDecimal.ZERO;
	private BigDecimal cgst = BigDecimal.ZERO;
	private BigDecimal sgst = BigDecimal.ZERO;
	private BigDecimal cess = BigDecimal.ZERO;
	private BigDecimal intr = BigDecimal.ZERO;

}
