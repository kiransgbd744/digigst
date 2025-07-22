package com.ey.advisory.app.services.strcutvalidation.sales;

import static com.ey.advisory.common.FormatValidationUtil.isDecimal;
import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

public class SGSTAmountValidationRule implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {

		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		Object docTypeObj = (String) row[10];
		Object supplyTypeObj = (String) row[11];
		if (obj != null) {
			
			// First check if the input object is a valid decimal number.
			if (!isDecimal(obj)) {
				errorLocations.add(GSTConstants.SGST_AMOUNT);
				TransDocProcessingResultLoc location 
				                   = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER081",
						"Invalid SGST/UT GST Amount", location));
				// if it's not a number, then we can return the errors
				// immediately
				return errors;
			}
			BigDecimal result = new BigDecimal(obj.toString());
			if ((docTypeObj != null && (docTypeObj.toString() != "CR"
					|| docTypeObj.toString() != "RCR"
					|| docTypeObj.toString() != "RFV"))
					
					|| (supplyTypeObj != null
							&& supplyTypeObj.toString() != "CAN")) {
				if (result.compareTo(BigDecimal.ZERO) < 0) {
					errorLocations.add(GSTConstants.SGST_AMOUNT);
					TransDocProcessingResultLoc location 
					                   = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER081",
							"Invalid SGST/UT GST Amount", location));
				}
			}
		}

		return errors;
	}

}
