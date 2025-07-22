package com.ey.advisory.app.services.validation.gstr1a.InvoiceFile;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAsEnteredInvEntity;
import com.ey.advisory.app.services.validation.B2csBusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
public class Gstr1AFrom
		implements B2csBusinessRuleValidator<Gstr1AAsEnteredInvEntity> {

	@Override
	public List<ProcessingResult> validate(Gstr1AAsEnteredInvEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();

		if (document.getFrom() != null && !document.getFrom().isEmpty()) {
			String from = document.getFrom();

			if (!from.matches("[0-9A-Za-z/-]+")) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.FROM);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER5208",
						"Invalid input in From column.", location));
				return errors;
			}
		}
		return errors;
	}

}
