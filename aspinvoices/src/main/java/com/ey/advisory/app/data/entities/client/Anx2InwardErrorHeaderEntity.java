package com.ey.advisory.app.data.entities.client;

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
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Entity
@Table(name = "ANX_INWARD_ERROR_HEADER")
@Slf4j
@Data
public class Anx2InwardErrorHeaderEntity {

	@Expose
	@SerializedName("id")
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "ANX_INWARD_ERROR_HEADER_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;

	@Expose
	@SerializedName("irn")
	@Column(name = "IRN")
	protected String irn;

	@Expose
	@SerializedName("irnDate")
	@Column(name = "IRN_DATE")
	protected String irnDate;

	@Expose
	@SerializedName("docType")
	@Column(name = "DOC_TYPE")
	protected String docType;

	@Expose
	@SerializedName("docNo")
	@Column(name = "DOC_NUM")
	protected String docNo;

	@Expose
	@SerializedName("docDate")
	@Column(name = "DOC_DATE")
	protected String docDate;

	@Expose
	@SerializedName("reverseCharge")
	@Column(name = "REVERSE_CHARGE")
	protected String reverseCharge;

	@Expose
	@SerializedName("suppGstin")
	@Column(name = "SUPPLIER_GSTIN")
	protected String sgstin;

	@Expose
	@SerializedName("custGstin")
	@Column(name = "CUST_GSTIN")
	protected String cgstin;

	@Expose
	@SerializedName("pos")
	@Column(name = "POS")
	protected String pos;

	@Expose
	@SerializedName("dispatcherGstin")
	@Column(name = "DISPATCHER_GSTIN")
	protected String dispatcherGstin;

	@Expose
	@SerializedName("shipToGstin")
	@Column(name = "SHIP_TO_GSTIN")
	protected String shipToGstin;

	@Expose
	@SerializedName("invOtherCharges")
	@Column(name = "INV_OTHER_CHARGES")
	protected String invoiceOtherCharges;

	@Expose
	@SerializedName("invAssessableAmt")
	@Column(name = "INV_ASSESSABLE_AMT")
	protected String invoiceAssessableAmount;

	@Expose
	@SerializedName("invIgstAmt")
	@Column(name = "INV_IGST_AMT")
	protected String invoiceIgstAmount;

	@Expose
	@SerializedName("invCgstAmt")
	@Column(name = "INV_CGST_AMT")
	protected String invoiceCgstAmount;

	@Expose
	@SerializedName("invSgstAmt")
	@Column(name = "INV_SGST_AMT")
	protected String invoiceSgstAmount;

	@Expose
	@SerializedName("invCessAdvaloremAmt")
	@Column(name = "INV_CESS_ADVLRM_AMT")
	protected String invoiceCessAdvaloremAmount;

	@Expose
	@SerializedName("invCessSpecificAmt")
	@Column(name = "INV_CESS_SPECIFIC_AMT")
	protected String invoiceCessSpecificAmount;

	@Expose
	@SerializedName("invStateCessAmt")
	@Column(name = "INV_STATE_CESS_AMT")
	protected String invoiceStateCessAmount;

	@Expose
	@SerializedName("invStateCessSpecificAmt")
	@Column(name = "INV_STATE_CESS_SPECIFIC_AMOUNT")
	protected String invoiceStateSpecifAmount;

	@Expose
	@SerializedName("itcEntitlement")
	@Column(name = "ITC_ENTITLEMENT")
	protected String itcEntitlement;

	@Expose
	@SerializedName("portCode")
	@Column(name = "SHIP_PORT_CODE")
	protected String portCode;

	@Expose
	@SerializedName("billOfEntryNo")
	@Column(name = "BILL_OF_ENTRY")
	protected String billOfEntryNo;

	@Expose
	@SerializedName("billOfEntryDate")
	@Column(name = "BILL_OF_ENTRY_DATE")
	protected String billOfEntryDate;

	@Expose
	@SerializedName("paymentDueDate")
	@Column(name = "PAYMENT_DUE_DATE")
	protected String paymentDueDate;

	@Expose
	@SerializedName("ecomGSTIN")
	@Column(name = "ECOM_GSTIN")
	protected String egstin;

	@Expose
	@SerializedName("returnPeriod")
	@Column(name = "RETURN_PERIOD")
	protected String taxperiod;

	@Expose
	@SerializedName("originalSupplierGstin")
	@Column(name = "ORIG_SUPPLIER_GSTIN")
	protected String origSgstin;

	@Expose
	@SerializedName("diffPercent")
	@Column(name = "DIFF_PERCENT")
	protected String diffPercent;

	@Expose
	@SerializedName("sec7OfIgstFlag")
	@Column(name = "SECTION7_OF_IGST_FLAG")
	protected String section7OfIgstFlag;

	@Expose
	@SerializedName("custOrSupType")
	@Column(name = "CUST_SUPP_TYPE")
	private String supplierType;

	@Expose
	@SerializedName("tcsFlag")
	@Column(name = "TCS_FLAG")
	protected String tcsFlag;

	@Expose
	@SerializedName("tdsFlag")
	@Column(name = "TDS_FLAG")
	protected String tdsFlag;

	@Expose
	@SerializedName("division")
	@Column(name = "DIVISION")
	protected String division;

	@Expose
	@SerializedName("location")
	@Column(name = "LOCATION")
	protected String location;

	@Expose
	@SerializedName("purchaseOrganization")
	@Column(name = "PURCHASE_ORGANIZATION")
	protected String purchaseOrganization;

	@Expose
	@SerializedName("profitCentre")
	@Column(name = "PROFIT_CENTRE")
	protected String profitCentre;

	@Expose
	@SerializedName("postingDate")
	@Column(name = "POSTING_DATE")
	protected String postingDate;

	@Expose
	@SerializedName("ewbNo")
	@Column(name = "EWAY_BILL_NUM")
	protected String eWayBillNo;

	@Expose
	@SerializedName("ewbDate")
	@Column(name = "EWAY_BILL_DATE")
	protected String eWayBillDate;

	@Expose
	@SerializedName("purchaseVoucherDate")
	@Column(name = "PURCHASE_VOUCHER_DATE")
	private String purchaseVoucherDate;

	@Expose
	@SerializedName("udf28")
	@Column(name = "USERDEFINED_FIELD28")
	protected String userDefinedField28;
	
	@Expose
	@SerializedName("custOrSuppCode")
	@Column(name = "CUST_SUPP_CODE")
	protected String custOrSuppCode;

	@Column(name = "CREATED_BY")
	protected String createdBy;

	@Column(name = "CREATED_ON")
	protected LocalDateTime createdDate;

	@Column(name = "MODIFIED_BY")
	protected String modifiedBy;

	@Column(name = "MODIFIED_ON")
	protected LocalDateTime updatedDate;

	@Column(name = "RECEIVED_DATE")
	protected LocalDate receivedDate;

	@Expose
	@SerializedName("isDeleted")
	@Column(name = "IS_DELETE")
	protected String isDeleted;

	@Expose
	@SerializedName("isError")
	@Column(name = "IS_ERROR")
	private String isError;

	@Expose
	@SerializedName("isProcessed")
	@Column(name = "IS_PROCESSED")
	private String isProcessed;

	@Expose
	@SerializedName("isInfo")
	@Column(name = "IS_INFORMATION")
	protected String isInfo;

	@Expose
	@SerializedName("isSaved")
	@Column(name = "IS_SAVED_TO_GSTN")
	protected String isSaved;

	@Expose
	@SerializedName("isSent")
	@Column(name = "IS_SENT_TO_GSTN")
	protected String isSent;

	@Expose
	@SerializedName("isSubmitted")
	@Column(name = "IS_SUBMITTED")
	protected String isSubmitted;

	@Expose
	@SerializedName("derivedTaxperiod")
	@Column(name = "DERIVED_RET_PERIOD")
	protected Integer derivedTaxperiod;

	@Expose
	@SerializedName("sentToGstnDate")
	@Column(name = "SENT_TO_GSTN_DATE")
	protected String sentToGstnDate;

	@Expose
	@SerializedName("fileId")
	@Column(name = "FILE_ID")
	protected Long acceptanceId;

	@Expose
	@SerializedName("userId")
	@Column(name = "USER_ID")
	protected String userId;

	@Expose
	@SerializedName("errCodes")
	@Column(name = "ERROR_CODES")
	private String errCodes;

	@Column(name = "DOC_KEY")
	protected String docKey;

	@Expose
	@SerializedName("dataOriginTypeCode")
	@Column(name = "DATAORIGINTYPECODE")
	protected String dataOriginTypeCode;

	@Expose
	@SerializedName("javaReqReceivedOn")
	@Column(name = "JAVA_REQ_REC_ON")
	protected String reqReceivedOn;

	@Expose
	@SerializedName("javaBeforeSavingOn")
	@Column(name = "JAVA_BEFORE_SAVING_ON")
	protected String beforeSavingOn;

	@Expose
	@SerializedName("fiYear")
	@Column(name = "FI_YEAR")
	protected String finYear;

	@Expose
	@SerializedName("hciReceivedOn")
	@Column(name = "HCI_RECEIVED_ON")
	protected String hciReceivedOn;

	@Expose
	@SerializedName("sourceFileName")
	@Column(name = "SOURCE_FILENAME")
	protected String sourceFileName;

	@Expose
	@SerializedName("gstnStatus")
	@Column(name = "GSTN_STATUS")
	protected String gstnStatus;

	@Expose
	@SerializedName("initiatedOn")
	@Column(name = "INITIATED_ON")
	protected LocalDateTime initiatedOn;

	@Expose
	@SerializedName("sourceIdentifier")
	@Column(name = "SOURCE_IDENTIFIER")
	protected String sourceIdentifier;

	@Expose
	@SerializedName("an2Gstr2TableNo")
	@Column(name = "AN2_TABLE_SECTION")
	protected String tableTypeNew;

	@Expose
	@SerializedName("an2Gstr2SubCategory")
	@Column(name = "AN2_TAX_DOC_TYPE")
	protected String gstnBifurcationNew;

	@Expose
	@SerializedName("gstr2SubCategory")
	@Column(name = "TAX_DOC_TYPE")
	protected String gstnBifurcation;

	@Expose
	@SerializedName("returnType")
	@Column(name = "AN_RETURN_TYPE")
	protected String returnType;

	@Expose
	@SerializedName("checkSum")
	@Column(name = "CHECKSUM")
	protected String checkSum;

	@Expose
	@SerializedName("gstr2TableNo")
	@Column(name = "TABLE_SECTION")
	protected String tableType;

	// Extra columns

	@Expose
	@SerializedName("custOrSupAddr1")
	@Column(name = "CUST_SUPP_ADDRESS1")
	protected String custOrSuppAddress1;

	@Expose
	@SerializedName("custOrSupAddr2")
	@Column(name = "CUST_SUPP_ADDRESS2")
	protected String custOrSuppAddress2;

	@Expose
	@SerializedName("custOrSupAddr3")
	@Column(name = "CUST_SUPP_ADDRESS3")
	protected String custOrSuppAddress3;

	@Expose
	@SerializedName("custOrSupAddr4")
	@Column(name = "CUST_SUPP_ADDRESS4")
	protected String custOrSuppAddress4;

	@Expose
	@SerializedName("plantCode")
	@Column(name = "PLANT_CODE")
	protected String plantCode;

	@Expose
	@SerializedName("profitCentre3")
	@Column(name = "USERACCESS1")
	protected String userAccess1;

	@Expose
	@SerializedName("profitCentre4")
	@Column(name = "UserAccess2")
	protected String userAccess2;

	@Expose
	@SerializedName("profitCentre5")
	@Column(name = "USERACCESS3")
	protected String userAccess3;

	@Expose
	@SerializedName("profitCentre6")
	@Column(name = "USERACCESS4")
	protected String userAccess4;

	@Expose
	@SerializedName("profitCentre7")
	@Column(name = "USERACCESS5")
	protected String userAccess5;

	@Expose
	@SerializedName("profitCentre8")
	@Column(name = "USERACCESS6")
	protected String userAccess6;

	@Expose
	@SerializedName("crDrPreGst")
	@Column(name = "CRDR_PRE_GST")
	protected String crDrPreGst;
	
	@Expose
	@SerializedName("supLegalName")
	@Column(name = "CUST_SUPP_NAME")
	protected String supplierLegalName;
	
	@Expose
	@SerializedName("stateApplyingCess")
	@Column(name = "STATE_APPLYING_CESS")
	protected String stateApplyingCess;
	
	@Expose
	@SerializedName("claimRefundFlag")
	@Column(name = "CLAIM_REFUND_FLAG")
	protected String claimRefundFlag;
	
	@Expose
	@SerializedName("autoPopToRefundFlag")
	@Column(name = "AUTOPOPULATE_TO_REFUND")
	protected String autoPopToRefundFlag;
	
	@Expose
	@SerializedName("supplyType")
	@Column(name = "SUPPLY_TYPE")
	protected String supplyType;

	// Extra columns

	/**
	 * Initialize an empty array list to hold the line items. The ITEM_INDEX
	 * column will make sure that the order of the line items are preserved in
	 * the database. This is the JPA way of indexing one to many collections.
	 */
	@Expose
	@SerializedName("lineItems")
	@OneToMany(mappedBy = "document", fetch = FetchType.EAGER)
	@OrderColumn(name = "ITEM_INDEX")
	@Cascade({ org.hibernate.annotations.CascadeType.ALL })
	protected List<Anx2InwardErrorItemEntity> lineItems = new ArrayList<>();

	public Integer getItemNoForIndex(Integer itemIndex) {

		int lineItemCount = this.lineItems.size();

		if (itemIndex < lineItemCount && itemIndex >= 0) {
			Anx2InwardErrorItemEntity lineItem = lineItems.get(itemIndex);
			try {
				return Integer.valueOf(lineItem.getLineNo());
			} catch (Exception e) {
				if (lineItem.getLineNo() == null
						|| lineItem.getLineNo().trim().isEmpty()) {

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("lineItem null or empty ");
					}
					//LOGGER.debug("Line item error " + e.getMessage());

					return null;
				} else {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("lineItem " + lineItem.getLineNo());
					}
					//LOGGER.debug("Line item error " + e.getMessage());
					return -1;
				}
			}
		}

		return null;
	}
}
