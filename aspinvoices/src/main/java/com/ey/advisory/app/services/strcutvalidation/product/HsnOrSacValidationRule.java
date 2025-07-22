package com.ey.advisory.app.services.strcutvalidation.product;

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
 * @author Anand3.M
 *
 */

public class HsnOrSacValidationRule implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (!isPresent(obj)) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.HSNORSAC);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER1615",
					"HSN or SAC cannot be left blank", location));
			return errors;
		}

		String regex = "^[0-9]*$";
		Pattern pattern = Pattern.compile(regex);

		Matcher matcher = pattern.matcher(obj.toString());

		String hsnOrSac = obj.toString();

		if (hsnOrSac.length() > 8 || !matcher.matches()) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.HSNORSAC);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, " ER1615",
					"HSN or SAC cannot be left blank", location));
			return errors;

		}

		return errors;

	}
}
