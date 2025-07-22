package com.ey.advisory.app.docs.dto.ledger;

import java.util.List;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LedgerBalanceJobTriggerDto {
	
	@Expose
	protected String groupcode;
	
	@Expose
	protected List<String> gstins;
	
	@Expose
	protected Long entityId;
	
	@Expose
	protected Long jobId;
	
	@Expose
	protected String taxPeriod;

}
