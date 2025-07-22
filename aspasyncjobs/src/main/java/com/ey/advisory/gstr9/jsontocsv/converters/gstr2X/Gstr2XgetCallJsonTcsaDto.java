package com.ey.advisory.gstr9.jsontocsv.converters.gstr2X;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Gstr2XgetCallJsonTcsaDto {
	
	@SerializedName("ctin")
	@Expose
	private String ctin;

	@SerializedName("chksum")
	@Expose
	private String chksum;

	@SerializedName("omonth")
	@Expose
	private String omonth;

	@SerializedName("month")
	@Expose
	private String month;

	@SerializedName("pos")
	@Expose
	private String pos;

	@SerializedName("supR")
	@Expose
	private BigDecimal supR;

	@SerializedName("retsupR")
	@Expose
	private BigDecimal retsupR;

	@SerializedName("supU")
	@Expose
	private BigDecimal supU;

	@SerializedName("retsupU")
	@Expose
	private BigDecimal retsupU;

	@SerializedName("amt")
	@Expose
	private BigDecimal amt;

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

	@SerializedName("remarks")
	@Expose
	private String remarks;

	@SerializedName("comment")
	@Expose
	private String comment;
}
