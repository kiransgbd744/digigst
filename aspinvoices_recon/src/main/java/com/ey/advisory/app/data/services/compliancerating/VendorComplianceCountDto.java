package com.ey.advisory.app.data.services.compliancerating;

import java.util.List;

import lombok.Data;

@Data
public class VendorComplianceCountDto {

	private Integer ttlVendors;
	private Integer compliantVendors;
	private Integer nCompliantVendors;
	private Integer gstr1NcomVendors;
	private Integer gstr3BNcomVendors;
	private List<String> gstr1NonCompliantVgstins;
	private List<String> gstr3BNonCompliantVgstins;

}
