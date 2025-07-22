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
import com.google.common.collect.ImmutableList;

/**
 * @author Siva.Nandam
 *
 */
public class Gstr6DocTypeValidation
		implements BusinessRuleValidator<Gstr6DistributionExcelEntity> {

	private static final List<String> SUPPLYTYPE1 = ImmutableList.of(

			GSTConstants.INV, GSTConstants.RNV, GSTConstants.CR,
			GSTConstants.RCR);

	@Override
	public List<ProcessingResult> validate(
			Gstr6DistributionExcelEntity document, ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();

		if (document.getDocType() != null && !document.getDocType().isEmpty()) {

			if (!SUPPLYTYPE1.contains(document.getDocType().toUpperCase())) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.DOC_TYPE);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, " ER3015",
						"Invalid Document Type.", location));
			}

		}
		return errors;
	}

}
