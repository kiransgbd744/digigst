package com.ey.advisory.app.services.strcutvalidation.table3h3i;

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
 * @author Sujith.Nanga
 *
 */
public class SgstinOrPanValidationRule implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();

		if (!isPresent(obj))
			return errors;

		if (obj.toString().trim().length() != 15
				&& obj.toString().trim().length() != 10) {
			Set<String> errorLocations = new HashSet<>();
			TransDocProcessingResultLoc location = 
					new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errorLocations.add(GSTConstants.SUPPLIER_GSTIN_OR_PAN);
			errors.add(new ProcessingResult(APP_VALIDATION, "ER1211",
					"Invalid Supplier GSTIN or PAN.", location));
			return errors;
		}

		String gstinRegex = "^[0-9][0-9][A-Za-z][A-Za-z][A-Za-z]"
				+ "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][A-Za-z][A-Za-z0-9]"
				+ "[A-Za-z0-9][A-Za-z0-9]$";

		Pattern gstinPattern = Pattern.compile(gstinRegex);

		Matcher gstinMatcher = gstinPattern.matcher(obj.toString().trim());

		if (obj.toString().trim().length() == 15 && !gstinMatcher.matches()) {
			Set<String> errorLocations = new HashSet<>();
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errorLocations.add(GSTConstants.SUPPLIER_GSTIN_OR_PAN);
			errors.add(new ProcessingResult(APP_VALIDATION, "ER1211",
					"Invalid Supplier GSTIN or PAN.", location));
			return errors;
		}
		String panRegex = "^[A-Za-z][A-Za-z][A-Za-z]"
				+ "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][A-Za-z]$";
		Pattern panPattern = Pattern.compile(panRegex);

		Matcher panMatcher = panPattern.matcher(obj.toString().trim());
		if (obj.toString().trim().length() == 10 && !panMatcher.matches()) {
			Set<String> errorLocations = new HashSet<>();
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errorLocations.add(GSTConstants.SUPPLIER_GSTIN_OR_PAN);
			errors.add(new ProcessingResult(APP_VALIDATION, "ER1211",
					"Invalid Supplier GSTIN or PAN.", location));
			return errors;
		}

		return errors;
	}
}
