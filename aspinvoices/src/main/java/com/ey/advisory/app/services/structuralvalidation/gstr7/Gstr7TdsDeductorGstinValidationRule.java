package com.ey.advisory.app.services.structuralvalidation.gstr7;

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

public class Gstr7TdsDeductorGstinValidationRule implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(int idx, Object obj, Object[] row, TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (!isPresent(obj)) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.TDS_DEDUCTOR_GSTIN);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(null, errorLocations.toArray());

			errors.add(new ProcessingResult(APP_VALIDATION, "ER2003", "TDS Deductor GSTIN cannot be left balnk",
					location));
			return errors;
		}

		String regex = "^[0-9][0-9][A-Za-z][A-Za-z][A-Za-z]"
				+ "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][A-Za-z][A-Za-z0-9]"
				+ "[A-Za-z0-9][A-Za-z0-9]$";

		String regex1 = "^[0-9][0-9][A-Za-z][A-Za-z][A-Za-z]"
				+ "[A-Za-z][0-9][0-9][0-9][0-9][0-9][A-Za-z][A-Za-z0-9]"
				+ "[A-Za-z0-9][A-Za-z0-9]$";
		
		String regex2 = "^[0-9][0-9][0-9][0-9][A-Za-z]"
				+ "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][0-9][UOuo]"
				+ "[Nn][A-Za-z0-9]$";

		String regex3 = "^[0-9][0-9][0-9][0-9][A-Za-z]"
				+ "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][0-9][Nn]"
				+ "[Rr][A-Za-z0-9]$";
		
			Pattern pattern = Pattern.compile(regex);

			Pattern pattern1 = Pattern.compile(regex1);
			
			Pattern pattern2 = Pattern.compile(regex2);

			Pattern pattern3 = Pattern.compile(regex3);
			
			  Matcher matcher = pattern.matcher(obj.toString().trim());
			  Matcher matcher1 = pattern1.matcher(obj.toString().trim());
			  Matcher matcher2 = pattern2.matcher(obj.toString().trim());
			  Matcher matcher3 = pattern3.matcher(obj.toString().trim());
		if (matcher.matches() || matcher1.matches() 
				|| matcher2.matches() || matcher3.matches()) {
		}else{
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.TDS_DEDUCTOR_GSTIN);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(null, errorLocations.toArray());

			errors.add(new ProcessingResult(APP_VALIDATION, "ER2004", "Invalid TDS Deductor GSTIN.", location));
			return errors;
		}

		/*if (obj.toString().trim().length() != 15) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.TDS_DEDUCTOR_GSTIN);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(null, errorLocations.toArray());

			errors.add(new ProcessingResult(APP_VALIDATION, "ER2004", "Invalid TDS Deductor GSTIN.", location));
			return errors;
		}
*/
		return errors;

	}

}
