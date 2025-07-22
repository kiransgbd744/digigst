package com.ey.advisory.app.services.strcutvalidation.einvoice;

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

public class CustomerTanValidation implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(int idx,Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (!isPresent(obj)) {
			
			return errors;
		}
	
		
			String docNo = obj.toString().trim();
			String regex = "^[a-zA-Z0-9]*$";
			Pattern pattern = Pattern.compile(regex);

			Matcher matcher = pattern.matcher(docNo);
			if (docNo.trim().length() > 10 || !matcher.matches()) {

				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.CUSTOMER_TAN);
				TransDocProcessingResultLoc location = 
						new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER10134",
						          "Invalid Customer TAN.", location));
				return errors;
			}
		return errors;
	}

}
