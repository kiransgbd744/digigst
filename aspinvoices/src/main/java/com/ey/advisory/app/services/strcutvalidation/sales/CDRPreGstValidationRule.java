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
public class CDRPreGstValidationRule implements ValidationRule {
	
	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row, 
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		
	
if(obj != null){
		if(  !obj.toString().equalsIgnoreCase("Y") 
			|| !obj.toString().equalsIgnoreCase("N")){
/*if((obj.toString().length() > 1) ||!(obj.equals("Y") || obj.equals("y") 
				|| obj.equals("n") || obj.equals("N"))){
*/
	errorLocations.add(GSTConstants.PRE_GST);
	TransDocProcessingResultLoc location 
	                     = new TransDocProcessingResultLoc(
			null, errorLocations.toArray());
	errors.add(new ProcessingResult(APP_VALIDATION, "ER059",
			"Invalid Pre-GST Flag",
			location));
}

}	
		return errors;
	}

	
}
