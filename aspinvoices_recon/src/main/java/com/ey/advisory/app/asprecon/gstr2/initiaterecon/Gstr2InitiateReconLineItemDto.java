/**
 * 
 */
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
public class Gstr2InitiateReconLineItemDto {
	
	private String section;

	private int gstr2Count;

	private BigDecimal gstr2TaxableValue = BigDecimal.ZERO;

	private BigDecimal gstr2IGST = BigDecimal.ZERO;

	private BigDecimal gstr2CGST = BigDecimal.ZERO;

	private BigDecimal gstr2SGST = BigDecimal.ZERO;

	private BigDecimal gstr2Cess = BigDecimal.ZERO;

	private int prCount;

	private BigDecimal prTaxableValue = BigDecimal.ZERO;

	private BigDecimal prIGST = BigDecimal.ZERO;

	private BigDecimal prCGST = BigDecimal.ZERO;

	private BigDecimal prSGST = BigDecimal.ZERO;

	private BigDecimal prCess = BigDecimal.ZERO;

	

}
