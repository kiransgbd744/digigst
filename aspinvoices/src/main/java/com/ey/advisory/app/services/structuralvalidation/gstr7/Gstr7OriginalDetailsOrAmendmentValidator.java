package com.ey.advisory.app.services.structuralvalidation.gstr7;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ey.advisory.app.data.entities.client.Gstr7AsEnteredTdsEntity;
import com.ey.advisory.common.BusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

public class Gstr7OriginalDetailsOrAmendmentValidator implements BusinessRuleValidator<Gstr7AsEnteredTdsEntity> {

	@Override
	public List<ProcessingResult> validate(Gstr7AsEnteredTdsEntity document, ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();

		String orgGrossAmt = document.getOrgGrossAmt();
		String orgReturnPeriod = document.getOrgRetPeriod();
		String orgTdsGstin = document.getOrgTdsGstin();

		if ((orgGrossAmt != null && !orgGrossAmt.trim().isEmpty())
				&& (orgReturnPeriod != null && !orgReturnPeriod.trim().isEmpty())
				&& (orgTdsGstin != null && !orgTdsGstin.trim().isEmpty()))
			return errors;

		if (orgGrossAmt != null && !orgGrossAmt.trim().isEmpty()) {
			if ((orgReturnPeriod == null || orgReturnPeriod.trim().isEmpty())
					|| (orgTdsGstin == null || orgTdsGstin.trim().isEmpty())) {

				errorLocations.add(GSTConstants.ORG_GROSS_AMOUNT);
				errorLocations.add(GSTConstants.ORG_RETURN_PERIOD);
				errorLocations.add(GSTConstants.ORG_TDS_DEDUCTEE_GSTIN);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER2029",
						"Original TDS Deductee GSTIN, Original Return Period, Original Gross Amount. All three fields should be blank or should have data.",
						location));
				return errors;

			}
		}
		if (orgReturnPeriod != null && !orgReturnPeriod.trim().isEmpty()) {
			if ((orgGrossAmt == null || orgGrossAmt.trim().isEmpty())
					|| (orgTdsGstin == null || orgTdsGstin.trim().isEmpty())) {

				errorLocations.add(GSTConstants.ORG_GROSS_AMOUNT);
				errorLocations.add(GSTConstants.ORG_RETURN_PERIOD);
				errorLocations.add(GSTConstants.ORG_TDS_DEDUCTEE_GSTIN);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER2029",
						"Original TDS Deductee GSTIN, Original Return Period, Original Gross Amount. All three fields should be blank or should have data.",
						location));
				return errors;

			}
		}

		if (orgTdsGstin != null && !orgTdsGstin.trim().isEmpty()) {
			if ((orgReturnPeriod == null || orgReturnPeriod.trim().isEmpty())
					|| (orgGrossAmt == null || orgGrossAmt.trim().isEmpty())) {

				errorLocations.add(GSTConstants.ORG_GROSS_AMOUNT);
				errorLocations.add(GSTConstants.ORG_RETURN_PERIOD);
				errorLocations.add(GSTConstants.ORG_TDS_DEDUCTEE_GSTIN);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER2029",
						"Original TDS Deductee GSTIN, Original Return Period, Original Gross Amount. All three fields should be blank or should have data.",
						location));
				return errors;

			}
		}
		return errors;
	}
}
