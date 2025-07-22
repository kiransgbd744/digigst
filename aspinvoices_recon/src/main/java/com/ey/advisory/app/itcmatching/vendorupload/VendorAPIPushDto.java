package com.ey.advisory.app.itcmatching.vendorupload;

import com.google.gson.annotations.Expose;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
public class VendorAPIPushDto{
	
	@Expose
	private String error;
	
	@Expose
	private String refId;
	
	@Expose
	private String recipientPAN;

	@Expose
	private String vendorGstin;

	@Expose
	private String vendorPAN;

	@Expose
	private String SupplierCode;

	@Expose
	private String vendorName;

	@Expose
	private String vendPrimEmailId;

	@Expose
	private String vendorContactNumber;

	@Expose
	private String vendorEmailId1;

	@Expose
	private String vendorEmailId2;

	@Expose
	private String vendorEmailId3;

	@Expose
	private String vendorEmailId4;

	@Expose
	private String recipientEmailId1;

	@Expose
	private String recipientEmailId2;

	@Expose
	private String recipientEmailId3;

	@Expose
	private String recipientEmailId4;

	@Expose
	private String recipientEmailId5;

	@Expose
	private String vendorType;

	@Expose
	private String hsn;

	@Expose
	private String vendorRiskCategory;

	@Expose
	private String vendorPaymentTerms;

	@Expose
	private String vendorRemarks;

	@Expose
	private String approvalStatus;

	@Expose
	private String excludeVendorRemarks;

	@Expose
	private String isVendorCom;

	@Expose
	private String isExcludeVendor;

	@Expose
	private String isNonComplaintCom;

	@Expose
	private String isCreditEligibility;

	@Expose
	private String isDelete;

}