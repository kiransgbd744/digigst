package com.ey.advisory.app.services.validation.advanceAdjusted;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.List;

import com.ey.advisory.app.data.entities.client.Gstr1AsEnteredTxpdFileUploadEntity;
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
public class AANewGrossAdvAdjustment implements
		B2csBusinessRuleValidator<Gstr1AsEnteredTxpdFileUploadEntity> {

	@Override
	public List<ProcessingResult> validate(
			Gstr1AsEnteredTxpdFileUploadEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();

		if (document.getNewGrossAdvanceAdjusted() == null
				|| document.getNewGrossAdvanceAdjusted().isEmpty()) {
			errorLocations.add(GSTConstants.NEW_GROSS_ADVANCE_ADJUSTMENT);
			TransDocProcessingResultLoc location = 
					new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER5168",
					"NewGrossAdvanceAdjusted Cannot be left blank.", location));
			return errors;
		}
		return errors;
	}

}
