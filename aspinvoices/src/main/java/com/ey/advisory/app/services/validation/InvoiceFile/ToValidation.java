package com.ey.advisory.app.services.validation.InvoiceFile;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.data.entities.client.Gstr1AsEnteredInvEntity;
import com.ey.advisory.app.services.validation.B2csBusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
/**
 * 
 * @author Mahesh.Golla
 *
 */
public class ToValidation
		implements B2csBusinessRuleValidator<Gstr1AsEnteredInvEntity> {

	@Override
	public List<ProcessingResult> validate(Gstr1AsEnteredInvEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();

		Set<String> errorLocations = new HashSet<>();
		if (document.getTo() != null && !document.getTo().isEmpty()) {
			String to =document.getTo();
			if (!to.matches("[0-9A-Za-z/-]+")) {
				errorLocations.add(GSTConstants.TO);
				TransDocProcessingResultLoc location = 
						new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER5209",
						"Invalid input in To column.", location));
				return errors;
			}
		}
		return errors;
	}

}
