package com.ey.advisory.app.itcmatching.vendorupload;

import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Rajesh N K
 * @modified by Malatesh.R.A
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TBL_VENDOR_MASTER_ERROR")
public class VendorMasterErrorReportEntity {

	@Expose
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "TBL_VENDOR_MASTER_ERROR_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "VENDOR_ERROR_ID")
	private Long id;

	@Column(name = "FILE_ID")
	private Long fileId;

	@Column(name = "ERROR_DESC")
	private String error;

	@Column(name = "INFORMATION_DESC")
	private String information;

	@Column(name = "ERROR_TYPE_DESC")
	private ErrorTypeDescription errorTypeDeascription;

	@Column(name = "INVOICE_KEY")
	private String invoiceKey;

	@Column(name = "RECIPIENT_PAN")
	private String recipientPAN;

	@Column(name = "VENDOR_PAN")
	private String vendorPAN;

	@Column(name = "VENDOR_GSTIN")
	private String vendorGstin;

	@Column(name = "VENDOR_CODE")
	private String vendorCode;

	@Column(name = "VENDOR_NAME")
	private String vendorName;

	@Column(name = "VENDOR_PRIMARY_EMAIL_ID")
	private String vendPrimEmailId;

	@Column(name = "VENDOR_PRIMARY_CONTACT_NUMBER")
	private String vendorContactNumber;

	@Column(name = "VENDOR_EMAIL_ID_1")
	private String vendorEmailId1;

	@Column(name = "VENDOR_EMAIL_ID_2")
	private String vendorEmailId2;

	@Column(name = "VENDOR_EMAIL_ID_3")
	private String vendorEmailId3;

	@Column(name = "VENDOR_EMAIL_ID_4")
	private String vendorEmailId4;

	@Column(name = "RECIPIENT_EMAIL_ID_1")
	private String recipientEmailId1;

	@Column(name = "RECIPIENT_EMAIL_ID_2")
	private String recipientEmailId2;

	@Column(name = "RECIPIENT_EMAIL_ID_3")
	private String recipientEmailId3;

	@Column(name = "RECIPIENT_EMAIL_ID_4")
	private String recipientEmailId4;

	@Column(name = "RECIPIENT_EMAIL_ID_5")
	private String recipientEmailId5;

	@Column(name = "ACTION")
	private String isDelete;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	@Column(name = "VENDOR_TYPE")
	private String vendorType;
	
	@Column(name = "HSN")
	private String hsn;
	
	@Column(name = "VENDOR_RISK_CATEGORY")
	private String vendorRiskCategory;
	
	@Column(name = "VENDOR_PAYMENT_TERMS")
	private String vendorPaymentTerms;
	
	@Column(name = "VENDOR_REMARKS")
	private String vendorRemarks;
	
	@Column(name = "APPROVAL_STATUS")
	private String approvalStatus;
	
	@Column(name = "EXCLUDE_VENDOR_REMARKS")
	private String excludeVendorRemarks;
	
	@Column(name = "IS_VENDOR_COM")
	private String isVendorCom;
	
	@Column(name = "IS_EXCLUDE_VENDOR")
	private String isExcludeVendor;
	
	@Column(name = "IS_NON_COMPLAINT_COM")
	private String isNonComplaintCom;
	
	@Column(name = "IS_CREDIT_ELIGIBILITY")
	private String isCreditEligibility;
	
	@Column(name = "REF_ID")
	private String refId;
	
	@Column(name = "PAYLOAD_ID")
	private String payloadId;
}
