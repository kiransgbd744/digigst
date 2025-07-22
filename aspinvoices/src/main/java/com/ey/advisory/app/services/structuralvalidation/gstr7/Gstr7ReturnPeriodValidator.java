package com.ey.advisory.app.services.structuralvalidation.gstr7;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.RETURN_PERIOD;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.ey.advisory.app.data.entities.client.Gstr7AsEnteredTdsEntity;
import com.ey.advisory.common.BusinessRuleValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * 
 * @author Anand3.M
 *
 */
public class Gstr7ReturnPeriodValidator implements BusinessRuleValidator<Gstr7AsEnteredTdsEntity> {

	@Override
	public List<ProcessingResult> validate(Gstr7AsEnteredTdsEntity document, ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();
		String returnPeriod = document.getReturnPeriod();
		if (returnPeriod != null && !returnPeriod.isEmpty()) {
			
			String tax = "01" + returnPeriod.trim();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
			LocalDate pregst = LocalDate.of(2017, 07, 01);
			// Calculate the last day of the month.
			LocalDate returnPeriod1 = LocalDate.parse(tax, formatter);
			if (returnPeriod1.compareTo(pregst) < 0) {
				errorLocations.add(RETURN_PERIOD);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER2026", "Return Period cannot be before 072017",
						location));
				return errors;

			}
		}
		return errors;
	}

}
