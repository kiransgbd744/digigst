package com.ey.advisory.app.docs.dto.gstr9;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Gstr9TaxPayReqDto {
	/**
	* Central Tax
	*
	*/
	@SerializedName("cgst")
	@Expose
	private Gstr9TaxPayCgstReqDto gstr9TaxPayCgstReqDto;
	/**
	* State tax/UT Tax
	*
	*/
	@SerializedName("sgst")
	@Expose
	private Gstr9TaxPaySgstReqDto gstr9TaxPaySgstReqDto;
	/**
	* Liab ID
	*
	*/
	@SerializedName("liab_id")
	@Expose
	private Integer liabId;
	/**
	* Transaction Code
	*
	*/
	@SerializedName("trancd")
	@Expose
	private Integer trancd;
	/**
	* Transaction Date
	*
	*/
	@SerializedName("trandate")
	@Expose
	private String trandate;
}
