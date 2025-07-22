package com.ey.advisory.app.data.entities.client;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import jakarta.persistence.SequenceGenerator;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Jithendra Kumar B
 *
 */
@Setter
@Getter
@ToString
@Entity
@Table(name = "TBL_EINV_RECON_RESP_ERR")
public class Gstr1vsEinvReconRespErrorEntity {

	@Expose
	@SerializedName("id")
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "TBL_EINV_RECON_RESP_ERR_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	protected Long id;

	@Column(name = "RECON_REQUEST_ID")
	private Long reconRequestId;

	@Column(name = "USER_RESPONSE")
	private String userResponse;

	@Column(name = "PREV_RESPONSE")
	private String prevResponse;

	@Column(name = "RPT_TYPE")
	private String rptType;

	@Column(name = "REASON")
	private String reason;

	@Column(name = "EINV_STATUS")
	private String enivStatus;

	@Column(name = "AUTODRAFT_STATUS")
	private String autoDraftStatus;

	@Column(name = "AUTODRAFTED_DATE")
	private String autoDraftedDate;

	@Column(name = "ERROR_CODE")
	private String errorCode;

	@Column(name = "ERROR_MESSAGE")
	private String errorMessage;

	@Column(name = "SRC_TYP_OF_IRN")
	private String srcTypeOfIRN;

	@Column(name = "TABLE_TYPE_SR")
	private String tableTypeSR;

	@Column(name = "IRN_SR")
	private String irnSR;

	@Column(name = "IRN_EINV")
	private String irnEniv;

	@Column(name = "IRN_DATE_SR")
	private String irnDateSR;

	@Column(name = "IRN_DATE_EINV")
	private String irnDateEinv;

	@Column(name = "RET_PERIOD_SR")
	private String retPeriodSR;

	@Column(name = "RET_PERIOD_EINV")
	private String retPeriodEinv;

	@Column(name = "CAL_MONTH_SR")
	private String calMonthSR;

	@Column(name = "SGSTIN_SR")
	private String sgstinSR;

	@Column(name = "SGSTIN_EINV")
	private String sgstinEinv;

	@Column(name = "CGSTIN_SR")
	private String cgstinSR;

	@Column(name = "CUST_LEGAL_NAME")
	private String custLegalName;

	@Column(name = "CUST_TYPE")
	private String custType;

	@Column(name = "CUST_CODE")
	private String custCode;

	@Column(name = "CGSTIN_EINV")
	private String cgstinEinv;

	@Column(name = "DOC_TYPE_SR")
	private String docTypeSR;

	@Column(name = "DOC_TYPE_EINV")
	private String docTypeEinv;

	@Column(name = "SUPPLY_TYPE_SR")
	private String supplyTypeSR;

	@Column(name = "SUPPLY_TYPE_EINV")
	private String supplyTypeEinv;

	@Column(name = "DOC_NUM_SR")
	private String docNumSR;

	@Column(name = "DOC_NUM_EINV")
	private String docNumEinv;

	@Column(name = "DOC_DATE_SR")
	private String docDateSR;

	@Column(name = "DOC_DATE_EINV")
	private String docDateEinv;

	@Column(name = "ACCOUNTING_VOUCHER_NUM")
	private String accountingVoucherNum;

	@Column(name = "ACCOUNTING_VOUCHER_DATE")
	private String accountingVoucherDate;

	@Column(name = "BILLING_POS_SR")
	private String billingPosSR;

	@Column(name = "BILLING_POS_EINV")
	private String billingPosEinv;

	@Column(name = "PORT_CODE_SR")
	private String portCodeSR;

	@Column(name = "PORT_CODE_EINV")
	private String portCodeEinv;

	@Column(name = "SHIP_BILL_NUM_SR")
	private String shipBillNumSR;

	@Column(name = "SHIP_BILL_NUM_EINV")
	private String shipBillNumEinv;

	@Column(name = "SHIP_BILL_DATE_SR")
	private String shipBillDateSR;

	@Column(name = "SHIP_BILL_DATE_EINV")
	private String shipBillDateEinv;

	@Column(name = "RCHRG_FLAG_SR")
	private String rechrgFlagSR;

	@Column(name = "RCHRG_FLAG_EINV")
	private String rchrgFlagEinv;

	@Column(name = "TAXABLE_VALUE_SR")
	private String taxableValueSR;

	@Column(name = "TAXABLE_VALUE_EINV")
	private String taxableValueEinv;

	@Column(name = "IGST_AMT_SR")
	private String igstAmtSR;

	@Column(name = "IGST_AMT_EINV")
	private String igstAmtEinv;

	@Column(name = "CGST_AMT_SR")
	private String cgstAmtSR;

	@Column(name = "CGST_AMT_EINV")
	private String cgstAmtEinv;

	@Column(name = "SGST_AMT_SR")
	private String sgstAmtSR;

	@Column(name = "SGST_AMT_EINV")
	private String sgstAmtEinv;

	@Column(name = "CESS_AMT_SR")
	private String cessAmtSR;

	@Column(name = "CESS_AMT_EINV")
	private String cessAmtEinv;

	@Column(name = "INV_VAL_SR")
	private String invValSR;

	@Column(name = "INV_VAL_EINV")
	private String invValEinv;

	@Column(name = "DIFFPRCNT_FLAG")
	private String diffPrcntFlag;

	@Column(name = "SEC_7_OF_IGST_FLAG")
	private String sec7OfIgstFlag;

	@Column(name = "CLAIM_REFUND_FLAG")
	private String claimRefundFlag;

	@Column(name = "AUTO_POPULATE_TO_REFUND")
	private String autoPopulateToRefund;

	@Column(name = "COMPANY_CODE")
	private String companyCode;

	@Column(name = "EINV_GET_CALL_DATE")
	private String einvGetCallDate;

	@Column(name = "EINV_GET_CALL_TIME")
	private String einvGetCallTime;

	@Column(name = "RECON_ID")
	private String reconId;

	@Column(name = "RECON_DATE")
	private String reconDate;

	@Column(name = "RECON_TIME")
	private String reconTime;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "UPDATED_ON")
	private LocalDateTime updatedOn;

	@Column(name = "UPDATED_BY")
	private String updatedBy;

	@Column(name = "ERROR_ID")
	private String errorId;

	@Column(name = "ERROR_DESC")
	private String errorDesc;

	@Column(name = "DOC_HEADER_ID")
	private String docHeaderId;

	@Column(name = "GET_CALL_ID")
	private String getCallId;

	@Column(name = "FILE_ID")
	private String fileId;

	@Column(name = "DOC_KEY_SR")
	private String docKeySR;

	@Column(name = "DOC_KEY_EINV")
	private String docKeyEinv;

	@Column(name = "TABLE_TYPE_EINV")
	private String tableTypeEinv;

	@Column(name = "REPORT_CATEGORY")
	private String reportCategory;
	
	@Column(name = "REMARKS")
	private String remarks;
	
	@Column(name = "MISMATCH_REASON")
	private String mismatchReason;

	@Column(name = "SCORE")
	private String score;

	@Column(name = "RECIPIENT_NAME_EINV")
	private String recipientNameEinv;

	@Column(name = "ECOM_GSTIN_EINV")
	private String ecomGstinEinv;
	
	@Column(name = "ECOM_GSTIN_SR")
	private String ecomGstinSR;

	@Column(name = "RECORD_STATUS_DIGIGST")
	private String recordStatusDigiGst;

	@Column(name = "PREV_RPT_TYPE")
	private String prevRptTy;
	
	@Column(name = "TOTAL_TAX_EINV")
	private String totalTaxEinv;
	
	@Column(name = "TOTAL_TAX_SR")
	private String totalTaxSR;

	@Column(name = "SUBCATEGORY")
	private String subCategory;

	@Column(name = "REASON_FOR_MISMATCH")
	private String reasonForMismatch;
	
	//added 7 columns
	@Column(name = "PLANT_CODE")
	private String plantCode;
	
	@Column(name = "DIVISION")
	private String division;
	
	@Column(name = "SUBDIVISION")
	private String subDivision;
	
	@Column(name = "LOCATION")
	private String location;
	
	@Column(name = "PROFITCENTER1")
	private String profitCentre1;

	@Column(name = "PROFITCENTER2")
	private String profitCentre2;
	
	@Column(name = "PROFITCENTER3")
	private String profitCentre3;

}
