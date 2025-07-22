package com.ey.advisory.app.services.validation.gstr1a.advanceReceived;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.List;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAsEnteredAREntity;
import com.ey.advisory.app.services.validation.ARBusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

public class Gstr1AArOrgGrossAR
		implements ARBusinessRuleValidator<Gstr1AAsEnteredAREntity> {

	@Override
	public List<ProcessingResult> validate(Gstr1AAsEnteredAREntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();

		if (document.getMonth() != null && !document.getMonth().isEmpty()) {
			if (document.getOrgGrossAdvRec() == null
					|| document.getOrgGrossAdvRec().isEmpty()) {
				errorLocations.add(GSTConstants.ORG_GROSS_ADVANCE_RECEIVED);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER5113",
						"OrgGrossAdvanceReceived Cannot be "
								+ "left blank if Month field is provided.",
						location));
				return errors;
			}
		}

		return errors;
	}

}
