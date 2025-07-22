package com.ey.advisory.app.services.asprecon.gstr2a.autorecon;

import java.util.List;

import com.ey.advisory.app.vendorcomm.dto.GstinDto;
import com.ey.advisory.app.vendorcomm.dto.ReportTypesDto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AutoReconReqListingDto {

	private Long requestId;
	private Long noOfRecipientGstins;
	private Long noOfReportTypes;
	private String fromTaxPeriod;
	private String toTaxPeriod;
	private String createdOn;
	private String status;
	private List<ReportTypesDto> reportTypes;
	private List<GstinDto> recipientGstins;
}
