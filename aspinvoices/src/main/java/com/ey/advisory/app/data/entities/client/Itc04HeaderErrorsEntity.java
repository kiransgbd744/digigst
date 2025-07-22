package com.ey.advisory.app.data.entities.client;

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
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Entity
@Table(name = "ITC04_ERR_HEADER")
@Setter
@Getter
@ToString
@Slf4j
public class Itc04HeaderErrorsEntity {

	@Expose
	@SerializedName("id")
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "ITC04_ERR_HEADER_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	protected Long id;

	@Expose
	@SerializedName("tableNumber")
	@Column(name = "TABLE_NUMBER")
	protected String tableNumber;

	@Column(name = "DOC_KEY")
	protected String docKey;

	@Expose
	@SerializedName("actionType")
	@Column(name = "ACTION_TYPE")
	protected String actionType;

	@Expose
	@SerializedName("fyDocDate")
	@Column(name = "FI_YEAR_DC_DATE")
	protected String fyDocDate;

	@Expose
	@SerializedName("fyjwDocDate")
	@Column(name = "FI_YEAR_JWDC_DATE")
	protected String fyjwDocDate;

	@Expose
	@SerializedName("fyInvDate")
	@Column(name = "FI_YEAR_INV_DATE")
	protected String fyInvDate;

	@Expose
	@SerializedName("fy")
	@Column(name = "FI_YEAR")
	protected String fy;

	@Expose
	@SerializedName("retPeriod")
	@Column(name = "RETURN_PERIOD")
	protected String retPeriod;

	@Expose
	@SerializedName("qRetPeriod")
	@Column(name = "QRETURN_PERIOD")
	protected String qRetPeriod;

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
	protected String deliveryChallanaDate;

	@Expose
	@SerializedName("jdChallanNo")
	@Column(name = "JW_DELIVERY_CHALLAN_NO")
	protected String jwDeliveryChallanaNumber;

	@Expose
	@SerializedName("jdChalanDate")
	@Column(name = "JW_DELIVERY_CHALLAN_DATE")
	protected String jwDeliveryChallanaDate;

	@Expose
	@SerializedName("invNumber")
	@Column(name = "INV_NUM")
	protected String invNumber;

	@Expose
	@SerializedName("invDate")
	@Column(name = "INV_DATE")
	protected String invDate;

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
	@SerializedName("itemAccAmt")
	@Column(name = "TAXABLE_VALUE")
	protected String itemAccAmt;

	@Expose
	@SerializedName("igstAmt")
	@Column(name = "IGST_AMT")
	protected String igstAmount;

	@Expose
	@SerializedName("cgstAmt")
	@Column(name = "CGST_AMT")
	protected String cgstAmount;

	@Expose
	@SerializedName("sgstAmt")
	@Column(name = "SGST_AMT")
	protected String sgstAmount;

	@Expose
	@SerializedName("cessAdvAmt")
	@Column(name = "CESS_AMT_ADVALOREM")
	protected String cessAdvaloremAmount;

	@Expose
	@SerializedName("cessSpAmt")
	@Column(name = "CESS_AMT_SPECIFIC")
	protected String cessSpecificAmount;

	@Expose
	@SerializedName("stCessAdvAmt")
	@Column(name = "STATE_CESS_AMT_ADVALOREM")
	protected String stateCessAdvaloremAmount;

	@Expose
	@SerializedName("stCessSpeAmt")
	@Column(name = "STATE_CESS_AMT_SPECIFIC")
	protected String stateCessSpecificAmount;

	@Expose
	@SerializedName("totalValue")
	@Column(name = "TOTAL_VALUE")
	protected String totalValue;

	@Expose
	@SerializedName("postingDate")
	@Column(name = "POSTING_DATE")
	protected String postingDate;

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
	protected String accountingVoucherDate;

	@Expose
	@SerializedName("isDelete")
	@Column(name = "IS_DELETE")
	protected String isDelete;

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
	@SerializedName("fileId")
	@Column(name = "FILE_ID")
	protected Long acceptanceId;

	@Expose
	@SerializedName("dataOriginTypeCode")
	@Column(name = "DATAORIGINTYPECODE")
	protected String dataOriginTypeCode;

	@Expose
	@SerializedName("isError")
	@Column(name = "IS_ERROR")
	private String isError;

	@Expose
	@SerializedName("isProcessed")
	@Column(name = "IS_PROCESSED")
	private String isProcessed;

	@Expose
	@SerializedName("isFiled")
	@Column(name = "IS_FILED")
	private String isFiled;

	@Expose
	@SerializedName("gstinKey")
	@Column(name = "GSTN_KEY")
	private String gstinKey;

	@Expose
	@SerializedName("isSavedToGstn")
	@Column(name = "IS_SAVED_TO_GSTN")
	private String isSavedToGstn;

	@Expose
	@SerializedName("isSentToGstn")
	@Column(name = "IS_SENT_TO_GSTN")
	private String isSentToGstn;

	@Expose
	@SerializedName("isSumbit")
	@Column(name = "IS_SUBMITTED")
	private String isSumbit;

	@Expose
	@SerializedName("gstnErr")
	@Column(name = "GSTN_ERROR")
	private String gstnErr;

	@Expose
	@SerializedName("sentToGstnDate")
	@Column(name = "SENT_TO_GSTN_DATE")
	private String sentToGstnDate;

	@Expose
	@SerializedName("gstnStatus")
	@Column(name = "GSTN_STATUS")
	private String gstnStatus;

	@Expose
	@SerializedName("gstnErrCode")
	@Column(name = "GSTN_ERROR_CODE")
	private String gstnErrCode;

	@Expose
	@SerializedName("gstnErrorDes")
	@Column(name = "GSTN_ERROR_DESC")
	private String gstnErrorDes;

	@Expose
	@SerializedName("submittedDate")
	@Column(name = "SUBMITTED_DATE")
	private String submittedDate;

	@Expose
	@SerializedName("javaBSavingOn")
	@Column(name = "JAVA_BEFORE_SAVING_ON")
	private String javaBSavingOn;

	@Expose
	@SerializedName("erpBatchId")
	@Column(name = "ERP_BATCH_ID")
	private String erpBatchId;

	@Expose
	@SerializedName("exDate")
	@Column(name = "EXTRACTED_DATE")
	private String exDate;

	@Expose
	@SerializedName("exBatchId")
	@Column(name = "EXTRACTED_BATCH_ID")
	private String exBatchId;

	@Expose
	@SerializedName("payloadId")
	@Column(name = "PAYLOAD_ID")
	private String payloadId;

	@Expose
	@SerializedName("batchId")
	@Column(name = "BATCH_ID")
	private Long batchId;

	@Expose
	@SerializedName("fileSetNo")
	@Column(name = "FILE_SET_NO")
	private String fileSetNo;

	@Expose
	@SerializedName("filedDate")
	@Column(name = "FILED_DATE")
	private String filedDate;

	@Expose
	@SerializedName("saveToGstnDate")
	@Column(name = "SAVED_TO_GSTN_DATE")
	private String saveToGstnDate;

	@Expose
	@SerializedName("initiatedOn")
	@Column(name = "INITIATED_ON")
	private String initiatedOn;

	@Expose
	@SerializedName("hciRecOn")
	@Column(name = "HCI_RECEIVED_ON")
	private String hciRecOn;

	@Expose
	@SerializedName("javaReqRecOn")
	@Column(name = "JAVA_REQ_REC_ON")
	private String javaReqRecOn;

	@Expose
	@SerializedName("lineItems")
	@ToString.Exclude
	@OneToMany(mappedBy = "document", fetch = FetchType.EAGER)
	@OrderColumn(name = "ITEM_INDEX")
	@Cascade({ org.hibernate.annotations.CascadeType.ALL })
	protected List<Itc04ItemErrorsEntity> lineItems = new ArrayList<>();

	public Integer getItemNoForIndex(Integer itemIndex) {

		int lineItemCount = this.lineItems.size();

		if (itemIndex < lineItemCount && itemIndex >= 0) {
			Itc04ItemErrorsEntity lineItem = lineItems.get(itemIndex);
			try {
				return Integer.valueOf(lineItem.getItemSerialNumber());
			} catch (Exception e) {
				if (lineItem.getItemSerialNumber() == null
						|| lineItem.getItemSerialNumber().trim().isEmpty()) {

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("lineItem null or empty ");
					}
					LOGGER.error("Line item error " + e.getMessage());

					return null;
				} else {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"lineItem " + lineItem.getItemSerialNumber());
					}
					LOGGER.error("Line item error " + e.getMessage());
					return -1;
				}
			}
		}

		return null;
	}

	@Expose
	@SerializedName("errCodes")
	@Column(name = "ERROR_CODES")
	private String errCodes;
}