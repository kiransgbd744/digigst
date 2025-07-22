package com.ey.advisory.app.data.services.compliancerating;

import java.util.List;

import lombok.Data;

/**
 * @author Saif.S
 *
 */
@Data
public class VendorComplianceRatingRequestDto {

	private Long entityId;
	private String source;
	private String fy;
	private String returnType;
	private String viewType;
	private String reportType;
	private List<String> vendorGstins;
	private List<String> vendorPans;
}
