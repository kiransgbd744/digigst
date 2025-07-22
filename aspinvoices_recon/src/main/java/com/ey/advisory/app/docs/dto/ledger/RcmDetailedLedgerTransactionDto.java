package com.ey.advisory.app.docs.dto.ledger;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RcmDetailedLedgerTransactionDto {
	
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
	@SerializedName(value = "itc4a2")
	private LedgerItcReclaimBalanceAmts itc4a2;
	
	@Expose
	@SerializedName(value = "itc4a3")
	private LedgerItcReclaimBalanceAmts itc4a3;
	
	@Expose
	@SerializedName(value = "inwardsup_3_1d")
	private LedgerItcReclaimBalanceAmts itc31d;
	
	@Expose
	@SerializedName(value = "clsbal")
	private LedgerItcReclaimBalanceAmts clsbal;


	

}
