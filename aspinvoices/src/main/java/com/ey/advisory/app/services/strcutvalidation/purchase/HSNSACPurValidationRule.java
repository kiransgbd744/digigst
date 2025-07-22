package com.ey.advisory.app.services.strcutvalidation.purchase;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.services.strcutvalidation.ValidatorUtil;
import com.ey.advisory.app.services.strcutvalidation.sales.ValidationRule;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

public class HSNSACPurValidationRule implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		
		if(obj==null){
			errorLocations.add(GSTConstants.HSNORSAC);
			TransDocProcessingResultLoc location 
			                     = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER072",
					"Invalid HSN / SAC",
					location));
		}
		
		if(obj!=null){
			String hsnOrSac=obj.toString();
			
			
			if (!ValidatorUtil.isEvenNumber(hsnOrSac.length()) 
					|| hsnOrSac.length() > 8 ) {
				errorLocations.add(GSTConstants.HSNORSAC);
				TransDocProcessingResultLoc location 
				                     = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER072",
						"Invalid HSN / SAC",
						location));
			}
			
			
			 
		}
		
		return errors;
	}

}

