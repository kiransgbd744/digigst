package com.ey.advisory.app.services.structuralvalidation.gstr6;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ey.advisory.app.services.strcutvalidation.outward.ValidationRule;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.google.common.collect.ImmutableList;

public class Gstr6OEligibilityValidtion implements ValidationRule {

	
	@Override
	public List<ProcessingResult> isValid(int idx,Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();

		if (!isPresent(obj)){
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.ELIGIBILITY_INDICATOR);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER3025",
					"Invalid Eligibility Indicator.", location));
			return errors;
			
		}
if(obj.toString().length()>2){
	Set<String> errorLocations = new HashSet<>();
	errorLocations.add(GSTConstants.ELIGIBILITY_INDICATOR);
	TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
			idx, errorLocations.toArray());
	errors.add(new ProcessingResult(APP_VALIDATION, "ER3025",
			"Invalid Eligibility Indicator.", location));
	return errors;
}
		
		return errors;

	}
}
