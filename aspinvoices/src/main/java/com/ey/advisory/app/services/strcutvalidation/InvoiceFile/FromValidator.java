package com.ey.advisory.app.services.strcutvalidation.InvoiceFile;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.services.strcutvalidation.sales.ValidationRule;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

/**
 * Mahesh Golla
 */
public class FromValidator implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (!isPresent(obj)) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.FROM);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER5208",
					"Invalid input in From column.", location));
			return errors;
		}
		String from = obj.toString().trim();
		LocalDate parseObjToDate = DateUtil.parseObjToDate(from);
		if (parseObjToDate != null) {
			String fromm = String.valueOf(parseObjToDate);
			if (fromm.length() > 16) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.FROM);
				TransDocProcessingResultLoc location =
						new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER5208",
						"Invalid input in From column.", location));
				return errors;
			}
		} else {
			if (from.trim().length() > 16) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.FROM);
				TransDocProcessingResultLoc location =
						new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER5208",
						"Invalid input in From column.", location));
				return errors;
			}

		}
		if (from.contains(" ")) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.FROM);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER5208",
					"Invalid input in From column.", location));
			return errors;
		}
		return errors;
	}
}
