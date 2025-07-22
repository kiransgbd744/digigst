package com.ey.advisory.app.services.strcutvalidation.b2c;

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
public class B2cAutoPopRefundValidationRule implements ValidationRule {
	private static final List<String> IMPORTS = ImmutableList.of("Y", "N", "y",
			"n");

	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (!isPresent(obj)) return errors;
		
		Set<String> errorLocations = new HashSet<>();
		String refund = obj.toString().trim();
			// First check if the input object is a valid decimal number.
			if (refund.length() > 1 || 
					!IMPORTS.contains(refund)) {
				errorLocations.add(GSTConstants.AUTO_POP_REFUND);
				TransDocProcessingResultLoc location 
				                     = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER0211",
						"Invalid Auto Populate to Refund Flag.",
						location));
			}
			
		
		return errors;
	}
}
