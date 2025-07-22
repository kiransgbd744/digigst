/*package com.ey.advisory.app.services.businessvalidation.table3h3i;

import static com.ey.advisory.app.data.entities.client.GSTConstants.*;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.data.entities.client.GSTConstants;
import com.ey.advisory.app.data.entities.client.InwardTable3IDetailsEntity;
import com.ey.advisory.app.services.validation.BusinessRuleValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;

public class DocumentTypeValidator
		implements BusinessRuleValidator<InwardTable3IDetailsEntity> {
	
	private static final List<String> DOCUMENT_TYPE_IMPORTS = ImmutableList.of(
			GSTConstants.INV, GSTConstants.RNV, GSTConstants.DR,
			GSTConstants.RDR, GSTConstants.CR,GSTConstants.RCR,
			GSTConstants.SLF,GSTConstants.RSLF,GSTConstants.DLC,
			GSTConstants.RFV,GSTConstants.PV,GSTConstants.AV);

	@Override
	public List<ProcessingResult> validate(InwardTable3IDetailsEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (document.getDocType() != null && !document.getDocType().isEmpty()) {
			if (!(DOCUMENT_TYPE_IMPORTS
					.contains(trimAndConvToUpperCase(document.getDocType())))) {

				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.DOC_TYPE);
				TransDocProcessingResultLoc location = 
						new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER1208",
						"Invalid Document Type", location));
			}
		}
		return errors;
	}
}
*/