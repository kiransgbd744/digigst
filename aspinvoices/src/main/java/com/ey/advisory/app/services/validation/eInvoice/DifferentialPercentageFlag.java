package com.ey.advisory.app.services.validation.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * @author Siva.Nandam
 *
 */
@Component("DifferentialPercentageFlag")
public class DifferentialPercentageFlag implements DocRulesValidator<OutwardTransDocument> {

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (document.getDiffPercent() != null
				&& !document.getDiffPercent().isEmpty()) {
			boolean valid = YorNFlagValidation
					.Diffvalid(document.getDiffPercent());
			if (!valid) {
				List<String> errorLocations = new ArrayList<>();
				errorLocations.add(GSTConstants.DIFF_PER_FLAG);
				TransDocProcessingResultLoc location 
				                = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER0052",
						"Invalid Differential Percentage Flag.", location));

			}
		}
		return errors;
	}

}
