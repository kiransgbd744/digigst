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
public class NegativeDetailedLedgerDetailsDto {

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
	@SerializedName(value = "openbal")
	private List<CrReversalLedgerOpenCloseBalDto> openBal;
	
	@Expose
	@SerializedName(value = "closebal")
	private List<CrReversalLedgerOpenCloseBalDto> closenBal;
	
	@Expose
	@SerializedName(value = "negliabdtls")
	private List<NegativeDetailedLedgerTransactionDto> transTypeBalDto;
	
}
