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
public class ISDADocDetails {

	@Expose
	@SerializedName("odocdt")
	private String orgIsdDocDate;

	@Expose
	@SerializedName("odoctyp")
	private String orgIsdDocType;

	@Expose
	@SerializedName("odocnum")
	private String orgIsdDocNumber;
	
	@Expose
	@SerializedName("doctyp")
	private String isdDocType;
	
	@Expose
	@SerializedName("docnum")
	private String isdDocNumber;

	@Expose
	@SerializedName("docdt")
	private String isdDocDate;

	@Expose
	@SerializedName("oinvnum")
	private String orgInvNumber;

	@Expose
	@SerializedName("oinvdt")
	private String orgInvDate;

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
	@SerializedName("itcelg")
	private String itcElg;

}
