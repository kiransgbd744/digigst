package com.ey.advisory.app.services.strcutvalidation.itc04;

import static com.ey.advisory.common.FormatValidationUtil.isDecimal;
import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.QUANTITY;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.services.strcutvalidation.outward.ValidationRule;
import com.ey.advisory.common.NumberFomatUtil;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

/**
 * 
 * @author Mahesh.Golla
 *
 */

public class QuantityValidationRule implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(int idx, Object obj, Object[] row,
			TabularDataLayout layout) {

		List<ProcessingResult> errors = new ArrayList<>();
		if (!isPresent(obj)) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(QUANTITY);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER5835",
					"Invalid Quantity.", location));
			// if it's not a number, then we can return the errors
			// immediately
			return errors;
		}

		// First check if the input object is a valid decimal number.
		Object quentity = obj.toString().trim();
		if (!isDecimal(quentity)) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(QUANTITY);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER5835",
					"Invalid Quantity.", location));
			// if it's not a number, then we can return the errors
			// immediately
			return errors;
		}
		boolean isValid = NumberFomatUtil.is132digValidDec(quentity);
		if (!isValid) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(QUANTITY);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER5835",
					"Invalid Quantity.", location));
			// if it's not a number, then we can return the errors
			// immediately
			return errors;
		}
		BigDecimal result = new BigDecimal(obj.toString().trim());
		Set<String> errorLocations = new HashSet<>();
		if (result.compareTo(BigDecimal.ZERO) < 0) {
			errorLocations.add(QUANTITY);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER5836",
					"Quantity cannot be negative.", location));
			return errors;
		}
		return errors;
	}
}
