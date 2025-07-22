package com.ey.advisory.app.services.strcutvalidation.Isd;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import org.apache.commons.lang3.StringUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.services.strcutvalidation.outward.ValidationRule;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

public class ItemSerialNoValidationRule implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(int idx, Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();

		if (!isPresent(obj)) {

			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.LINE_NO);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER0046",
					" Line Number cannot be left Blank.", location));
			return errors;
		}
		
		
		if (obj.toString().trim().length() > 6) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.LINE_NO);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER0047",
					"Invalid Line Number", location));
			return errors;

		}
		if(!StringUtils.isNumeric(obj.toString().trim())){
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.LINE_NO);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER0047",
					"Invalid Line Number", location));
			return errors;
		}
		
		return errors;
	}
}
