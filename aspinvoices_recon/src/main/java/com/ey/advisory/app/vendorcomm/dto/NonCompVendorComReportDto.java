package com.ey.advisory.app.vendorcomm.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class NonCompVendorComReportDto {

	private List<GstinDto> vendorGstins;
	private String returnType;
	private String financialYear;
	private String entityId;
}
