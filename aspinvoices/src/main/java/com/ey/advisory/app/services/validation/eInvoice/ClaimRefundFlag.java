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
import com.google.common.collect.ImmutableList;

/**
 * @author Siva.Nandam
 *
 */
@Component("ClaimRefundFlag")
public class ClaimRefundFlag
		implements DocRulesValidator<OutwardTransDocument> {

	private static final List<String> SUP_TYPE_IMPORTS = ImmutableList
			.of(GSTConstants.SEZWP, GSTConstants.SEZWOP, GSTConstants.DXP);

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (SUP_TYPE_IMPORTS.contains(document.getSupplyType())) {
			if (document.getClaimRefundFlag() != null
					&& !document.getClaimRefundFlag().isEmpty()) {
				boolean valid = YorNFlagValidation
						.valid(document.getClaimRefundFlag());
				if (!valid) {
					List<String> errorLocations = new ArrayList<>();
					errorLocations.add(GSTConstants.CLAIMREFUNDFLAG);
					TransDocProcessingResultLoc location 
					     = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER0103",
							" Invalid Claim Refund Flag.", location));

				}
			}
		}
		return errors;
	}

}
