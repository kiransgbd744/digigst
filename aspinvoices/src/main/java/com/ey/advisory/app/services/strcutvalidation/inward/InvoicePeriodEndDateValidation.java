package com.ey.advisory.app.services.strcutvalidation.inward;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;
import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.services.strcutvalidation.outward.ValidationRule;
import com.ey.advisory.common.DateFormatForStructuralValidatons;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

public class InvoicePeriodEndDateValidation implements ValidationRule {

	@Override
	public List<ProcessingResult> isValid(int idx, Object obj, Object[] row,
			TabularDataLayout layout) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (!isPresent(obj)) return errors;
		
		LocalDate date = DateFormatForStructuralValidatons.parseObjToDate(
				                                        obj.toString().trim());
		if (date == null ) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.INVOICE_PERIOD_ENDDATE);
			TransDocProcessingResultLoc location = 
					new TransDocProcessingResultLoc(
					idx, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER10094",
					          "Invalid Invoice Period End Date.", location));
			return errors;
		}
		
		return errors;
	}

}
