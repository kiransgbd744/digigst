package com.ey.advisory.app.services.strcutvalidation.gstr3b;

import static com.ey.advisory.common.FormatValidationUtil.isDecimal;
import static com.ey.advisory.common.FormatValidationUtil.isPresent;
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

public class Gstr3bSerialNumberPeriodValidationRule implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();

		if (!isPresent(obj)) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.SERIAL_NUMBER);
			TransDocProcessingResultLoc location = 
					new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER6105",
					"Invalid Serial No.", location));
			return errors;
		}
		String returnType = obj.toString().trim();
		if (returnType.length() > 4) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.SERIAL_NUMBER);
			TransDocProcessingResultLoc location = 
					new TransDocProcessingResultLoc(
					null, errorLocations.toArray());

			errors.add(new ProcessingResult(APP_VALIDATION, "ER6105",
					"Invalid Serial No.", location));
			return errors;
		}
		if (!isDecimal(obj.toString().trim())) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.SERIAL_NUMBER);
			TransDocProcessingResultLoc location = 
					new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER6105",
					"Invalid Serial No.", location));
			// if it's not a number, then we can return the errors
			// immediately
			return errors;
		}

		Integer result = new Integer(obj.toString().trim());
		Set<String> errorLocations = new HashSet<>();
		if (result < 1 || result > 10) {
			errorLocations.add(GSTConstants.SERIAL_NUMBER);
			TransDocProcessingResultLoc location = 
					new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER6105",
					"Invalid Serial No.", location));
			// if it's not a number, then we can return the errors
			// immediately
			return errors;
		}
		return errors;
	}
}
