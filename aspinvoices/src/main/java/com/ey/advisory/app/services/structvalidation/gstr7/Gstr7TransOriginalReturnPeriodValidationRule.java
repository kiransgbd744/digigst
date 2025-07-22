package com.ey.advisory.app.services.structvalidation.gstr7;

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
import com.google.common.base.Strings;

/**
 * 
 * @author Siva.Reddy
 *
 */
public class Gstr7TransOriginalReturnPeriodValidationRule
		implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(int idx, Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();

		if (!isPresent(obj))
			return errors;

		String taxPeriod = obj != null ? obj.toString().trim() : "";

		if (!Strings.isNullOrEmpty(taxPeriod) && taxPeriod.matches("[0-9]+")) {

			int month = Integer.valueOf(taxPeriod.substring(0, 2));
			int year = Integer.valueOf(taxPeriod.substring(2));
			if (taxPeriod.length() != 6 || month > 12 || month == 00
					|| year > 9999 || year < 0000) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.ORG_RETURN_PERIOD);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER63010",
						"Invalid Original Return Period", location));
				return errors;
			}
		}

		return errors;
	}

}
