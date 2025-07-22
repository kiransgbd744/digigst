package com.ey.advisory.app.services.structvalidation.gstr7;

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
import com.google.common.base.Strings;

/**
 * 
 * @author Siva.Reddy
 *
 */
public class Gstr7TransOriginalDeducteeGstinValidationRule
		implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(int idx, Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();

		if (!isPresent(obj))
			return errors;

		String deducteeGstin = obj != null ? obj.toString().trim() : "";
		if (!Strings.isNullOrEmpty(deducteeGstin)) {
			String regex = "^[0-9][0-9][A-Za-z][A-Za-z][A-Za-z]"
					+ "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][A-Za-z][A-Za-z0-9]"
					+ "[A-Za-z0-9][A-Za-z0-9]$";

			String regex1 = "^[0-9][0-9][A-Za-z][A-Za-z][A-Za-z]"
					+ "[A-Za-z][0-9][0-9][0-9][0-9][0-9][A-Za-z][A-Za-z0-9]"
					+ "[A-Za-z0-9][A-Za-z0-9]$";

			String regex2 = "^[0-9][0-9][0-9][0-9][A-Za-z]"
					+ "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][0-9][UOuo]"
					+ "[Nn][A-Za-z0-9]$";

			String regex3 = "^[0-9][0-9][0-9][0-9][A-Za-z]"
					+ "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][0-9][Nn]"
					+ "[Rr][A-Za-z0-9]$";

			Pattern pattern = Pattern.compile(regex);

			Pattern pattern1 = Pattern.compile(regex1);

			Pattern pattern2 = Pattern.compile(regex2);

			Pattern pattern3 = Pattern.compile(regex3);
			Matcher matcher = pattern.matcher(deducteeGstin);
			Matcher matcher1 = pattern1.matcher(deducteeGstin);
			Matcher matcher2 = pattern2.matcher(deducteeGstin);
			Matcher matcher3 = pattern3.matcher(deducteeGstin);
			if (matcher.matches() || matcher1.matches() || matcher2.matches()
					|| matcher3.matches()) {
			} else {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.ORG_TDS_DEDUCTEE_GSTIN);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());

				errors.add(new ProcessingResult(APP_VALIDATION, "ER63009",
						"Invalid Original Deductee GSTIN.", location));
				return errors;
			}
		}

		return errors;

	}

}
