/*package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.app.data.entities.client.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ey.advisory.app.data.entities.client.GSTConstants;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

public class StOriginalDocNunValidator 
implements DocRulesValidator<OutwardTransDocument> { 



@Override
public List<ProcessingResult> validate(OutwardTransDocument document,
		ProcessingContext context) {
	
	  String regex= "^[a-zA-Z0-9_/]*$";
	  Pattern pattern = Pattern.compile(regex);
	  
	  Matcher matcher = pattern.matcher(document.getOrigDocNo());
	  List<ProcessingResult> errors = new ArrayList<>();
if(document.getOrigDocNo()!=null && !document.getOrigDocNo().isEmpty()){
	//OriginalDocNum
	if(!matcher.matches()){
		Set<String> errorLocations = new HashSet<>();
		errorLocations.add(GSTConstants.ORIGINAL_DOC_NO);
		TransDocProcessingResultLoc location = new 
				TransDocProcessingResultLoc(
				null, errorLocations.toArray());
		errors.add(new ProcessingResult(APP_VALIDATION, "ER057",
				"Invalid Original Document Number "
					+ "or Original Document Number is missing",
				location));	
	}
}
return errors;
		
}
}*/