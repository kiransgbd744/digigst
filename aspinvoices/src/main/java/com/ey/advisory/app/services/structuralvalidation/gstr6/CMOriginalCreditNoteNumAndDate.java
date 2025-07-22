package com.ey.advisory.app.services.structuralvalidation.gstr6;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.data.entities.client.Gstr6DistributionExcelEntity;
import com.ey.advisory.common.BusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * @author Siva.Nandam
 *
 */
public class CMOriginalCreditNoteNumAndDate
		implements BusinessRuleValidator<Gstr6DistributionExcelEntity> {

	@Override
	public List<ProcessingResult> validate(
			Gstr6DistributionExcelEntity document, ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (GSTConstants.RCR.equalsIgnoreCase(document.getDocType())) {
			if (document.getOrgCrNum() == null
					|| document.getOrgCrNum().isEmpty()) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.ORG_CREDIT_NOTE_NUM);
				TransDocProcessingResultLoc location 
				              = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER3048",
						"Invalid Original Credit Note Number is missing",
						location));
			}
			if (document.getOrgDocDate() == null
					) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.ORG_CREDIT_NOTE_DATE);
				TransDocProcessingResultLoc location 
				               = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER3049",
						"Invalid Original Credit Note Date is missing",
						location));
			}
		}
		return errors;
	}

}
