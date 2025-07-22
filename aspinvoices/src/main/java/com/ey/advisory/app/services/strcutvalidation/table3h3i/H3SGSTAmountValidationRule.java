package com.ey.advisory.app.services.strcutvalidation.table3h3i;

import static com.ey.advisory.common.FormatValidationUtil.isDecimal;
import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GSTConstants.*;

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

/**
 * 
 * @author Sujith.Nanga
 *
 */
public class H3SGSTAmountValidationRule implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		
		if (!isPresent(obj))
			return errors;

			// First check if the input object is a valid decimal number.
			if (!isDecimal(obj.toString().trim())) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.STATE_UTTAX_AMOUNT);
				TransDocProcessingResultLoc location = 
						new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER1225",
						"Invalid SGST/UTGST Amount", location));
				return errors;
			}
			boolean isValid = NumberFomatUtil.isValidDec(obj.toString().trim());
			if ( !isValid) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.STATE_UTTAX_AMOUNT);
				TransDocProcessingResultLoc location = 
						new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER1225",
						"Invalid SGST/UTGST Amount", location));
				return errors;
		}

	return errors;
}

}
