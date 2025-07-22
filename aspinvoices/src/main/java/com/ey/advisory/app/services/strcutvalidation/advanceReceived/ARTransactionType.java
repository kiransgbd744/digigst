package com.ey.advisory.app.services.strcutvalidation.advanceReceived;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.services.strcutvalidation.sales.ValidationRule;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.google.common.collect.ImmutableList;
/**
 * 
 * @author Mahesh.Golla
 *
 */
public class ARTransactionType implements ValidationRule {

	private static final List<String> IMPORTS = ImmutableList.of("L65", "N",
			"n", "l65","Z","ZL65","z","zl65","Zl65","zL65");

	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();

		if (!isPresent(obj))
			return errors;

		if (obj.toString().trim().length() > 5
				|| !IMPORTS.contains(obj.toString().trim())) {

			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.TransactionType);
			TransDocProcessingResultLoc location = 
					new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER5126",
					"Invalid Transaction Type", location));

		}
		return errors;
	}

}