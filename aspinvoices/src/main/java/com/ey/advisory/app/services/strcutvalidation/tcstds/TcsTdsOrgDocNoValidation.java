
package com.ey.advisory.app.services.strcutvalidation.tcstds;

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


public class TcsTdsOrgDocNoValidation implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		if(obj==null){
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.ORG_DOCUMENT_NUMBER);
			TransDocProcessingResultLoc location 
			                     = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER1340",
					"Invalid OriginalDocumentNumber",
					location));
		}
		if (obj!=null) {
			
			String docNo =obj.toString(); 
			String regex= "^[a-zA-Z0-9/-]*$";
				  Pattern pattern = Pattern.compile(regex);
				  
				  Matcher matcher = pattern.matcher(docNo);
				  if(docNo.length() > 16 || !matcher.matches()){
					
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.ORG_DOCUMENT_NUMBER);
					TransDocProcessingResultLoc location 
					                     = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER1340",
							"Invalid OriginalDocumentNumber",
							location));
				}
			}
		
		return errors;
	}

}
