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
@Component("AutoPopulateToRefund")
public class AutoPopulateToRefund
		implements DocRulesValidator<OutwardTransDocument> {

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (document.getAutoPopToRefundFlag() != null
				&& !document.getAutoPopToRefundFlag().isEmpty()) {
			boolean valid = YorNFlagValidation
					.valid(document.getAutoPopToRefundFlag());
			if (!valid) {
				List<String> errorLocations = new ArrayList<>();
				errorLocations.add(GSTConstants.AUTO_POP_REFUND);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER0105",
						"Invalid Auto Populate to Refund Flag.", location));

			}
		}
		return errors;
	}

}
