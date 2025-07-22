package com.ey.advisory.app.services.strcutvalidation.inward;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

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

public class SuppliergstinValidationRule implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(int idx,Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (!isPresent(obj)) return errors;
			
		if(GSTConstants.URP.equalsIgnoreCase(obj.toString().trim())) return errors;
			if (obj.toString().trim().length() == 15
					|| obj.toString().trim().length() == 10) {
				// nothing
			} else {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.SGSTIN);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				
				errors.add(new ProcessingResult(APP_VALIDATION, "ER1052",
						"Invalid Supplier GSTIN.", location));
				return errors;
			}

			if (obj.toString().trim().length() == 15) {
				String regex = "^[0-9][0-9][A-Za-z][A-Za-z][A-Za-z]"
						+ "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][A-Za-z][A-Za-z0-9]"
						+ "[A-Za-z0-9][A-Za-z0-9]$";
				
				String regex1 = "^[0-9][0-9][A-Za-z][A-Za-z][A-Za-z]"
						+ "[A-Za-z][0-9][0-9][0-9][0-9][0-9][A-Za-z][A-Za-z0-9]"
						+ "[A-Za-z0-9][A-Za-z0-9]$";
					Pattern pattern = Pattern.compile(regex);
				
					Pattern pattern1 = Pattern.compile(regex1);
					
					  Matcher matcher = pattern.matcher(obj.toString().trim());
					  Matcher matcher1 = pattern1.matcher(obj.toString().trim());
				if (matcher.matches() || matcher1.matches()) {
					
				}else{
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.SGSTIN);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				
				errors.add(new ProcessingResult(APP_VALIDATION, "ER1052",
						"Invalid Supplier GSTIN.", location));
				return errors;
			}
			}
			if (obj.toString().trim().length() == 10) {
				String regex = "^[A-Za-z][A-Za-z][A-Za-z]"
						+ "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][A-Za-z]$";
						
				
				String regex1 = "^[A-Za-z][A-Za-z][A-Za-z]"
						+ "[A-Za-z][0-9][0-9][0-9][0-9][0-9][A-Za-z]$";
					Pattern pattern = Pattern.compile(regex);
				
					Pattern pattern1 = Pattern.compile(regex1);
					
					  Matcher matcher = pattern.matcher(obj.toString().trim());
					  Matcher matcher1 = pattern1.matcher(obj.toString().trim());
				if (matcher.matches() || matcher1.matches()) {
					
				}else{
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.SGSTIN);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				
				errors.add(new ProcessingResult(APP_VALIDATION, "ER1052",
						"Invalid Supplier GSTIN.", location));
				return errors;
			}
			}
		return errors;

	}

}
