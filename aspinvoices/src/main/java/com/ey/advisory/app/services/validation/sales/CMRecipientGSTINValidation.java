package com.ey.advisory.app.services.validation.sales;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

@Component("CMRecipientGSTINValidation")
public class CMRecipientGSTINValidation
		implements DocRulesValidator<OutwardTransDocument> {

	private static final List<String> SUPPLYTYPE = ImmutableList.of(

			GSTConstants.EXPT, GSTConstants.EXPWT);

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		if(GSTConstants.URP.equalsIgnoreCase(document.getCgstin())) return errors;
		if (document.getSupplyType() != null
				&& !document.getSupplyType().isEmpty()) {

			if (SUPPLYTYPE.contains(
					trimAndConvToUpperCase(document.getSupplyType()))) {
				if (document.getCgstin() == null
						|| document.getCgstin().isEmpty()) {
					// nothing todo
				} else {
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.RecipientGSTIN);
					TransDocProcessingResultLoc location 
					              = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER0111",
							"Recipient GSTIN should be Blank.", location));
				}
			}

		}

		return errors;
	}
}
