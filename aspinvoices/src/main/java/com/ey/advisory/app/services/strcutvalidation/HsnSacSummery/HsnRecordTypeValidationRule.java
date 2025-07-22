/**
 * 
 */
package com.ey.advisory.app.services.strcutvalidation.HsnSacSummery;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import com.ey.advisory.app.services.strcutvalidation.sales.ValidationRule;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

/**
 * @author vishal.verma
 *
 */
public class HsnRecordTypeValidationRule implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();

		// Check for a valid 'RecordType' (B2B, B2C)
		if (isPresent(obj)) {
			String response = obj.toString().trim();
			if (Stream.of("B2B", "B2C").noneMatch(response::equalsIgnoreCase)) {
				errorLocations.add(GSTConstants.RECORD_TYPE);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER1277",
						"Invalid RecordType", location));
				return errors;
			}
		} else {

			if (isPresent(row[2])) {
				String taxPeriod = row[2].toString().trim();
				String referencePeriod = "012025"; // January 2025

				// If taxPeriod is earlier than 012025, return null (no errors)
				if (taxPeriod.compareTo(referencePeriod) < 0) {
					return null;
				} else {
					errorLocations.add(GSTConstants.RECORD_TYPE);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER1277",
							"Invalid RecordType", location));
					return errors;
				}
			}
		}

		return errors;
	}
}
