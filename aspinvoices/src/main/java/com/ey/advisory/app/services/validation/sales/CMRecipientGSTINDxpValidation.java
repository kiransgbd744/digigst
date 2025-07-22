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

@Component("CMRecipientGSTINDxpValidation")
public class CMRecipientGSTINDxpValidation
		implements DocRulesValidator<OutwardTransDocument> {

	private static final List<String> SUPPLYTYPE1 = ImmutableList.of(

			GSTConstants.SEZT, GSTConstants.SEZWT, GSTConstants.DXP);

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (document.getSupplyType() != null
				&& !document.getSupplyType().isEmpty()) {
			if (document.getReverseCharge() != null
					&& !document.getReverseCharge().isEmpty()) {
				if(GSTConstants.Y.equalsIgnoreCase(document.getReverseCharge())){

					if (document.getCgstin() == null
							|| document.getCgstin().isEmpty()) {

						Set<String> errorLocations = new HashSet<>();
						errorLocations.add(GSTConstants.RecipientGSTIN);
						TransDocProcessingResultLoc location 
						                 = new TransDocProcessingResultLoc(
								null, errorLocations.toArray());
						errors.add(
								new ProcessingResult(APP_VALIDATION, " ER0049",
										"Recipient GSTIN cannot be left Blank.",
										location));
					}
				} else if (SUPPLYTYPE1.contains(
						trimAndConvToUpperCase(document.getSupplyType()))) {
					if (document.getCgstin() == null
							|| document.getCgstin().isEmpty()) {
						Set<String> errorLocations = new HashSet<>();
						errorLocations.add(GSTConstants.RecipientGSTIN);
						TransDocProcessingResultLoc location 
						            = new TransDocProcessingResultLoc(
								null, errorLocations.toArray());
						errors.add(
								new ProcessingResult(APP_VALIDATION, " ER0049",
										"Recipient GSTIN cannot be left Blank.",
										location));
					}
				}
			} else if (SUPPLYTYPE1.contains(
					trimAndConvToUpperCase(document.getSupplyType()))) {
				if (document.getCgstin() == null
						|| document.getCgstin().isEmpty()) {
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.RecipientGSTIN);
					TransDocProcessingResultLoc location 
					                = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, " ER0049",
							"Recipient GSTIN cannot be left Blank.", location));
				}
			}
		}
		return errors;
	}
}
