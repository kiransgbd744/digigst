package com.ey.advisory.app.services.strcutvalidation.refund;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GSTConstants.*;

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
 * @author Mahesh.Golla
 *
 */
public class RefundsDescValidationRule implements ValidationRule {
	private static final List<String> DESCRIPTIONS = ImmutableList
			.of("IGST", "CGST", "SGST", "CESS");

	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {

		List<ProcessingResult> errors = new ArrayList<>();

		if (!isPresent(obj)) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.DESCRIPTION);
			TransDocProcessingResultLoc location =
					new TransDocProcessingResultLoc(
					null, errorLocations.toArray());

			errors.add(new ProcessingResult(APP_VALIDATION, "ER1882",
					"Invalid Description", location));
			return errors;
		}
		String des = obj.toString().trim().replaceAll("\\s+","").toUpperCase();
		if (!DESCRIPTIONS.contains(des)) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.DESCRIPTION);
			TransDocProcessingResultLoc location = 
					new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER1882",
					"Invalid Description", location));
			return errors;
		} 
		return errors;
	}
}
