package com.ey.advisory.app.services.strcutvalidation.outward;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.google.common.collect.ImmutableList;

public class OutsideIndiaValidationRule implements ValidationRule {

	private static final List<String> INDIA_IMPORTS = ImmutableList.of("Y", "N",
			"y", "n");

	@Override
	public List<ProcessingResult> isValid(int idx, Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();

		String outsideIndia = null;
		if (obj != null) {
			outsideIndia = obj.toString();
			if (outsideIndia != null) {
				if (outsideIndia.length() > 1
						|| !INDIA_IMPORTS.contains(outsideIndia)) {
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.OUTSIDEINDIA);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(GSTConstants.APP_VALIDATION,
							"ER1632", "Invalid outside of india.", location));
					return errors;

				}
			}
		}
		return errors;
	}

}
