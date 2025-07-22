/*package com.ey.advisory.app.services.validation.eInvoice;

import static com.ey.advisory.app.data.entities.client.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.List;

import com.ey.advisory.app.data.entities.client.GSTConstants;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

*//**
 * @author Siva.Nandam
 *
 *//*
public class SlfValiadtion implements DocRulesValidator<OutwardTransDocument> {

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();

		if (GSTConstants.SLF.equalsIgnoreCase(document.getDocType())) {
			if (document.getCategory() != null
					&& !document.getCategory().isEmpty()) {
				if (!GSTConstants.B2B
						.equalsIgnoreCase(document.getCategory())) {
					String[] errorLocations = new String[] {
							GSTConstants.CATEGORY };
					TransDocProcessingResultLoc location 
					= new TransDocProcessingResultLoc(
							null, errorLocations);
					errors.add(new ProcessingResult(APP_VALIDATION, "ER10004",
							"Category should be B2B where Document Type  is SLF",
							location));
				}
			}
		}
		return errors;
	}

}
*/