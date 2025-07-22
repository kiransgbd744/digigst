package com.ey.advisory.app.services.strcutvalidation.item;

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

public class ItemReverseChargeValidation implements ValidationRule {

	private static final List<String> Revarse_charge_IMPORTS = 
			ImmutableList.of("y","n","Y","N");
	
	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		if(obj!=null){
			
			if(!(Revarse_charge_IMPORTS.contains(obj.toString())) 
					            || obj.toString().length() > 1){
		
	errorLocations.add(GSTConstants.ReverseCharge);
	TransDocProcessingResultLoc location 
	                     = new TransDocProcessingResultLoc(
			null, errorLocations.toArray());
	errors.add(new ProcessingResult(APP_VALIDATION, "ER1617",
			"Invalid Reverse Charge Flag",
			location));
}

		}	

		return errors;
	}
}
