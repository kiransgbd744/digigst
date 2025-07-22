package com.ey.advisory.app.services.ledger;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author kiran s
 *
 */
@Getter
@Setter
@ToString
public class CreditLedgerBulkDetailsDto {
	
	@Expose
	@SerializedName(value = "entityId")
	private String entityId;
	
	@Expose
	@SerializedName(value = "gstin")
	private String gstin;
	
	@Expose
	@SerializedName(value = "fr_dt")
	private String fromDate;
	
	@Expose
	@SerializedName(value = "to_dt")
	private String toDate;
	
	/*@Expose
	@SerializedName("isCreditLedger")
	private Boolean isCreditLedger;*/
	
	@Expose
	@SerializedName(value = "reportType")
	private String reportType;
	
	@Expose
	@SerializedName("ret_period")
	private Boolean ret_period;

}

