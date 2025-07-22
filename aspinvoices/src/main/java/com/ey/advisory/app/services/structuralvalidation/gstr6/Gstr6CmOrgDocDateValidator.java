package com.ey.advisory.app.services.structuralvalidation.gstr6;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.List;

import com.ey.advisory.app.data.entities.client.Gstr6DistributionExcelEntity;
import com.ey.advisory.common.BusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * @author Siva.Nandam
 *
 */
public class Gstr6CmOrgDocDateValidator
		implements BusinessRuleValidator<Gstr6DistributionExcelEntity> {

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
	public List<ProcessingResult> validate(
			Gstr6DistributionExcelEntity document, ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();

		// Get the document date from the document and convert it to a
		// LocalDate object.
		if (document.getOrgDocDate() != null) {
			String docDate = document.getOrgDocDate().toString();
			if (document.getRetPeriod() != null
					&& !document.getRetPeriod().isEmpty()) {

				String DateYear = docDate.substring(0, 4);
				String Datemonth = docDate.substring(5, 7);
				String ReturnDate = document.getRetPeriod().substring(2, 6);
				String ReturnMonth = document.getRetPeriod().substring(0, 2);

				if (DateYear.compareTo(ReturnDate) > 0) {
					List<String> errorLocations = new ArrayList<>();
					errorLocations.add(GSTConstants.ORIGINAL_DOC_DATE);
					TransDocProcessingResultLoc location 
					  = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(
							new ProcessingResult(APP_VALIDATION, "ER3051",
									"Original  Document Date should not "
									+ "be greater than "
											+ "last date of return period.",
									location));
				}
				if (DateYear.compareTo(ReturnDate) == 0) {
					if (Datemonth.compareTo(ReturnMonth) > 0) {
						List<String> errorLocations = new ArrayList<>();
						errorLocations.add(GSTConstants.ORIGINAL_DOC_DATE);
						TransDocProcessingResultLoc location 
						 = new TransDocProcessingResultLoc(
								null, errorLocations.toArray());
						errors.add(
								new ProcessingResult(APP_VALIDATION, "ER3051",
										"Original  Document Date should "
										+ "not be greater than "
												+ "last date of return period.",
										location));
					}

				}

			}
		}
		return errors;
	}

}
