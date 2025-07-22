package com.ey.advisory.app.services.strcutvalidation.table3h3i;

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
import com.google.common.collect.ImmutableList;

/**
 * 
 * @author Sujith.Nanga
 *
 */
public class TransactionFlagValidationRule implements ValidationRule {

	private static final List<String> IMPORTS = ImmutableList.of("RC", "IMPS");

	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();

		if (!isPresent(obj)) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.TRANSACTION_FLAG);
			TransDocProcessingResultLoc location = 
					new TransDocProcessingResultLoc(
					null, errorLocations.toArray());

			errors.add(new ProcessingResult(APP_VALIDATION, "ER1209",
					"Transaction Flag Cannot be left blank", location));
			return errors;
		}

		// First check if the input object is a valid decimal number.
		if (obj.toString().trim().length() > 4
				|| !IMPORTS.contains(obj.toString().trim())) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.TRANSACTION_FLAG);
			TransDocProcessingResultLoc location = 
					new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER01210",
					"Invalid Transaction Flag.", location));
			// if it's not a number, then we can return the errors
			// immediately
			return errors;
		}
		return errors;
	}

}
