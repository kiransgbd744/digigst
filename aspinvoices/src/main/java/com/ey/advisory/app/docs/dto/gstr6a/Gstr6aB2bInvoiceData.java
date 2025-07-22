package com.ey.advisory.app.docs.dto.gstr6a;

import java.math.BigDecimal;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Anand3.M
 *
 */
@Data
public class Gstr6aB2bInvoiceData {

	@Expose
	@SerializedName("val")
	private BigDecimal val;

	@Expose
	@SerializedName("itms")
	private List<Gstr6aItems> itms;

	@Expose
	@SerializedName("samt")
	private BigDecimal samt;

	@Expose
	@SerializedName("txval")
	private BigDecimal txval;

	@Expose
	@SerializedName("camt")
	private BigDecimal camt;

	@Expose
	@SerializedName("inum")
	private String inum;

	@Expose
	@SerializedName("iamt")
	private BigDecimal iamt;

	@Expose
	@SerializedName("csamt")
	private BigDecimal csamt;

	@Expose
	@SerializedName("cfs")
	private String cfs;
	
	@Expose
	@SerializedName("cfp")
	private String cfp;

	@Expose
	@SerializedName("inv_typ")
	private String invType;

	@Expose
	@SerializedName("pos")
	private String pos;

	@Expose
	@SerializedName("idt")
	private String idt;

	@Expose
	@SerializedName("rchrg")
	private String rchrg;

	@Expose
	@SerializedName("chksum")
	private String chksum;
	
	@Expose
	@SerializedName("srctyp")
	private String sourceType;
	
	@Expose
	@SerializedName("irn")
	private String irn;
	
	@Expose
	@SerializedName("irngendate")
	private String irngendate;

}
