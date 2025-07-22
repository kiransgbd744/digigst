package com.ey.advisory.app.services.strcutvalidation.itc04;

import static com.ey.advisory.common.FormatValidationUtil.isDecimal;
import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.services.strcutvalidation.outward.ValidationRule;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.NumberFomatUtil;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

/**
 * 
 * @author Mahesh.Golla
 *
 */

public class LossessQuantityValidationRule implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(int idx, Object obj, Object[] row,
			TabularDataLayout layout) {

		List<ProcessingResult> errors = new ArrayList<>();
		if (!isPresent(obj)) {
			return errors;
		}

		// First check if the input object is a valid decimal number.
		Object quentity = obj.toString().trim();
		if (!isDecimal(quentity)) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.LOSSESS_QNT);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER5837",
					"Invalid LossesQuantity.", location));
			// if it's not a number, then we can return the errors
			// immediately
			return errors;
		}
		boolean isValid = NumberFomatUtil.is132digValidDec(quentity);
		if (!isValid) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.LOSSESS_QNT);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER5837",
					"Invalid LossesQuantity.", location));
			// if it's not a number, then we can return the errors
			// immediately
			return errors;
		}
		return errors;
	}
}
