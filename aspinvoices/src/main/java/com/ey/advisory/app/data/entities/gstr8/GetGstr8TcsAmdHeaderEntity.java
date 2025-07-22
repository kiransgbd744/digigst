package com.ey.advisory.app.data.entities.gstr8;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
@Entity
@Table(name = "GETGSTR8_TCS_AMD_HEADER")
public class GetGstr8TcsAmdHeaderEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	protected Long id;

	@Expose
	@SerializedName("batchId")
	@Column(name = "BATCH_ID")
	protected Long batchId;

	@Expose
	@SerializedName("ret_period")
	@Column(name = "RETURN_PERIOD")
	private String returnPeriod;

	@Expose
	@SerializedName("derivedTaxperiod")
	@Column(name = "DERIVED_RET_PERIOD")
	protected Integer derivedTaxperiod;

	@Expose
	@SerializedName("flag")
	@Column(name = "FLAG")
	protected String flag;

	@Expose
	@SerializedName("stin")
	@Column(name = "STIN")
	protected String stin;

	@Expose
	@SerializedName("ostin")
	@Column(name = "OSTIN")
	protected String ostin;

	@Expose
	@SerializedName("ofp")
	@Column(name = "OFP")
	protected String ofp;

	@Expose
	@SerializedName("stinName")
	@Column(name = "STIN_NAME")
	protected String stinName;
	
	@Expose
	@SerializedName("ostinName")
	@Column(name = "OSTIN_NAME")
	protected String ostinName;


	@Expose
	@SerializedName("source")
	@Column(name = "SOURCE")
	protected String source;
	
	@Expose
	@SerializedName("actn")
	@Column(name = "ACTION")
	protected String actn;

	@Expose
	@SerializedName("chksum")
	@Column(name = "CHKSUM")
	protected String chksum;

	@Expose
	@SerializedName("supReg")
	@Column(name = "SUP_REG")
	protected BigDecimal supReg;

	@Expose
	@SerializedName("retsupReg")
	@Column(name = "RETSUP_REG")
	protected BigDecimal retsupReg;

	@Expose
	@SerializedName("supUnReg")
	@Column(name = "SUP_UnREG")
	protected BigDecimal supUnReg;

	@Expose
	@SerializedName("retsupUnReg")
	@Column(name = "RETSUP_UNREG")
	protected BigDecimal retsupUnReg;

	@Expose
	@SerializedName("netAmt")
	@Column(name = "NET_AMT")
	protected BigDecimal netAmt;

	@Expose
	@SerializedName("cgstAmt")
	@Column(name = "CGST_AMT")
	protected BigDecimal cgstAmt;

	@Expose
	@SerializedName("sgstAmt")
	@Column(name = "SGST_AMT")
	protected BigDecimal sgstAmt;

	@Expose
	@SerializedName("igstAmt")
	@Column(name = "IGST_AMT")
	protected BigDecimal igstAmt;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Expose
	@SerializedName("docKey")
	@Column(name = "DOC_KEY")
	protected String docKey;

	@Expose
	@SerializedName("gstin")
	@Column(name = "GSTIN")
	protected String gstin;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

}
