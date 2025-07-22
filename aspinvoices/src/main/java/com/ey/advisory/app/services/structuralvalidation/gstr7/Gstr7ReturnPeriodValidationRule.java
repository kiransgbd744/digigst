package com.ey.advisory.app.services.structuralvalidation.gstr7;

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

public class Gstr7ReturnPeriodValidationRule implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(int idx, Object obj, Object[] row, TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (!isPresent(obj)) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.RETURN_PERIOD);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER2000", "Return Period cannot be left blank.", location));
			return errors;
		}

		if (obj != null) {
			String period = obj.toString().trim();
			int taxPeriod = period.length();
			if (taxPeriod > 6) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.RETURN_PERIOD);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER2001", "Invalid Return Period", location));
				return errors;
			} else if (taxPeriod < 6) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.RETURN_PERIOD);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER2001", "Invalid Return Period", location));
				return errors;
			}

			if (taxPeriod == 6) {
				int month = Integer.valueOf(period.substring(0, 2));
				int year = Integer.valueOf(period.substring(2));

				if ((month < 0 || month > 12) || (year < 0 || year > 9999)) {
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.RETURN_PERIOD);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(null,
							errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER2001", "Invalid Return Period", location));
					return errors;
				}

			}
		}

		String taxPeriod = obj.toString().trim();
		if (!taxPeriod.matches("[0-9]+") || taxPeriod.length() != 6) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.RETURN_PERIOD);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER2001", "Invalid Return Period", location));
			return errors;
		}
		int month = Integer.valueOf(taxPeriod.substring(0, 2));
		int year = Integer.valueOf(taxPeriod.substring(2));
		if (taxPeriod.length() != 6 || month > 12 || month == 00 || year > 9999 || year < 0000) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.RETURN_PERIOD);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER2000", "Invalid Return Period", location));
			return errors;
		}
		return errors;
	}

}
