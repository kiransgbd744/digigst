package com.ey.advisory.app.services.strcutvalidation.gstr8;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

public class OriginalReturnPeriodValidationRule implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(int idx, Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		String documentType = null;
		if (row[5] != null)
			documentType = row[5].toString().trim();

		if (documentType != null) {
			if (documentType.equalsIgnoreCase("RNV")) {
				if (!isPresent(obj)) {
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.RETURN_PREIOD);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER8004",
							"Invalid OriginalReturnPeriod", location));
					return errors;
				}
			}
		}
		if (obj != null) {
			String originalTaxPeriod = obj.toString().trim();
			if (!originalTaxPeriod.matches("[0-9]+")
					|| originalTaxPeriod.length() != 6) {
				errors = createValErrors(errors);
				return errors;
			}
			if (originalTaxPeriod.matches("[0-9]+")) {
				int month = Integer.valueOf(originalTaxPeriod.substring(0, 2));
				if (originalTaxPeriod.length() != 6 || month > 12
						|| month == 00) {
					errors = createValErrors(errors);
					return errors;
				}
			}
			Boolean validReturnPeriod = true;
			if (row[1] != null) {
				String taxPeriod = row[1].toString().trim();
				if (!taxPeriod.matches("[0-9]+") || taxPeriod.length() != 6) {
					validReturnPeriod = false;
				}

				if (taxPeriod != null && originalTaxPeriod != null
						&& validReturnPeriod) {
					int derivedTaxPeriod = GenUtil
							.getDerivedTaxPeriod(taxPeriod);
					int derivedOriginalTaxPeriod = GenUtil
							.getDerivedTaxPeriod(originalTaxPeriod);
					if (derivedOriginalTaxPeriod >= derivedTaxPeriod) {
						Set<String> errorLocations = new HashSet<>();
						errorLocations.add(GSTConstants.RETURN_PREIOD);
						TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
								null, errorLocations.toArray());
						errors.add(
								new ProcessingResult(APP_VALIDATION, "ER8022",
										"OriginalReturnPeriod cannot be Beyond or same as Returnperiod",
										location));
						return errors;
					}
				}
			}
		}
		return errors;
	}

	private List<ProcessingResult> createValErrors(
			List<ProcessingResult> errors) {
		Set<String> errorLocations = new HashSet<>();
		errorLocations.add(GSTConstants.RETURN_PREIOD);
		TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
				null, errorLocations.toArray());
		errors.add(new ProcessingResult(APP_VALIDATION, "ER8004",
				"Invalid OriginalReturnPeriod", location));
		return errors;
	}
}
