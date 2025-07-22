package com.ey.advisory.app.services.validation.gstr1a.advanceAdjusted;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.List;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAsEnteredTxpdFileUploadEntity;
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
public class Gstr1AAAOrgGrossAdvAdjustment implements
		B2csBusinessRuleValidator<Gstr1AAsEnteredTxpdFileUploadEntity> {

	@Override
	public List<ProcessingResult> validate(
			Gstr1AAsEnteredTxpdFileUploadEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();

		if (document.getMonth() != null && !document.getMonth().isEmpty()) {
			if (document.getOrgGrossAdvanceAdjusted() == null
					|| document.getOrgGrossAdvanceAdjusted().isEmpty()) {
				errorLocations.add(GSTConstants.ORG_GROSS_ADVANCE_ADJUSTMENT);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(
						new ProcessingResult(APP_VALIDATION, "ER5163",
								"OrgGrossAdvanceAdjusted Cannot be left blank if "
										+ "Month field is provided.",
								location));
				return errors;
			}
		}

		return errors;
	}

}
