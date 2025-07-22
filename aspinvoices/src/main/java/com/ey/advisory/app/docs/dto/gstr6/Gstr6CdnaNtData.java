package com.ey.advisory.app.docs.dto.gstr6;

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
public class Gstr6CdnaNtData {

	@Expose
	@SerializedName("flag")
	private String flag;

	@Expose
	@SerializedName("chksum")
	private String chksum;

	@Expose
	@SerializedName("ntty")
	private String ntty;

	@Expose
	@SerializedName("nt_num")
	private String ntnum;

	@Expose
	@SerializedName("nt_dt")
	private String ntdt;

	@Expose
	@SerializedName("ont_num")
	private String ontnum;

	@Expose
	@SerializedName("ont_dt")
	private String ontdt;

	@Expose
	@SerializedName("inum")
	private String inum;

	@Expose
	@SerializedName("idt")
	private String idt;

	@Expose
	@SerializedName("updby")
	private String updby;

	@Expose
	@SerializedName("rsn")
	private String rsn;

	@Expose
	@SerializedName("opd")
	private String opd;
	
	@Expose
	@SerializedName("pos")
	private String pos;
	
	@Expose
	@SerializedName("d_flag")
	private String delinkStatus;
	
	@Expose
	@SerializedName("val")
	private BigDecimal val;

	@Expose
	@SerializedName("itms")
	private List<Gstr6Items> itms;

	@Expose(serialize = false, deserialize = false)
	@SerializedName("docId")
	private Long docId;
	
	@Expose
	@SerializedName("ostin")
	private String ostin;

}
