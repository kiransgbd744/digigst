package com.ey.advisory.app.services.strcutvalidation.advanceAdjusted;

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

public class TxpdMonthValidationRule implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();

		if (!isPresent(obj))
			return errors;

		String dateMonth = obj.toString().trim();
		if (!dateMonth.matches("[0-9]+")) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.MONTH);
			TransDocProcessingResultLoc location = 
					new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER5155",
					"Invalid Month", location));
			return errors;
		} else {
			if (dateMonth.length() != 6
					|| (Integer.valueOf(dateMonth.substring(0, 2)) > 12
							|| Integer.valueOf(dateMonth.substring(0, 2)) < 01)
					|| (Integer.valueOf(dateMonth.substring(2)) > 9999
							|| Integer
									.valueOf(dateMonth.substring(2)) < 0000)) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.MONTH);
				TransDocProcessingResultLoc location = 
						new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER5155",
						"Invalid Month", location));
				return errors;
			}
		}
		return errors;
	}
}
