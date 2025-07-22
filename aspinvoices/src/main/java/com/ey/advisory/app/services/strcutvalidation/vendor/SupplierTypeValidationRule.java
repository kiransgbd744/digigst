package com.ey.advisory.app.services.strcutvalidation.vendor;

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

public class SupplierTypeValidationRule implements ValidationRule {

	private static final List<String> SUPPLIER_IMPORTS = ImmutableList.of("U",
			"C", "I", "G", "N", "O", "S", "R");

	@Override
	public List<ProcessingResult> isValid(int idx, Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (obj != null) {
			String recType = obj.toString();
			if (recType != null) {
				if (recType.length() > 30
						|| !SUPPLIER_IMPORTS.contains(recType)) {
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.SUPPLIER_TYPE);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(GSTConstants.APP_VALIDATION,
							"ER1631", "Invalid Supplier Type.", location));
					return errors;
				}
			}

		}
		return errors;
	}

}
