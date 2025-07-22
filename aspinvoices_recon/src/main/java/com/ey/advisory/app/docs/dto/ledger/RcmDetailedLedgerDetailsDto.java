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
public class RcmDetailedLedgerDetailsDto {

	@Expose
	@SerializedName(value = "gstin")
	private String gstin;
	
	@Expose
	@SerializedName(value = "frdt")
	private String fromDate;
	
	@Expose
	@SerializedName(value = "todt")
	private String toDate;
	
	@Expose
	@SerializedName(value = "opnbal")
	private CrReversalLedgerOpenCloseBalDto openBal;
	
	@Expose
	@SerializedName(value = "clsbal")
	private CrReversalLedgerOpenCloseBalDto closenBal;
	
	@Expose
	@SerializedName(value = "tr")
	private List<RcmDetailedLedgerTransactionDto> transTypeBalDto;
	
}
