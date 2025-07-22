package com.ey.advisory.app.services.validation.purchase;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.data.entities.client.InwardTransDocument;
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
public class Gstr2IsdLuDocTypeValidation
		implements DocRulesValidator<InwardTransDocument> {

	private static final List<String> DOCTYPE = ImmutableList.of(
			GSTConstants.INV, GSTConstants.CR, GSTConstants.RNV,
			GSTConstants.RCR);

	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (document.getDocType() != null && !document.getDocType().isEmpty()) {

			if (!DOCTYPE
					.contains(trimAndConvToUpperCase(document.getDocType()))) {

				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.DOC_TYPE);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER1038",
						"Invalid Document Type", location));
			}
		}
		return errors;
	}

}
