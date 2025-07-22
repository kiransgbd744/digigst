package com.ey.advisory.app.gstr2b;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Data
public class OtherSupplies {
	
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
	@SerializedName("cdnr")
	private CDNR  cdnrItem;
	
	@Expose
	@SerializedName("cdnra")
	private CDNRA  cdnrAItem;

	@Expose
	@SerializedName("isd")
	private ISD  isdItem;
	
	@Expose
	@SerializedName("isda")
	private ISDA  isdAItem;
	
	@Expose
	@SerializedName("cdnrrev")
	private CDNRRev cdnrRevItem;
	
	@Expose
	@SerializedName("cdnrarev")
	private CDNRARev cdnrARevItem;

}
