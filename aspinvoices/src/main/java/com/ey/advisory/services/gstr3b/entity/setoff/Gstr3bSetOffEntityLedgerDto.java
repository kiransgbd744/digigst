package com.ey.advisory.services.gstr3b.entity.setoff;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
/**
 * @author Ravindra V S
 *
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Gstr3bSetOffEntityLedgerDto {
	
	
	@Expose
	private String gstin;

	@Expose
	@SerializedName("ledgerTimestamp")
	private LocalDateTime ledgerTimestamp;
	
	@Expose
	@SerializedName("cashLedgerBalanceIgst")
	private BigDecimal cashLedgerBalanceIgst;

	@Expose
	@SerializedName("cashLedgerBalanceCgst")
	private BigDecimal cashLedgerBalanceCgst;
	
	@Expose
	@SerializedName("cashLedgerBalanceSgst")
	private BigDecimal cashLedgerBalanceSgst;
	
	@Expose
	@SerializedName("cashLedgerBalanceCess")
	private BigDecimal cashLedgerBalanceCess;
	
	@Expose
	@SerializedName("cashLedgerBalanceInterest")
	private BigDecimal cashLedgerBalanceInterest;
	
	@Expose
	@SerializedName("cashLedgerBalanceLateFee")
	private BigDecimal cashLedgerBalanceLateFee;
	
	@Expose
	@SerializedName("creditLedgerBalanceIgst")
	private BigDecimal creditLedgerBalanceIgst;

	@Expose
	@SerializedName("creditLedgerBalanceCgst")
	private BigDecimal creditLedgerBalanceCgst;
	
	@Expose
	@SerializedName("creditLedgerBalanceSgst")
	private BigDecimal creditLedgerBalanceSgst;
	
	@Expose
	@SerializedName("creditLedgerBalanceCess")
	private BigDecimal creditLedgerBalanceCess;
	

}
