package com.ey.advisory.app.services.strcutvalidation.vendor;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ey.advisory.app.services.strcutvalidation.outward.ValidationRule;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

public class SuppliergstinValidationRule implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(int idx, Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		String supplierGstnOrPan = null;
		if (obj != null) {
			supplierGstnOrPan = obj.toString();
			if (supplierGstnOrPan != null && supplierGstnOrPan.length() != 15
					&& supplierGstnOrPan.length() != 10) {
				Set<String> errorLocations = new HashSet<>();
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errorLocations.add(GSTConstants.SGSTIN);
				errors.add(new ProcessingResult(APP_VALIDATION, "ER1627",
						"Invalid Supplier GSTIN/PAN.", location));
				return errors;
			}
			String regex = "^[0-9][0-9][A-Za-z][A-Za-z][A-Za-z]"
					+ "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][A-Za-z][A-Za-z0-9]"
					+ "[A-Za-z0-9][A-Za-z0-9]$";
			Pattern pattern = Pattern.compile(regex);

			Matcher matcher = pattern.matcher(obj.toString());
			if (supplierGstnOrPan != null && supplierGstnOrPan.length() == 15
					&& !matcher.matches()) {
				Set<String> errorLocations = new HashSet<>();
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errorLocations.add(GSTConstants.SGSTIN);
				errors.add(new ProcessingResult(APP_VALIDATION, "ER1627",
						"Invalid Supplier GSTIN/PAN", location));
				return errors;
			}
			String panRegex1 = "^[A-Za-z][A-Za-z][A-Za-z]"
					+ "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][A-Za-z]$";
			Pattern panPattern = Pattern.compile(panRegex1);

			Matcher panMatcher = panPattern.matcher(obj.toString());
			if (supplierGstnOrPan != null && supplierGstnOrPan.length() == 10
					&& !panMatcher.matches()) {
				Set<String> errorLocations = new HashSet<>();
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errorLocations.add(GSTConstants.SGSTIN);
				errors.add(new ProcessingResult(APP_VALIDATION, "ER1627",
						"Invalid Supplier GSTIN/PAN", location));
				return errors;
			}

		}
		return errors;

	}

}
