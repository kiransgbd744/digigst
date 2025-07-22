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
public class ItcLedgerDetailsDto {
	
	@Expose
	private String gstin;
	
	@Expose
	@SerializedName(value = "fr_dt")
	private String fromDate;
	
	@Expose
	@SerializedName(value = "to_dt")
	private String toDate;
	
	@Expose
	@SerializedName(value = "op_bal")
	private ItcLedgerOpenCloseBalDto openBal;
	
	@Expose
	@SerializedName(value = "cl_bal")
	private ItcLedgerOpenCloseBalDto closenBal;
	
	@Expose
	@SerializedName(value = "tr")
	private List<ItcTransactionTypeBalDto> ItctransTypeBalDto;
	
}