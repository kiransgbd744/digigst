package com.ey.advisory.app.services.strcutvalidation.gstr8;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
public class UserDefinedFieldValidationRule implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(int idx, Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();

		if (obj != null) {
			if (obj.toString().length() > 100) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.USER_ACCESS1);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER8026",
						"Invalid User defined field", location));
			}
		}
		return errors;
	}
}