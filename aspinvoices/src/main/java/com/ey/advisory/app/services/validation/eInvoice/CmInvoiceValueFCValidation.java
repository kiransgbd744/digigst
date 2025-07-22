/*package com.ey.advisory.app.services.validation.eInvoice;

import static com.ey.advisory.app.data.entities.client.GSTConstants.APP_VALIDATION;

import java.math.BigDecimal;
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
public class CmInvoiceValueFCValidation
		implements DocRulesValidator<OutwardTransDocument> {

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		BigDecimal invoiceValueFc = document.getInvoiceValueFc();
		if (GSTConstants.EXP.equalsIgnoreCase(document.getCategory())) {
			if (invoiceValueFc == null) {
				invoiceValueFc = BigDecimal.ZERO;
			}
			if (invoiceValueFc.compareTo(BigDecimal.ZERO) == 0) {

				String[] errorLocations = new String[] {
						GSTConstants.INV_VALUE_FC };
				TransDocProcessingResultLoc location 
				       = new TransDocProcessingResultLoc(
						null, errorLocations);
				errors.add(new ProcessingResult(APP_VALIDATION, "ER10091",
						"InvoiceValueFC cannot be left blank.", location));
			}
		}

		return errors;
	}

}
*/