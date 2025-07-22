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
public class CmOtherSupplyTypedescValidation
		implements DocRulesValidator<OutwardTransDocument> {	
	

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (document.getSubSupplyType() == null
				|| document.getSubSupplyType().isEmpty())
			return errors;
		if (!GSTConstants.OTH.equalsIgnoreCase(document.getSubSupplyType()))
			return errors;

		if (document.getOtherSupplyTypeDescription() == null
				|| document.getOtherSupplyTypeDescription().isEmpty()) {
			List<String> errorLocations = new ArrayList<>();
			errorLocations.add(GSTConstants.OTH_SUP_DESC);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER10135",
					" Dispatcher GSTIN cannot be left blank", location));

		}

		return errors;
	}
	
}
*/