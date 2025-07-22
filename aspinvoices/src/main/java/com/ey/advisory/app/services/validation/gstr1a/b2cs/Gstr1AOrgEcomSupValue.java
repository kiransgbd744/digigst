package com.ey.advisory.app.services.validation.gstr1a.b2cs;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.List;

import com.ey.advisory.app.data.entities.client.Gstr1AsEnteredB2csEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAsEnteredB2csEntity;
import com.ey.advisory.app.services.validation.B2csBusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

public class Gstr1AOrgEcomSupValue
		implements B2csBusinessRuleValidator<Gstr1AAsEnteredB2csEntity> {

	@Override
	public List<ProcessingResult> validate(Gstr1AAsEnteredB2csEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();
		if (document.getOrgCGstin() != null
				&& !document.getOrgCGstin().isEmpty()) {
			if (document.getOrgSupVal() == null
					|| document.getOrgSupVal().isEmpty()) {
				errorLocations.add(GSTConstants.ORGECOM_SUP_VALUE);
				TransDocProcessingResultLoc location =
						new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER5018",
						"InvalidOrgeComSupplyValue.", location));
				return errors;
			}
		}
		return errors;
	}

}
