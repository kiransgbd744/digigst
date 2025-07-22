package com.ey.advisory.app.services.structuralvalidation.gstr6;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.data.entities.client.Gstr6DistributionExcelEntity;
import com.ey.advisory.common.BusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * @author Siva.Nandam
 *
 */
public class Gstr6SupplyTypeValidation
		implements BusinessRuleValidator<Gstr6DistributionExcelEntity> {

	@Override
	public List<ProcessingResult> validate(
			Gstr6DistributionExcelEntity document, ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();

		if (document.getSupplyType() != null
				&& !document.getSupplyType().isEmpty()) {

			if (!GSTConstants.CAN.equalsIgnoreCase(document.getSupplyType())) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.SUPPLY_TYPE);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER3016",
						"Invalid Supply Type", location));
			}

		}
		return errors;
	}

}
