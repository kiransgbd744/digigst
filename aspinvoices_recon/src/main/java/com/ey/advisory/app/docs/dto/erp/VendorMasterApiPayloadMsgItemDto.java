package com.ey.advisory.app.docs.dto.erp;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class VendorMasterApiPayloadMsgItemDto {

	@XmlElement(name = "RECIPIENT_PAN")
	private String recipientPAN;

	@XmlElement(name = "VENDOR_PAN")
	private String vendorPAN;

	@XmlElement(name = "VENDOR_GSTIN")
	private String vendorGstin;

	@XmlElement(name = "SUPPLIER_CODE")
	private String supplierCode;

	@XmlElement(name = "VENDOR_NAME")
	private String vendorName;

	@XmlElement(name = "VENDPRIMEMAILID")
	private String vendPrimEmailId;

	@XmlElement(name = "VENDORCONTACTNO")
	private String vendorContactNumber;

	@XmlElement(name = "VENDOREMAILID1")
	private String vendorEmailId1;

	@XmlElement(name = "VENDOREMAILID2")
	private String vendorEmailId2;

	@XmlElement(name = "VENDOREMAILID3")
	private String vendorEmailId3;

	@XmlElement(name = "VENDOREMAILID4")
	private String vendorEmailId4;

	@XmlElement(name = "RECIPIENTEMID1")
	private String recipientEmailId1;

	@XmlElement(name = "RECIPIENTEMID2")
	private String recipientEmailId2;

	@XmlElement(name = "RECIPIENTEMID3")
	private String recipientEmailId3;

	@XmlElement(name = "RECIPIENTEMID4")
	private String recipientEmailId4;

	@XmlElement(name = "RECIPIENTEMID5")
	private String recipientEmailId5;

	@XmlElement(name = "VENDOR_TYPE")
	private String vendorType;

	@XmlElement(name = "HSN")
	private String hsn;

	@XmlElement(name = "VENRISKCATEGORY")
	private String vendorRiskCategory;

	@XmlElement(name = "VENPAYMENTTERMS")
	private String vendorPaymentTerms;

	@XmlElement(name = "VENDOR_REMARKS")
	private String vendorRemarks;

	@XmlElement(name = "APPROVAL_STATUS")
	private String approvalStatus;

	@XmlElement(name = "EXCLUDVENREMARK")
	private String excludeVendorRemarks;

	@XmlElement(name = "IS_VENDOR_COM")
	private String isVendorCom;

	@XmlElement(name = "ISEXCLUDEVENDOR")
	private String isExcludeVendor;

	@XmlElement(name = "ISNCOMPLAINTCOM")
	private String isNonComplaintCom;

	@XmlElement(name = "ISCRELIGIBILITY")
	private String isCreditEligibility;

	@XmlElement(name = "IS_DELETE")
	private String isDelete;

	@XmlElement(name = "ERRMSG")
	private String errorMsg;
	
	@XmlElement(name = "MSG")
	private String msg;
}
