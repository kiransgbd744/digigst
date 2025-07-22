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
public class Gstr3bSetOffEntityDashboardRespDto {

	public Gstr3bSetOffEntityDashboardRespDto(
			Gstr3bSetOffEntityDashboardRespDto gstr3bSetOffEntityDashboardRespDto) {
		// TODO Auto-generated constructor stub
	}

	@Expose
	private String gstin;

	@Expose
	@SerializedName("stateName")
	private String stateName;

	@Expose
	@SerializedName("regtype")
	private String regtype;

	@Expose
	@SerializedName("authStatus")
	private String authStatus;

	@Expose
	@SerializedName("computeSetoffTimeStamp")
	private LocalDateTime computeSetoffTimeStamp;

	@Expose
	@SerializedName("computeSetoffStatus")
	private String computeSetoffStatus;

	@Expose
	@SerializedName("liabilitySetoffTimeStamp")
	private LocalDateTime liabilitySetoffTimeStamp;

	@Expose
	@SerializedName("liabilitySetoffStatus")
	private String liabilitySetoffStatus;

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

	@Expose
	@SerializedName("liabilityIgst")
	private BigDecimal liabilityIgst;

	@Expose
	@SerializedName("liabilityCgst")
	private BigDecimal liabilityCgst;

	@Expose
	@SerializedName("liabilitySgst")
	private BigDecimal liabilitySgst;

	@Expose
	@SerializedName("liabilityCess")
	private BigDecimal liabilityCess;

	@Expose
	@SerializedName("paidThroughItcIgst")
	private BigDecimal paidThroughItcIgst;

	@Expose
	@SerializedName("paidThroughItcCgst")
	private BigDecimal paidThroughItcCgst;

	@Expose
	@SerializedName("paidThroughItcSgst")
	private BigDecimal paidThroughItcSgst;

	@Expose
	@SerializedName("paidThroughItcCess")
	private BigDecimal paidThroughItcCess;

	@Expose
	@SerializedName("netLiabilityIgst")
	private BigDecimal netLiabilityIgst;

	@Expose
	@SerializedName("netLiabilityCgst")
	private BigDecimal netLiabilityCgst;

	@Expose
	@SerializedName("netLiabilitySgst")
	private BigDecimal netLiabilitySgst;

	@Expose
	@SerializedName("netLiabilityCess")
	private BigDecimal netLiabilityCess;

	@Expose
	@SerializedName("liabilityReverseChargeIgst")
	private BigDecimal liabilityReverseChargeIgst;

	@Expose
	@SerializedName("liabilityReverseChargeCgst")
	private BigDecimal liabilityReverseChargeCgst;

	@Expose
	@SerializedName("liabilityReverseChargeSgst")
	private BigDecimal liabilityReverseChargeSgst;

	@Expose
	@SerializedName("liabilityReverseChargeCess")
	private BigDecimal liabilityReverseChargeCess;

	@Expose
	@SerializedName("interestAndLateFeeIgst")
	private BigDecimal interestAndLateFeeIgst;

	@Expose
	@SerializedName("interestAndLateFeeCgst")
	private BigDecimal interestAndLateFeeCgst;

	@Expose
	@SerializedName("interestAndLateFeeSgst")
	private BigDecimal interestAndLateFeeSgst;

	@Expose
	@SerializedName("interestAndLateFeeCess")
	private BigDecimal interestAndLateFeeCess;

	@Expose
	@SerializedName("netGstLiabilityIgst")
	private BigDecimal netGstLiabilityIgst;

	@Expose
	@SerializedName("netGstLiabilityCgst")
	private BigDecimal netGstLiabilityCgst;

	@Expose
	@SerializedName("netGstLiabilitySgst")
	private BigDecimal netGstLiabilitySgst;

	@Expose
	@SerializedName("netGstLiabilityCess")
	private BigDecimal netGstLiabilityCess;

	@Expose
	@SerializedName("addnlCashReqIgst")
	private BigDecimal addnlCashReqIgst;

	@Expose
	@SerializedName("addnlCashReqCgst")
	private BigDecimal addnlCashReqCgst;

	@Expose
	@SerializedName("addnlCashReqSgst")
	private BigDecimal addnlCashReqSgst;

	@Expose
	@SerializedName("addnlCashReqCess")
	private BigDecimal addnlCashReqCess;

}
