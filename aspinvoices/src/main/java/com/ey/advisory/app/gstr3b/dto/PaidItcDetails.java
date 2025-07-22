package com.ey.advisory.app.gstr3b.dto;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PaidItcDetails {

	@SerializedName("liab_ldg_id")
	@Expose
	private String liabledgerId;

	@SerializedName("trans_typ")
	@Expose
	private String transType;

	@SerializedName("i_pdi")
	@Expose
	private BigDecimal IGSTPaidUsingIGST = BigDecimal.ZERO;

	@SerializedName("i_pdc")
	@Expose
	private BigDecimal IGSTPaidUsingCGST =  BigDecimal.ZERO;

	@SerializedName("i_pds")
	@Expose
	private BigDecimal IGSTPaidUsingSGST =  BigDecimal.ZERO;

	@SerializedName("c_pdi")
	@Expose
	private BigDecimal CGSTPaidUsingIGST =  BigDecimal.ZERO;

	@SerializedName("c_pdc")
	@Expose
	private BigDecimal CGSTPaidUsingCGST =  BigDecimal.ZERO;

	@SerializedName("s_pdi")
	@Expose
	private BigDecimal SGSTPaidUsingIGST=  BigDecimal.ZERO;

	@SerializedName("s_pds")
	@Expose
	private BigDecimal SGSTPaidUsingSGST =  BigDecimal.ZERO;

	@SerializedName("cs_pdcs")
	@Expose
	private BigDecimal CessPaidUsingCess =  BigDecimal.ZERO;

}
