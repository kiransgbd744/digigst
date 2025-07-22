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

public class OriginalSupplierGstinValidationRule implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(int idx, Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		String docType = null;
		if (row[5] != null)
			docType = row[5].toString().trim();
		if (docType != null) {
			if (docType.equalsIgnoreCase("RNV")) {
				if (!isPresent(obj)) {
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.SGSTIN);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());

					errors.add(new ProcessingResult(APP_VALIDATION, "ER8009",
							"Invalid OriginalSupplierGSTIN", location));
					return errors;
				}
			}
		}
		if (obj != null) {
			String regex1 = "^[0-9][0-9][A-Za-z][A-Za-z][A-Za-z]"
					+ "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][A-Za-z][A-Za-z0-9]"
					+ "[A-Za-z0-9][A-Za-z0-9]$";

			Pattern pattern1 = Pattern.compile(regex1);
			String supplierGstin = obj.toString().trim();
			Matcher matcher1 = pattern1.matcher(supplierGstin);
			String regex2 = "^[0-9]{12}[E][S][0-9a-zA-Z]{1}$";

			Pattern pattern2 = Pattern.compile(regex2);
			Matcher matcher2 = pattern2.matcher(supplierGstin);

			if (!(matcher1.matches() || matcher2.matches())
					|| supplierGstin.length() != 15) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.SGSTIN);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());

				errors.add(new ProcessingResult(APP_VALIDATION, "ER8009",
						"Invalid OriginalSupplierGSTIN", location));
				return errors;
			}

			if (row[0] != null) {
				String ecomGstin = row[0].toString().trim();
				if (supplierGstin.equalsIgnoreCase(ecomGstin)) {
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.SGSTIN);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());

					errors.add(new ProcessingResult(APP_VALIDATION, "ER8024",
							"eCOMGSTIN and OriginalSupplierGSTINorEnrolmentID should not be same",
							location));
					return errors;
				}
			}
		}
		return errors;
	}

}
