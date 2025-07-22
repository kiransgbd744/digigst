package com.ey.advisory.app.services.strcutvalidation.sales;

import static com.ey.advisory.common.GSTConstants.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

public class TaxPeriodValidationRule implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();

		if (obj == null) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.RETURN_PREIOD);
			TransDocProcessingResultLoc location = 
					new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER051",
					"Invalid Return Period", location));
		}
		if (obj != null) {

			String taxPeriod = obj.toString();
			if (!taxPeriod.matches("[0-9]+")) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.RETURN_PREIOD);
				TransDocProcessingResultLoc location = 
						new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER051",
						"Invalid Return Period", location));
			} else if (taxPeriod.matches("[0-9]+")) {

				int month = Integer.valueOf(taxPeriod.substring(0, 2));
				if (taxPeriod.length() != 6 || month > 12) {
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.RETURN_PREIOD);
					TransDocProcessingResultLoc location = 
							new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER051",
							"Invalid Return Period", location));
				}
			}
		}
		return errors;
	}

}
