package com.ey.advisory.app.data.entities.client.asprecon;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;

import lombok.Data;

@Entity
@Data
@Table(name = "TBL_GETIRN_LIST")

public class GetIrnListEntity  {

	@Expose
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "TBL_GETIRN_LIST_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;

	@Expose
	@Column(name = "SUPPLIER_GSTIN")
	private String suppGstin;

	@Expose
	@Column(name = "CUST_GSTIN")
	private String cusGstin;
	
	@Expose
	@Column(name = "MONTH_YEAR")
	private String monYear;

	@Expose
	@Column(name = "DERIVED_MONTHYEAR")
	private String derivdMonYear;

	@Expose
	@Column(name = "DOC_NUM")
	private String docNum;
	

	@Expose
	@Column(name = "DOC_DATE")
	private LocalDateTime docDate;

	@Expose
	@Column(name = "DOC_TYPE")
	private String docTyp;

	@Expose
	@Column(name = "SUPPLY_TYPE")
	private String suppType;

	@Expose
	@Column(name = "TOT_INV_AMT")
	private BigDecimal totInvAmt;

	@Expose
	@Column(name = "IRN")
	private String irn;

	@Expose
	@Column(name = "IRN_STATUS")
	private String irnSts;

	@Expose
	@Column(name = "ACK_NO")
	private Long ackNo;
	
	@Expose
	@Column(name = "ACK_DATE")
	private LocalDateTime ackDate ;
	
	@Expose
	@Column(name = "EWB_NUM")
	private String ewbNum;
	
	@Expose
	@Column(name = "EWB_DATE")
	private LocalDateTime ewbDate;
	
	@Expose
	@Column(name = "CANCEL_DATE")
	private LocalDateTime canDate;
	
	@Expose
	@Column(name = "GET_DETAIL_IRN_STATUS")
	private String getIrnDetSts;
	
	@Expose
	@Column(name = "GET_DETAIL_IRN_INITIATED_ON")
	private LocalDateTime getIrnDetIniOn;
	
	@Expose
	@Column(name = "BATCH_ID")
	private Long batchId;
	
	@Expose
	@Column(name = "IS_DELETE")
	private boolean isDelete;
	
	@Expose
	@Column(name = "CREATED_BY")
	private String createdBy;
	
	@Expose
	@Column(name = "UPDATED_BY")
	private String updatedBy;
	
	@Expose
	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;
	
	@Expose
	@Column(name = "UPDATED_ON")
	private LocalDateTime updatedOn;
	
	@Expose
	@Column(name = "QR_CODE_VALIDATED")
	private String qrCodeValidated;

	@Expose
	@Column(name = "QR_CODE_VALIDATION_RESULT")
	private String qrCodeValidationResult;

	@Expose
	@Column(name = "QR_CODE_MATCH_COUNT")
	private Integer qrCodeMatchCount;

	@Expose
	@Column(name = "QR_CODE_MISMATCH_COUNT")
	private Integer qrCodeMismatchCount;

	@Expose
	@Column(name = "QR_CODE_MISMATCH_ATTRIBUTES")
	private String qrCodeMismatchAttributes;

	//three new columns for QR tagging timestatus, PR tagging, PR tagging time status
	@Expose
	@Column(name = "QR_TAGGING_TIME_STATUS")
	private LocalDateTime qrTaggingTimeStatus;

	@Expose
	@Column(name = "PR_TAGGING")
	private String prTagging;

	@Expose
	@Column(name = "PR_TAGGING_TIME_STATUS")
	private LocalDateTime prTaggingTimeStatus;

	@Expose
	@Column(name = "REMARKS")
	private String remarks;
	
	@Expose
	@Column(name = "CANCELLATION_REASON")
	private String cancellationReason;
	
	@Expose
	@Column(name = "CANCELLATION_REM")
	private String cancellationRem;

	@Expose
	@Lob
	@Column(name = "SIGNED_INVOICE")
	private String signedInvoice;
	
	@Expose
	@Lob
	@Column(name = "SIGNED_QRCODE")
	private String SignedQRCode;
	
	@Expose
	@Column(name = "EWB_VALID_Till")
	private LocalDateTime ewbValidTill;
	

	}
