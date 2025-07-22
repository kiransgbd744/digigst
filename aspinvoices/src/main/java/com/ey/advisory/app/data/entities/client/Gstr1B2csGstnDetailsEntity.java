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
@Table(name = "GSTR1_GSTN_B2CS")
@Setter
@Getter
@ToString
public class Gstr1B2csGstnDetailsEntity {

	@Expose
	@Id
	@SerializedName("id")
	@SequenceGenerator(name = "sequence", sequenceName = "GSTR1_GSTN_B2CS_SEQ", allocationSize = 100)
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
	@SerializedName("orgQnt")
	@Column(name = "ORG_QNT")
	protected BigDecimal orgQnt;

	@Expose
	@SerializedName("orgRate")
	@Column(name = "ORG_RATE")
	protected BigDecimal orgRate;

	@Expose
	@SerializedName("orgTaxVal")
	@Column(name = "ORG_TAXABLE_VALUE")
	protected BigDecimal orgTaxVal;

	@Expose
	@SerializedName("orgCGstin")
	@Column(name = "ORG_ECOM_GSTIN")
	protected String orgCGstin;

	@Expose
	@SerializedName("orgSupVal")
	@Column(name = "ORG_ECOM_SUP_VAL")
	protected BigDecimal orgSupVal;

	@Expose
	@SerializedName("newPos")
	@Column(name = "NEW_POS")
	protected String newPos;

	@Expose
	@SerializedName("newHsnOrSac")
	@Column(name = "NEW_HSNORSAC")
	protected String newHsnOrSac;

	@Expose
	@SerializedName("newUom")
	@Column(name = "NEW_UOM")
	protected String newUom;

	@Expose
	@SerializedName("newQnt")
	@Column(name = "NEW_QNT")
	protected BigDecimal newQnt ;

	@Expose
	@SerializedName("newRate")
	@Column(name = "NEW_RATE")
	protected BigDecimal newRate ;

	@Expose
	@SerializedName("newTaxVal")
	@Column(name = "NEW_TAXABLE_VALUE")
	protected BigDecimal newTaxVal ;

	@Expose
	@SerializedName("newGstin")
	@Column(name = "NEW_ECOM_GSTIN")
	protected String newGstin;

	@Expose
	@SerializedName("newSupVal")
	@Column(name = "NEW_ECOM_SUP_VAL")
	protected BigDecimal newSupVal ;

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
	@SerializedName("totalValue")
	@Column(name = "TOT_VAL")
	protected BigDecimal totalValue;

	@Expose
	@SerializedName("gstnB2csKey")
	@Column(name = "B2CS_GSTN_INVKEY")
	protected String gstnB2csKey;

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

	public Gstr1B2csGstnDetailsEntity add(Gstr1B2csGstnDetailsEntity newObj) {
		this.sgstin = newObj.sgstin;
		this.returnPeriod = newObj.returnPeriod;
		this.transType = newObj.transType;
		this.month = newObj.month;
		this.orgPos = newObj.orgPos;
		this.orgRate = newObj.orgRate;
		this.orgCGstin = newObj.orgCGstin;
		this.newPos = newObj.newPos;
		this.newHsnOrSac = newObj.newHsnOrSac;
		this.newUom = newObj.newUom;
		this.newRate = newObj.newRate;
		this.newGstin = newObj.newGstin;
		this.orgQnt = newObj.orgQnt;
		this.newQnt = addBigDecimals(this.newQnt, newObj.newQnt);
		this.orgTaxVal = addBigDecimals(this.orgTaxVal, newObj.orgTaxVal);
		this.newTaxVal = addBigDecimals(this.newTaxVal, newObj.newTaxVal);
		this.orgSupVal = addBigDecimals(this.orgSupVal, newObj.orgSupVal);
		this.newSupVal = addBigDecimals(this.newSupVal, newObj.newSupVal);
		this.igstAmt = addBigDecimals(this.igstAmt, newObj.igstAmt);
		this.sgstAmt = addBigDecimals(this.sgstAmt, newObj.sgstAmt);
		this.cgstAmt = addBigDecimals(this.cgstAmt, newObj.cgstAmt);
		this.cessAmt = addBigDecimals(this.cessAmt, newObj.cessAmt);
		this.totalValue = addBigDecimals(this.totalValue, newObj.totalValue);
		this.gstnB2csKey = newObj.gstnB2csKey;
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
