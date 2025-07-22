package com.ey.advisory.app.itcmatching.vendorupload;

import lombok.Data;

@Data
public class VendorMasterReportDto {

	private String recipientPAN;

	private String vendorGstin;

	private String vendorPAN;

	private String vendorCode;

	private String vendorName;

	private String vendPrimEmailId;

	private String vendorContactNumber;

	private String vendorEmailId1;

	private String vendorEmailId2;

	private String vendorEmailId3;

	private String vendorEmailId4;

	private String recipientEmailId1;

	private String recipientEmailId2;

	private String recipientEmailId3;

	private String recipientEmailId4;

	private String recipientEmailId5;

	private String vendorType;

	private Integer hsn;

	private String vendorRiskCategory;

	private Integer vendorPaymentTerms;

	private String vendorRemarks;

	private String approvalStatus;

	private String excludeVendorRemarks;

	private String isVendorCom;

	private String isExcludeVendor;

	private String isNonComplaintCom;

	private String isCreditEligibility;

	private String isDelete;

}