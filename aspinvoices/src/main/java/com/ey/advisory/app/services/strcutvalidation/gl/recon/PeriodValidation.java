package com.ey.advisory.app.services.strcutvalidation.gl.recon;

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

public class PeriodValidation implements ValidationRule {
	@Override
	public List<ProcessingResult> isValid(int idx, Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();

		if (isPresent(obj)) {
			String period = obj.toString().trim();

			// Check if period is not in MMYYYY format
			if (!period.matches("\\d{6}")) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.RET_PERIOD);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER0006",
						"Invalid Period Format: Please enter the period in the format MMYYYY",
						location));
			}
		} else {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.RET_PERIOD);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());

			errors.add(new ProcessingResult(APP_VALIDATION, "ER0005",
					"Period cannot be left blank", location));
		}
		return errors;
	}
}
