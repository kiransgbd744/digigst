package com.ey.advisory.app.services.strcutvalidation.gstr8;

import static com.ey.advisory.common.FormatValidationUtil.isDecimal;
import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.NumberFomatUtil;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

public class OriginalNetSuppliesValidationRule implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(int idx, Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		String documentType = null;
		String supplieGstin = null;

		if (row[5] != null)
			documentType = row[5].toString().trim();
		if (row[7] != null)
			supplieGstin = row[7].toString().trim();
		if (documentType != null && supplieGstin != null) {
			if (documentType.equalsIgnoreCase("RNV")
					&& !supplieGstin.isEmpty()) {
				if (!isPresent(obj)) {
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.NET_SUPPLIES);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							idx, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER8005",
							"Invalid NetSupplies", location));
					return errors;
				}
			}
		}
		if (obj != null) {
			if (!isDecimal(obj.toString().trim())) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.NET_SUPPLIES);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER8005",
						"Invalid NetSupplies", location));
				return errors;
			}
			if (NumberFomatUtil.getBigDecimal(obj.toString().trim())
					.signum() == -1) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.NET_SUPPLIES);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER8005",
						"Invalid NetSupplies", location));
				return errors;
			}
			boolean isValid = NumberFomatUtil
					.is132digValidDec(obj.toString().trim());
			if (!isValid) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.NET_SUPPLIES);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER8005",
						"Invalid NetSupplies", location));
				return errors;
			}
		}

		return errors;
	}
}
