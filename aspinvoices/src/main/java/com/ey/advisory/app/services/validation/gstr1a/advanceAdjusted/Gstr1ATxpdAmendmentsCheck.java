package com.ey.advisory.app.services.validation.gstr1a.advanceAdjusted;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAsEnteredTxpdFileUploadEntity;
import com.ey.advisory.app.services.validation.B2csBusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
public class Gstr1ATxpdAmendmentsCheck implements
		B2csBusinessRuleValidator<Gstr1AAsEnteredTxpdFileUploadEntity> {

	@Override
	public List<ProcessingResult> validate(
			Gstr1AAsEnteredTxpdFileUploadEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		String month = document.getMonth();
		String orgPos = document.getOrgPOS();
		String orgRate = document.getOrgRate();

		if ((month != null && !month.trim().isEmpty())
				&& (orgRate != null && !orgRate.trim().isEmpty())
				&& (orgPos != null && !orgPos.trim().isEmpty()))
			return errors;

		if (month != null && !month.trim().isEmpty()) {
			if ((orgRate == null || orgRate.trim().isEmpty())
					|| (orgPos == null || orgPos.trim().isEmpty())) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.MONTH);
				errorLocations.add(GSTConstants.OrgPOS);
				errorLocations.add(GSTConstants.OrgRate);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER5515",
						"Month / OrgRate / OrgPOS cannot be left blank.",
						location));
				return errors;

			}
		}
		if (orgRate != null && !orgRate.trim().isEmpty()) {
			if ((month == null || month.trim().isEmpty())
					|| (orgPos == null || orgPos.trim().isEmpty())) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.MONTH);
				errorLocations.add(GSTConstants.OrgPOS);
				errorLocations.add(GSTConstants.OrgRate);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER5515",
						"Month / OrgRate / OrgPOS cannot be left blank.",
						location));
				return errors;

			}
		}
		if (orgPos != null && !orgPos.trim().isEmpty()) {
			if ((orgRate == null || orgRate.trim().isEmpty())
					|| (month == null || month.trim().isEmpty())) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.MONTH);
				errorLocations.add(GSTConstants.OrgPOS);
				errorLocations.add(GSTConstants.OrgRate);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER5515",
						"Month / OrgRate / OrgPOS cannot be left blank.",
						location));
				return errors;

			}
		}
		return errors;
	}
}
