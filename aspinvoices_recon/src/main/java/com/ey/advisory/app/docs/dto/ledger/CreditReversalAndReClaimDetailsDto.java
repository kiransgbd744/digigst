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
public class CreditReversalAndReClaimDetailsDto {
	
	 private String gstin;

	    @Expose
	    @SerializedName(value = "ret_period")
	    private String retPeriod;

	    @Expose
	    @SerializedName(value = "clsbal")
	    private CrReversalLedgerOpenCloseBalDto closeBal;
	    
	    @Expose
	    @SerializedName(value = "closebal")
	    private List<CrReversalLedgerOpenCloseBalDto> closeBals;

}
