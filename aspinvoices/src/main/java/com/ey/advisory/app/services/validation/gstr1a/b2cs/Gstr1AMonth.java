package com.ey.advisory.app.services.validation.gstr1a.b2cs;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.MONTH;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAsEnteredB2csEntity;
import com.ey.advisory.app.services.validation.B2csBusinessRuleValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
public class Gstr1AMonth
		implements B2csBusinessRuleValidator<Gstr1AAsEnteredB2csEntity> {

	@Override
	public List<ProcessingResult> validate(Gstr1AAsEnteredB2csEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();
		if (document.getMonth() != null && !document.getMonth().isEmpty()) {
			if (document.getReturnPeriod() != null
					&& !document.getReturnPeriod().isEmpty()) {
				String month = "01" + document.getMonth().trim();
				String returnPeriod = "01" + document.getReturnPeriod().trim();
				DateTimeFormatter formatter = DateTimeFormatter
						.ofPattern("ddMMyyyy");

				// Calculate the last day of the month.
				LocalDate month1 = LocalDate.parse(month, formatter);
				LocalDate returnPeriod1 = LocalDate.parse(returnPeriod,
						formatter);
				LocalDate pregst = LocalDate.of(2017, 07, 01);
				if (month1.compareTo(returnPeriod1) > 0) {
					errorLocations.add(MONTH);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER5009",
							"Month should be before Return Period", location));
					return errors;
				}

				else if (month1.compareTo(pregst) < 0) {
					errorLocations.add(MONTH);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER5008",
							"Month should start from 072017 ", location));
					return errors;
				}
			}
		}
		return errors;
	}

}
