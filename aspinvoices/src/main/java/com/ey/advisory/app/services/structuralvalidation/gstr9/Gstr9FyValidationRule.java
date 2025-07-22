package com.ey.advisory.app.services.structuralvalidation.gstr9;

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
 * @author Anand3.M
 *
 */
public class Gstr9FyValidationRule implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (!isPresent(obj)) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.FY);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER6168",
					"Financial Year cannot be left blank", location));
			return errors;
		}
		if (!obj.toString().trim().matches("[0-9]{4}[-][0-9]{4}")
				|| obj.toString().trim().length() != 9) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.FY);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER6169",
					"Invalid Financial year", location));
			return errors;
		}
		String firstDate = obj.toString().trim().substring(0, 4);
		String lastDate = obj.toString().trim().substring(5);
		int x = Integer.parseInt(firstDate) + 1;
		int y = Integer.parseInt(lastDate);

		if (x != y) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.FY);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER6169",
					"Invalid Financial Year.", location));
			return errors;
		}

		return errors;
	}
}

/*
 * String taxPeriod = obj.toString().trim(); if (!taxPeriod.matches("[0-9]+") ||
 * taxPeriod.length() != 9) { errors = createValErrors(errors); return errors; }
 * 
 * return errors; }
 * 
 * private List<ProcessingResult> createValErrors( List<ProcessingResult>
 * errors) { Set<String> errorLocations = new HashSet<>();
 * errorLocations.add(GSTConstants.FY); TransDocProcessingResultLoc location =
 * new TransDocProcessingResultLoc( null, errorLocations.toArray());
 * errors.add(new ProcessingResult(APP_VALIDATION, "ER6169",
 * "Invalid Tax Period", location)); return errors; }
 */
