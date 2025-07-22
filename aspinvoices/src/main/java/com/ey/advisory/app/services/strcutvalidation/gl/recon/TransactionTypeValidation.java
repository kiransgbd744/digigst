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

public class TransactionTypeValidation implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(int idx, Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();

		if (isPresent(obj)) {
			String transactionType = obj.toString().trim();

			if (!transactionType.equalsIgnoreCase("I")
					&& !transactionType.equalsIgnoreCase("O")) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.TRANSACTION_TYPE);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER0002",
						"Invalid Transaction Type: Please enter 'I' for Inward or 'O' for Outward transactions",
						location));
				return errors;
			}
		} else {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.TRANSACTION_TYPE);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER0001",
					"Transaction Type cannot be left blank", location));
			return errors;

		}

		return errors;
	}

}
