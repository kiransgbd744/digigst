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
public class NetNumber implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		if (!isPresent(obj)) {
			errorLocations.add(GSTConstants.NET_NUM);
			TransDocProcessingResultLoc location =
					new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER5213",
					"Invalid Net number.", location));
			return errors;
		}
		String can = obj.toString().trim();
		if (!can.matches("[0-9]+") || can.length() > 10) {
			errorLocations.add(GSTConstants.NET_NUM);
			TransDocProcessingResultLoc location =
					new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER5213",
					"Invalid Net number.", location));
			return errors;
		} else {
			Long cancelled = Long.valueOf(can);
			if (cancelled < 0) {
				errorLocations.add(GSTConstants.NET_NUM);
				TransDocProcessingResultLoc location =
						new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER5213",
						"Invalid Net number.", location));
				return errors;
			}
		}
		return errors;
	}

}
