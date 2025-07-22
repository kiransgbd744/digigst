package com.ey.advisory.app.services.strcutvalidation.cewb;

import static com.ey.advisory.common.FormatValidationUtil.isDecimal;
import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.services.strcutvalidation.sales.ValidationRule;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

/**
 * 
 * @author Mahesh.Golla
 *
 */
public class CewbTransDocNumber implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();

		if (!isPresent(obj))
			return errors;

		if (!isDecimal(obj.toString().trim())) {
			return errors;
		}
		if (obj.toString() != null && obj.toString().trim().length() > 15) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.TRANS_DOC_NO);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER6124",
					"Invalid Transport Documnet number.", location));
			return errors;
		}
		return errors;
	}
}
