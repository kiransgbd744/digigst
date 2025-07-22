package com.ey.advisory.app.services.validation.gstr9.inoutward;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.RETURN_PREIOD;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.ey.advisory.app.data.entities.client.Gstr9OutwardInwardAsEnteredEntity;
import com.ey.advisory.app.services.validation.B2csBusinessRuleValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

public class Gstr9InOutwardFyValidation implements
		B2csBusinessRuleValidator<Gstr9OutwardInwardAsEnteredEntity> {

	@Override
	public List<ProcessingResult> validate(
			Gstr9OutwardInwardAsEnteredEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		String fy = document.getFy() != null && !document.getFy().isEmpty()
				? "0103" + document.getFy().trim().substring(5) : null;

		if (fy != null) {
			List<String> errorLocations = new ArrayList<>();
			DateTimeFormatter formatter = DateTimeFormatter
					.ofPattern("ddMMyyyy");
			LocalDate pregst = LocalDate.of(2017, 07, 01);
			// Calculate the last day of the month.
			LocalDate returnPeriod = LocalDate.parse(fy, formatter);
			if (returnPeriod.compareTo(pregst) < 0) {
				errorLocations.add(RETURN_PREIOD);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER6152",
						"FY cannot be before 072017.", location));
				return errors;

			}
		}
		return errors;
	}

}
