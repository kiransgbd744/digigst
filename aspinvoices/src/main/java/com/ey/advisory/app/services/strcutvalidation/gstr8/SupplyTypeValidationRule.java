package com.ey.advisory.app.services.strcutvalidation.gstr8;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

public class SupplyTypeValidationRule implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(int idx, Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (!isPresent(obj)) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.SUPPLY_TYPE);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER8007",
					"Invalid SupplyType", location));
			return errors;
		}

		if (obj.toString().trim().length() > 5) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.SUPPLY_TYPE);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER8007",
					"Invalid SupplyType", location));
			return errors;
		}
		String supplyType = obj.toString().trim();
		if (!(supplyType.equalsIgnoreCase("TAX")
				|| supplyType.equalsIgnoreCase("CAN"))) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.SUPPLY_TYPE);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER8007",
					"Invalid SupplyType", location));
			return errors;
		}
		return errors;
	}

}
