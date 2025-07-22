package com.ey.advisory.app.services.validation.purchase;

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

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

/**
 * 
 * @author Siva.Nandam
 *
 */
public class CMsupplierGstinValidation
		implements DocRulesValidator<InwardTransDocument> {

	private static final List<String> SUPPLY_TYPE_IMPORTS = ImmutableList
			.of(GSTConstants.SEZG, GSTConstants.SEZS, GSTConstants.DXP);

	private static final List<String> DOC_TYPE_IMPORTS = ImmutableList
			.of(GSTConstants.INV, GSTConstants.CR, GSTConstants.DR);

	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (document.getSupplyType() != null
				&& !document.getSupplyType().isEmpty()) {
			if (document.getDocType() != null
					&& !document.getDocType().isEmpty()) {

				if (SUPPLY_TYPE_IMPORTS.contains(
						trimAndConvToUpperCase(document.getSupplyType()))) {
					if (document.getSgstin() == null
							|| document.getSgstin().isEmpty()
							|| document.getSgstin().length() != 15) {
						Set<String> errorLocations = new HashSet<>();
						errorLocations.add(GSTConstants.SGSTIN);
						TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
								null, errorLocations.toArray());
						errors.add(
								new ProcessingResult(APP_VALIDATION, "ER1054",
										"Supplier GSTIN cannot be left blank.",
										location));
						return errors;
					}
				}
				/*if (DOC_TYPE_IMPORTS
						.contains(trimAndConvToUpperCase(document.getDocType()))
						&& "TAX".equalsIgnoreCase(document.getSupplyType())) {
					if (document.getSgstin() == null
							|| document.getSgstin().isEmpty()) {
						Set<String> errorLocations = new HashSet<>();
						errorLocations.add(GSTConstants.SGSTIN);
						TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
								null, errorLocations.toArray());
						errors.add(
								new ProcessingResult(APP_VALIDATION, "ER1054",
										"Supplier GSTIN cannot be left blank.",
										location));
						return errors;
					}
				}*/
			}
		}

		return errors;
	}
}
