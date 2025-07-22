package com.ey.advisory.app.services.strcutvalidation.vendor;

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

public class LegalNameValidationRule implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(int idx, Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();

		Object s = row[5];
		if (!isPresent(s) || s.toString().equalsIgnoreCase(GSTConstants.N)) {
			if (!isPresent(obj)) {
				Set<String> errorLocations = new HashSet<>();
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errorLocations.add(GSTConstants.LEGALNAME);
				errors.add(new ProcessingResult(APP_VALIDATION, "ER1630",
						"Legal Name cannot be left blank.", location));
				return errors;
			}
		}

		return errors;

	}

}
