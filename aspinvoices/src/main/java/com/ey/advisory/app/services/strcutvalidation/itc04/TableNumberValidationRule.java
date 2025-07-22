package com.ey.advisory.app.services.strcutvalidation.itc04;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.services.strcutvalidation.outward.ValidationRule;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.google.common.collect.ImmutableList;

/**
 * 
 * @author Mahesh.Golla
 *
 */
public class TableNumberValidationRule implements ValidationRule {

	private static final List<String> TABLE_NUMBER = ImmutableList.of(
			GSTConstants.TABLE_NUMBER_4, GSTConstants.TABLE_NUMBER_5A,
			GSTConstants.TABLE_NUMBER_5B, GSTConstants.TABLE_NUMBER_5C);

	@Override
	public List<ProcessingResult> isValid(int idx, Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (!isPresent(obj)) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.TABLE_NUMBER);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER5801",
					"Table Number cannot be left Blank.", location));
			return errors;
		}
		if (obj.toString().trim().length() > 5) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.TABLE_NUMBER);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER5802",
					"Invalid TableNumber.", location));
			return errors;
		}

		if (!TABLE_NUMBER.contains(obj.toString().trim())) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.TABLE_NUMBER);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER5802",
					"Invalid TableNumber.", location));
			return errors;
		}

		return errors;
	}
}
