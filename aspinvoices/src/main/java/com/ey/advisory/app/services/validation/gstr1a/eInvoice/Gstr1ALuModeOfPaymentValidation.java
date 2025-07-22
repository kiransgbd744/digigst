package com.ey.advisory.app.services.validation.gstr1a.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;

/**
 * @author Shashikant.Shukla
 *
 */
@Component("Gstr1ALuModeOfPaymentValidation")
public class Gstr1ALuModeOfPaymentValidation
		implements DocRulesValidator<Gstr1AOutwardTransDocument> {

	private static final List<String> CATEGOTY_IMPORTS1 = ImmutableList.of(
			GSTConstants.CASH, GSTConstants.EPAY, GSTConstants.DIRDBT,
			GSTConstants.OTH);

	@Override
	public List<ProcessingResult> validate(Gstr1AOutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (document.getModeOfPayment() == null
				|| document.getModeOfPayment().isEmpty()) {
			document.setModeOfPayment(GSTConstants.OTH);
		}
		if (!CATEGOTY_IMPORTS1
				.contains(document.getModeOfPayment().toUpperCase())) {

			List<String> errorLocations = new ArrayList<>();
			errorLocations.add(GSTConstants.MODE_OF_PAYMENT);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER10109",
					"Invalid Payment Mode as per Master", location));

		}

		return errors;
	}

}
