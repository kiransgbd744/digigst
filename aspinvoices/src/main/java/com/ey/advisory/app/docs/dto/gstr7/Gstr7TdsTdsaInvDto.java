package com.ey.advisory.app.docs.dto.gstr7;

import java.math.BigDecimal;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Gstr7TdsTdsaInvDto {

	@Expose
	@SerializedName("oinum")
	private String oinum;

	@Expose
	@SerializedName("inum")
	private String inum;

	@Expose
	@SerializedName("oidt")
	private String oidt;

	@Expose
	@SerializedName("oival")
	private BigDecimal oival;

	@Expose
	@SerializedName("oamt_ded")
	private BigDecimal oamt_ded;

	@Expose
	@SerializedName("idt")
	private String idt;

	@Expose
	@SerializedName("ival")
	private BigDecimal ival;

	@Expose
	@SerializedName("amt_ded")
	private BigDecimal amt_ded;

	@Expose
	@SerializedName("camt")
	private BigDecimal camt;

	
	@Expose
	@SerializedName("samt")
	private BigDecimal samt;

	@Expose
	@SerializedName("iamt")
	private BigDecimal iamt;

	@Expose
	@SerializedName("source")
	private String source;

	@Expose
	@SerializedName("chksum")
	private String chksum;
	
	@Expose
	@SerializedName("flag")
	private String flag;
}
