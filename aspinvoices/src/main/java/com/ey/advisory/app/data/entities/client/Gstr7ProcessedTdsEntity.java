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

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Anand3.M
 *
 */
@Data
@Entity
@Table(name = "GSTR7_PROCESSED_TDS")
public class Gstr7ProcessedTdsEntity {

	@Expose
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GSTR7_PROCESSED_TDS_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;

	@Column(name = "FILE_ID")
	private Long fileId;

	@Column(name = "RETURN_PERIOD")
	private String returnPeriod;

	@Column(name = "DERIVED_RET_PERIOD")
	private Integer derReturnPeriod;
	
	@Column(name = "DATAORIGINTYPECODE")
	private String dataOriginType;

	@Column(name = "AS_ENTERED_ID")
	private Long asEnteredId;

	@Column(name = "ACTION_TYPE")
	private String actType;

	@Column(name = "TABLE_NUM")
	private String tabNum;

	@Column(name = "TDS_DEDUCTOR_GSTIN")
	private String tdsGstin;

	@Column(name = "ORG_TDS_DEDUCTEE_GSTIN")
	private String orgTdsGstin;

	@Column(name = "ORG_RETURN_PERIOD")
	private String orgRetPeriod;

	@Column(name = "ORG_GROSS_AMT")
	private BigDecimal orgGrossAmt;

	@Column(name = "NEW_TDS_DEDUCTEE_GSTIN")
	private String newGstin;

	@Column(name = "NEW_GROSS_AMT")
	private BigDecimal newGrossAmt;

	@Column(name = "INVOICE_VALUE")
	private BigDecimal invValue;

	@Column(name = "IGST_AMT")
	private BigDecimal igstAmt;

	@Column(name = "CGST_AMT")
	private BigDecimal cgstAmt;

	@Column(name = "SGST_AMT")
	private BigDecimal sgstAmt;

	@Column(name = "CONTRACT_NUMBER")
	private String conNumber;

	@Column(name = "CONTRACT_DATE")
	private LocalDate conDate;

	@Column(name = "CONTRACT_VALUE")
	private BigDecimal conValue;

	@Column(name = "PAYMENT_ADV_NUM")
	private String payNum;

	@Column(name = "PAYMENT_ADV_DATE")
	private LocalDate payDate;

	@Column(name = "DOC_NUM")
	private String docNum;

	@Column(name = "DOC_DATE")
	private LocalDate docDate;

	@Column(name = "PLANT_CODE")
	private String plantCode;

	@Column(name = "DIVISION")
	private String division;

	@Column(name = "PURCHASE_ORGANIZATION")
	private String purOrg;

	@Column(name = "PROFIT_CENTRE1")
	private String proCen1;

	@Column(name = "PROFIT_CENTRE2")
	private String proCen2;

	@Column(name = "USERDEFINED_FIELD1")
	private String usrDefField1;

	@Column(name = "USERDEFINED_FIELD2")
	private String usrDefField2;

	@Column(name = "USERDEFINED_FIELD3")
	private String usrDefField3;

	@Column(name = "TDS_INVKEY")
	private String tdsInvKey;

	@Column(name = "IS_INFORMATION")
	private boolean isInformation;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Column(name = "IS_SENT_TO_GSTN")
	private boolean isSentGstn;

	@Column(name = "IS_SAVED_TO_GSTN")
	private boolean isSavedGstn;

	@Column(name = "GSTN_ERROR")
	private boolean gstnError;

	@Column(name = "SENT_TO_GSTN_DATE")
	private LocalDate sentGstnDate;

	@Column(name = "SAVED_TO_GSTN_DATE")
	private LocalDate savedGstnDate;

	@Column(name = "IS_SUBMITTED")
	private boolean isSubmitted;

	@Column(name = "SUBMITTED_DATE")
	private LocalDate subDate;

	@Column(name = "IS_FILED")
	private boolean isFiled;

	@Column(name = "FILED_DATE")
	private LocalDate filDate;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;
	
	@Column(name = "BATCH_ID")
	private Long gstnBatchId;
	
	@Column(name = "GSTN_ERROR_CODE")
	protected String gstnErrorCode;
	
	@Column(name = "GSTN_ERROR_DESCRIPTION")
	protected String gstnErrorDesc;	
	
	@Column(name = "GSTIN_KEY")
	protected String gstinkey;
	

}
