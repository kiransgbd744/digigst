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

/**
 * @author Siva.Nandam
 *
 */
@Component("Gstr1ACmGstr1PosValidation")
public class Gstr1ACmGstr1PosValidation
		implements DocRulesValidator<Gstr1AOutwardTransDocument> {

	private static final List<String> SUPPLY_TYPE_IMPORTS = ImmutableList
			.of(GSTConstants.EXPT, GSTConstants.EXPWT);

	@Override
	public List<ProcessingResult> validate(Gstr1AOutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (document.getSupplyType() != null
				&& !document.getSupplyType().isEmpty()) {
			if ((!SUPPLY_TYPE_IMPORTS.contains(
					trimAndConvToUpperCase(document.getSupplyType())))) {
				if (document.getPos() == null || document.getPos().isEmpty()) {
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.POS);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER0056",
							"POS cannot be left Blank.", location));

				}
			}
		}
		return errors;

	}
}
