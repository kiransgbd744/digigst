package com.ey.advisory.app.services.strcutvalidation.vendor;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;
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

public class RecipientgstinValidationRule implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(int idx, Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();

		if (!isPresent(obj)) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.RecipientGSTIN);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());

			errors.add(new ProcessingResult(APP_VALIDATION, "ER1625",
					"Recipient GSTIN cannot be left balnk.", location));
			return errors;
		}

		if (obj.toString().length() == 15 || obj.toString().length() == 10) {
			// nothing
		} else {
			Set<String> errorLocations = new HashSet<>();
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errorLocations.add(GSTConstants.CGSTIN);
			errors.add(new ProcessingResult(APP_VALIDATION, "ER1624",
					"Invalid Recipient GSTIN.", location));
			return errors;
		}
		String regex = "^[0-9][0-9][A-Za-z][A-Za-z][A-Za-z]"
				+ "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][A-Za-z][A-Za-z0-9]"
				+ "[A-Za-z0-9][A-Za-z0-9]$";
		Pattern pattern = Pattern.compile(regex);

		Matcher matcher = pattern.matcher(obj.toString());
		if (obj.toString().length() == 15 && !matcher.matches()) {
			Set<String> errorLocations = new HashSet<>();
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errorLocations.add(GSTConstants.CGSTIN);
			errors.add(new ProcessingResult(APP_VALIDATION, "ER1624",
					"Invalid Recipient GSTIN..", location));
			return errors;
		}
		String regex1 = "^[A-Za-z][A-Za-z][A-Za-z]"
				+ "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][A-Za-z]$";
		Pattern pattern1 = Pattern.compile(regex1);

		Matcher matcher1 = pattern1.matcher(obj.toString());
		if (obj.toString().length() == 10 && !matcher1.matches()) {
			Set<String> errorLocations = new HashSet<>();
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errorLocations.add(GSTConstants.CGSTIN);
			errors.add(new ProcessingResult(APP_VALIDATION, "ER1624",
					"Invalid Recipient GSTIN.", location));
			return errors;
		}

		return errors;

	}

}
