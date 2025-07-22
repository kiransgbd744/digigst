package com.ey.advisory.app.data.services.noncomplaintvendor;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Component
@Getter
@Setter
@ToString
public class GstinWiseFilingStatus {
	private String gstin;
	private List<MonthWiseGstinFilingStatus> eachGstinwiseStatusCombination;
	private String filingType;

}
