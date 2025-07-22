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
public class SerialNOValidator implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		if (!isPresent(obj)) {
			errorLocations.add(GSTConstants.SERIAL_NUMBER);
			TransDocProcessingResultLoc location = 
					new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER5205",
					"Invalid Serial No.", location));
			return errors;
		}
		String serialNumber = obj.toString().trim();
		if (!serialNumber.matches("[0-9]+")) {
			errorLocations.add(GSTConstants.SERIAL_NUMBER);
			TransDocProcessingResultLoc location =
					new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER5205",
					"Invalid Serial No.", location));
			return errors;
		} else if (serialNumber.matches("[0-9]+")) {
			int i = Integer.parseInt(serialNumber.trim());
			if (serialNumber.length() > 4 || i > 12) {
				errorLocations.add(GSTConstants.SERIAL_NUMBER);
				TransDocProcessingResultLoc location = 
						new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER5206",
						"Serial No. should be  between 1-12.",
						location));
				return errors;
			}
		}
		return errors;
	}

}
