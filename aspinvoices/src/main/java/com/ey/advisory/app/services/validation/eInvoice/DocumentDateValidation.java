package com.ey.advisory.app.services.validation.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.DOC_DATE;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * @author Siva.Nandam
 *
 */
public class DocumentDateValidation
		implements DocRulesValidator<OutwardTransDocument> {

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();

		// Get the document date from the document and convert it to a
		// LocalDate object.
		if (document.getDocDate() != null) {
			LocalDate docDate = document.getDocDate();
			LocalDate presentDate = LocalDate.now();

			if (docDate.compareTo(presentDate) > 0) {
				List<String> errorLocations = new ArrayList<>();
				errorLocations.add(DOC_DATE);
				TransDocProcessingResultLoc location 
				     = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(
						new ProcessingResult(APP_VALIDATION, "ER10008",
								"The Document Date should be less than "
										+ "or equal to current date.",
								location));
			}

		}

		return errors;
	}

}
