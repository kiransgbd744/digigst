package com.ey.advisory.app.services.strcutvalidation.InvoiceFile;

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
public class TotalNumber implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		if (!isPresent(obj)) {
			errorLocations.add(GSTConstants.TOTAL_NUM);
			TransDocProcessingResultLoc location = 
					new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER5211",
					"Invalid Total Number.", location));
			return errors;
		}
		String totalNumber = obj.toString().trim();
		if (!totalNumber.matches("[0-9]+") || totalNumber.length() > 10) {
			errorLocations.add(GSTConstants.TOTAL_NUM);
			TransDocProcessingResultLoc location = 
					new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER5211",
					"Invalid Total Number.", location));
			return errors;
		} else {
			Long total = Long.valueOf(totalNumber);
			if (total < 0) {
				errorLocations.add(GSTConstants.TOTAL_NUM);
				TransDocProcessingResultLoc location = 
						new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER5211",
						"Invalid Total Number.", location));
				return errors;
			}
		}
		return errors;
	}

}
