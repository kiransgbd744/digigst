package com.ey.advisory.app.services.validation.purchase;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.DOC_DATE;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

import com.google.common.base.Strings;

/**
 * 
 * @author Siva.Nandam
 *
 */
public class DocDateTaxPeriodValidator
		implements DocRulesValidator<InwardTransDocument> {

	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();

		// Get the document date from the document and convert it to a
		// LocalDate object.
		LocalDate docDate = document.getDocDate();
		if (docDate == null)
			return errors;
		if (Strings.isNullOrEmpty(document.getTaxperiod()))
			return errors;
		String dateYear = docDate.toString().substring(0, 4);
		String datemonth = docDate.toString().substring(5, 7);
		String returnDate = document.getTaxperiod().substring(2, 6);
		String returnMonth = document.getTaxperiod().substring(0, 2);
		if (dateYear.compareTo(returnDate) > 0) {
			List<String> errorLocations = new ArrayList<>();
			errorLocations.add(DOC_DATE);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER1045",
					"Document Date should not be greater than "
							+ "last date of return period.",
					location));
			return errors;
		}
		if (dateYear.compareTo(returnDate) == 0
				&& datemonth.compareTo(returnMonth) > 0) {
			List<String> errorLocations = new ArrayList<>();
			errorLocations.add(DOC_DATE);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER1045",
					"Document Date should not be greater than "
							+ "last date of return period.",
					location));
			return errors;

		}

		return errors;
	}

}
