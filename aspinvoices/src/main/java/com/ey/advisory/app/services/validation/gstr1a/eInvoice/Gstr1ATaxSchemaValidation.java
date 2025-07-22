package com.ey.advisory.app.services.validation.gstr1a.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;

import com.google.common.base.Strings;

/**
 * @author Shashikant.Shukla
 *
 */
@Component("Gstr1ATaxSchemaValidation")
public class Gstr1ATaxSchemaValidation
		implements DocRulesValidator<Gstr1AOutwardTransDocument> {

	private static final List<String> TAX_SCHEMA_LIST = ImmutableList.of(
			GSTConstants.NEINV, GSTConstants.NEWB, GSTConstants.NBOTH,
			GSTConstants.GST, GSTConstants.NRET, GSTConstants.NRETEWB,
			GSTConstants.NRETEINV);

	@Override
	public List<ProcessingResult> validate(Gstr1AOutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		if (Strings.isNullOrEmpty(document.getTaxScheme()))
			return errors;
		if (TAX_SCHEMA_LIST
				.contains(trimAndConvToUpperCase(document.getTaxScheme())))
			return errors;

		errorLocations.add(GSTConstants.TAX_SCHEMA);
		TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
				null, errorLocations.toArray());
		errors.add(new ProcessingResult(APP_VALIDATION, "ER15007",
				"Invalid taxScheme", location));

		return errors;
	}

}
