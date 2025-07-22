package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;
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

@Component("CMPosvalidator")
public class CMPosvalidator implements DocRulesValidator<OutwardTransDocument> {

	private static final List<String> SUPPLY_TYPE_IMPORTS = ImmutableList.of(
			GSTConstants.TAX, GSTConstants.DTA, GSTConstants.SEZT,
			GSTConstants.DXP, GSTConstants.SEZWT);

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (document.getSupplyType() != null
				&& !document.getSupplyType().isEmpty()) {
			if ((SUPPLY_TYPE_IMPORTS.contains(
					trimAndConvToUpperCase(document.getSupplyType())))) {
				if (document.getPos() == null || document.getPos().isEmpty()) {
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.POS);
					TransDocProcessingResultLoc location 
					                      = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER0056",
							"POS cannot be left Blank.", location));

				}
			}
		}
		return errors;

	}
}
