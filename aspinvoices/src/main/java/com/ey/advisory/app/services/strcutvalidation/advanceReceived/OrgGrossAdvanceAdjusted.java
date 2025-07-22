package com.ey.advisory.app.services.strcutvalidation.advanceReceived;

import static com.ey.advisory.common.FormatValidationUtil.isDecimal;
import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.services.strcutvalidation.sales.ValidationRule;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

public class OrgGrossAdvanceAdjusted implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();

		if (obj != null) {
			if (!isDecimal(obj)) {
				errorLocations.add(GSTConstants.ORG_GROSS_ADVANCE_RECEIVED);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER5162",
						"Invalid OrgTaxable Value", location));
				// if it's not a number, then we can return the errors
				// immediately
				return errors;
			}
			BigDecimal result = new BigDecimal(obj.toString());
			if (result.compareTo(BigDecimal.ZERO) < 0) {
				errorLocations.add(GSTConstants.ORGTAXABLE_VALUE);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER5162",
						"Invalid OrgTaxable Value", location));
				return errors;
			}
		}
		return errors;
	}

}
