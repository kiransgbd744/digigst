package com.ey.advisory.app.docs.dto.ledger;

import java.time.LocalDate;
import java.time.LocalTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CashDetailsRespDto {
	
	@Expose
	@SerializedName(value = "dpt_dt")
	private String dptDate;
	
	@Expose
	@SerializedName(value  = "dpt_time")
	private String dptTime;

	
	@Expose
	@SerializedName(value  = "rpt_dt")
	private String rptDate;
	
	@Expose
	@SerializedName(value = "refNo")
	private String referenceNo;
	
	@Expose
	@SerializedName(value = "ret_period")
	private String retPeriod;
	
	@Expose
	@SerializedName(value = "desc")
    private String description;
	
	@Expose
	@SerializedName(value = "tr_typ")
	private String transType;
	
	@Expose
	@SerializedName(value = "igst")
	private LedgerBalanceAmts igst;
	
	@Expose
	@SerializedName(value = "sgst")
	private LedgerBalanceAmts sgst;
	
	@Expose
	@SerializedName(value = "cgst")
	private LedgerBalanceAmts cgst;
	
	@Expose
	@SerializedName(value = "cess")
	private LedgerBalanceAmts cess;
	
	@Expose
	@SerializedName(value = "igstbal")
	private LedgerBalanceAmts igstBal;
	
	@Expose
	@SerializedName(value = "sgstbal")
	private LedgerBalanceAmts sgstBal;

	@Expose
	@SerializedName(value = "cgstbal")
	private LedgerBalanceAmts cgstBal;

	@Expose
	@SerializedName(value = "cessbal")
	private LedgerBalanceAmts cessBal;
	
}
