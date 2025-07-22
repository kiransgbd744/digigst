package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Entity
@Table(name = "GSTR1_GSTN_ADV_ADJUSTMENT")
@Setter
@Getter
@ToString
public class Gstr1GstnTxpdFileUploadEntity {

	@Expose
	@Id
	@SerializedName("id")
	@SequenceGenerator(name = "sequence", sequenceName = "GSTR1_GSTN_ADV_ADJUSTMENT_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;

	@Expose
	@SerializedName("fileId")
	@Column(name = "FILE_ID")
	private Long fileId;
	@Expose
	@SerializedName("asEnterId")
	@Column(name = "AS_ENTERED_ID")
	private Long asEnterId;

	@Expose
	@SerializedName("sgstin")
	@Column(name = "SUPPLIER_GSTIN")
	private String sgstin;

	@Expose
	@SerializedName("returnPeriod")
	@Column(name = "RETURN_PERIOD")
	private String returnPeriod;

	@Expose
	@SerializedName("transactionType")
	@Column(name = "TRAN_TYPE")
	private String transactionType;

	@Expose
	@SerializedName("month")
	@Column(name = "MONTH")
	private String month;

	@Expose
	@SerializedName("orgPos")
	@Column(name = "ORG_POS")
	private String orgPOS;

	@Expose
	@SerializedName("orgRate")
	@Column(name = "ORG_RATE")
	private BigDecimal orgRate;

	@Expose
	@SerializedName("orgGrossAdvanceAdjusted")
	@Column(name = "ORG_GROSS_ADV_ADJUSTED")
	private BigDecimal orgGrossAdvanceAdjusted;

	@Expose
	@SerializedName("newPos")
	@Column(name = "NEW_POS")
	private String newPOS;

	@Expose
	@SerializedName("newRate")
	@Column(name = "NEW_RATE")
	private BigDecimal newRate;

	@Expose
	@SerializedName("newGrossAdvanceAdjusted")
	@Column(name = "NEW_GROSS_ADV_ADJUSTED")
	private BigDecimal newGrossAdvanceAdjusted;

	@Expose
	@SerializedName("igstAmt")
	@Column(name = "IGST_AMT")
	private BigDecimal integratedTaxAmount;

	@Expose
	@SerializedName("cgstAmt")
	@Column(name = "CGST_AMT")
	private BigDecimal centralTaxAmount;

	@Expose
	@SerializedName("sgstAmt")
	@Column(name = "SGST_AMT")
	private BigDecimal stateUTTaxAmount;

	@Expose
	@SerializedName("cessAmt")
	@Column(name = "CESS_AMT")
	private BigDecimal cessAmount;

	@Expose
	@SerializedName("gstnTxpdKey")
	@Column(name = "TXPD_GSTN_INVKEY")
	protected String gstnTxpdKey;

	@Expose
	@SerializedName("isDelete")
	@Column(name = "IS_DELETE")
	protected boolean isDelete;

	@Expose
	@SerializedName("createdBy")
	@Column(name = "CREATED_BY")
	protected String createdBy;

	@Expose
	@SerializedName("createdOn")
	@Column(name = "CREATED_ON")
	protected LocalDateTime createdOn;

	@Expose
	@SerializedName("modifiedBy")
	@Column(name = "MODIFIED_BY")
	protected String modifiedBy;

	@Expose
	@SerializedName("modifiedOn")
	@Column(name = "MODIFIED_ON")
	protected LocalDateTime modifiedOn;

	@Expose
	@SerializedName("sectionType")
	@Column(name = "IS_AMENDMENT")
	protected boolean sectionType;

	public Gstr1GstnTxpdFileUploadEntity add(
			Gstr1GstnTxpdFileUploadEntity newObj) {
		this.sgstin = newObj.sgstin;
		this.returnPeriod = newObj.returnPeriod;
		this.transactionType = newObj.transactionType;
		this.month = newObj.month;
		this.orgPOS = newObj.orgPOS;
		this.orgRate = newObj.orgRate;
		this.newPOS = newObj.newPOS;
		this.newRate = newObj.newRate;
		this.orgGrossAdvanceAdjusted = add(this.orgGrossAdvanceAdjusted,
				newObj.orgGrossAdvanceAdjusted);
		this.newGrossAdvanceAdjusted = add(this.newGrossAdvanceAdjusted,
				newObj.newGrossAdvanceAdjusted);
		this.integratedTaxAmount = add(this.integratedTaxAmount,
				newObj.integratedTaxAmount);
		this.stateUTTaxAmount = add(this.stateUTTaxAmount,
				newObj.stateUTTaxAmount);
		this.centralTaxAmount = add(this.centralTaxAmount,
				newObj.centralTaxAmount);
		this.cessAmount = add(this.cessAmount, newObj.cessAmount);
		return this;

	}

	private BigDecimal add(BigDecimal no1, BigDecimal no2) {
		if (no1 == null && no2 == null)
			return BigDecimal.ZERO;
		if (no1 == null)
			return no2;
		if (no2 == null)
			return no1;
		return no1.add(no2);
	}
}