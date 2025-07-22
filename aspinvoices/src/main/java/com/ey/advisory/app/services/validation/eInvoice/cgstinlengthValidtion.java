/*package com.ey.advisory.app.services.validation.eInvoice;

import static com.ey.advisory.app.data.entities.client.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.List;

import com.ey.advisory.app.data.entities.client.GSTConstants;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

*//**
 * @author Siva.Nandam
 *
 *//*
public class cgstinlengthValidtion
		implements DocRulesValidator<OutwardTransDocument> {

	private static final List<String> E_CATEGORY = ImmutableList
			.of(GSTConstants.B2B, GSTConstants.B2G);

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();

		if (document.getCategory() != null
				&& !document.getCategory().isEmpty()) {
			if (E_CATEGORY.contains(
					trimAndConvToUpperCase(document.getDocCategory()))) {
				if (document.getCgstin() == null || document.getCgstin().isEmpty()
						|| document.getCgstin().length() != 15) {
					String[] errorLocations = new String[] {
							GSTConstants.CGSTIN };
					TransDocProcessingResultLoc location 
					       = new TransDocProcessingResultLoc(
							null, errorLocations);
					errors.add(new ProcessingResult(APP_VALIDATION, "ER10019",
							"Recipient GSTIN cannot be left Blank",
							location));
				}
			}
		}
		return errors;
	}

}
*/