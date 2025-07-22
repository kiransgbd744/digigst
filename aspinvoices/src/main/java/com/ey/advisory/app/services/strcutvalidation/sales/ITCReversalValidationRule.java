package com.ey.advisory.app.services.strcutvalidation.sales;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.ey.advisory.app.services.strcutvalidation.*;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.google.common.collect.ImmutableList;

public class ITCReversalValidationRule implements ValidationRule {
	@Autowired
	private DataConverterFactory converterFactory;

	private static final List<String> VALID_COMMON_SUPPLY_INDICATORS = ImmutableList
			.of("T1", "T2", "T3", "T4");

	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row, TabularDataLayout layout) {

		/*if (!ValidatorUtil.isValuePresent(obj)) {
			return true;
		}
*/List<ProcessingResult> errors = new ArrayList<>();
Set<String> errorLocations = new HashSet<>();
errorLocations.add(GSTConstants.TAXABLE_VALUE);
TransDocProcessingResultLoc location 
                     = new TransDocProcessingResultLoc(
		null, errorLocations.toArray());
errors.add(new ProcessingResult(APP_VALIDATION, "ER075",
		"Invalid Taxable Value",
		location));
		Converter converter = converterFactory.getConverter(obj, String.class);
		if (converter == null) {
			return errors;
		}

		String itcReversal = (String) converter.convert(obj);
		/*if (VALID_COMMON_SUPPLY_INDICATORS
				.contains(itcReversal.toUpperCase())) {
			return true;
		}*/
		return errors;
	}

}
