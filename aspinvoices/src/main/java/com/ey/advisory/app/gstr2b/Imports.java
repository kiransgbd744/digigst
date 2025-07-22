package com.ey.advisory.app.gstr2b;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Vishal.Verma
 *
 */

@Data
public class Imports {
	
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
	@SerializedName("impg")
	private IMPG  impgItem;
	
	@Expose
	@SerializedName("impgsez")
	private IMPGSez  impgSezItem;
	
	@Expose
	@SerializedName("impga")
	private IMPGA  impgAItem;
	
	@Expose
	@SerializedName("impgasez")
	private IMPGASez  impgASezItem;
	

}
