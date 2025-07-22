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
public class Gstr6aCdnaInvoiceData {

	@Expose
	@SerializedName("itms")
	private List<Gstr6aItems> itms;

	@Expose
	@SerializedName("d_flag")
	private String delinkStatus;

	@Expose
	@SerializedName("pos")
	private String pos;

	@Expose
	@SerializedName("val")
	private BigDecimal val;

	@Expose
	@SerializedName("samt")
	private BigDecimal samt;

	@Expose
	@SerializedName("ont_num")
	private String ont_num;

	@Expose
	@SerializedName("txval")
	private BigDecimal txval;

	@Expose
	@SerializedName("nt_num")
	private String nt_num;

	@Expose
	@SerializedName("camt")
	private BigDecimal camt;

	@Expose
	@SerializedName("inum")
	private String inum;

	@Expose
	@SerializedName("nt_dt")
	private String ntdt;

	@Expose
	@SerializedName("p_gst")
	private String pgst;

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
	@SerializedName("ont_dt")
	private String ontdt;

	@Expose
	@SerializedName("idt")
	private String idt;

	@Expose
	@SerializedName("ntty")
	private String ntty;

	@Expose
	@SerializedName("chksum")
	private String chksum;

}
