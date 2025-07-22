package com.ey.advisory.app.itcmatching.vendorupload;

import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class VendorMasterUploadEntityDto extends VendorMasterUploadEntity {

	private String vendorCom;

	private String excludeVendor;

	private String nonComplaintCom;
	
	private String creditEligibility;
}
