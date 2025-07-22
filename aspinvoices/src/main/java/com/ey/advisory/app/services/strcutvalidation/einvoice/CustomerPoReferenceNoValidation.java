/*package com.ey.advisory.app.services.strcutvalidation.einvoice;

import static com.ey.advisory.app.data.entities.client.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.FormatValidationUtil.isPresent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.data.entities.client.GSTConstants;
import com.ey.advisory.app.services.strcutvalidation.outward.ValidationRule;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
*//**
 * 
 * @author Mahesh.Golla
 *
 *//*
public class CustomerPoReferenceNoValidation implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(int idx, Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		
		if (!isPresent(obj)) {
			return errors;
		}
		
		if (obj.toString().trim().length() > 20) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.CUSTOMER_PO_REFERENCE_NUMBER);
			TransDocProcessingResultLoc location = 
					new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER10108",
					          "Invalid Customer POReference Number.", location));
			return errors;
		}
		return errors;
	}

}
*/