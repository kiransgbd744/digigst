package com.ey.advisory.app.services.businessvalidation.master;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.admin.data.entities.client.MasterItemEntity;
import com.ey.advisory.common.BusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

public class ItemReversIdentifier
		implements BusinessRuleValidator<MasterItemEntity> {
	@Override
	public List<ProcessingResult> validate(MasterItemEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (document != null && !Arrays.asList("T1", "T3").contains(document)) {

			Set<String> errorLocations = new HashSet<>();
			errorLocations.add(GSTConstants.ITC_REVERSAL_IDENTIFIER);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER1649",
					"Invalid ITC Reversal Identifier", location));
			return errors;
		}

		return errors;
	}

}
