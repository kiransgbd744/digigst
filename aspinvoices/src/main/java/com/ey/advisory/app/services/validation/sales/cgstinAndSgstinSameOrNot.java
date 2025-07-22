package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * @author Siva.Nandam
 *
 */
@Component("cgstinAndSgstinSameOrNot")
public class cgstinAndSgstinSameOrNot
		implements DocRulesValidator<OutwardTransDocument> {

	private static final String[] FIELD_LOCATIONS = { GSTConstants.SGSTIN,
			GSTConstants.RecipientGSTIN };

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		if(GSTConstants.URP.equalsIgnoreCase(document.getSgstin())) return errors;
		if(GSTConstants.URP.equalsIgnoreCase(document.getCgstin())) return errors;
		if (document.getSgstin() != null && !document.getSgstin().isEmpty()) {
			if (document.getCgstin() != null
					&& !document.getCgstin().isEmpty()) {

				if (document.getSgstin()
						.equalsIgnoreCase(document.getCgstin())) {

					TransDocProcessingResultLoc location 
					         = new TransDocProcessingResultLoc(
							null, FIELD_LOCATIONS);
					errors.add(new ProcessingResult(APP_VALIDATION, "ER0513",
							"Recipient GSTIN cannot be same as Supplier GSTIN",
							location));

				}
			}
		}
		return errors;
	}

}
