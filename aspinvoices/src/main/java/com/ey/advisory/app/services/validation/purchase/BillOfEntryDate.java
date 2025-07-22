package com.ey.advisory.app.services.validation.purchase;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

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

/**
 * 
 * @author Siva.Nandam
 *
 */
public class BillOfEntryDate implements DocRulesValidator<InwardTransDocument> {

	@Override
	public List<ProcessingResult> validate(InwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		
		if (document.getSupplyType() != null
				&& !document.getSupplyType().isEmpty()) {

			if (GSTConstants.IMPG.equalsIgnoreCase(document.getSupplyType()) || GSTConstants.SEZG
					.equalsIgnoreCase(document.getSupplyType())) {
				if (document.getBillOfEntryDate() == null && GSTConstants.IMPG.equalsIgnoreCase(document.getSupplyType())) {

					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.BillOfEntryDate);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER1067",
							"Invalid Bill of Entry Date",
							location));
				}
			} else {
				if (document.getBillOfEntryDate() != null) {

					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.BillOfEntryDate);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER1067",
							"Invalid Bill of Entry Date",
							location));
				}
			}
		}
		return errors;
	}

}
