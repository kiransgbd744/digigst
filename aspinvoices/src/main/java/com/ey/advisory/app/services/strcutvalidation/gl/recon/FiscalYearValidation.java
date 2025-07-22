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

public class FiscalYearValidation implements ValidationRule {
	@Override
	public List<ProcessingResult> isValid(int idx, Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();

		if (isPresent(obj)) {
			String fiscalYear = obj.toString().trim();

			// Check if fiscal year is not in YYYY format
			if (!fiscalYear.matches("\\d{4}")) {

				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.FISCAL_YEAR);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());

				errors.add(new ProcessingResult(APP_VALIDATION, "ER0004",
						"Please enter the Fiscal Year in YYYY format", location));
				return errors;
			}
		}

		return errors;

	}

}
