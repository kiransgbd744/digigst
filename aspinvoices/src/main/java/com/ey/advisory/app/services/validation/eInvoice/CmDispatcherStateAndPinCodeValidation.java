package com.ey.advisory.app.services.validation.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.List;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.app.services.validation.sales.BigDecimalNagativeValueUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * @author Siva.Nandam
 *
 */
public class CmDispatcherStateAndPinCodeValidation
		implements DocRulesValidator<OutwardTransDocument> {

	private static final String S = "S";

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		String docCategory = document.getDocCategory();

		String shipToGstin = document.getShipToGstin();
		String shipToTradeName = document.getShipToTradeName();
		String shipToLocation = document.getShipToLocation();

		if (!BigDecimalNagativeValueUtil.valid(docCategory, shipToGstin, S)) {
			List<String> errorLocations = new ArrayList<>();
			errorLocations.add(GSTConstants.SHPITOGSTIN);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER10037",
					"Shipto GSTIN cannot be left blank", location));

		}
		if (!BigDecimalNagativeValueUtil.valid(docCategory, shipToTradeName,
				S)) {
			List<String> errorLocations = new ArrayList<>();
			errorLocations.add(GSTConstants.SHIP_TO_TRADE);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER10038",
					"Shipto Trande Name cannot be left blank", location));

		}
		if (!BigDecimalNagativeValueUtil.valid(docCategory, shipToLocation,
				S)) {
			List<String> errorLocations = new ArrayList<>();
			errorLocations.add(GSTConstants.SHIP_TO_LOC);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER10039",
					"Shipto Location cannot be left blank", location));

		}
		

		return errors;
	}

}
