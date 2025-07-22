package com.ey.advisory.app.services.structuralvalidation.gstr9;

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

/**
 * 
 * @author Anand3.M
 *
 */
public class Gstr9HsnValidationRule implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (!isPresent(obj)) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.HSN);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());

			errors.add(new ProcessingResult(APP_VALIDATION, "ER6172",
					"HSN cannot be left blank", location));
			return errors;
		}

		if (isPresent(obj)) {
			String hsnOrSac = obj.toString().trim();
			int length = hsnOrSac.length();

			if (length < 4 || length > 8) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.HSN);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER6173",
						"Invalid HSN", location));
				return errors;
			}
		} else {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.HSN);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER6173",
					"Invalid HSN", location));
			return errors;
		}

		return errors;
	}

	public static void main(String[] args) {
		int length = 10;

		if (length < 4 || length > 8) {
			System.out.println("length::" + length);
		}

	}
}
