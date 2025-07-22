package com.ey.advisory.app.vendorcomm.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class NonCompVendorRequestDto {

	private Long requestId;
	private Long noOfVendorGstins;
	private String createdOn;
	private String status;
	private long totalEmails;
	private long sentEmails;
	private String financialYear;
	private List<GstinDto> vendorGstins;
}
