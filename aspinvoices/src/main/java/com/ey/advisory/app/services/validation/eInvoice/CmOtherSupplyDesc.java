package com.ey.advisory.app.services.validation.eInvoice;

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
@Component("CmOtherSupplyDesc")
public class CmOtherSupplyDesc
		implements DocRulesValidator<OutwardTransDocument> {

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();
		if (GSTConstants.OTH.equalsIgnoreCase(document.getSubSupplyType())) {
			if (document.getOtherSupplyTypeDescription() == null
					|| document.getOtherSupplyTypeDescription().isEmpty()) {

				String[] errorLocations = new String[] {
						GSTConstants.OTH_SUPTYPE_DESC };
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations);
				errors.add(new ProcessingResult(APP_VALIDATION, "ER10136",
						"other SupplyType Desc cannot be left blank.", location));
			}
		}
		return errors;
	}

}
