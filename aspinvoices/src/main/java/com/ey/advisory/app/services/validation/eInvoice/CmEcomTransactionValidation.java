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
public class CmEcomTransactionValidation
		implements DocRulesValidator<OutwardTransDocument> {

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();

		if (GSTConstants.Y.equalsIgnoreCase(document.getTcsFlag())) {
			if (!GSTConstants.Y
					.equalsIgnoreCase(document.getEcomTransaction())) {

				String[] errorLocations = new String[] {
						GSTConstants.E_COM_TRANS };
				TransDocProcessingResultLoc location 
				= new TransDocProcessingResultLoc(
						null, errorLocations);
				errors.add(new ProcessingResult(APP_VALIDATION, "ER10115",
						"Invalid Ecom Transaction Flag.", location));
			}
		}

		return errors;
	}

}
*/