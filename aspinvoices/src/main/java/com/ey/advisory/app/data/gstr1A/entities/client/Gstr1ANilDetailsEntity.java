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

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Shashikant.Shukla
 *
 */

@Entity
@Table(name = "GSTR1A_PROCESSED_NILEXTNON")
@Data
@NoArgsConstructor
public class Gstr1ANilDetailsEntity {

	@Expose
	@SerializedName("id")
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GSTR1A_PROCESSED_NILEXTNON_SEQ", allocationSize = 100)
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
	@SerializedName("hsn")
	@Column(name = "ITM_HSNSAC")
	protected String hsn;

	@Expose
	@SerializedName("description")
	@Column(name = "ITM_DESCRIPTION")
	protected String description;

	@Expose
	@SerializedName("uqc")
	@Column(name = "ITM_UQC")
	protected String uqc;

	@Expose
	@SerializedName("qnt")
	@Column(name = "ITM_QTY")
	protected BigDecimal qnt;


	@Expose
	@SerializedName("derivedRetPeriod")
	@Column(name = "DERIVED_RET_PERIOD")
	protected Integer derivedRetPeriod;

	@Expose
	@SerializedName("tableSection")
	@Column(name = "TABLE_SECTION")
	private String tableSection;

	@Expose
	@SerializedName("nKey")
	@Column(name = "N_INVKEY")
	private String nKey;

	@Expose
	@SerializedName("nGstnKey")
	@Column(name = "N_GSTN_INVKEY")
	private String nGstnKey;

	@Expose
	@SerializedName("dataOriginType")
	@Column(name = "DATAORIGINTYPECODE")
	private String dataOriginType;

	@Expose
	@SerializedName("isDelete")
	@Column(name = "IS_DELETE")
	protected boolean isDelete;

	@Expose
	@SerializedName("isInfo")
	@Column(name = "IS_INFORMATION")
	protected boolean isInfo;

	@Expose
	@SerializedName("nilInterReg")
	@Column(name = "NIL_INTERSTATE_REG")
	private BigDecimal nilInterReg;

	@Expose
	@SerializedName("nilIntraReg")
	@Column(name = "NIL_INTRASTATE_REG")
	private BigDecimal nilIntraReg;

	@Expose
	@SerializedName("nilInterUnReg")
	@Column(name = "NIL_INTERSTATE_UNREG")
	private BigDecimal nilInterUnReg;

	@Expose
	@SerializedName("nilIntraUnReg")
	@Column(name = "NIL_INTRASTATE_UNREG")
	private BigDecimal nilIntraUnReg;

	@Expose
	@SerializedName("extInterReg")
	@Column(name = "EXT_INTERSTATE_REG")
	private BigDecimal extInterReg;

	@Expose
	@SerializedName("extIntraReg")
	@Column(name = "EXT_INTRASTATE_REG")
	private BigDecimal extIntraReg;

	@Expose
	@SerializedName("extInterUnReg")
	@Column(name = "EXT_INTERSTATE_UNREG")
	private BigDecimal extInterUnReg;

	@Expose
	@SerializedName("extIntraUnReg")
	@Column(name = "EXT_INTRASTATE_UNREG")
	private BigDecimal extIntraUnReg;

	@Expose
	@SerializedName("nonInterReg")
	@Column(name = "NON_INTERSTATE_REG")
	private BigDecimal nonInterReg;

	@Expose
	@SerializedName("nonIntraReg")
	@Column(name = "NON_INTRASTATE_REG")
	private BigDecimal nonIntraReg;

	@Expose
	@SerializedName("nonInterUnReg")
	@Column(name = "NON_INTERSTATE_UNREG")
	private BigDecimal nonInterUnReg;

	@Expose
	@SerializedName("nonIntraUnReg")
	@Column(name = "NON_INTRASTATE_UNREG")
	private BigDecimal nonIntraUnReg;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;
	
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

	public Gstr1ANilDetailsEntity(String nKey2) {
		this.nKey = nKey2;
	}

	public Gstr1ANilDetailsEntity add(Gstr1ANilDetailsEntity newObj) {
		this.sgstin = newObj.sgstin;
		this.returnPeriod = newObj.returnPeriod;
		this.nilInterReg = addBigDecimals(this.nilInterReg, newObj.nilInterReg);
		this.nilIntraReg = addBigDecimals(this.nilIntraReg, newObj.nilIntraReg);
		this.nilInterUnReg = addBigDecimals(this.nilInterUnReg,
				newObj.nilInterUnReg);
		this.nilIntraUnReg = addBigDecimals(this.nilIntraUnReg,
				newObj.nilIntraUnReg);
		this.nonInterReg = addBigDecimals(this.nonInterReg, newObj.nonInterReg);
		this.nonIntraReg = addBigDecimals(this.nonIntraReg, newObj.nonIntraReg);
		this.nonInterUnReg = addBigDecimals(this.nonInterUnReg,
				newObj.nonInterUnReg);
		this.nonIntraUnReg = addBigDecimals(this.nonIntraUnReg,
				newObj.nonIntraUnReg);
		this.extInterReg = addBigDecimals(this.extInterReg, newObj.extInterReg);
		this.extIntraReg = addBigDecimals(this.extIntraReg, newObj.extIntraReg);
		this.extInterUnReg = addBigDecimals(this.extInterUnReg,
				newObj.extInterUnReg);
		this.extIntraUnReg = addBigDecimals(this.extIntraUnReg,
				newObj.extIntraUnReg);
		this.nKey = newObj.nKey;
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