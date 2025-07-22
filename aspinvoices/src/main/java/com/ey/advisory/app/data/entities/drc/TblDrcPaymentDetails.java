package com.ey.advisory.app.data.entities.drc;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Siva.Reddy
 *
 */
@Data
@Entity
@Table(name = "TBL_DRC_PAYMENT_DTLS")
public class TblDrcPaymentDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Expose
	@SerializedName("drcArnNo")
	@Column(name = "DRC_ARN_NO")
	private String drcArnNo;
	
	@Column(name = "TAX_IGST_AMT")
	private BigDecimal taxIgstAmt;

	@Column(name = "TAX_CGST_AMT")
	private BigDecimal taxCgstAmt;

	@Column(name = "TAX_SGST_AMT")
	private BigDecimal taxSgstAmt;

	@Column(name = "TAX_CESS_AMT")
	private BigDecimal taxCessAmt;

	@Column(name = "TAX_TTL_AMT")
	private BigDecimal taxTtlAmt;

	@Column(name = "INTEREST_IGST_AMT")
	private BigDecimal interestIgstAmt;

	@Column(name = "INTEREST_CGST_AMT")
	private BigDecimal interestCgstAmt;

	@Column(name = "INTEREST_SGST_AMT")
	private BigDecimal interestSgstAmt;

	@Column(name = "INTEREST_CESS_AMT")
	private BigDecimal interestCessAmt;

	@Column(name = "INTEREST_TTL_AMT")
	private BigDecimal interestTtlAmt;
	
	@Column(name = "FEE_IGST_AMT")
	private BigDecimal feeIgstAmt;

	@Column(name = "FEE_CGST_AMT")
	private BigDecimal feeCgstAmt;

	@Column(name = "FEE_SGST_AMT")
	private BigDecimal feeSgstAmt;

	@Column(name = "FEE_CESS_AMT")
	private BigDecimal feeCessAmt;

	@Column(name = "FEE_TTL_AMT")
	private BigDecimal feeTtlAmt;
	
	@Column(name = "PENALTY_IGST_AMT")
	private BigDecimal penaltyIgstAmt;

	@Column(name = "PENALTY_CGST_AMT")
	private BigDecimal penaltyCgstAmt;

	@Column(name = "PENALTY_SGST_AMT")
	private BigDecimal penaltySgstAmt;

	@Column(name = "PENALTY_CESS_AMT")
	private BigDecimal penaltyCessAmt;

	@Column(name = "PENALTY_TTL_AMT")
	private BigDecimal penaltyTtlAmt;

	@Column(name = "OTHERS_IGST_AMT")
	private BigDecimal othersIgstAmt;

	@Column(name = "OTHERS_CGST_AMT")
	private BigDecimal othersCgstAmt;

	@Column(name = "OTHERS_SGST_AMT")
	private BigDecimal othersSgstAmt;

	@Column(name = "OTHERS_CESS_AMT")
	private BigDecimal othersCessAmt;

	@Column(name = "OTHERS_TTL_AMT")
	private BigDecimal othersTtlAmt;
	
	@ManyToOne
	@JoinColumn(name = "TBL_DRC_DETAILS_ID", referencedColumnName = "ID")
	protected TblDrcDetails drcDetailsId;
	

}
