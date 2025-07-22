package com.ey.advisory.app.services.strcutvalidation;

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

public class ShipToStateValidationRule implements ValidationRule {
	
	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		if(obj!=null){
			if(obj.toString().length() > 2){
				errorLocations.add(GSTConstants.ShipToState);
				TransDocProcessingResultLoc location 
				                     = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "EXXXX",
						"Invalid Ship to State",
						location));
			}
		}
		
		return errors;
	}

}
