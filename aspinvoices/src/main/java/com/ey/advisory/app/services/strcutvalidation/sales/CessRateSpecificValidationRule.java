package com.ey.advisory.app.services.strcutvalidation.sales;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.math.BigDecimal;
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

public class CessRateSpecificValidationRule implements ValidationRule {

	

	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row, 
			                                TabularDataLayout layout) {

		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		
		if(obj!=null){
			BigDecimal result=new BigDecimal(obj.toString());
			String regex= "^[0-9-.]*$";
			  Pattern pattern = Pattern.compile(regex);
			  
			  Matcher matcher = pattern.matcher(obj.toString());
			  if(!matcher.matches()){
				  errorLocations.add(GSTConstants.CESS_RATE_SPECIFIC);
					TransDocProcessingResultLoc location 
					                     = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER082",
							"Invalid Cess Rate",
							location));		  }
			  if(matcher.matches()){
			if(result.compareTo(BigDecimal.ZERO) < 0){
				errorLocations.add(GSTConstants.CESS_RATE_SPECIFIC);
				TransDocProcessingResultLoc location 
				                     = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER082",
						"Invalid Cess Rate",
						location));
			}
		}
		}
		return errors;
	}
}
		
