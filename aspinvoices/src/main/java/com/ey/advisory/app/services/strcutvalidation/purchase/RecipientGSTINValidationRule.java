package com.ey.advisory.app.services.strcutvalidation.purchase;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GSTConstants.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.services.strcutvalidation.sales.ValidationRule;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

public class RecipientGSTINValidationRule implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {

		List<ProcessingResult> errors = new ArrayList<>();

		if (!isPresent(obj)) {
			Set<String> errorLocations = new HashSet<>();
			TransDocProcessingResultLoc location = 
					new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errorLocations.add(GSTConstants.RECIPIENT_GSTIN);
			errors.add(new ProcessingResult(APP_VALIDATION, "ER1203",
					"Recipient GSTIN cannot be left balnk", location));
		}
		String regex = "[A-Za-z0-9]+";
		if (obj != null && obj.toString().length() == 15
				&& obj.toString().matches(regex)) {
			return errors;

		} else {
			Set<String> errorLocations = new HashSet<>();
			TransDocProcessingResultLoc location = 
					new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errorLocations.add(GSTConstants.RECIPIENT_GSTIN);
			errors.add(new ProcessingResult(APP_VALIDATION, "ER1203",
					"Recipient GSTIN cannot be left balnk", location));
		}

		if (obj != null && obj.toString().length() != 15) {
			Set<String> errorLocations = new HashSet<>();
			TransDocProcessingResultLoc location = 
					new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errorLocations.add(GSTConstants.RECIPIENT_GSTIN);
			errors.add(new ProcessingResult(APP_VALIDATION, "ER1204",
					"Recipient GSTIN is not as per On-Boarding data",
					location));
		}
		return errors;
	}
}
