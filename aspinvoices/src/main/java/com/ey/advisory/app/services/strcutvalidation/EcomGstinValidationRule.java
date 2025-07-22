package com.ey.advisory.app.services.strcutvalidation;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ey.advisory.app.services.strcutvalidation.sales.ValidationRule;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

public class EcomGstinValidationRule implements ValidationRule {
	
	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		
		if(obj!=null){
		String ecomGstin=obj.toString();
	
			String regex = "^[0-9][0-9][A-Za-z][A-Za-z][A-Za-z]"
					+ "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][A-Za-z][A-Za-z0-9]"
					+ "[Cc][A-Za-z0-9]$";
				Pattern pattern = Pattern.compile(regex);
					
				  Matcher matcher = pattern.matcher(ecomGstin);
				 
				  if(!matcher.matches()){
					 
						errorLocations.add(GSTConstants.E_ComGstin);
						TransDocProcessingResultLoc location 
						     = new TransDocProcessingResultLoc(
								null, errorLocations.toArray());
						errors.add(new ProcessingResult(APP_VALIDATION, "ER087",
								"Invalid eCom GSTIN",
								location));  
					  
				  }
		}
		return errors;
	}

}
