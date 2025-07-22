package com.ey.advisory.app.services.strcutvalidation.b2c;

import static com.ey.advisory.common.FormatValidationUtil.isDecimal;
import static com.ey.advisory.common.GSTConstants.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.services.strcutvalidation.sales.ValidationRule;
import com.ey.advisory.common.NumberFomatUtil;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

/**
 * 
 * @author Mahesh.Golla
 *
 */

public class B2cOtherValValidationRule implements ValidationRule {
	private static final String OTHER = "OtherValue";

	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {
        List<ProcessingResult> errors = new ArrayList<>();
		if (obj != null) {
			
			// First check if the input object is a valid decimal number.
			boolean isValid = NumberFomatUtil.isValidDec(obj);
			if(!isDecimal(obj) || !isValid){
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(OTHER);
				TransDocProcessingResultLoc location 
				                   = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER0235",
						"Invalid Other Amount", location));
				// if it's not a number, then we can return the errors
				// immediately
				return errors;
			}
			
		}

		return errors;
	}

}
