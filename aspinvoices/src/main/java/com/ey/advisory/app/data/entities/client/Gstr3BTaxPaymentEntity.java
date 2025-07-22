/**
 * 
 */
package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Arun KA
 *
 */

@Setter
@Getter
@Entity
@Table(name = "GSTR3B_GET_TAX_PAYMENT_DETAILS")
public class Gstr3BTaxPaymentEntity {
	
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GSTR3B_GET_TAX_PAYMENT_DETAILS_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;
	
	@Expose
	@SerializedName("gstin")
	@Column(name = "GSTIN")
	private String gstin;

	@Expose
	@SerializedName("taxPeriod")
	@Column(name = "TAX_PERIOD")
	private String taxPeriod;
	
	@Expose
	@SerializedName("liabilityLedgerId")
	@Column(name = "LIABILTY_LEDGER_ID")
	private Long liabilityLedgerId;
	
	@Expose
	@SerializedName("subSection")
	@Column(name = "SUBSECTION_NAME")
	private String subSection;
	
	@Expose
	@SerializedName("transactionType")
	@Column(name = "TRANSACTION_TYPE")
	private String transactionType;
	
	@Expose
	@SerializedName("taxType")
	@Column(name = "TAX_TYPE")
	private String taxType;
	
	@Expose
	@SerializedName("interestAmt")
	@Column(name = "INTEREST_AMT")
	private BigDecimal interestAmt;
	
	@Expose
	@SerializedName("taxAmt")
	@Column(name = "TAX_AMT")
	private BigDecimal taxAmt;
	
	@Expose
	@SerializedName("feeAmt")
	@Column(name = "FEE_AMT")
	private BigDecimal feeAmt;
	
	@Expose
	@SerializedName("paidUsingIgst")
	@Column(name = "PAID_USING_IGST")
	private BigDecimal paidUsingIgst;
	
	@Expose
	@SerializedName("paidUsingCgst")
	@Column(name = "PAID_USING_CGST")
	private BigDecimal paidUsingCgst;
	
	@Expose
	@SerializedName("paidUsingSgst")
	@Column(name = "PAID_USING_SGST")
	private BigDecimal paidUsingSgst;
	
	@Expose
	@SerializedName("paidUsingCess")
	@Column(name = "PAID_USING_CESS")
	private BigDecimal paidUsingCess;
	
	@Expose
	@SerializedName("createdDt")
	@Column(name = "CREATE_DATE")
	private LocalDateTime createdDt;
	
	@Expose
	@SerializedName("fy")
	@Column(name = "FY")
	private Long fy;
	
	@Expose
	@SerializedName("isActive")
	@Column(name = "IS_ACTIVE")
	private Boolean isActive;
	
	@Expose
	@SerializedName("link")
	@Column(name = "LINK")
	private Long link;
	
	
}
