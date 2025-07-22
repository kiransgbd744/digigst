package com.ey.advisory.app.services.validation.purchase;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.List;

import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.app.services.validation.eInvoice.YorNFlagValidation;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

import com.google.common.base.Strings;

/**
 * @author Siva.Nandam
 *
 */
public class RevChargeFlag implements DocRulesValidator<InwardTransDocument> {

	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (!Strings.isNullOrEmpty(document.getReverseCharge())) {
			if (("IMPG".equals(document.getSupplyType())
					|| "SEZG".equals(document.getSupplyType()))
					&& "Y".equals(document.getReverseCharge())) {
				List<String> errorLocations = new ArrayList<>();
				errorLocations.add(GSTConstants.ReverseCharge);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER1097",
						"Invalid Reverse Charge Flag", location));
			} else {
				boolean valid = YorNFlagValidation
						.valid(document.getReverseCharge());
				if (!valid) {
					List<String> errorLocations = new ArrayList<>();
					errorLocations.add(GSTConstants.ReverseCharge);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER1097",
							"Invalid Reverse Charge Flag", location));

				}
			}
		}

		return errors;
	}

}
