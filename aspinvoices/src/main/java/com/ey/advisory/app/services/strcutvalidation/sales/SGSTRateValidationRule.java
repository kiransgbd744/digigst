package com.ey.advisory.app.services.strcutvalidation.sales;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.ey.advisory.common.FormatValidationUtil.*;
import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

public class SGSTRateValidationRule implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {

		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
	
		// If the object is null, return the empty errors (as this is a 
		// non-mandatory field)
		if (!isPresent(obj)) return errors;
		
		// First check if the input object is a valid decimal number.
		if (!isDecimal(obj)) {
			errorLocations.add(GSTConstants.TAXABLE_VALUE);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER080",
					"Invalid SGST /UT GST Rate", location));
			// if it's not a number, then we can return the errors
			// immediately
			return errors;
		}
	
		// If it's a valid number, then check if the value is greater than
		// zero.
		BigDecimal result = new BigDecimal(obj.toString());
		if (result.compareTo(BigDecimal.ZERO) < 0) {
			errorLocations.add(GSTConstants.TAXABLE_VALUE);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER080",
					"Invalid SGST /UT GST Rate", location));
			return errors;
		}
		return errors;
	}
}
