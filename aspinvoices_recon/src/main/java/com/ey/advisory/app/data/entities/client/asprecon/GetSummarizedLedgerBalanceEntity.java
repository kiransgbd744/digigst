package com.ey.advisory.app.data.entities.client.asprecon;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Getter
@Setter
@ToString
@Entity
@Table(name = "GET_LEDGER_SUMMARIZED_BALANCE")
public class GetSummarizedLedgerBalanceEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	protected Long id;
	
	@Column(name = "GSTIN")
	protected String supplierGstin;
	
	@Column(name = "CASH_IGST_TOT_BAL")
	protected BigDecimal cashIgstTot_bal = BigDecimal.ZERO;
	
	@Column(name = "CASH_CGST_TOT_BAL")
	protected BigDecimal cashCgstTotBal = BigDecimal.ZERO;
	
	@Column(name = "CASH_CESS_TOT_BAL")
	protected BigDecimal cashCessTotBal = BigDecimal.ZERO;
	
	@Column(name = "CASH_SGST_TOT_BAL")
	protected BigDecimal cashSgstTotBal = BigDecimal.ZERO;
	
	@Column(name = "ITC_CGST_TOT_BAL")
	protected BigDecimal itcCgstTotbal = BigDecimal.ZERO;
	
	@Column(name = "ITC_SGST_TOT_BAL")
	protected BigDecimal itcSgstTotBal = BigDecimal.ZERO;
	
	@Column(name = "ITC_IGST_TOT_BAL")
	protected BigDecimal itcIgstTotBal = BigDecimal.ZERO;
	
	@Column(name = "ITC_CESS_TOT_BAL")
	protected BigDecimal itcCessTotBal = BigDecimal.ZERO;
	
	@Column(name = "LIB_IGST_TOT_BAL")
	protected BigDecimal libIgstTotbal = BigDecimal.ZERO;
	
	@Column(name = "LIB_CGST_TOT_BAL")
	protected BigDecimal libCgstTotBal = BigDecimal.ZERO;
	
	@Column(name = "LIB_SGST_TOT_BAL")
	protected BigDecimal libSgstTotBal = BigDecimal.ZERO;
	
	@Column(name = "LIB_CESS_TOT_BAL")
	protected BigDecimal libCessTotBal = BigDecimal.ZERO;
	
	@Column(name = "LAST_UPDATED_DATE")
	protected LocalDateTime lastUpdatedDate;
	
	@Column(name = "TAX_PERIOD")
	protected String taxPeriod;
	
	@Column(name = "LAST_REFRESH_DATE")
	protected LocalDateTime lastRefreshDate;
	
	@Column(name = "REFRESH_JOB_ID")
	protected Long refreshJobId;
	
	@Column(name = "REFRESH_STATUS")
	protected String refreshStatus;
	
	@Column(name = "CRREV_IGST_TOT_BAL")
	protected BigDecimal crRevIgstTotbal = BigDecimal.ZERO;
	
	@Column(name = "CRREV_CGST_TOT_BAL")
	protected BigDecimal crRevCgstTotBal = BigDecimal.ZERO;
	
	@Column(name = "CRREV_SGST_TOT_BAL")
	protected BigDecimal crRevSgstTotBal = BigDecimal.ZERO;
	
	@Column(name = "CRREV_CESS_TOT_BAL")
	protected BigDecimal crRevCessTotBal = BigDecimal.ZERO;
	
	@Column(name = "RCM_IGST_TOT_BAL")
	protected BigDecimal rcmIgstTot_bal = BigDecimal.ZERO;
	
	@Column(name = "RCM_CGST_TOT_BAL")
	protected BigDecimal rcmCgstTotBal = BigDecimal.ZERO;
	
	@Column(name = "RCM_CESS_TOT_BAL")
	protected BigDecimal rcmCessTotBal = BigDecimal.ZERO;
	
	@Column(name = "RCM_SGST_TOT_BAL")
	protected BigDecimal rcmSgstTotBal = BigDecimal.ZERO;
	
	@Column(name = "NEGATIVE_IGST_TOT_BAL")
	protected BigDecimal negativeIgstTot_bal = BigDecimal.ZERO;
	
	@Column(name = "NEGATIVE_CGST_TOT_BAL")
	protected BigDecimal negativeCgstTotBal = BigDecimal.ZERO;
	
	@Column(name = "NEGATIVE_CESS_TOT_BAL")
	protected BigDecimal negativeCessTotBal = BigDecimal.ZERO;
	
	@Column(name = "NEGATIVE_SGST_TOT_BAL")
	protected BigDecimal negativeSgstTotBal = BigDecimal.ZERO;
	
	@Column(name = "GetCall_STATUS")
	protected String status;
	
	@Column(name = "GetCall_STATUS_TIMESTAMP")
	protected LocalDateTime getCallStatusTimeStamp;

}
