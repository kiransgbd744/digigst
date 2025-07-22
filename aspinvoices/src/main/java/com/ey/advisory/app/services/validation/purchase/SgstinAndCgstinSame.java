package com.ey.advisory.app.services.validation.purchase;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.List;

import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * @author Siva.Nandam
 *
 */
public class SgstinAndCgstinSame
		implements DocRulesValidator<InwardTransDocument> {

	private static final String[] FIELD_LOCATIONS = {
			GSTConstants.RecipientGSTIN };

	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();

		if (document.getSgstin() != null && !document.getSgstin().isEmpty()) {
			if (document.getCgstin() != null
					&& !document.getCgstin().isEmpty()) {

				if (document.getSgstin().length() == 15) {

					if (document.getSgstin()
							.equalsIgnoreCase(document.getCgstin())) {

						TransDocProcessingResultLoc location 
						= new TransDocProcessingResultLoc(
								null, FIELD_LOCATIONS);
						errors.add(
								new ProcessingResult(APP_VALIDATION, "ER1305",
										"Recipient GSTIN cannot be same as "
										+ "Supplier GSTIN",
										location));

					}

				}
			}
		}
		return errors;
	}

}
