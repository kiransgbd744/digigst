/**
 * 
 */
package com.ey.advisory.services.gstr3b.entity.setoff;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Data
public class Gstr3bDetailsDto {
	
	private String desc;
	
	private BigDecimal otrci = BigDecimal.ZERO;

	private BigDecimal pdi = BigDecimal.ZERO;

	private BigDecimal pdc = BigDecimal.ZERO;

	private BigDecimal pds = BigDecimal.ZERO;

	private BigDecimal pdcs = BigDecimal.ZERO;

	private BigDecimal otrc7 = BigDecimal.ZERO;
	
	private BigDecimal rci8 = BigDecimal.ZERO;

	private BigDecimal inti10 = BigDecimal.ZERO;
	
	private BigDecimal lateFee12 = BigDecimal.ZERO;

	private BigDecimal ucb14 = BigDecimal.ZERO;

	private BigDecimal acr15 = BigDecimal.ZERO;
	
	private BigDecimal otrci2A = BigDecimal.ZERO;
	
	private BigDecimal otrci2B = BigDecimal.ZERO;
	
	//Negative liability
	private BigDecimal adjNegative2i  = BigDecimal.ZERO;
	
	private BigDecimal netOthRecTaxPayable2i = BigDecimal.ZERO;
	
	private BigDecimal adjNegative8A = BigDecimal.ZERO;
	
	private BigDecimal rci9 = BigDecimal.ZERO;//8-8a
	
	
	

}
