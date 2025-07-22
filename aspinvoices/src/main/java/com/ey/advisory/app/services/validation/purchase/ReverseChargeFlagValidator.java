package com.ey.advisory.app.services.validation.purchase;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.ReverseCharge;

import java.util.ArrayList;
import java.util.List;

import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * 
 * @author Siva.Nandam
 *
 */
public class ReverseChargeFlagValidator
		implements DocRulesValidator<InwardTransDocument>

{

	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();

		List<String> errorLocations = new ArrayList<>();
		// Get the document date from the document and convert it to a
		// LocalDate object.
		if (document.getSupplyType() != null
				&& !document.getSupplyType().isEmpty()) {
			if (document.getDocType() != null
					&& !document.getDocType().isEmpty()) {

				if (GSTConstants.IMPS.equalsIgnoreCase(document.getSupplyType())
						|| (GSTConstants.SLF
								.equalsIgnoreCase(document.getDocType()))) {
					if (document.getReverseCharge() == null
							|| document.getReverseCharge()
									.equalsIgnoreCase(GSTConstants.N)) {
						errorLocations.add(ReverseCharge);
						TransDocProcessingResultLoc location 
						= new TransDocProcessingResultLoc(
								null, errorLocations.toArray());
						errors.add(
								new ProcessingResult(APP_VALIDATION, "ER1099",
										"Reverse Charge flag should be Y.",
										location));

					}
				}
			}
		}

		return errors;
	}
}