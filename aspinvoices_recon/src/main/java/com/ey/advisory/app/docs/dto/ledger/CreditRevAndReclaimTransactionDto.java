package com.ey.advisory.app.docs.dto.ledger;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CreditRevAndReclaimTransactionDto {
	
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
	@SerializedName(value = "itc4a5")
	private LedgerItcReclaimBalanceAmts itc4a5;
	
	@Expose
	@SerializedName(value = "itc4b2")
	private LedgerItcReclaimBalanceAmts itc4b2;
	
	@Expose
	@SerializedName(value = "itc4d1")
	private LedgerItcReclaimBalanceAmts itc4d1;
	
	@Expose
	@SerializedName(value = "clsbal")
	private LedgerItcReclaimBalanceAmts clsbal;


	

}
