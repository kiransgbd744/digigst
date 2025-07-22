package com.ey.advisory.app.services.businessvalidation.gstr3b;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.RETURN_PREIOD;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.ey.advisory.app.data.entities.client.Gstr3bExcelEntity;
import com.ey.advisory.app.services.validation.BusinessRuleValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * 
 * @author Mahesh.Golla
 *
 */

public class Gstr3bReturnPeriod
		implements BusinessRuleValidator<Gstr3bExcelEntity> {

	@Override
	public List<ProcessingResult> validate(Gstr3bExcelEntity document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();
		if (document.getRetPeriod() != null
				&& !document.getRetPeriod().isEmpty()) {
			String tax = "01" + document.getRetPeriod().trim();
			DateTimeFormatter formatter = DateTimeFormatter
					.ofPattern("ddMMyyyy");
			LocalDate newDate = LocalDate.of(2022, 8, 01);
			LocalDate pregst = LocalDate.of(2017, 07, 01);
			// Calculate the last day of the month.
			LocalDate returnPeriod = LocalDate.parse(tax, formatter);
			if (returnPeriod.compareTo(pregst) < 0) {
				errorLocations.add(RETURN_PREIOD);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER6104",
						"Return Period cannot be before 072017", location));

			}
			if (document.getSerialNo().equalsIgnoreCase("9")
					|| document.getSerialNo().equalsIgnoreCase("10")) {
				if (returnPeriod.compareTo(newDate) < 0) {
					errorLocations.add(RETURN_PREIOD);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER6104",
							"Return Period cannot be before 082022", location));

				}
			}
		}
		return errors;
	}

}