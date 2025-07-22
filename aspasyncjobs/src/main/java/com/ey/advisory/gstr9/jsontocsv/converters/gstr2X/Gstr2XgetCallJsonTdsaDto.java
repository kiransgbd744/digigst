package com.ey.advisory.gstr9.jsontocsv.converters.gstr2X;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Gstr2XgetCallJsonTdsaDto {
	@SerializedName("month")
	@Expose
	private String month;

	@SerializedName("ctin")
	@Expose
	private String ctin;

	@SerializedName("inum")
	@Expose
	private String inum;

	@SerializedName("idt")
	@Expose
	private String idt;

	@SerializedName("ival")
	@Expose
	private BigDecimal ival;

	@SerializedName("chksum")
	@Expose
	private String chksum;

	@SerializedName("omonth")
	@Expose
	private String omonth;

	@SerializedName("amt_ded")
	@Expose
	private BigDecimal amtded;

	@SerializedName("iamt")
	@Expose
	private BigDecimal iamt;

	@SerializedName("camt")
	@Expose
	private BigDecimal camt;

	@SerializedName("samt")
	@Expose
	private BigDecimal samt;

	@SerializedName("flag")
	@Expose
	private String flag;
}
