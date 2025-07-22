package com.ey.advisory.app.services.strcutvalidation.interest;

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
 * @author Mahesh.Golla
 *
 */
public class InterestReturnPeriodValidationRule implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();

		if (!isPresent(obj)) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.RETURN_PERIOD);
			TransDocProcessingResultLoc location = 
					new TransDocProcessingResultLoc(
					null, errorLocations.toArray());

			errors.add(new ProcessingResult(APP_VALIDATION, "ER1845",
					"Return Period cannot be left blank.", location));
			return errors;
		}
		String taxPeriod = obj.toString().trim();
		if (!taxPeriod.matches("[0-9]+")) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.RETURN_PREIOD);
			TransDocProcessingResultLoc location = 
					new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER1843",
					"Invalid Return Period", location));
			return errors;
		} else if (taxPeriod.matches("[0-9]+")) {
			Set<String> errorLocations = new HashSet<>();
			if (taxPeriod.length() != 6
					|| (Integer.valueOf(taxPeriod.substring(0, 2)) > 12
							|| Integer.valueOf(taxPeriod.substring(0, 2)) < 01)
					|| (Integer.valueOf(taxPeriod.substring(2)) > 9999
							|| Integer
									.valueOf(taxPeriod.substring(2)) < 0000)) {
				errorLocations.add(GSTConstants.RETURN_PREIOD);
				TransDocProcessingResultLoc location = 
						new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER1843",
						"Invalid Return Period", location));
				return errors;
			}
		}
		return errors;
	}
}
