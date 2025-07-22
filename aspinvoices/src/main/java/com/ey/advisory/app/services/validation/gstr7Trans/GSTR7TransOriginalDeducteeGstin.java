package com.ey.advisory.app.services.validation.gstr7Trans;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.gstr7trans.Gstr7TransDocHeaderEntity;
import com.ey.advisory.app.services.docs.gstr7.Gstr7TransDocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;

import joptsimple.internal.Strings;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Reddy
 *
 */
@Slf4j
@Component("GSTR7TransOriginalDeducteeGstin")
public class GSTR7TransOriginalDeducteeGstin
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
			String originalDeducteeGstin = document.getOriginalDeducteeGstin();

			if (Strings.isNullOrEmpty(originalDeducteeGstin)) {
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errorLocations.add(GSTConstants.ORIGINAL_DOC_NO);
				errors.add(new ProcessingResult(APP_VALIDATION, "ER63009",
						"Original DeducteeGstin cannot be left blank",
						location));
			} else if (!isValidOriginalGstin(originalDeducteeGstin)) {
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errorLocations.add(GSTConstants.ORIGINAL_DOC_NO);

				errors.add(new ProcessingResult(APP_VALIDATION, "ER63006",
						"Invalid Original Deductee Gstin", location));
			}
		}
		return errors;

	}

	private boolean isValidOriginalGstin(String gstin) {
		String regex = "^[0-9][0-9][A-Za-z][A-Za-z][A-Za-z]"
				+ "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][A-Za-z][A-Za-z0-9]"
				+ "[A-Za-z0-9][A-Za-z0-9]$";

		String regex1 = "^[0-9][0-9][A-Za-z][A-Za-z][A-Za-z]"
				+ "[A-Za-z][0-9][0-9][0-9][0-9][0-9][A-Za-z][A-Za-z0-9]"
				+ "[A-Za-z0-9][A-Za-z0-9]$";

		String regex2 = "^[0-9][0-9][0-9][0-9][A-Za-z]"
				+ "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][0-9][UOuo]"
				+ "[Nn][A-Za-z0-9]$";

		String regex3 = "^[0-9][0-9][0-9][0-9][A-Za-z]"
				+ "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][0-9][Nn]"
				+ "[Rr][A-Za-z0-9]$";

		Pattern pattern = Pattern.compile(regex);

		Pattern pattern1 = Pattern.compile(regex1);

		Pattern pattern2 = Pattern.compile(regex2);

		Pattern pattern3 = Pattern.compile(regex3);

		Matcher matcher = pattern.matcher(gstin.trim());
		Matcher matcher1 = pattern1.matcher(gstin.trim());
		Matcher matcher2 = pattern2.matcher(gstin.trim());
		Matcher matcher3 = pattern3.matcher(gstin.trim());
		if (matcher.matches() || matcher1.matches() || matcher2.matches()
				|| matcher3.matches()) {
			LOGGER.debug("GSTIN is Valid");
			return true;
		} else {
			LOGGER.debug("GSTIN is InvValid");
			return false;
		}
	}

}