package com.ey.advisory.app.data.entities.client;

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
import jakarta.persistence.Transient;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "GSTR1_PROCESSED_ADV_RECEIVED")
@Setter
@Getter
@ToString
public class Gstr1ARDetailsEntity {

	@Expose
	@Id
	@SerializedName("id")
	@SequenceGenerator(name = "sequence", sequenceName = "GSTR1_AS_ENTERED_ADV_RECEVIED_SEQ", allocationSize = 100)
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
	@SerializedName("derivedRetPeriod")
	@Column(name = "DERIVED_RET_PERIOD")
	protected Integer derivedRetPeriod;

	@Expose
	@SerializedName("sgstin")
	@Column(name = "SUPPLIER_GSTIN")
	protected String sgstin;

	@Expose
	@SerializedName("returnPeriod")
	@Column(name = "RETURN_PERIOD")
	protected String returnPeriod;

	@Expose
	@SerializedName("returnType")
	@Column(name = "RETURN_TYPE")
	protected String returnType;

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
	protected BigDecimal orgRate;

	@Expose
	@SerializedName("orgGrossAdvRec")
	@Column(name = "ORG_GROSS_ADV_RECEIVED")
	protected BigDecimal orgGrossAdvRec;

	@Expose
	@SerializedName("newPos")
	@Column(name = "NEW_POS")
	protected String newPos;

	@Expose
	@SerializedName("newRate")
	@Column(name = "NEW_RATE")
	protected BigDecimal newRate;

	@Expose
	@SerializedName("newGrossAdvRec")
	@Column(name = "NEW_GROSS_ADV_RECEIVED")
	protected BigDecimal newGrossAdvRec;

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
	protected BigDecimal sgstAmt;

	@Expose
	@SerializedName("cessAmt")
	@Column(name = "CESS_AMT")
	protected BigDecimal cessAmt;

	@Expose
	@SerializedName("profitCentre")
	@Column(name = "PROFIT_CENTRE")
	protected String profitCentre;

	@Expose
	@SerializedName("division")
	@Column(name = "DIVISION")
	protected String division;

	@Expose
	@SerializedName("location")
	@Column(name = "LOCATION")
	protected String location;

	@Expose
	@SerializedName("plant")
	@Column(name = "PLANT_CODE")
	protected String plant;

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
	@SerializedName("invAtKey")
	@Column(name = "AT_INVKEY")
	protected String invAtKey;

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
	
	@Transient
	private String uiSectionType;
	
	@Transient
	private Long sNo;
	
	public Gstr1ARDetailsEntity(Long fileId, Long asEnterId,
			Integer derivedRetPeriod, String sgstin, String returnPeriod,
			String returnType, String transType, String month, String orgPos,
			BigDecimal orgRate, BigDecimal orgGrossAdvRec, String newPos,
			BigDecimal newRate, BigDecimal newGrossAdvRec, BigDecimal igstAmt,
			BigDecimal cgstAmt, BigDecimal sgstAmt, BigDecimal cessAmt) {
		this.fileId = fileId;
		this.asEnterId = asEnterId;
		this.derivedRetPeriod = derivedRetPeriod;
		this.sgstin = sgstin;
		this.returnPeriod = returnPeriod;
		this.returnType = returnType;
		this.transType = transType;
		this.month = month;
		this.orgPos = orgPos;
		this.orgRate = (orgRate != null) ? orgRate : BigDecimal.ZERO;
		this.orgGrossAdvRec = (orgGrossAdvRec != null) ? orgGrossAdvRec
				: BigDecimal.ZERO;
		this.newPos = newPos;
		this.newRate = (newRate != null) ? newRate : BigDecimal.ZERO;

		this.newGrossAdvRec = (newGrossAdvRec != null) ? newGrossAdvRec
				: BigDecimal.ZERO;

		this.igstAmt = (igstAmt != null) ? igstAmt : BigDecimal.ZERO;
		this.cgstAmt = (cgstAmt != null) ? cgstAmt : BigDecimal.ZERO;
		this.sgstAmt = (sgstAmt != null) ? sgstAmt : BigDecimal.ZERO;
		this.cessAmt = (cessAmt != null) ? cessAmt : BigDecimal.ZERO;

	}

	public Gstr1ARDetailsEntity add(Gstr1ARDetailsEntity newObj) {
		this.fileId = newObj.fileId;
		this.asEnterId = newObj.asEnterId;
		this.derivedRetPeriod = newObj.derivedRetPeriod;
		this.sgstin = newObj.sgstin;
		this.returnPeriod = newObj.returnPeriod;
		this.returnType = newObj.returnType;
		this.transType = newObj.transType;
		this.month = newObj.month;
		this.orgPos = newObj.orgPos;
		this.orgRate = this.orgRate.add(newObj.orgRate);
		this.orgGrossAdvRec = this.orgGrossAdvRec.add(newObj.orgGrossAdvRec);
		this.newPos = newObj.newPos;
		this.newRate = this.newRate.add(newObj.newRate);
		this.newGrossAdvRec = this.newGrossAdvRec.add(newObj.newGrossAdvRec);

		this.igstAmt = this.igstAmt.add(newObj.igstAmt);
		this.cgstAmt = this.cgstAmt.add(newObj.cgstAmt);
		this.sgstAmt = this.sgstAmt.add(newObj.sgstAmt);
		this.cessAmt = this.cessAmt.add(newObj.cessAmt);

		return this;
	}

	public Gstr1ARDetailsEntity() {
	}

	public Gstr1ARDetailsEntity(String atKey) {
		// this.b2csKey = b2csKey;
	}
}
