package com.ey.advisory.app.data.services.noncomplaintvendor;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
public class OverAllFilingStatusWithFinancialYearInfo {

	private String finanicalYear;
	private String status;
	private String modifedOn;
	private List<OverallFilingStatusDto> overallFilingStatusDtos;
	private String retFrequencyStatus;
	private String retFrequencyTime;
}
