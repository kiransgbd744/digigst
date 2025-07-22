package com.ey.advisory.app.services.strcutvalidation.advanceReceived;

import static com.ey.advisory.common.FormatValidationUtil.isDecimal;
import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.services.strcutvalidation.sales.ValidationRule;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.NumberFomatUtil;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

public class AROrgRateValidationRule implements ValidationRule {
	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		if (!isPresent(obj))
			return errors;

		if (!isDecimal(obj.toString().trim())) {
			errorLocations.add(GSTConstants.OrgRate);
			TransDocProcessingResultLoc location = 
					new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER5109",
					"Invalid Org Rate", location));
			return errors;
		}

		boolean isValid = NumberFomatUtil.isValidDec(obj.toString().trim());
		BigDecimal number = new BigDecimal(obj.toString().trim());
		if (!isValid || number.compareTo(BigDecimal.ZERO) < 0) {
			errorLocations.add(GSTConstants.OrgRate);
			TransDocProcessingResultLoc location = 
					new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER5109",
					"Invalid Org Rate", location));
			return errors;
		}
		return errors;
	}
}
