package com.ey.advisory.app.services.strcutvalidation.table4;

import static com.ey.advisory.common.FormatValidationUtil.isDecimal;
import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.math.BigDecimal;
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

public class Table4ValOfSuppMadeValidationRule implements ValidationRule {

	public static final String VALUEOFSUPPMADE = "ValOfSuppliesMade";

	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();

		if (!isPresent(obj)) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(VALUEOFSUPPMADE);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());

			errors.add(new ProcessingResult(APP_VALIDATION, "ER0310",
					" Value of Supplies made cannot be left Blank.", location));
			return errors;
		}

		if (!isDecimal(obj.toString().trim())) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(VALUEOFSUPPMADE);
			TransDocProcessingResultLoc location = 
					new TransDocProcessingResultLoc(
					null, errorLocations.toArray());

			errors.add(new ProcessingResult(APP_VALIDATION, "ER0311",
					"   Invalid Value of Supplies made.", location));
			return errors;
		}
		boolean isValid = NumberFomatUtil.isValidDec(obj.toString().trim());
		if (!isValid) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(VALUEOFSUPPMADE);
			TransDocProcessingResultLoc location = 
					new TransDocProcessingResultLoc(
					null, errorLocations.toArray());

			errors.add(new ProcessingResult(APP_VALIDATION, "ER0311",
					"   Invalid Value of Supplies made.", location));
			return errors;
		}
		BigDecimal result = new BigDecimal(obj.toString().trim());
		Set<String> errorLocations = new HashSet<>();
		if (result.compareTo(BigDecimal.ZERO) < 0) {
			errorLocations.add(VALUEOFSUPPMADE);
			TransDocProcessingResultLoc location = 
					new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER0310",
					"Invalid Value of Supplies made.", location));
			return errors;
		}
		return errors;
	}
}
