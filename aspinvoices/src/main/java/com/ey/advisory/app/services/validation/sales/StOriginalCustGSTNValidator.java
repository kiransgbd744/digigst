package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

@Component("CMOriginalCustGSTNValidator")
public class StOriginalCustGSTNValidator implements 
                      DocRulesValidator<OutwardTransDocument>{

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		
 //String regex= "^[0-9]{2}[A-Za-z]{5}[0-9]{4}[A-Za-z]{1}[A-Za-z0-9]{3}$";
	String regex = "^[0-9][0-9][A-Za-z][A-Za-z][A-Za-z]"
			+ "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][A-Za-z][A-Za-z0-9]"
			+ "[A-Za-z0-9][A-Za-z0-9]$";
		Pattern pattern = Pattern.compile(regex);
	
		  Matcher matcher = pattern.matcher(document.getOrigCgstin());
		List<ProcessingResult> errors = new ArrayList<>();
		if (document.getOrigCgstin() != null && !document.getOrigCgstin().isEmpty()) {
			if(!matcher.matches()){
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.ORG_CGSTN);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER063",
						"Invalid Original Customer GSTIN",
						location));
		}
			
				
			}
		
		
	
		return errors;
}
}


