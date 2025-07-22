package com.ey.advisory.app.services.strcutvalidation.table3h3i;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.Arrays;
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
 * @author Sujith.Nanga
 *
 */
public class ReturnTypeValidationRule implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {
		List<String> allowedReturnTypes = Arrays.asList("ANX-1", "ANX-1A");
		List<ProcessingResult> errors = new ArrayList<>();

		if (!isPresent(obj)) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.RETURN_TYPE);
			TransDocProcessingResultLoc location = 
					new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER1201",
					"Return Type cannot be left balnk", location));
			return errors;
		}

		// If the object is null, return the errors
			if (obj.toString().trim().length() > 6 ||
					!allowedReturnTypes.contains(obj.toString().trim())) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.RETURN_TYPE);
				TransDocProcessingResultLoc location = 
						new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER1202",
						"Invalid Return Type", location));
				return errors;
			}
		return errors;
	}

}
