package com.ey.advisory.app.services.strcutvalidation.einvoice;

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

public class TotalInvValueValidation implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(int idx, Object obj, Object[] row,
			TabularDataLayout layout) {
List<ProcessingResult> errors = new ArrayList<>();
		
		if (!isPresent(obj)) {
			return errors;
		}
		if (obj.toString().trim().length() > 100) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.TOTAL_INV_VALUE);
			TransDocProcessingResultLoc location = 
					new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER15095",
					             "Invalid Total Invoice Value", location));
			return errors;
		}
		return errors;
	}

}
