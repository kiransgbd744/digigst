/**
 * 
 */
package com.ey.advisory.services.gstr3b.itc.reclaim;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Data
public class Gstr3BValidateItcReclaimDto {

	@Expose
	@SerializedName("igst")
	private BigDecimal igst = BigDecimal.ZERO;
	
	@Expose
	@SerializedName("cgst")
	private BigDecimal cgst = BigDecimal.ZERO;
	
	@Expose
	@SerializedName("sgst")
	private BigDecimal sgst = BigDecimal.ZERO;
	
	@Expose
	@SerializedName("cess")
	private BigDecimal cess = BigDecimal.ZERO;
	
	@Expose
	@SerializedName("sectionName")
	private String sectionName;
	
	@Expose
	@SerializedName("timeStamp")
	private String timeStamp;
	
	private int order = 0;

}
