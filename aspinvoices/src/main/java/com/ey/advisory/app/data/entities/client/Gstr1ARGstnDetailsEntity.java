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

@Entity
@Table(name = "GSTR1_GSTN_ADV_RECEVIED")
@Setter
@Getter
@ToString
public class Gstr1ARGstnDetailsEntity {

	@Expose
	@Id
	@SerializedName("id")
	@SequenceGenerator(name = "sequence", sequenceName = "GSTR1_GSTN_ADV_RECEVIED_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	protected Long id;

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
	protected String sgstin;

	@Expose
	@SerializedName("returnPeriod")
	@Column(name = "RETURN_PERIOD")
	protected String returnPeriod;

	@Expose
	@SerializedName("transType")
	@Column(name = "TRAN_TYPE")
	protected String transType;

	@Expose
	@SerializedName("month")
	@Column(name = "MONTH")
	protected String month;

	@Expose
	@SerializedName("orgPos")
	@Column(name = "ORG_POS")
	protected String orgPos;

	@Expose
	@SerializedName("orgRate")
	@Column(name = "ORG_RATE")
	protected BigDecimal orgRate ;

	@Expose
	@SerializedName("orgGrossAdvRec")
	@Column(name = "ORG_GROSS_ADV_RECEIVED")
	protected BigDecimal orgGrossAdvRec ;

	@Expose
	@SerializedName("newPos")
	@Column(name = "NEW_POS")
	protected String newPos;

	@Expose
	@SerializedName("newRate")
	@Column(name = "NEW_RATE")
	protected BigDecimal newRate ;

	@Expose
	@SerializedName("newGrossAdvRec")
	@Column(name = "NEW_GROSS_ADV_RECEIVED")
	protected BigDecimal newGrossAdvRec ;

	@Expose
	@SerializedName("igstAmt")
	@Column(name = "IGST_AMT")
	protected BigDecimal igstAmt ;

	@Expose
	@SerializedName("cgstAmt")
	@Column(name = "CGST_AMT")
	protected BigDecimal cgstAmt ;

	@Expose
	@SerializedName("sgstAmt")
	@Column(name = "SGST_AMT")
	protected BigDecimal sgstAmt ;

	@Expose
	@SerializedName("cessAmt")
	@Column(name = "CESS_AMT")
	protected BigDecimal cessAmt ;

	@Expose
	@SerializedName("gstnAtKey")
	@Column(name = "AT_GSTN_INVKEY")
	protected String gstnAtKey;

	@Expose
	@SerializedName("isDelete")
	@Column(name = "IS_DELETE")
	protected boolean isDelete;

	@Expose
	@SerializedName("isInfo")
	@Column(name = "IS_INFORMATION")
	protected boolean isInfo;

	@Expose
	@SerializedName("isSentGstn")
	@Column(name = "IS_SENT_TO_GSTN")
	protected boolean isSentGstn;

	@Expose
	@SerializedName("isSavedGstn")
	@Column(name = "IS_SAVED_TO_GSTN")
	protected boolean isSavedGstn;

	@Expose
	@SerializedName("gstnError")
	@Column(name = "GSTN_ERROR")
	protected boolean gstnError;

	@Expose
	@SerializedName("batchId")
	@Column(name = "BATCH_ID")
	protected boolean batchId;

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

	public Gstr1ARGstnDetailsEntity add(Gstr1ARGstnDetailsEntity newObj) {
		this.sgstin = newObj.sgstin;
		this.returnPeriod = newObj.returnPeriod;
		this.transType = newObj.transType;
		this.month = newObj.month;
		this.orgPos = newObj.orgPos;
		this.orgRate = newObj.orgRate;

		this.newPos = newObj.newPos;

		this.newRate = newObj.newRate;

		this.igstAmt = addBigDecimals(this.igstAmt, newObj.igstAmt);
		this.sgstAmt = addBigDecimals(this.sgstAmt, newObj.sgstAmt);
		this.cgstAmt = addBigDecimals(this.cgstAmt, newObj.cgstAmt);
		this.cessAmt = addBigDecimals(this.cessAmt, newObj.cessAmt);

		this.gstnAtKey = newObj.gstnAtKey;
		this.createdBy = newObj.createdBy;
		this.createdOn = newObj.createdOn;
		this.asEnterId = newObj.asEnterId;
		return this;

	}

	private BigDecimal addBigDecimals(BigDecimal bd1, BigDecimal bd2) {
		if (bd1 == null && bd2 == null)
			return BigDecimal.ZERO;
		if (bd1 == null)
			return bd2;
		if (bd2 == null)
			return bd1;
		return bd1.add(bd2);
	}

}
