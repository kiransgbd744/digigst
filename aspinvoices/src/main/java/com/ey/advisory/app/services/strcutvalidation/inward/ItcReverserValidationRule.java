package com.ey.advisory.app.services.strcutvalidation.inward;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.services.strcutvalidation.outward.ValidationRule;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.google.common.collect.ImmutableList;

public class ItcReverserValidationRule implements ValidationRule {

	private static final List<String> IMPORTS = ImmutableList.of("T1", "T3",
			"t1", "t3","T2", "T4",
			"t2", "t4");

	@Override
	public List<ProcessingResult> isValid(int idx,Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (!isPresent(obj)) return errors;
			// First check if the input object is a valid decimal number.
			if (IMPORTS.contains(obj.toString().trim())) 	return errors;
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.ITC_REVERSAL_IDENTIFIER);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER1112",
						"Invalid ITC Reversal Indicator.", location));
				// if it's not a number, then we can return the errors
				// immediately
			
		return errors;
	}
}
