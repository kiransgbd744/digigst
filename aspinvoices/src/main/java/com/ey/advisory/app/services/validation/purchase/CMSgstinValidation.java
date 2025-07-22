package com.ey.advisory.app.services.validation.purchase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;
import com.google.common.base.Strings;

/**
 * 
 * @author Siva.Nandam
 *
 */
public class CMSgstinValidation
		implements DocRulesValidator<InwardTransDocument> {
	private static final List<String> SUPPLY_TYPE_IMPORTS = ImmutableList
			.of(GSTConstants.IMPG, GSTConstants.IMPS);

	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();
		String supplyType = document.getSupplyType();
		String sgstin = document.getSgstin();
		Set<String> errorLocations = new HashSet<>();
		if (Strings.isNullOrEmpty(supplyType) || !SUPPLY_TYPE_IMPORTS
				.contains(trimAndConvToUpperCase(supplyType)))
			return errors;
		if (Strings.isNullOrEmpty(sgstin)
				|| GSTConstants.URP.equalsIgnoreCase(sgstin))
			return errors;
		errorLocations.add(GSTConstants.SGSTIN);
		TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
				null, errorLocations.toArray());
		errors.add(new ProcessingResult(APP_VALIDATION, "ER1053",
				"Supplier GSTIN should be blank.", location));

		return errors;
	
	}
}
