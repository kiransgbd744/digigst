package com.ey.advisory.app.services.strcutvalidation.table3h3i;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;
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

/**
 * 
 * @author Sujith.Nanga
 *
 */
public class HSNValidationRule implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (!isPresent(obj)){
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.HSN);
			TransDocProcessingResultLoc location = 
					new TransDocProcessingResultLoc(
					null, errorLocations.toArray());

			errors.add(new ProcessingResult(APP_VALIDATION, "ER1219",
					"HSN or SAC cannot be left Blank.", location));
			return errors;

		}
			String hsn = obj.toString().trim();
			if (hsn.length() >= 6 && hsn.length() <= 8) {
              //nothing will print
			} else {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.HSN);
				TransDocProcessingResultLoc location = 
						new TransDocProcessingResultLoc(
						null, errorLocations.toArray());

				errors.add(new ProcessingResult(APP_VALIDATION, "ER1219",
						"Invalid HSN or SAC", location));
				return errors;
		}

		return errors;
	}

}
