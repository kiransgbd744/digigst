package com.ey.advisory.app.gstr2b;

import java.math.BigDecimal;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */
@Data
public class B2BInvoiceList {
	
	
	@Expose
	@SerializedName("inum")
	private String invoiceNumber;
	
	@Expose
	@SerializedName("typ")
	private String invoiceType;
	
	@Expose
	@SerializedName("dt")
	private String invoiceDate;
	
	@Expose
	@SerializedName("val")
	private BigDecimal invoiceValue = BigDecimal.ZERO;
	
	@Expose
	@SerializedName("pos")
	private String pos;
	
	@Expose
	@SerializedName("rev")
	private String suppRevChrg;
	
	@Expose
	@SerializedName("itcavl")
	private String itcAvail;
	
	@Expose
	@SerializedName("rsn")
	private String reasonForItcUnAvail;
	
	@Expose
	@SerializedName("diffprcnt")
	private BigDecimal diffPrcnt;
	
	@Expose
	@SerializedName("srctyp")
	private String sourceType;
	
	@Expose
	@SerializedName("irn")
	private String irn;
	
	@Expose
	@SerializedName("irngendate")
	private String irngendate;
	
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
	@SerializedName("txval")
	private BigDecimal totalTaxableVal = BigDecimal.ZERO;

	@Expose
	@SerializedName("imsStatus")
	private String imsStatus;
	
	@Expose
	@SerializedName("remarks")
	private String remarks;
	
	@Expose
	@SerializedName("items")
	private List<Item> b2bInvItems = null;
	

	
}
	