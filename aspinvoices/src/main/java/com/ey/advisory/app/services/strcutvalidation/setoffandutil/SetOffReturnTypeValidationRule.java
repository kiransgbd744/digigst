package com.ey.advisory.app.services.strcutvalidation.setoffandutil;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.RETURN_TYPE;

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
public class SetOffReturnTypeValidationRule implements ValidationRule {
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
			errors.add(new ProcessingResult(APP_VALIDATION, "ER1854",
					"Return Type cannot be left balnk", location));
			return errors;
		}
		String returnType = obj.toString().trim();
		if (returnType.length() > 6 || !RETURN_TYPES.contains(returnType)) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(RETURN_TYPE);
			TransDocProcessingResultLoc location = 
					new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, " ER1854",
					"Invalid Return Type.", location));
			return errors;
		}
		return errors;
	}

}
