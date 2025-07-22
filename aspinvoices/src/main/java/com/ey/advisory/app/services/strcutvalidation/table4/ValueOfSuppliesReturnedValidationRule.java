package com.ey.advisory.app.services.strcutvalidation.table4;

import static com.ey.advisory.common.FormatValidationUtil.isDecimal;
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

public class ValueOfSuppliesReturnedValidationRule implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {
List<ProcessingResult> errors = new ArrayList<>();
		
		
		if (obj != null) {
			
			// First check if the input object is a valid decimal number.
			if (!isDecimal(obj)) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.SGST_AMOUNT);
				TransDocProcessingResultLoc location 
				                   = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER081",
						"Invalid SGST/UT GST Amount", location));
				// if it's not a number, then we can return the errors
				// immediately
				return errors;
			}
			
		}

		return errors;
	}

}
