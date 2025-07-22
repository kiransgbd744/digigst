package com.ey.advisory.app.services.asprecon.gstr2a.autorecon;

import java.util.List;

import com.ey.advisory.app.vendorcomm.dto.GstinDto;
import com.ey.advisory.app.vendorcomm.dto.ReportTypesDto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class AutoReconReportDownloadReqDto {

	private List<GstinDto> recipientGstins;
	private List<ReportTypesDto> reportTypes;
	private String fromTaxPeriod;
	private String toTaxPeriod;
	private String reconFromDate;
	private String reconToDate;
	private String entityId;
}
