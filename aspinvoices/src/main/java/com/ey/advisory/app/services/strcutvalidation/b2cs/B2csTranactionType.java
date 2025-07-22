package com.ey.advisory.app.services.strcutvalidation.b2cs;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.TransactionType;

import java.util.ArrayList;
import java.util.List;

import com.ey.advisory.app.services.strcutvalidation.sales.ValidationRule;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.google.common.collect.ImmutableList;

/**
 * 
 * @author Mahesh.Golla
 *
 */
public class B2csTranactionType implements ValidationRule {

	private static final List<String> TRANS_TYPES = ImmutableList.of("L65",
			"l65", "N", "n");

	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (!isPresent(obj))
			return errors;

		if (obj.toString().trim().length() > 5
				|| !TRANS_TYPES.contains(obj.toString().trim())) {
			List<String> errorLocations = new ArrayList<>();
			errorLocations.add(TransactionType);
			TransDocProcessingResultLoc location = 
					new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER5032",
					"Invalid Transaction Type.", location));
		}
		return errors;
	}

}
