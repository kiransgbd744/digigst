/**
 * 
 */
package com.ey.advisory.app.docs.dto.gstr9;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author vishal.verma
 *
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Gstr9DownloadInnerDto {
	
	private String gstin;
	
	private String subSection;
	
	private String natureOfSupply;
	
	
	private BigDecimal igst = BigDecimal.ZERO;

	private BigDecimal cgst = BigDecimal.ZERO;

	private BigDecimal sgst = BigDecimal.ZERO;

	private BigDecimal cess = BigDecimal.ZERO;
	
	private BigDecimal taxableValue = BigDecimal.ZERO;
	
	private BigDecimal interest = BigDecimal.ZERO;
	
	private BigDecimal lateFee = BigDecimal.ZERO;
	
	private BigDecimal penalty = BigDecimal.ZERO;
	
	private BigDecimal other = BigDecimal.ZERO;
	
	private String descption;

	public Gstr9DownloadInnerDto merge(Gstr9DownloadInnerDto that) {

		BigDecimal igst = this.igst.add(that.igst);
		BigDecimal cgst = this.cgst.add(that.cgst);
		BigDecimal sgst = this.sgst.add(that.sgst);
		BigDecimal cess = this.cess.add(that.cess);
		BigDecimal taxableValue = this.taxableValue.add(that.taxableValue);
		BigDecimal interest = this.interest.add(that.interest);

		BigDecimal lateFee = this.lateFee.add(that.lateFee);
		BigDecimal penalty = this.penalty.add(that.penalty);
		BigDecimal other = this.other.add(that.other);
		return new Gstr9DownloadInnerDto(gstin, "7H", null, igst, cgst, sgst,
				cess, taxableValue, interest, lateFee, penalty, other, null);
	}

	
}
