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
public class ReverseAndNonReverseChargeSupplies {
	
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
	@SerializedName("b2b")
	private B2B  b2bItem;
	
	@Expose
	@SerializedName("b2ba")
	private B2BA  b2bAItem;
	
	@Expose
	@SerializedName("cdnr")
	private CDNR  cdnrItem;
	
	@Expose
	@SerializedName("cdnra")
	private CDNRA  cdnrAItem;
	
	@Expose
	@SerializedName("ecom")
	private ECOM  ecomItem;
	
	@Expose
	@SerializedName("ecoma")
	private ECOMA  ecomAItem;
	
}
