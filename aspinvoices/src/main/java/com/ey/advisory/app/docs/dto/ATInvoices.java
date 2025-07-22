package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class ATInvoices {

	@Expose
	@SerializedName("flag")
	private String flag;

	@Expose
	@SerializedName("chksum")
	private String chksum;

	@Expose
	@SerializedName("omon")
	private String omon;

	@Expose
	@SerializedName("pos")
	private String pos;

	@Expose
	@SerializedName("sply_ty")
	private String splyTy;

	@Expose
	@SerializedName("diff_percent")
	private BigDecimal diffPercent;

	@Expose
	@SerializedName("itms")
	private List<ATItemDetails> items;

	
}
