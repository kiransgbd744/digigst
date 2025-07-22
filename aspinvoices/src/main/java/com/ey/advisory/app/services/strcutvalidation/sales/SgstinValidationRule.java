package com.ey.advisory.app.services.strcutvalidation.sales;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

public class SgstinValidationRule implements ValidationRule {

	
	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row, 
			                             TabularDataLayout layout) {
		
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		
		if (obj==null || obj.toString().length() != 15) {
			TransDocProcessingResultLoc location 
            = new TransDocProcessingResultLoc(
            null, errorLocations.toArray());
			errorLocations.add(GSTConstants.SGSTIN);
			errors.add(new ProcessingResult(APP_VALIDATION, "ER052",
					"Invalid Supplier GSTIN",
					location));
		}
		
		
		return errors;

	}

	

}
