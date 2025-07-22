package com.ey.advisory.app.services.strcutvalidation.gstr8;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
public class OriginalPosValidationRule implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(int idx, Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();

		if (row[7] != null && row[5] != null) {
			String supplierGstin = row[7].toString().trim();
			String docType = row[5].toString().trim();

			String regex1 = "^[0-9][0-9][A-Za-z][A-Za-z][A-Za-z]"
					+ "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][A-Za-z][A-Za-z0-9]"
					+ "[A-Za-z0-9][A-Za-z0-9]$";

			Pattern pattern1 = Pattern.compile(regex1);
			Matcher matcher1 = pattern1.matcher(supplierGstin);

			if ((matcher1.matches() && supplierGstin.length() == 15)
					&& docType.equalsIgnoreCase("RNV")) {
				if (!isPresent(obj)) {
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.POS);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER8027",
							"Invalid POS", location));
					return errors;
				}
				String posRegex = "^[0-9]*$";
				Pattern posPattern = Pattern.compile(posRegex);

				Matcher posMatcher = posPattern.matcher(obj.toString().trim());
				if (obj.toString().trim().length() > 2
						|| !posMatcher.matches()) {
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.POS);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());

					errors.add(new ProcessingResult(APP_VALIDATION, "ER8027",
							"Invalid POS", location));
					return errors;
				}
				return errors;

			}
		}
		return errors;
	}
}
