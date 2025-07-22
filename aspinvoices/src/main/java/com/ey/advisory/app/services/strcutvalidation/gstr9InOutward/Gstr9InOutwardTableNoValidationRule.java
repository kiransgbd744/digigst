package com.ey.advisory.app.services.strcutvalidation.gstr9InOutward;

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

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Slf4j
public class Gstr9InOutwardTableNoValidationRule implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (!isPresent(obj)) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.TABLE_NUMBER);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());

			errors.add(new ProcessingResult(APP_VALIDATION, "ER6153",
					"Table Number cannot be left blank.", location));
			return errors;
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Gstr9InOutwardTableNoValidationRule: [{}]",
					obj.toString());
		}
		if (obj != null && obj.toString().trim().length() > 5) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.TABLE_NUMBER);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER6154",
					"Invalid Table Number.", location));
			return errors;
		}
		return errors;
	}
}
