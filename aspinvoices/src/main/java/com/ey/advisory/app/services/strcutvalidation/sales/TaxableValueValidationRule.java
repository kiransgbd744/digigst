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

public class TaxableValueValidationRule implements ValidationRule {

	
	
	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row, 
			                                   TabularDataLayout layout) {

		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		Object docTypeObj = (String) row[10];
		Object supplyTypeObj = (String) row[11];
	if(obj!=null){
		String regex= "^[0-9-.]*$";
		  Pattern pattern = Pattern.compile(regex);
		  
		  Matcher matcher = pattern.matcher(obj.toString());
		  if(!matcher.matches()){
			  errorLocations.add(GSTConstants.TAXABLE_VALUE);
				TransDocProcessingResultLoc location 
	            = new TransDocProcessingResultLoc(
	null, errorLocations.toArray());
	errors.add(new ProcessingResult(APP_VALIDATION, "ER075",
	"Invalid Taxable Value",
	location));
		  }
		  if(matcher.matches()){
		BigDecimal result = new BigDecimal(obj.toString());
		if((docTypeObj!=null && (docTypeObj.toString()!="CR" 
				|| docTypeObj.toString()!="RCR" 
				|| docTypeObj.toString()!="RFV" 
				|| docTypeObj.toString()!="RRFV" )) 
				|| (supplyTypeObj!=null && supplyTypeObj.toString()!="CAN")){
			if(result.compareTo(BigDecimal.ZERO) < 0){
				errorLocations.add(GSTConstants.TAXABLE_VALUE);
				TransDocProcessingResultLoc location 
	            = new TransDocProcessingResultLoc(
	null, errorLocations.toArray());
	errors.add(new ProcessingResult(APP_VALIDATION, "ER075",
	"Invalid Taxable Value",
	location));
		}
		}
		}
	}
		return errors;
	}

}

