package com.ey.advisory.app.services.strcutvalidation.ret1and1a;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GSTConstants.*;

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
 * @author Mahesh.Golla
 *
 */

public class Ret1And1AReturnTypeValidationRule implements ValidationRule {

	private static final List<String> RETURN_TYPES = ImmutableList.of("RET-1",
			"RET-1A");

	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();

		if (!isPresent(obj)) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.RETURN_TYPE);
			TransDocProcessingResultLoc location =
					new TransDocProcessingResultLoc(
					null, errorLocations.toArray());

			errors.add(new ProcessingResult(APP_VALIDATION, "ER1801",
					"Invalid Return Type.", location));
			return errors;
		}
			String returnType = obj.toString().trim();
			if (!RETURN_TYPES.contains(returnType) || returnType.length() > 6) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.RETURN_TYPE);
				TransDocProcessingResultLoc location = 
						new TransDocProcessingResultLoc(
						null, errorLocations.toArray());

				errors.add(new ProcessingResult(APP_VALIDATION, "ER1801",
						"Invalid Return Type.", location));
				return errors;
		}
		return errors;
	}
}
