package com.ey.advisory.app.services.structuralvalidation.master;

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

public class CustomerRecipientType implements ValidationRule {
	private static final List<String> RECIPIENT_TYPES = ImmutableList.of("U",
			"C", "I", "G", "N", "O", "S", "R");

	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {

		List<ProcessingResult> errors = new ArrayList<>();
		if (obj != null) {
			String recType = obj.toString();
			if (recType != null) {
				if (recType.length() > 30
						|| !RECIPIENT_TYPES.contains(recType)) {
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.RECIPIENTTYPE);
					TransDocProcessingResultLoc location = 
							new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER1608",
							"Invalid Recipient Type.", location));
					return errors;
				}
			}
		}

		return errors;
	}
}