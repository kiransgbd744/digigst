package com.ey.advisory.app.services.strcutvalidation.b2c;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public class B2cSgstinValidationRule implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {

		List<ProcessingResult> errors = new ArrayList<>();

		if (!isPresent(obj)) {
			Set<String> errorLocations = new HashSet<>();
			TransDocProcessingResultLoc location = 
					new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errorLocations.add(GSTConstants.SGSTIN);
			errors.add(new ProcessingResult(APP_VALIDATION, "ER0203",
					"Supplier GSTIN cannot be left balnk", location));
			return errors;
		}

		String sgstinRegex = "^[0-9][0-9][A-Za-z][A-Za-z][A-Za-z]"
				+ "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][A-Za-z][A-Za-z0-9]"
				+ "[A-Za-z0-9][A-Za-z0-9]$";
		Pattern pattern = Pattern.compile(sgstinRegex);

		Matcher matcher = pattern.matcher(obj.toString().trim());

		if (obj.toString().trim().length() != 15 || !matcher.matches()) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.SGSTIN);
			TransDocProcessingResultLoc location = 
					new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER0204",
					"Supplier GSTIN is not as per On-Boarding data", location));
			return errors;
		}
		return errors;

	}
}
