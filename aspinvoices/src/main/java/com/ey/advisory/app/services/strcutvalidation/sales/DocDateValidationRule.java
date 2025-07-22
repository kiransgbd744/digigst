package com.ey.advisory.app.services.strcutvalidation.sales;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

public class DocDateValidationRule implements ValidationRule {
	
	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row, 
			                             TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		if(obj==null){
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.DOC_DATE);
			TransDocProcessingResultLoc location 
			                     = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER056",
					"Invalid Document Date",
					location));	
		}
		if (obj!=null) {
		LocalDate date=	DateUtil.parseObjToDate(obj);
		if(date==null){
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.DOC_DATE);
			TransDocProcessingResultLoc location 
			                     = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER056",
					"Invalid Document Date",
					location));		
		}
		}
			
		
	
		return errors;
	}
	
}
