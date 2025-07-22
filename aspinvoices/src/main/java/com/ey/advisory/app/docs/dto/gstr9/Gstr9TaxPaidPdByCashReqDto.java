package com.ey.advisory.app.docs.dto.gstr9;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Gstr9TaxPaidPdByCashReqDto {

	/**
	 * Central Tax
	 *
	 */
	@SerializedName("cgst")
	@Expose
	public Gstr9TaxPaidPdByCashCgstReqDto gstr9TaxPaidPdByCashCgstReqDto;
	/*
	 * State tax/UT Tax
	 *
	 */
	@SerializedName("sgst")
	@Expose
	public Gstr9TaxPaidPdByCashSgstReqDto gstr9TaxPaidPdByCashSgstReqDto;
	/**
	 * Liab ID
	 *
	 */
	@SerializedName("liab_id")
	@Expose
	public Integer liabId;
	/**
	 * Transaction Code
	 *
	 */
	@SerializedName("trancd")
	@Expose
	public Integer trancd;
	/**
	 * Transaction Date
	 *
	 */
	@SerializedName("trandate")
	@Expose
	public String trandate;
	/**
	 * Debit Id
	 *
	 */
	@SerializedName("debit_id")
	@Expose
	public String debitId;
}
