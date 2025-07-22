package com.ey.advisory.admin.data.entities.client;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 
 * @author Dibyakanta.Sahoo
 *
 */

@Entity
@Table(name = "GSTR6_ISD_DISTRIBUTION")
@Data
public class Gstr6DistributionEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "TAX_PERIOD")
	private String returnPeriod;

	@Column(name = "ISD_GSTIN")
	private String isdGstin;

	@Column(name = "CUST_GSTIN")
	private String recipientGSTIN;

	@Column(name = "STATE_CODE")
	private String stateCode;

	@Column(name = "ORG_CUST_GSTIN")
	private String originalRecipeintGstin;

	@Column(name = "ORG_STATE_CODE")
	private String originalStatecode;

	@Column(name = "DOC_TYPE")
	private String documentType;

	@Column(name = "SUPPLY_TYPE")
	private String supplyType;

	@Column(name = "DOC_NUM")
	private String docNum;

	@Column(name = "DOC_DATE")
	private LocalDate docDate;

	@Column(name = "ORG_DOC_NUM")
	private String origDocNumber;

	@Column(name = "ORG_DOC_DATE")
	private LocalDate origDocDate;

	@Column(name = "ORG_CR_NUM")
	private String origCrNoteNumber;

	@Column(name = "ORG_CR_DATE")
	private LocalDate origCrNoteDate;

	@Column(name = "ELIGIBLE_INDICATOR")
	private String eligibleIndicator;

	@Column(name = "IGST_AMT_AS_IGST")
	private BigDecimal igstAsIgst;

	@Column(name = "IGST_AMT_AS_SGST")
	private BigDecimal igstAsSgst;

	@Column(name = "IGST_AMT_AS_CGST")
	private BigDecimal igstAsCgst;

	@Column(name = "SGST_AMT_AS_SGST")
	private BigDecimal sgstAsSgst;

	@Column(name = "SGST_AMT_AS_IGST")
	private BigDecimal sgstAsIgst;

	@Column(name = "CGST_AMT_AS_CGST")
	private BigDecimal cgstAsCgst;

	@Column(name = "CGST_AMT_AS_IGST")
	private BigDecimal cgstAsIgst;

	@Column(name = "CESS_AMT")
	private BigDecimal cessAmount;

	@Column(name = "DERIVED_RET_PERIOD")
	private Integer derivedRetPeriod;

	@Column(name = "IS_PROCESSED")
	private boolean isProcessed;

	@Column(name = "IS_INFORMATION")
	protected boolean isInfo;

	@Column(name = "IS_ERROR")
	protected boolean isError;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Column(name = "FILE_ID")
	private Long fileId;

	@Column(name = "AS_ENTERED_ID ")
	protected Long asEnterTableId;

	@Column(name = "CREATED_BY")
	protected String createdBy;

	@Column(name = "CREATED_ON")
	protected LocalDateTime createdOn;

	@Column(name = "DOC_KEY")
	protected String processKey;

	@Column(name = "BATCH_ID")
	private Long gstnBatchId;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	@Column(name = "IS_SENT_TO_GSTN")
	private boolean isSentToGstn;

	@Column(name = "SENT_TO_GSTN_DATE")
	private LocalDate sentToGSTNDate;

	@Column(name = "IS_SAVED_TO_GSTN")
	private boolean isSaveToGstn;

	@Column(name = "SAVED_TO_GSTN_DATE")
	private LocalDate savedToGSTNDate;

	@Column(name = "GSTN_ERROR_CODE")
	protected String gstnErrorCode;

	@Column(name = "GSTN_ERROR_DESC")
	protected String gstnErrorDesc;

	@Column(name = "IS_SUBMITTED")
	private boolean isSubmitted;

//	@Expose
	//@SerializedName("dataOriginType")
	@Column(name = "DATAORIGINTYPECODE")
	protected String dataOriginType;
	
	@Column(name = "SUBMITTED_DATE")
	private LocalDate submittedDate;
	
	

}
