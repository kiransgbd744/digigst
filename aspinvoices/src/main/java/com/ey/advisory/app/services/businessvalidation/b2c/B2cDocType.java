/*package com.ey.advisory.app.services.businessvalidation.b2c;

import static com.ey.advisory.app.data.entities.client.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.app.data.entities.client.GSTConstants.DOC_TYPE;

import java.util.ArrayList;
import java.util.List;

import com.ey.advisory.app.data.entities.client.OutwardB2cEntity;
import com.ey.advisory.app.services.businessvalidation.table4.BusinessRuleValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.google.common.collect.ImmutableList;

public class B2cDocType implements BusinessRuleValidator<OutwardB2cEntity> {

	private static final List<String> DOC_TYPES = ImmutableList.of("INV", "RNV",
			"CR", "RCR", "DR", "RDR", "RFV", "DLC", "RV", "AV");

	@Override
	public List<ProcessingResult> validate(OutwardB2cEntity document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();

		@SuppressWarnings("unused")
		String groupCode = TenantContext.getTenantId();
		if (document.getDocType() != null && !document.getDocType().isEmpty()) {
			String docType = document.getDocType();
			if (!DOC_TYPES.contains(docType)) {
				errorLocations.add(DOC_TYPE);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER0208",
						"Invalid Document Type", location));
			}
		}
		return errors;
	}
}
*/