package com.ey.advisory.app.data.services.noncomplaintvendor;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Component
@ToString
public class NonComplaintVendorReportDto {

	private String vendorGSTIN;
	private String vendorName;
	private String sourceofGSTIN;
	private String taxPeriod;
	private String returnType;
	private LocalDate filingDate;
	private String arnNo;
	private String statusofReturnFiling;
	private String quarterlyorMonthlyfiler;
	private String errorfromGSTN;

}
