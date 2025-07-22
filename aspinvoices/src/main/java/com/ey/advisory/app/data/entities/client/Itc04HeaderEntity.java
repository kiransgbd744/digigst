/**
 * 
 */
package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Cascade;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Laxmi.Salukuti
 *
 */
@Entity
@Table(name = "ITC04_HEADER")
@Setter
@Getter
@ToString
public class Itc04HeaderEntity {

	@Expose
	@SerializedName("id")
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "ITC04_HEADER_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	protected Long id;

	@Expose
	@SerializedName("tableNumber")
	@Column(name = "TABLE_NUMBER")
	protected String tableNumber;

	@Expose
	@SerializedName("actionType")
	@Column(name = "ACTION_TYPE")
	protected String actionType;

	@Expose
	@SerializedName("fyDcDate")
	@Column(name = "FI_YEAR_DC_DATE")
	protected String fyDcDate;

	@Expose
	@SerializedName("fyjwDcDate")
	@Column(name = "FI_YEAR_JWDC_DATE")
	protected String fyjwDcDate;

	@Expose
	@SerializedName("fyInvDate")
	@Column(name = "FI_YEAR_INV_DATE")
	protected String fyInvDate;

	@Expose
	@SerializedName("finYear")
	@Column(name = "FI_YEAR")
	protected String finYear;

	@Expose
	@SerializedName("retPeriod")
	@Column(name = "RETURN_PERIOD")
	protected String retPeriod;

	@Expose
	@SerializedName("qRetPeriod")
	@Column(name = "QRETURN_PERIOD")
	protected String qRetPeriod;
	
	@Expose
	@SerializedName("retPeriodDocKey")
	@Column(name = "RETURN_PERIOD_DOCKEY")
	protected String retPeriodDocKey;
	
	@Expose
	@SerializedName("supplierGstin")
	@Column(name = "SUPPLIER_GSTIN")
	protected String supplierGstin;

	@Expose
	@SerializedName("dChallanNo")
	@Column(name = "DELIVERY_CHALLAN_NO")
	protected String deliveryChallanaNumber;

	@Expose
	@SerializedName("dChalanDate")
	@Column(name = "DELIVERY_CHALLAN_DATE")
	protected LocalDate deliveryChallanaDate;

	@Expose
	@SerializedName("jdChallanNo")
	@Column(name = "JW_DELIVERY_CHALLAN_NO")
	protected String jwDeliveryChallanaNumber;

	@Expose
	@SerializedName("jdChalanDate")
	@Column(name = "JW_DELIVERY_CHALLAN_DATE")
	protected LocalDate jwDeliveryChallanaDate;

	@Expose
	@SerializedName("invNumber")
	@Column(name = "INV_NUM")
	protected String invNumber;

	@Expose
	@SerializedName("invDate")
	@Column(name = "INV_DATE")
	protected LocalDate invDate;

	@Expose
	@SerializedName("jwGstin")
	@Column(name = "JW_GSTIN")
	protected String jobWorkerGstin;

	@Expose
	@SerializedName("jwStateCode")
	@Column(name = "JW_STATE_CODE")
	protected String jobWorkerStateCode;

	@Expose
	@SerializedName("jwId")
	@Column(name = "JW_ID")
	protected String jobWorkerId;

	@Expose
	@SerializedName("jwName")
	@Column(name = "JW_NAME")
	protected String jobWorkerName;

	@Expose
	@SerializedName("jwType")
	@Column(name = "JW_WORKER_TYPE")
	protected String jobWorkerType;

	@Expose
	@SerializedName("taxableVal")
	@Column(name = "TAXABLE_VALUE")
	protected BigDecimal taxableValue;

	@Expose
	@SerializedName("igstAmt")
	@Column(name = "IGST_AMT")
	protected BigDecimal igstAmount;

	@Expose
	@SerializedName("cgstAmt")
	@Column(name = "CGST_AMT")
	protected BigDecimal cgstAmount;

	@Expose
	@SerializedName("sgstAmt")
	@Column(name = "SGST_AMT")
	protected BigDecimal sgstAmount;

	@Expose
	@SerializedName("cessAdvAmt")
	@Column(name = "CESS_AMT_ADVALOREM")
	protected BigDecimal cessAdvaloremAmount;

	@Expose
	@SerializedName("cessSpAmt")
	@Column(name = "CESS_AMT_SPECIFIC")
	protected BigDecimal cessSpecificAmount;

	@Expose
	@SerializedName("stCessAdvAmt")
	@Column(name = "STATE_CESS_AMT_ADVALOREM")
	protected BigDecimal stateCessAdvaloremAmount;

	@Expose
	@SerializedName("stCessSpeAmt")
	@Column(name = "STATE_CESS_AMT_SPECIFIC")
	protected BigDecimal stateCessSpecificAmount;

	@Expose
	@SerializedName("totalValue")
	@Column(name = "TOTAL_VALUE")
	protected BigDecimal totalValue;

	@Expose
	@SerializedName("postingDate")
	@Column(name = "POSTING_DATE")
	protected LocalDate postingDate;

	@Expose
	@SerializedName("userId")
	@Column(name = "USER_ID")
	protected String userId;

	@Expose
	@SerializedName("companyCode")
	@Column(name = "COMPANY_CODE")
	protected String companyCode;

	@Expose
	@SerializedName("sourceId")
	@Column(name = "SOURCE_IDENTIFIER")
	protected String sourceIdentifier;

	@Expose
	@SerializedName("sourFileName")
	@Column(name = "SOURCE_FILE_NAME")
	protected String sourceFileName;

	@Expose
	@SerializedName("division")
	@Column(name = "DIVISION")
	protected String division;

	@Expose
	@SerializedName("profitCentre1")
	@Column(name = "PROFIT_CENTRE1")
	protected String profitCentre1;

	@Expose
	@SerializedName("profitCentre2")
	@Column(name = "PROFIT_CENTRE2")
	protected String profitCentre2;

	@Expose
	@SerializedName("accVoucNo")
	@Column(name = "ACCOUNTING_VOUCHER_NUM")
	protected String accountingVoucherNumber;

	@Expose
	@SerializedName("accVoucDate")
	@Column(name = "ACCOUNTING_VOUCHER_DATE")
	protected LocalDate accountingVoucherDate;

	@Expose
	@SerializedName("initiatedOn")
	@Column(name = "INITIATED_ON")
	protected LocalDateTime initiatedOn;

	@Expose
	@SerializedName("hciReceivedOn")
	@Column(name = "HCI_RECEIVED_ON")
	protected LocalDateTime hciReceivedOn;

	@Expose
	@SerializedName("javaReqReceivedOn")
	@Column(name = "JAVA_REQ_REC_ON")
	protected LocalDateTime reqReceivedOn;

	@Expose
	@SerializedName("javaBeforeSavingOn")
	@Column(name = "JAVA_BEFORE_SAVING_ON")
	protected LocalDateTime beforeSavingOn;

	@Expose
	@SerializedName("erpBatchId")
	@Column(name = "ERP_BATCH_ID")
	protected Long erpBatchId;

	@Expose
	@SerializedName("extractedDate")
	@Column(name = "EXTRACTED_DATE")
	protected LocalDate extractedDate;// ERP Extracted Date - Derived

	@Expose
	@SerializedName("extractedOn")
	@Transient
	protected LocalDateTime extractedOn;// ERP Extracted Date

	@Expose
	@SerializedName("extractedBatchId")
	@Column(name = "EXTRACTED_BATCH_ID")
	protected Long extractedBatchId; // ERP Extracted Batch Id

	@Expose
	@SerializedName("payloadId")
	@Column(name = "PAYLOAD_ID")
	protected String payloadId;

	@Expose
	@SerializedName("gstnBatchId")
	@Column(name = "BATCH_ID")
	protected Long gstnBatchId;

	@Expose
	@SerializedName("fileId")
	@Column(name = "FILE_ID")
	protected Long fileId;

	@Expose
	@SerializedName("fileSetNo")
	@Column(name = "FILE_SET_NO")
	protected Long fileSetNo;

	@Expose
	@SerializedName("filedDate")
	@Column(name = "FILED_DATE")
	protected LocalDate filedDate;
	
	@Expose
	@SerializedName("dataOriginTypeCode")
	@Column(name = "DATAORIGINTYPECODE")
	protected String dataOriginTypeCode;

	@Expose
	@SerializedName("savedToGSTNDate")
	@Column(name = "SAVED_TO_GSTN_DATE")
	protected LocalDate savedToGSTNDate;

	@Expose
	@SerializedName("sentToGSTNDate")
	@Column(name = "SENT_TO_GSTN_DATE")
	protected LocalDate sentToGSTNDate;

	@Expose
	@SerializedName("errCodes")
	@Column(name = "ERROR_CODES")
	private String errCodes;

	@Expose
	@SerializedName("infoCodes")
	@Column(name = "INFORMATION_CODES")
	private String infoCodes;

	@Expose
	@SerializedName("gstnErrorCode")
	@Column(name = "GSTN_ERROR_CODE")
	protected String gstnErrorCode;

	@Expose
	@SerializedName("gstnErrorDesc")
	@Column(name = "GSTN_ERROR_DESC")
	protected String gstnErrorDesc;

	@Expose
	@SerializedName("submittedDate")
	@Column(name = "SUBMITTED_DATE")
	protected LocalDate submittedDate;

	@Expose
	@SerializedName("isProcessed")
	@Column(name = "IS_PROCESSED")
	private boolean isProcessed;

	@Expose
	@SerializedName("isError")
	@Column(name = "IS_ERROR")
	private boolean isError;

	@Expose
	@SerializedName("isInfo")
	@Column(name = "IS_INFORMATION")
	protected boolean isInfo;

	@Expose
	@SerializedName("isSavedToGstn")
	@Column(name = "IS_SAVED_TO_GSTN")
	protected boolean isSavedToGstn;

	@Expose
	@SerializedName("isSentToGstn")
	@Column(name = "IS_SENT_TO_GSTN")
	protected boolean isSentToGstn;

	@Expose
	@SerializedName("isDeleted")
	@Column(name = "IS_DELETE")
	protected boolean isDeleted;

	@Expose
	@SerializedName("isSubmitted")
	@Column(name = "IS_SUBMITTED")
	protected boolean isSubmitted;

	@Expose
	@SerializedName("isGstnError")
	@Column(name = "GSTN_ERROR")
	protected boolean isGstnError;

	@Expose
	@SerializedName("isFiled")
	@Column(name = "IS_FILED")
	protected boolean isFiled;
	
	@Expose
	@SerializedName("receivedDate")
	@Column(name = "RECEIVED_DATE")
	protected LocalDate receivedDate;

	@Column(name = "CREATED_ON")
	protected LocalDateTime createdDate;

	@Column(name = "MODIFIED_ON")
	protected LocalDateTime modifiedOn;

	@Column(name = "CREATED_BY")
	protected String createdBy;

	@Column(name = "MODIFIED_BY")
	protected String modifiedBy;

	@Column(name = "GSTN_KEY")
	protected String gstnKey;

	@Column(name = "DOC_KEY")
	protected String docKey;
	
	@Expose
	@SerializedName("lineItems")
	@OneToMany(mappedBy = "document", fetch = FetchType.EAGER)
	@ToString.Exclude
	@OrderColumn(name = "ITEM_INDEX")
	@Cascade({ org.hibernate.annotations.CascadeType.ALL })
	protected List<Itc04ItemEntity> lineItems = new ArrayList<>();
	
	@Column(name = "SAVE_FLAG")
	protected String saveFlag;

}