package com.ey.advisory.app.docs.dto.erp;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GetFilingFrequencyDto {
	private String gstin;
	private String financialYear;
	private String filingType;
	private String quarter;
}
