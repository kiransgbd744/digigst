package com.ey.advisory.app.services.strcutvalidation.gl.recon;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.services.strcutvalidation.outward.ValidationRule;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

public class AccountingVoucherNumberValidation implements ValidationRule {
	@Override
	public List<ProcessingResult> isValid(int idx, Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();

		if (isPresent(obj)) {
			String accountingVoucherNumber = obj.toString().trim();
			if (accountingVoucherNumber.length() > 500) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.ACCOUNTING_VOCHAR_NUM);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						idx, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER0011",
						"Accounting Document Number cannot be more than 500 characters",
						location));
			}
		} else {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.ACCOUNTING_VOCHAR_NUM);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER0010",
					"Accounting Document Number cannot be left blank",
					location));
		}

		return errors;
	}

}
