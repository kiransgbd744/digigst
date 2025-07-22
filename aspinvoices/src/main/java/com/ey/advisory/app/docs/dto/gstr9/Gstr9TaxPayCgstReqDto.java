package com.ey.advisory.app.docs.dto.gstr9;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Gstr9TaxPayCgstReqDto {

	/**
	* Tax
	*
	*/
	@SerializedName("tx")
	@Expose
	private BigDecimal tx;
	/**
	* Paid through cash
	*
	*/
	@SerializedName("intr")
	@Expose
	private BigDecimal intr;
	/**
	* Penalty
	*
	*/
	@SerializedName("pen")
	@Expose
	private BigDecimal pen;
	/**
	* Fee
	*
	*/
	@SerializedName("fee")
	@Expose
	private BigDecimal fee;
	/**
	* Others
	*
	*/
	@SerializedName("oth")
	@Expose
	private BigDecimal oth;
	/**
	* Total
	*
	*/
	@SerializedName("tot")
	@Expose
	private BigDecimal tot;

}
