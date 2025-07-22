package com.ey.advisory.app.data.gstr1A.entities.client;

import java.math.BigDecimal;
import java.time.LocalDate;
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
 * @author Shashikant.Shukla
 *
 */

@Entity
@Table(name = "GSTR1A_PROCESSED_B2CS")
@Setter
@Getter
@ToString
public class Gstr1AB2csDetailsEntity {

	@Expose
	@Id
	@SerializedName("id")
	@SequenceGenerator(name = "sequence", sequenceName = "GSTR1A_PROCESSED_B2CS_SEQ", allocationSize = 100)
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
	@SerializedName("derivedRetPeriod")
	@Column(name = "DERIVED_RET_PERIOD")
	protected Integer derivedRetPeriod;

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
	@SerializedName("orgHsnOrSac")
	@Column(name = "ORG_HSNORSAC")
	protected String orgHsnOrSac;

	@Expose
	@SerializedName("orgUom")
	@Column(name = "ORG_UOM")
	protected String orgUom;

	@Expose
	@SerializedName("orgQnt")
	@Column(name = "ORG_QNT")
	protected BigDecimal orgQnt ;

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
	protected BigDecimal orgSupVal ;

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
	protected BigDecimal newRate;

	@Expose
	@SerializedName("newTaxVal")
	@Column(name = "NEW_TAXABLE_VALUE")
	protected BigDecimal newTaxVal;

	@Expose
	@SerializedName("newGstin")
	@Column(name = "NEW_ECOM_GSTIN")
	protected String newGstin;

	@Expose
	@SerializedName("newSupVal")
	@Column(name = "NEW_ECOM_SUP_VAL")
	protected BigDecimal newSupVal;

	@Expose
	@SerializedName("igstAmt")
	@Column(name = "IGST_AMT")
	protected BigDecimal igstAmt;

	@Expose
	@SerializedName("cgstAmt")
	@Column(name = "CGST_AMT")
	protected BigDecimal cgstAmt;

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
	@SerializedName("profitCentre")
	@Column(name = "PROFIT_CENTRE")
	protected String profitCentre;

	@Expose
	@SerializedName("plant")
	@Column(name = "PLANT_CODE")
	protected String plant;

	@Expose
	@SerializedName("division")
	@Column(name = "DIVISION")
	protected String division;

	@Expose
	@SerializedName("location")
	@Column(name = "LOCATION")
	protected String location;

	@Expose
	@SerializedName("salesOrganisation")
	@Column(name = "SALES_ORGANIZATION")
	protected String salesOrganisation;

	@Expose
	@SerializedName("distributionChannel")
	@Column(name = "DISTRIBUTION_CHANNEL")
	protected String distributionChannel;

	@Expose
	@SerializedName("userAccess1")
	@Column(name = "USERACCESS1")
	protected String userAccess1;

	@Expose
	@SerializedName("userAccess2")
	@Column(name = "USERACCESS2")
	protected String userAccess2;

	@Expose
	@SerializedName("userAccess3")
	@Column(name = "USERACCESS3")
	protected String userAccess3;

	@Expose
	@SerializedName("userAccess4")
	@Column(name = "USERACCESS4")
	protected String userAccess4;

	@Expose
	@SerializedName("userAccess5")
	@Column(name = "USERACCESS5")
	protected String userAccess5;

	@Expose
	@SerializedName("userAccess6")
	@Column(name = "USERACCESS6")
	protected String userAccess6;

	@Expose
	@SerializedName("userDef1")
	@Column(name = "USERDEFINED_FIELD1")
	protected String userDef1;

	@Expose
	@SerializedName("userDef2")
	@Column(name = "USERDEFINED_FIELD2")
	protected String userDef2;

	@Expose
	@SerializedName("userDef3")
	@Column(name = "USERDEFINED_FIELD3")
	protected String userDef3;

	@Expose
	@SerializedName("invB2csKey")
	@Column(name = "B2CS_INVKEY")
	protected String invB2csKey;

	@Expose
	@SerializedName("gstnB2csKey")
	@Column(name = "B2CS_GSTN_INVKEY")
	protected String gstnB2csKey;

	@Expose
	@SerializedName("isInfo")
	@Column(name = "IS_INFORMATION")
	protected boolean isInfo;

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
	
	
	@Expose
	@SerializedName("isSaved")
	@Column(name = "IS_SAVED_TO_GSTN")
	protected boolean isSaved;

	@Expose
	@SerializedName("isSent")
	@Column(name = "IS_SENT_TO_GSTN")
	protected boolean isSent;

	@Expose
	@SerializedName("sentToGSTNDate")
	@Column(name = "SENT_TO_GSTN_DATE")
	protected LocalDate sentToGSTNDate;

	@Expose
	@SerializedName("savedToGSTNDate")
	@Column(name = "SAVED_TO_GSTN_DATE")
	protected LocalDate savedToGSTNDate;

	@Expose
	@SerializedName("isGstnError")
	@Column(name = "GSTN_ERROR")
	protected boolean isGstnError;

	@Expose
	@SerializedName("gstnBatchId")
	@Column(name = "BATCH_ID")
	protected Long gstnBatchId;

	public Gstr1AB2csDetailsEntity(Long fileId, String sgstin,
			String returnPeriod, String transType, String month, String orgPos,
			String orgHsnOrSac, String orgUom, BigDecimal orgQnt,
			BigDecimal orgRate, BigDecimal orgTaxVal, String orgCGstin,
			BigDecimal orgSupVal, String newPos, String newHsnOrSac,
			String newUom, BigDecimal newQnt, BigDecimal newRate,
			BigDecimal newTaxVal, String newGstin, BigDecimal newSupVal,
			BigDecimal igstAmt, BigDecimal cgstAmt, BigDecimal sgstAmt,
			BigDecimal cessAmt, BigDecimal totalValue, String b2csKey,
			Integer derivedRetPeriod) {
		this.fileId = fileId;
		this.sgstin = sgstin;
		this.returnPeriod = returnPeriod;
		this.transType = transType;
		this.month = month;
		this.orgPos = orgPos;
		this.orgHsnOrSac = orgHsnOrSac;
		this.orgUom = orgUom;
		this.orgQnt = (orgQnt != null) ? orgQnt : BigDecimal.ZERO;
		this.orgRate = (orgRate != null) ? orgRate : BigDecimal.ZERO;
		this.orgTaxVal = (orgTaxVal != null) ? orgTaxVal : BigDecimal.ZERO;
		this.orgCGstin = orgCGstin;
		this.orgSupVal = (orgSupVal != null) ? orgSupVal : BigDecimal.ZERO;
		this.newPos = newPos;
		this.newHsnOrSac = newHsnOrSac;
		this.newUom = newUom;
		this.newQnt = (newQnt != null) ? newQnt : BigDecimal.ZERO;
		this.newRate = (newRate != null) ? newRate : BigDecimal.ZERO;
		this.newTaxVal = (newTaxVal != null) ? newTaxVal : BigDecimal.ZERO;
		this.newGstin = newGstin;
		this.newSupVal = (newSupVal != null) ? newSupVal : BigDecimal.ZERO;
		this.igstAmt = (igstAmt != null) ? igstAmt : BigDecimal.ZERO;
		this.cgstAmt = (cgstAmt != null) ? cgstAmt : BigDecimal.ZERO;
		this.sgstAmt = (sgstAmt != null) ? sgstAmt : BigDecimal.ZERO;
		this.cessAmt = (cessAmt != null) ? cessAmt : BigDecimal.ZERO;
		this.totalValue = (totalValue != null) ? totalValue : BigDecimal.ZERO;
		// this.b2csKey = b2csKey;
		this.derivedRetPeriod = derivedRetPeriod;

	}

	public Gstr1AB2csDetailsEntity add(Gstr1AB2csDetailsEntity newObj) {
		this.fileId = newObj.fileId;
		this.sgstin = newObj.sgstin;
		this.returnPeriod = newObj.returnPeriod;
		this.transType = newObj.transType;
		this.month = newObj.month;
		this.orgPos = newObj.orgPos;
		this.orgHsnOrSac = newObj.orgHsnOrSac;
		this.orgUom = newObj.orgUom;
		this.orgQnt = this.orgQnt.add(newObj.orgQnt);
		this.orgRate = this.orgRate.add(newObj.orgRate);
		this.orgTaxVal = this.orgTaxVal.add(newObj.orgTaxVal);
		this.orgCGstin = newObj.orgCGstin;
		this.orgSupVal = this.orgSupVal.add(newObj.orgSupVal);
		this.newPos = newObj.newPos;
		this.newHsnOrSac = newObj.newHsnOrSac;
		this.newUom = newObj.newUom;
		this.newQnt = this.newQnt.add(newObj.newQnt);
		this.newRate = this.newRate.add(newObj.newRate);
		this.newTaxVal = this.newTaxVal.add(newObj.newTaxVal);
		this.newGstin = newObj.newGstin;
		this.newSupVal = this.newSupVal.add(newObj.newSupVal);
		this.igstAmt = this.igstAmt.add(newObj.igstAmt);
		this.cgstAmt = this.cgstAmt.add(newObj.cgstAmt);
		this.sgstAmt = this.sgstAmt.add(newObj.sgstAmt);
		this.cessAmt = this.cessAmt.add(newObj.cessAmt);
		this.totalValue = this.totalValue.add(newObj.totalValue);
		// this.b2csKey = newObj.b2csKey;
		this.derivedRetPeriod = newObj.derivedRetPeriod;
		return this;
	}

	public Gstr1AB2csDetailsEntity() {
	}

	public Gstr1AB2csDetailsEntity(String b2csKey) {
		// this.b2csKey = b2csKey;
	}
}
