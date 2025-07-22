package com.ey.advisory.app.services.validation.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;

/**
 * @author Siva.Nandam
 *
 */
@Component("LuTransactionTypeValidation")
public class LuTransactionTypeValidation
		implements DocRulesValidator<OutwardTransDocument> {

	private static final List<String> TRANSACTION_TYPE = ImmutableList
			.of(GSTConstants.I, GSTConstants.O);

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (document.getTransactionType() != null
				&& !document.getTransactionType().isEmpty()) {
			if (!TRANSACTION_TYPE.contains(
					trimAndConvToUpperCase(document.getTransactionType()))) {

				List<String> errorLocations = new ArrayList<>();
				errorLocations.add(GSTConstants.TRANSACTION_TYPE);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER10135",
						"Invalid Transaction Type", location));

			}
		}
		return errors;
	}

}
