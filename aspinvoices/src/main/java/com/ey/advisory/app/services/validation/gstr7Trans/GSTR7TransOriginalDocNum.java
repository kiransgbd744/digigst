package com.ey.advisory.app.services.validation.gstr7Trans;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.gstr7trans.Gstr7TransDocHeaderEntity;
import com.ey.advisory.app.services.docs.gstr7.Gstr7TransDocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;

import joptsimple.internal.Strings;

/**
 * @author Siva.Reddy
 *
 */
@Component("GSTR7TransOriginalDocNum")
public class GSTR7TransOriginalDocNum
		implements Gstr7TransDocRulesValidator<Gstr7TransDocHeaderEntity> {

	private static final List<String> DOCTYPE = ImmutableList
			.of(GSTConstants.RNV);

	@Override
	public List<ProcessingResult> validate(Gstr7TransDocHeaderEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();
		if (document.getDocType() == null || document.getDocType().isEmpty())
			return errors;

		if (!DOCTYPE.contains(document.getDocType()))
			return errors;

		if ("RNV".equalsIgnoreCase(document.getDocType())) {
			String originalDocNum = document.getOriginalDocNum();

			if (Strings.isNullOrEmpty(originalDocNum)) {
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errorLocations.add(GSTConstants.ORIGINAL_DOC_NO);
				errors.add(new ProcessingResult(APP_VALIDATION, "ER63029",
						"Original Document Number cannot be left blank",
						location));
			} else if (!isValidOriginalDocNum(originalDocNum)) {
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errorLocations.add(GSTConstants.ORIGINAL_DOC_NO);

				errors.add(new ProcessingResult(APP_VALIDATION, "ER63006",
						"Invalid Original Document Number", location));
			}
		}
		return errors;

	}

	private boolean isValidOriginalDocNum(String docNum) {
		// Check max length
		if (docNum.length() > 16)
			return false;

		// Check if it starts with "-" or "/"
		if (docNum.startsWith("-") || docNum.startsWith("/"))
			return false;

		// Ensure only allowed characters are present
		return docNum.matches("[A-Za-z0-9-/]+");
	}

}
