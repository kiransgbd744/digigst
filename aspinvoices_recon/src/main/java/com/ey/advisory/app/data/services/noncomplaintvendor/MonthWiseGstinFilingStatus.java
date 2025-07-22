package com.ey.advisory.app.data.services.noncomplaintvendor;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Component
public class MonthWiseGstinFilingStatus {

	private String month;
	private String status;
}
