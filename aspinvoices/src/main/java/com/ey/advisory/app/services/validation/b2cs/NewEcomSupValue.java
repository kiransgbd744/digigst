package com.ey.advisory.app.services.validation.b2cs;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.List;

import com.ey.advisory.app.data.entities.client.Gstr1AsEnteredB2csEntity;
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
public class NewEcomSupValue
		implements B2csBusinessRuleValidator<Gstr1AsEnteredB2csEntity> {

	@Override
	public List<ProcessingResult> validate(Gstr1AsEnteredB2csEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();
		
		if (document.getNewGstin() != null
				&& !document.getNewGstin().isEmpty()) {
			if (document.getNewSupVal() == null
					|| document.getNewSupVal().isEmpty()) {
				errorLocations.add(GSTConstants.NEWECOM_SUP_VALUE);
				TransDocProcessingResultLoc location =
						new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER5026",
						"Invalid NeweComSupplyValue", location));
				return errors;
			}
		}
		return errors;
	}

}