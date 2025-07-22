package com.ey.advisory.app.services.strcutvalidation.b2c;

import static com.ey.advisory.common.FormatValidationUtil.isDecimal;
import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.services.strcutvalidation.sales.ValidationRule;
import com.ey.advisory.common.NumberFomatUtil;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

/**
 * 
 * @author Mahesh.Golla
 *
 */

public class B2cEComValueOfSRetValidationRule implements ValidationRule {
	private static final String ECOM_VALUEOF_SUPRET = "EcomValoueOfSupplierReturn";

	@Override
	public List<ProcessingResult> isValid(Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();

		if (!isPresent(obj))
			return errors;
		if (!isDecimal(obj)) {
			errorLocations.add(ECOM_VALUEOF_SUPRET);
			TransDocProcessingResultLoc location = 
					new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER0226",
					" Invalid E-Com Value Of Supplies Returned", location));
			// if it's not a number, then we can return the errors
			// immediately
			return errors;
		}
		boolean isValid = NumberFomatUtil.isValidDec(obj.toString().trim());
		if (!isValid) {
			errorLocations.add(ECOM_VALUEOF_SUPRET);
			TransDocProcessingResultLoc location = 
					new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER0226",
					" Invalid E-Com Value Of Supplies Returned", location));
			// if it's not a number, then we can return the errors
			// immediately
			return errors;
		}
		BigDecimal result = new BigDecimal(obj.toString().trim());
		if (result.compareTo(BigDecimal.ZERO) < 0) {
			errorLocations.add(ECOM_VALUEOF_SUPRET);
			TransDocProcessingResultLoc location =
					new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER0226",
					"  Invalid E-Com Value Of Supplies Returned.", location));
			return errors;

		}

		return errors;
	}

}
