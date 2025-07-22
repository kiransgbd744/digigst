/**
 * 
 */
package com.ey.advisory.vendor.master.api.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Shashikant.Shukla
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
public class VendorMasterApiTransRq { 
	@XmlTransient
	protected Long id;
	/* this is just added for soap request for vendor */
	
	@XmlElement(name = "recipient-pan")
	private String recipientPan;
	@XmlElement(name = "vendor-pan")
	private String vendorPAN;
	@XmlElement(name = "vendor-gstin")
	private String vendorGstin;
	@XmlElement(name = "supplier-code")
	private String supplierCode;
	@XmlElement(name = "vendor-name")
	private String vendorName;
	@XmlElement(name = "vend-prim-email-id")
	private String vendPrimEmailId;
	@XmlElement(name = "vendor-contact-number")
	private String vendorContactNumber;
	@XmlElement(name = "vendor-email-id1")
	protected String vendorEmailId1;
	@XmlElement(name = "vendor-email-id2")
	protected String vendorEmailId2;
	@XmlElement(name = "vendor-email-id3")
	protected String vendorEmailId3;
	@XmlElement(name = "vendor-email-id4")
	protected String vendorEmailId4;
	@XmlElement(name = "recipient-email-id1")
	protected String recipientEmailId1;
	@XmlElement(name = "recipient-email-id2")
	protected String recipientEmailId2;
	@XmlElement(name = "recipient-email-id3")
	protected String recipientEmailId3;
	@XmlElement(name = "recipient-email-id4")
	protected String recipientEmailId4;
	@XmlElement(name = "recipient-email-id5")
	protected String recipientEmailId5;	
	@XmlElement(name = "vendor-type")
	private String vendorType;
	@XmlElement(name = "hsn")
	private String hsn;
	@XmlElement(name = "vendor-risk-category")
	private String vendorRiskCategory;
	@XmlElement(name = "vendor-payment-terms")
	protected String vendorPaymentTerms;
	@XmlElement(name = "vendor-remarks")
	protected String vendorRemarks;
	@XmlElement(name = "approval-status")
	protected String approvalStatus;
	@XmlElement(name = "exclude-vendor-remarks")
	protected String excludeVendorRemarks;
	@XmlElement(name = "is-vendor-com")
	protected String isVendorCom;
	@XmlElement(name = "is-exclude-vendor")
	protected String isExcludeVendor;
	@XmlElement(name = "is-non-complaint-com")
	protected String isNonComplaintCom;
	@XmlElement(name = "is-credit-eligibility")
	protected String isCreditEligibility;
	@XmlElement(name = "is-delete")
	protected String isDelete;
}