package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.DOC_DATE;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * 
 * @author Siva.Nandam
 *
 */

@Component("DocDateTaxPeriodValidator")
public class DocDateTaxPeriodValidator
		implements DocRulesValidator<OutwardTransDocument> {

	/**
	 * This method first gets the tax period as a string and then finds out the
	 * last day of the month for the taxperiod. It then extracts the date part
	 * of the document date. Finally, it checks if the date part of the document
	 * date is less than or equal to the last day of the tax period. If not, it
	 * marks it as an error.
	 * 
	 * @param OutwardTransDocument
	 * @param ProcessingContext
	 * @return List<ProcessingResult>
	 */
	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();

		// Get the document date from the document and convert it to a
		// LocalDate object.
		if (document.getDocDate() != null) {
			LocalDate docDate = document.getDocDate();
			if (document.getTaxperiod() != null
					&& !document.getTaxperiod().isEmpty()) {

				String DateYear = docDate.toString().substring(0, 4);
				String Datemonth = docDate.toString().substring(5, 7);
				String ReturnDate = document.getTaxperiod().substring(2, 6);
				String ReturnMonth = document.getTaxperiod().substring(0, 2);

				if (DateYear.compareTo(ReturnDate) > 0) {
					List<String> errorLocations = new ArrayList<>();
					errorLocations.add(DOC_DATE);
					TransDocProcessingResultLoc location 
					                 = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(
							new ProcessingResult(APP_VALIDATION, "ER0035",
									"Document Date should not be greater than "
											+ "last date of return period.",
									location));
				}
				if (DateYear.compareTo(ReturnDate) == 0) {
					if (Datemonth.compareTo(ReturnMonth) > 0) {
						List<String> errorLocations = new ArrayList<>();
						errorLocations.add(DOC_DATE);
						TransDocProcessingResultLoc location 
						      = new TransDocProcessingResultLoc(
								null, errorLocations.toArray());
						errors.add(
								new ProcessingResult(APP_VALIDATION, "ER0035",
										"Document Date should not be greater than "
												+ "last date of return period.",
										location));
					}

				}

			}
		}
		return errors;
	}

}
