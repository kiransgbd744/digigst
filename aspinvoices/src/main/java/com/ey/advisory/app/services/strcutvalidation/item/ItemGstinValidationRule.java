package com.ey.advisory.app.services.strcutvalidation.item;

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
 * @author Anand3.M
 *
 */
public class ItemGstinValidationRule implements ValidationRule {
	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		String recipientGstnOrPan = null;
		if (obj != null) {
			recipientGstnOrPan = obj.toString();
			if (recipientGstnOrPan != null && recipientGstnOrPan.length() != 15
					&& recipientGstnOrPan.length() != 10) {
				Set<String> errorLocations = new HashSet<>();
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errorLocations.add(GSTConstants.GSTINORPAN);
				errors.add(new ProcessingResult(APP_VALIDATION, "ER1635",
						"GSTIN/PAN cannot be left balnk.", location));
				return errors;
			}

			String gstinRegex = "^[0-9][0-9][A-Za-z][A-Za-z][A-Za-z]"
					+ "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][A-Za-z][A-Za-z0-9]"
					+ "[A-Za-z0-9][A-Za-z0-9]$";
			Pattern gstinPattern = Pattern.compile(gstinRegex);

			Matcher gstinMatcher = gstinPattern.matcher(recipientGstnOrPan);

			if (recipientGstnOrPan != null && recipientGstnOrPan.length() == 15
					&& !gstinMatcher.matches()) {
				Set<String> errorLocations = new HashSet<>();
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errorLocations.add(GSTConstants.GSTINORPAN);
				errors.add(new ProcessingResult(APP_VALIDATION, "ER1634",
						"Invalid GSTIN/PAN", location));
				return errors;
			}
			String panRegex = "^[A-Za-z][A-Za-z][A-Za-z]"
					+ "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][A-Za-z]$";
			Pattern panPattern = Pattern.compile(panRegex);

			Matcher panMatcher = panPattern.matcher(recipientGstnOrPan);
			if (recipientGstnOrPan != null && recipientGstnOrPan.length() == 10
					&& !panMatcher.matches()) {
				Set<String> errorLocations = new HashSet<>();
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errorLocations.add(GSTConstants.GSTINORPAN);
				errors.add(new ProcessingResult(APP_VALIDATION, "ER1634",
						"Invalid GSTIN/PAN", location));
				return errors;
			}
		}
		return errors;
	}
}