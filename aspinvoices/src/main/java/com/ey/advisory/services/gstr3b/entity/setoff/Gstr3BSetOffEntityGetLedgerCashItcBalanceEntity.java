package com.ey.advisory.services.gstr3b.entity.setoff;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Hema G M 
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TBL_GET_LEDGER_CASH_ITC_BAL")
public class Gstr3BSetOffEntityGetLedgerCashItcBalanceEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "GSTIN")
	private String gstin;

	@Column(name = "TAX_PERIOD")
	private String taxPeriod;

	@Column(name = "INTR_IGST")
	private BigDecimal interestIgst;

	@Column(name = "INTR_CGST")
	private BigDecimal interestCgst;

	@Column(name = "INTR_SGST")
	private BigDecimal interestSgst;

	@Column(name = "INTR_CESS")
	private BigDecimal interestCess;

	@Column(name = "FEE_IGST")
	private BigDecimal feeIgst;

	@Column(name = "FEE_CGST")
	private BigDecimal feeCgst;

	@Column(name = "FEE_SGST")
	private BigDecimal feeSgst;

	@Column(name = "FEE_CESS")
	private BigDecimal feeCess;

	@Column(name = "TX_IGST")
	private BigDecimal taxIgst;

	@Column(name = "TX_CGST")
	private BigDecimal taxCgst;

	@Column(name = "TX_SGST")
	private BigDecimal taxSgst;

	@Column(name = "TX_CESS")
	private BigDecimal taxCess;

	@Column(name = "ITC_BAL_IGST")
	private BigDecimal itcBalanceIgst;

	@Column(name = "ITC_BAL_CGST")
	private BigDecimal itcBalanceCgst;

	@Column(name = "ITC_BAL_SGST")
	private BigDecimal itcBalanceSgst;

	@Column(name = "ITC_BAL_CESS")
	private BigDecimal itcBalanceCess;

	@Column(name = "ITC_BLCK_IGST")
	private BigDecimal itcBlckIgst;

	@Column(name = "ITC_BLCK_CGST")
	private BigDecimal itcBlckCgst;

	@Column(name = "ITC_BLCK_SGST")
	private BigDecimal itcBlckSgst;

	@Column(name = "ITC_BLCK_CESS")
	private BigDecimal itcBlckCess;

	@Column(name = "IS_ACTIVE")
	private Boolean isActive;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "UPDATED_BY")
	private String updatedBy;

	@Column(name = "UPDATED_ON")
	private LocalDateTime updatedOn;
}
