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
import com.google.common.collect.ImmutableList;

/**
 * 
 * @author Anand3.M
 *
 */
public class Gstr9TableValidationRule implements ValidationRule {
	private static final List<String> TDS_TDSA = ImmutableList.of("17", "18");

	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (!isPresent(obj)) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.TABLE_NUMBER);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER6170",
					"Table Number cannot be left blank", location));
			return errors;
		}
		if (obj != null && obj.toString().trim().length() > 5) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.TABLE_NUMBER);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER6171",
					"Invalid TableNumber.", location));
			return errors;

			/*
			 * String tableNumber = obj.toString().trim(); int tableNo =
			 * Integer.parseInt(tableNumber); if (tableNo != 17 || tableNo !=
			 * 18) { Set<String> errorLocations = new HashSet<>();
			 * errorLocations.add(GSTConstants.TABLE_NUMBER);
			 * TransDocProcessingResultLoc location = new
			 * TransDocProcessingResultLoc( null, errorLocations.toArray());
			 * errors.add(new ProcessingResult(APP_VALIDATION, "ERXXXX",
			 * "Invalid TableNumber", location)); return errors;
			 */
		}
		if (!TDS_TDSA.contains(obj.toString().toUpperCase().trim())) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.TABLE_NUMBER);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER6171",
					"Invalid TableNumber.", location));
			return errors;
		}

		return errors;
	}
}
