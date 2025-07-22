package com.ey.advisory.app.gstr3b;

import java.math.BigDecimal;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */
@Data
public class Itc4cDto {
	
	private BigDecimal igst = BigDecimal.ZERO;
	
	private BigDecimal cgst = BigDecimal.ZERO;
	
	private BigDecimal sgst = BigDecimal.ZERO;
	
	private BigDecimal cess = BigDecimal.ZERO;

}
