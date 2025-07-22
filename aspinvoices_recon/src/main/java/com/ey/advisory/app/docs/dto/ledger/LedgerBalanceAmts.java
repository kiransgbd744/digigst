package com.ey.advisory.app.docs.dto.ledger;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class LedgerBalanceAmts {
	
	@Expose
	@SerializedName(value = "tx")
	private String taxValue = "0";

	@Expose
	@SerializedName(value = "intr")
	private String interestValue = "0";

	@Expose
	@SerializedName(value = "pen")
	private String penalty = "0";

	@Expose
	@SerializedName(value = "fee")
	private String fees = "0";

	@Expose
	@SerializedName(value = "oth")
	private String other = "0";

	@Expose
	@SerializedName(value = "tot")
	private String total = "0";


}
