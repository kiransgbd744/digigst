package com.ey.advisory.app.gstr3b;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Gstr3bTaxPaymentDto implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Expose
	private String gstin;
	
	@Expose
	private String taxPeriod;

	@Expose
	private Long liabilityLedgerId;
	
	@Expose
	private String subSection;
	
	@Expose
	private String transactionType;
	
	@Expose
	private String taxType;
	
	@Expose
	private BigDecimal interestAmt = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal taxAmt = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal feeAmt = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal paidUsingIgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal paidUsingCgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal paidUsingSgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal paidUsingCess = BigDecimal.ZERO;
	
	@Expose
	private LocalDateTime createdDt;
	
	@Expose
	private Long fy;
	
	@Expose
	private Boolean isActive;

	@Override
	public String toString() {
		return "Gstr3bTaxPaymentDto [gstin=" + gstin + ", taxPeriod="
				+ taxPeriod + ", liabilityLedgerId=" + liabilityLedgerId
				+ ", subSection=" + subSection + ", transactionType="
				+ transactionType + ", taxType=" + taxType + ", interestAmt="
				+ interestAmt + ", taxAmt=" + taxAmt + ", feeAmt=" + feeAmt
				+ ", paidUsingIgst=" + paidUsingIgst + ", paidUsingCgst="
				+ paidUsingCgst + ", paidUsingSgst=" + paidUsingSgst
				+ ", paidUsingCess=" + paidUsingCess + ", createdDt="
				+ createdDt + ", fy=" + fy + ", isActive=" + isActive + "]";
	}
	
	
	
	
	

}
