/**
 * 
 */
package com.ey.advisory.services.gstr3b.entity.setoff;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author vishal.verma
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Gstr3BIntrFeeDto {
	
	private BigDecimal igst = BigDecimal.ZERO;
	private BigDecimal cgst = BigDecimal.ZERO;
	private BigDecimal sgst = BigDecimal.ZERO;
	private BigDecimal cess = BigDecimal.ZERO;
	private String sectionName;
	
	
	
	public Gstr3BIntrFeeDto merge(Gstr3BIntrFeeDto that) {
		
		BigDecimal igst = this.igst.add(that.igst);
		BigDecimal cgst = this.cgst.add(that.cgst);
		BigDecimal sgst = this.sgst.add(that.sgst);
		BigDecimal cess = this.cess.add(that.cess);
		
		return new Gstr3BIntrFeeDto(igst,cgst,sgst,cess,null);
	}

}
