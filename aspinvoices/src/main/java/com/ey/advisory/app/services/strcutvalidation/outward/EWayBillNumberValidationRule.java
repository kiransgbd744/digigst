package com.ey.advisory.app.services.strcutvalidation.outward;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

public class EWayBillNumberValidationRule implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(int idx, Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();

		if (!isPresent(obj))
			return errors;
		if (obj.toString().length() > 12) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.EWay_BillNo);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER0108",
					"Invalid E-Way Bill Number", location));
			return errors;
		}
		
			/*String docNo = obj.toString().trim();
			String regex = "^[a-zA-Z0-9/-]*$";
			Pattern pattern = Pattern.compile(regex);

			Matcher matcher = pattern.matcher(docNo);
			if (!matcher.matches()) {

				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.EWay_BillNo);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER0108",
						"Invalid E-Way Bill Number", location));
				return errors;
			}*/
		

		return errors;
	}

}
