package com.ey.advisory.app.services.validation.gstr1a.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;

/**
 * @author Shashikant.Shukla
 *
 */
@Component("Gstr1ALuVehicleTypeValidation")
public class Gstr1ALuVehicleTypeValidation
		implements DocRulesValidator<Gstr1AOutwardTransDocument> {

	private static final List<String> VEHICLE_TYPE = ImmutableList
			.of(GSTConstants.R, GSTConstants.O);

	@Override
	public List<ProcessingResult> validate(Gstr1AOutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (document.getVehicleType() != null
				&& !document.getVehicleType().isEmpty()) {
			if (!VEHICLE_TYPE.contains(
					trimAndConvToUpperCase(document.getVehicleType()))) {

				List<String> errorLocations = new ArrayList<>();
				errorLocations.add(GSTConstants.VEHICLE_TYPE);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER10126",
						"Invalid Vehicle Type", location));

			}
		}
		return errors;
	}

}
