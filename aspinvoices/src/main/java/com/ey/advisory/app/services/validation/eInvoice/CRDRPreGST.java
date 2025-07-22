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
@Component("CRDRPreGST")
public class CRDRPreGST implements DocRulesValidator<OutwardTransDocument> {

	private static final List<String> DOC_TYPE_IMPORTS = ImmutableList
			.of(GSTConstants.CR, GSTConstants.DR);

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (DOC_TYPE_IMPORTS.contains(document.getSupplyType())) {
			if (document.getCrDrPreGst() != null
					&& !document.getCrDrPreGst().isEmpty()) {
				boolean valid = YorNFlagValidation
						.valid(document.getCrDrPreGst());
				if (!valid) {
					List<String> errorLocations = new ArrayList<>();
					errorLocations.add(GSTConstants.PRE_GST);
					TransDocProcessingResultLoc location 
					      = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER0044",
							"Invalid CR DR Pre GST Flag.", location));

				}
			}
		}
		return errors;
	}

}
