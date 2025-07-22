package com.ey.advisory.app.services.strcutvalidation.item;

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

public class ItcEntitlementValidationRule implements ValidationRule {

	private static final List<String> ITC_entitlement_IMPORTS = ImmutableList.of("y",
			"n", "Y", "N");

	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		if (obj != null) {
			if (!(ITC_entitlement_IMPORTS.contains(obj.toString()))) {

				errorLocations.add(GSTConstants.ITC_ENTITLEMENT);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER1650",
						"Invalid ITC Entitlement flag", location));
			}

		}

		return errors;
	}

}