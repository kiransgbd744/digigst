package com.ey.advisory.vendor.master.api.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * An Outward Document represents a concrete Financial Document like an Invoice
 * or a credit note.
 * 
 * @author Shashikant.Shukla
 *
 */

@Getter
@Setter
@ToString
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class VendorMasterApiLineItemReq {
	@Expose
	@SerializedName("recipientPan")
	@XmlElement(name = "recipient-pan")
	private String recipientPan;

	@Expose
	@SerializedName("vendorPAN")
	@XmlElement(name = "vendor-pan")
	private String vendorPAN;

	@Expose
	@SerializedName("vendorGstin")
	@XmlElement(name = "vendor-gstin")
	private String vendorGstin;

	@Expose
	@SerializedName("supplierCode")
	@XmlElement(name = "supplier-code")
	private String supplierCode;

	@Expose
	@SerializedName("vendorName")
	@XmlElement(name = "vendor-name")
	private String vendorName;

	@Expose
	@SerializedName("vendPrimEmailId")
	@XmlElement(name = "vend-prim-email-id")
	private String vendPrimEmailId;

	@Expose
	@SerializedName("vendorContactNumber")
	@XmlElement(name = "vendor-contact-number")
	private String vendorContactNumber;

	@Expose
	@SerializedName("vendorEmailId1")
	@XmlElement(name = "vendor-email-id1")
	private String vendorEmailId1;

	@Expose
	@SerializedName("vendorEmailId2")
	@XmlElement(name = "vendor-email-id2")
	private String vendorEmailId2;

	@Expose
	@SerializedName("vendorEmailId3")
	@XmlElement(name = "vendor-email-id3")
	private String vendorEmailId3;

	@Expose
	@SerializedName("vendorEmailId4")
	@XmlElement(name = "vendor-email-id4")
	private String vendorEmailId4;

	@Expose
	@SerializedName("recipientEmailId1")
	@XmlElement(name = "recipient-email-id1")
	private String recipientEmailId1;

	@Expose
	@SerializedName("recipientEmailId2")
	@XmlElement(name = "recipient-email-id2")
	private String recipientEmailId2;

	@Expose
	@SerializedName("recipientEmailId3")
	@XmlElement(name = "recipient-email-id3")
	private String recipientEmailId3;

	@Expose
	@SerializedName("recipientEmailId4")
	@XmlElement(name = "recipient-email-id4")
	private String recipientEmailId4;

	@Expose
	@SerializedName("recipientEmailId5")
	@XmlElement(name = "recipient-email-id5")
	private String recipientEmailId5;

	@Expose
	@SerializedName("vendorType")
	@XmlElement(name = "vendor-type")
	private String vendorType;

	@Expose
	@SerializedName("hsn")
	@XmlElement(name = "hsn")
	private String hsn;

	@Expose
	@SerializedName("vendorRiskCategory")
	@XmlElement(name = "vendor-risk-category")
	private String vendorRiskCategory;

	@Expose
	@SerializedName("vendorPaymentTerms")
	@XmlElement(name = "vendor-payment-terms")
	private String vendorPaymentTerms;

	@Expose
	@SerializedName("vendorRemarks")
	@XmlElement(name = "vendor-remarks")
	private String vendorRemarks;

	@Expose
	@SerializedName("approvalStatus")
	@XmlElement(name = "approval-status")
	private String approvalStatus;

	@Expose
	@SerializedName("excludeVendorRemarks")
	@XmlElement(name = "exclude-vendor-remarks")
	private String excludeVendorRemarks;

	@Expose
	@SerializedName("isVendorCom")
	@XmlElement(name = "is-vendor-com")
	private String isVendorCom;

	@Expose
	@SerializedName("isExcludeVendor")
	@XmlElement(name = "is-exclude-vendor")
	private String isExcludeVendor;

	@Expose
	@SerializedName("isNonComplaintCom")
	@XmlElement(name = "is-non-complaint-com")
	private String isNonComplaintCom;

	@Expose
	@SerializedName("isCreditEligibility")
	@XmlElement(name = "is-credit-eligibility")
	private String isCreditEligibility;

	@Expose
	@SerializedName("isDelete")
	@XmlElement(name = "is-delete")
	private String isDelete;
}
