package com.ey.advisory.app.services.strcutvalidation.tcstds;

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
 * @author Mahesh.Golla
 *
 */
public class TcsTdsDeductorGstinValidationRule implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {

		List<ProcessingResult> errors = new ArrayList<>();

		if (!isPresent(obj)) {
			Set<String> errorLocations = new HashSet<>();
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errorLocations.add(GSTConstants.TDS_DEDUCTOR_GSTIN);
			errors.add(new ProcessingResult(APP_VALIDATION, "ER1333",
					"Deductor/CollectorGSTIN cannot be left blank.", location));
			return errors;
		}
		String sgstinRegex = "^[0-9][0-9][A-Za-z][A-Za-z][A-Za-z]"
				+ "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][A-Za-z][A-Za-z0-9]"
				+ "[A-Za-z0-9][A-Za-z0-9]$";
		// Pattern pattern = Pattern.compile(sgstinRegex);
		// Matcher matcher = pattern.matcher(obj.toString().trim()); ||
		// !matcher.matches()
		if (obj != null && obj.toString().trim().length() != 15) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.TDS_DEDUCTOR_GSTIN);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER1334",
					"Invalid Deductor/CollectorGSTIN", location));
			return errors;
		}
		return errors;

	}
}
