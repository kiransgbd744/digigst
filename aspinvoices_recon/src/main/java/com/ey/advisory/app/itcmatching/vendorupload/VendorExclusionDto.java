package com.ey.advisory.app.itcmatching.vendorupload;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author Saif.S
 *
 */
@Getter
@Setter
public class VendorExclusionDto {
	
	private String vendorGstin;
	private String vendorName;
	private String excludeVendorRemarks;
}
