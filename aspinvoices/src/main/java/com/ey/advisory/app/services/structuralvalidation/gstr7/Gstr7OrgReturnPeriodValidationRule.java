package com.ey.advisory.app.services.structuralvalidation.gstr7;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.services.strcutvalidation.outward.ValidationRule;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

/**
 * 
 * @author Anand3.M
 *
 */
public class Gstr7OrgReturnPeriodValidationRule implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(int idx,Object obj, Object[] row,
			TabularDataLayout layout) {
List<ProcessingResult> errors = new ArrayList<>();
if (!isPresent(obj)) {

	return errors;
}

			String taxPeriod = obj.toString().trim();
			if (!taxPeriod.matches("[0-9]+") || taxPeriod.length() != 6) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.ORG_RETURN_PERIOD);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER2007",
						"Invalid Original Return Period", location));
				return errors;
			} 
			if (taxPeriod.matches("[0-9]+")) {

				int month = Integer.valueOf(taxPeriod.substring(0, 2));
				int year = Integer.valueOf(taxPeriod.substring(2));
				if (taxPeriod.length() != 6 || month > 12 || month == 00 || 
						year > 9999 || year < 0000) {
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.ORG_RETURN_PERIOD);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER2007",
							"Invalid Original Return Period", location));
					return errors;
				}
			}
		
		return errors;
	}

}






