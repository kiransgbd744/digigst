package com.ey.advisory.app.docs.dto.ledger;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LiabilityLedgersDetailsDto {
	
	@Expose
	private String gstin;
	
	@Expose
	@SerializedName(value = "ret_period")
	private String retPeriod;
	
	@Expose
	@SerializedName(value = "tr")
	private List<TransactionTypeLibBalDto> transTypeBalDto;
	
	@Expose
	@SerializedName(value = "cl_bal")
	private CashLedgerOpenCloseBalDto closeBal;

}
