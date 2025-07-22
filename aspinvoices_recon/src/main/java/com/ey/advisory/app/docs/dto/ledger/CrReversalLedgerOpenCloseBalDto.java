package com.ey.advisory.app.docs.dto.ledger;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CrReversalLedgerOpenCloseBalDto {

	private String gstin;

	@Expose
	private String srNo;

	@SerializedName(value = "igst")
	private BigDecimal igst = BigDecimal.ZERO;

	@SerializedName(value = "cgst")
	private BigDecimal cgst = BigDecimal.ZERO;

	@SerializedName(value = "sgst")
	private BigDecimal sgst = BigDecimal.ZERO;

	@SerializedName(value = "cess")
	private BigDecimal cess = BigDecimal.ZERO;

	@Expose
	@SerializedName(value = "trandt")
	private String transDate;

	@Expose
	@SerializedName(value = "rtnprd")
	private String rtnprd;

	@Expose
	@SerializedName(value = "refno")
	private String referenceNo;

	@Expose
	@SerializedName(value = "desc")
	private String description;
	
	@Expose
	@SerializedName(value = "trancd")
	private BigInteger trancd;

}
