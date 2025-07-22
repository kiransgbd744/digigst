package com.ey.advisory.app.services.strcutvalidation.gstr8;

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

public class IdentifierValidationRule implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(int idx, Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (!isPresent(obj))
			return errors;

		if (obj.toString().trim().length() > 100) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.IDENTIFIER);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER8003",
					"Invalid Identifier", location));
			return errors;
		}

		String identifier = obj.toString().trim();
		if (!identifier.matches("[a-zA-Z0-9 ]*")) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.IDENTIFIER);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER8003",
					"Invalid Identifier", location));
			return errors;
		}

		return errors;
	}
}
