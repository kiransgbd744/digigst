package com.ey.advisory.app.services.structuralvalidation.gstr7;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.ey.advisory.app.data.entities.client.Gstr7AsEnteredTdsEntity;
import com.ey.advisory.common.BusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

import com.google.common.base.Strings;

public class Gstr7DocDateValidation
		implements BusinessRuleValidator<Gstr7AsEnteredTdsEntity> {

	@Override
	public List<ProcessingResult> validate(Gstr7AsEnteredTdsEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();
		String returnPeriod = document.getReturnPeriod();
		LocalDate docDate = document.getDocDate();
		if (Strings.isNullOrEmpty(returnPeriod) || docDate == null)
			return errors;

		String tax = "31" + returnPeriod.trim();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");

		// Calculate the last day of the month.
		LocalDate returnPeriod1 = LocalDate.parse(tax, formatter);
		if (docDate.compareTo(returnPeriod1) > 0) {
			errorLocations.add(GSTConstants.DOC_DATE);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER1277",
					"Document Date should not be greater than last date of return period ",
					location));
			return errors;

		}

		return errors;
	}

}
