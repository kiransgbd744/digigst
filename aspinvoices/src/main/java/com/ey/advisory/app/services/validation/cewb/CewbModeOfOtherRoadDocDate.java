package com.ey.advisory.app.services.validation.cewb;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.data.entities.client.CewbExcelEntity;
import com.ey.advisory.app.services.validation.B2csBusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

public class CewbModeOfOtherRoadDocDate
		implements B2csBusinessRuleValidator<CewbExcelEntity> {

	@Override
	public List<ProcessingResult> validate(CewbExcelEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		String transMode = document.getTransMode() != null
				&& !document.getTransMode().isEmpty()
						? document.getTransMode().toUpperCase() : null;

		if (transMode != null) {
			String transDate = document.getTransDocDate() != null
					&& !document.getTransDocDate().isEmpty()
							? document.getTransDocDate() : null;
			if (transDate == null
					&& !GSTConstants.ROAD.equalsIgnoreCase(transMode)) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.TRANS_PORT_DOCDATE);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(GSTConstants.APP_VALIDATION,
						"ER6130",
						"In case of mode other than Road Transport Document Date is Mandatory.",
						location));
				return errors;
			}
		}
		return errors;
	}

}
