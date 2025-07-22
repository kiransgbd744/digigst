package com.ey.advisory.app.services.strcutvalidation.advanceAdjusted;

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
public class TxpdTranactionType implements ValidationRule {

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
			List<String> errorLocations = new ArrayList<>();
			errorLocations.add(TransactionType);
			TransDocProcessingResultLoc location = 
					new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER5175",
					"Invalid Transaction Type", location));
		}
		return errors;
	}

}
