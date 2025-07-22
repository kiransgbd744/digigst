package com.ey.advisory.app.services.strcutvalidation.outward;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.services.strcutvalidation.sales.ValidationRule;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

public class UomValidationRule implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		if (obj != null) {

			/*
			 * String uom = obj.toString(); String regex = "^[a-zA-Z0-9-/]*$";
			 * 
			 * Pattern pattern = Pattern.compile(regex);
			 */

			// Matcher matcher = pattern.matcher(uom);
			if (/* !matcher.matches() || */obj.toString().length() > 30
					|| obj.toString().contains(" ")) {
				errorLocations.add(GSTConstants.UOM);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER073",
						"Invalid Unit of Measurement", location));

				return errors;
			}
		}
		return errors;
	}

}
