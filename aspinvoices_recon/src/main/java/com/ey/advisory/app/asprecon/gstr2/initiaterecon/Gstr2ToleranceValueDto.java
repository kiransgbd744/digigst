package com.ey.advisory.app.asprecon.gstr2.initiaterecon;


import java.math.BigDecimal;

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
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Gstr2ToleranceValueDto {
	
	private BigDecimal taxableVal = BigDecimal.ZERO;
	
	private BigDecimal totalTax = BigDecimal.ZERO;
	
	private BigDecimal igst = BigDecimal.ZERO;
	
	private BigDecimal cgst = BigDecimal.ZERO;
	
	private BigDecimal sgst = BigDecimal.ZERO;
	
	private BigDecimal cess = BigDecimal.ZERO;
}
