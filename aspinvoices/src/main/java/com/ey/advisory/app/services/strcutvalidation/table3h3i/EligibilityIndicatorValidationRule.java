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
public class EligibilityIndicatorValidationRule implements ValidationRule {

	private static final List<String> IMPORTS = ImmutableList.of("CG", "IG",
			"IS", "NO");

	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		
		if (!isPresent(obj))
			return errors;

			// First check if the input object is a valid decimal number.
			if (!IMPORTS.contains(obj.toString().trim()) || 
					obj.toString().trim().length() > 2) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.ELIGIBILITY_INDICATOR);
				TransDocProcessingResultLoc location =
						new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER1227",
						"Invalid EligibilityIndicator", location));
				return errors;
		}
		return errors;

	}

}
