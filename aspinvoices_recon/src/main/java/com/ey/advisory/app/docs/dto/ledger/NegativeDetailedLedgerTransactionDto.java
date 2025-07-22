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
public class NegativeDetailedLedgerTransactionDto {
	
	@Expose
	@SerializedName(value = "trandate")
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
	@SerializedName(value = "trantyp")
	private String trantyp;
	
	@Expose
	@SerializedName(value = "negliab")
	private List<LedgerItcReclaimBalanceAmts> negliab;
	
	@Expose
	@SerializedName(value = "negliabal")
	private List<LedgerItcReclaimBalanceAmts> negliabal;
	
	/*@Expose
	@SerializedName(value = "clsBalOther")
	private LedgerItcReclaimBalanceAmts clsBalOther;
	
	@Expose
	@SerializedName(value = "clsbalRevCharge")
	private LedgerItcReclaimBalanceAmts clsbalRevCharge;*/


	

}
