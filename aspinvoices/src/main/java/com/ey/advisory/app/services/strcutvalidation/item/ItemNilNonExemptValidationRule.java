package com.ey.advisory.app.services.strcutvalidation.item;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.services.strcutvalidation.sales.ValidationRule;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

public class ItemNilNonExemptValidationRule implements ValidationRule {
	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (obj != null
				&& !Arrays.asList("NIL", "NON", "EXT").contains(obj)) {

			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.NILNONEXMPT);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER1643",
					"Invalid NIL/NON/Exempt", location));
			return errors;
		}

		return errors;
	}

}
