package com.ey.advisory.app.services.strcutvalidation.outward;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.google.common.collect.ImmutableList;

public class TCSFlagValidationRule implements ValidationRule {

	private static final List<String> IMPORTS = ImmutableList.of("Y", "N", "E","O");

	@Override
	public List<ProcessingResult> isValid(int idx, Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();

		if (!isPresent(obj))
			return errors;
		// First check if the input object is a valid decimal number.
		if (!IMPORTS.contains(obj.toString().trim().toUpperCase())) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.TCSFlag);
			TransDocProcessingResultLoc location = 
					new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER0096",
					"Invalid TCS Flag.", location));
			// if it's not a number, then we can return the errors
			// immediately
			return errors;
		}

		return errors;
	}
}
