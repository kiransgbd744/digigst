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
public class ReturnPeriodValidationRule implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(int idx,Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();

		if (!isPresent(obj)) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.RETURN_PREIOD);
			TransDocProcessingResultLoc location 
			                 = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER0020",
					"Return Period cannot be left blank.", location));
			return errors;
		}

		String taxPeriod = obj.toString().trim();
		if (!taxPeriod.matches("[0-9]+") || taxPeriod.length() != 6) {
			errors = createValErrors(errors);
			return errors;
		}
		if (taxPeriod.matches("[0-9]+")) {

			int month = Integer.valueOf(taxPeriod.substring(0, 2));
			if (taxPeriod.length() != 6 || month > 12 || month == 00) {
				errors = createValErrors(errors);
				return errors;
			}
		}

		return errors;
	}
	
	private List<ProcessingResult> createValErrors(
					List<ProcessingResult> errors) {
		Set<String> errorLocations = new HashSet<>();
		errorLocations.add(GSTConstants.RETURN_PREIOD);
		TransDocProcessingResultLoc location 
		                    = new TransDocProcessingResultLoc(
				null, errorLocations.toArray());
		errors.add(new ProcessingResult(APP_VALIDATION, "ER0021",
				"Invalid Return Period", location));
		return errors;
	}
}
