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
public class Gstr6B2bInvoiceData {

	@Expose
	@SerializedName("flag")
	private String flag;

	@Expose
	@SerializedName("chksum")
	private String chksum;

	@Expose
	@SerializedName("inum")
	private String inum;

	@Expose
	@SerializedName("idt")
	private String idt;

	@Expose
	@SerializedName("val")
	private BigDecimal val;

	@Expose
	@SerializedName("updby")
	private String updby;
	
	@Expose
	@SerializedName("pos")
	private String pos;

	@Expose
	@SerializedName("itms")
	private List<Gstr6Items> itms;
	
	@Expose(serialize = false, deserialize = false)
	@SerializedName("docId")
	private Long docId;	

}


