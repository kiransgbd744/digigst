package com.ey.advisory.app.data.services.noncomplaintvendor;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConfirmantRangeDto {

	private List<String> mandatoryTaxPeriods;
	
	private List<String> optionalTaxPeriods;
}
