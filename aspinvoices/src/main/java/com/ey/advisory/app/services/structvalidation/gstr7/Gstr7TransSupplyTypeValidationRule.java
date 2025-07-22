package com.ey.advisory.app.services.structvalidation.gstr7;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.app.caches.EInvoiceSupplyTypeCache;
import com.ey.advisory.app.services.strcutvalidation.outward.ValidationRule;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

public class Gstr7TransSupplyTypeValidationRule implements ValidationRule {

	@Autowired
	@Qualifier("DefaultEInvoiceSupplyTypeCache")
	private EInvoiceSupplyTypeCache supplyTypeCache;

	@Override
	public List<ProcessingResult> isValid(int idx, Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (!isPresent(obj)) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.SUPPLY_TYPE);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER63025",
					"Supply Type cannot be left blank.", location));
			return errors;
		}

		// Validate if the supply type is either "TAX" or "CAN"
		String supplyType = trimAndConvToUpperCase(obj.toString().trim());
		if (!supplyType.equals("TAX") && !supplyType.equals("CAN")) {

			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.SUPPLY_TYPE);

			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER63002",
					"Invalid Supply Type.", location));
		}

		return errors;
	}

}
