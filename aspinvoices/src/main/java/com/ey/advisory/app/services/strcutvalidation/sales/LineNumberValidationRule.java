package com.ey.advisory.app.services.strcutvalidation.sales;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

public class LineNumberValidationRule implements ValidationRule {
	
	

	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row, 
			                                 TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		if(obj==null || obj.toString().length() > 20){
			errorLocations.add(GSTConstants.LINE_NO);
			TransDocProcessingResultLoc location 
			                     = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER560",
					"Invalid Line Number",
					location));
		}
		if (obj!=null) {
			
			String regex= "^[0-9]*$";
			  Pattern pattern = Pattern.compile(regex);
			  
			  Matcher matcher = pattern.matcher(obj.toString());
			  if(!matcher.matches()){
				  errorLocations.add(GSTConstants.LINE_NO);
					TransDocProcessingResultLoc location 
					                     = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER560",
							"Invalid Line Number",
							location));  
			  }
			 
		}

		
		return errors;
	}

}
