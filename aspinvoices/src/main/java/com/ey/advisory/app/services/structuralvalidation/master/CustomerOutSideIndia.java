package com.ey.advisory.app.services.structuralvalidation.master;

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

public class CustomerOutSideIndia implements ValidationRule {
	private static final List<String> OUTSIDEINDIA = ImmutableList.of("Y", "N",
			"n", "y");

	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		String outsideIndia = null;
		if (obj != null) {
			outsideIndia = obj.toString();
			if (outsideIndia != null) {
				if (outsideIndia.length() > 1
						|| !OUTSIDEINDIA.contains(outsideIndia)) {
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.OUTSIDEINDIA);
					TransDocProcessingResultLoc location = 
							new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, " ER1609",
							"Invalid Outside India Flag.", location));
				}
			}
		}
		return errors;
	}
}