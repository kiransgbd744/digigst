/*package com.ey.advisory.app.services.validation.eInvoice;

import static com.ey.advisory.app.data.entities.client.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.List;

import com.ey.advisory.app.data.entities.client.GSTConstants;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

*//**
 * @author Siva.Nandam
 *
 *//*
public class CmCountryCodeValidation
		implements DocRulesValidator<OutwardTransDocument> {

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();

		if (GSTConstants.EXP.equalsIgnoreCase(document.getCategory())) {
			if (document.getCountryCode() == null
					|| document.getCountryCode().isEmpty()) {

				String[] errorLocations = new String[] {
						GSTConstants.COUNTRY_CODE };
				TransDocProcessingResultLoc location 
				      = new TransDocProcessingResultLoc(
						null, errorLocations);
				errors.add(new ProcessingResult(APP_VALIDATION, "ER10089",
						"CountryCode cannot be left blank", location));
			}
		}

		return errors;
	}

}
*/