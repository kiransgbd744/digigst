package com.ey.advisory.app.services.strcutvalidation;

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

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;
public class ItcFlagValidationRule implements ValidationRule {
	private static final List<String> ITC_charge_IMPORTS = 
			ImmutableList.of("T1","T2","T3","T4");
	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		if(obj!=null){
			if(obj.toString().length() > 2 
					|| !(ITC_charge_IMPORTS.contains(
							trimAndConvToUpperCase(obj.toString())))){
			
	errorLocations.add(GSTConstants.ItcFlag);
	TransDocProcessingResultLoc location 
	                     = new TransDocProcessingResultLoc(
			null, errorLocations.toArray());
	errors.add(new ProcessingResult(APP_VALIDATION, "ER088",
			"Invalid ITC Flag",
			location));
}

		}	

		return errors;
	}

}

