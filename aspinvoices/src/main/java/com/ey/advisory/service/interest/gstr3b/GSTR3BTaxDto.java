/**
 * 
 */
package com.ey.advisory.service.interest.gstr3b;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Data
public class GSTR3BTaxDto {

	@Expose
	@SerializedName("iamt")
	private BigDecimal igst = BigDecimal.ZERO;
	
	@Expose
	@SerializedName("camt")
	private BigDecimal cgst = BigDecimal.ZERO;
	
	@Expose
	@SerializedName("samt")
	private BigDecimal sgst = BigDecimal.ZERO;
	
	@Expose
	@SerializedName("csamt")
	private BigDecimal cess = BigDecimal.ZERO;
}
