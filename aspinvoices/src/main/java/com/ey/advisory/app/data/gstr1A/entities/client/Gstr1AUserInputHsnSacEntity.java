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
@Table(name = "GSTR1A_USERINPUT_HSNSAC")
@Setter
@Getter
@ToString
public class Gstr1AUserInputHsnSacEntity {

	@Expose
	@SerializedName("id")
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GSTR1A_USERINPUT_HSNSAC_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;

	@Column(name = "HSNSAC")
	private String hsn;

	@Column(name = "SUPPLIER_GSTIN")
	private String sgstin;

	@Expose
	@SerializedName("returnPeriod")
	@Column(name = "RETURN_PERIOD")
	private String returnPeriod;

	@Expose
	@SerializedName("derivedRetPeriod")
	@Column(name = "DERIVED_RET_PERIOD")
	private String derivedRetPeriod;

	@Column(name = "HSNSAC_DESC")
	private String desc;

	@Column(name = "UQC")
	private String uqc;

	@Column(name = "DOC_KEY")
	private String docKey;
	
	@Column(name = "TAX_RATE")
	private BigDecimal usrRate;
	

	@Column(name = "ITM_QTY")
	private BigDecimal usrQunty;
	
	@Column(name = "TOTAL_VALUE")
	private BigDecimal usrTotalValue;

	@Column(name = "TAXABLE_VALUE")
	private BigDecimal usrTaxableValue;

	@Column(name = "IGST_AMT")
	private BigDecimal usrIgst;

	@Column(name = "CGST_AMT")
	private BigDecimal usrCgst;

	@Column(name = "SGST_AMT")
	private BigDecimal usrSgst;

	@Column(name = "CESS_AMT")
	private BigDecimal usrCess;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Expose
	@Column(name = "CREATE_ON")
	private LocalDateTime createdOn;

	@Expose
	@SerializedName("isSaved")
	@Column(name = "IS_SAVED_TO_GSTN")
	private boolean isSaved;

	@Expose
	@SerializedName("isSent")
	@Column(name = "IS_SENT_TO_GSTN")
	private boolean isSent;

	@Expose
	@SerializedName("sentToGSTNDate")
	@Column(name = "SENT_TO_GSTN_DATE")
	private LocalDate sentToGSTNDate;

	@Expose
	@SerializedName("savedToGSTNDate")
	@Column(name = "SAVED_TO_GSTN_DATE")
	private LocalDate savedToGSTNDate;

	@Expose
	@SerializedName("isGstnError")
	@Column(name = "GSTN_ERROR")
	private boolean isGstnError;

	@Expose
	@SerializedName("gstnBatchId")
	@Column(name = "BATCH_ID")
	private Long gstnBatchId;

	@Expose
	@SerializedName("gstinTaxPeriod")
	@Column(name = "GTSIN_TAXPERIOD_COM")
	protected String gstinTaxPeriod;
	
	@Expose
	@SerializedName("softDeleteReason")
	@Column(name = "SOFT_DELETE_REASON")
	protected String softDeleteReason;
	
	@Expose
	@SerializedName("recordType")
	@Column(name = "RECORD_TYPE")
	protected String recordType;
}
