package com.ey.advisory.app.services.strcutvalidation.advanceAdjusted;

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

public class TxpdPOSValidationRule implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row, 
			                               TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (!isPresent(obj))
			return errors;
		if(obj.toString().trim().length() != 2){
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.OrgPOS);
			TransDocProcessingResultLoc location 
			                     = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER5157",
					"Invalid OrgPOS ",
					location));
		}
		return errors;
	}
}
