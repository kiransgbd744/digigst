package com.ey.advisory.app.services.validation.gstr1a.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * @author Shashikant.Shukla
 *
 */
public class Gstr1AInvoiceAssessableAmountMandatory
		implements DocRulesValidator<Gstr1AOutwardTransDocument> {

	@Override
	public List<ProcessingResult> validate(Gstr1AOutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		BigDecimal invoiceAssessableAmount = document
				.getInvoiceAssessableAmount();
		if (invoiceAssessableAmount == null) {
			invoiceAssessableAmount = BigDecimal.ZERO;
		}

		if (invoiceAssessableAmount.compareTo(BigDecimal.ZERO) == 0) {
			List<String> errorLocations = new ArrayList<>();
			errorLocations.add(GSTConstants.INVOICE_ASSESSABLE_AMOUNT);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER10077",
					"Invalid Invoice Assesable Amount.", location));
		}
		return errors;
	}

}
