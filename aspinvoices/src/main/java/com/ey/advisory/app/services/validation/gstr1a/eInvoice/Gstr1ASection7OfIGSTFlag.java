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

/**
 * @author Shashikant.Shukla
 *
 */
@Component("Gstr1ASection7OfIGSTFlag")
public class Gstr1ASection7OfIGSTFlag
		implements DocRulesValidator<Gstr1AOutwardTransDocument> {

	@Override
	public List<ProcessingResult> validate(Gstr1AOutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (document.getSection7OfIgstFlag() != null
				&& !document.getSection7OfIgstFlag().isEmpty()) {
			boolean valid = Gstr1AYorNFlagValidation
					.valid(document.getSection7OfIgstFlag());
			if (!valid) {
				List<String> errorLocations = new ArrayList<>();
				errorLocations.add(GSTConstants.SEC7_IGST_FLAG);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER0072",
						"Invalid Section 7 Flag", location));

			}
		}
		return errors;
	}

}
