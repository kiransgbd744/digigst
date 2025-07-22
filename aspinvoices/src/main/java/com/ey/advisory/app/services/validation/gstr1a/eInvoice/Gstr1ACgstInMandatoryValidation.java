package com.ey.advisory.app.services.validation.gstr1a.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.base.Strings;

/**
 * @author Shashikant.Shukla
 *
 */
public class Gstr1ACgstInMandatoryValidation
		implements DocRulesValidator<Gstr1AOutwardTransDocument> {

	@Override
	public List<ProcessingResult> validate(Gstr1AOutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (GSTConstants.I.equalsIgnoreCase(document.getTransactionType())
				&& Strings.isNullOrEmpty(document.getCgstin())) {
			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.CGSTIN);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER0049",
					"Recipient GSTIN cannot be left Blank.", location));
			return errors;
		}
		return errors;
	}
}
